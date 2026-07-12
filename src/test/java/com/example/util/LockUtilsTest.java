package com.example.util;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.ReentrantLock;

import static com.example.util.LockUtils.withLock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LockUtilsTest {

    @Test
    void returnsValueWhileHoldingLock() {
        ReentrantLock lock = new ReentrantLock();

        boolean heldInsideAction = withLock(lock, lock::isHeldByCurrentThread);

        assertThat(heldInsideAction).isTrue();
        assertThat(lock.isLocked()).isFalse();
    }

    @Test
    void runsVoidActionWhileHoldingLock() {
        ReentrantLock lock = new ReentrantLock();
        boolean[] heldInsideAction = {false};

        withLock(lock, () -> heldInsideAction[0] = lock.isHeldByCurrentThread());

        assertThat(heldInsideAction[0]).isTrue();
        assertThat(lock.isLocked()).isFalse();
    }

    @Test
    void unlocksWhenActionFails() {
        ReentrantLock lock = new ReentrantLock();

        assertThatThrownBy(() -> withLock(lock, () -> {
            throw new IllegalStateException("expected");
        })).isInstanceOf(IllegalStateException.class);
        assertThat(lock.isLocked()).isFalse();
    }
}
