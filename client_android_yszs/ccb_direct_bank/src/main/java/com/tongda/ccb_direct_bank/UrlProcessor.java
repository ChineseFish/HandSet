package com.tongda.ccb_direct_bank;

import COM.CCB.EnDecryptAlgorithm.MCipherEncryptor;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;

public class UrlProcessor {
    // 第三方信息
    private static String MERCHANTID = "DSB000000000259"; // 商户号-固定
    private static String POSID = "100000259"; // 商户柜台代码
    private static String BRANCHID = "330000000"; // 分行代码-固定
    private static String APPID = "11500"; // 第三方APP平台号
    private static String APPNAME = "自游宝"; // 第三方APP平台名字

    // 客户信息
    private static String NAME = ""; // 客户姓名
    private static String IDTYPE = ""; // 证件类型
    private static String USERID = ""; // 证件号码

    // 其他信息
    private static String TXCODE = "DS0000"; // 交易码
    private static String CHARSET = "utf-8"; // 编码方式


    // 服务信息
    private static String bankURL = "https://ibsbjstar.ccb.com.cn/CCBIS/CCBETradReqServlet";//服务器地址

    private static String key = "7dd2b5985aab5632b1f61abf020111"; //公钥

    //
    public static String encrypt(final String userId){

        //
        long TIMESTAMP = new Date().getTime(); //毫秒时间戳
        StringBuilder strSrcParas = new StringBuilder();
        strSrcParas.append("MERCHANTID=").append(MERCHANTID).append("&")
                .append("POSID=").append(POSID).append("&")
                .append("BRANCHID=").append(BRANCHID).append("&")
                .append("APPID=").append(APPID).append("&")
                .append("APPNAME=").append(APPNAME).append("&")
                .append("NUSERID=").append(userId).append("&")
                .append("NAME=").append(NAME).append("&")
                .append("IDTYPE=").append(IDTYPE).append("&")
                .append("USERID=").append(USERID).append("&")
                .append("TXCODE=").append(TXCODE).append("&")
                .append("TIMESTAMP=").append(TIMESTAMP);

        //
        MCipherEncryptor ccbEncryptor = new MCipherEncryptor(key);
        String ccbParam = "";
        try {
            ccbParam = ccbEncryptor.doEncrypt(strSrcParas.toString());
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | ShortBufferException | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException | InvalidAlgorithmParameterException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //
        StringBuilder urlParam = new StringBuilder();
        urlParam.append("?MERCHANTID=").append(MERCHANTID).append("&")
                .append("POSID=").append(POSID).append("&")
                .append("BRANCHID=").append(BRANCHID).append("&")
                .append("CHARSET=").append(CHARSET).append("&")
                .append("ccbParam=").append(ccbParam);

        //
        return bankURL + urlParam.toString();
    }
}
