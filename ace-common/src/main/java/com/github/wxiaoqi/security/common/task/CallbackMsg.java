package com.github.wxiaoqi.security.common.task;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.HttpUtils;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Response;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import static com.github.wxiaoqi.security.common.util.HttpUtils.POST;

@Getter
@Setter
public  class CallbackMsg implements Serializable {


    private static final long serialVersionUID = -5809782578272943999L;
//    //更新时间
    public Date createTime;//创建时间

    public Integer count;//回调次数

    public Long notifyId;


    public String notifyStr;//回调的json串
//    public Integer type;//1.预下单标示  2.下单成功通知标示  3.充值成功通知标示  4.提现成功通知标示 5.退款成功通知标示
//
//    public String orderNo;//相关订单号
//
    public String callbackUrl;//回调url


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallbackMsg that = (CallbackMsg) o;
        return Objects.equals(notifyId, that.notifyId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(notifyId);
    }
}
