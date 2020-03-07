package com.github.wxiaoqi.security.admin.rest.base;

import com.github.wxiaoqi.security.admin.biz.base.DictDataBiz;
import com.github.wxiaoqi.security.common.entity.admin.DictData;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("dictData")
public class DictDataController extends BaseController<DictDataBiz, DictData> {


    @RequestMapping(value = "/pageDictData", method = RequestMethod.GET)
    public TableResultResponse<DictData> pageDictData(@RequestParam Map<String, Object> param) {
        return super.queryListByCustomPage(param);
    }
}
