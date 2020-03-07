package com.udax.front.controller.ud;

import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.FrontAdvert;
import com.github.wxiaoqi.security.common.entity.admin.FrontNotice;
import com.github.wxiaoqi.security.common.entity.front.DcAssetAccount;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.front.FrontUserInfo;
import com.github.wxiaoqi.security.common.entity.ud.*;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.enums.ud.QueueStatus;
import com.github.wxiaoqi.security.common.enums.ud.UDOrderDetailStatus;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.*;
import com.udax.front.annotation.UserOperationAuthority;
import com.udax.front.bean.WithdrawModel;
import com.udax.front.biz.CacheBiz;
import com.udax.front.biz.DcAssertAccountBiz;
import com.udax.front.biz.FrontUserBiz;
import com.udax.front.biz.common.CommonControllerBiz;
import com.udax.front.biz.ud.*;
import com.udax.front.bizmodel.CommonBiz;
import com.udax.front.controller.BaseFrontController;
import com.udax.front.service.ServiceUtil;
import com.udax.front.util.BizControllerUtil;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.vo.reqvo.PageInfo;
import com.udax.front.vo.reqvo.ud.QueueListModel;
import com.udax.front.vo.reqvo.ud.QueueModel;
import com.udax.front.vo.reqvo.ud.UnlockModel;
import com.udax.front.vo.rspvo.OwnMemberVo;
import com.udax.front.vo.rspvo.ud.*;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhoucf  不需要token的接口
 * @create 2018／2／27
 */
@RestController
@RequestMapping("/wallet/ud/")
public class UDIndexController extends BaseFrontController<FrontUserBiz,FrontUser> {
    @Autowired
    private HUserInfoBiz userInfoBiz;

    @Autowired
    private HPurchaseLevelBiz hPurchaseLevelBiz;

    @Autowired
    private HQueueBiz hQueueBiz;

    @Autowired
    private FrontUserBiz frontUserBiz;

    @Autowired
    private HParamBiz paramBiz;

    @Autowired
    private  HUnLockDetailBiz unLockDetailBiz;

    @Autowired
    private HOrderDetailBiz orderDetailBiz;


    @Autowired
    private HSettledProfitBiz settledProfitBiz;

    @Autowired
    private CommonControllerBiz commonBiz;

    @Autowired
    private HCommissionDetailBiz detailBiz;

    @Autowired
    private CacheBiz cacheBiz;

    @Autowired
    private DcAssertAccountBiz accountBiz;

