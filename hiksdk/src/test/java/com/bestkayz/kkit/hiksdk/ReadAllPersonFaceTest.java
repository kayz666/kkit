package com.bestkayz.kkit.hiksdk;

import com.alibaba.fastjson.JSONObject;
import com.bestkayz.kkit.common.core.base.Result;
import com.bestkayz.kkit.common.tools.FileUtils;
import com.bestkayz.kkit.hiksdk.lib.HCNetSDK;
import com.bestkayz.kkit.hiksdk.lib.HikEventCallBack;
import com.bestkayz.kkit.hiksdk.provider.HikSDKProvider;
import com.bestkayz.kkit.hiksdk.provider.HikSDKTool;
import com.bestkayz.kkit.hiksdk.provider.impl.HikUserDTO;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Scanner;

@Slf4j
public class ReadAllPersonFaceTest {

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            log.info("请输入设备地址:");
            String ip = scanner.nextLine();
            log.info("请输入设备密码:");
            String password = scanner.nextLine();

            log.info("正在连接到设备 {}...",ip);
            HikSDKProvider hikSDKProvider = HikSDKTool.builder(ip, password, null, "1").build();

            log.info("设备信息 {}",JSONObject.toJSONString(hikSDKProvider.getDeviceInfo()));
            log.info("读取设备所有卡列表...");
            Result<List<HikUserDTO>> result = hikSDKProvider.getAllUserCard();
            if (result.succeed()) {
                int picCount = 0;
                log.info("读取所有卡完毕  总计:{}张",result.getData().size());
                for (HikUserDTO hikUserDTO : result.getData()) {
                    // log.info("用户：{}  卡号：{}", hikUserDTO.getEmployeeNo(), hikUserDTO.getCardNo());
                    Result<byte[]> picResult = hikSDKProvider.getOneFace(hikUserDTO.getCardNo());
                    if (picResult.succeed()) {
                        FileUtils.writeBytesToFile(".\\Pictures\\" + hikUserDTO.getCardNo() + ".jpg", picResult.getData());
                        log.info("成功读取到照片 工号:{} 卡号:{} 照片大小:picSize:{}",hikUserDTO.getEmployeeNo(),hikUserDTO.getCardNo(), picResult.getData().length);
                        picCount++;
                    } else {
                        log.error("获取人员人脸失败 {}", picResult.getDetails());
                    }
                }
                log.info("读取卡照片完毕  总计{}人  成功读取{}照片 ",result.getData().size(),picCount);
            } else {
                log.error("获取所有人员卡信息失败 {}", result.getMsg());
            }
            hikSDKProvider.release();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
