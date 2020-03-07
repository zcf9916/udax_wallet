package com.udax.front.biz.ud;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.ud.HCommissionDetail;
import com.github.wxiaoqi.security.common.mapper.ud.HCommissionDetailMapper;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.Query;
import com.udax.front.util.BizControllerUtil;
import com.udax.front.vo.rspvo.ud.UDCmsRspVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 
 *
 * @author Mr.AG
 * @email 463540703@qq.com
 * @date 2017-12-26 19:43:46
 */
@Service
@Slf4j
public class HCommissionDetailBiz extends BaseBiz<HCommissionDetailMapper, HCommissionDetail> {




    public TableResultResponse selectPage(Query query) throws Exception{
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<HCommissionDetail> list = mapper.selectPage(query);
        List<UDCmsRspVo> rspVoList =BizControllerUtil.transferEntityToListVo(UDCmsRspVo.class,list);
        return new TableResultResponse<UDCmsRspVo>(result.getTotal(), rspVoList);
    }


    //获取用户获取的分成利润
    public BigDecimal getPowProfit(){
        Long userId = BaseContextHandler.getUserID();
        BigDecimal returnResult = mapper.getPowProfit(userId);
        return returnResult == null ? BigDecimal.ZERO : returnResult;
    }



}