package com.github.wxiaoqi.security.common.annotation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

public class DateToTimeStampSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
//        String string;
//        if (value.compareTo(BigDecimal.ZERO) == 0) {
//            string = "0";
//        } else {
//            value = value.setScale(8, BigDecimal.ROUND_HALF_UP);
//            string = value.toPlainString();
//            if (string.contains(".")) {
//                while (string.endsWith("0")) {
//                    string = string.substring(0, string.length() - 1);
//                }
//                if (string.endsWith(".")) {
//                    string = string.substring(0, string.length() - 1);
//                }
//            }
//        }
       // jsonGenerator.writeString(value.stripTrailingZeros().toPlainString());

//        value = value.setScale(8, BigDecimal.ROUND_HALF_UP);
//        jsonGenerator.writeNumber(value.doubleValue());
        //时间转时间戳
        jsonGenerator.writeNumber(value.getTime());
    }
}
