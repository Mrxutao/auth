package com.xutao.itrip.controller;

import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xutao.itrip.beans.dto.Dto;
import com.xutao.itrip.beans.pojo.ItripUser;
import com.xutao.itrip.service.MailService;
import com.xutao.itrip.service.UserService;
import com.xutao.itrip.utils.DtoUtil;
import com.xutao.itrip.utils.ErrorCode;
import com.xutao.itrip.utils.MD5;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Controller
@RequestMapping("api")
public class UserController {

	@Resource
	private UserService userService;
	@Resource
	private MailService mailService;

	/**
	 * 验证用户名唯一性
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/ckuser", method = RequestMethod.GET, produces = "application/json")
	@ApiOperation(value = "用户名验证唯一", httpMethod = "GET", protocols = "HTTP", produces = "application/json", response = Dto.class, notes = "用户名验证唯一")
	public @ResponseBody Dto ckuser(
			@ApiParam(name = "name", value = "被检查的用户名", defaultValue = "test@bdqn.cn") @RequestBody String name)
			throws Exception {

		ItripUser user = userService.findUserName(name);
		System.out.println("姓名:" + user.getUserName());
		if (user != null) {
			return DtoUtil.returnFail("用户名已存在,注册失败", ErrorCode.AUTH_USER_ALREADY_EXISTS);
		} else {
			return DtoUtil.returnFail("用户名可以注册", ErrorCode.AUTH_UNKNOWN);
		}
	}

	/**
	 * 使用邮箱注册
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/register/{user}", method = RequestMethod.POST, produces = "application/json")
	@ApiOperation(value = "注册", httpMethod = "POST", protocols = "HTTP", produces = "application/json", response = Dto.class, notes = "注册")
	public @ResponseBody Dto register(
			@ApiParam(name = "ItripUser", value = "用户实体", required = true) @RequestBody ItripUser user)
			throws Exception {
		// 将用户注册信息保存数据库中
		if (!validEmail(user.getUserCode()))
			return DtoUtil.returnFail("请使用正确的邮箱地址注册", ErrorCode.AUTH_ILLEGAL_USERCODE);
		try {
			ItripUser u = new ItripUser();
			u.setUserCode(user.getUserCode());
			u.setUserName(user.getUserName());
			if (null == userService.findUserName(user.getUserCode())) {
				u.setUserPassword(MD5.getMd5(user.getUserPassword(), 32));
				userService.insertUser(u);
				return DtoUtil.returnSuccess();
			} else {
				return DtoUtil.returnFail("用户名已存在", ErrorCode.AUTH_USER_ALREADY_EXISTS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_UNKNOWN);
		}
	}

	@ApiOperation(value = "邮箱注册用户激活", httpMethod = "GET", protocols = "HTTP", produces = "application/json", response = Dto.class, notes = "邮箱激活")
	@RequestMapping(value = "/activate", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody Dto activate(
			@ApiParam(name = "user", value = "注册邮箱地址", defaultValue = "test@bdqn.cn") @RequestBody String user,
			@ApiParam(name = "code", value = "激活码", defaultValue = "018f9a8b2381839ee6f40ab2207c0cfe")@RequestBody String code) {
		try {
			if (userService.activate(user, code)) {
				return DtoUtil.returnSuccess("激活成功");
			} else {
				return DtoUtil.returnSuccess("激活失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return DtoUtil.returnFail("激活失败", ErrorCode.AUTH_ACTIVATE_FAILED);
		}
	}

	private boolean validEmail(String email) {
		String regex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		return Pattern.compile(regex).matcher(email).find();
	}
	

	@RequestMapping(value = "/registerByPhone", method = RequestMethod.POST, produces = "application/json")
	@ApiOperation(value = "使用手机注册", httpMethod = "POST", protocols = "HTTP", produces = "application/json", response = Dto.class, notes = "使用手机注册")
	public @ResponseBody Dto registerByPhone(
			@ApiParam(name = "user", value = "用户实体", required = true) @RequestBody ItripUser user) {
		if (!validPhone(user.getUserCode()))
			return DtoUtil.returnFail("请使用正确的手机号码注册", ErrorCode.AUTH_ILLEGAL_USERCODE);
		try {
			ItripUser u = new ItripUser();
			u.setUserCode(user.getUserCode());
			u.setUserPassword(user.getUserPassword());
			u.setUserType(user.getUserType());
			u.setUserName(user.getUserName());
			if (null == userService.findUserName(user.getUserCode())) {
				u.setUserPassword(MD5.getMd5(user.getUserPassword(), 32));
				userService.itriptxCreateUserByPhone(u);
				return DtoUtil.returnSuccess();
			} else {
				return DtoUtil.returnFail("用户已存在", ErrorCode.AUTH_USER_ALREADY_EXISTS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_UNKNOWN);
		}
	}

	public boolean validPhone(String phone) {
		String regex = "^1[3578]{1}\\d{9}$";
		return Pattern.compile(regex).matcher(phone).find();
	}

	@ApiOperation(value = "手机注册用户用户短信验证", httpMethod = "PUT", protocols = "HTTP", produces = "application/json", response = Dto.class, notes = "手机短信注册")
	@RequestMapping(value = "/validatePhone", method = RequestMethod.PUT, produces = "application/json")
	public @ResponseBody Dto validatePhone(
			@ApiParam(name = "user", value = "实体", defaultValue = "13873568013") String user,
			@ApiParam(name = "code", value = "验证码", defaultValue = "6666") @RequestBody String code) {
		try {
			if (userService.validatePhone(user, code)) {
				return DtoUtil.returnSuccess("验证成功");
			} else {
				return DtoUtil.returnSuccess("验证失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return DtoUtil.returnFail("验证失败", ErrorCode.AUTH_ACTIVATE_FAILED);
		}

	}

}