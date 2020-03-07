package com.udax.front.biz.ifr;

import com.alibaba.fastjson.JSON;
import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.configuration.KeyConfiguration;
import com.github.wxiaoqi.security.common.entity.admin.UserOfferInfo;
import com.github.wxiaoqi.security.common.entity.front.*;
import com.github.wxiaoqi.security.common.entity.ifr.IfrPlan;
import com.github.wxiaoqi.security.common.enums.AccountLogType;
import com.github.wxiaoqi.security.common.enums.AccountSignType;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.TransferOrderStatus;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.mapper.admin.UserOfferInfoMapper;
import com.github.wxiaoqi.security.common.mapper.front.DcAssetAccountLogMapper;
import com.github.wxiaoqi.security.common.mapper.front.DcAssetAccountMapper;
import com.github.wxiaoqi.security.common.mapper.front.TransferListMapper;
import com.github.wxiaoqi.security.common.mapper.front.TransferOrderMapper;
import com.github.wxiaoqi.security.common.mapper.ifr.IfrPlanMapper;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.HttpUtils;
import com.github.wxiaoqi.security.common.vo.AccountAssertLogVo;
import com.github.wxiaoqi.security.common.vo.UdaxLastPricesBean;
import com.udax.front.service.ServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

import static com.github.wxiaoqi.security.common.enums.AccountSignType.ACCOUNT_PAY_AVAILABLE;
import static com.github.wxiaoqi.security.common.enums.AccountSignType.MERCHANT_REFUND_SETTLE;


@Service
@Slf4j
public class IfrPlanBiz extends BaseBiz<IfrPlanMapper, IfrPlan> {


}
