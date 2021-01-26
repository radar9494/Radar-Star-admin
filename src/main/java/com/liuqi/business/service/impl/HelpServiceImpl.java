package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.mapper.HelpMapper;
import com.liuqi.business.model.HelpModel;
import com.liuqi.business.model.HelpModelDto;
import com.liuqi.business.service.HelpService;
import com.liuqi.business.service.HelpTypeService;
import com.liuqi.redis.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class HelpServiceImpl extends BaseServiceImpl<HelpModel, HelpModelDto> implements HelpService {

    @Autowired
    private HelpMapper helpMapper;
    @Autowired
    private HelpTypeService helpTypeService;
    @Autowired
    private RedisRepository redisRepository;

    @Override
    public BaseMapper<HelpModel, HelpModelDto> getBaseMapper() {
        return this.helpMapper;
    }

    @Override
    protected void doMode(HelpModelDto dto) {
        super.doMode(dto);
        dto.setTypeName(helpTypeService.getNameById(dto.getTypeId(),true));
    }

    @Override
    public List<HelpModelDto> getByLikeTitle(String title) {
        HelpModelDto dto=new HelpModelDto();
        dto.setTitleLike(title);
        return this.queryListByDto(dto,true);
    }

    @Override
    public List<HelpModelDto> getByTypeId(Long typeId) {
        HelpModelDto dto=new HelpModelDto();
        dto.setTypeId(typeId);
        return this.queryListByDto(dto,true);
    }

    @Override
    public HelpModelDto getById(Long id) {
        String key = KeyConstant.KEY_HELP_ID + id;
        HelpModelDto help = redisRepository.getModel(key);
        if (help == null) {
            help = helpMapper.getById(id);
            if (help != null) {
                redisRepository.set(key, help, 2L, TimeUnit.DAYS);
            }
        }
        return help;
    }

    @Override
    public void cleanCacheByModel(HelpModel helpModel) {
        super.cleanCacheByModel(helpModel);
        String key = KeyConstant.KEY_HELP_ID + helpModel.getId();
        redisRepository.del(key);
    }
}
