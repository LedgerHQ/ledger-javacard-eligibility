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

import javacard.security.MessageDigest;

public class SHA512Wrapper {
	
	private static SHA512 sha512;
	private static MessageDigest sha512_native;
	
	public static void init(MessageDigest sha512_native, SHA512 sha512) {
		SHA512Wrapper.sha512_native = sha512_native;
		SHA512Wrapper.sha512 = sha512;
	}
	
	public static void init() {
		if (sha512_native == null) {
			sha512.init();
		}
	}
	
	public static void update(byte[] data, short dataOffset, short dataLength) {
		if (sha512_native != null) {
			sha512_native.update(data, dataOffset, dataLength);
		}
		else {
			sha512.update(data, dataOffset, dataLength);
		}
	}
	
	public static void doFinal(byte[] data, short dataOffset, short dataLength, byte[] target, short targetOffset) {
		if (sha512_native != null) {
			sha512_native.doFinal(data, dataOffset, dataLength, target, targetOffset);
		}
		else {
			sha512.doFinal(data, dataOffset, dataLength, target, targetOffset);
		}
	}

}
