package com.koolpos.cupinsurance;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.koolpos.cupinsurance.message.ISO8583Engine;
import com.koolpos.cupinsurance.message.constant.ConstantUtils;
import com.koolpos.cupinsurance.message.parameter.EMVICData;
import com.koolpos.cupinsurance.message.parameter.UtilFor8583;
import com.koolpos.cupinsurance.message.peripheral.CardSwiper;
import com.koolpos.cupinsurance.message.peripheral.EMVICManager;
import com.koolpos.cupinsurance.message.peripheral.PinPadManager;
import com.koolpos.cupinsurance.message.utils.StringResourceUtil;
import com.koolpos.cupinsurance.message.utils.Utility;

public class MainActivity extends Activity implements View.OnClickListener, CardSwiper.CardSwiperListener {
	private final String TAG = "MainActivity";
	
	private Button btnSignIn;
	private Button btnConsume;
	private TextView commonTextView;
	
	// 签到报文
	byte[] data;// = hex2byte("00456000010000000000000000000000000000000060211000000008000020000000C0001000000130303030303030313838383838383831303030303031300011010000010030");

	String data8583 = "";
	String payKeyIndex = "01";
	
	 private final int FINISH_TRANSACTION_HANDLER = 1;
	 private CardSwiper ex_cardSwiper;
	 private EMVICManager emvManager = null;
	 private static final int RECEIVE_TRACK_DATA = 0;
	 private static final int RECEIVE_TRACK_DATA_ERROR = 1;
	 private String transAmount = "1.00";
	 private String pinblock;
	 private JSONObject transJsonObj = new JSONObject();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViews();
		
	}
	
	private void findViews() {
		btnSignIn = (Button) findViewById(R.id.btnSignIn);
		btnSignIn.setOnClickListener(this);
		btnConsume = (Button) findViewById(R.id.btnConsume);
		btnConsume.setOnClickListener(this);
		
		commonTextView = (TextView) findViewById(R.id.resultText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		commonTextView.setText("");
		switch (view.getId()) {
		case R.id.btnSignIn:
			new ExeSignInThread().start();
			break;
		case R.id.btnConsume:
			showMessageInTextView("请刷卡");
			onStartSwiper(this);
			onStartReadICData(this, "00", transAmount);
			break;
		default:
			break;
		}
	}
	
	Handler mTransactionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PinPadManager.MODE_PAY_NEXT:
                    Bundle pinpadData = (Bundle) msg.getData();
                    boolean isCancelled = pinpadData.getBoolean("isCancelled");
                    if (isCancelled) {
                        showMessageInTextView("transaction is cancelled");
                    } else {
                        pinblock = pinpadData.getString("pwd");

                        //TODO:start transaction
                        makeTransactionParams();
//                        presenter.onStartTransaction(this, transJsonObj);
                        new ExeTransactionThread().start();
                    }
                    break;
                case FINISH_TRANSACTION_HANDLER:
                    JSONObject transObj = (JSONObject) msg.obj;
//                    mListener.onFinishTransaction(transObj);
                    break;
                default:
                    break;
            }
        }
    };
    
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RECEIVE_TRACK_DATA:
                    Hashtable<String, String> trackData = (Hashtable) msg.obj;
                    UtilFor8583.getInstance().trans.setEntryMode(ConstantUtils.ENTRY_SWIPER_MODE);
                    String serviceCode = trackData.get("servicesCode");
                    if (serviceCode.startsWith("2") || serviceCode.startsWith("6")) {
                        showMessageInTextView("请插IC卡");
                    } else {
                        //TODO:start PINPAD and input password
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("pan", trackData.get("pan"));
                            jsonObject.put("brhKeyIndex", payKeyIndex);
                            jsonObject.put("transAmount", transAmount);

                            transJsonObj.put("pan", trackData.get("pan"));
                            transJsonObj.put("track2", trackData.get("track2"));
                            transJsonObj.put("track3", trackData.get("track3"));
                            transJsonObj.put("validTime", trackData.get("validTime"));
                            
                            PinPadManager pinpad = PinPadManager.getInstance();
                            pinpad.setParams(MainActivity.this, jsonObject, mTransactionHandler);
                            pinpad.start();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    break;
                case RECEIVE_TRACK_DATA_ERROR:
                	showMessageInTextView("刷卡错误");
                    break;
                default:
                    break;
            }
        }
    };

    Handler mICDataHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EMVICManager.STATUS_VALUE_0: {
                    break;
                }
                case EMVICManager.STATUS_VALUE_1: {
                    break;
                }
                case EMVICManager.STATUS_VALUE_2:
                    break;
                case EMVICManager.STATUS_VALUE_3:
                    break;
                case EMVICManager.STATUS_VALUE_4: {
                    break;
                }
                case EMVICManager.TRADE_STATUS_0:
                    break;
                case EMVICManager.TRADE_STATUS_1:
                    onStopSwiper();
                    break;
                case EMVICManager.TRADE_STATUS_2:
                    break;
                case EMVICManager.TRADE_STATUS_3:
                    break;
                case EMVICManager.TRADE_STATUS_4:
                    break;
                case EMVICManager.TRADE_STATUS_5:
                    break;
                case EMVICManager.TRADE_STATUS_6:
                    break;
                case EMVICManager.TRADE_STATUS_7:
                    break;
                case EMVICManager.TRADE_STATUS_8:
                    break;
                case EMVICManager.TRADE_STATUS_9:
                    break;
                case EMVICManager.TRADE_STATUS_10:
                    break;
                case EMVICManager.TRADE_STATUS_BAN: {
                    JSONObject transData = new JSONObject();
                    try {
                        transData.put("isCancelled", true);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    onStopReadICData();
                    break;
                }
                case EMVICManager.TRADE_STATUS_ABORT: {
                    JSONObject transData = new JSONObject();
                    try {
                        transData.put("isCancelled", true);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    onStopReadICData();
                    break;
                }
                case EMVICManager.TRADE_STATUS_APPROVED:
                    break;
                case EMVICManager.TRADE_STATUS_DISABLESERVICE: {
                    JSONObject transData = new JSONObject();
                    try {
                        transData.put("isCancelled", true);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    onStopReadICData();
                    break;
                }
                case EMVICManager.TRADE_STATUS_ONLINE:
                    break;
                case EMVICManager.TRADE_STATUS_AFGETICDATA: {
                    EMVICData mEMVICData = EMVICData.getEMVICInstance();
                    String pwd = Utility.hexString(mEMVICData.getPinBlock());
                    try {
                        transJsonObj.put("pan", mEMVICData.getICPan());
                        transJsonObj.put("track2", mEMVICData.getTrack2());
                        transJsonObj.put("validTime", mEMVICData.getDataOfExpired());

                        pinblock = pwd;
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    UtilFor8583.getInstance().trans.setEntryMode(ConstantUtils.ENTRY_IC_MODE);
                    onStopReadICData();
                    makeTransactionParams();
                    new ExeTransactionThread().start();
                    break;
                }
                case EMVICManager.TRADE_STATUS_READICCARDID: {
                    EMVICData mEMVICData = EMVICData.getEMVICInstance();
                    emvManager.getPAN();
                    String pan = mEMVICData.getICPan();
                    JSONObject sendMsg = new JSONObject();
                    try {
                        sendMsg.put("pan", pan);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case PinPadManager.MODE_INPUT_AUTH_CODE:
                    //needPwd = true;
                    break;
                default:
                    break;
            }
        }
    };

    public void onStartSwiper(Context context) {
        if (ex_cardSwiper == null) {
            ex_cardSwiper = new CardSwiper();
            ex_cardSwiper.onCreate(context, this);
        }
        ex_cardSwiper.onStart();
    }
    
    public void onStopSwiper() {
        if (ex_cardSwiper != null) {
            ex_cardSwiper.onPause();
            ex_cardSwiper = null;
        }
    }

    public void onStartReadICData(Context context, String keyIndex, String transAmountIC) {
        if (emvManager == null) {
            emvManager = EMVICManager.getEMVICManagerInstance();
            UtilFor8583.getInstance().terminalConfig.setKeyIndex(payKeyIndex);
            emvManager.setTransAmount(transAmountIC);
            emvManager.onCreate(context, mICDataHandler);
        }
        emvManager.onStart();
    }

    public void onStopReadICData() {
        if (emvManager != null) {
            emvManager.onPause();
            emvManager = null;
        }
    }
    
    @Override
    public void onRecvTrackData(final Hashtable<String, String> trackData) {
        Message msg = mHandler.obtainMessage();
        msg.what = RECEIVE_TRACK_DATA;
        msg.obj = trackData;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onRecvTrackDataError(int resCode, int trackIndex) {
        Message msg = mHandler.obtainMessage();
        msg.what = RECEIVE_TRACK_DATA_ERROR;
        Map<String, Integer> errorParams = new HashMap<String, Integer>();
        errorParams.put("resCode", resCode);
        errorParams.put("trackIndex", trackIndex);
        msg.obj = errorParams;
        mHandler.sendMessage(msg);
    }
	
    public void showMessageInTextView(String message) {
        commonTextView.setText(message);
    }
    
    private void makeTransactionParams() {
        try {

            transJsonObj.put("paymentId", "00000000");
            transJsonObj.put("F02", transJsonObj.optString("pan"));
            transJsonObj.put("track3", transJsonObj.optString("track3"));
            transJsonObj.put("F36", transJsonObj.optString("track3"));
            transJsonObj.put("track2", transJsonObj.optString("track2"));
            transJsonObj.put("F35", transJsonObj.optString("track2"));
            transJsonObj.put("pwd", pinblock);
            transJsonObj.put("F52", pinblock);
            transJsonObj.put("transType", "1021");
            transJsonObj.put("brhKeyIndex", payKeyIndex);
            transJsonObj.put("F04", "1");
            transJsonObj.put("transAmount", "1");
            transJsonObj.put("F62", "8686860211000496799");
            transJsonObj.put("F60.6", "00000000");
            transJsonObj.put("F61", "885522");
            transJsonObj.put("idCard", "885522");
            transJsonObj.put("openBrh", "");//paymentInfo.getOpenBrh()
            transJsonObj.put("F40_6F20", "");//paymentInfo.getOpenBrh()
            transJsonObj.put("iposId", "00000001");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		onStopSwiper();
		onStopReadICData();
	}

	class ExeSignInThread extends Thread {

		@Override
		public void run() {
			ISO8583Engine iso8583 = ISO8583Engine.getInstance(MainActivity.this, "888888820000002", "00000001", payKeyIndex);
			JSONObject signInObj = iso8583.signIn();
			System.out.println(signInObj.toString());
		}
		
	}
	
	class ExeTransactionThread extends Thread {
		@Override
		public void run() {
			ISO8583Engine iso8583 = ISO8583Engine.getInstance(MainActivity.this, "888888820000002", "00000001", payKeyIndex);
			JSONObject transObj = iso8583.exeTransaction(transJsonObj);
			if (null != transObj) {
				System.out.println(transObj.toString());
			}
		}
	}

}
