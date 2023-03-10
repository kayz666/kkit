package com.bestkayz.kkit.hiksdk.provider.impl;

import lombok.Data;

import java.io.Serializable;

@Data
public class HikUserDTO implements Serializable {

    private String employeeNo;

    private String cardNo;

    private byte[] photoBytes;
}
