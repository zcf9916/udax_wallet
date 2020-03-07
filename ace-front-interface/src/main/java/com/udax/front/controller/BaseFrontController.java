package com.udax.front.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.merchant.MchTradeDetail;
import com.github.wxiaoqi.security.common.entity.merchant.Merchant;
import com.github.wxiaoqi.security.common.enums.FrontUserStatus;
import com.github.wxiaoqi.security.common.enums.MchResponseCode;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.ValidType;
import com.github.wxiaoqi.security.common.enums.merchant.MchStatus;
import com.github.wxiaoqi.security.common.exception.MchException;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.msg.TableResultResponse;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.Query;
import com.github.wxiaoqi.security.common.util.WebUtil;
import com.udax.front.biz.FrontUserBiz;
import com.udax.front.biz.merchant.MerchantBiz;
import com.udax.front.service.ServiceUtil;
import com.udax.front.util.BizControllerUtil;
import com.udax.front.util.SignUtil;
import com.udax.front.vo.reqvo.merchant.OrderBaseModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * ${DESCRIPTION}
 *
 * @author zhoucf 前端专用的基础controller
 * @create 2017-06-15 8:48
 */
public class BaseFrontController<Biz extends BaseBiz,Entity> implements ApplicationEventPublisherAware {



    @Autowired
    protected ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
         this.publisher = applicationEventPublisher;
    }

    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    protected Biz baseBiz;

    /**
     * 分页查询
     * @param query
     * @return
     *
     * example
     *
    *         Map<String, Object> queryParams = new HashMap<String, Object>();
     *         //参数后面跟着:like 或者equal 默认equal(可以不填)
    *         queryParams.put("userId:like", user.getId()); // 用户Id
    *         queryParams.put("page", model.getPage()); // 当前页
    *         queryParams.put("limit", model.getLimit()); // 每页大小
    *         Query query = new Query(queryParams);
     */
    public TableResultResponse<Entity> pageQuery(Query query) {
        Class<Entity> clazz = (Class<Entity>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        Example example = new Example(clazz);
        //设置排序情况
        example.setOrderByClause(query.getOrderByInfo());
        if(query.entrySet().size()>0) {
            Example.Criteria criteria = example.createCriteria();
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                String[] key = entry.getKey().split(":");
                if(key.length == 0  || "like".equals(key[1])){
                    criteria.andLike(key[0], "%" + entry.getValue().toString() + "%");
                }else{
                    criteria.andEqualTo(key[0], entry.getValue().toString());
                }
            }
        }
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List<Entity> list = baseBiz.selectByExample(example);
        return new TableResultResponse<Entity>(result.getTotal(), list);
    }

    /**
     * 分页查询
     * @param query
     * @return
     *
     * example
     *
     *         Map<String, Object> queryParams = new HashMap<String, Object>();
     *         //参数后面跟着:like 或者equal 默认equal(可以不填)
     *         queryParams.put("userId:like", user.getId()); // 用户Id
     *         queryParams.put("page", model.getPage()); // 当前页
     *         queryParams.put("limit", model.getLimit()); // 每页大小
     *         Query query = new Query(queryParams);
     */
    public TableResultResponse pageQuery(Query query,Class cl,BaseBiz biz) {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List list = pageQueryByOption(query,cl,biz);
        return new TableResultResponse(result.getTotal(), list);
    }

    //分页查询并转换成对应vo
    public TableResultResponse pageQueryTransToVo(Query query,Class cl,Class voCl,BaseBiz biz) throws Exception {
        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List list = pageQueryByOption(query,cl,biz);
        List voList = BizControllerUtil.transferEntityToListVo(voCl,list);
        return new TableResultResponse(result.getTotal(), voList);
    }

    private List pageQueryByOption(Query query,Class cl,BaseBiz biz){
        Example example = new Example(cl);
        //设置排序情况
        example.setOrderByClause(query.getOrderByInfo());
        if(query.entrySet().size()>0) {
            Example.Criteria criteria = example.createCriteria();
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                String[] key = entry.getKey().split(":");
                if(entry == null || entry.getValue() == null || StringUtils.isBlank(entry.getValue().toString())){
                    continue;
                }
                if(key.length == 2  && "like".equals(key[1]) ){
                    criteria.andLike(key[0], "%" + entry.getValue().toString() + "%");
                }else if(key.length == 1  || "equal".equals(key[1])){
                    criteria.andEqualTo(key[0], entry.getValue().toString());
                } else if(key.length == 2  && "in".equals(key[1])){
                    criteria.andIn(key[0],(List)entry.getValue());
                } else{
                    if(key[1].equals("<=")){
                        criteria.andLessThanOrEqualTo(key[0], entry.getValue().toString());
                    }else if(key[1].equals(">=") ){
                        criteria.andGreaterThanOrEqualTo(key[0], entry.getValue().toString());
                    }
                }
            }
        }

        List list = biz.selectByExample(example);
        return list;
    }
    /*自定义sql分页查询列表*/
    public TableResultResponse queryByCustomPage(Query query,BaseBiz biz) {

        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List list =  biz.queryListByCustomPage(query);
        return new TableResultResponse(result.getTotal(), list);
       // return new TablePa
    }

    /*自定义sql分页查询列表*/
    public TableResultResponse queryByCustomPageTransToVo(Query query,Class cl,Class voCl,BaseBiz biz) throws Exception {

        Page<Object> result = PageHelper.startPage(query.getPage(), query.getLimit());
        List list =  biz.queryListByCustomPage(query);
        List voList = BizControllerUtil.transferEntityToListVo(voCl,list);
        return new TableResultResponse(result.getTotal(), voList);
        // return new TablePa
    }


    //后台登录,验证商户
    public Map<String,Object> validBgMerchant(FrontUserBiz frontUserBiz) throws  Exception{
        // 通过登录名查询出用户
        FrontUser user = ServiceUtil.selectUnionUserInfoById(BaseContextHandler.getUserID(),frontUserBiz);
        if (user == null || user.getUserInfo() == null) {
            throw new MchException(ResponseCode.USER_NOT_EXIST.name());
        }
//        //用户状态是否正常
//        if(user.getUserStatus().intValue() != FrontUserStatus.ACTIVE.value()){
//            throw new MchException(ResponseCode.UNACTIVE.name());
//        }
//        //用户是否通过实名认证
//        if(user.getUserInfo().getIsValid() != ValidType.AUTH.value()){
//            throw new MchException(ResponseCode.UNAUTH.name());
//        }
        //查询是否已经认证过了
        Merchant merchantParam = new Merchant();
        merchantParam.setUserId(Long.valueOf(BaseContextHandler.getUserID()));
        Merchant merchant = (Merchant) baseBiz.selectOne(merchantParam);
//        if(merchant == null || merchant.getMchStatus().intValue() <  MchStatus.ACTIVE.value().intValue()){
//            throw new MchException(MchResponseCode.MERCHANT_NOAUTH.name());
//        }
        Map<String,Object> map = InstanceUtil.newHashMap();
        map.put("user",user);
        map.put("merchant",merchant);
        return map;
    }




    //验证商户 以及签名
    public Merchant validMerchant(OrderBaseModel model, MerchantBiz biz,boolean validateIp) throws  Exception{
        Merchant merchantParam = new Merchant();
        merchantParam.setMchNo(Long.valueOf(model.getMchNo()));
        Merchant merchant = biz.selectOne(merchantParam);
        if(merchant == null || StringUtils.isBlank(merchant.getSecretKey()) || merchant.getUserId() == null
                || merchant.getChargeId() == null){
            throw new MchException(MchResponseCode.MERCHANT_NOAUTH.name());
        }
        //未认证
        if(merchant.getMchStatus().equals(MchStatus.NOAUTH.value())){
            throw new MchException(MchResponseCode.MERCHANT_NOAUTH.name());
        }
        //商户名是否为空
        if(StringUtils.isBlank(merchant.getMchName())){
            throw new MchException(MchResponseCode.MERCHANT_NAME_NULL.name());
        }
        //ip地址域名列表
        if(StringUtils.isBlank(merchant.getBindAddress())){
            throw new MchException(MchResponseCode.MERCHANT_IPLIST_ERROR.name());
        }
        //充值回调地址
        if(StringUtils.isBlank(merchant.getRechargeCallback())){
            throw new MchException(MchResponseCode.MERCHANT_RECHARGE_ERROR.name());
        }
        //提现回调地址
        if(StringUtils.isBlank(merchant.getWithdrawCallback())){
            throw new MchException(MchResponseCode.MERCHANT_WITHDRAW_ERROR.name());
        }
        if(validateIp && !isWhiteReq(merchant.getBindAddress())){
            throw new MchException(MchResponseCode.MERCHANT_INVALID_IP.name());
        }

        //验证签名
        String  secrectSign = SignUtil.sign(model,merchant.getSecretKey());
        if(!secrectSign.equals(model.getSign())){
            throw new MchException(MchResponseCode.MERCHANT_SIGN_ERROR.name());
        }
        return merchant;
    }


    /*
     * 判断是否是白名单
     */
    private boolean isWhiteReq(String whiteIpList) {
        if (StringUtils.isBlank(whiteIpList)) {
            return false;
        }
        String ip = WebUtil.getHost(request);
        String[] ipArr = whiteIpList.split(";");
        for(String ips : ipArr){
            if(ips.contains(ip)){
                return true;
            }
        }
        return false;
    }



//    //验证商户 不严重签名
//    public ObjectRestResponse validMchWithOutSign(Long mchNo, MerchantBiz biz) throws  Exception{
//        Merchant merchantParam = new Merchant();
//        merchantParam.setMchNo(mchNo);
//        Merchant merchant = biz.selectOne(merchantParam);
//        if(merchant == null || StringUtils.isBlank(merchant.getSecretKey()) || merchant.getUserId() == null
//                || merchant.getChargeId() == null){
//            return new ObjectRestResponse().status(ResponseCode.MERCHANT_NOAUTH);
//        }
//        //重复认证
//        if(merchant.getMchStatus().equals(MchStatus.NOAUTH.value())){
//            return new ObjectRestResponse().status(ResponseCode.MERCHANT_NOAUTH);
//        }
//        return new ObjectRestResponse().data(merchant);
//    }


}
