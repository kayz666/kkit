package com.bestkayz.kkit.hiksdk.lib;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Kayz
 * @create: 2021-01-14
 **/
public class HCNetSDKErrorCode {

    public static final Map<Integer,String> errCodeMap = new HashMap(){{
        put(-1,"HIK KKIT 初始化错误");
        put(1,"用户名密码错误");
        put(2,"权限不足");
        put(3,"没有初始化");
        put(4,"通道号错误");
        put(5,"连接到DVR的客户端个数超过最大");
        put(6,"版本不匹配");
        put(7,"连接服务器失败");
        put(8,"向服务器发送失败");
        put(9,"从服务器接收数据失败");
        put(10,"从服务器接收数据超时");
        put(11,"传送的数据有误");
        put(12,"调用次序错误");
        put(13,"无此权限");
        put(14,"DVR命令执行超时");
        put(15,"串口号错误");
        put(16,"报警端口错误");
        put(17,"参数错误");
        put(18,"服务器通道处于错误状态");
        put(19,"没有硬盘");
        put(20,"硬盘号错误");
        put(21,"服务器硬盘满");
        put(22,"服务器硬盘出错");
        put(23,"服务器不支持");
        put(24,"服务器忙");
        put(25,"服务器修改不成功");
        put(26,"密码输入格式不正确");
        put(27,"硬盘正在格式化，不能启动操作");
        put(28,"DVR资源不足");
        put(29,"DVR操作失败");
        put(30,"打开PC声音失败");
        put(31,"服务器语音对讲被占用");
        put(32,"时间输入不正确");
        put(33,"回放时服务器没有指定的文件");
        put(34,"创建文件出错");
        put(35,"打开文件出错");
        put(36,"上次的操作还没有完成");
        put(37,"获取当前播放的时间出错");
        put(38,"播放出错");
        put(39,"文件格式不正确");
        put(40,"路径错误");
        put(41,"资源分配错误");
        put(42,"声卡模式错误");
        put(43,"缓冲区太小");
        put(44,"创建SOCKET出错");
        put(45,"设置SOCKET出错");
        put(46,"个数达到最大");
        put(47,"用户不存在");
        put(48,"写FLASH出错");
        put(49,"DVR升级失败");
        put(50,"解码卡已经初始化过");
        put(51,"调用播放库中某个函数失败");
        put(52,"设备端用户数达到最大");
        put(53,"获得客户端的IP地址或物理地址失败");
        put(54,"该通道没有编码");
        put(55,"IP地址不匹配");
        put(56,"MAC地址不匹配");
        put(57,"升级文件语言不匹配");
        put(58,"播放器路数达到最大");
        put(59,"备份设备中没有足够空间进行备份");
        put(60,"没有找到指定的备份设备");
        put(61,"图像素位数不符，限24色");
        put(62,"图片高*宽超限，限128*2560");
        put(63,"图片大小超限，限100K");
        put(64,"载入当前目录下PlayerSdk出错");
        put(65,"找不到PlayerSdk中某个函数入口");
        put(66,"载入当前目录下DSsdk出错");
        put(67,"找不到DsSdk中某个函数入口");
        put(68,"调用硬解码库DsSdk中某个函数失败");
        put(69,"声卡被独占");
        put(70,"加入多播组失败");
        put(71,"建立日志文件目录失败");
        put(72,"绑定套接字失败");
        put(73,"socket连接中断，此错误通常是由于连接中断或目的地不可达");
        put(74,"注销时用户ID正在进行某操作");
        put(75,"监听失败");
        put(76,"程序异常");
        put(77,"写文件失败");
        put(78,"禁止格式化只读硬盘");
        put(79,"用户配置结构中存在相同的用户名");
        put(80,"导入参数时设备型号不匹配");
        put(81,"导入参数时语言不匹配0");
        put(82,"导入参数时软件版本不匹配");
        put(83,"预览时外接IP通道不在线");
        put(84,"加载高清IPC通讯库StreamTransClient.dll失败");
        put(85,"加载转码库失败");
        put(86,"超出最大的ip接入通道数");
        put(500,"noerror");
        put(501,"inputparameterisinvalid;");
        put(502,"Theorderofthefunctiontobecallediserror.");
        put(503,"Createmultimediaclockfailed;");
        put(504,"Decodevideodatafailed.");
        put(505,"Decodeaudiodatafailed.");
        put(506,"Allocatememoryfailed.");
        put(507,"Openthefilefailed.");
        put(508,"Createthreadoreventfailed");
        put(509,"CreateDirectDrawobjectfailed.");
        put(510,"failedwhencreatingoff,screensurface.");
        put(511,"bufferisoverflow");
        put(512,"failedwhencreatingaudiodevice.");
        put(513,"Setvolumefailed");
        put(514,"Thefunctiononlysupportplayfile.");
        put(515,"Thefunctiononlysupportplaystream.");
        put(516,"Systemnotsupport.");
        put(517,"Nofileheader.");
        put(518,"Theversionofdecoderandencoderisnotadapted.");
        put(519,"Initializedecoderfailed.");
        put(520,"Thefiledataisunknown.");
        put(521,"Initializemultimediaclockfailed");
        put(522,"Bltfailed.");
        put(523,"Updatefailed.");
        put(524,"openfileerror,streamtypeismulti");
        put(525,"openfileerror,streamtypeisvideo");
        put(526,"JPEGcompresserror");
        put(527,"Don'tsupporttheversionofthisfile.");
        put(528,"extractvideodatafailed.");

    }};

    public static String codeMsg(Integer code){
        return "" + code + "  " + errCodeMap.get(code);
    }

    public static final Map<Byte,String> faceErrorMsg;

    static {
        faceErrorMsg = new HashMap<>();
        faceErrorMsg.put(new Byte("0"),"失败");
        faceErrorMsg.put(new Byte("1"),"成功");
        faceErrorMsg.put(new Byte("2"),"重试或人脸质量差");
        faceErrorMsg.put(new Byte("3"),"内存已满");
        faceErrorMsg.put(new Byte("4"),"已存在该人脸");
        faceErrorMsg.put(new Byte("5"),"非法人脸ID");
        faceErrorMsg.put(new Byte("6"),"算法建模失败");
        faceErrorMsg.put(new Byte("7"),"未下发卡权限");
        faceErrorMsg.put(new Byte("8"),"未定义");
        faceErrorMsg.put(new Byte("9"),"人眼间距小");
        faceErrorMsg.put(new Byte("10"),"图片数据长度小于1KB");
        faceErrorMsg.put(new Byte("11"),"图片格式不符（png/jpg/bmp）");
        faceErrorMsg.put(new Byte("12"),"图片像素数量超过上限");
        faceErrorMsg.put(new Byte("13"),"图片像素数量低于下限");
        faceErrorMsg.put(new Byte("14"),"图片信息校验失败");
        faceErrorMsg.put(new Byte("15"),"图片解码失败");
        faceErrorMsg.put(new Byte("16"),"人脸检测失败");
        faceErrorMsg.put(new Byte("17"),"人脸评分失败");
    }

    public static String convertFaceErrMsg(Integer statusCode){
        if (faceErrorMsg.containsKey(statusCode.byteValue())){
            return "" + statusCode + "  " + faceErrorMsg.get(statusCode.byteValue());
        }else {
            return "" + statusCode + "  人脸未知错误";
        }
    }

}
