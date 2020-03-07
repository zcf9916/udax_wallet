package com.udax.front.controller.tencent;

import com.alibaba.fastjson.JSON;
import com.github.wxiaoqi.security.common.configuration.SftpConfiguration;
import com.github.wxiaoqi.security.common.entity.front.FrontUser;
import com.github.wxiaoqi.security.common.enums.UploadType;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.CacheUtil;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.UploadUtil;
import com.udax.front.biz.FrontUserBiz;
import com.udax.front.controller.BaseFrontController;
import com.udax.front.tencent.Configure;
import com.udax.front.tencent.ImageUtils;
import com.udax.front.tencent.TencentSendUtils;
import com.udax.front.tencent.rsp.TencentCallbackModel;
import com.udax.front.tencent.rsp.TencentRspModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.wxiaoqi.security.common.constant.Constants.TENCENT_GROUP_NAME;

/**
 *
 *  接收腾讯回调信息
 * @author liuzz
 *
 */

@RestController
@RequestMapping("/wallet/tencent/")
@Slf4j
public class TencentCallBackController  extends BaseFrontController<FrontUserBiz, FrontUser> {


	@Autowired
    private SftpConfiguration sftpConfiguration;



    /**
	 *  发红包
	 *  参数 代币 红包类型
	 *
	 * @return
	 */
	@PostMapping("callback")
	public TencentRspModel afterCreateGroup(@RequestBody @Valid TencentCallbackModel model)  {
         log.info("类型:" + model.getCallbackCommand()+"回调信息:" + JSON.toJSONString(model));
         try{
             if(StringUtils.isNotBlank(model.getCallbackCommand())) {
                 String groupId = model.getGroupId();
                 Set<Serializable> userNameGroup = InstanceUtil.newHashSet();
                 int currentSize = CacheUtil.getCache().getListSize(TENCENT_GROUP_NAME + groupId).intValue();
                 int afterSize = 0;
                 //成员退出
                 if (model.getCallbackCommand().contains("CallbackAfterMemberExit")) {
                     Arrays.asList(model.getExitMemberList()).stream().forEach((k) -> {
                         //userNameGroup.add(k.getMember_Account());
                         log.info(k.getMember_Account() + "从" + groupId + "中退群,从redis移除");
                         //根据组名,对应用户名插入redis list
                         CacheUtil.getCache().listRemove(TENCENT_GROUP_NAME + groupId, k.getMember_Account());

                     });
                     afterSize = currentSize - model.getExitMemberList().length;
                 } else if (model.getCallbackCommand().contains("CallbackAfterCreateGroup") || model.getCallbackCommand().contains("CallbackAfterNewMemberJoin")) {
                     if (model.getCallbackCommand().contains("CallbackAfterCreateGroup")) {
                         //创建组之后 //新成员加入
                         Arrays.asList(model.getMemberList()).stream().forEach((k) -> {
                             userNameGroup.add(k.getMember_Account());
                         });
                         afterSize = currentSize + model.getMemberList().length;
                     }
                     if (model.getCallbackCommand().contains("CallbackAfterNewMemberJoin")) {
                         //创建组之后 //新成员加入
                         Arrays.asList(model.getNewMemberList()).stream().forEach((k) -> {
                             userNameGroup.add(k.getMember_Account());
                         });
                         afterSize = currentSize + model.getNewMemberList().length;
                     }

                     log.info(userNameGroup + "加入" + groupId + "中,添加到redis中");
                     //根据组名,对应用户名插入redis list
                     CacheUtil.getCache().rightPushList(TENCENT_GROUP_NAME + groupId, userNameGroup);
                 }

                 //加入新成员的时候  人数大于9,不修改群头像;退群之后,人数大于9也不修改
                 log.info("原始群人数:" + currentSize + ";操作之后的群人数:" + afterSize);
                 if (currentSize <= 9 || afterSize <= 9) {
                     List<Serializable> userNameList = CacheUtil.getCache().getRangeList(TENCENT_GROUP_NAME + groupId, 0L, 9L);
                     //获取前九个加群的人的头像地址
                     List<String> imageList = userNameList.stream().map((e) -> {
                         FrontUser frontUser = baseBiz.selectUnionUserInfoByUserName((String) e);
                         String imagePath = null;
                         if (frontUser != null && frontUser.getUserInfo() != null) {
                             imagePath = frontUser.getUserInfo().getPortrait();//获取头像地址
                         }
                         if (StringUtils.isBlank(imagePath)) {
                             imagePath = ClassUtils.getDefaultClassLoader().getResource("").getPath() + "/static/group-default.png";
                         }
                         return imagePath;
                     }).collect(Collectors.toList());

//
//			 StringBuffer outPath = new StringBuffer().append(dir)
//					 .append(File.separatorChar)
//					 .append( groupId).append(".png");
                     //临时文件路径
                     String path = UploadUtil.getUploadDir(request) + File.separatorChar + Base64.getEncoder().encodeToString(groupId.getBytes()) + ".png";

                     ImageUtils.getCombinationOfhead(imageList, path);

                     List<String> fileName = InstanceUtil.newArrayList(path);
                     List<String> fileNamePath = UploadUtil.remove2SftpList(fileName, sftpConfiguration, UploadType.getType(UploadType.TENCENT_GROUP_INFO.value().toString()));
                     log.info("生成群" + groupId + "的新头像");
                     TencentSendUtils.groupInfo(groupId, fileNamePath.get(0));
                 }
             }
         } catch (Exception e){
             log.error(e.getMessage(),e);
             TencentRspModel rspModel =  new TencentRspModel();
             rspModel.setActionStatus("FAIL");
             rspModel.setErrorCode("1");
             rspModel.setErrorInfo("");
             return rspModel;
         }

         TencentRspModel rspModel =  new TencentRspModel();
         rspModel.setActionStatus("OK");
         rspModel.setErrorCode("0");
         rspModel.setErrorInfo("");
         return rspModel;
	}

//	/**
//	 *  发红包
//	 *  参数 代币 红包类型
//	 *
//	 * @return
//	 */
//	@PostMapping("afterNewMemberJoinModel")
//	public TencentRspModel afterNewMemberJoinModel(@RequestBody @Valid AfterNewMemberJoinModel model) throws Exception {
//		log.info("新成员加入群组回调信息:" + JSON.toJSONString(model));
//		return new TencentRspModel();
//	}
//
//
//	/**
//	 *  发红包
//	 *  参数 代币 红包类型
//	 *
//	 * @return
//	 */
//	@PostMapping("afterMemberLeft")
//	public TencentRspModel AfterMemberLeft(@RequestBody @Valid AfterMemberLeftModel model) throws Exception {
//		log.info("群成员离开回调信息:" + JSON.toJSONString(model));
//		return new TencentRspModel();
//	}


}
