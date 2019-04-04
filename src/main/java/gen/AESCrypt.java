package gen;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import util.Constant;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class AESCrypt {

    public static void main(String[] args) throws Exception {
        System.out.println( AESCrypt.encrypt(args[0].trim()));
    }

    public static String encrypt(String value) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(Constant.Cryto.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        String encryptedValue64 = new BASE64Encoder().encode(encryptedByteValue);
        return encryptedValue64;

    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(Constant.Cryto.KEY.getBytes(),Constant.Cryto.ALGORITHM);
        return key;
    }




}