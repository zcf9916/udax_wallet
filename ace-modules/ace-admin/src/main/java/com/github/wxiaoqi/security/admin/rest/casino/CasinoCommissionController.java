package com.github.wxiaoqi.security.admin.rest.casino;

import com.github.wxiaoqi.security.admin.biz.casino.CasinoCommissionBiz;
import com.github.wxiaoqi.security.admin.biz.casino.CasinoCommissionLogBiz;
import com.github.wxiaoqi.security.admin.biz.casino.CasinoUserInfoBiz;
import com.github.wxiaoqi.security.common.entity.casino.CasinoCommission;
import com.github.wxiaoqi.security.common.entity.casino.CasinoCommissionLog;
import com.github.wxiaoqi.security.common.entity.casino.CasinoMethodConfig;
import com.github.wxiaoqi.security.common.entity.casino.CasinoUserInfo;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import com.github.wxiaoqi.security.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/casinoCommission")
public class CasinoCommissionController extends BaseController<CasinoCommissionBiz, CasinoCommission> {


    @Autowired
    private CasinoCommissionLogBiz casinoCommissionLogBiz;

    @Autowired
    private CasinoUserInfoBiz casinoUserInfoBiz;

    @RequestMapping(value = "",method = RequestMethod.POST)
    public ObjectRestResponse<CasinoCommission> add(@RequestBody CasinoCommission entity){

        casinoCommissionLogBiz.addCommission(entity);
        return new ObjectRestResponse<CasinoCommission>();
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public ObjectRestResponse<CasinoCommission> remove(@PathVariable Long id){
        CasinoCommission commission = baseBiz.selectById(id);
        CasinoCommissionLog log = new CasinoCommissionLog();
        log.setOrderNo(commission.getOrderNo());
        List<CasinoCommissionLog> logs = casinoCommissionLogBiz.selectList(log);
        if (!StringUtil.listIsBlank(logs)){
            for (CasinoCommissionLog commissionLog : logs) {
                if (!CasinoMethodConfig.RoleType.WHITE.value().equals(commissionLog.getRoleType())){
                    CasinoUserInfo info = new CasinoUserInfo();
                    info.setUserId(commissionLog.getDirectUserId());
                    CasinoUserInfo casinoUserInfo = casinoUserInfoBiz.selectOne(info);
                    casinoUserInfo.setTotalAmount(casinoUserInfo.getTotalAmount().subtract(commissionLog.getAmount()));
                    casinoUserInfoBiz.updateById(casinoUserInfo);
                }
            }
        }
        casinoCommissionLogBiz.delete(log);
        //删除返佣明细
        casinoCommissionLogBiz.addCommission(commission);
        return new ObjectRestResponse<CasinoCommission>();
    }

}
