package com.project.binar.okariru.service.jwtAuth;

import com.project.binar.okariru.repository.CustomerRepository;
import com.project.binar.okariru.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final CustomerRepository customerRepository;
    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;
    private static final int OTPDURATION = 5;


    public String generateOtp(String email) {

        if (customerRepository.existsByEmail(email) == false) {
            throw new IllegalArgumentException("Email tidak ditemukan");
        }

        SecureRandom random = new SecureRandom();
        String otpKeyValue = String.format("%05d", random.nextInt(10000));

        String key = "OTP_" + email;
        redisTemplate.opsForValue().set(key, otpKeyValue, Duration.ofMinutes(OTPDURATION));

        emailService.sendEmail(email, otpKeyValue);

        return otpKeyValue;
    }

    public String validateOtpAndGetResetToken(String email, String inputOTP) {
        String key = "OTP_" + email;

        if (redisTemplate.opsForValue().get(key) != null &&  redisTemplate.opsForValue().get(key).equals(inputOTP)) {
            SecureRandom random = new SecureRandom();

            redisTemplate.delete(key);

            String resetToken = String.format("%05d", random.nextInt(10000));

            redisTemplate.opsForValue().set("RESET_TOKEN_" + resetToken, email, Duration.ofMinutes(OTPDURATION));
            return resetToken;
        } else
            return null;
    }

    public String getEmailByResetToken(String resetToken) {
        return redisTemplate.opsForValue().get("RESET_TOKEN_" + resetToken);
    }

    public void deleteResetToken(String resetToken) {
        redisTemplate.delete("RESET_TOKEN_" + resetToken);
    }
}
