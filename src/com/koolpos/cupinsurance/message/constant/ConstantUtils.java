package com.koolpos.cupinsurance.message.constant;

public class ConstantUtils {

	public static final String PRINT_TYPE_DEFAULT = "1"; // default print type
	public static final String PRINT_TYPE_ALIPAY = "02"; // Alipay print type
    public static final String PRINT_TYPE_MISPOS = "03"; // mispos print type
    public static final String PRINT_TYPE_WECHAT = "04"; // wechat print type
    public static final String PRINT_TYPE_BAIDU = "05"; // baidu print type
    public static final String PRINT_TYPE_ALIPAY_OVER_SEA = "12"; // Alipay over sea print type
    public static final String PRINT_TYPE_TRANSFER = "11"; // transfer print type
    public static final String PRINT_TYPE_TERMINAL_CUP = "13"; // cup pay print type
    public static final String PRINT_TYPE_TERMINAL_NOWPAY = "14"; // now pay print type
    public static final String PRINT_TYPE_TERMINAL_99BILL = "15"; // 99bill print type
    public static final String STR_NULL_PIN = "0000000000000000"; // null pin
    public static final String MISC_ALIPAY = "alipay"; // alipay misc type
    // block
																	// string
	// PIN Mode
	public static final byte HAVE_PIN = 0x10; // have pin
    public static final byte NO_PIN = 0x20; // no pin

	public static final byte HAVE_PIN_99Bill = 0x01;
	public static final byte NO_PIN_99Bill = 0x02;

	public static final byte ENTRY_PREPAID_CARD_QRCODE_MODE = (byte) 0x81;
	public static final byte ENTRY_PREPAID_COUPON_QRCODE_MODE = (byte) 0x82;
	public static final byte ENTRY_QRCODE_MODE = 0x03;
	public static final byte ENTRY_SWIPER_MODE = 0x02;
	public static final byte ENTRY_KEYBOARD_MODE = 0x01;
	public static final byte ENTRY_UNKNOW_MODE = 0x00;
	public static final byte ENTRY_IC_MODE = 0x05;

	// appstore package name
	public static final String APP_STORE_PACKAGE_NAME = "cn.koolcloud.ipos.appstore";
	public static final String COUPON_APP_PACKAGE_NAME = "com.koolyun.coupon";
    public static final String COUPON_WAN_APP_PACKAGE_NAME = "com.wjl.whrxh";
	// public static final String COUPON_APP_PACKAGE_NAME =
	// "cn.koolcloud.ipos.appstore";

	public static final String ALREADY_REVERSED = "已撤销";

	// keys for alert common dialog
	public static final String POSITIVE_BTN_KEY = "positive_btn_key";
	public static final String NEGATIVE_BTN_KEY = "negative_btn_key";
	public static final String MSG_KEY = "alert_common_dialog_msg";
	public static final String IDENTIFIER_KEY = "alert_common_dialog_identifier";
	public static final String SER_KEY = "cn.koolcloud.pos.ser";
	public static final String UPDATE_INFO_KEY = "update_info_key";
	public static final String LOCAl_SERVICE_TAG = "local_service_tag";
	public static final String START_SERVICE_EXTERNAL_TAG = "start_service_external";

	public static final String DEVICE_PINPAD_KEY = "pinpad";
	public static final String DEVICE_NETWORK_KEY = "network";
	public static final String DEVICE_PRINTER_KEY = "printer";
	public static final String DEVICE_MISPOS_KEY = "mispos";
	public static final String DEVICE_ALL_KEY = "all_devices";

