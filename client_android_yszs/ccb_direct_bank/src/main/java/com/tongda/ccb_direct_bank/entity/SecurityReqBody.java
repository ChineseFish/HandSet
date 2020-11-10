package com.tongda.ccb_direct_bank.entity;

/**
 * Created by wutw on 2018/1/4 0004.
 */
public class SecurityReqBody {
    /** 客户端当前时间 */
    public String SYSTEM_TIME = "";

    /** 客户端唯一标识 */
    public String HARDWARESN = "";

    /** 场景类型 */
    public String StrUsInd_2 = "";

    /** 客户姓名 */
    public String Cst_Nm = "";

    /** 证件类型 */
    public String Crdt_TpCd = "";

    /** 证件号码 */
    public String Crdt_No = "";

    /** P6客户号 */
    public String Cst_ID = "";

    /** 渠道客户号 */
    public String CHNL_CUST_NO = "";

    /** 性别代码 */
    public String Gnd_Cd = "";

    /** 民族代码 */
    public String Ethnct_Cd = "";

    /** 出生日期 */
    public String Brth_Dt = "";

    /** 有效期日期 */
    public String AvlDt_Dt = "";

    /** 有效期结束日期 */
    public String AvlDt_EdDt = "";

    /** 机构中文全称 */
    public String Inst_Chn_FullNm = "";

    /** 详细地址 */
    public String Dtl_Adr = "";

    /** 签约组织机构代码 */
    public String Sign_OrCd = "";

    /** 人脸图片 */
    public String base64_Ecrp_Txn_Inf = "";

    /** 身份证正面名称 */
    public String File_Nm = "";

    /** 身份证反面名称 */
    public String Upload_FlNm = "";

    /** 电子渠道平台流水号 */
    public String PLAT_FLOW_NO = "";
}
