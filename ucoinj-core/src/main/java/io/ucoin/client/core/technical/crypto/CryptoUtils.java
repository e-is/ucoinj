package io.ucoin.client.core.technical.crypto;

import io.ucoin.client.core.technical.UCoinTechnicalException;

import java.nio.charset.Charset;

import org.abstractj.kalium.crypto.Util;

import com.lambdaworks.codec.Base64;

public class CryptoUtils extends Util {
	
	public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");
	public static final Charset CHARSET_ASCII = Charset.forName("US-ASCII");
	private static org.apache.commons.codec.binary.Base64 base64;
	static {
		base64 = new org.apache.commons.codec.binary.Base64();
	}
	
	public static byte[] zeros(int n) {
        return new byte[n];
    }
	
	public static byte[] copyEnsureLength(byte[] source, int length) {
		byte[] result = zeros(length);
		if (source.length > length) {
			System.arraycopy(source, 0, result, 0, length);
		}
		else {
			System.arraycopy(source, 0, result, 0, source.length);
		}
        return result;
    }

	protected static Charset initCharset(String charsetName) {
		Charset result = Charset.forName(charsetName);
		if (result == null) {
			throw new UCoinTechnicalException("Could not load charset: " + charsetName);
		}
		return result;
	}

	public static byte[] decodeUTF8(String string) {
		return string.getBytes(CHARSET_UTF8);
	}
	
	public static byte[] decodeAscii(String string) {
		return string.getBytes(CHARSET_ASCII);
	}
	

	public static byte[] decodeBase64(String data) {
		return Base64.decode(data.toCharArray());
	}
	
	public static String encodeBase64(byte[] data) {
		return new String(Base64.encode(data));
	}
	
	public static byte[] decodeBase58(String data) {
		try {
			return Base58.decode(data);
		} catch (AddressFormatException e) {
			throw new UCoinTechnicalException("Could decode from base 58: " + e.getMessage());
		}
	}
	
	public static String encodeBase58(byte[] data) {
		return Base58.encode(data);
	}
}
