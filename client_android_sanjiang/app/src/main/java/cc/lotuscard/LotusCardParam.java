package cc.lotuscard;

public class LotusCardParam {
	public LotusCardParam()
	{
		arrCardNo = new byte[8];
		arrBuffer = new byte[64];
		arrKeys = new byte[64];
		arrCosResultBuffer =  new byte[256];
		arrCosSendBuffer = new byte[256];
	}
	/**
	 * 卡片类型
	 */
	public int nCardType;
	/**
	 * 8字节卡号
	 */
	public byte[] arrCardNo;
	
	/**
	 * 卡片容量大小
	 */
	public int nCardSize;
	
	/**
	 * 读写缓冲
	 */
	public byte[] arrBuffer;
	
	/**
	 * 缓冲大小
	 */
	public int nBufferSize;
	/**
	 * 密钥
	 */
	public byte[] arrKeys;
	
	/**
	 * KEYs大小
	 *
	 */
	public int nKeysSize;
	/**
	 * pCosResultBuffer COS执行结果缓冲
	 */
	public byte[] arrCosResultBuffer;
	/**
	 * unCosReultBufferLength COS执行结果缓冲长度 
	 */
	public int unCosReultBufferLength;	
	
	/**
	 * pCosSendBuffer COS指令发送缓冲
	 */
	public byte[] arrCosSendBuffer;
	/**
	 * unCosSendBufferLength COS指令发送缓冲长度 
	 */
	public int unCosSendBufferLength;	
}
