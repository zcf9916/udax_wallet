package com.udax.front.tencent;

import com.alibaba.fastjson.JSON;
import com.github.wxiaoqi.security.common.constant.Constants;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.entity.front.RedPacketLog;
import com.github.wxiaoqi.security.common.entity.front.RedPacketSend;
import com.github.wxiaoqi.security.common.entity.front.TransferOrder;
import com.github.wxiaoqi.security.common.enums.RedPacketSendType;
import com.github.wxiaoqi.security.common.enums.RedPacketType;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.HttpUtils;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.udax.front.biz.FrontUserBiz;
import com.udax.front.tencent.req.*;
import com.udax.front.tencent.rsp.GroupOpenHttpSvcRspModel;
import com.udax.front.tencent.rsp.TencentRspModel;
import com.udax.front.util.CacheBizUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class TencentSendUtils {

    //查询用户是否在群组中
    public static boolean getInfoInGroup(String userName ,String groupId){

        GroupOpenHttpSvcModel msgModel = new GroupOpenHttpSvcModel();
        msgModel.setGroupId(groupId);
        msgModel.setUser_Account(new String[]{userName});


        String rspBody =  HttpUtils.postJson(Configure.getFormatUrl(Configure.Url.GROUP_OPEN_HTTP_SVC),msgModel);
        GroupOpenHttpSvcRspModel rspModel = JSON.parseObject(rspBody,GroupOpenHttpSvcRspModel.class);
        log.info("查询用户在群组中的身份,返回结果:" + JSON.toJSONString(rspModel));
        if(rspModel == null || StringUtil.listIsBlank(rspModel.getUserIdList())){
            return false;
        }
        if(!rspModel.getUserIdList().get(0).getMember_Account().equals(userName)){
            return false;
        }

        return true;
    }



    //发送自定义消息
     public static TencentRspModel singleMsg(TransferOrder order,FrontUserBiz frontUserBiz){
//         //获取用户名
//         FrontUser fromUser = CacheBizUtil.getFrontUserCache(order.getUserId(),frontUserBiz);
//         FrontUser receieveUser = CacheBizUtil.getFrontUserCache(order.getReceiveUserId(),frontUserBiz);


         SendSingleMsgModel msgModel = new SendSingleMsgModel();
         msgModel.setFrom_Account(order.getUserName());
         msgModel.setMsgRandom(getMsgRandom());
         msgModel.setTo_Account(order.getReceiveUserName());

         SendSingleMsgModel.MsgBodyClass[] arr = new SendSingleMsgModel.MsgBodyClass[1];
         SendSingleMsgModel.MsgBodyClass msgBody = new SendSingleMsgModel.MsgBodyClass();
         arr[0] = msgBody;
         //接收用户
         FrontUser receieveUser = CacheBizUtil.getFrontUserCache(order.getReceiveUserId(),frontUserBiz);
         //发送用户
         FrontUser sendUser = CacheBizUtil.getFrontUserCache(order.getUserId(),frontUserBiz);

         SendSingleMsgModel.MessageTransfer messageTransfer = new SendSingleMsgModel.MessageTransfer();
         messageTransfer.setCreateTime(order.getCreateTime().getTime());
         messageTransfer.setDes(order.getRemark());
         messageTransfer.setMoneyNum(order.getArrivalAmount().stripTrailingZeros().toPlainString());
         messageTransfer.setReceiverID(receieveUser.getUserName().toString());
         messageTransfer.setSenderID(sendUser.getUserName().toString());
         messageTransfer.setSymbol(order.getSymbol());
         messageTransfer.setTransferID(order.getOrderNo());


         SendSingleMsgModel.TIMCustomElem el = new SendSingleMsgModel.TIMCustomElem();
         el.setData("type_transfer");
         el.setDesc(messageTransfer.getDes());
         el.setExt(JSON.toJSONString(messageTransfer));

         msgBody.setMsgContent(el);
         msgModel.setMsgBody(arr);


         String rspBody =  HttpUtils.postJson(Configure.getFormatUrl(Configure.Url.SEND_SINGLE_MSG),msgModel);
         TencentRspModel rspModel = JSON.parseObject(rspBody,TencentRspModel.class);
         log.info("转账单聊信息,返回结果:" + JSON.toJSONString(rspModel));
         return rspModel;
     }

    //单聊红包信息
    public static TencentRspModel singleRedPackMsg(RedPacketLog order, FrontUserBiz frontUserBiz){
//         //获取用户名
         FrontUser fromUser = CacheBizUtil.getFrontUserCache(order.getUserId(),frontUserBiz);
         FrontUser receieveUser = CacheBizUtil.getFrontUserCache(order.getReceiveUserId(),frontUserBiz);
         //发红包记录
        RedPacketSend send  = (RedPacketSend) CacheUtil.getCache().get(Constants.REDPACKETS.RPORDER + order.getOrderNo());

        SendSingleMsgModel msgModel = new SendSingleMsgModel();
        msgModel.setFrom_Account(fromUser.getUserName());
        msgModel.setMsgRandom(getMsgRandom());
        msgModel.setTo_Account(receieveUser.getUserName());

        SendSingleMsgModel.MsgBodyClass[] arr = new SendSingleMsgModel.MsgBodyClass[1];
        SendSingleMsgModel.MsgBodyClass msgBody = new SendSingleMsgModel.MsgBodyClass();
        arr[0] = msgBody;
        SendSingleMsgModel.MessageRedPackerReceive messageTransfer = new SendSingleMsgModel.MessageRedPackerReceive();
        messageTransfer.setDes(send.getRemark());
        messageTransfer.setMoneyNum(order.getTotalAmount().stripTrailingZeros().toPlainString());
        messageTransfer.setReceiverID(receieveUser.getUserName());
        messageTransfer.setSenderID(fromUser.getUserName());
        messageTransfer.setSymbol(order.getSymbol());
        messageTransfer.setGroup(false);
        messageTransfer.setRandom(send.getType() == RedPacketType.RANDOM.value() ? true : false);
        messageTransfer.setReceiveMoney(order.getAmount().stripTrailingZeros().toPlainString());
        messageTransfer.setRedPackerID(send.getOrderNo());

        SendSingleMsgModel.TIMCustomElem el = new SendSingleMsgModel.TIMCustomElem();
        el.setData("type_red_packet_receive");
        el.setDesc(messageTransfer.getDes());
        el.setExt(JSON.toJSONString(messageTransfer));

        msgBody.setMsgContent(el);
        msgModel.setMsgBody(arr);


        String rspBody =  HttpUtils.postJson(Configure.getFormatUrl(Configure.Url.SEND_SINGLE_MSG),msgModel);
        TencentRspModel rspModel = JSON.parseObject(rspBody,TencentRspModel.class);
        log.info("转账单聊信息,返回结果:" + JSON.toJSONString(rspModel));
        return rspModel;
    }

    //群聊红包信息
    public static TencentRspModel multiRedPackMsg(RedPacketLog order, FrontUserBiz frontUserBiz){
//         //获取用户名
        FrontUser fromUser = CacheBizUtil.getFrontUserCache(order.getUserId(),frontUserBiz);
        FrontUser receieveUser = CacheBizUtil.getFrontUserCache(order.getReceiveUserId(),frontUserBiz);
        //发红包记录
        RedPacketSend send  = (RedPacketSend) CacheUtil.getCache().get(Constants.REDPACKETS.RPORDER + order.getOrderNo());

        MultiMsgModel msgModel = new MultiMsgModel();
        msgModel.setFrom_Account(fromUser.getUserName());
        msgModel.setRandom(getMsgRandom());
        msgModel.setGroupId(send.getGroupId());

        MultiMsgModel.MsgBodyClass[] arr = new MultiMsgModel.MsgBodyClass[1];
        MultiMsgModel.MsgBodyClass msgBody = new MultiMsgModel.MsgBodyClass();
        arr[0] = msgBody;
        MultiMsgModel.MessageRedPackerReceive messageTransfer = new MultiMsgModel.MessageRedPackerReceive();
        messageTransfer.setDes(send.getRemark());
        messageTransfer.setMoneyNum(order.getTotalAmount().stripTrailingZeros().toPlainString());
        messageTransfer.setReceiverID(receieveUser.getUserName());
        messageTransfer.setSenderID(fromUser.getUserName());
        messageTransfer.setSymbol(order.getSymbol());
        messageTransfer.setGroup(true);
        messageTransfer.setGroupID(send.getGroupId());
        messageTransfer.setRandom(send.getType().intValue() == RedPacketType.RANDOM.value() ? true : false);
        messageTransfer.setReceiveMoney(order.getAmount().stripTrailingZeros().toPlainString());
        messageTransfer.setRedPackerID(send.getOrderNo());

        MultiMsgModel.TIMCustomElem el = new MultiMsgModel.TIMCustomElem();
        el.setData("type_red_packet_receive");
        el.setDesc(messageTransfer.getDes());
        el.setExt(JSON.toJSONString(messageTransfer));

        msgBody.setMsgContent(el);
        msgModel.setMsgBody(arr);
        //如果是群聊的红包
      String rspBody =  HttpUtils.postJson(Configure.getFormatUrl(Configure.Url.SEND_MULTI_MSG),msgModel);
      TencentRspModel rspModel = JSON.parseObject(rspBody,TencentRspModel.class);
      log.info("群聊红包信息,返回结果:" + JSON.toJSONString(rspModel));
      return rspModel;



    }


    //修改群组消息
    public static TencentRspModel groupInfo(String groupId,String faceUrl){
        ModifyGroupBaseInfoModel model = new ModifyGroupBaseInfoModel();
        model.setGroupId(groupId);
        model.setFaceUrl(faceUrl);
        String rspBody =  HttpUtils.postJson(Configure.getFormatUrl(Configure.Url.SET_GROUP_INFO),model);
        TencentRspModel rspModel = JSON.parseObject(rspBody,TencentRspModel.class);
        return rspModel;
    }

    //设置个人加好友方式
    public static TencentRspModel setUserInfo(String userName){
        SetUserImageModel model = new SetUserImageModel();
        model.setFrom_Account(userName);
        SetUserImageModel.PersonalInfo[] arr =  new SetUserImageModel.PersonalInfo[1];
        SetUserImageModel.PersonalInfo info = new SetUserImageModel.PersonalInfo();
        info.setTag("Tag_Profile_IM_AllowType");
        info.setValue("AllowType_Type_NeedConfirm");
        arr[0] = info;
        model.setProfileItem(arr);
        String rspBody =  HttpUtils.postJson(Configure.getFormatUrl(Configure.Url.SET_USER_INFO),model);
        TencentRspModel rspModel = JSON.parseObject(rspBody,TencentRspModel.class);
        return rspModel;
    }

    //修改个人头像
    public static TencentRspModel setUserImg(String imgUrl){
        SetUserImageModel model = new SetUserImageModel();
        model.setFrom_Account(BaseContextHandler.getUsername());
        SetUserImageModel.PersonalInfo[] arr =  new SetUserImageModel.PersonalInfo[1];
        SetUserImageModel.PersonalInfo info = new SetUserImageModel.PersonalInfo();
        info.setTag("Tag_Profile_IM_Image");
        info.setValue(imgUrl);
        arr[0] = info;
        model.setProfileItem(arr);
        String rspBody =  HttpUtils.postJson(Configure.getFormatUrl(Configure.Url.SET_USER_INFO),model);
        TencentRspModel rspModel = JSON.parseObject(rspBody,TencentRspModel.class);
        return rspModel;
    }



    //导入账号
    public static TencentRspModel importAccount(String userName){
        AccountImportModel model = new AccountImportModel();
        model.setIdentifier(userName);
        model.setNick(userName);
        String rspBody =  HttpUtils.postJson(Configure.getFormatUrl(Configure.Url.ACCOUNT_IMPORT),model);
        TencentRspModel rspModel = JSON.parseObject(rspBody,TencentRspModel.class);
        return rspModel;
    }



    public static Integer getMsgRandom(){
        LocalDateTime localDateTime = LocalDateTime.now();
        String currentTime = (localDateTime.toLocalTime().toString()).replaceAll("[^0-9]","");
        //时间戳+2位随机数
        Integer randomMsg = Integer.valueOf(currentTime);
        return randomMsg;
    }

    public static void main(String[] args) {

//        SendSingleMsgModel model = new SendSingleMsgModel();
//        model.setFrom_Account("liuzz");
//        model.setMsgRandom(getMsgRandom());
//        model.setTo_Account("88888888klasdf");
//
//        SendSingleMsgModel.MsgBodyClass[] arr = new SendSingleMsgModel.MsgBodyClass[1];
//        SendSingleMsgModel.MsgBodyClass msgBody = new SendSingleMsgModel.MsgBodyClass();
//        arr[0] = msgBody;
//
//
//
//        SendSingleMsgModel.MessageTransfer messageTransfer = new SendSingleMsgModel.MessageTransfer();
//        messageTransfer.setCreateTime(123123L);
//        messageTransfer.setDes("转账备注");
//        messageTransfer.setMoneyNum("100");
//        messageTransfer.setReceiverID("123");
//        messageTransfer.setSenderID("123");
//        messageTransfer.setSymbol("BTC");
//        messageTransfer.setTransferID("1234");
//
//
//        SendSingleMsgModel.TIMCustomElem el = new SendSingleMsgModel.TIMCustomElem();
//        el.setData("type_transfer");
//        el.setDesc(messageTransfer.getDes());
//        el.setExt(JSON.toJSONString(messageTransfer));
//
//        msgBody.setMsgContent(el);
//        model.setMsgBody(arr);
//
//        String rspBody =  HttpUtils.postJson(Configure.getFormatUrl(Configure.Url.SEND_SINGLE_MSG),model);
//        TencentRspModel rspModel = JSON.parseObject(rspBody,TencentRspModel.class);


//        ModifyGroupBaseInfoModel model = new ModifyGroupBaseInfoModel();
//        model.setGroupId("@TGS#2JTCS77FH");
//        model.setFaceUrl("http://www.baidu.com");
//        TencentSendUtils.groupInfo(model.getGroupId(),model.getFaceUrl());
//        String rspBody =  HttpUtils.postJson(Configure.getFormatUrl(Configure.Url.SET_GROUP_INFO),model);
//        TencentRspModel rspModel = JSON.parseObject(rspBody,TencentRspModel.class);

//        CreateGroupModel model = new CreateGroupModel();
//        model.setName("#123123");
//        model.setType("Public");
//        model.setOwner_Account("88888888");
//        CreateGroupModel.MemberClass[] arr = new CreateGroupModel.MemberClass[2];
////
////
////
//        CreateGroupModel.MemberClass tt = new CreateGroupModel.MemberClass();
//        tt.setMember_Account("87973931@qq.com");
//        tt.setRole("Admin");
//        CreateGroupModel.MemberClass tt1 = new CreateGroupModel.MemberClass();
//        tt1.setMember_Account("87973932@qq.com");
//        arr[0] = tt;
//        arr[1] = tt1;
//
//        model.setMemberList(arr);
//        String rspBody =  HttpUtils.postJson(Configure.getFormatUrl(Configure.Url.CREATE_GROUP),model);
//        TencentRspModel rspModel = JSON.parseObject(rspBody,TencentRspModel.class);


//        MultiMsgModel msgModel = new MultiMsgModel();
//        msgModel.setFrom_Account("wallet1@qq.com");
//        //msgModel.setTo_Account("wallet2@qq.com");
//        msgModel.setRandom(getMsgRandom());
//
//        MultiMsgModel.MsgBodyClass[] arr = new MultiMsgModel.MsgBodyClass[1];
//        MultiMsgModel.MsgBodyClass msgBody = new MultiMsgModel.MsgBodyClass();
//        arr[0] = msgBody;
//        MultiMsgModel.MessageRedPackerReceive messageTransfer = new MultiMsgModel.MessageRedPackerReceive();
//        messageTransfer.setDes("备注");
//        messageTransfer.setMoneyNum("1.0");
//        messageTransfer.setReceiverID("wallet2@qq.com");
//        messageTransfer.setSenderID("wallet1@qq.com");
//        messageTransfer.setSymbol("BTC");
//        messageTransfer.setGroup(true);
//        messageTransfer.setGroupID("");
//        messageTransfer.setRandom( true);
//        messageTransfer.setReceiveMoney("0.1");
//        messageTransfer.setRedPackerID("1123123123");
//
//
//        MultiMsgModel.TIMCustomElem el = new MultiMsgModel.TIMCustomElem();
//        el.setData("type_red_packet_receive");
//        el.setDesc(messageTransfer.getDes());
//        el.setExt(JSON.toJSONString(messageTransfer));
//
//        msgBody.setMsgContent(el);
//        msgModel.setMsgBody(arr);
//
//        log.info("转账单聊信息,返回结果:" + JSON.toJSONString(msgModel));
//
//        String rspBody =  HttpUtils.postJson(Configure.getFormatUrl(Configure.Url.SEND_SINGLE_MSG),msgModel);
//        TencentRspModel rspModel = JSON.parseObject(rspBody,TencentRspModel.class);
//        log.info("转账单聊信息,返回结果:" + JSON.toJSONString(rspModel));
//        //如果是群聊的红包
//        if(send.getSendType().intValue() == RedPacketSendType.MULTI.value()){
//            String rspBody =  HttpUtils.postJson(Configure.getFormatUrl(Configure.Url.SEND_MULTI_MSG),msgModel);
//            TencentRspModel rspModel = JSON.parseObject(rspBody,TencentRspModel.class);
//            log.info("群聊红包信息,返回结果:" + JSON.toJSONString(rspModel));
//            return rspModel;
//        } else {
//            String rspBody =  HttpUtils.postJson(Configure.getFormatUrl(Configure.Url.SEND_SINGLE_MSG),msgModel);
//            TencentRspModel rspModel = JSON.parseObject(rspBody,TencentRspModel.class);
//            log.info("转账单聊信息,返回结果:" + JSON.toJSONString(rspModel));
//            return rspModel;
//        }
        System.out.println(TencentSendUtils.getInfoInGroup("283632905@qq.com","@TGS#2YF565BGG"));

    }


}
