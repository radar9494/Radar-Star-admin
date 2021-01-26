package com.liuqi.business.service.impl;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.enums.WorkDetailTypeEnum;
import com.liuqi.business.enums.WorkResultEnum;
import com.liuqi.business.enums.WorkStatusEnum;
import com.liuqi.business.mapper.WorkMapper;
import com.liuqi.business.model.WorkModel;
import com.liuqi.business.model.WorkModelDto;
import com.liuqi.business.service.UserService;
import com.liuqi.business.service.WorkDetailService;
import com.liuqi.business.service.WorkService;
import com.liuqi.business.service.WorkTypeService;
import com.liuqi.exception.BusinessException;
import com.liuqi.redis.NumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WorkServiceImpl extends BaseServiceImpl<WorkModel,WorkModelDto> implements WorkService {

	@Autowired
	private WorkMapper workMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private WorkTypeService workTypeService;
	@Autowired
	private WorkDetailService workDetailService;
	@Autowired
	private NumRepository numRepository;
	@Override
	public BaseMapper<WorkModel,WorkModelDto> getBaseMapper() {
		return this.workMapper;
	}

	@Override
	protected void doMode(WorkModelDto dto) {
		super.doMode(dto);
		dto.setUserName(userService.getNameById(dto.getUserId()));
		dto.setRealName(userService.getRealNameById(dto.getUserId()));
		dto.setTypeStr(workTypeService.getNameById(dto.getTypeId()));
	}

	@Override
	@Transactional
	public void publish(Long userId, Long typeId, String title, String phone, String email, String content, String file1, String file2, String file3) {
		WorkModel work = new WorkModel();
		work.setTitle(title);
		work.setNo(numRepository.getWorkCode());
		work.setTypeId(typeId);
		work.setUserId(userId);
		work.setPhone(phone);
		work.setEmail(email);
		work.setStatus(WorkStatusEnum.DOING.getCode());
		work.setResult(WorkResultEnum.NOT.getCode());
		this.insert(work);

		//添加一条描述信息
		workDetailService.saveDetail(work.getId(), content, file1, file2, file3,WorkDetailTypeEnum.USER.getCode());
	}

	@Override
	@Transactional
	public void reply(Long workId, String content, String file1Path, String file2Path,String file3Path,Integer type) {
		WorkModel work = this.getById(workId);
		if (WorkStatusEnum.END.getCode().equals(work.getStatus())) {
			throw new BusinessException("订单已完结");
		}
		this.update(work);
		workDetailService.saveDetail(workId, content, file1Path, file2Path,file3Path,type);
	}


	@Override
	@Transactional
	public void userEnd(Long workId,Integer result) {
		WorkModel work = this.getById(workId);
		if (WorkStatusEnum.END.getCode().equals(work.getStatus())) {
			throw new BusinessException("订单已完结");
		}
		work.setResult(result);
		work.setStatus(WorkStatusEnum.END.getCode());
		this.update(work);
		workDetailService.saveDetail(workId, "用户结束工单", "", "","", WorkDetailTypeEnum.USER.getCode());
	}

	@Override
	@Transactional
	public void sysEnd(Long workId,Integer result) {
		WorkModel work = this.getById(workId);
		if (WorkStatusEnum.END.getCode().equals(work.getStatus())) {
			throw new BusinessException("订单已完结");
		}
		work.setResult(result);
		work.setStatus(WorkStatusEnum.END.getCode());
		this.update(work);
		workDetailService.saveDetail(workId, "系统结束工单", "", "","", WorkDetailTypeEnum.SYS.getCode());
	}
}
