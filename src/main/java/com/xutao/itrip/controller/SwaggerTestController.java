package com.xutao.itrip.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xutao.itrip.beans.dto.Dto;
import com.xutao.itrip.beans.pojo.ItripUser;
import com.xutao.itrip.utils.DtoUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Controller
@RequestMapping(value="/api")
@Api(value="测试Swagger")
public class SwaggerTestController {
	
	@RequestMapping(value = "/test/{id}", method = RequestMethod.GET, produces = "application/json")
	@ApiOperation(value = "用户查询", httpMethod = "GET", protocols = "HTTP", produces = "application/json", response = Dto.class, notes = "根据用户Id查询用户对象")
	public @ResponseBody Dto<ItripUser> test(@ApiParam(required = true, name = "id", value = "用户ID") @PathVariable Long id) {
		ItripUser user = new ItripUser();
		user.setId(id);
		user.setUserName("T220");
		return DtoUtil.returnDataSuccess(user);
	}
	
}
