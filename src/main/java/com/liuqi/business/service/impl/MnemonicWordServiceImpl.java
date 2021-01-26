package com.liuqi.business.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.model.MnemonicWordModel;
import com.liuqi.business.model.MnemonicWordModelDto;


import com.liuqi.business.service.MnemonicWordService;
import com.liuqi.business.mapper.MnemonicWordMapper;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MnemonicWordServiceImpl extends BaseServiceImpl<MnemonicWordModel,MnemonicWordModelDto> implements MnemonicWordService{

	@Autowired
	private MnemonicWordMapper mnemonicWordMapper;

	@Override
	public BaseMapper<MnemonicWordModel,MnemonicWordModelDto> getBaseMapper() {
		return this.mnemonicWordMapper;
	}

	@Override
	public List<String> ramdomKeyWord() {
		return mnemonicWordMapper.ramdomKeyWord();
	}
}
