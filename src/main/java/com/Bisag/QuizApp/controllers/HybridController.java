package com.Bisag.QuizApp.controllers;


import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.Bisag.QuizApp.utils.AesUtil;



public class HybridController {

	public final String privateKeyPem = "-----BEGIN PRIVATE KEY-----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-----END PRIVATE KEY-----";

	public String Hybrid_Data_Decryption(String encryptedDataFromJavaScript) {
		Security.addProvider(new BouncyCastleProvider());
		String originalData = null;
		try {
			String base64KeyData = privateKeyPem.replace("-----BEGIN PRIVATE KEY-----", "")
					.replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");
			byte[] privateKeyBytes = Base64.getDecoder().decode(base64KeyData);
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
			PrivateKey privateKey = kf.generatePrivate(spec);
			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "BC");
			rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
			int chunkSize = 684;
			String aes_data = "";
			for (int i = 0; i < encryptedDataFromJavaScript.length(); i += chunkSize) {
				int endIndex = Math.min(i + chunkSize, encryptedDataFromJavaScript.length());
				String chunk = encryptedDataFromJavaScript.substring(i, endIndex);
				byte[] decodedChunk = Base64.getDecoder().decode(chunk);
				byte[] encryptedChunk = rsaCipher.doFinal(decodedChunk);
				aes_data += new String(encryptedChunk, StandardCharsets.UTF_8);
			}
			String decryptedPassword = new String(java.util.Base64.getDecoder().decode(aes_data));
			AesUtil aesUtil = new AesUtil(256, 1000);
			originalData = aesUtil.decrypt(decryptedPassword.split("::")[1], decryptedPassword.split("::")[0],
					"CAg8AMIICCgKCAgEAv3SWf07W0jarHY59m", decryptedPassword.split("::")[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return originalData;
	}

	public final String publicKeyPEM = "-----BEGIN PUBLIC KEY-----MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAv3SWf07W0jarHY59mTQKBJcMGi3xTQGz/cUuvJg2usSeTwwelG+f4sR3jaOBsasezrLhxBTxyoEevA7+UpGW8suI4tjOSRWC5QD8sY7CeQGX5JJeaA94hICpBBvudR2tNBpY2ctiIIruPgDE/nz7cLUQN3K3YRWvaHG8BRvgiyxICWXGc3U6/PV8GMuu80yERzQuTwehSKQtEzoZY3571rFfYJKpGhZ05HDSiO2dO4l1ZMTkUyYBeTK7Vvx0dW3g1EDQeHHsWOfZ10d6Oa5pCHambXFlOgD3RSHYL6L0AaVRHI5EcdVf4G3kIl7L5OYiddmjPE1Ps3oPkUIgTUHE4D+bnwtOiBZWrkE5vC9Fz6+VKnddW7fVxyxezG0tRs/uShhrTwVM3nSoR90xEVyrKbjRK55az5DFzNw5Qfw6Z3D3yMYC+WIYAxgEn045UvLZ2KJpE98/t/7MUCgZRFPcgptjcvou3HJyBiqbHn7vmTlkUeA+DM34mFuvvBGS56kubaWQ1Uy2ATxHwY8T8njtPQRKh1nf+l8l1WT/oEVkOKS2KxIvA8h8j96JNY75y9Zw89ZKkmkIH+I88Y8ZL7aIcQmMEXWJKRF9H90YvS+7TBumHpj7W5bvfa1AdNXE2rAh0iDydZas1YTa7MjXS1emkjfa3plChCTj3gNZJV5NJwUCAwEAAQ==-----END PUBLIC KEY-----";

	public String Hybrid_Data_Encryption(String data) {
		Security.addProvider(new BouncyCastleProvider());

		try {
			String base64KeyData = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "")
					.replace("-----END PUBLIC KEY-----", "").replaceAll("\\s", "");
			byte[] publicKeyBytes = Base64.getDecoder().decode(base64KeyData);

			KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
			PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

			AesUtil aesutil = new AesUtil(256, 1000);
			String aesEncryptedData = aesutil.encrypt(aesutil.salt, aesutil.iv, "CAg8AMIICCgKCAgEAv3SWf07W0jarHY59m",
					data);

			Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", "BC");
			rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);

			// String [] encryptedChunks;
			int chunkSize = 380; // Adjust as needed
			List<String> encryptedChunks = new ArrayList<>();
			for (int i = 0; i < aesEncryptedData.length(); i += chunkSize) {
				int endIndex = Math.min(i + chunkSize, aesEncryptedData.length());
				String chunk = aesEncryptedData.substring(i, endIndex);

				// Encrypt the chunk with RSA
				byte[] encryptedChunk = rsaCipher.doFinal(chunk.getBytes("UTF-8"));
				encryptedChunks.add(Base64.getEncoder().encodeToString(encryptedChunk));
			}

			return String.join("", encryptedChunks);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}