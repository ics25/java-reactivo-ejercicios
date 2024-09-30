package com.fynalproject.demo.controller;

import com.fynalproject.demo.domain.Payment;
import com.fynalproject.demo.domain.PaymentStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentsController {
    @PostMapping()
    public PaymentStatus getPayments(@RequestBody Payment payment) {
        String userId = payment.getUserId();
        if (userId.indexOf("0") % 3 == 0) {
            return PaymentStatus.APPROVED;
        } else if (userId.indexOf("0") % 3 == 1) {
            return PaymentStatus.REJECTED;
        }
        return PaymentStatus.INVALID;
    }
}
