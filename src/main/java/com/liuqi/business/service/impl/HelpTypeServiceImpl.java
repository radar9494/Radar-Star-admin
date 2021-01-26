package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.enums.HelpTypeStatusEnum;
import com.liuqi.business.mapper.HelpTypeMapper;
import com.liuqi.business.model.HelpTypeModel;
import com.liuqi.business.model.HelpTypeModelDto;
import com.liuqi.business.service.HelpTypeService;
import com.liuqi.redis.RedisRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HelpTypeServiceImpl extends BaseServiceImpl<HelpTypeModel, HelpTypeModelDto> implements HelpTypeService {

    @Autowired
    private HelpTypeMapper helpTypeMapper;
    @Autowired
    private RedisRepository redisRepository;

    @Override
    public BaseMapper<HelpTypeModel, HelpTypeModelDto> getBaseMapper() {
        return this.helpTypeMapper;
    }

    @Override
    protected void doMode(HelpTypeModelDto dto) {
        super.doMode(dto);
        dto.setParentName(this.getNameById(dto.getId(),false));
    }

    @Override
    public void cleanCacheByModel(HelpTypeModel helpTypeModel) {
        super.cleanCacheByModel(helpTypeModel);
        String key = KeyConstant.KEY_HELPTYPE_ID + helpTypeModel.getId();
        redisRepository.del(key);
    }

    @Override
    public HelpTypeModelDto getById(Long id) {
        if(id<=0){
            return null;
        }
        String key = KeyConstant.KEY_HELPTYPE_ID + id;
        HelpTypeModelDto helpType = redisRepository.getModel(key);
        if (helpType == null) {
            helpType = helpTypeMapper.getById(id);
            if (helpType != null) {
                redisRepository.set(key, helpType, 2L, TimeUnit.DAYS);
            }
        }
        return helpType;
    }


    @Override
    public String getNameById(Long id,boolean hasSelf) {
        String name="";
        HelpTypeModelDto helpType = this.getById(id,false);
        if(hasSelf) {
            name = helpType != null ? helpType.getName() : "";
        }
        while(helpType!=null){
            helpType=this.getById(helpType.getParentId());
            if(helpType!=null ) {
                if(StringUtils.isEmpty(name)) {
                    name = helpType.getName();
                }else{
                    name = helpType.getName() + "->" + name;
                }
            }
        }
        return name;
    }

    @Override
    public List<HelpTypeModelDto> getFirstLevel() {
        return helpTypeMapper.getByParent(0L);
    }

    @Override
    public List<HelpTypeModelDto> getSub(Long parentId) {
        return helpTypeMapper.getByParent(parentId);
    }


    @Override
    public List<HelpTypeModelDto> getUsingFirstLevel() {
        List<HelpTypeModelDto> list=this.getFirstLevel();
        return list.stream().filter(t-> HelpTypeStatusEnum.USING.getCode().equals(t.getStatus())).collect(Collectors.toList());
    }

    @Override
    public List<HelpTypeModelDto> getUsingSub(Long parentId) {
        List<HelpTypeModelDto> list=this.getSub(parentId);
        return list.stream().filter(t-> HelpTypeStatusEnum.USING.getCode().equals(t.getStatus())).collect(Collectors.toList());
    }
}
