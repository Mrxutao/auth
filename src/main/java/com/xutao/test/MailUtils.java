package com.xutao.test;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtils {
	
	
	/**
     * 发送邮件
     * @param host  邮件传送协议服务器
     * @param username  发件人用户名
     * @param password  发件人用户密码
     * @param addr    收件人地址
     * @param subject 邮件主题
     * @param text  邮件内容
     * @throws MessagingException
     */
    public static void sendMail(String host,String username,String password,String addr,String subject,String text) throws MessagingException{
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.auth", "true");
        //创建邮件会话
        Session session = Session.getInstance(properties);
        //构建信息体
        Message message = new MimeMessage(session);
        //发送地址
        InternetAddress address = new InternetAddress(username);
        message.setFrom(address);
        //收件地址
        InternetAddress toAddress = new InternetAddress(addr);
        message.setRecipient(MimeMessage.RecipientType.TO, toAddress);
        //主题
        message.setSubject(subject);
        //正文
        message.setText(text);
        //保存文件
        message.saveChanges();
        //获取实现了SMTP协议的Transport类
        Transport transport = session.getTransport("smtp");
        //连接服务器
        transport.connect(host, username, password);
        //发送邮件给所有收件人
        transport.sendMessage(message, message.getAllRecipients());
        //关闭连接
        transport.close();
    }
}
