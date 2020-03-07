package com.github.wxiaoqi.security.admin.biz.front;


import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.admin.HedgeDetail;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.mapper.admin.HedgeDetailMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class HedgeDetailBiz extends BaseBiz<HedgeDetailMapper, HedgeDetail> {
    //设置admin_dealed 字段为1
    public void updateByDealedStatus(Long id) {
        HedgeDetail detail = new HedgeDetail();
        detail.setId(id);
        detail.setAdminDealed(EnableType.ENABLE.value());
        mapper.updateByPrimaryKeySelective(detail);
    }
}
