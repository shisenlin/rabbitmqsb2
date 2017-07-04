package com.ssl.rabbit;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.io.StringWriter;
import java.security.*;

/**
 * Created by ssl on 2017/6/29.
 */
public class Pkcs10Generator {

    private static final String SECURITY_PROVIDER = "BC";
    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1WithRSA";
    private static final int KEYSIZE = 2048;
    public static KeyPair generateKeyPair() {
        return generateKeyPair(KEYSIZE);
    }

    public static KeyPair generateKeyPair(int keysize) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM, SECURITY_PROVIDER);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
            return null;
        }
        keyPairGenerator.initialize(keysize <= 0 ? KEYSIZE : keysize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }
    public static String generateCertificationRequest(KeyPair keyPair) {
        return generateCertificationRequest(keyPair, SIGNATURE_ALGORITHM);
    }

    public static String generateCertificationRequest(KeyPair keyPair, String algorithm) {
        try {
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(privateKey);

            String subject = "CN=im stephen, C=CN";

            X500Principal x500Principal = new X500Principal(subject);

            org.bouncycastle.jce.PKCS10CertificationRequest pcks10Request;
            try {
                pcks10Request = new org.bouncycastle.jce.PKCS10CertificationRequest(algorithm, x500Principal, publicKey, new DERSet(), privateKey);
            } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException e) {
                e.printStackTrace();
                return null;
            }

            PemObject pemObject = new PemObject("CERTIFICATE REQUEST", pcks10Request.getEncoded());
            StringWriter stringWriter = new StringWriter();
            PemWriter pemWriter = new PemWriter(stringWriter);
            pemWriter.writeObject(pemObject);
            pemWriter.close();
            stringWriter.close();

            String pkcs10 = stringWriter.toString();
//			String pkcs10 = Base64.encode(pcks10Request.getDEREncoded(), false); // 适用于 bcprov-jdk15on-1.46.jar
            pkcs10 = StringUtils.chomp(pkcs10.replaceAll("\r\n", "").replaceAll("\r", "").replaceAll("\n", ""));
            pkcs10 = StringUtils.removeStart(pkcs10, "-----BEGIN NEW CERTIFICATE REQUEST-----");
            pkcs10 = StringUtils.removeStart(pkcs10, "-----BEGIN CERTIFICATE REQUEST-----");
            pkcs10 = StringUtils.removeEnd(pkcs10, "-----END NEW CERTIFICATE REQUEST-----");
            pkcs10 = StringUtils.removeEnd(pkcs10, "-----END CERTIFICATE REQUEST-----");
            pkcs10 = pkcs10.replaceAll("-----BEGIN\\sNEW\\sCERTIFICATE\\sREQUEST-----", "").replaceAll("-----END\\sNEW\\sCERTIFICATE\\sREQUEST-----", "");

            return pkcs10;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        KeyPair keyPair = generateKeyPair();
        String csr = generateCertificationRequest(keyPair);
        System.out.println(csr);
    }
}
