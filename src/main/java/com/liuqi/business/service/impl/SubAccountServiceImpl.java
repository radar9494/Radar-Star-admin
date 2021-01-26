package com.liuqi.business.service.impl;


import com.google.common.base.Preconditions;
import com.liuqi.base.LoginUserTokenHelper;
import com.liuqi.business.enums.UserStatusEnum;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.model.UserModelDto;
import com.liuqi.business.service.UserService;
import com.liuqi.exception.BusinessException;
import com.liuqi.token.RedisTokenManager;
import com.liuqi.utils.ShiroPasswdUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.SubAccountModel;
import com.liuqi.business.model.SubAccountModelDto;


import com.liuqi.business.service.SubAccountService;
import com.liuqi.business.mapper.SubAccountMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SubAccountServiceImpl extends BaseServiceImpl<SubAccountModel,SubAccountModelDto> implements SubAccountService{

	@Autowired
	private SubAccountMapper subAccountMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private RedisTokenManager redisTokenManager;

	@Transactional
	@Override
	public void delete(Long id) {
		subAccountMapper.deleteById(id);
	}

	@Transactional
	@Override
	public void insertModel(Long userId, Long subId, String token) {
		int byUserIdSubId = this.getByUserIdSubId(userId, subId);
        if(byUserIdSubId<=0){
			SubAccountModel model=new SubAccountModel();
			model.setSubId(subId);
			model.setUserId(userId);
			model.setToken(token);
			this.insert(model);
		}

	}

	@Transactional
	@Override
	public String changAccount(Long id,Long userId,String pwd) {
		SubAccountModelDto userAccount= this.getById(id);
		if(!userAccount.getUserId().equals(userId)){
			throw new BusinessException("账号异常!");
		}
		//邮箱不一样需要验证登录密码
		UserModel baseUser=userService.getById(userId);
		UserModel user=userService.getById(userAccount.getSubId());
		if(!baseUser.getEmail().equals(user.getEmail())){
			if (!StringUtils.equalsIgnoreCase(user.getPwd(),
					ShiroPasswdUtil.getUserPwd(pwd))) {
				throw new BusinessException("密码错误!");
			}
		}
		String token=redisTokenManager.getUserTokenByUserId(userAccount.getSubId());
		if(StringUtils.isNotEmpty(token)&&!token.equals(userAccount.getToken())){
			userAccount.setToken(token);
		    this.update(userAccount);
		}
		return userAccount.getToken();
	}

	@Override
	public int getByUserIdSubId(Long userId, Long id) {
		return subAccountMapper.getByUserIdSubId(userId,id);
	}

	@Override
	protected void doMode(SubAccountModelDto dto) {
		super.doMode(dto);
		UserModelDto byId = userService.getById(dto.getSubId());
		dto.setSubName(byId.getName());
		dto.setSubEmail(byId.getEmail());
	}

	@Transactional
	@Override
	public void add(String name, String pwd, Long userId, int time, HttpServletRequest request) {
		UserModel userModel = userService.queryByName(name);
		Preconditions.checkNotNull(userModel,"账号不存在!");
		int count=this.getByUserIdSubId(userId,userModel.getId());
		if(count>0){
			throw new BusinessException("请勿重复添加!");
		}
		if(userModel.getId().equals(userId)){
			throw new BusinessException("不能添加自己!");
		}
		Preconditions.checkArgument(userModel.getStatus().equals(UserStatusEnum.USDING.getCode()),"请先激活!");
		Preconditions.checkArgument(StringUtils.equalsIgnoreCase(userModel.getPwd(),
				ShiroPasswdUtil.getUserPwd(pwd)),"密码错误!");
		String token=redisTokenManager.getToken(userModel,time);

		SubAccountModelDto search=new SubAccountModelDto();
		search.setUserId(userId);
		//其他关联的都要添加
		List<SubAccountModelDto> list = this.queryListByDto(search, false);
        for(SubAccountModelDto dto:list){
			insertModel(dto.getUserId(),userModel.getId(),token);
		}
		insertModel(userId,userModel.getId(),token);
		String token1 = LoginUserTokenHelper.getToken(request);
		insertModel(userModel.getId(),userId,token1);
	}

	@Override
	public BaseMapper<SubAccountModel,SubAccountModelDto> getBaseMapper() {
		return this.subAccountMapper;
	}

}
