package com.github.wxiaoqi.security.admin.biz.front;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.BasicSymbol;
import com.github.wxiaoqi.security.common.entity.admin.BasicSymbolImage;
import com.github.wxiaoqi.security.common.entity.admin.WhiteExchInfo;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.BasicSymbolImageMapper;
import com.github.wxiaoqi.security.common.mapper.admin.BasicSymbolMapper;
import com.github.wxiaoqi.security.common.mapper.admin.WhiteExchInfoMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.model.IconModel;
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
public class BasicSymbolImageBiz extends BaseBiz<BasicSymbolImageMapper, BasicSymbolImage> {

    @Autowired
    private BasicSymbolMapper basicSymbolMapper;

    @Autowired
    private WhiteExchInfoMapper whiteExchInfoMapper;

    @Override
    public void insertSelective(BasicSymbolImage entity) {

        Example example = new Example(BasicSymbolImage.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("basicSymbolId", entity.getBasicSymbolId());
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            criteria.andEqualTo("exchangeId", BaseContextHandler.getExId());
        } else {
            criteria.andEqualTo("exchangeId", entity.getExchangeId());
        }
        List<BasicSymbolImage> images = mapper.selectByExample(example);
        if (!StringUtil.listIsBlank(images)) {
            throw new UserInvalidException(Resources.getMessage("CONFIG_CURRENCY_INFO_REPEAT"));
        }
        BasicSymbol basicSymbol = basicSymbolMapper.selectByPrimaryKey(entity.getBasicSymbolId());
        entity.setSymbol(basicSymbol.getSymbol());
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            entity.setExchangeId(BaseContextHandler.getExId());
        }
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
        this.cacheReturn();
    }


    public void updateSelectiveById(BasicSymbolImage entity) {
        EntityUtils.setUpdatedInfo(entity);
        mapper.updateByPrimaryKeySelective(entity);
        this.cacheReturn();
    }

    public void deleteById(Object id) {
        mapper.deleteByPrimaryKey(id);

    }

    public List<IconModel> cacheReturn() {
        List<IconModel> imageList = null;
        List<IconModel> basicSymbolList = null;
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.BASIC_SYMBOL_IMAGE);
        try {
            //获取默认币种图标集合
            basicSymbolList = basicSymbolMapper.selectListIcon(EnableType.ENABLE.value());
            //获取交易所个性币种图标集合
            imageList = mapper.selectExchAndImage();
            HashMap<String, IconModel> map = new HashMap<>();
            if (StringUtil.listIsNotBlank(imageList)) {
                imageList.forEach(exchImage -> {
                    map.put(exchImage.getSymbol() + exchImage.getExchangeId(), exchImage);
                });
            }


            Map<Long, ArrayList<IconModel>> allMap = InstanceUtil.newHashMap();
            for (WhiteExchInfo whiteExchInfo : whiteExchInfoMapper.selectAll()) {
                ArrayList<IconModel> list = new ArrayList<>();
                basicSymbolList.forEach(basicIcon -> {
                    if (map.get(basicIcon.getSymbol() + whiteExchInfo.getId()) != null) {
                        list.add(map.get(basicIcon.getSymbol() + whiteExchInfo.getId()));
                    } else {
                        list.add(basicIcon);
                    }
                });
                allMap.put(whiteExchInfo.getId(), list);
            }

            allMap.forEach((k, v) -> {
                CacheUtil.getCache().set(Constants.CacheServiceType.BASIC_SYMBOL_IMAGE + k, v);
            });
            logger.info(" ==币种图标表:IconModel 缓存完成,缓存条数：{}", basicSymbolList.size());
        } catch (Exception e) {
            logger.error("加载币种图标缓存时异常", e);
        }
        return basicSymbolList;
    }
}
