package main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DecryptDbeaver {

	// from the DBeaver source 8/23/19
	// https://github.com/dbeaver/dbeaver/blob/57cec8ddfdbbf311261ebd0c7f957fdcd80a085f/plugins/org.jkiss.dbeaver.model/src/org/jkiss/dbeaver/model/impl/app/DefaultSecureStorage.java#L31
	private static final byte[] LOCAL_KEY_CACHE = new byte[] { -70, -69, 74, -97, 119, 74, -72, 83, -55, 108, 45, 101, 61, -2, 84, 74 };
	
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("syntax: param1: full path to your credentials-config.json file");
			System.exit(1);
		}
		System.out.println(decrypt(Files.readAllBytes(Paths.get(args[0]))));
	}

	private static String decrypt(byte[] contents) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		try (InputStream byteStream = new ByteArrayInputStream(contents)) {
			byte[] fileIv = new byte[16];
			byteStream.read(fileIv);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKey aes = new SecretKeySpec(LOCAL_KEY_CACHE, "AES");
			cipher.init(Cipher.DECRYPT_MODE, aes, new IvParameterSpec(fileIv));
			try (CipherInputStream cipherIn = new CipherInputStream(byteStream, cipher)) {
				return inputStreamToString(cipherIn);
			}
		}
	}

	private static String inputStreamToString(InputStream is) {
    Scanner s = new Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }

}
