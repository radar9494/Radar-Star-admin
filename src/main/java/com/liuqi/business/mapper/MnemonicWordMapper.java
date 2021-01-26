package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.model.MnemonicWordModel;
import com.liuqi.business.model.MnemonicWordModelDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MnemonicWordMapper extends BaseMapper<MnemonicWordModel, MnemonicWordModelDto> {

    List<String> ramdomKeyWord();

}
