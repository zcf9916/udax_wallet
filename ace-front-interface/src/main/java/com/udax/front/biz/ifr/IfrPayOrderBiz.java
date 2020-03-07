package com.udax.front.biz.ifr;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.ifr.IfrPayOrder;
import com.github.wxiaoqi.security.common.enums.AccountLogType;
import com.github.wxiaoqi.security.common.enums.AccountSignType;
import com.github.wxiaoqi.security.common.enums.IfrOrderStatus;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.mapper.ifr.IfrPayOrderMapper;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import com.udax.front.biz.DcAssertAccountBiz;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;


@Service
@Slf4j
public class IfrPayOrderBiz extends BaseBiz<IfrPayOrderMapper, IfrPayOrder> {


    @Autowired
    private DcAssertAccountBiz assertBiz;

    public void orderCallback(int result, String orderNo, Integer amount) throws Exception {

        if (StringUtils.isBlank(orderNo) || amount == null) {
            logger.error("ifrorder 回调  订单号为空 或 充值金额为空");
             return;
         }
         IfrPayOrder param = new IfrPayOrder();
         param.setPayStatus(IfrOrderStatus.INIT.value());
         param.setOrderNo(orderNo);

         IfrPayOrder order = mapper.selectOne(param);
         if( order == null){
             logger.error("ifrorder 回调  订单号查询结果为空:" + orderNo);
             throw new BusinessException(ResponseCode.PARAM_ERROR.name());
         }
//        if (order.getAmount().intValue() * 100 != amount) {
//            logger.error("ifrorder回调---充值金额与订单金额不一致！");
//            return;
//        }

        //成功
        if(result == IfrOrderStatus.SUCCESS.value()){

            //增加冻结的数量
            AccountAssertLogVo payVo = new AccountAssertLogVo();
            payVo.setUserId(order.getUserId());
            payVo.setSymbol("IFR");
            payVo.setAmount(order.getUnits());
            payVo.setChargeSymbol("IFR");
            payVo.setType(AccountLogType.RECHARGE);
            payVo.setTransNo(orderNo);//充值流水号
            payVo.setRemark(AccountLogType.RECHARGE.name());
            assertBiz.signUpdateAssert(payVo,AccountSignType.ACCOUNT_RECHARGE_FREEZE);
        }

        //更新订单状态
        Example example = new Example(IfrPayOrder.class);
        example.createCriteria().andEqualTo("orderNo", orderNo);
        IfrPayOrder updateOrder = new IfrPayOrder();
        updateOrder.setPayStatus(result);
        mapper.updateByExampleSelective(updateOrder, example);

    }

}
