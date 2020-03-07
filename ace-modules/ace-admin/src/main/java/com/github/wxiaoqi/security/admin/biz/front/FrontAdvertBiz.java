package com.github.wxiaoqi.security.admin.biz.front;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.FrontAdvert;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.FrontAdvertMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class FrontAdvertBiz extends BaseBiz<FrontAdvertMapper, FrontAdvert> {

    @Override
    public void insertSelective(FrontAdvert entity) {
        if (BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            //如果是ADMIN 必须选择交易所
            if (entity.getExchangeId()==null){
                throw new UserInvalidException(Resources.getMessage("CONFIG_EXCH_ID"));
            }
        }else {
            entity.setExchangeId(BaseContextHandler.getExId());
        }
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
        this.cacheReturn();
    }

    public void updateSelectiveById(FrontAdvert entity) {
        EntityUtils.setUpdatedInfo(entity);
        mapper.updateByPrimaryKeySelective(entity);
        this.cacheReturn();
    }


    public void deleteById(Object id) {
        mapper.deleteByPrimaryKey(id);
        this.cacheReturn();
    }

    public List<FrontAdvert> cacheReturn(){
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.FRONT_ADVERT);
        FrontAdvert frontAdvert = new FrontAdvert();
        frontAdvert.setStatus(EnableType.ENABLE.value());
        List<FrontAdvert> list = mapper.select(frontAdvert);
        HashMap<String, List<FrontAdvert>> map = new HashMap<>();
        list.forEach(advert -> {
            //根据交易所ID  语言  客户端类型
           if ( map.get(advert.getExchangeId()+":"+advert.getLanguageType()+":"+advert.getClientType())!=null){
               List<FrontAdvert> adList = map.get(advert.getExchangeId()+":"+advert.getLanguageType()+":"+advert.getClientType());
               adList.add(advert);
           }else {
               ArrayList<FrontAdvert> advertList = new ArrayList<>();
               advertList.add(advert);
               map.put(advert.getExchangeId()+":"+advert.getLanguageType()+":"+advert.getClientType(),advertList);
           }
        });
        map.forEach((k, v) -> {
            CacheUtil.getCache().set(Constants.CacheServiceType.FRONT_ADVERT  + k, (ArrayList) v);
        });
        return list;
    }
}
