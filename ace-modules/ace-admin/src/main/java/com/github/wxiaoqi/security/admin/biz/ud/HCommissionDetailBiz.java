package com.github.wxiaoqi.security.admin.biz.ud;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.ud.HCommissionDetail;
import com.github.wxiaoqi.security.common.enums.ud.UdCommissionType;
import com.github.wxiaoqi.security.common.mapper.ud.HCommissionDetailMapper;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分成明细表
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HCommissionDetailBiz extends BaseBiz<HCommissionDetailMapper, HCommissionDetail> {

    @Autowired
    HCommissionDetailMapper  commissionDetailMapper;

    //分成报表统计
    public TableResultResponse<HCommissionDetail> queryCommissionReport(Map<String, Object> params) {

        HashMap<Long, HCommissionDetail> resultMap = new HashMap<Long, HCommissionDetail>();

        //根据申购等级查询申购总量、申购所产生总收益
        List<HCommissionDetail> list =  commissionDetailMapper.queryCommissionReport(params);
        if(StringUtil.listIsNotBlank(list)){
            for(HCommissionDetail commissionDetail :list){
                if(!resultMap.containsKey(commissionDetail.getLevelId())){
                    resultMap.put(commissionDetail.getLevelId(),commissionDetail);
                }
            }
        }

        //根据申购等级查询平台收益、申购总分成(分给用户的总利润)
        List<HCommissionDetail> listTwo =  commissionDetailMapper.queryCommissionReportTwo(params);
        if(StringUtil.listIsNotBlank(listTwo)){
            for(HCommissionDetail commissionDetailTwo :listTwo){
                if(resultMap.containsKey(commissionDetailTwo.getLevelId())){
                    HCommissionDetail model = resultMap.get(commissionDetailTwo.getLevelId());
                    if(UdCommissionType.USER_CMS.value().equals(commissionDetailTwo.getType())){//用户总收成
                        model.setUserTotalProfit(model.getUserTotalProfit().add(commissionDetailTwo.getProfit()));
                    }else if(UdCommissionType.PLAT_CMS.value().equals(commissionDetailTwo.getType())){//平台总收成
                        model.setPlatformTotalProfit(commissionDetailTwo.getProfit());
                    }else if(UdCommissionType.NODE_AWARD.value().equals(commissionDetailTwo.getType())){
                        model.setUserTotalProfit(model.getUserTotalProfit().add(commissionDetailTwo.getProfit()));
                    }else if(UdCommissionType.GLOBAL_AWARD.value().equals(commissionDetailTwo.getType())){
                        model.setUserTotalProfit(model.getUserTotalProfit().add(commissionDetailTwo.getProfit()));
                    }
                }else{
                    if(UdCommissionType.USER_CMS.value().equals(commissionDetailTwo.getType())
                            || UdCommissionType.NODE_AWARD.value().equals(commissionDetailTwo.getType())
                            || UdCommissionType.GLOBAL_AWARD.value().equals(commissionDetailTwo.getType())){//用户总收成
                        commissionDetailTwo.setUserTotalProfit(commissionDetailTwo.getProfit());
                    }else if(UdCommissionType.PLAT_CMS.value().equals(commissionDetailTwo.getType())){//平台总收成
                        commissionDetailTwo.setPlatformTotalProfit(commissionDetailTwo.getProfit());
                    }
                    resultMap.put(commissionDetailTwo.getLevelId(),commissionDetailTwo);
                }
            }
        }
        List<HCommissionDetail> resultList = InstanceUtil.newArrayList();
        if(resultMap!=null){
            resultList = new ArrayList(resultMap.values());//map转list
            Integer pageNum = Integer.parseInt(params.get("page").toString());//当前页
            Integer pageSize = Integer.parseInt(params.get("limit").toString());// 每页数目

            //每页的集合下标的开始数
            int index = (pageNum - 1) * pageSize;
            //list的大小
            int total = resultList.size();
            //对list进行分页截取
            resultList = resultList.subList(index,total-index>pageSize?index+pageSize:total);

            return new TableResultResponse<HCommissionDetail>(total, resultList);
        }

        return new TableResultResponse<HCommissionDetail>(resultList.size(), resultList);
    }
}
