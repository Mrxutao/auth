package com.xutao.itrip.service.impl;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.xutao.itrip.service.MailService;
import com.xutao.itrip.utils.RedisAPI;
/**
 * 实现邮箱发送接口的功能
 * @author 许涛
 *
 */
@Service
public class MailServiceImpl implements MailService {
	@Autowired
	private SimpleMailMessage simpleMailMessage;
	@Autowired
	private JavaMailSender javaMailSender;
	@Autowired
	private RedisAPI redisAPI;

	/**
	 * 发送邮箱 把激活码保存到Redis中
	 */
	public void sendMail(String embracer, String activation) {
		simpleMailMessage.setTo(embracer);
		simpleMailMessage.setText("注册邮箱:"+embracer+"激活码:"+activation);//邮件内容
		javaMailSender.send(simpleMailMessage);
		this.saveActivationInfo("activation:"+embracer, activation);//将激活码缓存在Redis中
	}

	/**
	 * 保存激活信息
	 * 
	 * @param key
	 * @param value
	 */
	private void saveActivationInfo(String key, String value) {
		redisAPI.set(key, 30*60, value);
	}
}