	// mispos index
	public static final String IP = "116.228.223.216";
	public static final int PORT = 10021;
	public static final String ALLINPAY_INDEX = "92";
	public static final String MISPOS_INDEX = "90";
	public static final String MISPOS_MISC = "MIS_TRANSFER";
	public static final String MISPOS_TRAN_TYPE = "SALE_TRANSFER";
	// Mispos All in Pay test
	// public static final String IP = "116.236.252.102";
	// public static final int PORT = 8880;
	// Exception/Error tag
	public static final String ERROR_TYPE_0 = "0x00"; // 冲正解包出错。
	/*
	 *  组织报文失败：1.可能是Mac计算失败（键盘没有插好，或密钥没有灌成功）
	 *             2.可以是组文过程中返回的报文长度为0.
	 */
	public static final String ERROR_TYPE_1 = "0x01";
	public static final String ERROR_TYPE_2 = "0x02"; // 预留

	public static final String APMP_TRAN_TYPE_CONSUME = "1021";
	public static final String APMP_TRAN_TYPE_CONSUMECANCE = "3021";
	public static final String APMP_TRAN_TYPE_PREAUTH = "1011";
	public static final String APMP_TRAN_TYPE_PRAUTHCOMPLETE = "1031";
	public static final String APMP_TRAN_TYPE_PRAUTHSETTLEMENT = "1091";
	public static final String APMP_TRAN_TYPE_PRAUTHCANCEL = "3011";
	public static final String APMP_TRAN_TYPE_PREAUTHCOMPLETECANCEL = "3031";
	public static final String APMP_TRAN_SUPER_TRANSFER = "1720";
	public static final String FOR_PRINT_MERCHANT_NAME = "merchantName";
	public static final String FOR_PRINT_MERCHANT_ID = "merchantID";
	public static final String FOR_PRINT_MECHINE_ID = "mechineID";
	public static final String FOR_PRINT_OPERATOR = "operator";
	public static final String FOR_TYPE_TOTALAMOUNT = "totalAmount";
	public static final String TAB_TYPE_COUPON = "coupon";

	public static final String ORDER_STATE_SUCCESS = "0";
	public static final String ORDER_STATE_FAILURE = "1";
	public static final String ORDER_STATE_CHONGZHENG = "2";
	public static final String ORDER_STATE_REVOKE = "3";
	public static final String ORDER_STATE_AUTH_COMPLETE = "4";
	public static final String ORDER_STATE_INTERRUPT = "5";
	public static final String ORDER_STATE_TIMEOUT = "9";

	public static final String ALIIPAY_OPEN_BRH = "0229000228";

	public static final String LANGUAGE_CHINESE = "zh";

	/*--------------MSG TYPE-------------------------------------*/
	public static final int MSG_TYPE_DEFAULT = 10;
	public static final int MSG_TYPE_CUP = 11;
	public static final int MSG_TYPE_ALLINPAY = 12;
	public static final int MSG_TYPE_99BILL = 13;
	public static final int MSG_TYPE_NOWPAY = 14;
	/*--------------MSG TYPE-------------------------------------*/

	/*--------------MASTER KEY INDEX-----------------------------*/
	public static final String MASTER_KEY_INDEX_KOOLYUN = "00";
	public static final String MASTER_KEY_INDEX_CUP = "01";
	public static final String MASTER_KEY_INDEX_ALLINPAY = "02";
	public static final String MASTER_KEY_INDEX_99BILL= "03";
	public static final String MASTER_KEY_INDEX_NOWPAY= "04";
	public static final String MASTER_KEY_INDEX_MISPOS= "90";
	public static final String MASTER_KEY_INDEX_CASH_NET= "91";
	/*--------------MASTER KEY INDEX-----------------------------*/

	/*--------------MSG SEND TYPE--------------------------------*/
	public static final String MSG_SEND_TYPE_BY_SS_POSP = "BY_SS_POSP";
	public static final String MSG_SEND_TYPE_BY_NAC_POSP = "BY_NAC_POSP";
	public static final String MSG_SEND_TYPE_BY_TERM = "BY_TERM";
	public static final String MSG_SEND_TYPE_WRITE_BACK = "WRITE_BACK";
	/*--------------MSG SEND TYPE--------------------------------*/
	
	public static final String TPDU = "6000010000";
	public static final String STATIC_ANI = "000000000000000000000000000000000";

}
