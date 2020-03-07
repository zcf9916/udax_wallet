package com.github.wxiaoqi.security.admin.rest.front;

import com.github.wxiaoqi.security.admin.biz.front.CmsConfigBiz;
import com.github.wxiaoqi.security.admin.vo.TreeNode;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.BaseVersion;
import com.github.wxiaoqi.security.common.entity.front.CmsConfig;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import com.github.wxiaoqi.security.common.util.StringUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("cmsConfig")
public class CmsConfigController extends BaseController<CmsConfigBiz, CmsConfig> {

    @RequestMapping(value = "/pageAll", method = RequestMethod.GET)
    public List<CmsConfig> pageAll(@RequestParam Map<String, Object> param) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            param.put("exchangeId", BaseContextHandler.getExId());
        }
        List<CmsConfig> list = super.queryListNoPage(param);

        if (StringUtil.listIsBlank(list)){
            return new ArrayList<>();
        }

        ArrayList<CmsConfig> returnTrees = new ArrayList<>();

        for (CmsConfig treeNode : list) {
            if (treeNode.getParentId().equals(AdminCommonConstant.ROOT )) {
                returnTrees.add(treeNode);
            }
        }

        for (CmsConfig cmsConfig : returnTrees) {

            for (CmsConfig treeNode : list) {
                if (treeNode.getParentId().equals(cmsConfig.getId())){
                    if (cmsConfig.getChildren()==null){
                        cmsConfig.setChildren(new ArrayList<CmsConfig>());

                    }
                    cmsConfig.getChildren().add(treeNode);
                }
            }
        }
        return returnTrees;
    }


}
