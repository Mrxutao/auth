package com.xutao.itrip.controller;

import java.util.Calendar;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xutao.itrip.beans.dto.Dto;
import com.xutao.itrip.beans.pojo.ItripUser;
import com.xutao.itrip.beans.vo.ItripTokenVO;
import com.xutao.itrip.exception.UserLoginFailedException;
import com.xutao.itrip.service.TokenService;
import com.xutao.itrip.service.UserService;
import com.xutao.itrip.utils.DtoUtil;
import com.xutao.itrip.utils.EmptyUtils;
import com.xutao.itrip.utils.ErrorCode;
import com.xutao.itrip.utils.MD5;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Controller
@RequestMapping(value = "api")
public class LoginController {

	@Resource
	private UserService userService;
	@Resource
	private TokenService tokenService;

	@ApiOperation(value = "用户登录", httpMethod = "POST", protocols = "HTTP", produces = "application/json", response = Dto.class, notes = "根据用户名、密码进行统一认证")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "form", required = true, value = "用户名", name = "name", defaultValue = "yao.liu2015@bdqn.cn"),
			@ApiImplicitParam(paramType = "form", required = true, value = "密码", name = "password", defaultValue = "111111"), })
	@RequestMapping(value = "/dologin", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody Dto dologin(@ApiParam String name, @ApiParam String password, HttpServletRequest request) {

		if (!EmptyUtils.isEmpty(name) && !EmptyUtils.isEmpty(password)) {
			ItripUser user = null;
			try {
				user = userService.UserLogin(name.trim(), MD5.getMd5(password.trim(), 32));
			} catch (UserLoginFailedException e) {
				return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_AUTHENTICATION_FAILED);
			} catch (Exception e) {
				e.printStackTrace();
				return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_UNKNOWN);
			}
			if (EmptyUtils.isNotEmpty(user)) {// 登录成功
				String token = tokenService.generateToken(request.getHeader("user-agent"), user);
				tokenService.save(token, user);

				// 返回ItripTokenVO
				ItripTokenVO tokenVO = new ItripTokenVO(token,
						Calendar.getInstance().getTimeInMillis() + TokenService.SESSION_TIMEOUT * 1000, // 2h有效期
						Calendar.getInstance().getTimeInMillis());

				return DtoUtil.returnDataSuccess(tokenVO);
			} else {
				return DtoUtil.returnFail("用户名或密码错误", ErrorCode.AUTH_AUTHENTICATION_FAILED);
			}
		} else {
			return DtoUtil.returnFail("参数错误！检查提交的参数名称是否正确。", ErrorCode.AUTH_PARAMETER_ERROR);
		}
	}
}
