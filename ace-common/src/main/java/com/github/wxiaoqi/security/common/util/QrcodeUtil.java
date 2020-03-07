package com.github.wxiaoqi.security.common.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 二维码工具类
 * 
 * @author tangjianbing
 * @since 2018年6月22日 
 */
public class QrcodeUtil {
    private static Logger logger = LogManager.getLogger();

	public static void produceQR(ServletOutputStream stream, String code,int width,int height) throws IOException{  
        try {  
            Map<EncodeHintType,Object> config = new HashMap<>();
            config.put(EncodeHintType.CHARACTER_SET,"UTF-8");
            config.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            config.put(EncodeHintType.MARGIN, 0);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(code, BarcodeFormat.QR_CODE,width,height,config);
            MatrixToImageWriter.writeToStream(bitMatrix,"png",stream);
            logger.info("QrcodeUtil:二维码生成完毕，已经输出到页面中。");
        } catch (Exception e) {  
        	logger.error("QrcodeUtil:二维码生成错误", e);  
        } finally {  
            if (stream != null) {  
                stream.flush();  
                stream.close();  
            }  
        }  
    }  
    
}
