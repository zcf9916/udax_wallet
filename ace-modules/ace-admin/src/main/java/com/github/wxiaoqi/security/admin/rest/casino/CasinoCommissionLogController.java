package com.github.wxiaoqi.security.admin.rest.casino;

import com.github.wxiaoqi.security.admin.biz.casino.CasinoCommissionLogBiz;
import com.github.wxiaoqi.security.common.entity.casino.CasinoCommissionLog;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/casinoCommissionLog")
public class CasinoCommissionLogController extends BaseController<CasinoCommissionLogBiz, CasinoCommissionLog> {


    @RequestMapping(value = "/pageLog", method = RequestMethod.GET)
    public TableResultResponse<CasinoCommissionLog> pageLog(@RequestParam Map<String, Object> params) {
        return super.queryListByCustomPage(params);
    }

}
