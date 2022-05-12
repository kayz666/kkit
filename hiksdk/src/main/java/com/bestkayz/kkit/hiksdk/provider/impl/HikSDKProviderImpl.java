package com.bestkayz.kkit.hiksdk.provider.impl;

import com.alibaba.fastjson.JSONObject;
import com.bestkayz.kkit.common.core.base.Result;
import com.bestkayz.kkit.common.core.utils.ObjectAssertUtil;
import com.bestkayz.kkit.common.tools.DateTimeUtil;
import com.bestkayz.kkit.hiksdk.exceptions.HikSDKException;
import com.bestkayz.kkit.hiksdk.lib.*;
import com.bestkayz.kkit.hiksdk.provider.HikSDKProvider;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;


import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;


/**
 * @Author : kayz
 * @Date : 2020/5/6
 * @Version 1.0
 */
@Slf4j
@Setter
@Getter
@Accessors(chain = true)
public class HikSDKProviderImpl extends HikSDKDevice implements HikSDKProvider {

    private final static Integer MAX_DELAY_TIME = 30*1000;
    private boolean isLocked = false;

    private HCNetSDK hCNetSDK = HCNetSDK.INSTANCE;
    private int lUserID = -1;//用户句柄

    private int lAlarmHandle = -1;

    private String ip = "192.168.1.1";
    private Short port = 8000;
    private String userName = "admin";
    private String password = "Admin12345";

    private JSONObject deviceMap = new JSONObject();
    private String deviceId;

    private FMSGCallBack fMSFCallBack;
    private FMSGCallBack_V31 fmsgCallBack_v31;

    private HikSDKProviderImpl(){
    }

