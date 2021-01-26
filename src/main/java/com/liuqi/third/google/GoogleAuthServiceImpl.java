package com.liuqi.third.google;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GoogleAuthServiceImpl implements GoogleAuthService {

    @Override
    public String getUserKey(){
       return  GoogleAuthenticator.generateSecretKey();
    }
    /**
     * 验证
     *
     * @param secret 用户私钥
     * @param code   验证码
     * @return
     */
    @Override
    public boolean verify(String secret, long code) {
        long t = System.currentTimeMillis();
        GoogleAuthenticator ga = new GoogleAuthenticator();
        //ga.setWindowSize(5);
        boolean r = ga.check_code(secret, code, t);
        System.out.println("检查code是否正确？" + r);
        return r;
    }
}
