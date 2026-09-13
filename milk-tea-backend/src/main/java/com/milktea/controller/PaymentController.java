package com.milktea.controller;

import com.milktea.common.Result;
import com.milktea.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;

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
    public Result<Map<String, Object>> wxPay(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        log.info("微信支付请求: {}", params);
        
        Long orderId = Long.valueOf(params.get("orderId").toString());
        String orderNo = params.get("orderNo").toString();
        BigDecimal amount = new BigDecimal(params.get("amount").toString());
        Long userId = authenticatedUser(request);
        
        return paymentService.wxPay(orderId, orderNo, amount, userId);
    }
    
    private Long authenticatedUser(HttpServletRequest request) {
        Object id = request.getAttribute("authenticatedUserId");
        if (id == null) throw new org.springframework.security.access.AccessDeniedException("请先登录");
        return Long.valueOf(id.toString());
    }
}
