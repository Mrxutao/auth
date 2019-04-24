package com.xutao.itrip.service;

import com.xutao.itrip.beans.pojo.ItripUser;

public interface UserService {

	/**
	 * 检查用户名是否存在
	 * 
	 * @param userCode
	 * @return
	 * @throws Exception
	 */
	public ItripUser findUserName(String userCode) throws Exception;

	/**
	 * 需求:邮箱注册
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public int insertUser(ItripUser user) throws Exception;

	/**
	 * 需求:更新用户信息
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public void updateUser(ItripUser user) throws Exception;

	/**
	 * 需求:登录
	 * 
	 * @param userCode
	 * @param userPassword
	 * @return
	 * @throws Exception
	 */
	public ItripUser UserLogin(String userCode, String userPassword) throws Exception;

	/**
	 * 邮箱激活
	 * 
	 * @param email
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public boolean activate(String email, String code) throws Exception;

	/**
	 * 使用手机号创建用户账号
	 * 
	 * @param user
	 * @throws Exception
	 */
	public void itriptxCreateUserByPhone(ItripUser user) throws Exception;

	/**
	 * 短信验证手机号
	 * 
	 * @param phoneNumber
	 * @return
	 * @throws Exception
	 */
	public boolean validatePhone(String phoneNumber, String code) throws Exception;
	
	

}
