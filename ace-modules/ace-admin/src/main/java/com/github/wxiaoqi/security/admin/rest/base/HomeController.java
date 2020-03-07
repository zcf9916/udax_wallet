package com.github.wxiaoqi.security.admin.rest.base;

import com.github.wxiaoqi.security.admin.biz.base.HomeBiz;
import com.github.wxiaoqi.security.common.entity.admin.UserTransactionModel;
import com.github.wxiaoqi.security.common.entity.front.FrontTransferDetail;
import com.github.wxiaoqi.security.common.util.model.HomeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 后台首页
 */
@RestController
@RequestMapping("home")
public class HomeController {

    @Autowired
    private HomeBiz homeBiz;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public List<HomeVo> getFrontUserPool(@RequestParam Map<String, Object> params) {
        return homeBiz.selectFrontUserPool(params);
    }

    @RequestMapping(value = "/transferOrder", method = RequestMethod.GET)
    public List<UserTransactionModel> getTransferOrder(@RequestParam Map<String, Object> params) {
        return homeBiz.selectTransferOrder(params);
    }

    @RequestMapping(value = "/totalMchTrade", method = RequestMethod.GET)
    public List<UserTransactionModel> getTotalMchTrade(@RequestParam Map<String, Object> params) {
        return homeBiz.selectTotalMchTrade(params);
    }

    @RequestMapping(value = "/transferDetail", method = RequestMethod.GET)
    public List<FrontTransferDetail> getTransferDetail(@RequestParam Map<String, Object> params) {
        return homeBiz.selectgetTransferDetail(params);
    }

    @RequestMapping(value = "/withdraw", method = RequestMethod.GET)
    public List<UserTransactionModel> getFrontWithdraw(@RequestParam Map<String, Object> params) {
        return homeBiz.selectFrontWithdraw(params);
    }

    @RequestMapping(value = "/recharge", method = RequestMethod.GET)
    public List<HomeVo> getFrontRecharge(@RequestParam Map<String, Object> params) {
        return homeBiz.selectFrontRecharge(params);
    }

    /**
     * 交易所总资产
     *
     * @return
     */
    @RequestMapping(value = "/asset", method = RequestMethod.GET)
    public List<HomeVo> getAsset() {
        return homeBiz.selectTotalAccount();
    }
}
