package com.udax.front.annotation;

import com.github.wxiaoqi.security.common.util.DateUtil;
import com.udax.front.biz.CacheBiz;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeFormatValid implements ConstraintValidator<DateTimeFormat, String> {

   private static DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern(DateUtil.DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);

   private static DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(DateUtil.DATE_PATTERN.YYYY_MM_DD);


   public void initialize(DcCode constraint) {
   }

   //判断当前代币是否系统的基础代币  并且是否可用
   public boolean isValid(String date, ConstraintValidatorContext context) {
      if(StringUtils.isBlank(date)){
         return true;
      }
      try{

         LocalDateTime.parse(date,formatter1);
      }catch (Exception e){
         try{
            LocalDate.parse(date,formatter2);
         }catch (Exception ee){
            return false;
         }
         return true;
      }
      return true;
   }

}
