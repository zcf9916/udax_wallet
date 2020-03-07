package com.github.wxiaoqi.security.common.util.ftp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.wxiaoqi.security.common.configuration.SftpConfiguration;
import com.github.wxiaoqi.security.common.exception.FtpException;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * Java Secure Channel
 * 
 * @author ShenHuaJie
 * @version 2016年5月20日 下午3:19:19
 */
public class SftpClient {
    private Logger logger = LogManager.getLogger();
    private Session session = null;
    private ChannelSftp channel = null;

    private static SftpClient client = null;

    private SftpClient() {
    }

    public static  SftpClient connect(SftpConfiguration config) {
//        if(client != null){
//            return client;
//        }
//        client = new SftpClient();
//        client.init(config);
//        return client;
        return  new SftpClient().init(config);
    }


    public SftpClient init(SftpConfiguration config) {
        try {
            String host = config.getHost();
            int port = config.getPort();
            String userName = config.getUserName();
            String password = config.getPassword();
            Integer timeout = config.getTimeout();
            Integer aliveMax = config.getAliveMax();
            JSch jsch = new JSch(); // 创建JSch对象
            session = jsch.getSession(userName, host, port); // 根据用户名，主机ip，端口获取一个Session对象
            if (password != null) {
                session.setPassword(password); // 设置密码
            }
            session.setConfig("StrictHostKeyChecking", "no"); // 为Session对象设置properties
            if (timeout != null) session.setTimeout(timeout); // 设置timeout时间
            if (aliveMax != null) session.setServerAliveCountMax(aliveMax);
            session.connect(); // 通过Session建立链接
            channel = (ChannelSftp)session.openChannel("sftp"); // 打开SFTP通道
            channel.connect(); // 建立SFTP通道的连接
            logger.info("SSH Channel connected.");
        } catch (JSchException e) {
            throw new FtpException("", e);
        }
        return this;
    }

    public void disconnect() {
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
            logger.info("SSH Channel disconnected.");
        }
    }

    /** 发送文件 */
    public void put(String src, String dst) {
        try {
            channel.put(src, dst, new FileProgressMonitor());
        } catch (SftpException e) {
            throw new FtpException("", e);
        }
    }

    /** 获取文件 */
    public void get(String src, String dst) {
        try {
            channel.get(src, dst, new FileProgressMonitor());
        } catch (SftpException e) {
            throw new FtpException("", e);
        }
    }

    /**
     * 获取客户端
     */
    public ChannelSftp getChannel(){
        return channel;
    }
}
