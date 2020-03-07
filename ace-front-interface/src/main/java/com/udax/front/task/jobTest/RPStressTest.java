package com.udax.front.task.jobTest;

import com.github.wxiaoqi.security.common.entity.ud.HSettledProfit;
import com.github.wxiaoqi.security.common.enums.EnableType;
import com.github.wxiaoqi.security.common.enums.ud.SettledProfitStatus;
import com.github.wxiaoqi.security.common.util.HttpUtils;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.udax.front.biz.ud.HSettledProfitBiz;
import com.udax.front.vo.reqvo.PacketsModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;


public class RPStressTest {

    private String orderNo = "1165935884512530432";

    public  String url = "http://localhost:9776/wallet/redpackets/openRedPackets";


    private String token =
            "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxODU1OTkzMzA4NCIsInVzZXJJZCI6OTYsIm5hbWUiOiIxMTQzMDU0MDUwNzAzNDQ2MDE2IiwiZXhpZCI6MSwiZXhwIjoxNTY2ODI1MjI4LCJpYXQiOjE1NjY4MTA4Mjh9.VX8t6oMdgE3eHg1ZIoJn3M0MHX93LYVpAqlWen84Ij8o5NGKcU84Dyso2W3CRuNDcnErgDvbwceSicX1DIZHJ58Q6DVloidDBVXszW3tfIQcCnT4sItdfEanqtIjCDG9jedTXiGPeDMi2dSV26S-XZtS2ht_ZvGy5qn9LRq-alQ";


    public void openRp(){
        for(int i = 1 ;i < 500 ;i ++ ){
            Thread t = new Thread(new RPThread(Long.valueOf(i)));
            t.start();
        }
    }

    public static void main(String[] args) {
        RPStressTest rt = new RPStressTest();
        rt.openRp();
    }


    class RPThread implements Runnable{

        private Long userId;

        public RPThread(Long userId) {
            this.userId = userId;
        }

        @Override
        public void run() {
            PacketsModel model = new PacketsModel();
           // model.setUserId(userId);
            model.setOrderNo(orderNo);
            Map<String,String> headerMap = InstanceUtil.newHashMap();
            headerMap.put("Content-Type","application/json");
            headerMap.put("Authorization",token);
            headerMap.put("exInfo","UDAX-UD");
            String result = HttpUtils.postWithHeader(url,model,headerMap);
            System.out.println(Thread.currentThread().getName() + ":result:" + result);
        }
    }
}
