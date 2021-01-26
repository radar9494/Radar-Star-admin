package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.model.SuperNodeConfigModel;
import com.liuqi.business.model.SuperNodeModel;
import com.liuqi.business.model.SuperNodeModelDto;

public interface SuperNodeService extends BaseService<SuperNodeModel,SuperNodeModelDto>{

    /**
     * 根据用户获取超级节点信息
     *
     * @param userId
     * @return
     */
    SuperNodeModelDto getByUserId(Long userId);

    /**
     * 获取总人数
     *
     * @return
     */
    int getTotalCount();

    /**
     * 加入超级节点
     * @param config  配置
     * @param userId 参与用户
     * @param recommendUserId 推荐用户
     * @param subWallet 是否扣减钱包
     */
    void joinSuperNode(SuperNodeConfigModel config,Long userId,Long recommendUserId,boolean subWallet);
}
