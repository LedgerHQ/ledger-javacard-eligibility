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

import java.io.ByteArrayOutputStream;

public class Dump {
	
    public static String dump(byte[] buffer, int offset, int length) {
        StringBuffer result = new StringBuffer();
        for (int i=0; i<length; i++) {
                String temp = Integer.toHexString((short)(buffer[offset + i] & 0xff));
                if (temp.length() < 2) {
                        temp = "0" + temp;
                }
                result.append(temp);
                if (i != length - 1) {
                        result.append(" ");
                }
        }
        return result.toString();
    }
    
    public static String dump(byte[] buffer) {
        return dump(buffer, 0, buffer.length);
    }
    
    public static byte[] hexToBin(String src) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        int i = 0;
        while (i < src.length()) {
                char x = src.charAt(i);
                if (!((x >= '0' && x <= '9') || (x >= 'A' && x <= 'F') || (x >= 'a' && x <= 'f'))) {
                        i++;
                        continue;
                }
                try {
                        result.write(Integer.valueOf("" + src.charAt(i) + src.charAt(i + 1), 16));
                        i += 2;
                }
                catch (Exception e) {
                        return null;
                }
        }
        return result.toByteArray();
    }
}

