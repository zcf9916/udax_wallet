package com.udax.front.biz.fund;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.fund.FundProductInfo;
import com.github.wxiaoqi.security.common.mapper.fund.FundProductInfoMapper;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.udax.front.vo.reqvo.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author zhoucf
 * @date 2019-3-26 19:43:46
 */
@Service
@Slf4j
public class FundProductBiz extends BaseBiz<FundProductInfoMapper,FundProductInfo> {



//    /**
//     * 用户认证并通知用户审核人员
//     */
//    @Transactional
//    public void authUserInfo(FrontUserInfo frontUserInfo) {
//        updateUserInfoByUserId(frontUserInfo);
//    }
//
      //分页连表查询
     public  TableResultResponse selectUnionPage(Map<String,Object> param, PageInfo pageInfo){
         Page<Object> result = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
         List<FundProductInfo> list =  mapper.selectUnionPage(param);
         return new TableResultResponse(result.getTotal(), list);
     }

    //连表查询
    public  FundProductInfo selectUnion(Map<String,Object> param){
        List<FundProductInfo> list =  mapper.selectUnionPage(param);
        return StringUtil.listIsNotBlank(list) ? list.get(0) : null;
    }

}