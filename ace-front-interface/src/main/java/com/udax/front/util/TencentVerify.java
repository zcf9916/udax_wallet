package com.udax.front.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.common.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.net.URLEncoder;

@Slf4j
public class TencentVerify {
    private static final String APP_ID = "2001970578";
    private static final String APP_SECRET = "08vBGw1J4Xsogi3ruziHlqQ**";
    private static final String VERIFY_URI = "https://ssl.captcha.qq.com/ticket/verify?aid=%s&AppSecretKey=%s&Ticket=%s&Randstr=%s&UserIP=%s";

    //可以用okhttp 连接池,适用于请求多的情况
    public static boolean verifyTicket(String ticket, String rand, String userIp) {

        final int[] result = new int[1];
        result[0] = -1;
        try {
        String url = String.format(VERIFY_URI,
                    APP_ID,
                    APP_SECRET,
                    URLEncoder.encode(ticket, "UTF-8"),
                    URLEncoder.encode(rand, "UTF-8"),
                    URLEncoder.encode(userIp, "UTF-8")
            );
//        OkHttpClient okHttpClient = new OkHttpClient();
//        final Request request = new Request.Builder().url(url).get().build();//默认就是GET请求，可以不写
//
//            HttpUtils.get(url)
//        Call call = okHttpClient.newCall(request);
//            Response response =    call.execute();
//            String res = response.body().string();
            String res = HttpUtils.get(url);
            JSONObject jsonObject = JSON.parseObject(res);
           // 返回码
            int code = jsonObject.getInteger("response");
            // 恶意等级
            int evilLevel = jsonObject.getInteger("evil_level");

            // 验证成功
            if (code == 1) {
                return true;
            }
//            response.close();
//            call.enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    log.error("请求失败: ");
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    String res = response.body().string();
//                    JSONObject jsonObject = JSON.parseObject(res);
//                   // 返回码
//                    int code = jsonObject.getInteger("response");
//                    // 恶意等级
//                    int evilLevel = jsonObject.getInteger("evil_level");
//
//                    // 验证成功
//                    if (code == 1) {
//                        result[0] = evilLevel;
//                    }
//                    response.close();
//                }
//            });
        } catch (java.io.IOException e) {
            // 忽略
            log.error(e.getMessage(),e);
        }


        return false;
//
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        HttpGet httpGet;
//        CloseableHttpResponse response = null;
//        try {
//            httpGet = new HttpGet(String.format(VERIFY_URI,
//                    APP_ID,
//                    APP_SECRET,
//                    URLEncoder.encode(ticket, "UTF-8"),
//                    URLEncoder.encode(rand, "UTF-8"),
//                    URLEncoder.encode(userIp, "UTF-8")
//            ));
//            response = httpclient.execute(httpGet);
//
//            HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                String res = EntityUtils.toString(entity);
//                System.out.println(res); // 临时输出
//
//                JSONObject result = JSON.parseObject(res);
//                // 返回码
//                int code = result.getInteger("response");
//                // 恶意等级
//                int evilLevel = result.getInteger("evil_level");
//
//                // 验证成功
//                if (code == 1) return evilLevel;
//            }
//        } catch (java.io.IOException e) {
//            // 忽略
//        } finally {
//            try {
//                response.close();
//            } catch (Exception ignore) {
//            }
//        }
    }

    public static void main(String[] args) throws Exception {
//       int t =  TencentVerify.verifyTicket("t02lzIXcOeM52OI4N2u62bugHQ6cvfA0lf6Zc97eEsTB8Ks-GNQ9XdDFfJVjYMd3keprewc1f4bWaj_gbyN8H6wYyX0-2dxqfw76turqdIozYe1Z1Of3rklzw**", "@z_4", "113.89.196.149");
//        System.out.println(t);
    }
}