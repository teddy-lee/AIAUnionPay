package com.koolpos.cupinsurance.message.iso8583;

public class CUP8583 {
	public static final byte ISO8583_CUP = 1;

	/**
	 * Clear Bit Flag
	 * 
	 * @param iso
	 *            - ISO_data
	 * @param dataBuffer
	 *            - dataBuffer
	 * @param maxLen
	 *            - maxLen isotable
	 * @param isoVersion
	 *            - isoVersion
	 */
	public static void ClearBit(CUPData iso, byte[] dataBuffer, int maxLen,
			byte isoVersion) {
		int i;

		for (i = 0; i < 128; i++) {
			iso.bitFlag[i] = 0;
		}

		iso.offset = 0;
		iso.dataBuffer = dataBuffer;

		iso.isotable = CUPPack.isotable;
		/***************************************************************************
		 * ********** Delete ISO93Pack.java if (isoVersion == ISO8583_VER_87)
		 * iso.isotable = ISO87Pack.isotable; else if (isoVersion ==
		 * ISO8583_VER_93) iso.isotable = ISO93Pack.isotable; else iso.isotable
		 * = null;
		 **************************************************************************/
	}

	public static void GetDataBuffer(byte[] dataBuffer, short offset,
			CUPData iso) {
		System.arraycopy(iso.dataBuffer, 0, dataBuffer, offset, iso.offset);
	}

	public static short GetDataLength(CUPData iso) {
		return iso.offset;
	}
}
