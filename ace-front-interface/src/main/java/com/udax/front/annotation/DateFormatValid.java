package com.udax.front.annotation;

import com.github.wxiaoqi.security.common.util.DateUtil;
import com.udax.front.biz.CacheBiz;
import com.udax.front.service.ServiceUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class DateFormatValid implements ConstraintValidator<DateFormat, String> {

   private static DateTimeFormatter ftf = DateTimeFormatter.ofPattern(DateUtil.DATE_PATTERN.YYYY_MM_DD);


   public void initialize(DcCode constraint) {
   }

   //判断当前代币是否系统的基础代币  并且是否可用
   public boolean isValid(String date, ConstraintValidatorContext context) {
      if(StringUtils.isBlank(date)){
         return true;
      }
      try{

         LocalDate.parse(date,ftf);
      }catch (Exception e){
         return false;
      }
      return true;
   }

}
