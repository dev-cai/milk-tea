package com.milktea.service;

import com.milktea.exception.BusinessException;
import java.util.Map;
import java.util.Set;

/** Central order transition rules used by payment and fulfilment workflows. */
public final class OrderStateMachine {
    private static final Map<Integer, Set<Integer>> TRANSITIONS = Map.of(
            0, Set.of(1, 5),
            1, Set.of(2, 5, 6),
            2, Set.of(3, 6),
            3, Set.of(4, 6),
            4, Set.of(6),
            6, Set.of(4, 7),
            5, Set.of(),
            7, Set.of());

    private OrderStateMachine() {
    }

    public static void assertTransition(Integer current, Integer target) {
        if (current == null || target == null || !TRANSITIONS.getOrDefault(current, Set.of()).contains(target)) {
            throw new BusinessException(409, "不允许的订单状态流转: " + current + " -> " + target);
        }
    }
}
