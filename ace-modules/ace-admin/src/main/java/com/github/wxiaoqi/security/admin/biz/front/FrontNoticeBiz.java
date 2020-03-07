package com.github.wxiaoqi.security.admin.biz.front;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.FrontNotice;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.mapper.admin.FrontNoticeMapper;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.EntityUtils;

@Service
@Transactional(rollbackFor = Exception.class)
public class FrontNoticeBiz extends BaseBiz<FrontNoticeMapper, FrontNotice> {

	@Override
	public void insertSelective(FrontNotice entity) {
		if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
			entity.setExchangeId(BaseContextHandler.getExId());
		}
		EntityUtils.setCreatAndUpdatInfo(entity);
		mapper.insertSelective(entity);
		this.cacheReturn();
	}

	public void updateSelectiveById(FrontNotice entity) {
		EntityUtils.setUpdatedInfo(entity);
		mapper.updateByPrimaryKeySelective(entity);
		this.cacheReturn();
	}

	public void deleteById(Object id) {
		mapper.deleteByPrimaryKey(id);
		this.cacheReturn();
	}

	public List<FrontNotice> cacheReturn() {
		CacheUtil.getCache().deleteAllKeys(Constants.CacheServiceType.FRONT_NOTICE);
		FrontNotice frontNotice = new FrontNotice();
		frontNotice.setStatus(EnableType.ENABLE.value());
		List<FrontNotice> list = mapper.select(frontNotice);
		HashMap<String, List<FrontNotice>> map = new HashMap<>();
		list.forEach(notice -> {
			if (map.get(notice.getExchangeId()+":"+notice.getLanguageType()+":"+notice.getClientType())!=null) {
				List<FrontNotice> adList = map.get(notice.getExchangeId()+":"+notice.getLanguageType()+":"+notice.getClientType());
				adList.add(notice);
			} else {
				ArrayList<FrontNotice> advertList = new ArrayList<>();
				advertList.add(notice);
				map.put(notice.getExchangeId()+":"+notice.getLanguageType()+":"+notice.getClientType(), advertList);
			}
		});
		map.forEach((k, v) -> {
			CacheUtil.getCache().set(Constants.CacheServiceType.FRONT_NOTICE + k, (ArrayList) v);
		});
		return list;
	}
}
