package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.enums.LockWalletLogTypeEnum;
import com.liuqi.business.mapper.LockWalletMapper;
import com.liuqi.business.model.LockWalletModel;
import com.liuqi.business.model.LockWalletModelDto;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.utils.MathUtil;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LockWalletServiceImpl extends BaseServiceImpl<LockWalletModel, LockWalletModelDto> implements LockWalletService {

    @Autowired
    private LockWalletMapper lockWalletMapper;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private LockWalletUpdateLogService lockWalletUpdateLogService;
    @Autowired
    private UserService userService;
    @Autowired
    private LockConfigService lockConfigService;
    @Autowired
    private LockWalletLogService lockWalletLogService;

    @Override
    public BaseMapper<LockWalletModel, LockWalletModelDto> getBaseMapper() {
        return this.lockWalletMapper;
    }

    /**
     * 修改
     *
     * @param t
     */
    @Override
    @Transactional
    public boolean update(LockWalletModel t) {
        cleanAllCache();
        cleanCacheByModel(t);
        //检查是否满足
        check(t);
        t.setUpdateTime(new Date());
        boolean status = this.getBaseMapper().update(t) > 0;
        if (status) {
            this.afterUpdateOperate(t);
            return status;
        } else {
            throw new BusinessException(t.getClass().getName() + "-id:" + t.getId() + "更新失败");
        }
    }

    @Override
    public LockWalletModelDto getByUserAndCurrencyId(Long userId, Long currencyId) {
        LockWalletModelDto wallet = lockWalletMapper.getByUserAndCurrencyId(userId, currencyId);
        //插入时判断  如果没有钱包新建一个
        if (wallet == null) {
            wallet = ((LockWalletService) AopContext.currentProxy()).addWallet(userId, currencyId);
        }
        return wallet;
    }

    @Override
    public void insertUserWallet(Long userId) {
        //初始化用户钱包
        List<Long> curList = lockConfigService.getLockCurrencyIdList();
        for (Long currencyId : curList) {
            //获取钱包  没有时创建
            this.getByUserAndCurrencyId(userId, currencyId);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public LockWalletModelDto addWallet(Long userId, Long currencyId) {
        if (userId != null && currencyId != null && userId > 0 && currencyId > 0) {
            LockWalletModelDto wallet = new LockWalletModelDto();
            wallet.setUserId(userId);
            wallet.setFreeze(BigDecimal.ZERO);
            wallet.setLocking(BigDecimal.ZERO);
            wallet.setCurrencyId(currencyId);
            this.insert(wallet);
            return wallet;
        }
        return null;
    }

    @Override
    @Transactional
    public void adminUpdate(LockWalletModelDto wallet, Long opeId) {
        //获取原对象
        LockWalletModelDto model = this.getById(wallet.getId());
        BigDecimal oldLocking = model.getLocking();
        BigDecimal oldFreeze = model.getFreeze();
        BigDecimal modifyLocking = wallet.getLocking();
        BigDecimal modifyFreeze = wallet.getFreeze();
        BigDecimal newLocking = MathUtil.add(oldLocking, modifyLocking);
        BigDecimal newFreeze = MathUtil.add(oldFreeze, modifyFreeze);
        model.setLocking(newLocking);
        model.setFreeze(newFreeze);
        model.setUpdateTime(new Date());
        //更新数据
        wallet = this.modifyWallet(model.getUserId(), model.getCurrencyId(), modifyLocking, modifyFreeze);
        lockWalletLogService.addLog(model.getUserId(), model.getCurrencyId(), modifyLocking, LockWalletLogTypeEnum.SYS.getCode(), 0L, "系统修改", wallet);
        lockWalletUpdateLogService.insert(oldLocking, modifyLocking, newLocking, oldFreeze, modifyFreeze, newFreeze, opeId, model.getUserId(), model.getCurrencyId(), wallet.getRemark());

    }

    private void check(LockWalletModel wallet) {
        if (wallet.getLocking().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("u:" + wallet.getUserId() + ",c:" + wallet.getCurrencyId() + "，锁仓小于0->" + wallet.getLocking());
        }
        if (wallet.getFreeze().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("u:" + wallet.getUserId() + ",c:" + wallet.getCurrencyId() + "，冻结小于0->" + wallet.getFreeze());
        }
    }

    @Override
    protected void doMode(LockWalletModelDto dto) {
        super.doMode(dto);
        dto.setUserName(userService.getNameById(dto.getUserId()));
        dto.setRealName(userService.getRealNameById(dto.getUserId()));
        dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
        dto.setPosition(currencyService.getPositionById(dto.getCurrencyId()));
    }

    @Override
    @Transactional
    public LockWalletModelDto modifyWallet(Long userId, Long currencyId, BigDecimal changeLocking, BigDecimal changeFreeze) {
        if (userId != null && currencyId != null && userId > 0 && currencyId > 0) {
            LockWalletModelDto dto = this.getByUserAndCurrencyId(userId, currencyId);
            boolean status = lockWalletMapper.modifyWallet(userId, currencyId, changeLocking, changeFreeze) > 0;
            if (status) {
                dto = this.getByUserAndCurrencyId(userId, currencyId);
            } else {
                System.out.println("u:" + userId + ",c:" + currencyId);
                throw new BusinessException("余额不足");
            }
            return dto;
        }
        throw new BusinessException("参数异常u:" + userId + ",c:" + currencyId);
    }


    @Override
    @Transactional
    public LockWalletModelDto modifyWalletLocking(Long userId, Long currencyId, BigDecimal changeLocking) {
        if (userId != null && currencyId != null && userId > 0 && currencyId > 0) {
            LockWalletModelDto dto = this.getByUserAndCurrencyId(userId, currencyId);
            boolean status = lockWalletMapper.modifyWalletLocking(userId, currencyId, changeLocking) > 0;
            if (status) {
                dto = this.getByUserAndCurrencyId(userId, currencyId);
            } else {
                System.out.println("u:" + userId + ",c:" + currencyId);
                throw new BusinessException("余额不足");
            }
            return dto;
        }
        throw new BusinessException("参数异常u:" + userId + ",c:" + currencyId);
    }


    @Override
    @Transactional
    public LockWalletModelDto modifyWalletFreeze(Long userId, Long currencyId, BigDecimal changeFreeze) {
        if (userId != null && currencyId != null && userId > 0 && currencyId > 0) {
            LockWalletModelDto dto = this.getByUserAndCurrencyId(userId, currencyId);
            boolean status = lockWalletMapper.modifyWalletFreeze(userId, currencyId, changeFreeze) > 0;
            if (status) {
                dto = this.getByUserAndCurrencyId(userId, currencyId);
            } else {
                System.out.println("u:" + userId + ",c:" + currencyId);
                throw new BusinessException("余额不足");
            }
            return dto;
        }
        throw new BusinessException("参数异常u:" + userId + ",c:" + currencyId);
    }
}
