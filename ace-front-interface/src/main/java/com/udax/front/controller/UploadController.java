package com.udax.front.controller;

import com.github.wxiaoqi.security.common.configuration.SftpConfiguration;
import com.github.wxiaoqi.security.common.context.BaseContextHandler;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.UploadType;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.StringUtil;
import com.github.wxiaoqi.security.common.util.UploadUtil;
//import com.udax.front.util.UploadUtil;
import com.udax.front.biz.UploadBiz;
import com.udax.front.tencent.TencentSendUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

import static com.github.wxiaoqi.security.common.enums.UploadType.TENCENT_GROUP_INFO;

/**
 * 文件上传控制器
 * 
 * @author ShenHuaJie
 * @version 2016年5月20日 下午3:11:42
 */
@RestController
@RequestMapping(value = "/wallet/upload", method = RequestMethod.POST)
@Slf4j
public class UploadController {

    @Autowired
	private UploadBiz uploadBiz;

	@Autowired
	private SftpConfiguration sftpConfig;

	public String getService() {
		return null;
	}

	// 上传文件(支持批量)
	@RequestMapping("/temp/file")
	public ObjectRestResponse uploadFile(HttpServletRequest request) {
		List<String> fileNames = UploadUtil.uploadFile(request);
		if (fileNames.size() > 0) {
			return new ObjectRestResponse().data(fileNames);
		} else {
			return new ObjectRestResponse().status(ResponseCode.UPLOAD_FILE_IS_NULL);
		}
	}

	// 上传文件(支持批量)
	@RequestMapping("/temp/image")
	public ObjectRestResponse uploadImage(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
		List<String> fileNames = UploadUtil.uploadImage(request, false);
		if (fileNames.size() > 0) {
			return new ObjectRestResponse().data(fileNames);
		} else {
			return new ObjectRestResponse().status(ResponseCode.UPLOAD_FILE_IS_NULL);
		}
	}

	// 上传文件(支持批量)
	@RequestMapping("/temp/imageData")
	public ObjectRestResponse uploadImageData(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
		List<String> fileNames = UploadUtil.uploadImageData(request);
		if (fileNames.size() > 0) {
			return new ObjectRestResponse().data(fileNames);
		} else {
			return new ObjectRestResponse().status(ResponseCode.UPLOAD_FILE_IS_NULL);
		}
	}

	// 上传文件(支持批量)
	@RequestMapping("/file")
	public ObjectRestResponse uploadFile2Ftp(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
		List<String> fileNames = UploadUtil.uploadFile(request);
		if (fileNames.size() > 0) {
			List<String> resultList = InstanceUtil.newArrayList();
			for (int i = 0; i < fileNames.size(); i++) {
				String filePath = UploadUtil.getUploadDir(request) + fileNames.get(i);
				String objectId = UUID.randomUUID().toString().replaceAll("-", "");
				String file = UploadUtil.remove2DFS("file", objectId, filePath).getRemotePath();
				resultList.add(file);
			}
			return new ObjectRestResponse().data(fileNames);
		} else {
			return new ObjectRestResponse().status(ResponseCode.UPLOAD_FILE_IS_NULL);
		}
	}

	// 上传文件(和tencent交互)
	@RequestMapping("/tcImageUpload")
	public ObjectRestResponse tcImageUpload(HttpServletRequest request) {
		String uploadType = request.getParameter("uploadType");
		Long numSize = 5l;//默认为5M
		if(!UploadUtil.uploadLimitFileSize(request, numSize)) {
			return new ObjectRestResponse().status(ResponseCode.UPLOAD_FILE_SIZE);
		}
		List<String> fileNames = UploadUtil.uploadImage(request, false);
		if (fileNames.size() > 0) {
			List<String> resultList = InstanceUtil.newArrayList();
			for (int i = 0; i < fileNames.size(); i++) {
				String filePath = UploadUtil.getUploadDir(request) + fileNames.get(i);
				if(!checkFile(filePath)){
					return new ObjectRestResponse().status(ResponseCode.UPLOAD_TYPE_ERROR);
				};
				resultList.add(filePath);
			}
			//上传到sftp服务器
			List<String> resp = UploadUtil.remove2SftpList(resultList, sftpConfig,UploadType.getType(uploadType));
			if(StringUtil.listIsNotBlank(resp)){
				log.info("更新腾讯用户的头像地址:" + resp.get(0));
				TencentSendUtils.setUserImg(resp.get(0));
				String oldFileName = null;
				//如果是上传图像,需要删除旧的头像
				if(UploadType.getType(uploadType) == UploadType.HEAD_PICTURE){
					//更新头像地址,并返回旧的头像地址
					oldFileName = uploadBiz.portraitUpload(resp.get(0));
				}

				if(StringUtils.isNotBlank(oldFileName)){
					//删除旧的图片
					UploadUtil.removeSftpFile(UploadType.getType(uploadType),oldFileName,sftpConfig);
				}
			}
			return new ObjectRestResponse().data(resp);
		} else {
			return new ObjectRestResponse().status(ResponseCode.UPLOAD_FILE_IS_NULL);
		}
	}



