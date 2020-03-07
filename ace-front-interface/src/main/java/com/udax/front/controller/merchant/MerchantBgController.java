package com.udax.front.controller.merchant;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.common.configuration.KeyConfiguration;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.BasicSymbol;
import com.github.wxiaoqi.security.common.entity.admin.CfgDcRechargeWithdraw;
import com.github.wxiaoqi.security.common.entity.front.*;
import com.github.wxiaoqi.security.common.entity.merchant.MchNotify;
import com.github.wxiaoqi.security.common.entity.merchant.MchRefundDetail;
import com.github.wxiaoqi.security.common.entity.merchant.MchTradeDetail;
import com.github.wxiaoqi.security.common.entity.merchant.Merchant;
import com.github.wxiaoqi.security.common.enums.*;
import com.github.wxiaoqi.security.common.enums.merchant.MchNotifyStatus;
import com.github.wxiaoqi.security.common.enums.merchant.MchStatus;
import com.github.wxiaoqi.security.common.exception.MchException;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.task.CallbackMsg;
import com.github.wxiaoqi.security.common.util.*;
import com.udax.front.biz.*;
import com.udax.front.biz.merchant.MchNotifyBiz;
import com.udax.front.biz.merchant.MchRefundDetailBiz;
import com.udax.front.biz.merchant.MchTradeDetailBiz;
import com.udax.front.biz.merchant.MerchantBiz;
import com.udax.front.bizmodel.CommonBiz;
import com.udax.front.controller.BaseFrontController;
import com.udax.front.service.ServiceUtil;
import com.udax.front.util.BizControllerUtil;
import com.udax.front.util.CacheBizUtil;
import com.udax.front.util.SignUtil;
import com.udax.front.vo.reqvo.merchant.*;
import com.udax.front.vo.rspvo.*;
import com.udax.front.vo.rspvo.merchant.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.wxiaoqi.security.common.constant.Constants.MCH_CALLBACK_TASK;
import static com.github.wxiaoqi.security.common.enums.AccountLogType.*;

/**
 * @author zhoucf
 * @create 2018／4/30 商户后台相关接口
 */
@RestController
@RequestMapping("/wallet/mchbg/")
public class MerchantBgController extends BaseFrontController<MerchantBiz,Merchant> {


    @Autowired
    private MchTradeDetailBiz mchTradeDetailBiz;


    @Autowired
    private MchRefundDetailBiz mchRefundDetailBiz;

    @Autowired
    private FrontUserBiz frontUserBiz;

    @Autowired
    private FrontRechargeBiz frontRechargeBiz;

    @Autowired
    private FrontWithdrawBiz frontWithdrawBiz;

    @Autowired
    private TransferOrderBiz transferOrderBiz;


    @Autowired
    private CacheBiz cacheBiz;

    @Autowired
    private DcAssertAccountBiz dcAssertAccountBiz;


    @Autowired
    private DcAssertAccountLogBiz logBiz;

    @Autowired
    private Environment env;



