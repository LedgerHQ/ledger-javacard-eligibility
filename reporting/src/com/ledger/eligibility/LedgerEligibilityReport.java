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

import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

public class LedgerEligibilityReport {
	
	static class APDUResponse {
		public byte[] response;
		public long exchangeTime;
	}
	
	private static final byte[] SELECT_APDU = Dump.hexToBin("00A4040010FF4C4547522E454C494730312E493031");
	private static final byte[] TEST_EC = Dump.hexToBin("E020000000");
	private static final byte[] TEST_ECDH = Dump.hexToBin("E022000000");
	private static final byte[] TEST_RIPEMD160 = Dump.hexToBin("E024000000");
	private static final byte[] TEST_SHA512 = Dump.hexToBin("E026000000");
	private static final byte[] TEST_BIP32_GEN = Dump.hexToBin("E028000000");
	private static final byte[] TEST_BIP32_DERIVE_HARDENED = Dump.hexToBin("E02A000000");
	private static final byte[] TEST_BIP32_DERIVE_NON_HARDENED = Dump.hexToBin("E02A010000");
	
	private static final int SW_OK = 0x9000;
	
    private static final byte STATUS_PASSED = (byte)0x01;
    private static final byte STATUS_FAILED_GENERIC = (byte)0xf0;
    private static final byte STATUS_FAILED_SIGNATURE_GENERATION = (byte)0xf1;
    private static final byte STATUS_FAILED_SIGNATURE_VALIDATION = (byte)0xf2;
    private static final byte STATUS_FAILED_KEY_AGREEMENT_X = (byte)0xf1;
    private static final byte STATUS_FAILED_KEY_AGREEMENT_XY = (byte)0xf2;
    private static final byte STATUS_FAILED_INTERNAL = (byte)0xff;
    
    private static final byte FLAG_SECURE_RANDOM_SUPPORTED = (byte)0x01;
    private static final byte FLAG_EC_KEY_GENERATION_SUPPORTED = (byte)0x02;
    private static final byte FLAG_EC_TRANSIENT_RESET = (byte)0x04;
    private static final byte FLAG_EC_TRANSIENT_DESELECT = (byte)0x08;
    private static final byte FLAG_EC_RFC6979_SUPPORT = (byte)0x10;
    private static final byte FLAG_KEY_AGREEMENT_X = (byte)0x01;
    private static final byte FLAG_KEY_AGREEMENT_XY = (byte)0x02;
    private static final byte FLAG_NATIVE_RIPEMD160 = (byte)0x01;
    private static final byte FLAG_NATIVE_SHA512 = (byte)0x01;
    private static final byte FLAG_NATIVE_HMAC_SHA512 = (byte)0x01;
    private static final byte FLAG_HMAC_TRANSIENT_RESET = (byte)0x02;
    private static final byte FLAG_HMAC_TRANSIENT_DESELECT = (byte)0x04;
	
	
	private static APDUResponse exchange(CardChannel channel, byte[] command) throws CardException {
		long start = System.currentTimeMillis();
		ResponseAPDU response = channel.transmit(new CommandAPDU(command));
		long duration = System.currentTimeMillis() - start;
		if (response.getSW() != SW_OK) {
			throw new RuntimeException("Invalid status " + Integer.toHexString(response.getSW()));
		}
		
		APDUResponse apduResponse = new APDUResponse();
		apduResponse.response = response.getData();
		apduResponse.exchangeTime = duration;
		return apduResponse;
	}
		
