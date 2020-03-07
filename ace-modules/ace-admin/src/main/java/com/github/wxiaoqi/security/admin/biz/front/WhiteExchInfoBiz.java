package com.github.wxiaoqi.security.admin.biz.front;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.WhiteExchInfo;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.mapper.admin.WhiteExchInfoMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class WhiteExchInfoBiz extends BaseBiz<WhiteExchInfoMapper, WhiteExchInfo> {
    /**
     * 根据角色id查询关联交易所
     *
     * @param id
     * @return
     */
    @Override
    public WhiteExchInfo selectById(Object id) {
        WhiteExchInfo whiteExchInfo = new WhiteExchInfo();
        whiteExchInfo.setGroupId((Long) id);
        WhiteExchInfo info = super.selectOne(whiteExchInfo);
        if (info != null && StringUtils.isNotBlank(info.getSrcSymbolId())) {
            String[] split = info.getSrcSymbolId().split(",");
            info.setSrcSymbol(split);
        }
        return info;
    }


    public void insertSelective(WhiteExchInfo entity) {
        String string = String.join(",", entity.getSrcSymbol());
        entity.setSrcSymbolId(string);
        EntityUtils.setCreatAndUpdatInfo(entity);
        entity.setStatus(EnableType.ENABLE.value());
        mapper.insertSelective(entity);
        cacheReturn();
    }


    public void updateSelectiveById(WhiteExchInfo entity) {
        String string = String.join(",", entity.getSrcSymbol());
        entity.setSrcSymbolId(string);
        EntityUtils.setUpdatedInfo(entity);
        mapper.updateByPrimaryKeySelective(entity);
        cacheReturn();

    }

    public List<WhiteExchInfo> cacheReturn() {
        try {
            CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.WHITE_EXCH_INFO_LIST);
            CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.WHITE_EXCH_INFO);
            WhiteExchInfo info = new WhiteExchInfo();
            info.setStatus(EnableType.ENABLE.value());
            List<WhiteExchInfo> list = mapper.select(info);
            CacheUtil.getCache().set(Constants.CacheServiceType.WHITE_EXCH_INFO_LIST, (ArrayList) list);
            list.forEach(whiteExchInfo -> {
                CacheUtil.getCache().set(Constants.CacheServiceType.WHITE_EXCH_INFO + whiteExchInfo.getDomainName(), whiteExchInfo);
                //根据交易所Id缓存对象
                CacheUtil.getCache().set(Constants.CacheServiceType.WHITE_EXCH_INFO + whiteExchInfo.getId(), whiteExchInfo);
            });
            logger.info("== 白标表:WhiteExchInfo  缓存完成,缓存条数：{}", list.size());
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
