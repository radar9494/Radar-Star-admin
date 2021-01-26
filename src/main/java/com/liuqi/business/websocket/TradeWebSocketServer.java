package com.liuqi.business.websocket;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.KDto;
import com.liuqi.business.dto.TradeInfoDto;
import com.liuqi.business.enums.KTypeEnum;
import com.liuqi.business.enums.WebSocketTypeEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.ReturnResponse;
import com.liuqi.token.RedisTokenManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.*;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

/**
 * 交易websocket
 *  方法：
 *      symbols：返回K线插件基本信息{"method":"symbols","tradeId":1}
 *      kData：K线图{"method":"kData","tradeId":1,"resolution":1,"from":1569746069,"to":1569746169}
 *      trade：交易{"method":"trade","tradeId":1}
 *      check：心跳检查{"method":"check","tradeId":1}
 *      userinfo：用户信息{"method":"userinfo","tradeId":1,"token":"112233344","hasRecord":0}    //hasRecord  0不要 1要
 *      depth：深度图{"method":"depth","tradeId":1}
 *
 *      sub：订阅{"method":"sub","tradeId":1,"type":1}   type:1 交易 2深度图 3交易区域信息 4用户资产 5用户记录
 *      unsub：取消订阅{"method":"unsub","tradeId":1,"type":1}   type:1 交易 2深度图 3交易区域信息 4用户资产 5用户记录
 *      {"method":"sub","tradeId":1,"type":4,"currencyId":1,"token":"2340420bcb8c63b4ebd247cb795a4d76"}
 */
@Slf4j
@Component
@ServerEndpoint(value = "/websocket/trade",prefix = "websocket")
public class TradeWebSocketServer {

    @Autowired
    private TradeInfoCacheService tradeInfoCacheService;
    @Autowired
    private KDataService kDataService;
    @Autowired
    private CurrencyTradeService currencyTradeService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private TrusteeService trusteeService;
    @Autowired
    private TradeRecordUserService tradeRecordUserService;
    @Autowired
    private IndexService indexService;
    @Autowired
    private RedisTokenManager redisTokenManager;
    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private CollectPushHandle collectPushHandle;
    @Autowired
    private UserTradeCollectService userTradeCollectService;

    private Session session;
    //当前连接已订阅交易id
    private CopyOnWriteArraySet<Long> tradeSet = new CopyOnWriteArraySet<Long>();
    //当前连接已订阅区域id
    private CopyOnWriteArraySet<Long> areaSet = new CopyOnWriteArraySet<Long>();
    //当前连接已订阅深度id
    private CopyOnWriteArraySet<Long> deepSet = new CopyOnWriteArraySet<Long>();

    //所有订阅交易   map<交易id:list<用户>>    定时任务改为每个jvm运行  推送连接改jvm的session
    public static ConcurrentHashMap<Long, CopyOnWriteArraySet<Session>> tradeMap = new ConcurrentHashMap<Long, CopyOnWriteArraySet<Session>>(); //交易订阅
    //所有订阅区域   map<交易id:list<用户>>
    public static ConcurrentHashMap<Long, CopyOnWriteArraySet<Session>> areaMap = new ConcurrentHashMap<Long, CopyOnWriteArraySet<Session>>();  //区域订阅
    //所有订阅深度   map<交易id:list<用户>>
    public static ConcurrentHashMap<Long, CopyOnWriteArraySet<Session>> deepMap = new ConcurrentHashMap<Long, CopyOnWriteArraySet<Session>>();  //深度订阅

    public static CopyOnWriteArraySet<Session> getTradeMap(Long tradeId) {
        if (tradeId == null && tradeId <= 0) {
            return new CopyOnWriteArraySet();
        }
        CopyOnWriteArraySet<Session> set = TradeWebSocketServer.tradeMap.get(tradeId);
        if (set == null) {
            synchronized (tradeId) {
                set = TradeWebSocketServer.tradeMap.get(tradeId);
                if (set == null) {
                    set = new CopyOnWriteArraySet();
                    TradeWebSocketServer.tradeMap.put(tradeId, set);
                }
            }
        }
        return set;
    }

    public static CopyOnWriteArraySet<Session> getAreaMap(Long areaId) {
        if (areaId == null && areaId <= 0) {
            return new CopyOnWriteArraySet();
        }
        CopyOnWriteArraySet<Session> set = TradeWebSocketServer.areaMap.get(areaId);
        if (set == null) {
            synchronized (areaId) {
                set = TradeWebSocketServer.areaMap.get(areaId);
                if (set == null) {
                    set = new CopyOnWriteArraySet();
                    TradeWebSocketServer.areaMap.put(areaId, set);
                }
            }
        }
        return set;
    }

