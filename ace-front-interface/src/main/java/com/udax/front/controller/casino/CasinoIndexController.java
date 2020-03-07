package com.udax.front.controller.casino;

import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.FrontAdvert;
import com.github.wxiaoqi.security.common.entity.casino.CasinoCommissionLog;
import com.github.wxiaoqi.security.common.entity.casino.CasinoUserInfo;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.ud.HCommissionRelation;
import com.github.wxiaoqi.security.common.enums.ClientType;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.ValidType;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.Query;
import com.udax.front.biz.CacheBiz;
import com.udax.front.biz.FrontUserBiz;
import com.udax.front.biz.casino.CasinoCommissionLogBiz;
import com.udax.front.biz.casino.CasinoUserInfoBiz;
import com.udax.front.biz.ud.HCommissionRelationBiz;
import com.udax.front.controller.BaseFrontController;
import com.udax.front.util.BizControllerUtil;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.vo.rspvo.ud.UDAdRspVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * @author zhoucf  不需要token的接口
 * @create 2018／2／27
 */
@RestController
@RequestMapping("/wallet/casino/")
public class CasinoIndexController extends BaseFrontController<FrontUserBiz,FrontUser> {


    @Autowired
    private CasinoUserInfoBiz frontUserBiz;

    @Autowired
    private HCommissionRelationBiz cmsRelationBiz;

    @Autowired
    private CacheBiz cacheBiz;
    @Autowired
    private FrontUserBiz frontUserInfoBiz;
    @Autowired
    private CasinoCommissionLogBiz commissionLogBiz;

    //用户信息
    @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse userInfo() throws Exception{
        FrontUser user = frontUserBiz.selectCasinoUnionUserInfoById(BaseContextHandler.getUserID());
        FrontUser userInfo = frontUserInfoBiz.selectUnionUserInfoById(BaseContextHandler.getUserID());
        Map<String,Object> rsp = InstanceUtil.newHashMap();
        rsp.put("isValid",user.getCasinoUserInfo().getIsValid());
        rsp.put("userName",user.getCasinoUserInfo().getCasinoName());
        rsp.put("mobile",user.getMobile());
        rsp.put("email",user.getEmail());
        rsp.put("name",Optional.ofNullable(userInfo.getUserInfo().getFirstName()).orElseGet(() -> {return "";}) + Optional.ofNullable(userInfo.getUserInfo().getRealName()).orElseGet(() -> {return "";}));
        rsp.put("direct",user.getCasinoUserInfo().getDirectChild());//直接
        rsp.put("indirect",user.getCasinoUserInfo().getAllChild());//所有
        rsp.put("type",user.getCasinoUserInfo().getType());//0 未激活用户   1 精英用户   2 副总经理  3  副总裁
        return new ObjectRestResponse().data(rsp);
    }


    //绑定账户
    @RequestMapping(value = "/bind/account", method = RequestMethod.PUT)
    @ResponseBody
    public ObjectRestResponse bind(@RequestParam String casinoAccount) throws Exception{
        CasinoUserInfo userInfo = new CasinoUserInfo();
        userInfo.setCasinoName(casinoAccount);
        userInfo.setIsValid(ValidType.SUBMIT.value());
        Example example = new Example(CasinoUserInfo.class);
        example.createCriteria().andEqualTo("userId",BaseContextHandler.getUserID());
        frontUserBiz.updateByExampleSelective(userInfo,example);
        return new ObjectRestResponse();
    }

    //分成基本信息
    @RequestMapping(value = "/cms/info", method = RequestMethod.GET)
    @ResponseBody
    public ObjectRestResponse info() throws Exception{
        //第一排：推广总人数（下线3代）      总收益：0
        //邀请记录信息显示：注册时间、邀请人手机号（图标显示）、邀请人邮箱（图标显示）、属于当前推广人发展的第几代（显示第几代）
        Map<String,Object> rspMap = InstanceUtil.newHashMap();
        FrontUser user = frontUserBiz.selectCasinoUnionUserInfoById(BaseContextHandler.getUserID());
        if(user == null){
            return new ObjectRestResponse().status(ResponseCode.PARAM_ERROR);
        }
//        Example example = new Example(HCommissionRelation.class);
//        example.createCriteria().andEqualTo("receiveUserId",user.getId())
//                .andLessThanOrEqualTo("level",user.getCasinoUserInfo().getLevel() + 3);
        //推广总人数
       // int count =  cmsRelationBiz.selectCountByExample(example);
        //总收益
        BigDecimal cms  = commissionLogBiz.getCMs(user.getId());

        BigDecimal rebate  = commissionLogBiz.getRebate(user.getId());
        rspMap.put("count",user.getCasinoUserInfo().getAllChild());//推广总人数
        rspMap.put("cms",Optional.ofNullable(cms).orElseGet(()->{return BigDecimal.ZERO;}).setScale(2,BigDecimal.ROUND_UP).stripTrailingZeros().toPlainString());//分销总收益

        rspMap.put("rebate",Optional.ofNullable(rebate).orElseGet(()->{return BigDecimal.ZERO;}).setScale(2,BigDecimal.ROUND_UP).stripTrailingZeros().toPlainString());//信用证总收益

        List<FrontAdvert> noticeList =  CacheBizUtil.getFrontAdvert(BaseContextHandler.getAppExId(),ClientType.APP.value(),BaseContextHandler.getLanguage(),cacheBiz);
        List<UDAdRspVo> adList = BizControllerUtil.transferEntityToListVo(UDAdRspVo.class,noticeList);
        rspMap.put("ad",adList);
        return new ObjectRestResponse().data(rspMap);
    }

