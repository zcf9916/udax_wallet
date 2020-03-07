package com.github.wxiaoqi.security.common.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.github.wxiaoqi.security.common.config.Resources;
import com.github.wxiaoqi.security.common.configuration.SftpConfiguration;
import com.github.wxiaoqi.security.common.enums.UploadType;
import com.github.wxiaoqi.security.common.util.ftp.SftpClient;
import com.github.wxiaoqi.security.common.util.model.FileInfo;
import com.github.wxiaoqi.security.common.util.model.FileManager;
import com.github.wxiaoqi.security.common.util.model.FileModel;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 上传辅助类 与Spring.multipartResolver冲突
 *
 * @author ShenHuaJie
 */
public final class UploadUtil {
	private UploadUtil() {
	}

	private static final Logger logger = LogManager.getLogger();

	/**
	 * 上传文件缓存大小限制
	 */
	private static int fileSizeThreshold = 1024 * 1024 * 1;
	/**
	 * 上传文件临时目录
	 */
	private static final String uploadFileDir = "/WEB-INF/upload/";

	/**
	 * 获取所有文本域
	 */
	public static final List<?> getFileItemList(HttpServletRequest request, File saveDir) throws FileUploadException {
		if (!saveDir.isDirectory()) {
			saveDir.mkdir();
		}
		List<?> fileItems = null;
		RequestContext requestContext = new ServletRequestContext(request);
		if (FileUpload.isMultipartContent(requestContext)) {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setRepository(saveDir);
			factory.setSizeThreshold(fileSizeThreshold);
			ServletFileUpload upload = new ServletFileUpload(factory);
			fileItems = upload.parseRequest(request);
		}
		return fileItems;
	}

	/**
	 * 获取文本域
	 */
	public static final FileItem[] getFileItem(HttpServletRequest request, File saveDir, String... fieldName)
			throws FileUploadException {
		if (fieldName == null || saveDir == null) {
			return null;
		}
		List<?> fileItemList = getFileItemList(request, saveDir);
		FileItem fileItem = null;
		FileItem[] fileItems = new FileItem[fieldName.length];
		for (int i = 0; i < fieldName.length; i++) {
			for (Iterator<?> iterator = fileItemList.iterator(); iterator.hasNext();) {
				fileItem = (FileItem) iterator.next();
				// 根据名字获得文本域
				if (fieldName[i] != null && fieldName[i].equals(fileItem.getFieldName())) {
					fileItems[i] = fileItem;
					break;
				}
			}
		}
		return fileItems;
	}

	/**
	 * 上传文件处理(支持批量)
	 */
	public static List<String> uploadFile(HttpServletRequest request) {
		List<String> fileNames = InstanceUtil.newArrayList();
		List<FileInfo> files = uploadFiles(request);
		for (FileInfo fileInfo : files) {
			fileNames.add(fileInfo.getFileName());
		}
		return fileNames;
	}

	/**
	 * 上传文件处理(支持批量)
	 */
	public static List<FileInfo> uploadFiles(HttpServletRequest request) {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		List<FileInfo> fileNames = InstanceUtil.newArrayList();
		if (multipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			String pathDir = getUploadDir(request);
			File dirFile = new File(pathDir);
			if (!dirFile.isDirectory()) {
				dirFile.mkdirs();
			}
			for (Iterator<String> iterator = multiRequest.getFileNames(); iterator.hasNext();) {
				String key = iterator.next();
				MultipartFile multipartFile = multiRequest.getFile(key);
				if (multipartFile != null) {
					FileInfo fileInfo = new FileInfo();
					String name = multipartFile.getOriginalFilename();
					fileInfo.setOrgName(name);
					if (name.indexOf(".") == -1 && "blob".equals(name)) {
						name = name + ".png";
					}
					String uuid = UUID.randomUUID().toString();
					String postFix = name.substring(name.lastIndexOf(".")).toLowerCase();
					String fileName = uuid + postFix;
					String filePath = pathDir + File.separator + fileName;
					File file = new File(filePath);
					file.setWritable(true, false);
					fileInfo.setFileSize(multipartFile.getSize());
					try {
						multipartFile.transferTo(file);
						fileInfo.setFileName(fileName);
						fileNames.add(fileInfo);
					} catch (Exception e) {
						logger.error(name + "保存失败", e);
					}
				}
			}
		}
		return fileNames;
	}

