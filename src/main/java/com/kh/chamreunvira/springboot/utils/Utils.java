package com.kh.chamreunvira.springboot.utils;

public class Utils {

    public static String toNickName(String nickName) {
        return "@".concat(nickName.replace(" " , "_").toLowerCase());
    }

}
