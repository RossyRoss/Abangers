package com.example.pc_user.abang.registration;

/**
 * Created by Pc-user on 09/01/2018.
 */

public class UDFile {
    private String UDUserCode;
    private String UDFullname;
    private String UDAddr;
    private String UDEmail;
    private String UDStatus;

    private UDFile() {

    }

    public UDFile(String UDUserCode, String UDFullname, String UDAddr, String UDEmail, String UDStatus) {
        this.UDUserCode = UDUserCode;
        this.UDFullname = UDFullname;
        this.UDAddr = UDAddr;
        this.UDEmail = UDEmail;
        this.UDStatus = UDStatus;
    }

    public String getUDUserCode() {
        return UDUserCode;
    }

    public String getUDFullname() {
        return UDFullname;
    }

    public String getUDAddr() {
        return UDAddr;
    }

    public String getUDEmail() {
        return UDEmail;
    }

    public String getUDStatus() {
        return UDStatus;
    }
}
