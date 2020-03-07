package com.udax.front.tencent.req;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

//发送单对单消息
@Getter
@Setter
public class SendSingleMsgModel {

    @JSONField(name="From_Account")
    private String From_Account;//消息发送方 Identifier（用于指定发送消息方帐号）

    @JSONField(name="To_Account")
    private String To_Account;//消息接收方 Identifier

    @JSONField(name="MsgRandom")
    private Integer MsgRandom;//消息随机数，由随机函数产生（标记该条消息，用于后台定位问题）

    @JSONField(name="MsgBody")
    private MsgBodyClass[] MsgBody;//初始化成员列表

    @Getter
    @Setter
    public static class MsgBodyClass {

        @JSONField(name="MsgType")
        private String MsgType = "TIMCustomElem";//自定义类型

        @JSONField(name="MsgContent")
        private TIMCustomElem MsgContent;
//        @JSONField(name="MsgContent")
//        private MessageTransfer MsgContent;//

    }

    @Getter
    @Setter
    public static class TIMCustomElem{

        @JSONField(name="Data")
        public  String Data;
        @JSONField(name="Desc")
        public  String Desc;
        @JSONField(name="Ext")
        public  String Ext;
    }

    //发红包结构体
    @Getter
    @Setter
    public static class MessageRedPackerReceive {
        //币种
        String symbol;
        //数目
        String moneyNum;
        //描述
        String des;
        //发送方ID
        String senderID;
        //交易ID
        String redPackerID;
        //是否是群红包
        boolean isGroup;
        //个数
        String number;
        //接收方ID
        String receiverID;
        //群组ID
        String groupID;
        //是否是随机红包
        boolean isRandom;
        //收到了多少钱
        String receiveMoney;
    }

    //转账结构体
    @Getter
    @Setter
    public static class MessageTransfer{

            //币种
            public  String symbol;
            //数目
            public  String moneyNum;
            //描述
            public  String des;
            //发送方ID
            public  String senderID;
            //接收方ID
            public  String receiverID;
            //交易ID
            public  String transferID;
            //交易时间
            public  Long createTime;
    }


}