	/**
	 * 上传文件处理(支持批量)
	 */
	public static List<String> uploadImage(HttpServletRequest request, boolean lessen) {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		List<String> fileNames = InstanceUtil.newArrayList();
		if (multipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			String pathDir = getUploadDir(request);
			File dirFile = new File(pathDir);
			if (!dirFile.isDirectory()) {
				dirFile.mkdirs();
			}
			for (Iterator<String> iterator = multiRequest.getFileNames(); iterator.hasNext();) {
				String key = iterator.next();
				// 获取数据map值下的元素集合
				List<MultipartFile> multipartFileList = multiRequest.getMultiFileMap().get(key);
				for (MultipartFile multipartFile : multipartFileList) {
					if (multipartFile != null) {
						String name = multipartFile.getOriginalFilename();
						if (name.indexOf(".") == -1 && "blob".equals(name)) {
							name = name + ".png";
						}
						String uuid = UUID.randomUUID().toString();
						String postFix = name.substring(name.lastIndexOf(".")).toLowerCase();
						String fileName = uuid + postFix;
						String filePath = pathDir + File.separator + fileName;
						File file = new File(filePath);
						file.setWritable(true, false);
						try {
							multipartFile.transferTo(file);
							fileNames.add(fileName);
							file.length();
							System.out.println("文件大小:"+file.length());
						} catch (Exception e) {
							logger.error(name + "保存失败", e);
						}
						if (lessen) {
							try { // 缩放
								BufferedImage bufferedImg = ImageIO.read(file);
								int orgwidth = bufferedImg.getWidth();// 原始宽度
								ImageUtil.scaleWidth(file, 100);
								if (orgwidth > 300) {
									ImageUtil.scaleWidth(file, 300);
								}
								if (orgwidth > 500) {
									ImageUtil.scaleWidth(file, 500);
								}
							} catch (Exception e) {
								logger.error(name + "缩放失败", e);
							}
						}
					}
				}
			}
		}
		return fileNames;
	}

	/**
	 * 上传图片文件大小限制
	 * num 单位M
	 */	
	public static Boolean uploadLimitFileSize(HttpServletRequest request, Long num) {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		if (multipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			for (Iterator<String> iterator = multiRequest.getFileNames(); iterator.hasNext();) {
				String key = iterator.next();
				// 获取数据map值下的元素集合
				List<MultipartFile> multipartFileList = multiRequest.getMultiFileMap().get(key);
				for (MultipartFile multipartFile : multipartFileList) {
					if (multipartFile != null) {
						if (multipartFile.getSize()>num*1024*1024) {
							return false;
							//throw  new IllegalArgumentException("UPLOAD_SIZE_MAX");
						}
					}
				}
			}
		}
		return true;
	}
	
