package com.koolpos.cupinsurance.message.parameter;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;
import cn.koolcloud.jni.PinPadInterface;

import com.koolpos.cupinsurance.message.utils.ByteUtil;

/**
 * Created by admin on 2015/2/10.
 */
public class Encryption8583Util {
    public static final String TAG = "Encryption8583Util";

    // Key Algorithm
    public static final byte SINGLE_KEY = 0;
    public static final byte DOUBLE_KEY = 1;
    public static final byte ENCRYPT_DATA_TAG = 0x03;
    public static final byte DECRYPT_DATA_TAG = 0x30;

    // Key Algorithm
    public static final int PRIMARY_ACCOUNT = 0x0;
    public static final int MSR_2TH_TRACK = 0x1;
    public static final int MSR_3TH_TRACK = 0x2;
    public static final int PRIMARY_ACCOUNT_DECRYPTION = 0x80;
    public static final int MSR_2TH_TRACK_DECRYPTION = 0x81;
    public static final int MSR_3TH_TRACK_DECRYPTION = 0x82;

    public static int encryptData(int mode, String arrayDataStr, byte encryptArrayData[], String keyIndex) {
        int macTag = -1;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

        PinPadInterface.open();

        Log.i(TAG, "====get,arrayData.length:" + arrayDataStr.length());

        Log.i(TAG, "get,CurrentTime1:" + simpleDateFormat.format(new Date()));
        Log.i(TAG, "encryptData mode:" + mode);
        Log.i(TAG, "encryptData key index:" + keyIndex);
        int nResult = PinPadInterface.selectKey(2, Integer.parseInt(keyIndex), 1, SINGLE_KEY);
        if (nResult < 0) {
            return -1;
        }

        macTag = PinPadInterface.cryptoString(mode, 0, arrayDataStr.getBytes(), arrayDataStr.length(), encryptArrayData);
        Log.i(TAG, "get,CurrentTime2:" + simpleDateFormat.format(new Date()));

        String encryptStr = ByteUtil.byteArray2String(encryptArrayData);
        Log.i(TAG, "macTag:" + macTag);
        Log.i(TAG, "====encryptArrayData:" + encryptStr);

        PinPadInterface.close();

        return macTag;
    }

    public static int decryptData(int mode, byte encryptArrayData[], byte decryptedArrayData[], String keyIndex) {
        int macTag = -1;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

        PinPadInterface.open();

        Log.i(TAG, "====get, encryptArrayData.length:" + encryptArrayData.length);

        Log.i(TAG, "get, CurrentTime1:" + simpleDateFormat.format(new Date()));
        Log.i(TAG, "decryptData mode:" + mode);
        Log.i(TAG, "decryptData key index:" + keyIndex);
        int nResult = PinPadInterface.selectKey(2, Integer.parseInt(keyIndex), 2, DOUBLE_KEY);
        if (nResult < 0) {
            return -1;
        }

        macTag = PinPadInterface.cryptoString(mode, 2, encryptArrayData, encryptArrayData.length, decryptedArrayData);
        Log.i(TAG, "get, CurrentTime2:" + simpleDateFormat.format(new Date()));

        String encryptedStr = ByteUtil.byteArray2String(decryptedArrayData);
        Log.i(TAG, "macTag:" + macTag);
        Log.i(TAG, "====decryptedArrayData:" + encryptedStr);

        PinPadInterface.close();

        return macTag;
    }
}
