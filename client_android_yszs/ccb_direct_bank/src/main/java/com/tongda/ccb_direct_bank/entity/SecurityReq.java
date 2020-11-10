package com.tongda.ccb_direct_bank.entity;

/**
 * Created by wutw on 2018/1/4 0004.
 */
public class SecurityReq extends BaseReq{
    /** 渠道类别 */
    public String SYS_CODE = "0250";

    /** 应用包名 */
    public String APP_NAME = "com.ccb.hnfsk";

    /** 应用类别 客户端类型:01代表安卓，02代表ios*/
    public String MP_CODE = "01";

    /** 版本编号 e护航版本号*/
    public String SEC_VERSION = "1.0.0";

    /** 交易类型 */
    public String TXCODE = "SZPHJJIdentityVerify";

    /** 机构号 h5传送过来*/
    public String BRANCHID = "";

    /** 扩展字段1 */
    public String Rmrk_1_Rcrd_Cntnt = "";

    /** 扩展字段2 */
    public String Rmrk_2_Rcrd_Cntnt = "";

    /** 扩展字段3 */
    public String Rmrk_3_Rcrd_Cntnt = "";

    /** 扩展字段3 */
    public String APP_IMEI = "";
    /** 扩展字段3 */
    public String GPS_INFO = "";

}
