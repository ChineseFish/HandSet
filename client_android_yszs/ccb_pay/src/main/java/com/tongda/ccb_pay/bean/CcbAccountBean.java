package com.tongda.ccb_pay.bean;

import java.io.Serializable;

public class CcbAccountBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private String merchantId;
    private String posId;
    private String bankId;
    private String pubNo;
    private String installNum;
    public String getMerchantId() {
        return merchantId;
    }
    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }
    public String getPosId() {
        return posId;
    }
    public void setPosId(String posId) {
        this.posId = posId;
    }
    public String getBankId() {
        return bankId;
    }
    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
    public String getPubNo() {
        return pubNo;
    }
    public void setPubNo(String pubNo) {
        this.pubNo = pubNo;
    }
    public String getInstallNum() {
        return installNum;
    }
    public void setInstallNum(String installNum) {
        this.installNum = installNum;
    }
}
