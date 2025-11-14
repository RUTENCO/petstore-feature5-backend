package com.petstore.backend.entity;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StatusTest {

    private Status status;

    @BeforeEach
    void setUp() {
        status = new Status();
    }

    @Test
    void constructor_ShouldCreateEmptyStatus() {
        // Then
        assertNotNull(status);
        assertNull(status.getStatusId());
        assertNull(status.getStatusName());
    }

    @Test
    void setStatusId_ShouldSetStatusId() {
        // Given
        Integer statusId = 1;

        // When
        status.setStatusId(statusId);

        // Then
        assertEquals(statusId, status.getStatusId());
    }

    @Test
    void setStatusName_ShouldSetStatusName() {
        // Given
        String statusName = "ACTIVE";

        // When
        status.setStatusName(statusName);

        // Then
        assertEquals(statusName, status.getStatusName());
    }

    @Test
    void allFieldsCanBeSetAndRetrieved() {
        // Given
        Integer statusId = 1;
        String statusName = "INACTIVE";

        // When
        status.setStatusId(statusId);
        status.setStatusName(statusName);

        // Then
        assertEquals(statusId, status.getStatusId());
        assertEquals(statusName, status.getStatusName());
    }

    @Test
    void fieldsCanBeNullSafely() {
        // Given
        status.setStatusId(null);
        status.setStatusName(null);

        // When & Then
        assertNull(status.getStatusId());
        assertNull(status.getStatusName());

        // Should not throw any exceptions
        assertDoesNotThrow(() -> {
            status.toString();
            status.hashCode();
        });
    }

    @Test
    void statusName_ShouldAcceptEmptyString() {
        // Given
        String emptyStatusName = "";

        // When
        status.setStatusName(emptyStatusName);

        // Then
        assertEquals(emptyStatusName, status.getStatusName());
    }

    @Test
    void statusName_ShouldAcceptLongString() {
        // Given
        String longStatusName = "A".repeat(100);

        // When
        status.setStatusName(longStatusName);

        // Then
        assertEquals(longStatusName, status.getStatusName());
    }

    @Test
    void statusId_ShouldAcceptZero() {
        // Given
        Integer zeroId = 0;

        // When
        status.setStatusId(zeroId);

        // Then
        assertEquals(zeroId, status.getStatusId());
    }

    @Test
    void statusId_ShouldAcceptNegativeValue() {
        // Given
        Integer negativeId = -1;

        // When
        status.setStatusId(negativeId);

        // Then
        assertEquals(negativeId, status.getStatusId());
    }

    @Test
    void statusId_ShouldAcceptLargeValue() {
        // Given
        Integer largeId = Integer.MAX_VALUE;

        // When
        status.setStatusId(largeId);

        // Then
        assertEquals(largeId, status.getStatusId());
    }

    @Test
    void multipleStatusInstancesComparison() {
        // Given
        Status status1 = new Status();
        Status status2 = new Status();
        
        status1.setStatusId(1);
        status1.setStatusName("ACTIVE");
        
        status2.setStatusId(1);
        status2.setStatusName("ACTIVE");

        // Then
        assertEquals(status1.getStatusId(), status2.getStatusId());
        assertEquals(status1.getStatusName(), status2.getStatusName());
    }

    @Test
    void statusWithCompleteValidData() {
        // Given
        Integer statusId = 42;
        String statusName = "CUSTOM_STATUS";

        // When
        status.setStatusId(statusId);
        status.setStatusName(statusName);

        // Then
        assertEquals(statusId, status.getStatusId());
        assertEquals(statusName, status.getStatusName());
        assertNotNull(status);
    }

    @Test
    void statusName_ShouldAcceptSpecialCharacters() {
        // Given
        String specialStatusName = "STATUS_TEST-123!@#";

        // When
        status.setStatusName(specialStatusName);

        // Then
        assertEquals(specialStatusName, status.getStatusName());
    }

    @Test
    void statusName_ShouldAcceptLongNames() {
        // Given
        String longStatusName = "VERY_LONG_STATUS_NAME_THAT_EXCEEDS_NORMAL_EXPECTATIONS_FOR_TESTING_PURPOSES";

        // When
        status.setStatusName(longStatusName);

        // Then
        assertEquals(longStatusName, status.getStatusName());
        assertEquals(75, status.getStatusName().length());
    }

    @Test
    void statusConsistency_AfterMultipleChanges() {
        // Given & When
        status.setStatusId(1);
        status.setStatusName("INITIAL");
        
        status.setStatusId(2);
        status.setStatusName("MODIFIED");
        
        status.setStatusId(3);
        status.setStatusName("FINAL");

        // Then
        assertEquals(3, status.getStatusId());
        assertEquals("FINAL", status.getStatusName());
    }
}
