package com.tongda.ccb_direct_bank.entity;

/**
 * Created by wutw on 2018/1/15 0015.
 */
public class FileUploadEntity extends BaseReq{
    /** 用户ID*/
    public String USERID = "";

    /** 交易类型 */
    public String TXCODE = "";

    /** 机构号 h5传送过来*/
    public String BRANCHID = "";

    /**文件名 */
    public String File_Nm = "";

    /**时间戳 */
    public String File_Date = "";

    /**IBS版本 */
    public String CCB_IBSVersion = "";

    /**IBS版本 */
    public String ACTION = "";
}
