package sk.svelta.icaltimereports.web.util;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class to generate md5 hash strings.
 *
 * @author Jaroslav Švelta
 */
public class MD5Util {

    /**
     * This class cannot be instancionated.
     */
    private MD5Util() {
    }

    /**
     * Generate md5 hash from <code>value</code>
     *
     * @param value string to be hashed
     * @return hash string from <code>value</code>
     * @throws NoSuchAlgorithmException when md5 hash algorithm is not implemented
     */
    public static String generateHash(String value) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(value.getBytes(Charset.forName("utf-8")));
        BigInteger number = new BigInteger(1, digest); // 1 - positive number
        return number.toString(16); // hexadecimal
    }

}
