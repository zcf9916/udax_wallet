package com.udax.front.biz;

import java.util.Date;

import com.github.wxiaoqi.security.common.enums.MchResponseCode;
import com.github.wxiaoqi.security.common.exception.MchException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.wxiaoqi.security.common.biz.BaseBiz;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.front.FrontUserInfo;
import com.github.wxiaoqi.security.common.entity.merchant.Merchant;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.UploadType;
import com.github.wxiaoqi.security.common.enums.ValidType;
import com.github.wxiaoqi.security.common.enums.merchant.MchStatus;
import com.github.wxiaoqi.security.common.mapper.front.FrontUserInfoMapper;
import com.github.wxiaoqi.security.common.mapper.merchant.MerchantMapper;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.udax.front.biz.merchant.MerchantBiz;

import lombok.extern.slf4j.Slf4j;

//上传相关业务逻辑
@Service
@Slf4j
public class UploadBiz extends BaseBiz<FrontUserInfoMapper,FrontUserInfo> {


	@Autowired
	private MerchantMapper merchantMapper;

	@Autowired
	private MerchantBiz merchantBiz;



	//验证身份证上传相关逻辑
    public ObjectRestResponse idCardUpload(Long userId){
		FrontUserInfo userInfoParam = new FrontUserInfo();
		userInfoParam.setUserId(userId);
    	FrontUserInfo userInfo = mapper.selectOne(userInfoParam);
    	if(!userInfo.getIsValid().equals(ValidType.NO_AUTH.value())){
    		return  new ObjectRestResponse().status(ResponseCode.USER_AUTH_DUPLICATE);
		}
		return new ObjectRestResponse();
	}

	//验证商户上传相关逻辑
	public ObjectRestResponse merchantUploadValid(Long userId,UploadType uploadType){
    	//查询是否第一次上次,商户信息还未生成
		Merchant merchantParam = new Merchant();
		merchantParam.setUserId(userId);
		Merchant merchant = merchantMapper.selectOne(merchantParam);
		//查询是否已经认证过了
		if(merchant == null){
//			//必须先上传正面
//			if(uploadType == UploadType.MERCHANT_INFO_FM ){
//				return  new ObjectRestResponse().status(ResponseCode.MERCHANT_UPLOAD_ZM);
//			}
		} else {
			//状态不是初始状态
			if(merchant.getMchStatus() != MchStatus.NOAUTH.value()){
				throw new MchException(MchResponseCode.MERCHANT_AUTH_DUPLICATE.name());
			}
		}

		return new ObjectRestResponse();



	}

//	/**
//	 *
//	 * @param uploadType
//	 * @param filePath
//	 * @return
//	 */
//	//更新商户上传的相关信息
//	public String merchantUploadUpdate(UploadType uploadType,String filePath){
//		//查询是否第一次上次,商户信息还未生成
//		Merchant merchantParam = new Merchant();
//		merchantParam.setUserId(BaseContextHandler.getUserID());
//		Merchant merchant = merchantMapper.selectOne(merchantParam);
//		//查询是否已经认证过了
//		if(merchant == null){
//			//添加商户信息
//			merchantParam.setCreateTime(new Date());
//			merchantParam.setMchLicenseZm(filePath);//商户营业执照正面
//			merchantParam.setSalt(RandomStringUtils.randomAlphabetic(20));//盐值
//			merchantBiz.authMerchant(merchantParam);
//			return null;
//		}
//		String oldFilePath = null;
//		if(uploadType == UploadType.MERCHANT_INFO_FM){
//			merchantParam.setMchLicenseFm(filePath);
//			oldFilePath = merchant.getMchLicenseFm();
//		}
//		if(uploadType == UploadType.MERCHANT_INFO_ZM){
//			merchantParam.setMchLicenseZm(filePath);
//			oldFilePath = merchant.getMchLicenseZm();
//		}
//		merchantBiz.updateSelectiveById(merchantParam);
//		//返回要删除的图片
//		return oldFilePath;
//	}


	//头像上传相关逻辑
	public String portraitUpload(String portraitPath){

		FrontUserInfo userInfoParam = new FrontUserInfo();
		userInfoParam.setUserId(BaseContextHandler.getUserID());
		FrontUserInfo userInfo = mapper.selectOne(userInfoParam);
		//之前的头像地址
		String oldPortrait = userInfo.getPortrait();
		//更新新的头像地址
		FrontUserInfo updateParam = new FrontUserInfo();
		updateParam.setId(userInfo.getId());
		updateParam.setPortrait(portraitPath);
		mapper.updateByPrimaryKeySelective(updateParam);
		return oldPortrait;
	}
}
