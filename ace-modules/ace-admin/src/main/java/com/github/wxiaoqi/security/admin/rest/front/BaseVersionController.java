package com.github.wxiaoqi.security.admin.rest.front;

import com.github.wxiaoqi.security.admin.biz.front.BaseVersionBiz;
import com.github.wxiaoqi.security.common.entity.admin.BaseVersion;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("baseVersion")
public class BaseVersionController extends BaseController<BaseVersionBiz, BaseVersion> {

    @RequestMapping(value = "/pageAll", method = RequestMethod.GET)
    public TableResultResponse<BaseVersion> pageFrontWithdraw(@RequestParam Map<String, Object> param) {
        return super.queryListByCustomPage(param);
    }
}
