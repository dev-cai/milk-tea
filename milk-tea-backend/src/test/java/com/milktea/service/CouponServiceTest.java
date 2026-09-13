package com.milktea.service;

import com.milktea.entity.Coupon;
import com.milktea.entity.UserCoupon;
import com.milktea.exception.BusinessException;
import com.milktea.mapper.CouponMapper;
import com.milktea.mapper.UserCouponMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CouponServiceTest {
    private CouponMapper couponMapper;
    private UserCouponMapper userCouponMapper;
    private CouponService service;

    @BeforeEach
    void setUp() {
        couponMapper = mock(CouponMapper.class);
        userCouponMapper = mock(UserCouponMapper.class);
        service = new CouponService(couponMapper, userCouponMapper);
    }

    private void stubCoupon(int type, String discount, String minAmount) {
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setId(10L);
        userCoupon.setUserId(2L);
        userCoupon.setCouponId(20L);
        userCoupon.setStatus(0);
        Coupon coupon = new Coupon();
        coupon.setId(20L);
        coupon.setType(type);
        coupon.setDiscount(new BigDecimal(discount));
        coupon.setMinAmount(new BigDecimal(minAmount));
        coupon.setStatus(1);
        coupon.setValidStart(LocalDateTime.now().minusMinutes(1));
        coupon.setValidEnd(LocalDateTime.now().plusMinutes(1));
        when(userCouponMapper.selectById(10L)).thenReturn(userCoupon);
        when(couponMapper.selectById(20L)).thenReturn(coupon);
    }

    @Test
    void fullReductionCouponUsesFixedAmount() {
        stubCoupon(1, "10", "30");
        assertEquals(new BigDecimal("10.00"), service.calculateDiscount(2L, 10L, new BigDecimal("35")));
    }

    @Test
    void discountCouponCalculatesReductionNotPayableAmount() {
        stubCoupon(2, "0.8", "0");
        // 8折 on 25 yuan means 5 yuan reduction, not 20 yuan reduction.
        assertEquals(new BigDecimal("5.00"), service.calculateDiscount(2L, 10L, new BigDecimal("25")));
    }

    @Test
    void legacyTenPointDiscountValueIsStillInterpretedAsRate() {
        stubCoupon(2, "8", "0");
        assertEquals(new BigDecimal("5.00"), service.calculateDiscount(2L, 10L, new BigDecimal("25")));
    }

    @Test
    void exchangeCouponCannotBeAppliedToOrder() {
        stubCoupon(3, "100", "0");
        assertThrows(BusinessException.class,
                () -> service.calculateDiscount(2L, 10L, new BigDecimal("25")));
    }

    @Test
    void couponBelowThresholdIsRejected() {
        stubCoupon(1, "10", "30");
        assertThrows(BusinessException.class,
                () -> service.calculateDiscount(2L, 10L, new BigDecimal("29.99")));
    }
}
