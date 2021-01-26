package com.liuqi.business.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.google.common.base.Functions;
import com.google.common.cache.Cache;
import com.google.common.collect.Lists;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.UserDto;
import com.liuqi.business.enums.UserStatusEnum;
import com.liuqi.business.enums.WalletLogTypeEnum;
import com.liuqi.business.enums.YesNoEnum;

import com.liuqi.business.mapper.UserMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.redis.CodeCache;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.ReturnResponse;
import com.liuqi.third.google.GoogleAuthService;
import com.liuqi.token.RedisTokenManager;
import com.liuqi.utils.ShiroPasswdUtil;
import com.liuqi.utils.ValidatorUtil;
import netscape.javascript.JSObject;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.util.privilegedactions.GetDeclaredField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl extends BaseServiceImpl<UserModel, UserModelDto> implements UserService {


    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private ConfigService configService;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private RedisTokenManager redisTokenManager;
    @Autowired
    private AutoRechargeService autoRechargeService;
    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private UserLevelService userLevelService;
    @Autowired
    private TimestampLevelService timestampLevelService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private UserWalletLogService userWalletLogService;
    @Autowired
    private OtcWalletService otcWalletService;
    @Autowired
    private MiningWalletService miningWalletService;


    @Autowired
    private MnemonicWordService mnemonicWordService;
    @Autowired
    private GoogleAuthService googleAuthService;


    @Override
    public BaseMapper<UserModel, UserModelDto> getBaseMapper() {
        return this.userMapper;
    }

    @Override
    protected void doMode(UserModelDto dto) {
        super.doMode(dto);
        dto.setRealName(userAuthService.getNameByUserId(dto.getId()));
    }

    @Override
    public UserModelDto getById(Long id) {
        UserModelDto user = redisRepository.getModel(KeyConstant.KEY_USER_ID + id);
        if (user == null) {
            user = userMapper.getById(id);
            if (user != null) {
                this.doMode(user);
                //缓存一小时
                redisRepository.set(KeyConstant.KEY_USER_ID + id, user, 5L, TimeUnit.HOURS);
            }
        }
        return user;
    }

    @Override
    public UserModelDto getNotPwdById(Long id) {
        UserModelDto dto = this.getById(id);
        dto.setPwd("");
        dto.setTradePwd("");
        return dto;
    }

    @Override
    public String getRealNameById(Long id) {
        String name = "";
        UserModelDto user = this.getById(id);
        name = user != null ? user.getRealName() : "";
        user = null;
        return name;
    }

    @Override
    public String getOtcNameById(Long id) {
        String name = "";
        UserModelDto user = this.getById(id);
        name = user != null ? user.getOtcName() : "";
        user = null;
        return name;
    }

    @Override
    public String getNameById(Long id) {
        String name = "";
        UserModel user = this.getById(id);
        name = user != null ? user.getName() : "";
        user = null;
        return name;
    }

    @Override
    public void cleanCacheByModel(UserModel userModel) {
        if (userModel != null) {
            redisRepository.del(KeyConstant.KEY_USER_ID + userModel.getId());
        }
    }

    @Override
    public UserModelDto queryByPhone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return null;
        }
        UserModelDto search = new UserModelDto();
        search.setPhone(phone);
        return userMapper.queryUnique(search);
    }

    @Override
    public UserModelDto queryByEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            return null;
        }
        UserModelDto search = new UserModelDto();
        search.setEmail(email);
        return userMapper.queryUnique(search);
    }

    @Override
    public UserModelDto queryByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        UserModelDto search = new UserModelDto();
        search.setName(name);
        return userMapper.queryUnique(search);
    }

    @Override
    public UserModelDto queryByInviteCode(String inviteCode) {
        if (StringUtils.isEmpty(inviteCode)) {
            return null;
        }
        UserModelDto search = new UserModelDto();
        search.setInviteCode(inviteCode);
        return userMapper.queryUnique(search);
    }

    @Override
    public Long queryIdByName(String name) {
        UserModelDto dto = this.queryByName(name);
        return dto != null ? dto.getId() : 0L;
    }

    @Override
    public List<Long> queryIdByLikeName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        return userMapper.queryIdByLikeName(name);
    }


    @Override
    public boolean whiteUser(Long userId) {
        UserModel user = this.getById(userId);
        return user.getWhiteIf().equals(YesNoEnum.YES.getCode());
    }

    @Override
    public int getTotal() {
        return userMapper.getTotal();
    }

    @Override
    @Transactional
    public Map<String, Object> login(String name, String password, HttpServletRequest request,String emailCode) {
        String key = KeyConstant.KEY_LOGIN_ERROR + name;
        Integer times = redisRepository.getInteger(key);
        if (times >= 3) {
            throw new BusinessException("密码错误3次，请15分钟后再试");
        }
        UserModel userModel = this.queryByName(name);

        Map<String, Object> map = new HashMap<String, Object>();
        if (userModel != null) {
           if("导入".equals(userModel.getRemark())&&userModel.getEmailAuth()==0){
           }else{
               CodeCache.verifyCode(userModel.getEmail(), emailCode);
           }
            userAuthService.init(userModel.getId());
            if (userModel.getStatus() == 2) {
                throw new BusinessException("用户被冻结，请联系管理员！");
            }
            if (userModel.getStatus() == UserStatusEnum.NOTUSING.getCode()) {
                throw new BusinessException("请先激活！");
            }
             if (StringUtils.equalsIgnoreCase(userModel.getPwd(),
                     ShiroPasswdUtil.getUserPwd(password))) {
                //使用token登录
                String token = redisTokenManager.getToken(userModel);
                map.put("token", token);
                map.put("status", UserStatusEnum.USDING.getCode());
                if (StringUtils.isEmpty(userModel.getMnemonic())) {
                    List<String> mnemonicRandom = mnemonicWordService.ramdomKeyWord();
                    String join = StringUtils.join(mnemonicRandom, ",");
                    map.put("mnemonicRandom", join);
                }
                if (this.update(userModel)) {
                    map.put("name", userModel.getName());
                    //清除登录错误信息
                    redisRepository.del(key);
                   return map;
                } else {
                    throw new BusinessException("数据库异常！");
                }
            } else {
                times++;
                if (times == 1) {
                    redisRepository.set(key, times, 15, TimeUnit.MINUTES);
                } else {
                    redisRepository.incrOne(key);
                }
                throw new BusinessException("账号密码错误");
            }
        } else {
            throw new BusinessException("用户名不存在");
        }
    }

    @Override
    @Transactional
    public void forgetPassword(String name, String pwd, String code, Integer type, String email) {
        //验证验证码  失败抛异常
        UserModel userModel = this.queryByName(name);
        if (userModel == null) {
            throw new BusinessException("用户名不存在");
        }
        if (type == 0) {
            if (!email.equals(userModel.getEmail())) {
                throw new BusinessException("请输入账号绑定的邮箱");
            }
            CodeCache.verifyCode(userModel.getEmail(), code);
        } else {
            if (!userModel.getMnemonic().equals(code)) {
                throw new BusinessException("助记词错误");
            }
        }
        userModel.setPwd(ShiroPasswdUtil.getUserPwd(pwd));
        this.update(userModel);
    }


    @Override
    @Transactional
    public Long register(UserModelDto user) {
        //设置基础信息
        this.userBase(user);
        //初始化用户基本信息
        this.initUserBase(user.getId(), user.getParentName());
        return user.getId();
    }

    private void userBase(UserModelDto user) {
        if (StringUtils.isEmpty(user.getName())) {
            user.setName(user.getPhoneEmail());
        }
        user.setPhoneAuth(YesNoEnum.NO.getCode());
        user.setEmailAuth(YesNoEnum.YES.getCode());
        user.setStatus(UserStatusEnum.NOTUSING.getCode());
        user.setGoogleAuth(0);
        user.setPayType(2);
        user.setGoogleSecret(googleAuthService.getUserKey());
        user.setTradeFree(0);
//		if(ValidatorUtil.isMobile(user.getPhoneEmail())){
//			if(StringUtils.isEmpty(user.getZone())) {
//				user.setZone("86");
//			}
//			user.setPhone(user.getPhoneEmail());
//			user.setPhoneAuth(YesNoEnum.NO.getCode());
//			user.setAuthType(UserModelDto.AUTHTYPE_PHONE);
//		}else{
        //user.setEmail(user.getea());
//			user.setEmailAuth(YesNoEnum.NO.getCode());
        user.setAuthType(UserModelDto.AUTHTYPE_EMAIL);
//		}
        String defaultPwd = configService.queryValueByName(ConfigConstant.CONFIG_NAME_DEFAULT_PWD);
        //设置密码强度
        if (StringUtils.isEmpty(user.getPwd())) {
            user.setPwd(defaultPwd);
        }
        if (StringUtils.isEmpty(user.getTradePwd())) {
            user.setTradePwd(defaultPwd);
        }
        //设置密码强度
        user.setPwdStrength(this.checkPassword(user.getPwd()));
        user.setPwd(ShiroPasswdUtil.getUserPwd(user.getPwd()));
        user.setTradePwd(ShiroPasswdUtil.getUserPwd(user.getTradePwd()));
        user.setRobot(YesNoEnum.NO.getCode());
        user.setWhiteIf(YesNoEnum.NO.getCode());
        user.setInviteCode(this.getInviteCode());
        user.setOtc(YesNoEnum.NO.getCode());
        user.setOtcName(user.getName());
        String address = createAddress();
        user.setAddress(address);
        List<String> mnemonicRandom = mnemonicWordService.ramdomKeyWord();
        String join = StringUtils.join(mnemonicRandom, " ");
        //注册就生成一个助记词绑定用户
        user.setMnemonic(join);
        user.setIsRemember(0);
        this.insert(user);
    }


    private String createAddress() {
        String s = "";
        while (true) {
            s = RandomUtil.randomString(32);
            UserModel user = this.getByAddress(s);
            if (user == null) {
                break;
            }
        }
        return s;
    }

    /**
     * 初始化用户信息
     *
     * @param userId
     * @param inviteCode
     */
    private void initUserBase(Long userId, String inviteCode) {
        //
        //初始化用户层级信息
      //   userLevelService.initLevel(userId,inviteCode);
        //初始化用户认证信息
        userAuthService.initAuth(userId);
        //初始化用户钱包信息
        userWalletService.insertUserWallet(userId);
        //初始化OTC钱包信息
        otcWalletService.insertOtcWallet(userId);
        //初始化矿池钱包信息
        miningWalletService.insertMiningWallet(userId);
    }

    private String getInviteCode() {
        String inviteCode = RandomUtil.randomString(6);
        int i = 0;
        while (this.queryByInviteCode(inviteCode) != null) {
            i++;
            inviteCode = RandomUtil.randomString(6);
            if (i > 20) {
                throw new BusinessException("邀请码生成异常");
            }
        }
        return inviteCode;
    }


    @Override
    @Transactional
    public void changePwd(Long userId, String newPwd, String pwd, boolean checkCode, String code) {
        UserModel user = this.getById(userId);
        if (user.getPwd().equalsIgnoreCase(ShiroPasswdUtil.getUserPwd(pwd))) {
            if (checkCode) {
                //验证验证码  失败抛异常
                CodeCache.verifyCode(user.getEmail(), code);
            } else {
                if (!user.getMnemonic().equals(code)) {
                    throw new BusinessException("助记词错误");
                }
            }
            user.setPwd(ShiroPasswdUtil.getUserPwd(newPwd));
            this.update(user);
        } else {
            throw new BusinessException("原密码输入错误");
        }
    }

    @Override
    @Transactional
    public void changeTradePwd(String newTradePassword, Long userId, boolean checkCode, String code) {
        UserModel user = this.getById(userId);

            //验证验证码  失败抛异常
            if (checkCode) {
                CodeCache.verifyCode(user.getEmail(), code);
            } else {
                if (!user.getMnemonic().equals(code)) {
                    throw new BusinessException("助记词错误");
                }
            }
            user.setTradePwd(ShiroPasswdUtil.getUserPwd(newTradePassword));
            this.update(user);

    }

    @Override
    @Transactional
    public void findLoginPwd(String newPwd, String code, Long userId) {
        UserModel user = this.getById(userId);
        //验证验证码  失败抛异常
        CodeCache.verifyCode(user.getEmail(), code);

        user.setPwd(ShiroPasswdUtil.getUserPwd(newPwd));
        this.update(user);
    }

    @Override
    @Transactional
    public void findTradePwd(String newTradePwd, String code, Long userId) {
        UserModel user = this.getById(userId);
        //验证验证码  失败抛异常
        CodeCache.verifyCode(user.getEmail(), code);

        user.setTradePwd(ShiroPasswdUtil.getUserPwd(newTradePwd));
        this.update(user);
    }

    @Override
    @Transactional
    public void freeze(Long userId, Long adminId) {
        UserModel user = this.getById(userId);
        user.setStatus(UserStatusEnum.FREEZE.getCode());
        this.update(user);
        loggerService.insert(LoggerModelDto.TYPE_UPDATE, "冻结用户" + user.getName(), "用户管理", adminId);
    }


    @Override
    @Transactional
    public void unfreeze(Long userId, Long adminId) {
        UserModel user = this.getById(userId);
        user.setStatus(UserStatusEnum.USDING.getCode());
        this.update(user);
        loggerService.insert(LoggerModelDto.TYPE_UPDATE, "解冻用户" + user.getName(), "用户管理", adminId);
    }

    @Override
    @Transactional
    public void phoneAuth(Long userId, String phone, String zone) {
        UserModel user = this.getById(userId);
        user.setPhone(phone);
        user.setPhoneAuth(YesNoEnum.YES.getCode());
        user.setZone(zone);
        this.update(user);
    }

    @Override
    @Transactional
    public void emailAuth(Long userId, String email) {
        UserModel user = this.getById(userId);
        user.setEmail(email);
        user.setEmailAuth(YesNoEnum.YES.getCode());
        this.update(user);
    }

    @Override
    @Transactional
    public void robot(Long userId, Long adminId) {
        UserModel user = this.getById(userId);
        user.setRobot(YesNoEnum.YES.getCode().equals(user.getRobot()) ? YesNoEnum.NO.getCode() : YesNoEnum.YES.getCode());
        this.update(user);
        loggerService.insert(LoggerModelDto.TYPE_UPDATE, "修改用户：" + user.getName() + ",改为机器人状态:" + YesNoEnum.getName(user.getRobot()), "用户管理", adminId);
    }

    @Override
    @Transactional
    public void modifyPwd(Long userId, String pwd, Long adminId) {
        UserModel user = this.getById(userId);
        user.setPwd(ShiroPasswdUtil.getUserPwd(pwd));
        this.update(user);
        loggerService.insert(LoggerModelDto.TYPE_UPDATE, "修改用户密码：" + user.getName(), "用户管理", adminId);
    }

    @Override
    @Transactional
    public void modifyTradePwd(Long userId, String tradePwd, Long adminId) {
        UserModel user = this.getById(userId);
        user.setTradePwd(ShiroPasswdUtil.getUserPwd(tradePwd));
        this.update(user);
        loggerService.insert(LoggerModelDto.TYPE_UPDATE, "修改用户交易密码：" + user.getName(), "用户管理", adminId);
    }


    @Override
    public UserModel getByAddress(String s) {
        return userMapper.getByAddress(s);
    }

    @Override
    public void checkTradePssword(Long userId, String tradePwd) {
        UserModelDto user = this.getById(userId);

        String key = KeyConstant.KEY_TRADE_ERROR + userId;
        Integer times = redisRepository.getInteger(key);
        if (times >= 3) {
            throw new BusinessException("交易密码错误3次，请15分钟后再试");
        }

        if (!user.getTradePwd().equalsIgnoreCase(ShiroPasswdUtil.getUserPwd(tradePwd))) {
            times++;
            if (times == 1) {
                redisRepository.set(key, times, 15, TimeUnit.MINUTES);
            } else {
                redisRepository.incrOne(key);
            }
            throw new BusinessException("交易密码错误");
        }
    }

    @Override
    public List<Long> getTimestamSub(Long key) {
        UserModelDto byId = this.getById(key);
        List<Long> list = null;
        String value = redisRepository.getString(KeyConstant.KEY_USER_TIMESTAM_SUB + key);
        if (StringUtils.isNotEmpty(value)) {
            List<Integer> strList = JSONObject.parseObject(value, List.class);
            list = strList.stream().map(m -> m.longValue()).collect(Collectors.toList());
        } else {
            list = userMapper.getTimestamSub1(byId.getActiveDate());
            List<Long> list1 = userMapper.getTimestamSub2(byId.getActiveDate());
            list.addAll(list1);
            redisRepository.set(KeyConstant.KEY_USER_TIMESTAM_SUB + key, JSONObject.toJSONString(list));
        }
        return list;
    }

    @Transactional
    @Override
    public void setInfo(Long userId, String name, String email, String code, String pwd, String tradePwd,String phoneCode) {
            UserModelDto user = this.getById(userId);
            CodeCache.verifyCode(user.getName(),phoneCode);
            //CodeCache.verifyCode(email,code);
            UserModelDto userModelDto = this.queryByName(name);
            if(userModelDto!=null){
                throw new BusinessException("登录名已存在!");
            }
            user.setName(name);
            user.setEmail(email);
            user.setAuthType(1);
            user.setEmailAuth(YesNoEnum.YES.getCode());
            user.setPwd(ShiroPasswdUtil.getUserPwd(pwd));
            user.setTradePwd(ShiroPasswdUtil.getUserPwd(tradePwd));
            this.update(user);
    }

    @Override
    public void checkTradePwd(Long userId, String tradePwd, String code, Long gooleCode) {
        UserModelDto user = this.getById(userId);

        String key = KeyConstant.KEY_TRADE_ERROR + userId;
        Integer times = redisRepository.getInteger(key);
        if (times >= 3) {
            throw new BusinessException("交易密码错误3次，请15分钟后再试");
        }
        String value = redisRepository.getString(KeyConstant.KEY_TRADE_TRAEDE_FREE + userId);
       if(StringUtils.isNoneEmpty(value)){
           return;
       }
       try {
           if (!user.getTradePwd().equalsIgnoreCase(ShiroPasswdUtil.getUserPwd(tradePwd))) {
               throw new BusinessException("交易密码错误");
           }
           //0 交易密码 1交易密码+谷歌验证码 2交易密码+邮箱验证码  3交易密码加谷歌验证码加邮箱验证码
           if (user.getPayType() == 1) {
               boolean verify = googleAuthService.verify(user.getGoogleSecret(), gooleCode);
               if (!verify) {
                   throw new BusinessException("谷歌验证码错误");
               }
           } else if (user.getPayType() == 2) {
               CodeCache.verifyCode(user.getEmail(), code);

           } else if (user.getPayType() == 3) {
               CodeCache.verifyCode(user.getEmail(), code);
               boolean verify = googleAuthService.verify(user.getGoogleSecret(), gooleCode);
               if (!verify) {
                   throw new BusinessException("谷歌验证码错误");
               }
           }
       }catch (BusinessException e){
           times++;
           if (times == 1) {
               redisRepository.set(key, times, 15, TimeUnit.MINUTES);
           } else {
               redisRepository.incrOne(key);
           }
           throw new BusinessException(e.getMessage());
       }
    }

    @Transactional
    @Override
    public void active(Long userId, String phone, boolean flag) {
        //phone 是下级
        UserModelDto sub = this.queryByName(phone);
        UserModelDto user = this.getById(userId);
        if (sub.getStatus().equals(UserStatusEnum.USDING.getCode())) {
            throw new BusinessException("用户已激活!");
        }
        userLevelService.initLevel(sub.getId(), user.getName());

        if (flag) {
            Long rdbId = currencyService.getRdbId();
            String s = configService.queryValueByName("active.quantity");
            BigDecimal quantity = new BigDecimal(s);
            UserWalletModelDto wallet = userWalletService.getByUserAndCurrencyId(user.getId(), rdbId);
            if (wallet.getUsing().compareTo(quantity) < 0) {
                throw new BusinessException("余额不足!");
            }
            wallet.setUsing(wallet.getUsing().subtract(quantity));
            userWalletService.update(wallet);
            userWalletLogService.addLog(user.getId(), rdbId, quantity.negate(), WalletLogTypeEnum.ACTIVE.getCode(), null, "激活下级" + sub.getName(), wallet);
        }
        //		 //查询前面5个激活的
//		 List<UserModelDto> list= this.getByActiveDate();
//		 if(list.contains(user)){
//		 	list.remove(user);
//		 }
//          if(CollectionUtil.isNotEmpty(list)){
//          	  for(UserModelDto item:list){
//          	  	//初始层级(把前五个变为自己的下级)
//          	  	timestampLevelService.initSubLevel(item,userId);
//
//				//初始层级（(把自己变为前五个的下级)）
//          	  	timestampLevelService.initParentLevel(item,userId);
//			  }
//		  }
        sub.setStatus(UserStatusEnum.USDING.getCode());
        sub.setActiveDate(new Date());
        List<Long> list = userMapper.getTimestamSub1(new Date());
        for (Long id : list) {
            redisRepository.del(KeyConstant.KEY_USER_TIMESTAM_SUB + id);
        }
        this.update(sub);
    }

    @Override
    public UserModelDto queryByNameOrAddress(String phone) {
        return userMapper.queryByNameOrAddress(phone);
    }

    @Transactional
    @Override
    public void active(String phone, String pwd, Long userId) {
        //phone 是上级
        UserModelDto sub = this.getById(userId);
        if (sub.getStatus().equals(UserStatusEnum.USDING.getCode())) {
            throw new BusinessException("用户已激活!");
        }
        userLevelService.initLevel(userId, phone);
        UserModelDto user = this.queryByName(phone);

        Long rdbId = currencyService.getRdbId();
        String s = configService.queryValueByName("active.quantity");
        BigDecimal quantity = new BigDecimal(s);
        UserWalletModelDto wallet = userWalletService.getByUserAndCurrencyId(user.getId(), rdbId);
        if (wallet.getUsing().compareTo(quantity) < 0) {
            throw new BusinessException("余额不足!");
        }
        wallet.setUsing(wallet.getUsing().subtract(quantity));
        userWalletService.update(wallet);
        userWalletLogService.addLog(user.getId(), rdbId, quantity.negate(), WalletLogTypeEnum.ACTIVE.getCode(), null, "激活下级" + sub.getName(), wallet);
        sub.setStatus(UserStatusEnum.USDING.getCode());
        sub.setActiveDate(new Date());
        this.update(sub);
    }

    @Override
    public UserDto getBaseInfo(Long userId) {
        UserDto dto = new UserDto();
        UserModel user = this.getById(userId);
        if (user != null) {
            UserAuthModel auth = userAuthService.getByUserId(userId);
            dto.setId(userId);
            dto.setName(user.getName());
            dto.setRealName(auth != null ? auth.getRealName() : "");
            dto.setPhone(user.getPhone());
            dto.setEmail(user.getEmail());
        }
        return dto;
    }

    /*  一、假定密码字符数范围6-16，除英文数字和字母外的字符都视为特殊字符：
        弱：^[0-9A-Za-z]{6,16}$
        中：^(?=.{6,16})[0-9A-Za-z]*[^0-9A-Za-z][0-9A-Za-z]*$
        强：^(?=.{6,16})([0-9A-Za-z]*[^0-9A-Za-z][0-9A-Za-z]*){2,}$
        二、假定密码字符数范围6-16，密码字符允许范围为ASCII码表字符：
        弱：^[0-9A-Za-z]{6,16}$
        中：^(?=.{6,16})[0-9A-Za-z]*[\x00-\x2f\x3A-\x40\x5B-\xFF][0-9A-Za-z]*$
        强：^(?=.{6,16})([0-9A-Za-z]*[\x00-\x2F\x3A-\x40\x5B-\xFF][0-9A-Za-z]*){2,}$*/
    private int checkPassword(String passwordStr) {
        int strength = 1;
        String regexZ = "\\d*";
        String regexS = "[a-zA-Z]+";
        String regexT = "\\W+$";
        String regexZT = "\\D*";
        String regexST = "[\\d\\W]*";
        String regexZS = "\\w*";
        String regexZST = "[\\w\\W]*";

        if (passwordStr.matches(regexZ)) {
            return 1;
        }
        if (passwordStr.matches(regexS)) {
            return 1;
        }
        if (passwordStr.matches(regexT)) {
            return 1;
        }
        if (passwordStr.matches(regexZT)) {
            return 2;
        }
        if (passwordStr.matches(regexST)) {
            return 2;
        }
        if (passwordStr.matches(regexZS)) {
            return 2;
        }
        if (passwordStr.matches(regexZST)) {
            return 3;
        }
        return strength;
    }
}
