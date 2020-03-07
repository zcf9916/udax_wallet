package com.github.wxiaoqi.security.admin.biz.front;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.admin.FrontFreezeInfo;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.FreezeFunctionType;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.mapper.admin.FrontFreezeInfoMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.ExceptionUtil;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;


/**
 * 用户冻结功能服务实现类
 * 
 * @author Tang
 * @version 2018-12-27
 */
@Service
public class FrontFreezeInfoBiz extends BaseBiz<FrontFreezeInfoMapper, FrontFreezeInfo> {
		

    @Transactional
    public FrontFreezeInfo update(FrontFreezeInfo record) {
        try {

			FrontFreezeInfo paramInfo = new FrontFreezeInfo();
			paramInfo.setUserId(record.getUserId());
            ArrayList<FrontFreezeInfo>   freezeTypeList = (ArrayList<FrontFreezeInfo>) mapper.select(paramInfo);
        	String[] ss = null;
        	
        	//从数据库查出来的,当前用户所有交易功能集合。页面传过来的值看是否包含在此集合里，若不包含,表示此功能是枚举类新增的值
        	List<String> typeList = InstanceUtil.newArrayList(); 
        	for(FrontFreezeInfo freezeInfo:freezeTypeList) {
				typeList.add(freezeInfo.getFreezeType().toString());
			}
        	
			if(freezeTypeList!=null&&freezeTypeList.size()>0) {
				if(StringUtils.isBlank(record.getFreezeTypeStr())) {//页面传过来为空,则表示没有冻结的,或者全部解冻
					for(FrontFreezeInfo freezeInfo:freezeTypeList) {
						if(EnableType.DISABLE.value().equals(freezeInfo.getEnable())) {
							freezeInfo.setEnable(EnableType.ENABLE.value());//禁用的功能需要解冻
							freezeInfo.setUpdTime(new Date());//单个更新冻结时间    以防以后针对具体功能单独时间计算
							freezeInfo.setUpdName(record.getUpdName());
							Example example = new Example(FrontFreezeInfo.class);
							example.createCriteria().andEqualTo("userId",freezeInfo.getUserId()).andEqualTo("freezeType",freezeInfo.getFreezeType());
							mapper.updateByExampleSelective(freezeInfo,example);
						}
					}
				}else {//1-页面传过来不为空,则表示有的需要冻结  2-数据库里冻结的功能,但页面没有传此值,则表示需要解冻
					ss =record.getFreezeTypeStr().split(",");
	        	    FrontFreezeInfo entity = new FrontFreezeInfo();
	        		entity.setUpdTime(new Date());
	        		entity.setUpdName(record.getUpdName());
	        		for (FreezeFunctionType functionType : FreezeFunctionType.values()) {
	        			if(typeList.contains(functionType.value().toString())) {//数据库包含此条数据
	        				if(Arrays.asList(ss).contains(functionType.value().toString())) {//此功能为页面传过来的值,则冻结
		        				entity.setEnable(EnableType.DISABLE.value());
		        				// 之前已冻结过的不更新,以防将此交易功能最初的冻结时间更新
		        				//mapper.update(entity, Condition.create().where("user_id = {0} and freeze_type = {1} and enable_ = {2}", record.getUserId(),functionType.value(),EnableType.ENABLE.value()));
								Example example = new Example(FrontFreezeInfo.class);
								example.createCriteria().andEqualTo("userId",record.getUserId()).andEqualTo("freezeType",functionType.value()).
										andEqualTo("enable",EnableType.ENABLE.value());
								mapper.updateByExampleSelective(entity,example);
		        			}else {//没有传过来的则更新解冻
		        				entity.setEnable(EnableType.ENABLE.value());
		        				//mapper.update(entity, Condition.create().where("user_id = {0} and freeze_type = {1}", record.getUserId(),functionType.value()));
								Example example = new Example(FrontFreezeInfo.class);
								example.createCriteria().andEqualTo("userId",record.getUserId()).andEqualTo("freezeType",functionType.value());
								mapper.updateByExampleSelective(entity,example);
		        			}
	        			}else {//数据库不包含此条数据  表示后面新增的枚举功能
	        				
	        				entity.setCrtTime(new Date());
	        				entity.setCrtName(record.getUpdName());
	    	        		entity.setUserId(record.getUserId());
	    	        		
	        				if(Arrays.asList(ss).contains(functionType.value().toString())) {//此功能为页面传过来的值,则冻结
		        				entity.setEnable(EnableType.DISABLE.value());
		        			}else {//没有传过来的则新增未冻结
		        				entity.setEnable(EnableType.ENABLE.value());
		        				entity.setCrtTime(new Date());
		        			}
	        				mapper.insert(entity);
	        			}
	        			
	                }
				}																
			}else {//数据库没有此用户信息 ,第一次冻结,新增insert
				if(StringUtils.isNotBlank(record.getFreezeTypeStr())) {
					ss =record.getFreezeTypeStr().split(",");
	        	    FrontFreezeInfo entity = new FrontFreezeInfo();
	        		entity.setCrtTime(new Date());
	        		entity.setUpdTime(new Date());
	        		entity.setCrtName(record.getUpdName());
	        		entity.setUserId(record.getUserId());
	        		for (FreezeFunctionType functionType : FreezeFunctionType.values()) {
	        			if(Arrays.asList(ss).contains(functionType.value().toString())) {//此功能为页面传过来的值,则冻结
	        				entity.setFreezeType(functionType.value());
	        				entity.setEnable(EnableType.DISABLE.value());
	        			}else {//没有传过来的也更新入库
	        				entity.setFreezeType(functionType.value());
	        				entity.setEnable(EnableType.ENABLE.value());
	        			}
	        			mapper.insert(entity);
	                }
	        	}
			}
			ArrayList<FrontFreezeInfo>  freezeTypeCacheList = (ArrayList<FrontFreezeInfo>) mapper.select(paramInfo);
            CacheUtil.getCache().hset(Constants.CacheServiceType.FRONT_FREEZE_INFO+ (record.getUserId().intValue()% Constants.REDIS_MAP_BATCH),record.getUserId().toString(),freezeTypeCacheList);
        } catch (DuplicateKeyException e) {
            logger.error(e);
            throw new BusinessException("已经存在相同的记录.");
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException(ExceptionUtil.getStackTraceAsString(e));
        }
        return record;
    }
	
