package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.WorkTypeStatusEnum;
import com.liuqi.business.mapper.WorkTypeMapper;
import com.liuqi.business.model.WorkTypeModel;
import com.liuqi.business.model.WorkTypeModelDto;
import com.liuqi.business.service.WorkTypeService;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class WorkTypeServiceImpl extends BaseServiceImpl<WorkTypeModel, WorkTypeModelDto> implements WorkTypeService {

    @Autowired
    private WorkTypeMapper workTypeMapper;
    @Autowired
    private RedisRepository redisRepository;

    @Override
    public BaseMapper<WorkTypeModel, WorkTypeModelDto> getBaseMapper() {
        return this.workTypeMapper;
    }

    @Override
    public WorkTypeModelDto getById(Long id) {
        String key = KeyConstant.KEY_WORKTYPE_Id + id;
        WorkTypeModelDto dto = redisRepository.getModel(key);
        if (dto == null) {
            dto = workTypeMapper.getById(id);
            if (dto != null) {
                redisRepository.set(key, dto, 1L, TimeUnit.DAYS);
            }
        }
        return dto;
    }

    @Override
    public String getNameById(Long typeId) {
        WorkTypeModelDto dto = this.getById(typeId);
        return dto != null ? dto.getName() : "";
    }

    @Override
    public List<WorkTypeModelDto> getAll() {
        String key = KeyConstant.KEY_WORKTYPE;
        List<WorkTypeModelDto> list = redisRepository.getModel(key);
        if (list == null) {
            list = this.queryListByDto(new WorkTypeModelDto(), true);
            if (list != null && list.size() > 0) {
                redisRepository.set(key, list, 1L, TimeUnit.DAYS);
            }
        }
        return list;
    }

    @Override
    public List<WorkTypeModelDto> getUsing() {
        List<WorkTypeModelDto> list = this.getAll();
        list = list.stream().filter(t -> WorkTypeStatusEnum.SHOW.getCode().equals(t.getStatus())).collect(Collectors.toList());
        return list;
    }

    @Override
    protected void doMode(WorkTypeModelDto dto) {
        super.doMode(dto);
        redisRepository.del(KeyConstant.KEY_WORKTYPE_Id + dto.getId());
        redisRepository.del(KeyConstant.KEY_WORKTYPE);
    }


}
