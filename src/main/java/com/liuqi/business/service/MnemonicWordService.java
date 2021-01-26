package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.MnemonicWordModel;
import com.liuqi.business.model.MnemonicWordModelDto;

import java.util.List;

public interface MnemonicWordService extends BaseService<MnemonicWordModel,MnemonicWordModelDto>{

    List<String> ramdomKeyWord();

}