    //商户后台基础信息接口
    @PostMapping("/assertQuery")
    public Object assertQuery() throws Exception{

//        Map<String,Object> data = validBgMerchant(frontUserBiz);
//        Merchant merchant = (Merchant) data.get("merchant");
//        if(merchant == null || merchant.getMchStatus().intValue() <  MchStatus.ACTIVE.value().intValue()){
//            return new ObjectRestResponse().status(MchResponseCode.MERCHANT_NOAUTH);
//        }
        String url = env.getProperty("udax.quotation");//udax行情接口
        String returnJson = HttpUtils.postJson(url,"");
        UdaxQuotationBean jsonBean = JSON.parseObject(returnJson,UdaxQuotationBean.class);
        Map<String,QuotationBean> newQuotationBeanMap  = InstanceUtil.newHashMap();
        if(jsonBean != null){
            Map<String,JSONObject> quotationBeanMap = (Map<String,JSONObject>) jsonBean.getData().get("quotation");
            if(quotationBeanMap != null){
                quotationBeanMap.forEach((k,v) ->{
                    if(!k.contains("/"+Constants.QUOTATION_DCCODE)){
                        return;
                    }
                    QuotationBean bean = new QuotationBean();
                    bean.setLastPrice(v.getBigDecimal("lastPrice"));
                    bean.setSymbol(v.getString("symbol"));
                    BigDecimal openPrice = v.getBigDecimal("openPrice");
                    if(openPrice == null || bean.getLastPrice() == null
                            || bean.getLastPrice().compareTo(BigDecimal.ZERO) <= 0){
                        bean.setRose(BigDecimal.ZERO.setScale(2,BigDecimal.ROUND_DOWN));
                    } else {
                        bean.setRose(bean.getLastPrice().subtract(openPrice).divide(openPrice,2,BigDecimal.ROUND_DOWN));
                    }
                    newQuotationBeanMap.put(k,bean);
                });
            }
        }

        ObjectRestResponse result = new ObjectRestResponse().rel(true);
        DcAssetAccount assetAccount = new DcAssetAccount();
        assetAccount.setUserId(BaseContextHandler.getUserID());
        //查询资产
        List<DcAssetAccount> accountList = dcAssertAccountBiz.selectList(assetAccount);
        Map<String,String> symbolMap = accountList.stream().collect(Collectors.toMap(DcAssetAccount::getSymbol,DcAssetAccount::getSymbol));
        List<BasicSymbol> basicList = CacheBizUtil.getBasicSymbolExch(cacheBiz,BaseContextHandler.getExId());
        Map<String,String> basicSymbolMap = basicList.stream().collect(Collectors.toMap(BasicSymbol::getSymbol,BasicSymbol::getSymbol));
        basicSymbolMap.forEach((k,v) ->{
            if(symbolMap.get(k) != null){
                return;
            }
            DcAssetAccount account  = new DcAssetAccount();
            account.setSymbol(k);
            account.setTotalAmount(BigDecimal.ZERO);
            account.setAvailableAmount(BigDecimal.ZERO);
            account.setFreezeAmount(BigDecimal.ZERO);
            account.setWaitConfirmAmount(BigDecimal.ZERO);
            accountList.add(account);
            symbolMap.put(k,k);
        });
        //用户资产数据转vo
        List<AccountListModel> list = BizControllerUtil.transferEntityToListVo(AccountListModel.class,accountList);

        //计算可用总资产
        BigDecimal totalAmount = BigDecimal.ZERO;
        if(StringUtil.listIsNotBlank(accountList)){
            for(DcAssetAccount account : accountList){
                QuotationBean bean = newQuotationBeanMap.get(account.getSymbol()+"/" + Constants.QUOTATION_DCCODE);
                if(bean != null && bean.getLastPrice() != null){
                    totalAmount = totalAmount.add(bean.getLastPrice().multiply(account.getTotalAmount())).setScale(8,RoundingMode.FLOOR);
                }
            }
        }


        Map<String,Object> map = InstanceUtil.newHashMap();
        map.put("dcCode",Constants.QUOTATION_DCCODE);
        map.put("totalAmount",totalAmount);
        map.put("assertList",list);
        result.setData(map);
        return result;
    }



    //商户后台基础信息接口
    @PostMapping("/baseInfo")
    public Object baseInfo() throws Exception{

        Map<String,Object> data = validBgMerchant(frontUserBiz);
        FrontUser user = (FrontUser) data.get("user");
        Merchant merchant = (Merchant) data.get("merchant");
        if(merchant == null || merchant.getMchStatus().intValue() <  MchStatus.ACTIVE.value().intValue()){
            return new ObjectRestResponse().status(MchResponseCode.MERCHANT_NOAUTH);
        }





        Map<String,Object> map = InstanceUtil.newHashMap();
        MchBgInfoRspVo rspVo = new MchBgInfoRspVo();
        BeanUtils.copyProperties(merchant,rspVo);
        BeanUtils.copyProperties(user,rspVo);
        BeanUtils.copyProperties(user.getUserInfo(),rspVo);
        rspVo.setExInfo(CacheBizUtil.getExchInfo(BaseContextHandler.getExId(),cacheBiz));
        map.put("merchantInfo",rspVo);

        ObjectRestResponse result = new ObjectRestResponse().rel(true);
        result.setData(map);
        return result;
    }



