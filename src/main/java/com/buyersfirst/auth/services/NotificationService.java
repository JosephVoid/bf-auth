package com.buyersfirst.auth.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.sms}")
    private String sms_q;

    @Value("${rabbitmq.email}")
    private String email_q;

    public boolean sendSMS(String to, String msg) {
        try {
            rabbitTemplate.convertAndSend(sms_q, "{\"to\": \"" + to + "\", \"message\": \"" + msg + "\"}");
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean sendEmail(String to, String subject, String body) {
        try {
            rabbitTemplate.convertAndSend(email_q,
                    "{\"to\": \"" + to + "\", \"subject\": \"" + subject + "\", \"body\":\"" + body + "\" }");
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
}