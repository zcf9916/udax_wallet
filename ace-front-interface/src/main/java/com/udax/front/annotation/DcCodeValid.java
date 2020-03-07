package com.udax.front.annotation;

import com.github.wxiaoqi.security.common.util.StringUtil;
import com.udax.front.biz.CacheBiz;
import com.udax.front.service.ServiceUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class DcCodeValid implements ConstraintValidator<DcCode, String> {

   @Autowired
   private CacheBiz cacheBiz;


   private boolean required = true;

   @Override
   public void initialize(DcCode constraint) {
      required = constraint.required();
   }

   //判断当前代币是否系统的基础代币  并且是否可用
   public boolean isValid(String dcCode, ConstraintValidatorContext context) {
      if(!required && StringUtils.isBlank(dcCode)){
         return true;
      }
     return ServiceUtil.validDcCode(dcCode,cacheBiz);
   }
}
