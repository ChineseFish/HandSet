package com.tongda.ccb_direct_bank.entity;

/**
 * Created by wutw on 2018/1/11 0011.
 */
public class BankEntity {
    /** 图片 */
    public String bankcardBitmap = "";

    /** 卡号 */
    public String bankcardNumber = "";

    /** 发卡行名称 */
    public String bankcardBankName = "";

    /** 发卡行标识代码 */
    public String bankcardBandIdentificationNumber = "";

    /** 卡片名称 */
    public String bankcardCardName = "";

    /** 卡片类型 */
    public String bankcardCardType = "";

    public String toString(){
        return bankcardNumber+bankcardBankName;
    }

}
