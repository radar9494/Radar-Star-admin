package com.liuqi.business.service.impl;


import com.liuqi.business.enums.UsingEnum;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.utils.MathUtil;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.MiningWalletMapper;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MiningWalletServiceImpl extends BaseServiceImpl<MiningWalletModel, MiningWalletModelDto> implements MiningWalletService {

    @Autowired
    private MiningWalletMapper miningWalletMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MiningWalletLogService miningWalletLogService;
    @Autowired
    private MiningConfigService miningConfigService;
    @Autowired
    private UserWalletUpdateLogService userWalletUpdateLogService;
    @Autowired
    private MiningUserTotalHandleService miningUserTotalHandleService;


    @Override
    public BaseMapper<MiningWalletModel, MiningWalletModelDto> getBaseMapper() {
        return this.miningWalletMapper;
    }

    @Override
    protected void doMode(MiningWalletModelDto dto) {
        dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
        dto.setUserName(userService.getNameById(dto.getUserId()));
    }

    @Override
    @Transactional
    public MiningWalletModel modified(long userId, long currencyId, BigDecimal num,BigDecimal freeze) {
        MiningWalletModel miningWalletModel = findByUserIdAndCurrencyId(userId, currencyId);
        if (num.compareTo(BigDecimal.ZERO) < 0) {
            Assert.isTrue(miningWalletModel.getUsing().add(num).compareTo(BigDecimal.ZERO) >= 0, "金额不足");
            Assert.isTrue(miningWalletModel.getFreeze().add(freeze).compareTo(BigDecimal.ZERO) >= 0, "冻结异常");
        }
        miningWalletMapper.updateUsing(userId, currencyId, num,freeze);
         return miningWalletModel = findByUserIdAndCurrencyId(userId, currencyId);
    }

    @Override
    public List<WalletStaticModel> groupByCurrencyId() {
        return miningWalletMapper.groupByCurrencyId();
    }


    @Override
    public List<MiningWalletModel> getFreeze() {
        return miningWalletMapper.getFreeze();
    }

    @Override
    public MiningWalletModel getTotal(Long id,Integer status) {
        return miningWalletMapper.getTotal(id,status);
    }

    @Transactional
    @Override
    public void insertMiningWallet(Long userId) {
        MiningConfigModelDto search=new MiningConfigModelDto();
        search.setType(0);
        List<MiningConfigModelDto> list = miningConfigService.queryListByDto(search, false);
        for(MiningConfigModelDto config:list){
            this.findByUserIdAndCurrencyId(userId,config.getCurrencyId());
        }
    }

    @Transactional
    @Override
    public void adminUpdate(MiningWalletModel wallet, Long opeId) {
        //获取原对象
        MiningWalletModel model = this.getById(wallet.getId());
        BigDecimal oldUsing = model.getUsing();
        BigDecimal oldFreeze  = model.getFreeze();
        BigDecimal modifyUsing=wallet.getUsing();
        BigDecimal modifyFreeze=wallet.getFreeze();
        BigDecimal newUsing = MathUtil.add(oldUsing,modifyUsing);
        BigDecimal newFreeze = MathUtil.add(oldFreeze,modifyFreeze);
        model.setUsing(newUsing);
         model.setFreeze(newFreeze);
        model.setUpdateTime(new Date());
        //更新数据
        MiningWalletModel modified = this.modified(model.getUserId(), model.getCurrencyId(), modifyUsing,modifyFreeze  );
        userWalletUpdateLogService.insert(oldUsing,modifyUsing,newUsing,oldFreeze,modifyFreeze,newFreeze,opeId,model.getUserId(),model.getCurrencyId(),wallet.getRemark(),2);
        miningWalletLogService.addLog( model.getCurrencyId(),model.getUserId() ,modifyUsing, WalletLogTypeEnum.SYS.getCode(),"系统修改",modified);
        miningUserTotalHandleService.add(model.getUserId(), modifyFreeze.doubleValue(),model.getCurrencyId());

    }

    @Override
    public List getByUserId(Long userId, String currencyName) {
        MiningWalletModelDto search=new MiningWalletModelDto();
        search.setUserId(userId);
        List<Long> currencyList= Lists.newArrayList();
        MiningConfigModelDto search1=new MiningConfigModelDto();
        search1.setType(0);
       if(StringUtils.isEmpty(currencyName)){
           List<MiningConfigModelDto> list = miningConfigService.queryListByDto(search1, false);
           for(MiningConfigModelDto config:list){
               currencyList.add(config.getCurrencyId());
           }
           if(currencyList==null || currencyList.size()==0){
               currencyList.add(-1L);
           }
           search.setCurrencyList(currencyList);
       }else{
           CurrencyModelDto currency = currencyService.getByName(currencyName);
           Long currencyId =currency!=null?currency.getId():0L;
                   search.setCurrencyId(currencyId);
       }
        return this.queryListByDto(search,true);
    }

    @Override
    @Transactional
    public MiningWalletModel findByUserIdAndCurrencyId(long userId, long currencyId) {
        MiningWalletModel miningWalletModel = miningWalletMapper.findByUserIdAndCurrencyId(userId, currencyId);
        if (miningWalletModel == null) {
            miningWalletModel = new MiningWalletModel();
            miningWalletModel.setCurrencyId(currencyId);
            miningWalletModel.setUserId(userId);
            miningWalletModel.setUserId(userId);
            miningWalletModel.setUsing(BigDecimal.ZERO);
            insert(miningWalletModel);
        }
        return miningWalletModel;
    }


}
