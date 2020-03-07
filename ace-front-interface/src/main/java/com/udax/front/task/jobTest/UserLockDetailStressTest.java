package com.udax.front.task.jobTest;

import com.github.wxiaoqi.security.common.util.HttpUtils;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.udax.front.vo.reqvo.PacketsModel;
import org.apache.tomcat.util.threads.TaskThread;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

//用户解锁资产,并发测试
public class UserLockDetailStressTest {

    public void openRp(){
        CyclicBarrier  cyclicBarrier = new CyclicBarrier(110);
        Thread lastThread = null;
        for(int i = 1 ;i <= 10 ;i ++ ){
            Thread t = new Thread(new TaskThread(cyclicBarrier,LocalDate.now().plusDays(i).toString(),lastThread));
            t.start();
            lastThread = t;
        }
        for(int i = 1 ;i <= 100 ;i ++ ){
            Thread t = new Thread(new RechargeThread(cyclicBarrier));
            t.start();
        }
    }

    public static void main(String[] args) {
        UserLockDetailStressTest rt = new UserLockDetailStressTest();
        rt.openRp();
    }
    class RechargeThread implements Runnable{

        CyclicBarrier barrier;

        public RechargeThread(CyclicBarrier barrier) {
            this.barrier =  barrier;
        }

        /**
         * 1.定时任务执行:
         *   解锁资产,改变状态
         *   更新解锁次数,已解锁资产
         * 2.充值入金:
         *   更新总资产,更新明细的应释放次数
         */
        @Override
        public void run() {
            try{
                barrier.await();
                String url = "http://localhost:9776/wallet/blockchain/recharge?info=";
                String info = "kQ4snv8H3xaSuQBpowzEteBkuAZprfFx1uVR5ABn3BzQrtjskQNNyBbFzPz8meBy5AuQ2vj9sSr1zd8Psfz1zR8PoeVNmxji2eRWttfNqQMRvA8CnuZEwurLmCJEwwj9wB4bnQRAsAyRrCfGyCAVvAJW1DBuvAnPzxncsDiRwCRyrP4u5DFEuuJNswnVtRnTtwnvtsJ8mC8jngMTuAnpntnQ3u8z5uRO5wVTng89uxyHvTmRqDiPztBnvCFD5vzp4AflngjrlPiMouVU3AQLzgMUlQqMwtqSrRnRqReLmfePtRbr4TFm1A8k4TvN3vfq2vB9rPBPqTrKrxAOzgfiqRnvsRns1fbK40uMnd8O2h8lsQrOzvj0uxfi4BmM3SfD2djErDjQvundstaOzgzmtS4crRbL4CrEnfjbw9JdsQYHrwBLsBfo3CNrrtQ=";
                url += info;
                System.out.println(Thread.currentThread().getName() + ":充值开始执行");
                Map<String,String> headerMap = InstanceUtil.newHashMap();
                headerMap.put("Content-Type","application/json");
                HttpUtils.postWithHeader(url,null,headerMap);
                System.out.println(Thread.currentThread().getName() + ":充值结束执行");
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }
    }

    class TaskThread implements Runnable{

        CyclicBarrier barrier;
        String date;
        Thread lastThread;
        public TaskThread(CyclicBarrier barrier,String date,Thread thread) {
            this.barrier =  barrier;
            this.date = date;
            this.lastThread = thread;
        }

        /**
         * 1.定时任务执行:
         *   解锁资产,改变状态
         *   更新解锁次数,已解锁资产
         * 2.充值入金:
         *   更新总资产,更新明细的应释放次数
         */
        @Override
        public void run() {
            try{
                barrier.await();
                if(this.lastThread != null){
                    this.lastThread.join();
                }

                System.out.println(Thread.currentThread().getName() + ":定时开始执行");
                Map<String,String> headerMap = InstanceUtil.newHashMap();
                headerMap.put("Content-Type","application/json");
                headerMap.put("exInfo","UDAX-HK");
                HttpUtils.postWithHeader("http://localhost:9776/wallet/untoken/test?date="+date,null,headerMap);
                System.out.println(Thread.currentThread().getName() + ":定时结束执行");
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }
    }
}