    public static CopyOnWriteArraySet<Session> getDeepMap(Long tradeId) {
        if (tradeId == null && tradeId <= 0) {
            return new CopyOnWriteArraySet();
        }
        CopyOnWriteArraySet<Session> set = TradeWebSocketServer.deepMap.get(tradeId);
        if (set == null) {
            synchronized (tradeId) {
                set = TradeWebSocketServer.deepMap.get(tradeId);
                if (set == null) {
                    set = new CopyOnWriteArraySet();
                    TradeWebSocketServer.deepMap.put(tradeId, set);
                }
            }
        }
        return set;
    }

    @OnOpen
    public void onOpen(Session session){
        this.session = session;
    }
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (!tradeSet.isEmpty()) {
            for (Long tradeId : tradeSet) {
                TradeWebSocketServer.getTradeMap(tradeId).remove(session);
            }
        }
        if (!areaSet.isEmpty()) {
            for (Long areaId : areaSet) {
                TradeWebSocketServer.getAreaMap(areaId).remove(session);
            }
        }
        if (!deepSet.isEmpty()) {
            for (Long tradeId : deepSet) {
                TradeWebSocketServer.getDeepMap(tradeId).remove(session);
            }
        }
    }


    private Long getUserId(String token, Session session) {
        //判断类型
        if (StringUtils.isEmpty(token)) {
            sendMessage("未登录", WebSocketTypeEnum.FAIL.getCode(), session);
            return -1L;
        }
        UserModel user = redisTokenManager.getUserByToken(token);
        if (user == null) {
            sendMessage("未登录", WebSocketTypeEnum.FAIL.getCode(), session);
            return -1L;
        }
        return user.getId();
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message,Session session) {
        if(StringUtils.isEmpty(message) || !message.startsWith("{")){
            return;
        }
        JSONObject obj=JSONObject.parseObject(message);
        if(!obj.containsKey("tradeId")){
            return;
        }
        String method=obj.getString("method");
        Long tradeId = obj.getLong("tradeId");
        //统计
        redisRepository.set(KeyConstant.KEY_WEBSOCKET_COUNT + session.id(), "1", 10L, TimeUnit.SECONDS);
        //基础信息
        if(method.equals("symbols")){
            JSONObject json=this.tradeInfo(tradeId);
            json.put("tradeId",tradeId);
            sendMessage(json, WebSocketTypeEnum.K.getCode());
        }else if(method.equals("kData")){//K线图
            String resolution=obj.getString("resolution");//传入的类型
            Integer type=getType(resolution);
            Date startDate=new Date(obj.getLong("from")*1000);
            Date endDate=new Date(obj.getLong("to")*1000);
            List<KDto> list=kDataService.queryCacheDataByType(type,tradeId,startDate,endDate);
            //计算出最近的一条数据
            if(endDate.compareTo(DateTime.of(new Date()).offset(DateField.SECOND,-10))>0) {
                KDto cur = kDataService.getCacheCurK(type, tradeId);
                if (cur != null) {
                    list.add(cur);
                }
            }
            sendMessage_k(list,resolution,WebSocketTypeEnum.K.getCode());
        }else if(method.equals("check")) {//心跳检查,返回一个空信息，验证连接是否接通
            JSONObject json = new JSONObject();
            json.put("tradeId",tradeId);
            sendMessage(json,WebSocketTypeEnum.CHECK.getCode());
        } else if (method.equals("trade")) {//交易信息1
            JSONObject json = tradeInfoCacheService.getTradeInfo(tradeId);
            sendMessage(json, WebSocketTypeEnum.TRADE.getCode());
        }else if(method.equals("userinfo")){//用户信息
            //查询用户基本信息
            String token=obj.getString("token");//传入的类型
            String hasRecord=obj.containsKey("hasRecord")?obj.getString("hasRecord"):"0";//是否需要记录信息0不要 1要
            if(StringUtils.isEmpty(token)){
                sendMessage("未登录",WebSocketTypeEnum.FAIL.getCode());
                return ;
            }
            Long userId = this.getUserId(token);
            if(userId <=0){
                sendMessage("未登录",WebSocketTypeEnum.FAIL.getCode());
                return ;
            }
            //用户钱包信息
            CurrencyTradeModel trade= currencyTradeService.getById(tradeId);
            JSONObject json= getUserWallet(userId,trade);
            json.put("tradeId", tradeId);

            boolean b = userTradeCollectService.hasCollect(userId, tradeId);
            json.put("isCollect", b);
            if("1".equals(hasRecord)) {
                //查询用户交易对信息
                List<TrusteeModelDto> trusteeList = trusteeService.findUserNoSuccess(userId, tradeId, true, 20);
                //查询用户已完成的交易数据  买/卖
                TradeRecordUserModelDto search = new TradeRecordUserModelDto();
                search.setUserId(userId);
                search.setTradeId(tradeId);
                search.setLimit(true);
                search.setCount(20);
                List<TradeRecordUserModelDto> recordList = tradeRecordUserService.queryListByDto(search, true);
                //是否免密
                // boolean hasTradePassword=getUserService().inputTradePassword(user.getId());


                json.put("tradeList", trusteeList);
                json.put("tradeRecordList", recordList);
            }
            //json.put("hasTradePassword",hasTradePassword);

            sendMessage(json,WebSocketTypeEnum.USER.getCode());
        }else if(method.equals("depth")){//深度图
            JSONObject json = tradeInfoCacheService.getTradeDepthInfo(tradeId,null);
            sendMessage(json,WebSocketTypeEnum.DEPTH.getCode());
        }else if(method.equals("area")){//区域
            Long areaId= obj.getLong("areaId");
            JSONObject json=new JSONObject();
            List<TradeInfoDto>  dtoList=indexService.getByAreaId(areaId);
            json.put("tradeList",dtoList);
            json.put("areaId",areaId);
            sendMessage(json,WebSocketTypeEnum.AREA.getCode());
        }else if (method.equals("sub")) {//订阅
            this.sub(obj, tradeId, true);
        } else if (method.equals("unsub")) {//取消
            this.sub(obj, tradeId, false);
        }
    }
    private JSONObject getUserWallet(Long userId, CurrencyTradeModel trade){
        UserWalletModel wallet=userWalletService.getByUserAndCurrencyId(userId,trade.getCurrencyId());
        UserWalletModel tradeWallet=userWalletService.getByUserAndCurrencyId(userId,trade.getTradeCurrencyId());
        JSONObject obj=new JSONObject();
        obj.put("wallet",wallet);
        obj.put("tradeWallet",tradeWallet);
        return obj;
    }

    public Integer getType(String type){
        Integer searchType= KTypeEnum.ONEM.getCode();
        if(type.equals("1")){
            searchType=KTypeEnum.ONEM.getCode();
        }else if(type.equals("5")){
            searchType=KTypeEnum.FIVEM.getCode();
        }else if(type.equals("15")){
            searchType=KTypeEnum.FIFTEENM.getCode();
        }else if(type.equals("30")){
            searchType=KTypeEnum.THIRTYM.getCode();
        }else if(type.equals("60")){
            searchType=KTypeEnum.ONEH.getCode();
        }else if(type.equals("D")){
            searchType=KTypeEnum.ONED.getCode();
        }else if(type.equals("W")){
            searchType=KTypeEnum.ONEW.getCode();
        }
        return searchType;
    }

    public JSONObject tradeInfo(Long tradeId){
        CurrencyTradeModelDto trade=currencyTradeService.getById(tradeId);
        List<String> resolutions=new ArrayList<String>();
        resolutions.add("1");
        resolutions.add("5");
        resolutions.add("15");
        resolutions.add("30");
        resolutions.add("60");
        resolutions.add("D");
        resolutions.add("W");
        String name=trade.getTradeCurrencyName();
        JSONObject obj=new JSONObject();
        obj.put("ticker",trade.getId()+"");//交易id，指定后，所有数据请求
        obj.put("minmov2",0);
        obj.put("session","24x7");//交易时间
        obj.put("timezone","Asia / Shanghai");//时区
        obj.put("has_intraday",true);
        obj.put("description",name);//说明
        obj.put("supported_resolutions",resolutions);
        obj.put("type","stock");
        obj.put("currency_code","");
        obj.put("exchange - listed","");
        obj.put("volume_precision",6);
        obj.put("pointvalue",1);
        obj.put("name",name);//商品名称
        obj.put("exchange - traded","");
        obj.put("minmov",1);//最小波动
        obj.put("pricescale",10000);//价格精度
        obj.put("has_no_volume",false);
        return obj;
    }

    //type:1 交易 2深度图 2交易区域信息 4用户资产 5用户记录
    private void sub(JSONObject obj, Long tradeId, boolean sub) {
        int type = obj.getInteger("type");
        JSONObject json = null;
        switch (type) {
            case 1://1 交易
                if (sub) {
                    tradeSet.add(tradeId);
                    TradeWebSocketServer.getTradeMap(tradeId).add(session);
                    sendMessage(obj, WebSocketTypeEnum.SUB.getCode());

                    json = tradeInfoCacheService.getTradeInfo(tradeId);
                    sendMessage(json, WebSocketTypeEnum.TRADE.getCode());
                } else {
                    TradeWebSocketServer.getTradeMap(tradeId).remove(session);
                    tradeSet.remove(tradeId);
                    sendMessage(obj, WebSocketTypeEnum.UNSUB.getCode());
                }
                break;
            case 2://2深度图
                if (sub) {
                    deepSet.add(tradeId);
                    TradeWebSocketServer.getDeepMap(tradeId).add(session);
                    sendMessage(obj, WebSocketTypeEnum.SUB.getCode());

                    json = tradeInfoCacheService.getTradeDepthInfo(tradeId,null);
                    sendMessage(json, WebSocketTypeEnum.DEPTH.getCode());
                } else {
                    TradeWebSocketServer.getDeepMap(tradeId).remove(session);
                    deepSet.remove(tradeId);
                    sendMessage(obj, WebSocketTypeEnum.UNSUB.getCode());
                }
                break;
            case 3://3交易区域信息
                Long areaId = obj.getLong("areaId");
                if (sub) {
                    areaSet.add(areaId);
                    TradeWebSocketServer.getAreaMap(areaId).add(session);
                    sendMessage(obj, WebSocketTypeEnum.SUB.getCode());

                    json = new JSONObject();
                    List<TradeInfoDto> dtoList = indexService.getByAreaId(areaId);
                    json.put("tradeList", dtoList);
                    json.put("areaId",areaId);
                    sendMessage(json, WebSocketTypeEnum.AREA.getCode());
                } else {
                    TradeWebSocketServer.getAreaMap(areaId).remove(session);
                    areaSet.remove(areaId);
                    sendMessage(obj, WebSocketTypeEnum.UNSUB.getCode());
                }
                break;
            case 6://自选区域
                String token = obj.getString("token");
                Long userId = getUserId(token, session);
                if (sub) {
                    collectPushHandle.add(userId, session);
                } else {
                    collectPushHandle.del(userId);
                    sendMessage(obj, WebSocketTypeEnum.UNSUB.getCode(), session);
                }
                break;
            default:
        }
    }

    /**
     * 获取用户信息
     *
     * @param token
     * @return
     */
    private Long getUserId(String token) {
        //判断类型
        if (StringUtils.isEmpty(token)) {
            sendMessage("未登录", WebSocketTypeEnum.FAIL.getCode());
            return -1L;
        }
        UserModel user = redisTokenManager.getUserByToken(token);
        if (user == null) {
            sendMessage("未登录", WebSocketTypeEnum.FAIL.getCode());
            return -1L;
        }
        return user.getId();
    }
    public void sendMessage(Object obj, int code, Session session) {
        if (session.isWritable()) {
            ReturnResponse response = ReturnResponse.builder().code(code).obj(obj).time(System.currentTimeMillis()).build();
            session.sendText(JSONObject.toJSONString(response));
        }
    }


    /**
     * 发送的消息
     * @param obj 发送的消息
     * @param code TradeWebSocketServer常量  0K线数据  1交易数据
     * @throws IOException
     */
    public void sendMessage(Object obj,int code){
        if (this.session.isWritable()) {
            ReturnResponse response = ReturnResponse.builder().code(code).obj(obj).time(System.currentTimeMillis()).build();
            this.session.sendText(JSONObject.toJSONString(response));
        }
    }

    /**
     * 发送的消息
     * @param obj 发送的消息
     * @throws IOException
     */
    public void sendMessage_k(Object obj,String resolution,int code){
        if (this.session.isWritable()) {
            ReturnResponse response = ReturnResponse.builder().code(code).obj(obj).time(System.currentTimeMillis()).build();
            response.setOther(resolution);
            this.session.sendText(JSONObject.toJSONString(response));
        }
    }
    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        //error.printStackTrace();
    }

    public static void sendTradeMessage(Long tradeId, String msg) {
        CopyOnWriteArraySet<Session> set = TradeWebSocketServer.getTradeMap(tradeId);
        if (!set.isEmpty()) {
            for (Session session : set) {
                if (session.isWritable()) {
                    session.sendText(msg);
                }
            }
        }
    }

    public static void sendAreaMessage(Long areaId, String msg) {
        CopyOnWriteArraySet<Session> set = TradeWebSocketServer.getAreaMap(areaId);
        if (!set.isEmpty()) {
            for (Session session : set) {
                if (session.isWritable()) {
                    session.sendText(msg);
                }
            }
        }
    }


    public static void sendDeepMessage(Long tradeId, String msg) {
        CopyOnWriteArraySet<Session> set = TradeWebSocketServer.getDeepMap(tradeId);
        if (!set.isEmpty()) {
            for (Session session : set) {
                if (session.isWritable()) {
                    session.sendText(msg);
                }
            }
        }
    }
}
