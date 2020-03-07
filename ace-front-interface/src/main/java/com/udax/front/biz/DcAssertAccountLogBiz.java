package com.udax.front.biz;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.configuration.KeyConfiguration;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.admin.UserOfferInfo;
import com.github.wxiaoqi.security.common.entity.front.*;
import com.github.wxiaoqi.security.common.enums.AccountLogType;
import com.github.wxiaoqi.security.common.enums.AccountSignType;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.TransferOrderStatus;
import com.github.wxiaoqi.security.common.mapper.admin.UserOfferInfoMapper;
import com.github.wxiaoqi.security.common.mapper.front.DcAssetAccountLogMapper;
import com.github.wxiaoqi.security.common.mapper.front.DcAssetAccountMapper;
import com.github.wxiaoqi.security.common.mapper.front.TransferListMapper;
import com.github.wxiaoqi.security.common.mapper.front.TransferOrderMapper;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.LocalDateUtil;
import com.github.wxiaoqi.security.common.util.Query;
import com.udax.front.service.ServiceUtil;
import com.udax.front.vo.reqvo.GetAssertLogModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.github.wxiaoqi.security.common.enums.AccountSignType.ACCOUNT_PAY_AVAILABLE;
import static com.github.wxiaoqi.security.common.enums.AccountSignType.MERCHANT_REFUND_SETTLE;


@Service
@Slf4j
public class DcAssertAccountLogBiz extends BaseBiz<DcAssetAccountLogMapper, DcAssetAccountLog> {



	public List<Map<String,Object>> getAssertCount(GetAssertLogModel model)  {



        Map<String,Object> param = InstanceUtil.newHashMap();
        param.put("userId", BaseContextHandler.getUserID());
        param.put("symbol", model.getSymbol());
        param.put("beginDate",model.getBeginDate());
        param.put("endDate",model.getEndDate());
        if(StringUtils.isBlank(model.getBeginDate())  && StringUtils.isBlank(model.getEndDate())){
            param.put("beginDate",LocalDateUtil.date2LocalDate(LocalDateUtil.getStartDayOfMonth(LocalDate.now())).toString());
            param.put("endDate",LocalDateUtil.getNextMonthFirstDay(LocalDate.now().toString()).toString());
        }
        param.put("types",AccountLogType.getBillTypeList(model.getBillType()));
        List<Map<String,Object>> list =  mapper.getAssertCount(param);
		return list;
	}


	//根据条件获取最新流水
	public DcAssetAccountLog getLatestLog(DcAssetAccountLog log){
        return  mapper.getLatestLog(log);
    }


}
