package com.tongda.ccb_direct_bank.entity;

/**
 * Created by wutw on 2018/1/11 0011.
 */
public class IDCardEntity {
    /** 图片 */
//    public String PICTURE_FRONT = "";

    /** 图片 */
    public String cardImage = "";

    /** 姓名 */
    public String cardName = "";

    /** 性别 */
    public String cardSex = "";

    /** 头像 */
    public String cardFace = "";

    /** 民族 */
    public String cardNation = "";

    /** 出生 */
    public String cardDate = "";

    /** 年 */
    public String cardYear = "";

    /** 月 */
    public String cardMonth = "";

    /** 日 */
    public String cardDay = "";

    /** 地址 */
    public String cardAddress = "";

    /** 号码 */
    public String cardID = "";

    /**  扫描的方向*/
    public String cardOrientation = "";

    /** 签发机关 */
    public String cardAuthority = "";

    /** 有效期限 */
    public String cardValidity = "";

    public String scanType = "";

    public String toString()
    {
        return cardName+ cardValidity+cardAuthority+cardSex+cardValidity;
    }
}
