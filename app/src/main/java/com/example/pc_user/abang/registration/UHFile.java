package com.example.pc_user.abang.registration;

/**
 * Created by Pc-user on 09/01/2018.
 */

public class UHFile {
    private String UHUsercode;
    private String UHUsername;
    private String UHPassword;
    private String UHStatus;

    public UHFile() {

    }

    public UHFile(String UHUsercode, String UHUsername, String UHPassword, String UHStatus) {
        this.UHUsercode = UHUsercode;
        this.UHUsername = UHUsername;
        this.UHPassword = UHPassword;
        this.UHStatus = UHStatus;
    }

    public String getUHUsercode() {
        return UHUsercode;
    }

    public String getUHUsername() {
        return UHUsername;
    }

    public String getUHPassword() {
        return UHPassword;
    }

    public String getUHStatus() {
        return UHStatus;
    }
}
