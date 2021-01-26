package com.liuqi.base;

import com.liuqi.business.enums.TableIdNameEnum;
import com.liuqi.business.model.KDataModel;
import com.liuqi.business.service.KDataService;
import com.liuqi.business.service.TableIdService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

/**
 * @author tanyan
 * @create 2019-12=08
 * @description
 */
public class IdTest extends BaseTest {
    @Autowired
    private KDataService kDataService;
    @Test
    public void test01(){
        System.out.println("------->");
        System.out.println("------->");
        System.out.println("------->");
        System.out.println("------->");
        System.out.println("------->");
        System.out.println("------->");
        for(int i=0;i<10000;i++) {
            KDataModel k=new KDataModel();
            k.setTradeId(1L);
            k.setType(1);
            k.setClosePrice(new BigDecimal("1"));
            k.setOpenPrice(new BigDecimal("1"));
            k.setNums(new BigDecimal("1"));
            k.setMaxPrice(new BigDecimal("1"));
            k.setMinPrice(new BigDecimal("1"));
            kDataService.insert(k);
        }

    }
}
