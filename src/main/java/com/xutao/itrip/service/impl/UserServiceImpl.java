package com.xutao.itrip.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.xutao.itrip.beans.pojo.ItripUser;
import com.xutao.itrip.exception.UserLoginFailedException;
import com.xutao.itrip.mapper.ItripUserMapper;
import com.xutao.itrip.service.MailService;
import com.xutao.itrip.service.SmsService;
import com.xutao.itrip.service.UserService;
import com.xutao.itrip.utils.EmptyUtils;
import com.xutao.itrip.utils.MD5;
import com.xutao.itrip.utils.RedisAPI;

@Service
public class UserServiceImpl implements UserService {

	private Logger logger = Logger.getLogger(UserServiceImpl.class);
	
	@Resource
	private ItripUserMapper itripUserMapper;
	@Resource
	private MailService mailService;
	@Resource
	private RedisAPI redisAPI;
	@Resource
	private SmsService sesService;
	private int expire = 1;// 过期时间（分钟）
	/**
	 * 用户名唯一性
	 * @throws Exception 
	 */
	public ItripUser findUserName(String userCode) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userCode", userCode);
		List<ItripUser>list=itripUserMapper.getItripUserListByMap(map);
		if(list.size()>0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 需求:注册用户
	 */
	public int insertUser(ItripUser user) throws Exception {
		//生成随机的激活码
		String activetionCode=MD5.getMd5(new Date().toLocaleString(), 32);
		//发送邮件(缓存redis)
		mailService.sendMail(user.getUserCode(), activetionCode);
		int i = itripUserMapper.insertItripUser(user);
		return i;
	}

	/**
	 * 需求:登录
	 */
	public ItripUser UserLogin(String userCode, String userPassword) throws Exception {
		ItripUser user=this.findUserName(userCode);
		if(null!=user&&user.getUserPassword().equals(userPassword)) {
			if(user.getActivated()!=1) {
				throw new UserLoginFailedException("用户未激活");
			}
			return user;
		}
		return null;
	}

	/**
	 * 需求:邮箱激活
	 */
	@Override
	public boolean activate(String email, String code) throws Exception {
		String key = "activation:" + email;
		if (redisAPI.exist(key)) {//判断是否存在，不存在，过期或未注册
			if (redisAPI.get(key).equals(code)) {
				ItripUser user = this.findUserName(email);
				if (EmptyUtils.isNotEmpty(user)) {
					logger.debug("激活用户" + email);
					user.setActivated(1);// 激活用户
					user.setUserType(0);// 自注册用户
					user.setFlatID(user.getId());
					itripUserMapper.updateItripUser(user);
					return true;
				}
			}
		}
		return false;
	}
		
	

	/**
	 * 需求:更新用户信息
	 */
	public void updateUser(ItripUser user) throws Exception {
		 itripUserMapper.updateItripUser(user);
	}

	/**
	 * 需求:使用手机号验证
	 * @param user
	 * @throws Exception
	 */
	public void itriptxCreateUserByPhone(ItripUser user) throws Exception {
		//发送短信
		String code=String.valueOf(MD5.getRandomCode());
		sesService.send(user.getUserCode(), "1", new String[] {code,String.valueOf(expire)});
		//缓存验证码
		String key="activation:"+user.getUserCode();
		redisAPI.set(key, expire * 60, code);
		//保存用户信息
		itripUserMapper.insertItripUser(user);
	}

	@Override
	public boolean validatePhone(String phoneNumber, String code) throws Exception {
		String key="activation"+phoneNumber;
		if(redisAPI.exist(key)) {
			if(redisAPI.get(key).equals(code)) {
				ItripUser user=this.findUserName(phoneNumber);
				if(EmptyUtils.isNotEmpty(user)) {
					logger.debug("激活用户" + phoneNumber);
					user.setActivated(1);
					user.setUserType(0);// 自注册用户
					user.setFlatID(user.getId());
					itripUserMapper.updateItripUser(user);
					return true;
				}
			}
		}
		return false;
	}
	

	
}
