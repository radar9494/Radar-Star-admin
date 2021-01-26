package com.liuqi.business.service;

import com.liuqi.base.BaseService;
import com.liuqi.business.dto.UserDto;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.model.UserModelDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface UserService extends BaseService<UserModel, UserModelDto> {


    UserModelDto getNotPwdById(Long id);
    /**
     * 获取名称
     * @param id
     * @return
     */
    String getRealNameById(Long id);
    /**
     * 获取名称
     * @param id
     * @return
     */
    String getNameById(Long id);
    /**
     * 获取名称
     * @param id
     * @return
     */
    String getOtcNameById(Long id);
    /**
     * 根据手机查询用户
     * @param phone
     * @return
     */
    UserModelDto queryByPhone(String phone);
    /**
     * 根据手机查询用户
     * @param email
     * @return
     */
    UserModelDto queryByEmail(String email);
    /**
     * 根据手机查询用户
     * @param name
     * @return
     */
    UserModelDto queryByName(String name);

    /**
     * 根据邀请码获取
     * @param inviteCode
     * @return
     */
    UserModelDto queryByInviteCode(String inviteCode);

    Long queryIdByName(String name);
    /**
     * 根据手机查询用户
     * @param name
     * @return
     */
    List<Long> queryIdByLikeName(String name);

    /**
     * 是否白名单用户
     * @param userId
     * @return
     */
    boolean whiteUser(Long userId);

    /**
     * 获取用户总数
     * @return
     */
    int getTotal();

    /**
     * 登录
     * @param name
     * @param password
     * @param request
     * @return
     */
    Map<String,Object> login(String name, String password, HttpServletRequest request,String emailCode);

    /**
     * 忘记密码
     * @param name
     * @param pwd
     * @param code
     */
    void forgetPassword(String name, String pwd, String code,Integer type,String email);



    /**
     * 注册
     * @param user
     */
    Long register(UserModelDto user);

    /**
     * 修改密码
     *
     * @param userId
     * @param newPwd
     * @param pwd
     * @param code
     */
    void changePwd(Long userId, String newPwd, String pwd,boolean checkCode, String code);

    /**
     * 修改交易密码
     *
     * @param tradePassword
     * @param newTradePassword
     * @param userId
     * @param checkCode
     * @param code
     */
    void changeTradePwd( String newTradePassword, Long userId,boolean checkCode, String code);


    /**
     * 忘记密码
     *
     * @param newPwd
     * @param code
     * @param userId
     */
    void findLoginPwd(String newPwd, String code, Long userId);

    /**
     * 找回交易密码
     *
     * @param newTradePwd
     * @param code
     * @param userId
     * @return
     */
    void findTradePwd(String newTradePwd, String code, Long userId);



    /**
     * 冻结
     * @param userId
     * @param adminId
     */
    void freeze(Long userId,Long adminId);

    /**
     * 解冻
     * @param userId
     * @param adminId
     */
    void unfreeze(Long userId,Long adminId);

    /**
     * 手机认证
     *
     * @param userId
     * @param phone
     * @param zone
     */
    void phoneAuth(Long userId, String phone, String zone);

    /**
     * 邮箱认证
     *
     * @param userId
     * @param email
     */
    void emailAuth(Long userId, String email);

    /**
     * 设置机器人
     *
     * @param userId
     * @param adminId
     */
    void robot(Long userId, Long adminId);

    /**
     * 修改密码
     * @param userId
     * @param pwd
     * @param adminId
     */
    void modifyPwd(Long userId, String pwd,Long adminId);
    /**
     * 修改交易密码
     * @param userId
     * @param pwd
     * @param adminId
     */
    void modifyTradePwd(Long userId, String pwd,Long adminId);


    /**
     * 获取用户的基本信息
     * @param userId
     * @return
     */
    UserDto getBaseInfo(Long userId);

    void active(String phone, String pwd, Long userId);



    UserModel getByAddress(String s);

    UserModelDto queryByNameOrAddress(String phone);

    void active(Long userId, String phone,boolean flag);

     List<Long>  getTimestamSub(Long userId);

     void checkTradePssword(Long userId,String tradePwd);


     void checkTradePwd(Long userId,String tradePwd,String code,Long gooleCode);

    void setInfo(Long userId, String name, String email, String code, String pwd, String tradePwd,String phoneCode);
}