	public List<FrontFreezeInfo> cacheAndReturnFreezeInfo(Long userId) {
		FrontFreezeInfo paramInfo = new FrontFreezeInfo();
		paramInfo.setUserId(userId);
		ArrayList<FrontFreezeInfo>  freezeTypeCacheList = (ArrayList<FrontFreezeInfo>) mapper.select(paramInfo);
		if(freezeTypeCacheList==null||freezeTypeCacheList.size()<1) { // 第一次查询数据库没交易数据,则初始未被冻结缓存数据,防止下次查询没缓存数据,会去入库查询,浪费性能
			freezeTypeCacheList = new ArrayList<FrontFreezeInfo>();
			for (FreezeFunctionType freezeType : FreezeFunctionType.values()) {
				FrontFreezeInfo entity = new FrontFreezeInfo();
				entity.setUserId(userId);
				entity.setFreezeType(freezeType.value());
				entity.setEnable(EnableType.ENABLE.value());//启用状态
				entity.setCrtTime(new Date());
				freezeTypeCacheList.add(entity);
            }
    	}
        CacheUtil.getCache().hset(Constants.CacheServiceType.FRONT_FREEZE_INFO+ (userId.intValue()%Constants.REDIS_MAP_BATCH),userId.toString(),freezeTypeCacheList);
		return freezeTypeCacheList;
	}	

}
