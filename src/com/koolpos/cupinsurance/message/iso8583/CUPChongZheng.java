package com.koolpos.cupinsurance.message.iso8583;

import java.util.Locale;

import android.util.Log;

import com.koolpos.cupinsurance.message.constant.Constant;
import com.koolpos.cupinsurance.message.parameter.EMVICData;
import com.koolpos.cupinsurance.message.parameter.OldTrans;
import com.koolpos.cupinsurance.message.utils.AppUtil;
import com.koolpos.cupinsurance.message.utils.ByteUtil;
import com.koolpos.cupinsurance.message.utils.StringUtil;

public class CUPChongZheng implements Constant {
	private static final String APP_TAG = "CUP";
	public static byte[] F_MessageType = new byte[2];
	// private static byte[] F3_ProcessingCode;// F3 processCode
	private static CUPData iso = new CUPData();

	private static final byte FFIX = 0x00;
	private static final byte FLLVAR = 0x01;
	private static final byte FLLLVAR = 0x02;

	private static final byte ATTBIN = 0x00;
	private static final byte ATTN = 0x04;
	private static final byte ATTAN = 0x08;
	private static final byte ATTANS = 0x0C;

	public static final boolean debug = true;

	private static OldTrans oldTrans = null;

	public static boolean chongzhengUnpack(byte[] databuf, OldTrans oldTrans2) {
		oldTrans = oldTrans2;

		int i, j, n;
		byte bitmask;
		int offset = 0;
		boolean ret = true;

		// TPDU
		offset += 5;
		// ANI
		offset += 14;
		// APDU
		offset += 6;
		
		iso.setOffset((short) 0);
		iso.setDataBuffer(databuf, offset, databuf.length - offset);
		System.arraycopy(databuf, offset, F_MessageType, 0, 2);
		Log.d(APP_TAG,
				"F_MessageType = " + StringUtil.toBestString(F_MessageType));

		offset += 2;
		byte[] F_Bitmap = new byte[8];
		System.arraycopy(databuf, offset, F_Bitmap, 0, 8);
		offset += 8;

		/* copy dataBuffer elements to iso */
		iso.setOffset((short) 10); // MsgID + Bitmap
		for (i = 0; i < 8; i++) {
			bitmask = (byte) 0x80;
			for (j = 0; j < 8; j++, bitmask = (byte) ((bitmask & 0xFF) >>> 1)) {
				if (i == 0 && j == 0) {
					continue; // Jumped over the expansion sign of bitmap
				}
				/* Check bitmap flag */
				if ((F_Bitmap[i] & bitmask) == 0) {
					continue;
				}
				/* Count isotable[] index */
				n = (i << 3) + j + 1;
				try {
					Log.d(APP_TAG, "Count isotable[] index n = " + n);
					ret = saveData(n);
					if (ret == false) {
						Log.d("8583", "saveData[" + n + "] fail");
						return ret;
					}
				} catch (Exception e) {
					Log.e("8583", "saveData[" + n + "]Exception");
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	public void getTransType() {

	}

	private static boolean saveData(int bit) {
		int length;
		int fieldLen;
		boolean ret = true;
		ISOTable[] isotable;
		byte[] llvar = new byte[1];
		byte[] lllvar = new byte[2];

		if (iso.isotable != null) {
			isotable = iso.isotable;
		} else {
			isotable = CUPPack.isotable;
		}

		switch (isotable[bit].fieldType) {
		case FFIX + ATTN: // Fixed Numeric
			// Make length even and divide by 2
			length = (isotable[bit].fieldLen + 1) / 2;
			fieldLen = isotable[bit].fieldLen;
			break;
		case FFIX + ATTBIN: // Fixed Binary
			// Divide by 8
			length = isotable[bit].fieldLen;
			fieldLen = isotable[bit].fieldLen;
			break;
		case FFIX + ATTAN: // Fixed Alpha Numeric
		case FFIX + ATTANS: // Fixed Alpha Numeric Special
			length = isotable[bit].fieldLen;
			fieldLen = isotable[bit].fieldLen;
			break;
		case FLLVAR + ATTANS: // LL Variable Alpha Numeric Special
			iso.fetchDataBuffer(llvar, 0, 1);
			length = ((llvar[0] >> 4) & 0x0F) * 10 + (llvar[0] & 0x0F);
			fieldLen = length;
			break;
		case FLLLVAR + ATTAN: // LLL Variable Alpha Numeric

		case FLLLVAR + ATTANS: // LLL Variable Alpha Numeric Special
			// Convert length to BCD (2 bytes)
			iso.fetchDataBuffer(lllvar, 0, 2);
			length = lllvar[0] & 0x0F;
			length = length * 100 + ((lllvar[1] >> 4) & 0x0F) * 10
					+ (lllvar[1] & 0x0F);
			fieldLen = length;
			break;
		case FLLVAR + ATTN: // LL Variable Numeric
			iso.fetchDataBuffer(llvar, 0, 1);
			length = ((llvar[0] >> 4) & 0x0F) * 10 + (llvar[0] & 0x0F);
			fieldLen = length;
			length = (length + 1) / 2;
			break;
		case FLLLVAR + ATTN: /* LLL Variable Numeric */
			iso.fetchDataBuffer(lllvar, 0, 2);
			length = lllvar[0] & 0x0F;
			length = length * 100 + ((lllvar[1] >> 4) & 0x0F) * 10
					+ (lllvar[1] & 0x0F);
			fieldLen = length;
			length = (length + 1) / 2;
			break;
		default: // Unknown format; no data to move
			length = 0;
			fieldLen = length;
			break;
		}
		byte[] tempBuffer = new byte[length];
		iso.fetchDataBuffer(tempBuffer, 0, tempBuffer.length);
		switch (bit) {
		case CUPField.F02_PAN:
			byte[] F2_AccountNumber = new byte[tempBuffer.length * 2];
			ByteUtil.bcdToAscii(tempBuffer, 0, F2_AccountNumber, 0, fieldLen, 0);
			String oldPan = StringUtil.toString(F2_AccountNumber);
			oldPan = oldPan.substring(0, fieldLen);
			oldTrans.setOldPan(oldPan);
			Log.i(APP_TAG, "F2_AccountNumber : oldPan = " + oldPan);
			break;
		case CUPField.F03_PROC:
			String oldProcesscode = StringUtil.getFormatString(tempBuffer);
			oldTrans.setOldProcesscode(oldProcesscode);
			System.out.println("****************** oldProcesscode = "
					+ oldProcesscode);
			// int oldTransType = MessageType.getTransType(oldProcesscode,
			// F_MessageType);
			// if (oldTransType == -1) {
			//
			// } else {
			// oldTrans.setTransType(oldTransType);
			// }
			// Log.i(APP_TAG, "F03_PROC: oldProcesscode is " + oldProcesscode
			// + ", oldTransType = " + oldTransType);
			break;
		case CUPField.F04_AMOUNT:
			Long oldTransAmount = AppUtil.toAmount(tempBuffer);
			Log.i(APP_TAG, "F04_AMOUNT: oldTransAmount = " + oldTransAmount);
			oldTrans.setOldTransAmount(oldTransAmount);
			break;
		case CUPField.F11_STAN:
			int oldTrace = ByteUtil.bcdToInt(tempBuffer);
			oldTrans.setOldTrace(oldTrace);
			Log.i(APP_TAG, "F11_STAN: oldTrace = " + oldTrace);// Trace
			break;
		case CUPField.F12_TIME:

			String oldTransTime = StringUtil.toBestString(ByteUtil
					.bcdToAscii(tempBuffer));
			oldTrans.setOldTransTime(oldTransTime);
			Log.i(APP_TAG, "F12_TIME: oldTransTime = " + oldTransTime);
			// appState.trans.setTransTime(StringUtil.toString(ByteUtil.bcdToAscii(tempBuffer)));
			break;
		case CUPField.F13_DATE:

			String oldTransDate = StringUtil.toString(ByteUtil
					.bcdToAscii(tempBuffer));
			oldTrans.setOldTransDate(oldTransDate);
			Log.i(APP_TAG, "F13_DATE: oldTransDate = " + oldTransDate);
			break;
		case CUPField.F14_EXP:

			String oldExpiry = StringUtil.toString(ByteUtil
					.bcdToAscii(tempBuffer));// Date Of Expired
			oldTrans.setOldExpiry(oldExpiry);
			Log.i(APP_TAG, "F14_EXP: oldExpiry = " + oldExpiry);
			break;
		case CUPField.F15_SETTLE_DATE:
			break;
		case CUPField.F22_POSE:
			oldTrans.setOldEntryMode(tempBuffer[0]);
			oldTrans.setOldPinMode(tempBuffer[1]);
			Log.i(APP_TAG,
					"F22_POSE = "
							+ StringUtil.toString(ByteUtil
									.bcdToAscii(tempBuffer)));
			break;
		case CUPField.F24_NII:
			break;
		case CUPField.F25_POCC:
			String pocc = StringUtil.toString(ByteUtil.bcdToAscii(tempBuffer));
			Log.i(APP_TAG, "F25_POCC = " + pocc);
			oldTrans.setOldPocc(pocc);
			break;
		case CUPField.F26_CAPTURE:
			Log.i(APP_TAG,
					"F26_POCC = "
							+ StringUtil.toString(ByteUtil
									.bcdToAscii(tempBuffer)));
			break;
		case CUPField.F32_ACQUIRER:
			String oldAcquirerCode = StringUtil.getFormatString(tempBuffer);
			oldTrans.setOldAcquirerCode(oldAcquirerCode);
			Log.i(APP_TAG, "F32_ACQUIRER = " + oldAcquirerCode);
			// appState.trans.setAcquirerCode(StringUtil.toString(tempBuffer));
			break;
		case CUPField.F37_RRN:
			String oldRrn = StringUtil.toString(tempBuffer);
			oldTrans.setOldRrn(oldRrn);
			Log.i(APP_TAG, "F37_RRN = " + oldRrn);
			// appState.trans.setRRN(StringUtil.toString(tempBuffer));
			break;
		case CUPField.F38_AUTH:
			String oldAuthCode = StringUtil.toString(tempBuffer);
			oldTrans.setOldAuthCode(oldAuthCode);
			Log.i(APP_TAG, "F38_AUTH = " + oldAuthCode);
			break;
		case CUPField.F39_RSP:
			Log.i(APP_TAG, "F39_RSP = " + StringUtil.toString(tempBuffer));
			break;
		case CUPField.F40:
			procB40_CUP(tempBuffer);
			break;
		case CUPField.F41_TID:
			String oldTID = StringUtil.toString(tempBuffer);
			oldTrans.setOldTID(oldTID);
			Log.i(APP_TAG, "F41_TID: oldTID = :" + oldTID);
			break;
		case CUPField.F42_ACCID:
			String oldMID = StringUtil.toString(tempBuffer);
			oldTrans.setOldMID(oldMID);
			Log.i(APP_TAG, "F42_ACCID: oldMID = :" + oldMID);
			break;
		case CUPField.F44_ADDITIONAL:
			byte[] issID = new byte[11];
			byte[] acqID = new byte[11];
			System.arraycopy(tempBuffer, 0, issID, 0, 11);
			System.arraycopy(tempBuffer, 11, acqID, 0, 11);
			Log.i(APP_TAG, "IssuerID is :" + StringUtil.toString(issID));
			Log.i(APP_TAG, "Acquirer is :" + StringUtil.toString(acqID));
			oldTrans.setOldIssuerID(StringUtil.toString(issID));
			oldTrans.setOldAcquirerID(StringUtil.toString(acqID));
			// 20202020202020202020202020202020202020202020
			break;
		case CUPField.F48:
			// procB48_CUP(tempBuffer, appState);
			break;
		case CUPField.F49_CURRENCY:
			Log.i(APP_TAG,
					"F49_CURRENCY is :" + StringUtil.toString(tempBuffer));
			break;
		case CUPField.F52_PIN:
			Log.i(APP_TAG, "F52_PIN is :" + StringUtil.toBestString(tempBuffer));
			break;
		case CUPField.F53_SCI:
			Log.i(APP_TAG,
					"F53_SCI is :" + StringUtil.getFormatString(tempBuffer));
			break;
		case CUPField.F54_TIP:
			byte[] bal = new byte[12];
			System.arraycopy(tempBuffer, 8, bal, 0, 12);
			Log.i(APP_TAG, StringUtil.toString(bal));
			int lbal = Integer.parseInt(StringUtil.toString(bal));
			Log.i(APP_TAG, "Balance = " + lbal);
			break;
		case CUPField.F55_ICC:
			EMVICData mEMVICData = EMVICData.getEMVICInstance();
			mEMVICData.setF55Length(tempBuffer.length);
			mEMVICData.setF55(tempBuffer);
			break;
		case CUPField.F60:
			procB60_CUP(tempBuffer);
			break;
		case CUPField.F61:
			procB61_CUP(tempBuffer);
			break;
		case CUPField.F62:
			procB62_CUP(tempBuffer, oldTrans);
			break;
		case CUPField.F63:
			// procB63_CUP(tempBuffer, appState);
			procB63_CUP(tempBuffer, oldTrans);
			break;
		case CUPField.F64_MAC:
			Log.i(APP_TAG,
					"F64_MAC is :" + StringUtil.getFormatString(tempBuffer));
			// appState.trans.setMac(tempBuffer);
			break;
		}
		return ret;
	}

	private static void procB60_CUP(byte[] F60_Field) {
		byte[] transTypeCode = new byte[1];
		System.arraycopy(F60_Field, 0, transTypeCode, 0, 1);
		String transTypeCodeStr = StringUtil.toString(ByteUtil
				.bcdToAscii(transTypeCode));
		int oldTransType = CUPMessageType.getTransType(
				oldTrans.getOldProcesscode(), F_MessageType,
				oldTrans.getOldPocc(), transTypeCodeStr);
		if (oldTransType == -1) {

		} else {
			oldTrans.setTransType(oldTransType);
		}
		byte[] batchNumber = new byte[3];
		System.arraycopy(F60_Field, 1, batchNumber, 0, 3);
		int oldBatch = ByteUtil.bcdToInt(batchNumber);
		oldTrans.setOldBatch(oldBatch);
		Log.d(APP_TAG, "F60_Field is :" + StringUtil.getFormatString(F60_Field));
		Log.d(APP_TAG, "oldBatch = " + oldBatch);
	}

	private static void procB61_CUP(byte[] F60_Field) {
		int f61_offset = 0;
		byte[] oriBatchNumber = new byte[3];
		System.arraycopy(F60_Field, f61_offset, oriBatchNumber, 0, 3);
		int oldBatch = ByteUtil.bcdToInt(oriBatchNumber);
		f61_offset += 3;
		// TODO:oldTrans.setOldOriBatch(oldBatch);
		Log.d(APP_TAG, "oldOriBatch = " + oldBatch);
		byte[] oriTraceId = new byte[3];
		System.arraycopy(F60_Field, f61_offset, oriTraceId, 0, 3);
		int oldOriTrace = ByteUtil.bcdToInt(oriTraceId);
		oldTrans.setOldOriTrace(oldOriTrace);
		Log.d(APP_TAG, "oldOriTrace = " + oldOriTrace);
	}

	private static void procB40_CUP(byte[] F40_Field) {

		String temp_F40 = StringUtil.toString(F40_Field).toUpperCase(
				Locale.getDefault());
		String[] strs = temp_F40.split("6F");
		for (String s : strs) {
			if (s.length() < 2) {
				continue;
			}
			String flag = s.substring(0, 2);
			if (flag.equals("08")) {
				String oldPayOrderBatch = s.substring(8);
				oldTrans.setOldPayOrderBatch(oldPayOrderBatch);
				continue;
			}
			if (flag.equals("10")) {
				String oldApOrderId = s.substring(8);
				oldTrans.setOldApOrderId(oldApOrderId);
				continue;
			}
			if (flag.equals("13")) {
				String oldSale_F40_Type = s.substring(8);
				oldTrans.setOldSale_F40_Type(oldSale_F40_Type);
				continue;
			}
			if (flag.equals("20")) {
				String oldOpenBrh = s.substring(8);
				oldTrans.setOldOpenBrh(oldOpenBrh);
				continue;
			}
			if (flag.equals("21")) {
				String oldCardId = s.substring(8);
				oldTrans.setOldCardId(oldCardId);
				continue;
			}
			if (flag.equals("22")) {
				String alipayPId = s.substring(8);
				oldTrans.setAlipayPId(alipayPId);
				oldTrans.setOldMID(alipayPId);
				continue;
			}
			if (flag.equals("23")) {
				String terminalId = s.substring(8);
				oldTrans.setOldTID(terminalId);
				continue;
			}
			if (flag.equals("26")) {
				String alipayAccount = s.substring(8);
				oldTrans.setAlipayAccount(alipayAccount);
				continue;
			}
			if (flag.equals("27")) {
				String alipayTransactionID = s.substring(8);
				oldTrans.setAlipayTransactionID(alipayTransactionID);
				continue;
			}

			if (flag.equals("28")) {
				String exchangeRate = s.substring(8);
				oldTrans.setExchangeRate(exchangeRate);
				continue;
			}

			if (flag.equals("29")) {
				String realAmount = s.substring(8);
				oldTrans.setRealAmount(realAmount);
				continue;
			}

			/*
			 * //merchant order id if (flag.equals("30")) { String merchOrderId
			 * = s.substring(8); oldTrans.setMerchOrderId(merchOrderId);
			 * continue; }
			 */
		}

	}

	private static void procB63_CUP(byte[] F63_Field, OldTrans trans) {

		int offset = 0;
		byte[] cardOrg = new byte[3];
		System.arraycopy(F63_Field, 0, cardOrg, 0, 3);
		trans.setOldCardOrganization(StringUtil.toString(cardOrg));

		Log.i(APP_TAG,
				"B63_CUP : cardOrganization = "
						+ trans.getOldCardOrganization());
		offset += 3;
		int textLength = F63_Field.length - offset;
		if (textLength > 0) {

		}
	}

	private static void procB62_CUP(byte[] F62_Field, OldTrans trans) {

		int offset = 0;
		byte[] toAcount = new byte[F62_Field.length];
		System.arraycopy(F62_Field, 0, toAcount, 0, F62_Field.length);
		trans.setToAccount(StringUtil.toString(toAcount));

		Log.i(APP_TAG, "B62_CUP : to account number = " + trans.getToAccount());
	}

}
