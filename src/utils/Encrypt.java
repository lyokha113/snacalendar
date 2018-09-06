/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;
import javax.xml.bind.DatatypeConverter;

/**
 * Encryt methods for text, password
 * Token helper
 *
 */
public class Encrypt implements Serializable {

    private static final String base64Key = "Av4Nwh/pTj5l9DeTKdqHJbsGQOQxor8fHGWnt4MV5y8=";
    private static final String bcryptSalt = "$2a$10$MsczYV6uidRbz4DQc.YNaO";

    public static String EncryptText(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes());
        byte[] pwdEncrypted = md.digest();
        return DatatypeConverter.printHexBinary(pwdEncrypted).toUpperCase();
    }

    public static String EncryptPwd(String pwd) {
        return BCrypt.hashpw(pwd, bcryptSalt);
    }

    public static String generateToken(String subject, long timeExperid) {
        String id = UUID.randomUUID().toString();
        Date now = new Date();
        Date exp = new Date(System.currentTimeMillis() + timeExperid);

        return Jwts.builder()
                .setId(id)
                .setSubject(subject)
                .setNotBefore(now)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS256, base64Key)
                .compact();
    }

    public static String verifyToken(String token) {
        try {
            return Jwts.parser().setSigningKey(base64Key).parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
            return null;
        }
    }

}
