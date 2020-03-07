package com.github.wxiaoqi.security.admin.biz.base;

import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.constant.AdminCommonConstant;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.User;
import com.github.wxiaoqi.security.common.entity.admin.UserTransactionModel;
import com.github.wxiaoqi.security.common.entity.front.FrontTransferDetail;
import com.github.wxiaoqi.security.common.entity.front.FrontUserInfo;
import com.github.wxiaoqi.security.common.enums.DateQueryType;
import com.github.wxiaoqi.security.common.enums.ValidType;
import com.github.wxiaoqi.security.common.mapper.admin.UserMapper;
import com.github.wxiaoqi.security.common.mapper.admin.UserTransactionModelMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontTransferDetailMapper;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserInfoMapper;
import com.github.wxiaoqi.security.common.mapper.merchant.MerchantMapper;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.model.DateVo;
import com.github.wxiaoqi.security.common.util.model.HomeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页服务层
 */
@Service
public class HomeBiz {

    @Autowired
    private FrontUserInfoMapper frontUserInfoMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserTransactionModelMapper userTransactionModelMapper;

    @Autowired
    private FrontTransferDetailMapper frontTransferDetailMapper;


    public List<HomeVo> selectFrontUserPool(Map<String, Object> params) {
        //当前登录人id
        Long aLong = BaseContextHandler.getUserID();
        User user = userMapper.selectByPrimaryKey(aLong);
        //新注册人数
        List<HomeVo> list = new ArrayList<>();
        DateVo vo = DateQueryType.getDate(Integer.parseInt(params.get("type").toString()));
        Example example = new Example(FrontUserInfo.class);
        Example.Criteria c = example.createCriteria();
        c.andBetween("createTime", vo.getBeginDate(), vo.getEndDate());
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            c.andEqualTo("exchangeId",BaseContextHandler.getExId());
        }
        List<FrontUserInfo> frontUserInfos = frontUserInfoMapper.selectByExample(example);
        HomeVo homeVo = new HomeVo(Resources.getMessage("home_new_user"), new BigDecimal(frontUserInfos.size()));
        list.add(homeVo);

        //实名认证人数
        c.andEqualTo("isValid", ValidType.AUTH.value());
        List<FrontUserInfo> infos = frontUserInfoMapper.selectByExample(example);
        HomeVo homeVo2 = new HomeVo(Resources.getMessage("home_real_name"), new BigDecimal(infos.size()));
        list.add(homeVo2);


        //商家注册人数
        HashMap<String, Object> param = new HashMap<>();
        param.put("beginDate", vo.getBeginDate());
        param.put("endDate", vo.getEndDate());
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            param.put("exchangeId", BaseContextHandler.getExId());
        }
        Integer integer = merchantMapper.selectMerchantCount(param);
        HomeVo homeVo3 = new HomeVo(Resources.getMessage("home_merchant_name"), new BigDecimal(integer));
        list.add(homeVo3);


        //总用户人数
        FrontUserInfo info = new FrontUserInfo();
        if (!user.getTopParentId().equals(AdminCommonConstant.ROOT)) {
            info.setExchangeId(user.getTopParentId());
        }
        List<FrontUserInfo> infoList = frontUserInfoMapper.select(info);
        HomeVo homeVo4 = new HomeVo(Resources.getMessage("home_user_total"), new BigDecimal(infoList.size()));
        list.add(homeVo4);
        return list;
    }

    /**
     * 用户之间转换
     *
     * @param params
     * @return
     */
    public List<UserTransactionModel> selectTransferOrder(Map<String, Object> params) {
        beginDateOrEndDate(params);
        return  userTransactionModelMapper.selectTotalTransfer(params);
    }

    /**
     * 商家交易
     *
     * @param params
     * @return
     */
    public List<UserTransactionModel> selectTotalMchTrade(Map<String, Object> params) {
        beginDateOrEndDate(params);
        return  userTransactionModelMapper.selectTotalMchTrade(params);
    }

    /**
     * 总资产
     */

    public List<HomeVo> selectTotalAccount() {
        List<HomeVo> accounts= null;
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            accounts=  userTransactionModelMapper.selectTotalAccount(BaseContextHandler.getExId());
        }else {
            accounts=  userTransactionModelMapper.selectTotalAccount(null);
        }
       if (StringUtil.listIsBlank(accounts)) {
           accounts.add(new HomeVo("BTC", new BigDecimal(BigInteger.ZERO)));
       }
        return accounts;
    }

    /**
     * 提现 柱状图
     *
     * @param params
     * @return
     */
    public List<UserTransactionModel> selectFrontWithdraw(Map<String, Object> params) {
        beginDateOrEndDate(params);
        return userTransactionModelMapper.selectUserWithdraw(params);
    }

    /**
     * 充值 饼状图
     */
    public List<HomeVo> selectFrontRecharge(Map<String, Object> params) {
        beginDateOrEndDate(params);
        List<HomeVo> voList = userTransactionModelMapper.selectUserRecharge(params);
        if (StringUtil.listIsBlank(voList)){
            voList.add(new HomeVo("BTC", new BigDecimal(BigInteger.ZERO)));
        }
        return voList;
    }

    /**
     * 币币转换
     * @param params
     * @return
     */
    public List<FrontTransferDetail> selectgetTransferDetail(Map<String, Object> params) {
        beginDateOrEndDate(params);
        return   frontTransferDetailMapper.selectTotalTransferDetail(params);
    }


    private void beginDateOrEndDate(Map<String, Object> params) {
        params.put("limit",10);
        DateVo vo = DateQueryType.getDate(Integer.parseInt(params.get("type").toString()));
        params.put("beginDate", vo.getBeginDate());
        params.put("endDate", vo.getEndDate());
        if (!BaseContextHandler.getExId().equals(AdminCommonConstant.ROOT)) {
            params.put("exchangeId", BaseContextHandler.getExId());
        }
    }


}
