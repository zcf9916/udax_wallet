package com.github.wxiaoqi.security.admin.biz.front;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.FrontCountry;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.mapper.admin.FrontCountryMapper;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.DataSortUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import com.github.wxiaoqi.security.common.util.Query;

import tk.mybatis.mapper.entity.Example;

@Service
@Transactional(rollbackFor = Exception.class)
public class FrontCountryBiz extends BaseBiz<FrontCountryMapper, FrontCountry> {

    @Override
    public TableResultResponse<FrontCountry> selectByQuery(Query query) {
        Example example = new Example(FrontCountry.class);
        if (query.entrySet().size() > 0) {
            Example.Criteria criteria = example.createCriteria();
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                criteria.andLike(entry.getKey(), "%" + entry.getValue().toString() + "%");
            }
        }
        example.setOrderByClause("sort");
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<FrontCountry> list = mapper.selectByExample(example);
        return new TableResultResponse<FrontCountry>(result.getTotal(), list);
    }

    @Override
    public void updateSelectiveById(FrontCountry entity) {
        EntityUtils.setUpdatedInfo(entity);
        mapper.updateByPrimaryKeySelective(entity);
        cacheReturn();
    }

    @Override
    public void deleteById(Object id) {
        mapper.deleteByPrimaryKey(id);
        cacheReturn();
    }

    @Override
    public void insertSelective(FrontCountry entity) {
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
        cacheReturn();
    }

    public List<FrontCountry> cacheReturn() {
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.FRONT_COUNTRY);
        List<FrontCountry> list = null;
        try {
            FrontCountry frontCountry = new FrontCountry();
            frontCountry.setStatus(EnableType.ENABLE.value());
            list = mapper.select(frontCountry);
            DataSortUtil.sortAsc(list, "sort");
            CacheUtil.getCache().set(Constants.CacheServiceType.FRONT_COUNTRY, (ArrayList<FrontCountry>) list);
            logger.info("== 国家表:FrontCountry  缓存完成,缓存条数：{}", list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
