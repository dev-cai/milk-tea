package com.milktea.service;

import com.milktea.exception.BusinessException;
import org.junit.jupiter.api.Test;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.*;

class OrderStateMachineTest {
    @Test void pendingPayToMakingAllowed() { assertDoesNotThrow(() -> OrderStateMachine.assertTransition(0, 1)); }
    @Test void pendingPayToCanceledAllowed() { assertDoesNotThrow(() -> OrderStateMachine.assertTransition(0, 5)); }
    @Test void pendingMakeToMakingAllowed() { assertDoesNotThrow(() -> OrderStateMachine.assertTransition(1, 2)); }
    @Test void pendingMakeToRefundAllowed() { assertDoesNotThrow(() -> OrderStateMachine.assertTransition(1, 6)); }
    @Test void makingToPickupAllowed() { assertDoesNotThrow(() -> OrderStateMachine.assertTransition(2, 3)); }
    @Test void pickupToFinishedAllowed() { assertDoesNotThrow(() -> OrderStateMachine.assertTransition(3, 4)); }
    @Test void finishedToRefundAllowed() { assertDoesNotThrow(() -> OrderStateMachine.assertTransition(4, 6)); }
    @Test void refundToFinishedAllowed() { assertDoesNotThrow(() -> OrderStateMachine.assertTransition(6, 4)); }
    @Test void refundToRefundedAllowed() { assertDoesNotThrow(() -> OrderStateMachine.assertTransition(6, 7)); }
    @Test void canceledIsTerminal() { assertThrows(BusinessException.class, () -> OrderStateMachine.assertTransition(5, 1)); }
    @Test void refundedIsTerminal() { assertThrows(BusinessException.class, () -> OrderStateMachine.assertTransition(7, 4)); }
    @Test void cannotFinishBeforePickup() { assertThrows(BusinessException.class, () -> OrderStateMachine.assertTransition(1, 4)); }
    @Test void cannotPayCanceled() { assertThrows(BusinessException.class, () -> OrderStateMachine.assertTransition(5, 1)); }
    @Test void cannotCancelFinished() { assertThrows(BusinessException.class, () -> OrderStateMachine.assertTransition(4, 5)); }
    @Test void cannotSkipMaking() { assertThrows(BusinessException.class, () -> OrderStateMachine.assertTransition(1, 3)); }
    @Test void nullCurrentRejected() { assertThrows(BusinessException.class, () -> OrderStateMachine.assertTransition(null, 1)); }
    @Test void nullTargetRejected() { assertThrows(BusinessException.class, () -> OrderStateMachine.assertTransition(1, null)); }
    @Test void unknownCurrentRejected() { assertThrows(BusinessException.class, () -> OrderStateMachine.assertTransition(99, 1)); }
    @Test void sameStateRejected() { assertThrows(BusinessException.class, () -> OrderStateMachine.assertTransition(2, 2)); }
    @Test void allActiveStatusesHaveDeterministicValidation() {
        int[] next = {1, 2, 3, 4, 6};
        IntStream.rangeClosed(0, 4).forEach(status -> assertDoesNotThrow(() -> OrderStateMachine.assertTransition(status, next[status])));
        assertDoesNotThrow(() -> OrderStateMachine.assertTransition(6, 7));
    }
}
