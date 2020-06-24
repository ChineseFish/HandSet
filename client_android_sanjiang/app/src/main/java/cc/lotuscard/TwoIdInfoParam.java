package cc.lotuscard;

public class TwoIdInfoParam {
	public TwoIdInfoParam()
	{
		arrTwoIdName = new byte[30];
		arrTwoIdSex = new byte[2];
		arrTwoIdNation = new byte[4];
		arrTwoIdBirthday = new byte[16];
		arrTwoIdAddress = new byte[70];
		arrTwoIdNo = new byte[36];
		arrTwoIdSignedDepartment = new byte[30];
		arrTwoIdValidityPeriodBegin = new byte[16];
		arrTwoIdValidityPeriodEnd = new byte[16];
		arrTwoIdNewAddress = new byte[70];
		arrTwoIdPhoto = new byte[1024];
		arrTwoIdFingerprint = new byte[1024];
		arrTwoIdPhotoJpeg = new byte[4096];
	}
	
	/**
	 * 姓名 UNICODE
	 */
	public byte[] arrTwoIdName;
	/**
	 * 性别 UNICODE
	 */
	public byte[] arrTwoIdSex;
	/**
	 * 民族 UNICODE
	 */
	public byte[] arrTwoIdNation;
	/**
	 * 出生日期 UNICODE YYYYMMDD
	 */
	public byte[] arrTwoIdBirthday;
	/**
	 * 住址 UNICODE
	 */
	public byte[] arrTwoIdAddress;
	/**
	 * 身份证号码 UNICODE
	 */
	public byte[] arrTwoIdNo;
	/**
	 * 签发机关 UNICODE
	 */
	public byte[] arrTwoIdSignedDepartment;
	/**
	 * 有效期起始日期 UNICODE YYYYMMDD
	 */
	public byte[] arrTwoIdValidityPeriodBegin;
	/**
	 * 有效期截止日期 UNICODE YYYYMMDD 有效期为长期时存储“长期”
	 */
	public byte[] arrTwoIdValidityPeriodEnd;
	/**
	 * 最新住址 UNICODE
	 */
	public byte[] arrTwoIdNewAddress;
	/**
	 * 照片信息
	 */
	public byte[] arrTwoIdPhoto;
	/**
	 * 指纹信息
	 */
	public byte[] arrTwoIdFingerprint;
	/**
	 * 照片信息 JPEG 格式
	 */
	public byte[] arrTwoIdPhotoJpeg;
	/**
	 * 照片信息长度 JPEG格式
	 */
	public int unTwoIdPhotoJpegLength;


}
