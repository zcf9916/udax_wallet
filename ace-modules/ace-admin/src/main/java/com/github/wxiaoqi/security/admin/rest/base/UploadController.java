package com.github.wxiaoqi.security.admin.rest.base;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.wxiaoqi.security.common.configuration.SftpConfiguration;
import com.github.wxiaoqi.security.common.enums.ResponseCode;
import com.github.wxiaoqi.security.common.enums.UploadType;
import com.github.wxiaoqi.security.common.msg.ObjectRestResponse;
import com.github.wxiaoqi.security.common.util.InstanceUtil;
import com.github.wxiaoqi.security.common.util.UploadUtil;

/**
 * 文件上传控制器
 * 
 * @author ShenHuaJie
 * @version 2016年5月20日 下午3:11:42
 */
@RestController
@RequestMapping(value = "/admin/upload", method = RequestMethod.POST)
public class UploadController {

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

	// 上传文件(支持批量)
	@RequestMapping("/imageFtp")
	public ObjectRestResponse uploadImage2Ftp(HttpServletRequest request) {
		String uploadType = request.getParameter("uploadType");
		uploadType = StringUtils.isNotBlank(uploadType) ?uploadType:UploadType.ADMIN_PICTURE.value().toString();//后台默认ADMIN_PICTURE
		if(!UploadType.isType(uploadType)){
			return new ObjectRestResponse().status(ResponseCode.UPLOAD_TYPE_ERROR);
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
				resultList.add(filePath);
			}

			List<String> resp = UploadUtil.remove2SftpList(resultList, sftpConfig,UploadType.getType(uploadType));

			return new ObjectRestResponse().data(resp);
		} else {
			return new ObjectRestResponse().status(ResponseCode.UPLOAD_FILE_IS_NULL);
		}
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
