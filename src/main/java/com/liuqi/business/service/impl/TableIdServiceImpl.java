package com.liuqi.business.service.impl;


import com.liuqi.base.BaseConstant;
import com.liuqi.business.constant.LockConstant;
import com.liuqi.business.enums.TableIdNameEnum;
import com.liuqi.business.mapper.TableIdMapper;
import com.liuqi.business.model.TableIdModel;
import com.liuqi.business.service.TableIdService;
import com.liuqi.business.service.UserWalletService;
import com.liuqi.exception.BusinessException;
import com.liuqi.redis.RedisRepository;
import com.liuqi.redis.lock.RedissonLockUtil;
import com.liuqi.utils.DateTimeUtils;
import org.apache.ibatis.annotations.Param;
import org.redisson.api.RLock;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TableIdServiceImpl implements TableIdService {

    @Autowired
    private TableIdMapper tableIdMapper;
    @Autowired
    private RedisRepository redisRepository;
    /**
     * 获取一个id
     *
     * @return
     */
    @Override
    public Long getNextId(TableIdNameEnum tableIdName) {
        String key= BaseConstant.BASE_PROJECT+":"+tableIdName.getName();
        //获取一个值
        long size=redisRepository.lListSize(key);
        //等于100时或者等于0时才执行  插入1000个id
        if(size==100 || size ==0){
            String lockKey= LockConstant.LOCK_TRADEID+tableIdName;
            RLock rLock=null;
            try {
                //一个去执行
                rLock= RedissonLockUtil.lock(lockKey);
                //在查询一次
                size = redisRepository.lListSize(key);
                //小于100时 插入1000个id
                if (size <= 100) {
                    ((TableIdService) AopContext.currentProxy()).batchId(key, tableIdName.getName());
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                RedissonLockUtil.unlock(rLock);
            }
        }
        Long id= redisRepository.idPull(key);
        if(id<=0){
            throw new BusinessException("获取id异常");
        }
        return id;
    }
    /**
     * 获取一个id
     *
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void batchId(String key,String tableIdName) {
        //清理数据
        this.deleteHistory(tableIdName);

        //获取未使用地址
        List<TableIdModel> list=new ArrayList<>();
        for(int i=0;i<=1000;i++){
            TableIdModel tableId = new TableIdModel();
            list.add(tableId);
        }
        tableIdMapper.insert(tableIdName,list);
        Long[] ids=new Long[list.size()];
        for(int i=0,size=list.size();i<size;i++){
            ids[i]=list.get(i).getId();
        }
        redisRepository.idPushList(key,ids);
    }

    @Override
    @Transactional
    public void deleteHistory(String tableIdName) {
        tableIdMapper.deleteHistory(tableIdName);
    }
}
