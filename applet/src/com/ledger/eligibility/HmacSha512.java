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

import javacard.framework.Util;

public class HmacSha512 {
	
	private static final short DIGEST_SIZE = 64;
	private static final byte IPAD = (byte)0x36;
	private static final byte OPAD = (byte)0x5c;
	private static final short BLOCK_SIZE = (short)128;
		
	public static void hmac(byte[] key, short keyOffset, short keyLength, byte[] data, short dataOffset, short dataLength, byte[] out, short outOffset, byte[] block, short blockOffset) {
		byte i;
		SHA512Wrapper.init();
		for (i=0; i<2; i++) {
			Util.arrayFillNonAtomic(block, blockOffset, BLOCK_SIZE, (i == 0 ? IPAD : OPAD));
			for (short j=0; j<keyLength; j++) {
				block[(short)(blockOffset + j)] ^= key[(short)(keyOffset + j)];
			}
			SHA512Wrapper.update(block, blockOffset, BLOCK_SIZE);
			if (i == 0) {
				SHA512Wrapper.doFinal(data, dataOffset, dataLength, out, outOffset);
			}
			else {
				SHA512Wrapper.doFinal(out, outOffset, DIGEST_SIZE, out, outOffset);
			}			
		}
	}
}
