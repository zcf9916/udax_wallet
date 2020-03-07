package com.github.wxiaoqi.security.admin.biz.front;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.CfgCurrencyTransfer;
import com.github.wxiaoqi.security.common.entity.admin.TransferExch;
import com.github.wxiaoqi.security.common.entity.admin.UserOfferInfo;
import com.github.wxiaoqi.security.common.enums.CurrencyTransferType;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.CfgCurrencyTransferMapper;
import com.github.wxiaoqi.security.common.mapper.admin.UserOfferInfoMapper;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class CfgCurrencyTransferBiz extends BaseBiz<CfgCurrencyTransferMapper, CfgCurrencyTransfer> {

    private Logger logger = LoggerFactory.getLogger(CfgCurrencyTransferBiz.class);

    @Autowired
    private CfgCurrencyTransferMapper cfgCurrencyTransferMapper;

    @Autowired
    private UserOfferInfoMapper userOfferInfoMapper;

    @Autowired
    private TransferExchBiz transferExchBiz;

    @Override
    public void insertSelective(CfgCurrencyTransfer entity) {
        if (entity.getSrcSymbol().equals(entity.getDstSymbol())) {
            throw new UserInvalidException(Resources.getMessage("CONFIG_CURRENCY_INFO_DSTSYMBOL"));
        }
        Map<String, Object> param = new HashMap<>();
        param.put("srcSymbol", entity.getSrcSymbol());
        param.put("dstSymbol", entity.getDstSymbol());
        Integer count = cfgCurrencyTransferMapper.selectTransferBySymbol(param);
        if (count > 0) {
            throw new UserInvalidException(Resources.getMessage("CONFIG_TRANSFER_REPEAT"));
        }
        //平台对冲
        if (entity.getTransferType().equals(CurrencyTransferType.PLATFORM_HEDGE_FLAG.value())) {
            entity.setHedgeFlag(CurrencyTransferType.PLATFORM_HEDGE_FLAG.value());
        } else {
            //用户报价
            entity.setHedgeFlag(CurrencyTransferType.USER_OFFER.value());
            Example example = new Example(UserOfferInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("srcSymbol", entity.getDstSymbol());
            criteria.andEqualTo("dstSymbol", entity.getSrcSymbol());
            List<UserOfferInfo> infos = userOfferInfoMapper.selectByExample(example);
            if (!StringUtil.listIsNotBlank(infos)) {
                throw new UserInvalidException(Resources.getMessage("CONFIG_CURRENCY_INFO"));
            }
        }
        EntityUtils.setCreatAndUpdatInfo(entity);
        try {
            mapper.insertTransfer(entity);
            cacheReturn();
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new UserInvalidException(Resources.getMessage("CONFIG_TRANSFER_REPEAT"));
        }
    }

    @Override
    public void updateSelectiveById(CfgCurrencyTransfer entity) {
        EntityUtils.setUpdatedInfo(entity);
        mapper.updateTransfer(entity);
        cacheReturn();

    }

    public void deleteById(Object id) {
        TransferExch exch = new TransferExch();
        exch.setTransferId((Long) id);
        transferExchBiz.delete(exch);
        mapper.deleteByPrimaryKey(id);
        transferExchBiz.cacheReturn();
        cacheReturn();
    }


    @Override
    public CfgCurrencyTransfer selectById(Object id) {
        CfgCurrencyTransfer transfer = mapper.selectTransferById((Long) id);
        return transfer;
    }

    @Override
    public TableResultResponse<CfgCurrencyTransfer> selectByQuery(Query query) {
        HashMap<String, Object> param = new HashMap<>();
        if (query.entrySet().size() > 0) {
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                param.put(entry.getKey(), entry.getValue());
            }
        }
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<CfgCurrencyTransfer> list = cfgCurrencyTransferMapper.selectQuery(param);
        return new TableResultResponse<CfgCurrencyTransfer>(result.getTotal(), list);
    }

    public List<CfgCurrencyTransfer> cacheReturn() {
        List<CfgCurrencyTransfer> list = null;
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.CFG_CURRENCY_TRANSFER_EXCH);
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.CFG_CURRENCY_TRANSFER_SYMBOL);
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.CFG_CURRENCY_TRANSFER_STRING);

        try {
            list = mapper.cacheReturn();
            //按照交易所缓存
            Map<Long, ArrayList<CfgCurrencyTransfer>> allMap = InstanceUtil.newHashMap();
            if (StringUtil.listIsNotBlank(list)) {
                list.forEach(transfer -> {
                    if (allMap.get(transfer.getExchId()) != null) {
                        ArrayList<CfgCurrencyTransfer> cfgCurrencyTransfers = allMap.get(transfer.getExchId());
                        cfgCurrencyTransfers.add(transfer);
                    } else {
                        ArrayList<CfgCurrencyTransfer> arrayList = new ArrayList<>();
                        arrayList.add(transfer);
                        allMap.put(transfer.getExchId(), arrayList);
                    }
                });

            }
            allMap.forEach((k, v) -> {
                //通过源货币和交易所id 分组缓存
                CacheUtil.getCache().set(Constants.CacheServiceType.CFG_CURRENCY_TRANSFER_EXCH + ":" + k, v);
                v.forEach(transfer -> {
                    //根据源货币查询对应的目标货币集合并缓存
                    List<CfgCurrencyTransfer> srcSymbol = mapper.selectTransferBySrcSymbol(transfer.getSrcSymbol(), transfer.getExchId());
                    CacheUtil.getCache().set(Constants.CacheServiceType.CFG_CURRENCY_TRANSFER_STRING + transfer.getSrcSymbol() + ":" + transfer.getExchId(), (ArrayList<CfgCurrencyTransfer>) srcSymbol);
                });
            });
            HashMap<String, Object> param = new HashMap<>();
            param.put("status", EnableType.ENABLE.value());
            List<CfgCurrencyTransfer> all = mapper.selectQuery(param);
            //缓存单个CfgCurrencyTransfer 对象  参数: 源货币 + ":" + 目标货币
            if (StringUtil.listIsNotBlank(all)) {
                all.forEach(cfg -> {
                    CacheUtil.getCache().set(Constants.CacheServiceType.CFG_CURRENCY_TRANSFER_SYMBOL + cfg.getSrcSymbol() + ":" + cfg.getDstSymbol(), cfg);
                });
            }
            logger.info(" ==转换配置表:CfgCurrencyTransfer 缓存完成,缓存条数：{}", list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
