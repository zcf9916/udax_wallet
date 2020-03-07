package com.github.wxiaoqi.security.common.annotation;

import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class BigDecimalCoinSerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
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
        jsonGenerator.writeString(value.stripTrailingZeros().toPlainString());

//        value = value.setScale(8, BigDecimal.ROUND_HALF_UP);
//        jsonGenerator.writeNumber(value.doubleValue());
    }
}