    //首页展示内容
    @RequestMapping(value = "/ad", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse ad() throws Exception{
       List<FrontAdvert> noticeList =  CacheBizUtil.getFrontAdvert(BaseContextHandler.getAppExId(),ClientType.APP.value(),BaseContextHandler.getLanguage(),cacheBiz);
       List<UDAdRspVo> adList = BizControllerUtil.transferEntityToListVo(UDAdRspVo.class,noticeList);
       return new ObjectRestResponse().data(adList);
    }


    //首页展示内容
    @RequestMapping(value = "/index", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse index() throws Exception{
        //查询用户信息
        UDIndexRspVo indexRspVo = new UDIndexRspVo();
        HUserInfo queryParam = new HUserInfo();
        queryParam.setUserId(BaseContextHandler.getUserID());
        HUserInfo hUserInfo = userInfoBiz.selectOne(queryParam);

        //首页展示udax  ud 的余额
        //HParam  symbolOne = ServiceUtil.getUdParamByKey("HOME_SYMBOL_ONE",paramBiz);
        HParam  symbolTwo = ServiceUtil.getUdParamByKey("HOME_SYMBOL_TWO",paramBiz);

        DcAssetAccount assetAccount = new DcAssetAccount();
        assetAccount.setUserId(BaseContextHandler.getUserID());
        //assetAccount.setSymbol(symbolOne.getUdValue());
        //资产数据
        //DcAssetAccount udxAssert = accountBiz.selectOne(assetAccount);

        assetAccount.setSymbol(symbolTwo.getUdValue());
        DcAssetAccount udAssert = accountBiz.selectOne(assetAccount);

        //indexRspVo.setUdxAssert(udxAssert == null ? BigDecimal.ZERO : udxAssert.getTotalAmount());
        indexRspVo.setUdxAssert(BigDecimal.ZERO);
        indexRspVo.setUdAssert(udAssert == null ? BigDecimal.ZERO : udAssert.getAvailableAmount());
        indexRspVo.setUdSymbol(symbolTwo.getUdValue());
        indexRspVo.setUdxSymbol("");



        //查询投资方案信息
        UDIndexLevelRspVo rspVo = new UDIndexLevelRspVo();
        HPurchaseLevel  proximalLevel = hPurchaseLevelBiz.getCurrentLevel(hUserInfo);//当前能投资的级别
        BeanUtils.copyProperties(proximalLevel,rspVo);
        int userLevel = hPurchaseLevelBiz.getNodeAward(hUserInfo);
        indexRspVo.setUserLevel(userLevel);
        //查询排队中的队列
        HQueue param = new HQueue();
        param.setUserId(BaseContextHandler.getUserID());
        param.setStatus(QueueStatus.WAIT_MATCH.value());//运行中的队列
        HQueue hQueue =  hQueueBiz.selectOne(param);
        if(hQueue != null){
            HQueue countParam = new HQueue();
            countParam.setLevelId(hQueue.getLevelId());
            countParam.setStatus(EnableType.ENABLE.value());
            Long queueNum = hQueueBiz.selectCount(countParam);//当前排队人数

            Example example = new Example(HQueue.class);
            example.createCriteria().andEqualTo("levelId", hQueue.getLevelId())
                    .andEqualTo("status", EnableType.ENABLE.value())
                    .andLessThanOrEqualTo("id",hQueue.getId());
            int currentPosition = hQueueBiz.selectCountByExample(example);//在队列中的位置

            UDCurrentQueueRspVo queueRspVo = new UDCurrentQueueRspVo();
            BeanUtils.copyProperties(hQueue,queueRspVo);
            queueRspVo.setCurrentPosition(currentPosition);
            queueRspVo.setQueueNum(queueNum);

            indexRspVo.setQueueRspVo(queueRspVo);
        }
        //查询匹配中的订单
        HOrderDetail orderDetailParam = new HOrderDetail();
        orderDetailParam.setUserId(BaseContextHandler.getUserID());
        orderDetailParam.setStatus(UDOrderDetailStatus.INIT.value());//匹配中的订单
        List<HOrderDetail> orderDetailList =  orderDetailBiz.selectList(orderDetailParam);
        if(StringUtil.listIsNotBlank(orderDetailList)){
            HOrderDetail orderDetail = orderDetailList.get(0);
            UDOrderRspVo orderRspVo = new UDOrderRspVo();
            BeanUtils.copyProperties(orderDetail,orderRspVo);
            //结束时间
            Date endTime = LocalDateUtil.localDate2Date(LocalDateUtil.date2LocalDate(orderRspVo.getCreateTime()).plusDays(orderDetail.getSettleDay()));
            orderRspVo.setEndTime(endTime);
            HSettledProfit profitQuery = new HSettledProfit();
            profitQuery.setOrderNo(orderDetail.getOrderNo());
            HSettledProfit profit = settledProfitBiz.selectOne(profitQuery);
            //订单收益
            if(profit != null){
                orderRspVo.setProfit(profit.getFreezeProfit());//当前的利润
            }
            indexRspVo.setOrderRspVo(orderRspVo);
        }

        //如果用户是锁定状态
        if(hUserInfo.getStatus().equals(EnableType.DISABLE.value())){
            indexRspVo.setExpire(0);
        } else {
            HParam  hparam = ServiceUtil.getUdParamByKey("EXPIRE",paramBiz);
            Long expire = Long.valueOf(hparam.getUdValue());//解锁有效期
            //查询最近一次解锁时间
            PageHelper.startPage(1,1);
            Example example = new Example(HUnLockDetail.class);
            example.createCriteria().andEqualTo("userId",BaseContextHandler.getUserID());
            example.setOrderByClause("id desc");
            List<HUnLockDetail> unLockDetailList = unLockDetailBiz.selectByExample(example);
            if(StringUtil.listIsNotBlank(unLockDetailList)){
                Date unlockTime = unLockDetailList.get(0).getCreateTime();
                Long days = expire - (LocalDate.now().toEpochDay() - LocalDateUtil.date2LocalDate(unlockTime).toEpochDay());
                indexRspVo.setExpire(days.intValue() > 0 ? days.intValue() : 0 );
                //如果超时时间被改了,同步更新用户状态
                Integer userStatus = hUserInfo.getStatus();
                Integer actualStatus = indexRspVo.getExpire() > 0 ? EnableType.ENABLE.value() : EnableType.DISABLE.value();
                if( userStatus != actualStatus){
                    HUserInfo updateParam = new HUserInfo();
                    updateParam.setId(hUserInfo.getId());
                    updateParam.setStatus(actualStatus);
                    userInfoBiz.updateSelectiveById(updateParam);
                }

            }
        }
        HParam  hparam = ServiceUtil.getUdParamByKey("UNLOCK_AMOUNT",paramBiz);//解锁金额

        //返回的对象
        indexRspVo.setUnlockAmount(new BigDecimal(hparam.getUdValue()));
        indexRspVo.setAutoInvest(hUserInfo.getAutoRepeat());
        indexRspVo.setPos(settledProfitBiz.getAllProfit());
        indexRspVo.setPow(detailBiz.getPowProfit().stripTrailingZeros().toPlainString() + " " + rspVo.getSymbol() );
        indexRspVo.setCurrentAmount(hUserInfo.getTotalAmount().intValue());
        indexRspVo.setAllChild(hUserInfo.getAllChild());
        indexRspVo.setDirectChild(hUserInfo.getDirectChild());
        indexRspVo.setLevelRspVo(rspVo);
        return new ObjectRestResponse().data(indexRspVo);
    }

    //方案详情页面
    @RequestMapping(value = "/levelDetail", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse levelDetail(@RequestParam(required = false) Long id) throws Exception{

        Map<String,Object> rspMap = InstanceUtil.newHashMap();
        //查询用户
        HUserInfo queryParam = new HUserInfo();
        queryParam.setUserId(BaseContextHandler.getUserID());
        HUserInfo hUserInfo = userInfoBiz.selectOne(queryParam);



        //List<> ids = list.stream() .map(HCommissionRelation::getReceiveUserId).collect(Collectors.toList());
        //查询当前能投资的方案
        List<HPurchaseLevel>  proximalLevelList = hPurchaseLevelBiz.getLevelListByCurrentLevel(hUserInfo);//当前能投资的级别列表
        //idmap
        Map<Long,Long> idList = proximalLevelList.stream().collect(Collectors.toMap(HPurchaseLevel::getId, HPurchaseLevel::getId));
        HQueue countParam = new HQueue();
        if(StringUtil.listIsNotBlank(proximalLevelList)){
            countParam.setLevelId(proximalLevelList.get(0).getId());
            countParam.setStatus(EnableType.ENABLE.value());
            Long queueNum = hQueueBiz.selectCount(countParam);//当前排队人数
            rspMap.put("queueNum",queueNum);
        }




//       ` if( id != null){
//            HPurchaseLevel  level = hPurchaseLevelBiz.selectById(id);
//            if(level != null){
//                BeanUtils.copyProperties(level,rspVo);
//                rspVo.setIfCurrentLevel(EnableType.DISABLE.value());
//            }
//        }

        HPurchaseLevel levelParam = new HPurchaseLevel();
        levelParam.setExchId(hUserInfo.getExchangeId());
        levelParam.setIsOpen(EnableType.ENABLE.value());
        //过滤出满足条件的
        List<HPurchaseLevel> list = hPurchaseLevelBiz.selectList(levelParam);
        List<UDLevelDetailRspVo> rspList = BizControllerUtil.transferEntityToListVo(UDLevelDetailRspVo.class,list);
        rspList.stream().forEach((r) ->{
            if(idList.get(r.getId()) != null){
                r.setIfCurrentLevel(EnableType.ENABLE.value());
            }
        });
        rspMap.put("vo",rspList);

        return new ObjectRestResponse().data(rspMap);
    }


    //用户信息
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse info() throws Exception{


        HUserInfo queryParam = new HUserInfo();
        queryParam.setUserId(BaseContextHandler.getUserID());
        HUserInfo hUserInfo = userInfoBiz.selectOne(queryParam);

        Map<String,Object> rspMap = InstanceUtil.newHashMap();
        rspMap.put("currentAmount",hUserInfo.getTotalAmount());//有效的申购总量
        rspMap.put("directChild",hUserInfo.getDirectChild());
        rspMap.put("allChild",hUserInfo.getAllChild());
        rspMap.put("status",hUserInfo.getStatus());
        rspMap.put("isValid",hUserInfo.getIsValid());
        rspMap.put("autoInvest",hUserInfo.getAutoRepeat());
        return new ObjectRestResponse().data(rspMap);
    }


    //用户解锁
    @RequestMapping(value = "/unlock", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse unlock(@RequestBody @Valid UnlockModel model) throws Exception{
        // 判断支付密码
        boolean flag = ServiceUtil.judgePayPassword(model.getPassword(), BaseContextHandler.getUserID(), frontUserBiz);
        if (!flag) {
            return new ObjectRestResponse().status(ResponseCode.TRADE_PWD_ERROR);
        }
        unLockDetailBiz.unlock();
        HParam hparam = ServiceUtil.getUdParamByKey("EXPIRE",paramBiz);
        return new ObjectRestResponse().data(hparam == null ? 0 : hparam.getUdValue());
    }




    //用户开启/关闭自动复投功能
    @RequestMapping(value = "/autoInvest", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse autoInvest() throws Exception{


        HUserInfo userInfo = ServiceUtil.selectUdUserInfo(userInfoBiz);
        HUserInfo updateParam = new HUserInfo();
        updateParam.setId(userInfo.getId());
        updateParam.setAutoRepeat(EnableType.ENABLE.value());
        if(userInfo.getAutoRepeat().equals(EnableType.ENABLE.value())){
            updateParam.setAutoRepeat(EnableType.DISABLE.value());
        }
        userInfoBiz.updateSelectiveById(updateParam);
        return new ObjectRestResponse().data(updateParam.getAutoRepeat());
    }


    //排队
    @UserOperationAuthority("UD_QUEUE") //判断当前用户申购权限是否被禁用
    @RequestMapping(value = "/queue", method = RequestMethod.POST)
    @ResponseBody
    public ObjectRestResponse queue(@RequestBody @Valid QueueModel model) throws Exception{
        // 判断支付密码
        boolean flag = ServiceUtil.judgePayPassword(model.getPassword(), BaseContextHandler.getUserID(), frontUserBiz);
        if (!flag) {
            return new ObjectRestResponse().status(ResponseCode.TRADE_PWD_ERROR);
        }


        hQueueBiz.queue(model.getId());

        return new ObjectRestResponse();
    }


    //查询申购历史
    @RequestMapping(value = "/queueList", method = RequestMethod.POST)
    @ResponseBody
    public TableResultResponse queueList(@RequestBody @Valid QueueListModel model) throws Exception{
//        HQueue param = new HQueue();
//        param.setUserId(BaseContextHandler.getUserID());
//        param.setStatus(EnableType.ENABLE.value());//运行中的队列
//        HQueue hQueue =  hQueueBiz.selectOne(param);

        Map<String,Object> param = BizControllerUtil.modelToMap(model);
        param.put("userId",BaseContextHandler.getUserID());
        if(StringUtils.isNotBlank(model.getEndDate())){
            org.joda.time.LocalDateTime localDateTime  = org.joda.time.LocalDateTime.parse(model.getEndDate());
            String endDate =  localDateTime.plusDays(1).toString(DateUtil.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
            param.put("endDate",endDate);
        }
        Query query = new Query(param);
        return hQueueBiz.selectQueueList(query);
    }

    //查看分成的历史
    @RequestMapping(value = "/cmsRecord", method = RequestMethod.POST)
    @ResponseBody
    public TableResultResponse currentQueue(@RequestBody @Valid QueueListModel model) throws Exception{
//        HQueue param = new HQueue();
//        param.setUserId(BaseContextHandler.getUserID());
//        param.setStatus(EnableType.ENABLE.value());//运行中的队列
//        HQueue hQueue =  hQueueBiz.selectOne(param);

        Map<String,Object> param = BizControllerUtil.modelToMap(model);
        param.put("receiveUserId",BaseContextHandler.getUserID());
        if(StringUtils.isNotBlank(model.getEndDate())){
            org.joda.time.LocalDateTime localDateTime  = org.joda.time.LocalDateTime.parse(model.getEndDate());
            String endDate =  localDateTime.plusDays(1).toString(DateUtil.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
            param.put("endDate",endDate);
        }
        Query query = new Query(param);
        return detailBiz.selectPage(query);
    }

//    @PostMapping("getOwnMember")
//    @ApiOperation(value = "获取自己所有直推的会员", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Object getOwnMember(@RequestBody @Valid PageInfo model) throws Exception {
//        Map<String,Object> param = BizControllerUtil.modelToMap(model);
//        param.put("parentId",BaseContextHandler.getUserID());
//        Query query = new Query(param);
////        userInfoBiz.queryListByCustomPage(query);
//        //TableResultResponse resultResponse = queryByCustomPage(query,userInfoBiz);
//        //List<HUserInfo> userInfoList = resultResponse.getData().getRows();
//        TableResultResponse resultResponse = queryByCustomPage(query,userInfoBiz);
//        List<HUserInfo> userInfoList = resultResponse.getData().getRows();
//        List<OwnMemberVo> rspVolist = new ArrayList<>();
//        userInfoList.forEach((f) ->{
//            OwnMemberVo vo = new OwnMemberVo();
//            BeanUtils.copyProperties(f,vo);
//            BeanUtils.copyProperties(f.getUser(),vo);
//            rspVolist.add(vo);
//        });
//        resultResponse.getData().setRows(rspVolist);
//        return resultResponse;
//
//    }


    @PostMapping("getOwnMember")
    @ApiOperation(value = "获取所有属于自己的用户", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getOwnMember(@RequestBody @Valid PageInfo model) throws Exception {
        Map<String,Object> allMemberparam = BizControllerUtil.modelToMap(model);
        allMemberparam.put("memberUserId",BaseContextHandler.getUserID());
        Query allMemberQuery = new Query(allMemberparam);
        long total = queryByCustomPage(allMemberQuery,userInfoBiz).getData().getTotal();//旗下所有用户数量

        //直推用户
        Map<String,Object> param = BizControllerUtil.modelToMap(model);
        param.put("parentId",BaseContextHandler.getUserID());
        Query query = new Query(param);
//        userInfoBiz.queryListByCustomPage(query);
        TableResultResponse resultResponse = queryByCustomPage(query,userInfoBiz);
        List<HUserInfo> userInfoList = resultResponse.getData().getRows();
        List<OwnMemberVo> rspVolist = new ArrayList<>();
        userInfoList.forEach((f) ->{
            OwnMemberVo vo = new OwnMemberVo();
            BeanUtils.copyProperties(f,vo);
            BeanUtils.copyProperties(f.getUser(),vo);
            rspVolist.add(vo);
        });
        resultResponse.getData().setRows(rspVolist);
        resultResponse.getData().setOtherProperty(total);
        return resultResponse;

    }
    /**
     * 处理提币申请
     *
     * @param withdrawModel
     * @return
     * @throws Exception
     */
    @UserOperationAuthority("WITHDRAW_COIN") //判断当前用户提币权限是否被禁用
    @PostMapping("withdraw")
    public Object withdrawals(@RequestBody WithdrawModel withdrawModel) throws Exception {

        FrontUser frontUser = ServiceUtil.selectUnionUserInfoById(BaseContextHandler.getUserID(),frontUserBiz);
        // 调用验证码公共方法校验验证码
        CommonBiz.commonVerifyMethod(frontUser, SendMsgType.WITHDRAW_REQUEST.value(), withdrawModel.getMobileCode(),
                withdrawModel.getEmailCode());
        ObjectRestResponse response = commonBiz.withdraw(withdrawModel,frontUser,FrontWithdrawType.NORMAL,false);
        if(!response.isRel()){
            return response;
        }
        response.data(null);
        CommonBiz.clearVerifyMethod(frontUser);
        return response;
    }

}
