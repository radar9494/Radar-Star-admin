package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.TaskInfo;
import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.mapper.RobotMapper;
import com.liuqi.business.model.CurrencyTradeModelDto;
import com.liuqi.business.model.RobotModel;
import com.liuqi.business.model.RobotModelDto;
import com.liuqi.business.service.CurrencyTradeService;
import com.liuqi.business.service.RobotService;
import com.liuqi.business.service.TaskService;
import com.liuqi.business.service.UserService;
import com.liuqi.jobtask.RobotJob;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class RobotServiceImpl extends BaseServiceImpl<RobotModel,RobotModelDto> implements RobotService {

	@Autowired
	private RobotMapper robotMapper;
	@Autowired
	private TaskService taskService;
	@Autowired
	private UserService userService;
	@Autowired
	private CurrencyTradeService currencyTradeService;
	@Autowired
	private RedisRepository redisRepository;
	@Override
	public BaseMapper<RobotModel,RobotModelDto> getBaseMapper() {
		return this.robotMapper;
	}
	@Override
	public RobotModelDto getById(Long id) {
		String key= KeyConstant.KEY_ROBOT_ID+id;
		RobotModelDto model=redisRepository.getModel(key);
		if(model==null){
			model=robotMapper.getById(id);
			if(model!=null){
				this.doMode(model);
				redisRepository.set(key,model,3L, TimeUnit.DAYS);
			}
		}
		return model;
	}

	@Override
	public void cleanCacheByModel(RobotModel robotModel) {
		super.cleanCacheByModel(robotModel);
		String key= KeyConstant.KEY_ROBOT_ID+robotModel.getId();
		redisRepository.del(key);
	}

	@Override
	@Transactional
	public void removeById(Long id) {
		robotMapper.removeById(id);
		String key= KeyConstant.KEY_ROBOT_ID+id;
		redisRepository.del(key);
	}

	@Override
	@Transactional
	public void pause(Long id) {
		RobotModel robot = this.getById(id);
		robot.setBuySwitch(SwitchEnum.OFF.getCode());
		robot.setSellSwitch(SwitchEnum.OFF.getCode());
		this.update(robot);
		taskService.pause(RobotJob.class.getName(), "robot" + id);
		addLogger();
	}

	@Override
	@Transactional
	public void resume(Long id) {
		taskService.resume(RobotJob.class.getName(), "robot" + id);
		addLogger();
	}

	@Override
	@Transactional
	public void afterAddOperate(RobotModel robotModel) {
		CurrencyTradeModelDto trade=currencyTradeService.getById(robotModel.getTradeId());
		Long id=robotModel.getId();
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		long base=robotModel.getTradeId()%9;
		String corn=base+"/"+robotModel.getInterval()+" * * * * ?";
		taskService.addMyJob(RobotJob.class.getName(), "robot" + id, corn,"机器人"+trade.getTradeCurrencyName()+"/"+trade.getCurrencyName(), params);
	}

	@Override
	@Transactional
	public void afterUpdateOperate(RobotModel robotModel) {
		CurrencyTradeModelDto trade=currencyTradeService.getById(robotModel.getTradeId());
		Long id=robotModel.getId();
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		long base=robotModel.getTradeId()%9;
		String corn=base+"/"+robotModel.getInterval()+" * * * * ?";
		taskService.editMyJob(RobotJob.class.getName(), "robot" + id, corn,"机器人"+trade.getTradeCurrencyName()+"/"+trade.getCurrencyName(), params);
	}

	@Override
	protected void doMode(RobotModelDto dto) {
		super.doMode(dto);
		String statusName=taskService.getJobStatus(RobotJob.class.getName(),"robot"+dto.getId());
		dto.setStatusName(TaskInfo.jobStatusName(statusName));
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
		dto.setTradeName(currencyTradeService.getNameById(dto.getTradeId()));
	}

}
