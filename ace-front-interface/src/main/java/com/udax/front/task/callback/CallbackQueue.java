package com.udax.front.task.callback;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.entity.merchant.MchNotify;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.task.CallbackMsg;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.HttpUtils;
import com.udax.front.biz.FrontRechargeBiz;
import com.udax.front.biz.FrontWithdrawBiz;
import com.udax.front.biz.merchant.MchNotifyBiz;
import com.udax.front.biz.merchant.MchRefundDetailBiz;
import com.udax.front.biz.merchant.MchTradeDetailBiz;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.io.Serializable;

import static com.github.wxiaoqi.security.common.constant.Constants.MCH_CALLBACK_TASK;

//回调任务队列
@Service
@Slf4j
public class CallbackQueue {

    @Autowired
    private MchNotifyBiz mchNotifyBiz;


    //阻塞队列
   // public  LinkedBlockingQueue<CallbackMsg> queue = new LinkedBlockingQueue<>(1000);

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor executor;



    //启动自动运行
    public   void excuteCallback() {
        while (true ){

            //从队列获取数据，交给定时器执行
            Serializable serializable = CacheUtil.getCache().sPop(MCH_CALLBACK_TASK);
            try {
                CallbackMsg message =  (CallbackMsg) serializable;
                if(message == null || message.getCreateTime() == null || message.getCount() == null){
                    continue;
                }

                Thread.sleep(1000);
                log.info("开始处理回调信息,message:" + message.getNotifyId());
                //CallbackMsg message = queue.take();//阻塞等待
                long excueTime = message.getCreateTime().getTime()+Constants.callBackInterval[message.getCount()]* 1000;
                long t = excueTime - System.currentTimeMillis();
                if (t/1000 <= 1) {//1s之内将要执行的数据提交给调度线程池
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            Response response = HttpUtils.postNotify(message);
                            message.setCount(message.getCount() + 1);
                            MchNotify param = new MchNotify();
                            param.setNotifyId(message.getNotifyId());
                            MchNotify mchNotify = mchNotifyBiz.selectOne(param);
                            if(mchNotify == null){
                                log.info("商户回调返回信息,通过noticeId查找,数据为空:" + mchNotify);
                                return;
                            }
                            //返回失敗
                            if (response != null) {
                                log.info("商户回调返回信息,httpcode:" + response.code());
                                if(response.code() == 200){
                                    try{
                                        byte[] bytes = response.body().bytes();
                                        String result = new String(bytes, "UTF-8");
                                        log.info("商户回调返回信息" +result);
                                        JSONObject object =  JSON.parseObject(result);
                                        String return_code = (String)object.get("return_code");
                                        String return_msg = (String)object.get("return_msg");
                                        String notifyId = (String)object.get("notifyId");
                                        if("SUCCESS".equals(return_code) && "OK".equals(return_msg)){
                                            if(notifyId.equals(message.getNotifyId().toString())){
                                                //更新回調次數
                                                log.info("商户正确返回回调信息,更新通知状态,notifyId:" +message.getNotifyId());
                                                MchNotify updateParam = new MchNotify();
                                                updateParam.setId(mchNotify.getId());
                                                updateParam.setCount(message.getCount());
                                                updateParam.setStatus(EnableType.ENABLE.value());
                                                mchNotifyBiz.updateSelectiveByIdWithoutSetParam(updateParam);
                                                return;
                                            }

                                        }
                                    } catch (Exception e){
                                        log.error(e.getMessage(),e);
                                    }
                                }

                            }
                            log.info("商户回调失敗,,message:" + message.getNotifyId());
                            //失敗的情況,更新回調次數
                            MchNotify updateParam = new MchNotify();
                            updateParam.setId(mchNotify.getId());
                            updateParam.setCount(message.getCount());
                            mchNotifyBiz.updateSelectiveByIdWithoutSetParam(updateParam);
                            //如果大于等于6次
                            if(message.getCount() < 6){
                                //重新放入队列
                                CacheUtil.getCache().setSAdd(MCH_CALLBACK_TASK,message);
                              //  queue.add(message);
                            }
                        }
                    });
                }else {
                    if(message.getCount() < 6){
                        //重新放入队列
                     //   queue.add(message);
                        CacheUtil.getCache().setSAdd(MCH_CALLBACK_TASK,message);
                    }

                }
            } catch (Exception e) {
              log.error(e.getMessage(),e);
                    //重新放入队列
                    //   queue.add(message);
              CacheUtil.getCache().setSAdd(MCH_CALLBACK_TASK,serializable);
            }
        }
    }

}
