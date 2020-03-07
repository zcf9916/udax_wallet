package com.github.wxiaoqi.security.common.handler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.wxiaoqi.security.common.constant.CommonConstants;
import com.github.wxiaoqi.security.common.enums.MchResponseCode;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.exception.BaseException;
import com.github.wxiaoqi.security.common.exception.BusinessException;
import com.github.wxiaoqi.security.common.exception.MchException;
import com.github.wxiaoqi.security.common.exception.auth.UserInvalidException;
import com.github.wxiaoqi.security.common.exception.auth.UserTokenException;
import com.github.wxiaoqi.security.common.msg.BaseResponse;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ace on 2017/9/8.
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

//    @ExceptionHandler(ClientTokenException.class)
//    public BaseResponse clientTokenExceptionHandler(HttpServletResponse response, ClientTokenException ex) {
//        response.AfterStatus(403);
//        logger.error(ex.getMessage(),ex);
//       // return new BaseResponse(ex.getStatus(), ex.getMessage());
//        return new ObjectRestResponse<>().rel(false).status(ex.getStatus()).msg(ex.getMessage());
//    }

    @ExceptionHandler(UserTokenException.class)
    public BaseResponse userTokenExceptionHandler(HttpServletResponse response, UserTokenException ex) {
        response.setStatus(200);
        logger.error(ex.getMessage(), ex);
        return new ObjectRestResponse<>().rel(false).status(ex.getStatus()).msg(ex.getMessage());
        //return new BaseResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public BaseResponse expiredJwtExceptionHandler(HttpServletResponse response, ExpiredJwtException ex) {
        response.setStatus(200);
        logger.error(ex.getMessage(), ex);
        return new ObjectRestResponse<>().rel(false).status( CommonConstants.EX_USER_INVALID_CODE).msg(ex.getMessage());
        //return new BaseResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(UserInvalidException.class)
    public BaseResponse userInvalidExceptionHandler(HttpServletResponse response, UserInvalidException ex) {
        response.setStatus(200);
        logger.error(ex.getMessage(), ex);
        return new ObjectRestResponse<>().rel(false).status(ex.getStatus()).msg(ex.getMessage());
        //return new BaseResponse(ex.getStatus(), ex.getMessage());
    }


    /**
     * 处理上传异常
     *
     * @param t
     * @return
     */
    @ExceptionHandler(MultipartException.class)
    public BaseResponse MultipartException(HttpServletResponse response, MultipartException ex) {
        return new ObjectRestResponse<>().status(ResponseCode.UPLOAD_FILE_SIZE);
        //return new BaseResponse(ex.getStatus(), ex.getMessage());
    }


    @ExceptionHandler({HttpMessageNotReadableException.class})
    public BaseResponse HttpMessageNotReadableExceptionHandler(HttpServletResponse response, HttpMessageNotReadableException ex) {
        logger.error(ex.getMessage(), ex);
        return new ObjectRestResponse<>().status(ResponseCode.PARAM_ERROR);
    }


    @ExceptionHandler({NumberFormatException.class})
    public BaseResponse NumberFormatExceptionHandler(HttpServletResponse response, NumberFormatException ex) {
        logger.error(ex.getMessage(), ex);
        return new ObjectRestResponse<>().status(ResponseCode.PARAM_ERROR);
    }


    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public BaseResponse MethodArgumentTypeMismatchExceptionHandler(HttpServletResponse response, MethodArgumentTypeMismatchException ex) {
        logger.error(ex.getMessage(), ex);
        return new ObjectRestResponse<>().status(ResponseCode.PARAM_ERROR);
    }


    @ExceptionHandler(BaseException.class)
    public void baseExceptionHandler(HttpServletResponse response, BaseException ex) {
        otherExceptionHandler(response, ex);
    }

    @ExceptionHandler(BusinessException.class)
    public void businessException(HttpServletResponse response, BusinessException ex) {
        //  response.setStatus(500);
        // return new ObjectRestResponse<>().rel(false).status(CommonConstants.EX_OTHER_CODE).msg(ex.getMessage());
        // return new BaseResponse(CommonConstants.EX_OTHER_CODE, ex.getMessage());
        otherExceptionHandler(response, ex);
    }


    @ExceptionHandler(MchException.class)
    public void mchException(HttpServletResponse response, MchException ex) {
        //  response.setStatus(500);
        // return new ObjectRestResponse<>().rel(false).status(CommonConstants.EX_OTHER_CODE).msg(ex.getMessage());
        // return new BaseResponse(CommonConstants.EX_OTHER_CODE, ex.getMessage());
        otherExceptionHandler(response, ex);
    }

    @ExceptionHandler(Exception.class)
    public void otherExceptionHandler(HttpServletResponse response, Exception ex) {
//        response.setStatus(500);
//        logger.error(ex.getMessage(),ex);
//        return new ObjectRestResponse<>().rel(false).status(CommonConstants.EX_OTHER_CODE).msg(ex.getMessage());
        //return new BaseResponse(CommonConstants.EX_OTHER_CODE, ex.getMessage());
        logger.error(ex.getMessage(), ex);
        ResponseCode code = ResponseCode.getEnumByname(ex.getMessage());
        if (code == ResponseCode.UNKNOW_ERROR) {
            MchResponseCode mchResponseCode = MchResponseCode.getEnumByname(ex.getMessage());
            sendJsonMessage(response, new ObjectRestResponse<>().status(mchResponseCode));
            return;
        }

        sendJsonMessage(response, new ObjectRestResponse<>().status(code));
    }

    /**
     * 处理validator异常
     *
     * @param e
     * @param response
     */
    @ExceptionHandler(BindException.class)
    public BaseResponse validExceptionHandler(BindException e, HttpServletResponse response) throws Exception {
        response.setStatus(500);
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        response.setContentType("application/json;charset=UTF-8");
        Map<String, Object> modelMap = new HashMap<String, Object>();
        StringBuffer errorInfo = new StringBuffer();
        for (FieldError error : fieldErrors) {
            errorInfo.append(error.getDefaultMessage() + ",");
        }
//        modelMap.put("code", HttpCode.INTERNAL_SERVER_ERROR.value().toString());
//        modelMap.put("msg",errorInfo);
//        modelMap.put("timestamp", System.currentTimeMillis());
//        logger.info(JSON.toJSON(modelMap));
//        byte[] bytes = JSON.toJSONBytes(modelMap, SerializerFeature.DisableCircularReferenceDetect);
//        response.getOutputStream().write(bytes);
        return new ObjectRestResponse<>().rel(false).status(ResponseCode.UNKNOW_ERROR).msg(errorInfo.toString());
    }

    /**
     * 处理validator json异常
     *
     * @param e
     * @param response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse validJsonExceptionHandler(MethodArgumentNotValidException e, HttpServletResponse response) throws Exception {
        logger.error(e.getMessage(), e);
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        response.setContentType("application/json;charset=UTF-8");
        // StringBuffer errorInfo = new StringBuffer();
        ResponseCode code = ResponseCode.UNKNOW_ERROR;
        FieldError error = fieldErrors.get(0);
        String msg = error.getDefaultMessage();
        if (StringUtils.isNotBlank(msg)) {
            msg = msg.replaceAll("\\{", "");
            msg = msg.replaceAll("\\}", "");
            code = ResponseCode.getEnumByname(msg);
            if (code == ResponseCode.UNKNOW_ERROR) {
                MchResponseCode mchResponseCode = MchResponseCode.getEnumByname(msg);
                return new ObjectRestResponse<>().status(mchResponseCode);
            }
        }
//        int count = 0;
//        for (FieldError error:fieldErrors){
//            String msg = error.getDefaultMessage();
//            if(StringUtils.isNotBlank(msg)){
//                msg = msg.replaceAll("\\{","");
//                msg = msg.replaceAll("\\}","");
//                msg = Resources.getMessage(msg);
//            }
//            count++;
//            if(count==fieldErrors.size()) {//最后一个不要拼接','
//                errorInfo.append(msg);
//            }else {
//                errorInfo.append(msg+";");
//            }
//        }
        return new ObjectRestResponse<>().status(code);
        //return new BaseResponse(CommonConstants.EX_OTHER_CODE, errorInfo.toString());
    }


    /**
     * 将某个对象转换成json格式并发送到客户端
     *
     * @param response
     * @param obj
     * @throws Exception
     */
    public void sendJsonMessage(HttpServletResponse response, Object obj) {
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = null;
        try {

            writer = response.getWriter();
            writer.print(JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat));
            writer.close();
            response.flushBuffer();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return;
        }


    }

}
