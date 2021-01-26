package com.liuqi.business.service.impl;


import com.liuqi.business.enums.ProtocolEnum;
import com.liuqi.business.enums.UserStatusEnum;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.TransferMapper;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class TransferServiceImpl extends BaseServiceImpl<TransferModel, TransferModelDto> implements TransferService {

    @Autowired
    private TransferMapper transferMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private UserWalletLogService userWalletLogService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private TransferConfigService transferConfigService;
    @Autowired
    private UserRechargeAddressService userRechargeAddressService;

    @Override
    protected void doMode(TransferModelDto dto) {
        super.doMode(dto);
        dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
        dto.setName(userService.getNameById(dto.getUserId()));
        String rechargeAddress = userRechargeAddressService.getRechargeAddress(dto.getUserId(), ProtocolEnum.RDT.getCode());
        dto.setAddress(rechargeAddress);
        String reiveAddress = userRechargeAddressService.getRechargeAddress(dto.getReceiveId(), ProtocolEnum.RDT.getCode());
        dto.setReceiveAddress(reiveAddress);
        dto.setReceiveName(userService.getNameById(dto.getReceiveId()));
    }

    @Transactional
    @Override
    public void transfer(Long userId, Long receiveId, Long currencyId, BigDecimal quantity,String phone) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("转账数量不正确");
        }
        TransferConfigModelDto config = transferConfigService.getByCurrencyId(currencyId);

        if (quantity.compareTo(config.getMinQuantity()) < 0) {
            throw new BusinessException("转账最小数量" + config.getMinQuantity());
        }
        if (quantity.compareTo(config.getMaxQuantity()) > 0) {
            throw new BusinessException("转账最大数量" + config.getMaxQuantity());
        }

//			UserModel userModel=userService.getById(userId);
        UserModel receiveModel = userService.getById(receiveId);
        BigDecimal poundage = config.getRate();
        TransferModel transfer = new TransferModel();
        transfer.setCurrencyId(currencyId);
        transfer.setUserId(userId);
        transfer.setPhone(phone);
        transfer.setReceiveId(receiveId);
        transfer.setQuantity(quantity);
        transfer.setRate(poundage);
        //判断金额是否足够
        UserWalletModel wallet = userWalletService.getByUserAndCurrencyId(userId, currencyId);
        if (wallet.getUsing().compareTo(quantity) < 0) {
            throw new BusinessException("币数量不足!");
        }
        this.insert(transfer);
        //可用扣除
        wallet.setUsing(MathUtil.sub(wallet.getUsing(), quantity));
        userWalletService.update(wallet);
        userWalletLogService.addLog(userId, currencyId, quantity.negate(), WalletLogTypeEnum.TRANSFER.getCode(), transfer.getId(), "转给"+receiveModel.getName(), wallet);

         Long rdbId=currencyService.getRdbId();
        UserWalletModel rdtWallet = userWalletService.getByUserAndCurrencyId(userId, rdbId);
        if (rdtWallet.getUsing().compareTo(poundage) < 0) {
            throw new BusinessException("RDB数量不足!");
        }
        //可用扣除
        rdtWallet.setUsing(MathUtil.sub(rdtWallet.getUsing(), poundage));
        userWalletService.update(rdtWallet);
        userWalletLogService.addLog(userId, rdbId, poundage.negate(), WalletLogTypeEnum.TRANSFER_FEES.getCode(), transfer.getId(), "转账矿工费", rdtWallet);

        //接收方未激活 激活
        UserWalletModel receiveWallet = userWalletService.getByUserAndCurrencyId(receiveId, currencyId);
        if (receiveModel.getStatus().equals(UserStatusEnum.NOTUSING.getCode())) {
            userService.active(userId, receiveModel.getName(), false);
        }else{
//            Long rdtId=currencyService.getRdtId();
//           if(currencyId.equals(rdtId)){
//               if(receiveWallet.getGatewaySwitch()==0){
//                   throw new BusinessException("对方网关未开启");
//               }
//           }
        }
        //接收方获取
        BigDecimal transferQuantity = transfer.getQuantity();


        receiveWallet.setUsing(MathUtil.add(receiveWallet.getUsing(), transferQuantity));
        userWalletService.update(receiveWallet);
        userWalletLogService.addLog(receiveId, currencyId, transferQuantity, WalletLogTypeEnum.TRANSFER.getCode(), transfer.getId(), "收款", receiveWallet);
    }

    @Override
    public BaseMapper<TransferModel, TransferModelDto> getBaseMapper() {
        return this.transferMapper;
    }

}
