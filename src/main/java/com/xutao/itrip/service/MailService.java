package com.xutao.itrip.service;

public interface MailService {
	
	/**
	 * 邮箱发送接口
	 * @param embracer
	 * @param activation
	 */
	public void sendMail(String embracer,String activation);
	
}
