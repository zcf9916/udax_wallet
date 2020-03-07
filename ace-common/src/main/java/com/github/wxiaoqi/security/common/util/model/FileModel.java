package com.github.wxiaoqi.security.common.util.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.UUID;

import javax.activation.MimetypesFileTypeMap;

import lombok.Data;

/**
 * @author ShenHuaJie
 * @version 2016年6月27日 上午9:50:51
 */
@SuppressWarnings("serial")
@Data
public class FileModel implements Serializable {
    private String namespace;
    private String objectId;
    private String key;

    private byte[] content;
    private String ext;

    private String mime;
    private String size;
    private String filename;

    private String remotePath;

    public FileModel(String namespace, String objectId, String filePath) {
        this.namespace = namespace;
        this.objectId = objectId;
        if (filePath != null && !"".equals(filePath.trim())) {
            this.ext = filePath.substring(filePath.lastIndexOf(".") + 1);
            InputStream is = null;
            byte[] file_buff = null;
            FileInputStream fileInputStream = null;
            try {
                File file = new File(filePath);
                this.size = String.valueOf(file.length());
                this.filename = file.getName();

                fileInputStream = new FileInputStream(file);
                if (fileInputStream != null) {
                    int len = fileInputStream.available();
                    file_buff = new byte[len];
                    fileInputStream.read(file_buff);
                }
                this.content = file_buff;
                is = getClass().getResourceAsStream("/config/mime.types");
                MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap(is);
                this.mime = mimetypesFileTypeMap.getContentType(filename);
                this.key = UUID.randomUUID().toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

}
