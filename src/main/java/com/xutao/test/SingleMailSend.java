package com.xutao.test;

import javax.mail.MessagingException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class SingleMailSend {
	// 获取spring容器
	static ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-mail.xml");
	// 从spring容器中获取发送简单邮件的发送策略
	// MailSender,This interface defines a strategy for sending simple mails
	static MailSender sender = (MailSender) context.getBean("mailSender");
	// 从spring容器中获取简单邮件模板包含from, to, cc, subject, and text fields
	// SimpleMailMessage,Models a simple mail message, including data such as the
	// from, to, cc, subject, and text fields
	static SimpleMailMessage mailMessage = (SimpleMailMessage) context.getBean("mailMessage");

	public static void main(String[] args) throws MessagingException {
		mailMessage.setSubject("测试");
		mailMessage.setText("这个一个通过spring框架来发送的邮件");
		mailMessage.setTo("3485408049@qq.com");
		// 发送邮件
		sender.send(mailMessage);
	}
}