    //商户补全数据接口
    @PostMapping("/updateInfo")
    public Object updateInfo(@RequestBody @Valid MchDataModel model) throws Exception {

        Map<String,Object> data = validBgMerchant(frontUserBiz);
        FrontUser user = (FrontUser) data.get("user");
        Merchant merchant = (Merchant) data.get("merchant");
        if(merchant == null || merchant.getMchStatus().intValue() <  MchStatus.ACTIVE.value().intValue()){
            return new ObjectRestResponse().status(MchResponseCode.MERCHANT_NOAUTH);
        }

        //验证码
        CommonBiz.commonVerifyMethod(user, SendMsgType.MERCHANT_UPDATE_INFO.value(),
                model.getMobileCode(), model.getEmailCode());
        Merchant updateParam = new Merchant();
        updateParam.setId(merchant.getId());
        updateParam.setMchName(model.getMchName());
        updateParam.setBindAddress(model.getBindAddress());
        updateParam.setRechargeCallback(model.getRechargeCallback());
        updateParam.setWithdrawCallback(model.getWithdrawCallback());
        updateParam.setSecretKey(model.getSecretKey());
        baseBiz.updateSelectiveById(updateParam);//商户名
        CommonBiz.clearVerifyMethod(user);


        return new ObjectRestResponse();
    }



