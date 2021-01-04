package com.cambrian.mall.auth.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author kuma
 */
public class SequenceUtilsTest {

    @Test
    public void distributed_id_smoke() {
        String sequence = SequenceUtils.getDistributedId("INV");
        System.out.println(sequence);
        assertTrue(true);
    }

    @Test
    public void two_id_should_not_same() {
        String s1 = SequenceUtils.getDistributedId("INV");
        String s2 = SequenceUtils.getDistributedId("INV");
        assertNotEquals("两个 id 不能相同", s1, s2);
    }

    @Test
    public void six_digits_smoke() {
        String sixDigits = SequenceUtils.getSixDigits();
        assertEquals(6, sixDigits.length());
        int value = Integer.parseInt(sixDigits);
        assertTrue(value >= 0);
        assertTrue(value < 1_000_000);
    }
}