    //查看分成的历史
    @RequestMapping(value = "/cms/record", method = RequestMethod.GET)
    @ResponseBody
    //@RequestBody @Valid QueueListModel model
    public TableResultResponse record(@RequestParam int page,@RequestParam int limit,@RequestParam int cmsType) throws Exception{
        if(limit >20) limit = 10;
        FrontUser currentUser = frontUserBiz.selectCasinoUnionUserInfoById(BaseContextHandler.getUserID());
        //查询自己已经结算的分成
        Map<String,Object> param = InstanceUtil.newHashMap();
        param.put("page",page);
        param.put("limit",limit);
        param.put("receiveUserId",BaseContextHandler.getUserID());
        param.put("cmsType",cmsType);//CasinoCommissionLog.CmsType.REBATE.value()
        param.put("orderByInfo","order_time desc");
        Query query = new Query(param);
        TableResultResponse<CasinoCommissionLog> pageResult =  commissionLogBiz.pageQuery(query);
        Map<Long,FrontUser> userIdMap = InstanceUtil.newHashMap();
        List<JSONObject> rspList = pageResult.getData().getRows().stream().collect(
                Collectors.mapping( l -> {
                    JSONObject ob = new JSONObject();
                    if(userIdMap.get(l.getDirectUserId()) == null){
                        FrontUser temp = new FrontUser();
                        FrontUser user = Optional.ofNullable(frontUserBiz.selectCasinoUnionUserInfoById(l.getDirectUserId())).orElse(temp);
                        userIdMap.put(l.getDirectUserId(),user);
                    }
                    FrontUser user =   userIdMap.get(l.getDirectUserId());
                    ob.put("orderTime",cmsType == CasinoCommissionLog.CmsType.REBATE.value() ? l.getOrderTime(): user.getCasinoUserInfo().getCreateTime().getTime()/1000);//订单时间
                    ob.put("level",  user.getCasinoUserInfo().getLevel() - currentUser.getCasinoUserInfo().getLevel());//类型  1 转账  2转币
                    ob.put("amount",l.getAmount().stripTrailingZeros().toPlainString() + " HKD");//返利币种
                    ob.put("userName",l.getDirectName());
                    ob.put("totalAmount",l.getTotalAmount().stripTrailingZeros().toPlainString() + " HKD");//返利币种
                    return ob;
                }, Collectors.toList())
        );

        TableResultResponse rspPage =  new TableResultResponse<>();
        rspPage.getData().setRows(rspList);
        rspPage.getData().setTotal(pageResult.getData().getTotal());
        rspPage.setRel(pageResult.isRel());
        rspPage.setStatus(pageResult.getStatus());
        rspPage.setMessage(pageResult.getMessage());
        return rspPage;
    }


    //查看邀请记录
    @RequestMapping(value = "/cms/inviteRecord", method = RequestMethod.GET)
    @ResponseBody
    //@RequestBody @Valid QueueListModel model
    public TableResultResponse currentQueue(@RequestParam int page,@RequestParam int limit) throws Exception{
        Query query = new Query();
        query.setLimit(limit > 50 ? 10 : limit);
        query.setPage(page);
        FrontUser user = frontUserBiz.selectCasinoUnionUserInfoById(BaseContextHandler.getUserID());
        query.put("receiveUserId",BaseContextHandler.getUserID());
        query.put("level:<=",user.getCasinoUserInfo().getLevel() + 2);//2代以内的用户
        TableResultResponse<HCommissionRelation> result = cmsRelationBiz.pageQuery(query);
        List<JSONObject> rspList = result.getData().getRows().stream().collect(
                Collectors.mapping( l -> {
                    JSONObject ob = new JSONObject();
                    FrontUser temp = new FrontUser();
                    FrontUser u = Optional.ofNullable(frontUserBiz.selectCasinoUnionUserInfoById(l.getUserId())).orElse(temp);
                    ob.put("userName",Optional.ofNullable(u.getUserName()).orElseGet(() -> {return "";}));
                    ob.put("mobile",Optional.ofNullable(u.getMobile()).orElseGet(() -> {return "";}));
                    ob.put("email",Optional.ofNullable(u.getEmail()).orElseGet(() ->{return "";}));
                    ob.put("createTime",u.getCasinoUserInfo().getCreateTime().getTime());//订
                    ob.put("status",u.getCasinoUserInfo().getType());//0 未激活用户   1 精英用户   2 副总经理  3  副总裁
                    ob.put("level",u.getCasinoUserInfo().getLevel() - user.getCasinoUserInfo().getLevel());//第几代
                    return ob;
                }, Collectors.toList())
        );

        TableResultResponse rspPage =  new TableResultResponse<>();
        rspPage.getData().setRows(rspList);
        rspPage.getData().setTotal(result.getData().getTotal());
        rspPage.setRel(result.isRel());
        rspPage.setStatus(result.getStatus());
        rspPage.setMessage(result.getMessage());
        return rspPage;


    }

}
