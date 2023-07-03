package ru.nuykin.quizrestapi.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Component
public class PBFDK2Encoder implements PasswordEncoder {

    @Value("${jwt.encoder.secret}")
    private String secret;
    @Value("${jwt.encoder.iteration}")
    private Integer iteration;
    @Value("${jwt.encoder.keyLength}")
    private Integer keyLength;

    private final String SECRET_KEY_INSTANCE = "PBKDF2WithHmacSHA512";
    @Override
    public String encode(CharSequence rawPassword) {
        try {
            byte[] res = SecretKeyFactory.getInstance(SECRET_KEY_INSTANCE)
                    .generateSecret(new PBEKeySpec(
                            rawPassword.toString().toCharArray(),
                            secret.getBytes(), iteration, keyLength
                            ))
                    .getEncoded();
            return Base64.getEncoder()
                    .encodeToString(res);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
