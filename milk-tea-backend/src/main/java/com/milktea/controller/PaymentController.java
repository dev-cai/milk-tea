package com.milktea.controller;

import com.milktea.common.Result;
import com.milktea.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付控制器
 * @author MilkTea Team
 */
@Slf4j
@RestController
@RequestMapping("/payment")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    /**
     * 微信支付
     */
    @PostMapping("/wx-pay")
    public Result<Map<String, Object>> wxPay(@RequestBody Map<String, Object> params) {
        log.info("微信支付请求: {}", params);
        
        Long orderId = Long.valueOf(params.get("orderId").toString());
        String orderNo = params.get("orderNo").toString();
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        Long userId = Long.valueOf(params.get("userId").toString());
        
        return paymentService.wxPay(orderId, orderNo, amount, userId);
    }
    
    /**
     * 支付宝支付
     */
    @PostMapping("/alipay")
    public Result<Map<String, Object>> alipay(@RequestBody Map<String, Object> params) {
        log.info("支付宝支付请求: {}", params);
        
        Long orderId = Long.valueOf(params.get("orderId").toString());
        String orderNo = params.get("orderNo").toString();
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        Long userId = Long.valueOf(params.get("userId").toString());
        
        return paymentService.alipay(orderId, orderNo, amount, userId);
    }
    
    /**
     * 余额支付
     */
    @PostMapping("/balance-pay")
    public Result<Map<String, Object>> balancePay(@RequestBody Map<String, Object> params) {
        log.info("余额支付请求: {}", params);
        
        Long orderId = Long.valueOf(params.get("orderId").toString());
        String orderNo = params.get("orderNo").toString();
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        Long userId = Long.valueOf(params.get("userId").toString());
        
        return paymentService.balancePay(orderId, orderNo, amount, userId);
    }
}
