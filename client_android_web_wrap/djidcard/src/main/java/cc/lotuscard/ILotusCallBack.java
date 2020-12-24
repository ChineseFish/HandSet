package cc.lotuscard;

public interface ILotusCallBack {
	/**
	 * 通过回调完成第三方设备操作二代证
	 * 
	 * @param objUser
	 *            参数 用户对象 在调用GetTwoIdInfoByMcuServer是传入
	 * 
	 * @param arrBuffer
	 *            参数 与 结果都在其中 根据回调来的参数操作二代证并把结果填入其中 第一字节是长度 后续是数据
	 * @return true = 操作成功
	 */
	public boolean callBackExtendIdDeviceProcess(Object objUser,
			byte[] arrBuffer);

	/**
	 * 通过回调处理数据读写
	 *
	 * @param nDeviceHandle
	 *            设备句柄
	 * @param bRead
	 *            是否读操作
	 * @param arrBuffer
	 *            缓冲
	 * @return true = 操作成功
	 */
	public boolean callBackReadWriteProcess(long nDeviceHandle, boolean bRead,
											byte[] arrBuffer);
}
