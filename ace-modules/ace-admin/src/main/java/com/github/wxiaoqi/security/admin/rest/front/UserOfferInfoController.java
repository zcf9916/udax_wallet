package com.github.wxiaoqi.security.admin.rest.front;

import com.github.wxiaoqi.security.admin.biz.front.FrontUserBiz;
import com.github.wxiaoqi.security.admin.biz.front.UserOfferInfoBiz;
import com.github.wxiaoqi.security.admin.vo.UserOfferVo;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.UserOfferInfo;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.rest.BaseController;
import com.github.wxiaoqi.security.common.util.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("offerInfo")
public class UserOfferInfoController extends BaseController<UserOfferInfoBiz, UserOfferInfo> {

    @Autowired
    private FrontUserBiz frontUserBiz;

    @Autowired
    private UserOfferInfoBiz userOfferInfoBiz;

    @RequestMapping(value = "/pageAll", method = RequestMethod.GET)
    public TableResultResponse<UserOfferInfo> list(@RequestParam Map<String, Object> params) {
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            params.put("exchangeId", BaseContextHandler.getExId());
        }
        return super.queryListByCustomPage(params);
    }

    /**
     * 查询前端用户List
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/pageUser", method = RequestMethod.GET)
    public TableResultResponse<FrontUser> listAll(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        return frontUserBiz.selectByQuery(query);
    }

    /**
     * 查询报价类型源币种
     */
    @RequestMapping(value = "/listSrcSymbol", method = RequestMethod.GET)
    public List<UserOfferInfo> listSrcSymbol() {
        List<UserOfferInfo> infos = userOfferInfoBiz.listSrcSymbol();
        infos.forEach((info -> {
            info.setSymbol(info.getDstSymbol());
        }));
        return infos;
    }

    /**
     * 根据源货币查目标货币
     */
    @RequestMapping(value = "/listDstSymbol", method = RequestMethod.GET)
    @ResponseBody
    public List<UserOfferInfo> listDstSymbol(String srcSymbol) {
        Example example = new Example(UserOfferInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("dstSymbol", srcSymbol);
        //过滤掉未审核的
        criteria.andEqualTo("status", EnableType.ENABLE.value());
        List<UserOfferInfo> infos = baseBiz.selectByExample(example);
        infos.forEach((info -> {
            info.setSymbol(info.getSrcSymbol());
        }));
        return infos;
    }

    @RequestMapping(value = "/{id}/verify", method = RequestMethod.PUT)
    public ObjectRestResponse<UserOfferVo> update(@RequestBody UserOfferVo entity) throws Exception {
        baseBiz.updateVerifyById(entity);
        return new ObjectRestResponse<UserOfferVo>();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ObjectRestResponse<UserOfferInfo> get(@PathVariable Long id) {
        ObjectRestResponse<UserOfferInfo> entityObjectRestResponse = new ObjectRestResponse<>();
        UserOfferInfo o = baseBiz.selectById(id);
        o.setRemainVolume(BigDecimal.ZERO);
        entityObjectRestResponse.data(o);
        return entityObjectRestResponse;
    }

    @RequestMapping(value = "/{id}/userInfoFreezeAndDelete", method = RequestMethod.DELETE)
    public ObjectRestResponse<UserOfferInfo> userInfoFreezeAndDelete(@PathVariable Long id) throws Exception {
        baseBiz.userInfoFreezeAndDelete(id);
        return new ObjectRestResponse<UserOfferInfo>();
    }
}
