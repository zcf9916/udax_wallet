package com.github.wxiaoqi.security.admin.biz.ifr;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.entity.ifr.IfrExchangeRate;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.ifr.IfrExchangeRateMapper;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import org.springframework.stereotype.Service;

@Service
public class IfrExchangeRateBiz extends BaseBiz<IfrExchangeRateMapper, IfrExchangeRate> {



    public void insertSelective(IfrExchangeRate entity) {

        IfrExchangeRate rate = new IfrExchangeRate();
        rate.setSymbol(entity.getSymbol());
        int count = mapper.selectCount(rate);
        if (count >0){
            throw new UserInvalidException(Resources.getMessage("CONFIG_CURRENCY_INFO_REPEAT"));
        }
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
    }

}
