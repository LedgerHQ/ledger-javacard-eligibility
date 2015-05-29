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

import javacard.security.Key;

public interface ProprietaryAPI {
	
	public boolean getUncompressedPublicPoint(byte[] privateKey, short privateKeyOffset, byte[] publicPoint, short publicPointOffset);
	public boolean hasHmacSHA512();
	public void hmacSHA512(Key key, byte[] in, short inBuffer, short inLength, byte[] out, short outOffset);

}
