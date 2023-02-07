package com.example.demo;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

@RestController
public class PaymentController {

    private HashMap<String, BigDecimal> paymentMap = new HashMap<>();

    @Value("${ip_key}")
    private String key;

    @Value("${ip_secret}")
    private String secret;

    @GetMapping("/payment/create")
    public MetchantGenerator createPaymentId(BigDecimal amount) {

        String uuid = UUID.randomUUID().toString();
        paymentMap.put(uuid, amount);

        return MetchantGenerator.builder().id(uuid).build();
    }

    @PostMapping("/payment/verify")
    public ResponseEntity<Object> verifyPayment(String imp_uid) {

        IamportClient client = new IamportClient(key, secret);

        try {
            IamportResponse<Payment> paymentIamportResponse = client.paymentByImpUid(imp_uid);
            BigDecimal amount = paymentIamportResponse.getResponse().getAmount();
            String merchantUid = paymentIamportResponse.getResponse().getMerchantUid();

            BigDecimal mapAmount = paymentMap.get(merchantUid);

            if (mapAmount == amount) {
                return new ResponseEntity<>("검증 성공", HttpStatus.OK);
            }

            return new ResponseEntity<>("검증 실패", HttpStatus.BAD_REQUEST);

        } catch (IamportResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class MetchantGenerator {
        private String id;
    }

}

