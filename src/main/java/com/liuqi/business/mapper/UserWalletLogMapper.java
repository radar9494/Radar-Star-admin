package com.liuqi.business.mapper;

import com.liuqi.base.BaseMapper;
import com.liuqi.business.dto.WalletLogDto;
import com.liuqi.business.model.UserWalletLogModel;
import com.liuqi.business.model.UserWalletLogModelDto;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface UserWalletLogMapper extends BaseMapper<UserWalletLogModel,UserWalletLogModelDto>{


    @Select(" select * from ( select currency_id,remark,money,create_time from t_user_wallet_log_0 where user_id=#{userId}\n" +
            " union all   \n" +
            "  select currency_id,remark,money,create_time from t_otc_wallet_log where user_id=#{userId}\n" +
            "\t\n" +
            "\t union all   \n" +
            "  select   currency_id,remark,num as money,create_time from t_mining_wallet_log where user_id=#{userId}) s order by s.create_time desc")

    List<WalletLogDto> getTotalLog(Long userId);
}