	// 上传文件(支持批量)
	@RequestMapping("/imageFtp")
	public ObjectRestResponse uploadImage2Ftp(HttpServletRequest request) {
		String uploadType = request.getParameter("uploadType");
		if(!UploadType.isType(uploadType)){
			return new ObjectRestResponse().status(ResponseCode.UPLOAD_TYPE_ERROR);
		}
		ObjectRestResponse result = null;
		//验证身份认证,判断是否已经认证过了
		if(UploadType.getType(uploadType) == UploadType.ID_CARD_ZM || UploadType.getType(uploadType) == UploadType.ID_CARD_FM){
            result = uploadBiz.idCardUpload(BaseContextHandler.getUserID());
			if(!result.isRel()){
				return result;
			}
		} else if(UploadType.getType(uploadType) == UploadType.MERCHANT_INFO_ZM ||
				UploadType.getType(uploadType) == UploadType.MERCHANT_INFO_FM){
			//验证商户认证
			result = uploadBiz.merchantUploadValid(BaseContextHandler.getUserID(),UploadType.getType(uploadType));
			if(!result.isRel()){
				return result;
			}
		}



		Long numSize = 5l;//默认为5M
		if(!UploadUtil.uploadLimitFileSize(request, numSize)) {
			return new ObjectRestResponse().status(ResponseCode.UPLOAD_FILE_SIZE);
		}
		List<String> fileNames = UploadUtil.uploadImage(request, false);
		if (fileNames.size() > 0) {
			List<String> resultList = InstanceUtil.newArrayList();
			for (int i = 0; i < fileNames.size(); i++) {
				String filePath = UploadUtil.getUploadDir(request) + fileNames.get(i);
				// String objectId = UUID.randomUUID().toString().replaceAll("-", "");
				// String file = UploadUtil.remove2DFS("image", objectId,
				// filePath).getRemotePath();
				if(!checkFile(filePath)){
					return new ObjectRestResponse().status(ResponseCode.UPLOAD_TYPE_ERROR);
				};
				resultList.add(filePath);
			}



			List<String> resp = UploadUtil.remove2SftpList(resultList, sftpConfig,UploadType.getType(uploadType));
			if(StringUtil.listIsNotBlank(resp)){
				String oldFileName = null;
				//如果是上传图像,需要删除旧的头像
				if(UploadType.getType(uploadType) == UploadType.HEAD_PICTURE){
					//更新头像地址,并返回旧的头像地址
					oldFileName = uploadBiz.portraitUpload(resp.get(0));
				}

				if(StringUtils.isNotBlank(oldFileName)){
					//删除旧的图片
					UploadUtil.removeSftpFile(UploadType.getType(uploadType),oldFileName,sftpConfig);
				}
			}



			return new ObjectRestResponse().data(resp);
		} else {
			return new ObjectRestResponse().status(ResponseCode.UPLOAD_FILE_IS_NULL);
		}
	}

    //验证上传文件类型
	private  boolean checkFile(String fileName){
		boolean flag=false;
		String suffixList="jpg,gif,png,bmp,jpeg";
		//获取文件后缀
		String suffix=fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());

		if(suffixList.contains(suffix.trim().toLowerCase())){
			flag=true;
		}
		return flag;
	}



	// 上传文件(支持批量) 上传图片
	@RequestMapping("/imageData")
	public ObjectRestResponse uploadImageData2Ftp(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
		List<String> fileNames = UploadUtil.uploadImageData(request);
		if (fileNames.size() > 0) {
			List<String> resultList = InstanceUtil.newArrayList();
			for (int i = 0; i < fileNames.size(); i++) {
				String filePath = UploadUtil.getUploadDir(request) + fileNames.get(i);
				String objectId = UUID.randomUUID().toString().replaceAll("-", "");
				String file = UploadUtil.remove2DFS("image", objectId, filePath).getRemotePath();
				resultList.add(file);
			}
			return new ObjectRestResponse().data(resultList);
		} else {
			return new ObjectRestResponse().status(ResponseCode.UPLOAD_FILE_IS_NULL);
		}
	}

}
