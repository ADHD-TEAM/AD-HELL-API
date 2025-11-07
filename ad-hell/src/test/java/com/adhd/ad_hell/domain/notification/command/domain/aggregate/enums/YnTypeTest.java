package com.adhd.ad_hell.domain.notification.command.domain.aggregate.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YnTypeTest {

    @Test
    void yesNoAndIsYes() {
        assertEquals(YnType.Y, YnType.yes());
        assertEquals(YnType.N, YnType.no());
        assertTrue(YnType.Y.isYes());
        assertFalse(YnType.N.isYes());
    }

    @Test
    void valuesAndValueOfCovered() {
        YnType[] values = YnType.values();
        assertEquals(2, values.length);
        assertEquals(YnType.Y, YnType.valueOf("Y"));
        assertEquals(YnType.N, YnType.valueOf("N"));
    }
}
