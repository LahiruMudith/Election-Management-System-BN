package lk.ijse.election_backend.controller;

import lk.ijse.election_backend.dto.PaymentRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/log/payment")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class PaymentController {

    @Value("${payhere.merchant_id}")
    private String merchantId;

    @Value("${payhere.merchant_secret}")
    private String merchantSecret;

    @PostMapping("/create")
    public Map<String, Object> createPayment(@RequestBody PaymentRequestDto request) throws Exception {
        String amountFormatted = String.format("%.2f", request.getAmount());
        String currency = "LKR";

        // Hashing merchant_secret
        String md5MerchantSecret = md5(merchantSecret).toUpperCase();

        // Hash input: merchant_id + order_id + amount + currency + md5(secret)
        String raw = merchantId + request.getOrderId() + amountFormatted + currency + md5MerchantSecret;
        String hash = md5(raw).toUpperCase();

        Map<String, Object> payment = new HashMap<>();
        payment.put("sandbox", true);
        payment.put("merchant_id", merchantId);
        payment.put("return_url", "http://localhost:8080/payhere/success");
        payment.put("cancel_url", "http://localhost:8080/payhere/cancel");
        payment.put("notify_url", "http://localhost:8080/payhere/notify");
        payment.put("order_id", request.getOrderId());
        payment.put("items", request.getItemName());
        payment.put("amount", amountFormatted);
        payment.put("currency", currency);
        payment.put("hash", hash);
        payment.put("first_name", request.getFirstName());
        payment.put("last_name", request.getLastName());
        payment.put("email", request.getEmail());
        payment.put("phone", request.getPhone());

        // Optional fields
        if (request.getAddress() != null) {
            payment.put("address", request.getAddress());
        }
        if (request.getCity() != null) {
            payment.put("city", request.getCity());
        }
        if (request.getCountry() != null) {
            payment.put("country", request.getCountry());
        }

        return payment;
    }

    // Simple MD5 hash implementation for Java
    private static String md5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : messageDigest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}