	public static List<String> uploadImageData(HttpServletRequest request) {
		List<String> fileNames = InstanceUtil.newArrayList();
		Enumeration<String> params = request.getParameterNames();
		String pathDir = getUploadDir(request);
		File dir = new File(pathDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		while (params.hasMoreElements()) {
			String key = params.nextElement();
			String fileStr = request.getParameter(key);
			if (fileStr != null && !"".equals(fileStr)) {
				int index = fileStr.indexOf("base64");
				if (index > 0) {
					try {
						String fileName = UUID.randomUUID().toString();
						String preStr = fileStr.substring(0, index + 7);
						String prefix = preStr.substring(preStr.indexOf("/") + 1, preStr.indexOf(";")).toLowerCase();
						fileStr = fileStr.substring(fileStr.indexOf(",") + 1);
						byte[] bb = Base64.getDecoder().decode(fileStr);
						for (int j = 0; j < bb.length; ++j) {
							if (bb[j] < 0) {// 调整异常数据
								bb[j] += 256;
							}
						}
						String distPath = pathDir + fileName + "." + prefix;
						OutputStream out = new FileOutputStream(distPath);
						out.write(bb);
						out.flush();
						out.close();
						fileNames.add(fileName + "." + prefix);
					} catch (Exception e) {
						logger.error("上传文件异常：", e);
					}
				}
			}
		}
		return fileNames;
	}

	/**
	 * 获取上传文件临时目录
	 */
	public static String getUploadDir(HttpServletRequest request) {
		return request.getServletContext().getRealPath(uploadFileDir) + File.separator;
	}

	/**
	 * 移动文件到fastDFS
	 */
	public static FileModel remove2DFS(String namespace, String objectId, String fileName) {
		FileModel fastDFSFile = new FileModel(namespace, objectId, fileName);
		if (fastDFSFile.getKey() != null) {
			FileManager.getInstance().upload(fastDFSFile);
		}
		return fastDFSFile;
	}

	/**
	 * 移动文件到SFTP namespace为空字符串或者null时，将把图片添加到默认路径
	 */
	public static String remove2Sftp(String filePath, String namespace, SftpConfiguration config) {
		File file = new File(filePath);
		if (!file.exists()) {
			throw new RuntimeException("文件" + filePath + "不存在");
		}
		SftpClient client = SftpClient.connect(config);
		// String dir = PropertiesUtil.getString("sftp.baseDir");
		/*
		 * 注意，sftp的传输必须先进入指定好的权限路径，不然传输会失败 新建的目录只能向下创建，不能向上，否则会异常，关于权限路径，请联系linux管理员
		 */
		// 设置临时路径
		String spacePath = "";
		try {
			// 进入默认路径,当此目录不存在，整个传输会异常,注意linux系统管理员必须配置好此路径
			client.getChannel().cd("upload");
			// 进入自定义目录
			spacePath = namespace + File.separator + DateUtil.getDateTime(DateUtil.DATE_PATTERN.YYYYMMDD);
			// 设置完整路径
			if (client.getChannel().stat(spacePath) != null) {
				client.getChannel().cd(spacePath);
			}
		} catch (SftpException sException) {
			String[] paths = spacePath.split(File.separator);
			for (String folder : paths) {
				try {
					if (client.getChannel().stat(folder) != null) {
						client.getChannel().cd(folder);
					} else {
						client.getChannel().mkdir(folder);
						client.getChannel().cd(folder);
					}
				} catch (SftpException e) {
					try {
						client.getChannel().mkdir(folder);
						client.getChannel().cd(folder);
					} catch (SftpException e1) {
						logger.error(Resources.getMessage("SFTP_DIR_NOTEXITS"));
					}
				}
			}
		}
		// String fileName = namespace + File.separator + file.getName();

		String fileName = file.getName();// 获取文件名
		// 图片处理
		/*try {
			// 默认以0.25倍压缩
			Thumbnails.of(file).scale(1f).toFile(file);
		} catch (IOException e) {
			logger.error("sftp传输图片异常,路径:" + file.getPath());
			logger.error("sftp传输图片压缩异常:" + e);
		}*/
		client.put(file.getPath(), fileName);
		client.disconnect();
		return PropertiesUtil.getString("sftp.nginx.path") + spacePath + File.separator + fileName;
	}


	public  static void removeSftpFile(UploadType uploadType,String fileName, SftpConfiguration config){
		try{
			String realFileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
			SftpClient client = SftpClient.connect(config);
			client.getChannel().cd("upload");
			client.getChannel().cd(uploadType.path());
			client.getChannel().rm(realFileName);
		}catch (Exception e){
			logger.error(e.getMessage(),e);
		}
	}

	/**
	 * 移动文件到SFTP 批量操作,避免多余的连接 namespace为空字符串或者null时，将把图片添加到默认路径
	 */
	public static List<String> remove2SftpList(List<String> filePathList, SftpConfiguration config, UploadType uploadType) {
		// 检查文件是否存在
		List<File> files = InstanceUtil.newArrayList();
		for (String filePath : filePathList) {
			File file = new File(filePath);
			if (!file.exists()) {
				throw new RuntimeException("文件" + filePath + "不存在");
			}
			files.add(file);
		}

		SftpClient client = SftpClient.connect(config);
		// String dir = PropertiesUtil.getString("sftp.baseDir");
		/*
		 * 注意，sftp的传输必须先进入指定好的权限路径，不然传输会失败 新建的目录只能向下创建，不能向上，否则会异常，关于权限路径，请联系linux管理员
		 */
		// 设置临时路径
		String spacePath =  File.separator + uploadType.path();
		try {
			// 进入默认路径,当此目录不存在，整个传输会异常,注意linux系统管理员必须配置好此路径
			//client.getChannel().cd("upload");
//			// 进入自定义目录
			//spacePath = uploadType.path() + File.separator + DateUtil.getDateTime(DateUtil.DATE_PATTERN.YYYYMMDD);
			SftpATTRS attrs = null;
			try{
				attrs = client.getChannel().stat("upload");
				client.getChannel().cd("upload");
			}catch (Exception e){
				logger.error(e.getMessage(),e);
			}
			if(attrs == null){
				client.getChannel().mkdir("upload");
				client.getChannel().cd("upload");
			}


			attrs = null;
			try{
				attrs = client.getChannel().stat(uploadType.path());
				client.getChannel().cd(uploadType.path());
			}catch (Exception e){
                logger.error(e.getMessage(),e);
			}
			if(attrs == null){
				client.getChannel().mkdir(uploadType.path());
				client.getChannel().cd(uploadType.path());
			}
		} catch (SftpException sException) {
			logger.error(sException.getMessage(),sException);
			return InstanceUtil.newArrayList();
//			String[] paths = spacePath.split(File.separator);
//			for (String folder : paths) {
//				try {
//					if (client.getChannel().stat(folder) != null) {
//						client.getChannel().cd(folder);
//					} else {
//						client.getChannel().mkdir(folder);
//						client.getChannel().cd(folder);
//					}
//				} catch (SftpException e) {
//					try {
//						client.getChannel().mkdir(folder);
//						client.getChannel().cd(folder);
//					} catch (SftpException e1) {
//						logger.error(Resources.getMessage("SFTP_DIR_NOTEXITS"));
//					}
//				}
//			}
		}
		// String fileName = namespace + File.separator + file.ge3tName();
		// 设置返回集合
		List<String> fileNameList = InstanceUtil.newArrayList();
		for (File file : files) {
			String fileName = file.getName();// 获取文件名
			// 图片处理
			try {
				// 默认以0.25倍压缩
				Thumbnails.of(file).scale(1f).toFile(file);
				client.put(file.getPath(), fileName);
				fileNameList.add(config.getNginxPath() + spacePath + File.separator + fileName);
			} catch (IOException e) {
				logger.error("sftp传输图片异常,路径:" + file.getPath());
				logger.error("sftp传输图片压缩异常:" + e);
				return null;
			}
		}
		// 断开
		client.disconnect();
		return fileNameList;
	}

}