    public HikSDKProvider build(){
        InvocationHandler invocationHandler = new HikAspectImpl(this);
        return  (HikSDKProvider) Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                this.getClass().getInterfaces(),
                invocationHandler
        );
    }

    public HikSDKProviderImpl addDeviceProperty(String key,Object data){
        deviceMap.put(key,data);
        return this;
    }

    public Object getDeviceProperty(String key){
        return deviceMap.get(key);
    }

    public boolean isLogin(){
        return lUserID != -1;
    }

    public static HikSDKProviderImpl getInstance(String deviceId){
        HikSDKProviderImpl hikSDKProviderImpl = new HikSDKProviderImpl();
        hikSDKProviderImpl.hCNetSDK.NET_DVR_Init();
        hikSDKProviderImpl.deviceId = deviceId;
        return hikSDKProviderImpl;
    }


    public synchronized void lock(){
        //log.info("准备获得锁...");
        while (isLocked){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //log.info("锁已获得");
        isLocked =true;
    }

    public synchronized void unlock(){
        //log.info("锁已释放");
        isLocked = false;
        notify();
    }

    private HikSDKException handleException(String msg,Integer code){
        return new HikSDKException(code,"IP:"+ ip+ " ,操作:" +msg+" ,代码:"+ code + " 错误:" + HCNetSDKErrorCode.codeMsg(code));
    }
    private HikSDKException handleException(String msg,Exception e){
        return new HikSDKException(-1,"IP:"+ ip+ " ,操作:" +msg+" ,代码:"+ -1 + " 错误:" + HCNetSDKErrorCode.codeMsg(-1));
    }

    public void login() {
        try {
            HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
            lUserID = hCNetSDK.NET_DVR_Login_V30(ip,
                    port, userName, password, m_strDeviceInfo);
            if (lUserID == -1)
            {
                int errorCode = hCNetSDK.NET_DVR_GetLastError();
                if (errorCode == 52){
                    // 先登出后再尝试重新登陆
                    //log.debug("错误码52  尝试登出...{}",lUserID);
                    hCNetSDK.NET_DVR_Logout_V30(lUserID);
                    //log.debug("重试登陆");
                    lUserID = hCNetSDK.NET_DVR_Login_V30(ip,
                            port, userName, password, m_strDeviceInfo);
                    if (lUserID == -1){
                        throw handleException("登录失败",hCNetSDK.NET_DVR_GetLastError());
                    }
                }else {
                    throw handleException("登录失败",hCNetSDK.NET_DVR_GetLastError());
                }
            }
            Pointer pUser = null;
            if (fmsgCallBack_v31 != null) {
                if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fmsgCallBack_v31, pUser))
                {
                    throw handleException("设置回调函数失败",hCNetSDK.NET_DVR_GetLastError());
                }
                HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
                m_strAlarmInfo.dwSize=m_strAlarmInfo.size();
                m_strAlarmInfo.byLevel=1;//智能交通布防优先级：0- 一等级（高），1- 二等级（中），2- 三等级（低）
                m_strAlarmInfo.byAlarmInfoType=1;//智能交通报警信息上传类型：0- 老报警信息（NET_DVR_PLATE_RESULT），1- 新报警信息(NET_ITS_PLATE_RESULT)
                m_strAlarmInfo.byDeployType =0; //布防类型(仅针对门禁主机、人证设备)：0-客户端布防(会断网续传)，1-实时布防(只上传实时数据)
                m_strAlarmInfo.write();
                lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V41(lUserID, m_strAlarmInfo);
                if (lAlarmHandle == -1){
                    throw handleException("布防失败",hCNetSDK.NET_DVR_GetLastError());
                }else {
                    log.debug("布防成功");
                }
            }
            log.info("设备登录成功 IP:{}",ip);
        }catch (Exception e){
            throw e;
        }
    }


    public void logout(){
        try {
            if (hCNetSDK.NET_DVR_Logout_V30(lUserID)){
                log.info("设备登出成功 IP:{}",ip);
            }else {
                //log.warn("登出失败");
            }
            lUserID = -1;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void cleanUp(){
        try {
            hCNetSDK.NET_DVR_Cleanup();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void init(){
        login();
    }

    @Override
    public void release() {
        ObjectAssertUtil.delay(50);
        cleanUp();
    }


    public Result addPersonInfo(String personId, String name){
        String commandUrl = "POST /ISAPI/AccessControl/UserInfo/Record?format=json";
        HCNetSDK.BYTE_ARRAY ptrByteArray = new HCNetSDK.BYTE_ARRAY(1024);
        System.arraycopy(commandUrl.getBytes(), 0,
                ptrByteArray.byValue, 0, commandUrl.length());
        ptrByteArray.write();
        int handler = hCNetSDK.NET_DVR_StartRemoteConfig(
                lUserID,
                2550,
                ptrByteArray.getPointer(),
                commandUrl.length(),
                null,
                null);
        //如果获取长连接失败，则进行重连
        if (handler < 0) {
            throw  handleException("获取长连接失败",hCNetSDK.NET_DVR_GetLastError());
        }
        try {
            byte[] Name = null;
            try {
                Name = name.getBytes("utf-8");
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            //String strInBuffer1 = "{\"UserInfo\":{\"Valid\":{\"beginTime\":\"2017-08-01T17:30:08\",\"enable\":true,\"endTime\":\"2030-08-01T17:30:08\"},\"employeeNo\":\"%s\",\"name\":\"";
            String strInBuffer1 = "{\"UserInfo\":{\"Valid\":{\"beginTime\":\"2017-08-01T17:30:08\",\"enable\":true,\"endTime\":\"2030-08-01T17:30:08\"},\"checkUser\":false,\"doorRight\":\"1\",\"RightPlan\":[{\"doorNo\": 1,\"planTemplateNo\": \"1,3,5\"}],\"employeeNo\":\"" +
                    "%s\",\"floorNumber\":1,\"maxOpenDoorTime\":0,\"name\":\"";
            strInBuffer1 = String.format(strInBuffer1,personId);
            String strInBuffer2 = "\",\"openDelayEnabled\":false,\"password\":\"123456\",\"roomNumber\":1,\"userType\":\"normal\"}}";
            int iStringSize = Name.length + strInBuffer1.length() + strInBuffer2.length();
            HCNetSDK.BYTE_ARRAY ptrByteArrayJsonInput = new HCNetSDK.BYTE_ARRAY(iStringSize);
            System.arraycopy(strInBuffer1.getBytes(), 0, ptrByteArrayJsonInput.byValue, 0, strInBuffer1.length());
            System.arraycopy(Name, 0, ptrByteArrayJsonInput.byValue, strInBuffer1.length(), Name.length);
            System.arraycopy(strInBuffer2.getBytes(), 0, ptrByteArrayJsonInput.byValue, strInBuffer1.length() + Name.length, strInBuffer2.length());
            ptrByteArrayJsonInput.write();

            HCNetSDK.BYTE_ARRAY ptrByteArrayJsonOutput = new HCNetSDK.BYTE_ARRAY(2048);
            IntByReference iOutpuSize = new IntByReference(0);
            int result = hCNetSDK.NET_DVR_SendWithRecvRemoteConfig(
                    handler,
                    ptrByteArrayJsonInput.getPointer(),
                    ptrByteArrayJsonInput.byValue.length,
                    ptrByteArrayJsonOutput.getPointer(),
                    1024*4,
                    iOutpuSize
            );
            if(result < 0)
            {
                throw  handleException("添加人员信息",hCNetSDK.NET_DVR_GetLastError());
            }

            ptrByteArrayJsonOutput.read();
            byte[] strOut = new byte[2048];
            System.arraycopy(ptrByteArrayJsonOutput.byValue, 0, strOut, 0, iOutpuSize.getValue());
            String strRet = new String(strOut).trim();
            JSONObject res = JSONObject.parseObject(strRet);
            if (res.getInteger("statusCode") == 1){
                return Result.success();
            }else {
                log.debug("下发人员信息失败  设备返回:{}",strRet);
                return Result.fail(res.getString("subStatusCode"));
            }
        }finally {
            if( handler > 0 ){
                hCNetSDK.NET_DVR_StopRemoteConfig(handler);
                // System.out.println("关闭长连接！");
            }
        }
    }


    public Result addPersonCard(String personId,String cardNo){
        String commandUrl = "PUT /ISAPI/AccessControl/CardInfo/SetUp?format=json";
        HCNetSDK.BYTE_ARRAY ptrByteArray = new HCNetSDK.BYTE_ARRAY(1024);
        System.arraycopy(commandUrl.getBytes(), 0,
                ptrByteArray.byValue, 0, commandUrl.length());
        ptrByteArray.write();
        int handler = hCNetSDK.NET_DVR_StartRemoteConfig(
                lUserID,
                2550,
                ptrByteArray.getPointer(),
                commandUrl.length(),
                null,
                null);
        //如果获取长连接失败，则进行重连
        if (handler < 0) {
            throw  handleException("获取长连接失败",hCNetSDK.NET_DVR_GetLastError());
        }
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject cardInfo = new JSONObject();
            cardInfo.put("employeeNo",""+personId);
            cardInfo.put("cardNo",cardNo);
            cardInfo.put("cardType","normalCard");
            jsonObject.put("CardInfo",cardInfo);
            String str = jsonObject.toJSONString();
            byte[] bytes = str.getBytes();
            HCNetSDK.BYTE_ARRAY ptrByteArrayJsonInput = new HCNetSDK.BYTE_ARRAY(bytes.length);
            System.arraycopy(bytes,0,ptrByteArrayJsonInput.byValue,0,bytes.length);
            ptrByteArrayJsonInput.write();

            HCNetSDK.BYTE_ARRAY ptrByteArrayJsonOutput = new HCNetSDK.BYTE_ARRAY(2048);
            IntByReference iOutpuSize = new IntByReference(0);
            int result = hCNetSDK.NET_DVR_SendWithRecvRemoteConfig(
                    handler,
                    ptrByteArrayJsonInput.getPointer(),
                    ptrByteArrayJsonInput.byValue.length,
                    ptrByteArrayJsonOutput.getPointer(),
                    1024*4,
                    iOutpuSize
            );
            if(result < 0)
            {
                return Result.fail(HCNetSDKErrorCode.codeMsg(hCNetSDK.NET_DVR_GetLastError()));
            }

            ptrByteArrayJsonOutput.read();
            byte[] strOut = new byte[2048];
            System.arraycopy(ptrByteArrayJsonOutput.byValue, 0, strOut, 0, iOutpuSize.getValue());
            String strRet = new String(strOut).trim();
            //log.info(strRet);
            JSONObject res = JSONObject.parseObject(strRet);
            if (res.getInteger("statusCode") == 1){
                log.debug("下发卡片成功 卡号{}",cardNo);
                return Result.success();
            }else {
                log.debug("下发卡片异常  返回:{}",strRet);
                return Result.fail(res.getString("subStatusCode"));
            }
        }finally {
            if( handler > 0 ){
                hCNetSDK.NET_DVR_StopRemoteConfig(handler);
                // System.out.println("关闭长连接！");
            }
        }
    }


    @Override
    public Result addPersonFace(String personId, byte[] face) {
        String commandUrl = "POST /ISAPI/Intelligent/FDLib/FaceDataRecord?format=json ";
        HCNetSDK.BYTE_ARRAY ptrByteArray = new HCNetSDK.BYTE_ARRAY(1024);
        System.arraycopy(commandUrl.getBytes(), 0,
                ptrByteArray.byValue, 0, commandUrl.length());
        ptrByteArray.write();
        int handler = hCNetSDK.NET_DVR_StartRemoteConfig(
                lUserID,
                2551,
                ptrByteArray.getPointer(),
                commandUrl.length(),
                null,
                null);
        //如果获取长连接失败，则进行重连
        if (handler < 0) {
            throw  handleException("获取长连接失败",hCNetSDK.NET_DVR_GetLastError());
        }
        try {
            HCNetSDK.NET_DVR_JSON_DATA_CFG  faceJsonDataCfg = new HCNetSDK.NET_DVR_JSON_DATA_CFG();
            faceJsonDataCfg.read();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("faceLibType", "blackFD");
            jsonObject.put("FDID", "1");
            jsonObject.put("FPID", ""+personId);//人脸下发关联的工号
            String strJsonData = jsonObject.toJSONString();
            System.arraycopy(strJsonData.getBytes(), 0, ptrByteArray.byValue, 0, strJsonData.length());//字符串拷贝到数组中
            ptrByteArray.write();

            faceJsonDataCfg.dwSize = faceJsonDataCfg.size();
            faceJsonDataCfg.lpJsonData = ptrByteArray.getPointer();
            faceJsonDataCfg.dwJsonDataSize = strJsonData.length();

            HCNetSDK.BYTE_ARRAY faceArray = new HCNetSDK.BYTE_ARRAY(face.length);
            System.arraycopy(face,0,faceArray.byValue,0,face.length);
            faceArray.write();

            faceJsonDataCfg.lpPicData = faceArray.getPointer();
            faceJsonDataCfg.dwPicDataSize = face.length;
            faceJsonDataCfg.write();
            HCNetSDK.BYTE_ARRAY ptrByteArrayJsonOutput = new HCNetSDK.BYTE_ARRAY(2048);
            IntByReference iOutpuSize = new IntByReference(0);
            int result = hCNetSDK.NET_DVR_SendWithRecvRemoteConfig(
                    handler,
                    faceJsonDataCfg.getPointer(),
                    faceJsonDataCfg.size(),
                    ptrByteArrayJsonOutput.getPointer(),
                    1024*4,
                    iOutpuSize
            );
            if(result < 0)
            {
                return Result.fail(HCNetSDKErrorCode.codeMsg(hCNetSDK.NET_DVR_GetLastError()) + " face size:" + face.length);
            }

            ptrByteArrayJsonOutput.read();
            byte[] strOut = new byte[2048];
            System.arraycopy(ptrByteArrayJsonOutput.byValue, 0, strOut, 0, iOutpuSize.getValue());
            String strRet = new String(strOut).trim();
            //log.info(strRet);
            JSONObject res = JSONObject.parseObject(strRet);
            Integer statusCode = res.getInteger("statusCode");
            if (statusCode == 1){
                log.debug("人脸下发成功 照片大小{}",face.length);
                return Result.success();
            }else {
                if (statusCode == 6) {
                    String subStatusCode = res.getString("subStatusCode");
                    if ("pupilDistanceTooSmall".equals(subStatusCode)){
                        return Result.fail("瞳孔距离太小");
                    }else {
                        return Result.fail(res.getString("subStatusCode"));
                    }
                }else {
                    return Result.fail(HCNetSDKErrorCode.convertFaceErrMsg(statusCode));
                }
            }
        }finally {
            if( handler > 0 ){
                hCNetSDK.NET_DVR_StopRemoteConfig(handler);
                // System.out.println("关闭长连接！");
            }
        }

    }

    public static final int ISAPI_DATA_LEN = 1024*1024;
    public static final int ISAPI_STATUS_LEN = 4*4096;
    public static final int BYTE_ARRAY_LEN = 1024;

    public Result delUserInfo(String strEmployeeID){
        String strURL = "PUT /ISAPI/AccessControl/UserInfo/Delete?format=json";
        HCNetSDK.BYTE_ARRAY ptrUrl = new HCNetSDK.BYTE_ARRAY(BYTE_ARRAY_LEN);
        System.arraycopy(strURL.getBytes(), 0, ptrUrl.byValue, 0, strURL.length());
        ptrUrl.write();

        //输入删除条件
        HCNetSDK.BYTE_ARRAY ptrInBuffer = new HCNetSDK.BYTE_ARRAY(ISAPI_DATA_LEN);
        ptrInBuffer.read();
        String strInbuffer = "{\"UserInfoDelCond\":{\"EmployeeNoList\":[{\"employeeNo\":\"" + strEmployeeID + "\"}]}}";
        ptrInBuffer.byValue = strInbuffer.getBytes();
        ptrInBuffer.write();

        HCNetSDK.NET_DVR_XML_CONFIG_INPUT struXMLInput = new HCNetSDK.NET_DVR_XML_CONFIG_INPUT();
        struXMLInput.read();
        struXMLInput.dwSize = struXMLInput.size();
        struXMLInput.lpRequestUrl = ptrUrl.getPointer();
        struXMLInput.dwRequestUrlLen = ptrUrl.byValue.length;
        struXMLInput.lpInBuffer = ptrInBuffer.getPointer();
        struXMLInput.dwInBufferSize = ptrInBuffer.byValue.length;
        struXMLInput.write();

        HCNetSDK.BYTE_ARRAY ptrStatusByte = new HCNetSDK.BYTE_ARRAY(ISAPI_STATUS_LEN);
        ptrStatusByte.read();

        HCNetSDK.BYTE_ARRAY ptrOutByte = new HCNetSDK.BYTE_ARRAY(ISAPI_DATA_LEN);
        ptrOutByte.read();

        HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT struXMLOutput = new HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT();
        struXMLOutput.read();
        struXMLOutput.dwSize = struXMLOutput.size();
        struXMLOutput.lpOutBuffer = ptrOutByte.getPointer();
        struXMLOutput.dwOutBufferSize = ptrOutByte.size();
        struXMLOutput.lpStatusBuffer = ptrStatusByte.getPointer();
        struXMLOutput.dwStatusSize  = ptrStatusByte.size();
        struXMLOutput.write();

        if(!hCNetSDK.NET_DVR_STDXMLConfig(lUserID, struXMLInput, struXMLOutput)) {
            return Result.fail(HCNetSDKErrorCode.codeMsg(hCNetSDK.NET_DVR_GetLastError()));
        } else {
            struXMLOutput.read();
            ptrOutByte.read();
            ptrStatusByte.read();
            String strOutXML = new String(ptrOutByte.byValue).trim();
            JSONObject reback = JSONObject.parseObject(strOutXML);
            int statusCode = reback.getInteger("statusCode");
            String statusString = reback.getString("statusString");
            if (statusCode == 1) {
                return Result.success();
            }else {
                return Result.fail("删除设备内人员失败 机号:"+deviceId+" SNO:"+strEmployeeID+" 错误信息:"+statusString+" 返回数据:"+strOutXML);
            }
        }
    }

    private static final String strInBuffer1 = "{\"UserInfoSearchCond\":{\"searchID\":\"1233\",\"searchResultPosition\":0,\"maxResults\":30,\"EmployeeNoList\":[{\"employeeNo\":\"";
    private static final String strInBuffer2 = "\"}]}}";

    public Result queryDeviceEmployee(String sno){
        String commandUrl = "POST /ISAPI/AccessControl/UserInfo/Search?format=json";
        HCNetSDK.BYTE_ARRAY ptrByteArray = new HCNetSDK.BYTE_ARRAY(1024);
        System.arraycopy(commandUrl.getBytes(), 0,
                ptrByteArray.byValue, 0, commandUrl.length());
        ptrByteArray.write();
        int handler = hCNetSDK.NET_DVR_StartRemoteConfig(
                lUserID,
                2550,
                ptrByteArray.getPointer(),
                commandUrl.length(),
                null,
                null);
        //如果获取长连接失败，则进行重连
        if (handler < 0) {
            throw  handleException("获取长连接失败",hCNetSDK.NET_DVR_GetLastError());
        }
        try {

            String queryStr = strInBuffer1+sno+strInBuffer2;

            HCNetSDK.BYTE_ARRAY ptrByteArrayJsonInput = new HCNetSDK.BYTE_ARRAY(queryStr.length());
            System.arraycopy(queryStr.getBytes(), 0, ptrByteArrayJsonInput.byValue, 0, queryStr.length());
            ptrByteArrayJsonInput.write();

            HCNetSDK.BYTE_ARRAY ptrByteArrayJsonOutput = new HCNetSDK.BYTE_ARRAY(2048);
            IntByReference iOutpuSize = new IntByReference(0);
            int result = hCNetSDK.NET_DVR_SendWithRecvRemoteConfig(
                    handler,
                    ptrByteArrayJsonInput.getPointer(),
                    ptrByteArrayJsonInput.byValue.length,
                    ptrByteArrayJsonOutput.getPointer(),
                    1024*4,
                    iOutpuSize
            );
            if(result < 0)
            {
                throw  handleException("查询人员信息",hCNetSDK.NET_DVR_GetLastError());
            }

            ptrByteArrayJsonOutput.read();
            byte[] strOut = new byte[2048];
            System.arraycopy(ptrByteArrayJsonOutput.byValue, 0, strOut, 0, iOutpuSize.getValue());
            String strRet = new String(strOut).trim();
            JSONObject reback = JSONObject.parseObject(strRet);
            JSONObject searchRe = reback.getJSONObject("UserInfoSearch");
            if (searchRe != null) {
                String statusStrg = searchRe.getString("responseStatusStrg");
                if (!"NO MATCH".equals(statusStrg)){
                    return Result.success(true);
                }else {
                    return Result.success(false);
                }
            }else {
                int statusCode = reback.getInteger("statusCode");
                String statusString = reback.getString("statusString");
                return Result.fail("查询人员信息失败 机号:"+deviceId+" SNO:"+sno+" 错误信息:"+statusString+" 返回数据:"+strRet);
            }
        }finally {
            if( handler > 0 ){
                hCNetSDK.NET_DVR_StopRemoteConfig(handler);
                // System.out.println("关闭长连接！");
            }
        }

    }

    public Result<List<Map<String,Object>>> getRecord(long startTime, long endTime,int count){
        try {
            HCNetSDK.NET_DVR_ACS_EVENT_COND struEventCond = new HCNetSDK.NET_DVR_ACS_EVENT_COND();
            struEventCond.read();
            struEventCond.dwSize = struEventCond.size();
            struEventCond.dwMajor = 5;
            struEventCond.dwMinor = 75;
            if (startTime > 1924963199000L){
                startTime = 1924963199000L;
            }
            if (endTime > 1924963199000L){
                endTime = 1924963199000L;
            }
            Calendar calendar =  new Calendar.Builder()
                    .setInstant(startTime)
                    .build();
            Calendar endCalendar = new Calendar.Builder()
                    .setInstant(endTime)
                    .build();
            struEventCond.struStartTime.dwYear = calendar.get(Calendar.YEAR);
            struEventCond.struStartTime.dwMonth = calendar.get(Calendar.MONTH)+1;
            struEventCond.struStartTime.dwDay = calendar.get(Calendar.DATE);
            struEventCond.struStartTime.dwHour = calendar.get(Calendar.HOUR_OF_DAY);
            struEventCond.struStartTime.dwMinute = calendar.get(Calendar.MINUTE);
            struEventCond.struStartTime.dwSecond = calendar.get(Calendar.SECOND);

            struEventCond.struEndTime.dwYear = endCalendar.get(Calendar.YEAR);
            struEventCond.struEndTime.dwMonth = endCalendar.get(Calendar.MONTH)+1;
            struEventCond.struEndTime.dwDay = endCalendar.get(Calendar.DATE);
            struEventCond.struEndTime.dwHour = endCalendar.get(Calendar.HOUR_OF_DAY);
            struEventCond.struEndTime.dwMinute = endCalendar.get(Calendar.MINUTE);
            struEventCond.struEndTime.dwSecond = endCalendar.get(Calendar.SECOND);

            log.info(struEventCond.struStartTime.toStringTime());
            log.info(struEventCond.struEndTime.toStringTime());

            struEventCond.byPicEnable = 0;
            struEventCond.byTimeType = 0;
            struEventCond.dwBeginSerialNo = 0;
            struEventCond.dwEndSerialNo = 0;
            struEventCond.dwIOTChannelNo = 0;

            struEventCond.wInductiveEventType = 0;
            struEventCond.bySearchType = 0;
            //struEventCond.szMonitorID = 0;

            struEventCond.write();
            Pointer ptrEventCond = struEventCond.getPointer();
            int m_lsetEventCfgHandle = hCNetSDK.NET_DVR_StartRemoteConfig(lUserID,HCNetSDK.NET_DVR_GET_ACS_EVENT,ptrEventCond,struEventCond.size(),null,null);
            if (m_lsetEventCfgHandle == -1) {
                throw  handleException("获取长连接失败",hCNetSDK.NET_DVR_GetLastError());            } else {
                //log.debug("建立长连接成功！");
            }

            HCNetSDK.NET_DVR_ACS_EVENT_CFG struEventCfg = new HCNetSDK.NET_DVR_ACS_EVENT_CFG();
            struEventCfg.read();
            struEventCfg.dwSize = struEventCfg.size();
            struEventCfg.write();
            List<Map<String,Object>> list = new ArrayList<>();
            int delayTime = MAX_DELAY_TIME/10;
            int dwState;
            while(true){
                dwState = hCNetSDK.NET_DVR_GetNextRemoteConfig(m_lsetEventCfgHandle,struEventCfg.getPointer(),struEventCfg.size());
                struEventCfg.read();
                if(dwState == -1){
                    throw  handleException("NET_DVR_SendWithRecvRemoteConfig接口调用失败",hCNetSDK.NET_DVR_GetLastError());                }
                else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_NEEDWAIT)
                {
                    //log.debug("配置等待");
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    delayTime--;
                    if (delayTime <= 0) return Result.fail("配置等待超时");
                    continue;
                }
                else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_FAILED)
                {
                    return Result.fail("获取记录失败");
                }
                else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_EXCEPTION)
                {
                    return Result.fail("获取记录异常");
                }
                else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_SUCCESS)
                {
                    try {
                        HCNetSDK.NET_DVR_IPADDR ipaddr = struEventCfg.struRemoteHostAddr;
                        //ipaddr.read();
                        HCNetSDK.NET_DVR_ACS_EVENT_DETAIL event_detail = struEventCfg.struAcsEventInfo;
                        //event_detail.read();
//
//                        log.info("{},{},{},{},{},{},{},{},{],{],{},{},{},{},{},{},{}",
//                                struEventCfg.struTime.toStringTime(),
//                                event_detail.dwSerialNo,
//                                new String(event_detail.byCardNo).trim(),
//                                new String(struEventCfg.struRemoteHostAddr.sIpV4),
//                                new String(event_detail.byMACAddr),
//                                new String(event_detail.byEmployeeNo),
//                                "          ",
//                                event_detail.byCurrentVerifyMode,
//                                event_detail.byCardType,
//                                struEventCfg.dwMajor,
//                                struEventCfg.dwMinor,
//                                new String(struEventCfg.sNetUser,"GBK").trim(),
//                                struEventCfg.dwPicDataLen,
//                                struEventCfg.wInductiveEventType,
//                                struEventCfg.byTimeType
//                        );
                        Map<String,Object> record = new HashMap<>();
                        record.put("time",convertTime(struEventCfg.struTime));
                        record.put("serialNo",event_detail.dwSerialNo);
                        record.put("employeeNo",event_detail.dwEmployeeNo);
                        record.put("cardNo",new String(event_detail.byCardNo).trim());
                        record.put("picSize",struEventCfg.dwPicDataLen);
                        list.add(record);
                        if (list.size() >= count) {
                            break;
                        }
                    } catch (Exception e) {
                        log.error("海康设备主动读取记录异常 "+ e.getMessage());
                    }
                    continue;
                }
                else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_FINISH) {
                    //log.debug("获取记录结束");
                    break;
                }

            }
            return Result.success(list);
        }catch (Exception e){
            return Result.fail(e.getMessage());
        }
    }

    private long convertTime(HCNetSDK.NET_DVR_TIME time){
        Calendar calendar = Calendar.getInstance();
        calendar.set(time.dwYear,time.dwMonth-1,time.dwDay,time.dwHour,time.dwMinute,time.dwSecond);
        return calendar.getTimeInMillis();
    }

    @Override
    public Result openDoor() {
        try {
            if (!hCNetSDK.NET_DVR_ControlGateway(lUserID,-1,1)){
                return Result.fail("返回值为FLASE");
            }
        }catch (Exception e){
            return Result.fail(e.getMessage());
        }
        return Result.success();
    }

    @Override
    public HCNetSDK.NET_DVR_DEVICECFG_V40 getDeviceInfo(){
        try {
            HCNetSDK.NET_DVR_DEVICECFG_V40 devicecfg_v40 = new HCNetSDK.NET_DVR_DEVICECFG_V40();
            devicecfg_v40.read();
            devicecfg_v40.write();
            IntByReference pInt = new IntByReference(0);
            boolean re = hCNetSDK.NET_DVR_GetDVRConfig(lUserID,1100,0xFFFFFFFF,devicecfg_v40.getPointer(),devicecfg_v40.size(),pInt);
            if (re){
                devicecfg_v40.read();
                return devicecfg_v40;
            }else {
                throw handleException("获取设备信息",hCNetSDK.NET_DVR_GetLastError());
            }
        }catch (Exception e){
            throw handleException(e.getMessage(),-1);
        }
    }

    @Override
    public Date getTime()  {
        try {
            HCNetSDK.NET_DVR_TIME struDvrGetTime = new HCNetSDK.NET_DVR_TIME();
            struDvrGetTime.read();
            struDvrGetTime.write();
            IntByReference pInt = new IntByReference(0);
            boolean re = hCNetSDK.NET_DVR_GetDVRConfig(lUserID,HCNetSDK.NET_DVR_GET_TIMECFG,0xFFFFFFFF,struDvrGetTime.getPointer(),struDvrGetTime.size(),pInt);
            if (re){
                struDvrGetTime.read();
                // log.info("获取到设备时间："+struDvrGetTime.dwYear+"-"+struDvrGetTime.dwMonth+"-"+struDvrGetTime.dwDay+" "+struDvrGetTime.dwHour+":"+struDvrGetTime.dwMinute+":"+struDvrGetTime.dwSecond);
                try {
                    Date date = DateTimeUtil.formatTime(struDvrGetTime.dwYear+"-"+struDvrGetTime.dwMonth+"-"+struDvrGetTime.dwDay+" "+struDvrGetTime.dwHour+":"+struDvrGetTime.dwMinute+":"+struDvrGetTime.dwSecond);
                    return date;
                }catch (Exception e){
                    throw handleException("转换设备时间",11);
                }
            }else {
                throw handleException("获取设备时间",hCNetSDK.NET_DVR_GetLastError());
            }
        }catch (Exception e){
            throw handleException(e.getMessage(),-1);
        }
    }

    @Override
    public Result setTime(Date date) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            HCNetSDK.NET_DVR_TIME struDvrSetTime = new HCNetSDK.NET_DVR_TIME();
            struDvrSetTime.read();
            struDvrSetTime.dwYear= calendar.get(Calendar.YEAR);
            struDvrSetTime.dwMonth= calendar.get(Calendar.MONTH)+1;
            struDvrSetTime.dwDay= calendar.get(Calendar.DATE);
            struDvrSetTime.dwHour= calendar.get(Calendar.HOUR_OF_DAY);
            struDvrSetTime.dwMinute= calendar.get(Calendar.MINUTE);
            struDvrSetTime.dwSecond= calendar.get(Calendar.SECOND);
            struDvrSetTime.write();
            boolean re = hCNetSDK.NET_DVR_SetDVRConfig(lUserID,HCNetSDK.NET_DVR_SET_TIMECFG,0xFFFFFFFF,struDvrSetTime.getPointer(),struDvrSetTime.size());
            if (re){
                log.info("设置设备时间成功");
                return Result.success();
            }else {
                int code = hCNetSDK.NET_DVR_GetLastError();
                //log.error("设置设备时间参数错误  错误码:{}",code);
                return Result.fail("错误码" + code);
            }
        }catch (Exception e){
            //log.error("",e);
            return Result.fail(e.getMessage());
        }
    }


    @Override
    public void setLog(String path) {
        try {
            hCNetSDK.NET_DVR_SetLogToFile(3,path,true);
        }catch (Exception e){
            throw handleException(e.getMessage(),-1);
        }
    }


    @Override
    public Result delOneCard(String strCardNo){
        try{
            HCNetSDK.NET_DVR_CARD_COND struCardCond = new HCNetSDK.NET_DVR_CARD_COND();
            struCardCond.read();
            struCardCond.dwSize = struCardCond.size();
            struCardCond.dwCardNum = 1;  //下发一张
            struCardCond.write();
            Pointer ptrStruCond = struCardCond.getPointer();
            int m_lSetCardCfgHandle = hCNetSDK.NET_DVR_StartRemoteConfig(lUserID, HCNetSDK.NET_DVR_DEL_CARD, ptrStruCond, struCardCond.size(),null ,null);
            if (m_lSetCardCfgHandle == -1) {
                throw  handleException("获取长连接失败",hCNetSDK.NET_DVR_GetLastError());
            }
            HCNetSDK.NET_DVR_CARD_SEND_DATA struCardData = new HCNetSDK.NET_DVR_CARD_SEND_DATA();
            struCardData.read();
            struCardData.dwSize = struCardData.size();
            for (int i = 0; i < HCNetSDK.ACS_CARD_NO_LEN; i++) {
                struCardData.byCardNo[i] = 0;
            }
            for (int i = 0; i <  strCardNo.length(); i++) {
                struCardData.byCardNo[i] = strCardNo.getBytes()[i];
            }
            struCardData.write();
            HCNetSDK.NET_DVR_CARD_STATUS struCardStatus = new HCNetSDK.NET_DVR_CARD_STATUS();
            struCardStatus.read();
            struCardStatus.dwSize = struCardStatus.size();
            struCardStatus.write();
            IntByReference pInt = new IntByReference(0);
            int dwState;
            while(true){
                dwState = hCNetSDK.NET_DVR_SendWithRecvRemoteConfig(m_lSetCardCfgHandle, struCardData.getPointer(), struCardData.size(),struCardStatus.getPointer(), struCardStatus.size(),  pInt);
                struCardStatus.read();
                if(dwState == -1){
                    return Result.fail(HCNetSDKErrorCode.codeMsg(hCNetSDK.NET_DVR_GetLastError()));
                    //throw new RuntimeException("删除人员失败，状态接口调用失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
                } else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_NEEDWAIT) {
                    //System.out.println("配置等待");
                    Thread.sleep(10);
                    continue;
                } else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_FAILED) {

                    return Result.fail("删除卡失败,错误码：" + struCardStatus.dwErrorCode);
                } else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_EXCEPTION) {
                    return Result.fail("删除卡异常,错误码：" + struCardStatus.dwErrorCode);
                } else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_SUCCESS) {
                    if (struCardStatus.dwErrorCode != 0){
                        log.warn("删除卡成功,但是错误码" + struCardStatus.dwErrorCode + ", 卡号：" + new String(struCardStatus.byCardNo).trim());
                    } else{
                        log.debug("删除卡成功, 卡号: " + new String(struCardStatus.byCardNo).trim() + ", 状态：" + struCardStatus.byStatus);
                    }
                    continue;
                } else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_FINISH) {
                    //System.out.println("删除卡完成");
                    break;
                }
            }
            return Result.success();
        }catch (Exception e){
            return Result.fail(e.getMessage());
        }
    }

    public Result delOneFace(String strCardNo) {
        HCNetSDK.NET_DVR_FACE_PARAM_CTRL struFaceDelCond = new HCNetSDK.NET_DVR_FACE_PARAM_CTRL();
        struFaceDelCond.dwSize = struFaceDelCond.size();
        struFaceDelCond.byMode = 0; //删除方式：0- 按卡号方式删除，1- 按读卡器删除

        struFaceDelCond.struProcessMode.setType(HCNetSDK.NET_DVR_FACE_PARAM_BYCARD.class);

        //需要删除人脸关联的卡号
        for (int i = 0; i < HCNetSDK.ACS_CARD_NO_LEN; i++)
        {
            struFaceDelCond.struProcessMode.struByCard.byCardNo[i] = 0;
        }
        System.arraycopy(strCardNo.getBytes(), 0, struFaceDelCond.struProcessMode.struByCard.byCardNo, 0, strCardNo.length());

        struFaceDelCond.struProcessMode.struByCard.byEnableCardReader[0] = 1; //读卡器
        struFaceDelCond.struProcessMode.struByCard.byFaceID[0] = 1; //人脸ID
        struFaceDelCond.write();

        Pointer ptrFaceDelCond = struFaceDelCond.getPointer();

        boolean bRet = hCNetSDK.NET_DVR_RemoteControl(lUserID, HCNetSDK.NET_DVR_DEL_FACE_PARAM_CFG, ptrFaceDelCond, struFaceDelCond.size());
        if (!bRet)
        {
            //throw new RuntimeException("删除人脸失败，错误码为"+hCNetSDK.NET_DVR_GetLastError());
            return Result.fail("删除人脸失败，错误码为"+hCNetSDK.NET_DVR_GetLastError());
        }
        return Result.success();
    }

    public Result setOneCard(String strCardNo, String sno, String name){
        try {
            HCNetSDK.NET_DVR_CARD_COND struCardCond = new HCNetSDK.NET_DVR_CARD_COND();
            struCardCond.read();
            struCardCond.dwSize = struCardCond.size();
            struCardCond.dwCardNum = 1;  //下发一张
            struCardCond.write();
            Pointer ptrStruCond = struCardCond.getPointer();

            int m_lSetCardCfgHandle = hCNetSDK.NET_DVR_StartRemoteConfig(lUserID, HCNetSDK.NET_DVR_SET_CARD, ptrStruCond, struCardCond.size(),null ,null);
            if (m_lSetCardCfgHandle == -1) {
                throw  handleException("获取长连接失败",hCNetSDK.NET_DVR_GetLastError());
            } else {
                //log.debug("建立下发卡长连接成功！");
            }

            HCNetSDK.NET_DVR_CARD_RECORD struCardRecord = new HCNetSDK.NET_DVR_CARD_RECORD();
            struCardRecord.read();
            struCardRecord.dwSize = struCardRecord.size();

            for (int i = 0; i < HCNetSDK.ACS_CARD_NO_LEN; i++)
            {
                struCardRecord.byCardNo[i] = 0;
            }
            for (int i = 0; i <  strCardNo.length(); i++)
            {
                struCardRecord.byCardNo[i] = strCardNo.getBytes()[i];
            }

            struCardRecord.byCardType = 1; //普通卡
            struCardRecord.byLeaderCard = 0; //是否为首卡，0-否，1-是
            struCardRecord.byUserType = 0;
            struCardRecord.byDoorRight[0] = 1; //门1有权限

            struCardRecord.struValid.byEnable = 1;    //卡有效期使能，下面是卡有效期从2000-1-1 11:11:11到2030-1-1 11:11:11
            struCardRecord.struValid.struBeginTime.wYear = 2000;
            struCardRecord.struValid.struBeginTime.byMonth = 1;
            struCardRecord.struValid.struBeginTime.byDay = 1;
            struCardRecord.struValid.struBeginTime.byHour = 11;
            struCardRecord.struValid.struBeginTime.byMinute = 11;
            struCardRecord.struValid.struBeginTime.bySecond = 11;
            struCardRecord.struValid.struEndTime.wYear = 2030;
            struCardRecord.struValid.struEndTime.byMonth = 1;
            struCardRecord.struValid.struEndTime.byDay = 1;
            struCardRecord.struValid.struEndTime.byHour = 11;
            struCardRecord.struValid.struEndTime.byMinute = 11;
            struCardRecord.struValid.struEndTime.bySecond = 11;

            struCardRecord.wCardRightPlan[0] = 1;//卡计划模板1有效
            int employeeNo = 0;
            try{
                employeeNo = Integer.valueOf(sno);
            }catch (Exception e){

            }
            if (employeeNo > 0 && employeeNo < 100000000){
                struCardRecord.dwEmployeeNo = employeeNo; //personId.intValue(); //工号
            }

            byte[] strCardName = name.getBytes("UTF-8");  //姓名
            for (int i = 0; i < HCNetSDK.NAME_LEN; i++)
            {
                struCardRecord.byName[i] = 0;
            }
            for (int i = 0; i <  strCardName.length; i++)
            {
                struCardRecord.byName[i] = strCardName[i];
            }
            struCardRecord.write();

            HCNetSDK.NET_DVR_CARD_STATUS struCardStatus = new HCNetSDK.NET_DVR_CARD_STATUS();
            struCardStatus.read();
            struCardStatus.dwSize = struCardStatus.size();
            struCardStatus.write();

            IntByReference pInt = new IntByReference(0);

            int delayTime = MAX_DELAY_TIME/10;
            int dwState;
            while(true){
                dwState = hCNetSDK.NET_DVR_SendWithRecvRemoteConfig(m_lSetCardCfgHandle, struCardRecord.getPointer(), struCardRecord.size(),struCardStatus.getPointer(), struCardStatus.size(),  pInt);
                struCardStatus.read();
                if(dwState == -1){
                    return Result.fail(HCNetSDKErrorCode.codeMsg(hCNetSDK.NET_DVR_GetLastError()));
                } else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_NEEDWAIT) {
                    // log.debug("配置等待");
                    Thread.sleep(10);
                    delayTime--;
                    if (delayTime <= 0) throw new RuntimeException("下发卡失败，等待超时");
                    continue;
                } else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_FAILED) {
                    return Result.fail("下发卡失败,状态失败，错误码：" + struCardStatus.dwErrorCode);
                } else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_EXCEPTION) {
                    return Result.fail("下发卡失败,状态异常，错误码：" + struCardStatus.dwErrorCode);
                } else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_SUCCESS) {
                    if (struCardStatus.dwErrorCode != 0){
                        //log.debug("下发卡失败,错误码" + struCardStatus.dwErrorCode + ", 卡号：" + new String(struCardStatus.byCardNo).trim());
                        return Result.fail("下发卡失败,状态1000，错误码： " + struCardStatus.dwErrorCode);
                    }
                    else{
                        // log.debug("下发卡成功, 卡号: " + new String(struCardStatus.byCardNo).trim() + ", 状态：" + struCardStatus.byStatus);
                    }
                    continue;
                } else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_FINISH) {
                    // log.debug("下发卡完成");
                    break;
                }

            }
            return Result.success();
        }catch (Exception e){
            return Result.fail(e.getMessage());
        }

    }

    public Result setOneFace(String strCardNo, byte[] face) {
        try {

            HCNetSDK.NET_DVR_FACE_COND struFaceCond = new HCNetSDK.NET_DVR_FACE_COND();
            struFaceCond.read();
            struFaceCond.dwSize = struFaceCond.size();
            struFaceCond.byCardNo = "123456".getBytes();
            struFaceCond.dwFaceNum = 1;  //下发一张
            struFaceCond.dwEnableReaderNo = 1;//人脸读卡器编号
            struFaceCond.write();
            Pointer ptrStruFaceCond = struFaceCond.getPointer();

            int m_lSetFaceCfgHandle = hCNetSDK.NET_DVR_StartRemoteConfig(lUserID, HCNetSDK.NET_DVR_SET_FACE, ptrStruFaceCond, struFaceCond.size(),null ,null);
            if (m_lSetFaceCfgHandle == -1)
            {
                throw  handleException("获取长连接失败",hCNetSDK.NET_DVR_GetLastError());
            } else {
                //log.debug("建立下发人脸长连接成功！");
            }

            HCNetSDK.NET_DVR_FACE_RECORD struFaceRecord = new HCNetSDK.NET_DVR_FACE_RECORD();
            struFaceRecord.read();
            struFaceRecord.dwSize = struFaceRecord.size();

            for (int i = 0; i < HCNetSDK.ACS_CARD_NO_LEN; i++)
            {
                struFaceRecord.byCardNo[i] = 0;
            }
            for (int i = 0; i <  strCardNo.length(); i++)
            {
                struFaceRecord.byCardNo[i] = strCardNo.getBytes()[i];
            }

            HCNetSDK.BYTE_ARRAY ptrpicByte = new HCNetSDK.BYTE_ARRAY(face.length);
            System.arraycopy(face,0,ptrpicByte.byValue,0,face.length);
            ptrpicByte.write();
            struFaceRecord.dwFaceLen  = face.length;
            struFaceRecord.pFaceBuffer  = ptrpicByte.getPointer();

            struFaceRecord.write();


            HCNetSDK.NET_DVR_FACE_STATUS struFaceStatus = new HCNetSDK.NET_DVR_FACE_STATUS();
            struFaceStatus.read();
            struFaceStatus.dwSize = struFaceStatus.size();
            struFaceStatus.write();

            IntByReference pInt = new IntByReference(0);
            int delayTime = MAX_DELAY_TIME/10;
            int dwFaceState;
            while(true){
                dwFaceState = hCNetSDK.NET_DVR_SendWithRecvRemoteConfig(m_lSetFaceCfgHandle, struFaceRecord.getPointer(), struFaceRecord.size(),struFaceStatus.getPointer(), struFaceStatus.size(),  pInt);
                struFaceStatus.read();
                if(dwFaceState == -1){
                    return Result.fail(HCNetSDKErrorCode.codeMsg(hCNetSDK.NET_DVR_GetLastError()));
                } else if(dwFaceState == HCNetSDK.NET_SDK_CONFIG_STATUS_NEEDWAIT) {
                    //log.debug("配置等待");
                    Thread.sleep(10);
                    delayTime--;
                    if (delayTime <= 0) return Result.fail("配置等待超时");
                    continue;
                }
                else if(dwFaceState == HCNetSDK.NET_SDK_CONFIG_STATUS_FAILED)
                {
                    return Result.fail("下发人脸失败，状态失败,错误码：" + hCNetSDK.NET_DVR_GetLastError() );
                }
                else if(dwFaceState == HCNetSDK.NET_SDK_CONFIG_STATUS_EXCEPTION)
                {
                    return Result.fail("下发人脸失败，状态异常,错误码：" + hCNetSDK.NET_DVR_GetLastError());

                }
                else if(dwFaceState == HCNetSDK.NET_SDK_CONFIG_STATUS_SUCCESS)
                {
                    if (struFaceStatus.byRecvStatus != 1){
                        return Result.fail("下发人脸失败，状态1000,错误码：" + struFaceStatus.byRecvStatus + " ," +HCNetSDKErrorCode.faceErrorMsg.get(struFaceStatus.byRecvStatus));
                    }
                    else{
                        //log.debug("下发卡成功, 卡号: " + new String(struFaceStatus.byCardNo).trim() + ", 状态：" + struFaceStatus.byRecvStatus);
                    }
                    continue;
                }
                else if(dwFaceState == HCNetSDK.NET_SDK_CONFIG_STATUS_FINISH) {
                    //log.debug("下发人脸完成");
                    break;
                }

            }
            return Result.success();
        }catch (Exception e){
            return Result.fail(e.getMessage());
        }

    }

    public Result<List<String>> getAllCard() {
        try {
            HCNetSDK. NET_DVR_CARD_COND struCardCond = new HCNetSDK.NET_DVR_CARD_COND();
            struCardCond.read();
            struCardCond.dwSize = struCardCond.size();
            struCardCond.dwCardNum = 0xffffffff; //查询所有
            struCardCond.write();
            Pointer ptrStruCond = struCardCond.getPointer();

            int m_lSetCardCfgHandle = hCNetSDK.NET_DVR_StartRemoteConfig(lUserID, HCNetSDK.NET_DVR_GET_CARD, ptrStruCond, struCardCond.size(),null ,null);
            if (m_lSetCardCfgHandle == -1)
            {
                throw  handleException("获取长连接失败",hCNetSDK.NET_DVR_GetLastError());
            }
            HCNetSDK.NET_DVR_CARD_RECORD struCardRecord = new HCNetSDK.NET_DVR_CARD_RECORD();
            struCardRecord.read();
            struCardRecord.dwSize = struCardRecord.size();
            struCardRecord.write();

            IntByReference pInt = new IntByReference(0);
            List<String> cardList = new ArrayList<>();
            int dwState;
            while(true){
                dwState = hCNetSDK. NET_DVR_GetNextRemoteConfig(m_lSetCardCfgHandle, struCardRecord.getPointer(), struCardRecord.size());
                struCardRecord.read();
                if(dwState == -1){
                    throw  handleException("NET_DVR_SendWithRecvRemoteConfig接口调用失败",hCNetSDK.NET_DVR_GetLastError());
                } else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_NEEDWAIT)
                {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    continue;
                }
                else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_FAILED)
                {
                    return Result.fail("获取卡参数失败");
                }
                else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_EXCEPTION)
                {
                    return Result.fail("获取卡参数异常");
                }
                else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_SUCCESS)
                {
                    try {
                        //System.out.println("********************************************");
                        cardList.add(new String(struCardRecord.byCardNo).trim());
                        //System.out.println("********************************************");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    continue;
                }
                else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_FINISH) {
                    System.out.println("获取卡参数完成");
                    break;
                }
            }
            return Result.success(cardList);
        }catch (Exception e){
            return Result.fail(e.getMessage());
        }
    }


    public Result<byte[]> getOneFace(String strCardNo){
        try {
            HCNetSDK.NET_DVR_FACE_COND  struFaceCond = new HCNetSDK.NET_DVR_FACE_COND();
            struFaceCond.read();
            struFaceCond.dwSize = struFaceCond.size();

            for (int i = 0; i < HCNetSDK.ACS_CARD_NO_LEN; i++)
            {
                struFaceCond.byCardNo[i] = 0;
            }
            for (int i = 0; i <  strCardNo.length(); i++)
            {
                struFaceCond.byCardNo[i] = strCardNo.getBytes()[i];
            }

            struFaceCond.dwFaceNum = 0xffffffff;
            struFaceCond.dwEnableReaderNo = 0;
            struFaceCond.write();
            int m_lSetCardCfgHandle = hCNetSDK.NET_DVR_StartRemoteConfig(lUserID,2566,struFaceCond.getPointer(),struFaceCond.size(),null,null);
            if (m_lSetCardCfgHandle == -1) {
                throw  handleException("获取长连接失败",hCNetSDK.NET_DVR_GetLastError());
            }
            HCNetSDK.NET_DVR_FACE_RECORD struFaceRecord = new HCNetSDK.NET_DVR_FACE_RECORD();
            struFaceRecord.read();
            struFaceRecord.dwSize = struFaceRecord.size();
            struFaceRecord.write();
            int dwState;
            while (true){
                dwState = hCNetSDK. NET_DVR_GetNextRemoteConfig(m_lSetCardCfgHandle, struFaceRecord.getPointer(), struFaceRecord.size());
                struFaceRecord.read();
                if(dwState == -1){
                    throw  handleException("NET_DVR_SendWithRecvRemoteConfig接口调用失败",hCNetSDK.NET_DVR_GetLastError());
                } else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_NEEDWAIT)
                {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    continue;
                }
                else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_FAILED)
                {
                    return Result.fail("获取卡参数失败");
                }
                else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_EXCEPTION)
                {
                    return Result.fail("获取卡参数异常");
                }
                else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_SUCCESS)
                {
                    try {
//                    System.out.println("获取卡参数成功, 卡号: " + new String(struFaceRecord.byCardNo).trim()
//                            + ", 人脸大小：" + struFaceRecord.dwFaceLen );
                        //faceSizeList.add(struFaceRecord.dwFaceLen);
                        byte[] pic = struFaceRecord.pFaceBuffer.getByteArray(0,struFaceRecord.dwFaceLen);
                        //struFaceRecord.getPointer();
                        //System.out.println(pic.length);
                        return Result.success(pic);
                        //getFile(pic,"C:\\\\Users\\\\lan50\\\\Pictures","543.jpg");

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    continue;
                }
                else if(dwState == HCNetSDK.NET_SDK_CONFIG_STATUS_FINISH) {
                    //System.out.println("获取卡参数完成");
                    break;
                }
            }
            return Result.fail("未读取到数据");
        }catch (Exception ex){
            return Result.fail(ex.getMessage());
        }
    }
}
