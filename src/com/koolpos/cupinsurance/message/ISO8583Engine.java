package com.koolpos.cupinsurance.message;

import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.koolpos.cupinsurance.message.constant.ConstantUtils;
import com.koolpos.cupinsurance.message.control.CUP8583Controller;
import com.koolpos.cupinsurance.message.iso8583.ISO8583Util;
import com.koolpos.cupinsurance.message.parameter.EMVICData;
import com.koolpos.cupinsurance.message.parameter.UtilFor8583;
import com.koolpos.cupinsurance.message.utils.MessageUtil;
import com.koolpos.cupinsurance.message.utils.PreferenceUtil;
import com.koolpos.cupinsurance.message.utils.SDCardFileTool;
import com.koolpos.cupinsurance.message.utils.Utility;
import com.koolpos.cupinsurance.network.NetConnection;

public class ISO8583Engine {
	private final String TAG = "ISO8583Engine";

	private static int traceNo = 0;
	private static int batchId = 0;

	private static ISO8583Engine instance = null;
	private static Context context;
	private static CUP8583Controller cup8583Controller;

	public static ISO8583Engine getInstance(Context ctx, String merchId, String terId, String keyIndex) {
		if (instance == null) {
			instance = new ISO8583Engine();
		}
		Log.e("ISO8583Engine", "keyindex:" + keyIndex);
		UtilFor8583.getInstance().terminalConfig.setKeyIndex(keyIndex);
		context = ctx;
		if (cup8583Controller == null) {
			generateCUP8583Controller(merchId, terId);
		}
		cup8583Controller.setMerchIdTermId(merchId, terId);
		return instance;

	}

	private static CUP8583Controller generateCUP8583Controller(String merchId, String terId) {
		/*MessageUtil.calculateBatchTraceNo(context);
		
		JSONObject merchObj = MessageUtil.getTransactionFromPreference(context);
		traceNo = merchObj.optInt("traceNo");
		batchId = merchObj.optInt("batchId");*/
		cup8583Controller = new CUP8583Controller(context, merchId, terId, traceNo, batchId);
		return cup8583Controller;
	}
	
	public JSONObject signIn(Context context) {
		generateTraceNo();
		JSONObject responseObj = null;
		cup8583Controller.signin();
		String str8583 = cup8583Controller.toString();
		NetConnection connect = new NetConnection();
		String response8583 = null;
		try {
			response8583 = connect.socketConnect(context, str8583);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (!TextUtils.isEmpty(response8583)) {
			responseObj = ISO8583Util.convert8583(context, cup8583Controller, response8583);
		}
		
		//TODO: clear singleton data
		/*EMVICData emvData = EMVICData.getEMVICInstance();
		if (null != emvData) {
			emvData.clearEMVData();
		}*/
		UtilFor8583 util8583 = UtilFor8583.getInstance();
		if (null != util8583) {
			util8583.clearUtilFor8583();
		}
		
		cup8583Controller = null;
		
		return responseObj;
	}
	
	public JSONObject exeTransaction(Context context, JSONObject transObj) {
		JSONObject responseObj = null;
		
		//calculate traceNO
		generateTraceNo();
		cup8583Controller.purchase(transObj);
		String str8583 = cup8583Controller.toString();
		Log.w("request 8583", "request:" + str8583);
		PreferenceUtil.saveReverse8583(context, str8583);
		PreferenceUtil.saveOldTransType(context, ConstantUtils.APMP_TRAN_TYPE_CONSUME);
		
		NetConnection connect = new NetConnection();
		try {
			String response8583 = connect.socketConnect(context, str8583);
			
			if (!TextUtils.isEmpty(response8583)) {
				responseObj = ISO8583Util.convert8583(context, cup8583Controller, response8583);
				//TODO:clear chongzheng cache
				PreferenceUtil.clearPropertyBySharedPreferences(context);
			} else {
				//TODO:start chong zheng action
				try {
					cup8583Controller.chongZheng(Utility.hex2byte(str8583), ConstantUtils.APMP_TRAN_TYPE_CONSUME);
					String chongzheng8583 = cup8583Controller.toString();
					String chongZhengResponse8583 = connect.socketConnect(context, chongzheng8583);
					if (!TextUtils.isEmpty(chongZhengResponse8583)) {
						JSONObject chongzhengObj = ISO8583Util.convert8583(context, cup8583Controller, chongZhengResponse8583);
						if (null != chongzhengObj) {
							PreferenceUtil.clearPropertyBySharedPreferences(context);
							Log.d(TAG, chongzhengObj.toString());
						}
						Log.d(TAG, chongZhengResponse8583);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			//TODO: clear singleton data
			/*EMVICData emvData = EMVICData.getEMVICInstance();
			if (null != emvData) {
				emvData.clearEMVData();
			}*/
			UtilFor8583 util8583 = UtilFor8583.getInstance();
			if (null != util8583) {
				util8583.clearUtilFor8583();
			}
			
			cup8583Controller = null;
		} catch (Exception e) {
			// TODO 冲正
			e.printStackTrace();
		}
		
		return responseObj;
	}
	
	private void generateTraceNo() {
		/*MessageUtil.calculateBatchTraceNo(context);
		
		JSONObject merchObj = MessageUtil.getTransactionFromPreference(context);
		traceNo = merchObj.optInt("traceNo");
		batchId = merchObj.optInt("batchId");*/
		
		String traceNo = SDCardFileTool.getPingzhengContent();
		if (TextUtils.isEmpty(traceNo)) {
			traceNo = "0";
		}
		SDCardFileTool.writePingzhengFile(Integer.parseInt(traceNo) + 1 + "");
		
		String batchNo = SDCardFileTool.getPiciContent();
		if (null != cup8583Controller) {
			cup8583Controller.setTraceNo(Integer.parseInt(traceNo) + 1);
			cup8583Controller.setBatchNo(Integer.parseInt(batchNo));
		}
		
	}

}
