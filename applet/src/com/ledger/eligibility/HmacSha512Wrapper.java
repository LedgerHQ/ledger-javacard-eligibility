/*
*******************************************************************************    
*   Java Card Bitcoin Hardware Wallet
*   (c) 2015 Ledger
*   
*   This program is free software: you can redistribute it and/or modify
*   it under the terms of the GNU Affero General Public License as
*   published by the Free Software Foundation, either version 3 of the
*   License, or (at your option) any later version.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*   GNU Affero General Public License for more details.
*
*   You should have received a copy of the GNU Affero General Public License
*   along with this program.  If not, see <http://www.gnu.org/licenses/>.
*******************************************************************************   
*/    

package com.ledger.eligibility;

import javacard.security.HMACKey;
import javacard.security.Signature;

public class HmacSha512Wrapper {
	
	private static Signature hmac_native;
	private static HMACKey key_native;
	
	public static void init(Signature hmac_native, HMACKey key_native) {
		HmacSha512Wrapper.hmac_native = hmac_native;
		HmacSha512Wrapper.key_native = key_native;
	}
	
	public static void sign(byte[] key, short keyOffset, short keyLength, byte[] data, short dataOffset, short dataLength, byte[] target, short targetOffset, byte[] tmp, short tmpOffset) {
		if ((hmac_native != null) && (key_native != null)) {
			key_native.setKey(key, keyOffset, keyLength);
			hmac_native.init(key_native, Signature.MODE_SIGN);
			hmac_native.sign(data, dataOffset, dataLength, target, targetOffset);			
		}
		else {
			HmacSha512.hmac(key, keyOffset, keyLength, data, dataOffset, dataLength, target, targetOffset, tmp, tmpOffset);
		}
	}
	
}
