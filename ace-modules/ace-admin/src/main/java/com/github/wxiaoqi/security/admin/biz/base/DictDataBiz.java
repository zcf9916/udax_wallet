package com.github.wxiaoqi.security.admin.biz.base;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.DictData;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.mapper.admin.DictDataMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.DataSortUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class DictDataBiz extends BaseBiz<DictDataMapper, DictData> {

    @Autowired
    private DictDataMapper dictDataMapper;


    @Override
    public void insertSelective(DictData entity) {
        DictData data = new DictData();
        data.setLanguageType(entity.getLanguageType());
        data.setDictType(entity.getDictType());
        data.setDictValue(entity.getDictValue());
        int count = mapper.selectCount(data);
        if (count > 0) {
            throw new UserInvalidException(Resources.getMessage("CONFIG_DICT_DATA"));
        }
        EntityUtils.setCreatAndUpdatInfo(entity);
        mapper.insertSelective(entity);
        cacheReturn();
    }

    @Override
    public void updateSelectiveById(DictData entity) {
        EntityUtils.setUpdatedInfo(entity);
        mapper.updateByPrimaryKeySelective(entity);
        cacheReturn();
    }



    public void deleteById(Object id) {
        mapper.deleteByPrimaryKey(id);
        cacheReturn();
    }

    public List<DictData> selectListData(String type, String language) {
        return mapper.selectListData(type, language);
    }

    public void cacheReturn() {
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.DICT_DATA);
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.DICT_DATA_DATA);// redis 可能存在旧数据
        CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.DICT_DATA_MAP);
        try {
            //获取类型集合
            List<DictData> dictDataList = dictDataMapper.GroupDictData();

            dictDataList.forEach(dictData -> {
                //根据type获取数据集合
                List<DictData> list = mapper.selectListData(dictData.getDictType(), "");
                //没有语言属性   直接缓存
                if (StringUtils.isEmpty(list.get(0).getLanguageType())) {
                    DataSortUtil.sortAsc(list, "sort");
                    CacheUtil.getCache().set(Constants.CacheServiceType.DICT_DATA + dictData.getDictType(), (ArrayList<DictData>) list);
                    //根据字典类型Map集合
                    HashMap<String, Object> map = new HashMap<>();
                    for (DictData data1 : list) {
                        map.put(data1.getDictLabel(), data1.getDictValue());
                    }
                    CacheUtil.getCache().set(Constants.CacheServiceType.DICT_DATA_MAP + dictData.getDictType(), map);
                }else {
                //表示配置语言属性
                    HashMap<String, ArrayList<DictData>> dictDataLanguageMap = new HashMap<>();
                    list.forEach(data ->{
                        if (dictDataLanguageMap.get(data.getDictType()+":"+ data.getLanguageType()) !=null){
                            ArrayList<DictData> arrayList = dictDataLanguageMap.get(data.getDictType()+":"+ data.getLanguageType());
                            arrayList.add(data);
                        }else {
                            ArrayList<DictData> arrayList = new ArrayList<>();
                            arrayList.add(data);
                            dictDataLanguageMap.put(data.getDictType()+":"+ data.getLanguageType(),arrayList);
                        }
                        CacheUtil.getCache().set(Constants.CacheServiceType.DICT_DATA_DATA + data.getDictType()+data.getDictValue()+data.getLanguageType(), data);
                    } );
                    dictDataLanguageMap.forEach((k, v) -> {
                        CacheUtil.getCache().set(Constants.CacheServiceType.DICT_DATA + k,v);
                        //根据字典类型Map集合+ 语言
                        HashMap<String, Object> map = new HashMap<>();
                        v.forEach(dictData1 -> {
                            map.put(dictData.getDictLabel(), dictData.getDictValue());
                        });
                        CacheUtil.getCache().set(Constants.CacheServiceType.DICT_DATA_MAP + k,map);
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
