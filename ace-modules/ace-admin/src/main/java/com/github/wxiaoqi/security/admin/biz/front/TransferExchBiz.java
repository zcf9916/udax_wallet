package com.github.wxiaoqi.security.admin.biz.front;

import com.github.wxiaoqi.security.admin.biz.front.CfgCurrencyTransferBiz;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.CfgCurrencyTransfer;
import com.github.wxiaoqi.security.common.entity.admin.TransferExch;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.BasicSymbolMapper;
import com.github.wxiaoqi.security.common.mapper.admin.CfgCurrencyTransferMapper;
import com.github.wxiaoqi.security.common.mapper.admin.TransferExchMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.vo.SymbolTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class TransferExchBiz extends BaseBiz<TransferExchMapper, TransferExch> {

    @Autowired
    private CfgCurrencyTransferMapper cfgCurrencyTransferMapper;

    @Autowired
    private BasicSymbolMapper basicSymbolMapper;

    @Autowired
    private CfgCurrencyTransferBiz cfgCurrencyTransferBiz;

    /**
     * 根据交易所id获取中间表集合
     *
     * @param id
     * @return
     */
    public List<TransferExch> getTransferExchBiz(Long id) {
        Example example = new Example(TransferExch.class);
        example.createCriteria().andEqualTo("exchId", id);
        return this.selectByExample(example);
    }

    /**
     * 币种授权交易所（add）
     *
     * @param id
     * @param exchId
     */
    public void modifyTransferExch(Long id, Long exchId) {
        //查询货币转换配置
        CfgCurrencyTransfer transfer = cfgCurrencyTransferMapper.selectByPrimaryKey(id);
        Integer srcCount = basicSymbolMapper.selectSymbolCount(transfer.getSrcSymbol(), exchId);
        Integer dstCount = basicSymbolMapper.selectSymbolCount(transfer.getDstSymbol(), exchId);
        if (srcCount == null || srcCount == 0) {
            throw new UserInvalidException(String.format(Resources.getMessage("CONFIG_TRANSFER_EXCH"), transfer.getSrcSymbol()));
        }
        if (dstCount == null || dstCount == 0) {
            throw new UserInvalidException(String.format(Resources.getMessage("CONFIG_TRANSFER_EXCH"), transfer.getDstSymbol()));
        }
        TransferExch transferExch = new TransferExch();
        transferExch.setExchId(exchId);
        transferExch.setTransferId(id);
        TransferExch exch = this.selectOne(transferExch);
        if (exch == null) {
            EntityUtils.setCreatAndUpdatInfo(transferExch);
            this.insertSelective(transferExch);
        }
        cfgCurrencyTransferBiz.cacheReturn();
    }

    /**
     * 删除授权
     *
     * @param id
     * @param exchId
     */
    public void removeTransferExch(Long id, Long exchId) {
        TransferExch transferExch = new TransferExch();
        //如果id为-1时 则根据交易所id 删除所有关联中间表
        if (id.equals(AdminCommonConstant.ROOT)) {
            transferExch.setExchId(exchId);
            this.delete(transferExch);
        } else {
            //或者删除单个
            transferExch.setExchId(exchId);
            transferExch.setTransferId(id);
            this.delete(transferExch);
        }
        cfgCurrencyTransferBiz.cacheReturn();
        cacheReturn();
    }

    public TransferExch selectTransferByExchId(Long transferId, Long exchId) {
        TransferExch exch = mapper.selectTransferByExchId(transferId, exchId);
        if (exch == null) {
            throw new UserInvalidException(Resources.getMessage("CONFIG_TRANSFER_EXCH_CHARGE"));
        }
        return exch;
    }


    public void updateSelectiveById(TransferExch entity) {
        EntityUtils.setUpdatedInfo(entity);
        mapper.updateByPrimaryKeySelective(entity);
        cacheReturn();
    }

    public List<SymbolTransfer> selectTransfer(Map<String, Object> params) {
        return mapper.selectTransfer(params);
    }

    @Override
    public void updateById(TransferExch entity) {
        super.updateById(entity);
        this.cacheReturn();
    }

    //缓存
    public void cacheReturn() {
        //根据key 删除 redis 中所有
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.TRANSFER_EXCH);
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.TRANSFER_EXCH_LIST);
        try {
            List<TransferExch> list = mapper.selectChargeAll();
            Map<Long, ArrayList<TransferExch>> allMap = InstanceUtil.newHashMap();
            if (StringUtil.listIsNotBlank(list)) {
                list.forEach(exch -> {
                    if (allMap.get(exch.getExchId()) == null) {
                        ArrayList<TransferExch> arrayList = new ArrayList<>();
                        arrayList.add(exch);
                        allMap.put(exch.getExchId(), arrayList);
                    } else {
                        ArrayList<TransferExch> cfgCurrencyTransfers = allMap.get(exch.getExchId());
                        cfgCurrencyTransfers.add(exch);
                    }
                    //缓存Key UDAX-WALLET:transferExch:exchId:srcSymbol:dstSymbol
                    CacheUtil.getCache().set(Constants.CacheServiceType.TRANSFER_EXCH + exch.getExchId() + ":" + exch.getSrcSymbol() + ":" + exch.getDstSymbol(), exch);
                });
                allMap.forEach((k, v) -> {
                    //通过交易所id 缓存
                    CacheUtil.getCache().set(Constants.CacheServiceType.TRANSFER_EXCH_LIST + k, v);
                });
            }
            logger.info("==转换配置表:TransferExch  缓存完成,缓存条数：{}", list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
