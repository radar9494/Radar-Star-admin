package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.UserStoreStatusEnum;
import com.liuqi.business.mapper.UserStoreMapper;
import com.liuqi.business.model.UserStoreModel;
import com.liuqi.business.model.UserStoreModelDto;
import com.liuqi.business.model.UserWalletModel;
import com.liuqi.business.service.UserService;
import com.liuqi.business.service.UserStoreService;
import com.liuqi.business.service.UserWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserStoreServiceImpl extends BaseServiceImpl<UserStoreModel, UserStoreModelDto> implements UserStoreService {

    @Autowired
    private UserStoreMapper userStoreMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserWalletService userWalletService;
    @Override
    public BaseMapper<UserStoreModel, UserStoreModelDto> getBaseMapper() {
        return this.userStoreMapper;
    }


    @Override
    public UserStoreModelDto getNextMatchStore(Long currencyId,BigDecimal quantity, Integer tradeType) {
        UserStoreModelDto store=null;
        List<UserStoreModelDto> list=this.getUsing();
        if(list!=null && list.size()>0){
            //打乱
            Collections.shuffle(list);
            UserWalletModel wallet=null;
            for (UserStoreModelDto temp : list) {
                //买类型的  需要判断金额是否足够
                if(BuySellEnum.BUY.getCode().equals(tradeType)){
                    wallet=userWalletService.getByUserAndCurrencyId(temp.getUserId(),currencyId);
                    if(wallet.getUsing().compareTo(quantity)>0){
                        store=temp;
                        break;
                    }
                }else{ //商户买  直接匹配
                    store=temp;
                    break;
                }
            }
        }
        return store;
    }

    @Override
    public List<UserStoreModelDto> getUsing() {
        UserStoreModelDto search = new UserStoreModelDto();
        search.setStatus(UserStoreStatusEnum.USING.getCode());
        return this.queryListByDto(search, false);
    }

    @Override
    protected void doMode(UserStoreModelDto dto) {
        super.doMode(dto);
        dto.setUserName(userService.getNameById(dto.getUserId()));
        dto.setRealName(userService.getRealNameById(dto.getUserId()));
    }
}