	public static void main(String args[]) throws Exception {
		TerminalFactory factory = TerminalFactory.getDefault();
		List<CardTerminal> terminals = factory.terminals().list();
		CardTerminal terminal = null;
		if (args.length == 0) {
			for (CardTerminal currentTerminal : terminals) {
				if (currentTerminal.isCardPresent()) {
					terminal = currentTerminal;
					break;
				}
			}
			if (terminal == null) {
				System.out.println("No card readers found");
				return;
			}
		}
		else {
			for (CardTerminal currentTerminal : terminals) {
				if (currentTerminal.getName().equalsIgnoreCase(args[0])) {
					terminal = currentTerminal;
					break;
				}
			}
			if (terminal == null) {
				System.out.println("Card reader not found");
				return;
			}
		}
		Card card = terminal.connect("*");
		CardChannel channel = card.getBasicChannel();
		try {
			exchange(channel, SELECT_APDU);
		}
		catch(Exception e) {
			System.out.println("Failed to select applet. Verify that it has been installed");
			e.printStackTrace();
			return;
		}
		// Test EC
		System.out.print("Test Elliptic Curves ... : ");
		APDUResponse response = exchange(channel, TEST_EC);
		switch(response.response[0]) {
			case STATUS_PASSED:
				System.out.print("OK");
				break;
			case STATUS_FAILED_GENERIC:
				System.out.print("Failed, general failure");
				break;
			case STATUS_FAILED_SIGNATURE_GENERATION:
				System.out.print("Failed, signature generation");
				break;
			case STATUS_FAILED_SIGNATURE_VALIDATION:
				System.out.print("Failed, signature verification");
				break;
			case STATUS_FAILED_INTERNAL:
				System.out.println("Failed, internal error");
				break;				
		}
		System.out.println(" (" + response.exchangeTime + " ms)");
		if ((response.response[1] & FLAG_EC_KEY_GENERATION_SUPPORTED) != 0) {
			System.out.println("\tKey generation supported");			
		}
		if ((response.response[1] & FLAG_SECURE_RANDOM_SUPPORTED) != 0) {
			System.out.println("\tSecure random supported");			
		}
		if ((response.response[1] & FLAG_EC_TRANSIENT_DESELECT) != 0) {
			System.out.println("\tTransient, Clear On Deselect key storage supported");			
		}
		if ((response.response[1] & FLAG_EC_TRANSIENT_RESET) != 0) {
			System.out.println("\tTransient, Clear On Reset key storage supported");			
		}
		if ((response.response[1] & FLAG_EC_RFC6979_SUPPORT) != 0) {
			System.out.println("\tRFC6979 available");			
		}
		// Test ECDH
		System.out.print("Test Public Key recovery ... : ");
		response = exchange(channel, TEST_ECDH);
		switch(response.response[0]) {
			case STATUS_PASSED:
				System.out.print("OK");
				break;
			case STATUS_FAILED_KEY_AGREEMENT_X:
				System.out.print("Failed, key agreement on X");
				break;
			case STATUS_FAILED_KEY_AGREEMENT_XY:
				System.out.print("Failed, key agreement on XY");
				break;
			case STATUS_FAILED_INTERNAL:
				System.out.println("Failed, internal error");
				break;
		}
		System.out.println(" (" + response.exchangeTime + " ms)");
		if ((response.response[1] & FLAG_KEY_AGREEMENT_X) != 0) {
			System.out.println("\tPartial key recovery supported");			
		}
		if ((response.response[1] & FLAG_KEY_AGREEMENT_XY) != 0) {
			System.out.println("\tFull key recovery supported");			
		}
		// Test RIPEMD160
		System.out.print("Test RIPEMD160 ... : ");
		response = exchange(channel, TEST_RIPEMD160);
		switch(response.response[0]) {
			case STATUS_PASSED:
				System.out.print("OK");
				break;
			case STATUS_FAILED_GENERIC:
				System.out.print("Failed");
				break;
			case STATUS_FAILED_INTERNAL:
				System.out.println("Failed, internal error");
				break;
		}
		System.out.println(" (" + response.exchangeTime + " ms)");
		if ((response.response[1] & FLAG_NATIVE_RIPEMD160) != 0) {
			System.out.println("\tNative RIPEMD160 supported");			
		}
		// Test SHA512
		System.out.print("Test SHA512 ... : ");
		response = exchange(channel, TEST_SHA512);
		switch(response.response[0]) {
			case STATUS_PASSED:
				System.out.print("OK");
				break;
			case STATUS_FAILED_GENERIC:
				System.out.print("Failed");
				break;
			case STATUS_FAILED_INTERNAL:
				System.out.println("Failed, internal error");
				break;
		}
		System.out.println(" (" + response.exchangeTime + " ms)");
		if ((response.response[1] & FLAG_NATIVE_SHA512) != 0) {
			System.out.println("\tNative SHA512 supported");			
		}
		// Test Seed Generation
		System.out.print("Test HD Seed Generation ... : ");
		response = exchange(channel, TEST_BIP32_GEN);
		switch(response.response[0]) {
			case STATUS_PASSED:
				System.out.print("OK");
				break;
			case STATUS_FAILED_GENERIC:
				System.out.print("Failed");
				break;
			case STATUS_FAILED_INTERNAL:
				System.out.println("Failed, internal error");
				break;
		}
		System.out.println(" (" + response.exchangeTime + " ms)");
		if ((response.response[1] & FLAG_NATIVE_HMAC_SHA512) != 0) {
			System.out.println("\tNative HMACSHA512 supported");			
		}
		if ((response.response[1] & FLAG_HMAC_TRANSIENT_DESELECT) != 0) {
			System.out.println("\tTransient, Clear On Deselect HMAC key storage supported");			
		}
		if ((response.response[1] & FLAG_HMAC_TRANSIENT_RESET) != 0) {
			System.out.println("\tTransient, Clear On Reset HMAC key storage supported");			
		}
		// Test Hardened Derivation
		System.out.print("Test Hardened derivation ... : ");
		response = exchange(channel, TEST_BIP32_DERIVE_HARDENED);
		switch(response.response[0]) {
			case STATUS_PASSED:
				System.out.print("OK");
				break;
			case STATUS_FAILED_GENERIC:
				System.out.print("Failed");
				break;
			case STATUS_FAILED_INTERNAL:
				System.out.println("Failed, internal error");
				break;
		}
		System.out.println(" (" + response.exchangeTime + " ms)");
		// Test Non Hardened Derivation
		System.out.print("Test Non Hardened derivation ... : ");
		response = exchange(channel, TEST_BIP32_DERIVE_NON_HARDENED);
		switch(response.response[0]) {
			case STATUS_PASSED:
				System.out.print("OK");
				break;
			case STATUS_FAILED_GENERIC:
				System.out.print("Failed");
				break;
			case STATUS_FAILED_INTERNAL:
				System.out.println("Failed, internal error");
				break;
		}
		System.out.println(" (" + response.exchangeTime + " ms)");		
		
	}

}
