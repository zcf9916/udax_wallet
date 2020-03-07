package com.github.wxiaoqi.security.admin.rest.casino;

import com.github.wxiaoqi.security.admin.biz.casino.CasinoRebateConfigBiz;
import com.github.wxiaoqi.security.admin.biz.front.AssetAccountLogBiz;
import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.casino.CasinoRebateConfig;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccountLog;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 推荐返利配置表
 */
@RestController
@RequestMapping("/rebateConfig")
public class CasinoRebateConfigController extends BaseController<CasinoRebateConfigBiz, CasinoRebateConfig> {

    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public TableResultResponse<CasinoRebateConfig> list(@RequestParam Map<String, Object> param) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            param.put("exchangeId", BaseContextHandler.getExId());
        }
        return super.queryListByCustomPage(param);
    }
}