    //商户补全数据接口
    @PostMapping("/rechargeQuery")
    public Object rechargeQuery(@RequestBody @Valid MchBgQueryRechargeModel model) throws Exception {
        // 通过登录名查询出用户
        Map<String,Object> data = validBgMerchant(frontUserBiz);
        FrontUser user = (FrontUser) data.get("user");

        Map<String,Object> map = BizControllerUtil.modelToMap(model);
        map.put("userId", user.getId());
        map.put("status",CommonStatus.SUCC.value());//显示充值成功的记录
        map.remove("beginDate");
        map.remove("endDate");
        map.put("createTime:>=",model.getBeginDate());
        if(StringUtils.isNotBlank(model.getEndDate())){
            org.joda.time.LocalDateTime localDateTime  = org.joda.time.LocalDateTime.parse(model.getEndDate());
            String endDate =  localDateTime.plusDays(1).toString(DateUtil.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
            map.put("createTime:<=",endDate);
        }
        map.put("orderByInfo","create_time desc");



        Query query = new Query(map);
        TableResultResponse resultResponse = pageQueryTransToVo(query,FrontRecharge.class,MchBgRechargeQueryRspVo.class,frontRechargeBiz);
        return resultResponse;
    }


    @PostMapping("/withdrawQuery")
    public Object withdrawQuery(@RequestBody @Valid MchBgQueryWithdrawModel model) throws Exception {
        // 通过登录名查询出用户
        Map<String,Object> data = validBgMerchant(frontUserBiz);
        FrontUser user = (FrontUser) data.get("user");

        Map<String,Object> map = BizControllerUtil.modelToMap(model);
        map.put("userId", user.getId());
        map.put("orderByInfo","create_time desc");
        map.remove("beginDate");
        map.remove("endDate");
        map.put("createTime:>=",model.getBeginDate());
        if(StringUtils.isNotBlank(model.getEndDate())){
            org.joda.time.LocalDateTime localDateTime  = org.joda.time.LocalDateTime.parse(model.getEndDate());
            String endDate =  localDateTime.plusDays(1).toString(DateUtil.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
            map.put("createTime:<=",endDate);
        }
        Query query = new Query(map);
        TableResultResponse resultResponse = pageQueryTransToVo(query,FrontWithdraw.class,MchBgWithdrawQueryRspVo.class,frontWithdrawBiz);
        return resultResponse;
    }


    @PostMapping("/transferQuery")
    public Object transferQuery(@RequestBody @Valid MchBgTransferQueryModel model) throws Exception {
        // 通过登录名查询出用户
        Map<String,Object> data = validBgMerchant(frontUserBiz);
        FrontUser user = (FrontUser) data.get("user");

        Map<String,Object> map = BizControllerUtil.modelToMap(model);
        map.put("userId", user.getId());
        map.put("status",TransferOrderStatus.PAYED.value());//已轉賬成功的
        map.remove("beginDate");
        map.remove("endDate");
        map.put("createTime:>=",model.getBeginDate());
        if(StringUtils.isNotBlank(model.getEndDate())){
            org.joda.time.LocalDateTime localDateTime  = org.joda.time.LocalDateTime.parse(model.getEndDate());
            String endDate =  localDateTime.plusDays(1).toString(DateUtil.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
            map.put("createTime:<=",endDate);
        }
        map.put("orderByInfo","update_time desc");
        Query query = new Query(map);
        TableResultResponse resultResponse = pageQueryTransToVo(query,TransferOrder.class,MchBgTransferQueryRspVo.class,transferOrderBiz);
        return resultResponse;
    }

    //流水查询
    @PostMapping("/assertlog")
    public Object assertlog(@RequestBody @Valid MchAssertLogModel model) {
        if( model.getBillType() != null && !BillType.isType(model.getBillType())){
            return new ObjectRestResponse().status(ResponseCode.BILLTYPE_ERROR);
        }

        //获取支出和收入
        List<Map<String,Object>> countList = logBiz.getAssertCount(model);
        BigDecimal incomeAmount = BigDecimal.ZERO;
        BigDecimal payAmount = BigDecimal.ZERO;
        if(StringUtil.listIsNotBlank(countList)){
            for(Map<String,Object> map:countList){
                if(map != null){
                    Integer direction = (Integer) map.get("direction");
                    if(direction.equals(DirectionType.PAY.value())){
                        payAmount = (BigDecimal) map.get("amount");
                    }
                    if(direction.equals(DirectionType.INCOME.value())){
                        incomeAmount = (BigDecimal) map.get("amount");
                    }
                }
            };
        }




        MchAssertLogRspVo rspVo = new MchAssertLogRspVo();
        rspVo.setIncomeAmount(incomeAmount);
        rspVo.setPayAmount(payAmount);

        //账单类型国际化
        List<MchAssertLogRspVo.BillVo> billList = new ArrayList<>();
        for(BillType type : BillType.values()) {
            MchAssertLogRspVo.BillVo billVo = rspVo.newBillVo();
            billVo.setName(BillType.getName(type));
            billVo.setBillType(type.value());
            billList.add(billVo);
        }
        rspVo.setTypeList(billList);



        //获取分页流水数据
        Map<String,Object> param = InstanceUtil.newHashMap();
        param.put("direction", model.getDirection());
        param.put("userId", BaseContextHandler.getUserID());
        param.put("page", model.getPage());
        param.put("limit", model.getLimit());
        param.put("type:in",AccountLogType.getBillTypeList(model.getBillType()));
        param.put("symbol", model.getSymbol());
        param.put("createTime:>=",model.getBeginDate());
        if(StringUtils.isNotBlank(model.getEndDate())){
            org.joda.time.LocalDateTime localDateTime  = org.joda.time.LocalDateTime.parse(model.getEndDate());
            String endDate =  localDateTime.plusDays(1).toString(DateUtil.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
            param.put("createTime:<=",endDate);
        }

        Query query = new Query(param);
        query.setOrderByInfo("create_time desc");

        TableResultResponse pageResult = pageQuery(query, DcAssetAccountLog.class,logBiz);
        if(pageResult !=null && pageResult.getData() != null && StringUtil.listIsNotBlank(pageResult.getData().getRows())){
            List<DcAssetAccountLog> list = pageResult.getData().getRows();
            List<MchAssertLogRspVo.DataVo> rspVoList = new ArrayList<>(list.size());
            list.forEach((f) ->{
                MchAssertLogRspVo.DataVo dataVo = rspVo.newDataVo();
                String directionSymbol = DirectionType.getDirectionSymbol(f.getDirection());
                dataVo.setId(f.getId());//流水Id
                dataVo.setTransNo(f.getTransNo());//平台流水號
                dataVo.setAmount(directionSymbol + f.getAmount().stripTrailingZeros().toPlainString() + " " + f.getSymbol());
                dataVo.setBillType(AccountLogType.getBillType(f.getType()).value());
                dataVo.setCreateTime(f.getCreateTime());
                dataVo.setBalance(f.getAfterTotal().stripTrailingZeros().toPlainString() + f.getSymbol());
                //流水類型
                Integer value = f.getType();
                if(value.intValue() == PAY_MERCHANT.value().intValue()
                        ||value.intValue() == MERCHANT_SETTLE.value().intValue()){
                    getMchPayOrder(f,dataVo);
                }
                if(value.intValue() == MERCHANT_REFUND.value().intValue()
                        ||value.intValue() == RECEIVE_REFUND.value().intValue()){
                    getMchRefundOrder(f,dataVo);
                }
                if(StringUtils.isNotBlank(f.getRemark())){
                    dataVo.setRemark(AccountLogType.getName(f.getRemark()));
                }
                rspVoList.add(dataVo);
            });
            //pageResult =  new TableResultResponse(pageResult.getData().getTotal(),rspVoList);
            rspVo.setDataVoList(rspVoList);
            rspVo.setTotal(pageResult.getData().getTotal());
        }
        return new ObjectRestResponse<>().data(rspVo);
    }



    private void getMchPayOrder(DcAssetAccountLog log,MchAssertLogRspVo.DataVo dataVo){
        MchTradeDetail mchTradeDetail = new MchTradeDetail();
        mchTradeDetail.setWalletOrderNo(log.getTransNo());
        mchTradeDetail = mchTradeDetailBiz.selectOne(mchTradeDetail);
        dataVo.setMchOrderNo(mchTradeDetail.getMchOrderNo());
        FrontUser user =  CacheBizUtil.getFrontUserCache(mchTradeDetail.getUserId(),frontUserBiz);
        if(user != null && user.getUserInfo() != null && StringUtils.isNotBlank(user.getUserInfo().getRealName())){
            dataVo.setUserInfo("*" + user.getUserInfo().getRealName());
        }
       // dataVo.setUserInfo(user.);
//        dataVo.setUserInfo(mchTradeDetail);
//        CacheBizUtil
    }
    private void getMchRefundOrder(DcAssetAccountLog log,MchAssertLogRspVo.DataVo dataVo){
       //
        MchRefundDetail mchRefundDetail = new MchRefundDetail();
        mchRefundDetail.setWalletOrderNo(log.getTransNo());
        mchRefundDetail = mchRefundDetailBiz.selectOne(mchRefundDetail);
        dataVo.setMchOrderNo(mchRefundDetail.getMchOrderNo());
        FrontUser user =  CacheBizUtil.getFrontUserCache(mchRefundDetail.getUserId(),frontUserBiz);
        if(user != null && user.getUserInfo() != null && StringUtils.isNotBlank(user.getUserInfo().getRealName())){
            dataVo.setUserInfo("*" + user.getUserInfo().getRealName());
        }
    }
}

