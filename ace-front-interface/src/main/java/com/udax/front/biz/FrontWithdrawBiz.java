package com.udax.front.biz;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccount;
import com.github.wxiaoqi.security.common.entity.front.FrontRecharge;
import com.github.wxiaoqi.security.common.entity.front.FrontWithdraw;
import com.github.wxiaoqi.security.common.mapper.front.FrontRechargeMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontWithdrawMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 
 *
 * @author Mr.AG
 * @email 463540703@qq.com
 * @date 2017-12-26 19:43:46
 */
@Service
@Slf4j
public class FrontWithdrawBiz extends BaseBiz<FrontWithdrawMapper,FrontWithdraw> {


    //查询当日提币量

    /**
     *
     * @param userId 用户
     * @param dcCode
     * @return
     */
    public BigDecimal queryWithdrawDaily(Long userId,String dcCode){
        return mapper.queryWithdrawDaily(userId,dcCode);
    }





}