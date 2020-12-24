package cc.lotuscard;

public enum LotusCardErrorCode {
	LCEC_OK ,				//正常执行
	LCED_UNKNOWN,				//未知的 
	LCEC_SEND_FALSE,			//发送失败
	LCEC_RECV_TIME_OUT,			//接收超时
	LCEC_RECV_ZERO_LEN,			//接收长度为0
	LCEC_RECV_CRC_FALSE,		//接收校验失败
	LCEC_REQUEST,				//寻卡
	LCEC_ANTICOLL,				//防冲突
	LCEC_SELECT,				//选卡
	LCEC_AUTHENTICATION,		//三次验证
	LCEC_HALT,					//中止
	LCEC_READ,					//读
	LCEC_WRITE,					//写
	LCEC_INCREMENT,				//加值
	LCEC_DECREMENT,				//减值
	LCEC_LOADKEY,				//装载密码
	LCEC_BEEP,					//蜂鸣
	LCEC_RESTORE,				//卡数据块传入卡的内部寄存器
	LCEC_TRANSFER,				//内部寄存器传入卡的卡数据块
	LCEC_SEND_COMMAND			//发送14443指令
}
