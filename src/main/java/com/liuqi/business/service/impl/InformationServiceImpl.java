package com.liuqi.business.service.impl;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.mapper.InformationMapper;
import com.liuqi.business.model.InformationModel;
import com.liuqi.business.model.InformationModelDto;
import com.liuqi.business.service.InformationService;
import com.liuqi.redis.RedisRepository;
import com.liuqi.third.info.Info;
import com.liuqi.third.info.InfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class InformationServiceImpl extends BaseServiceImpl<InformationModel, InformationModelDto> implements InformationService {

    @Autowired
    private InformationMapper informationMapper;
    @Autowired
    private RedisRepository redisRepository;

    @Override
    public BaseMapper<InformationModel, InformationModelDto> getBaseMapper() {
        return this.informationMapper;
    }

    @Override
    public InformationModel getByInfoId(Long infoId) {
        return informationMapper.getByInfoId(infoId);
    }

    @Override
    @Transactional
    public void thridInfo() {
        List<InfoDto> infoList = Info.getNewInfo();
        if (infoList != null && infoList.size() > 0) {
            for (InfoDto dto : infoList) {
                InformationModel info = this.getByInfoId(dto.getId());
                if (info == null) {
                    info = new InformationModel();
                    String content = dto.getContent();
                    int index = content.indexOf("】");
                    info.setInfoId(dto.getId());
                    info.setTitle(content.substring(0, index + 1));
                    info.setContent(content.substring(index + 1));
                    info.setPubTime(new Date(Long.valueOf(dto.getCreated_at()) * 1000L));
                    info.setUpCounts(0);
                    info.setDownCounts(0);
                    this.insert(info);
                    this.cleanCacheByModel(info);
                }
            }
        }
    }

    @Override
    public int getTotal() {
        return informationMapper.getTotal();
    }
    @Override
    public void cleanCacheByModel(InformationModel informationModel) {
        super.cleanCacheByModel(informationModel);
        redisRepository.del(KeyConstant.KEY_INFO_LIST);
    }
}
