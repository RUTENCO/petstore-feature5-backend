package com.petstore.backend.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.petstore.backend.entity.Promotion;
import com.petstore.backend.entity.Status;

class PromotionActivatedEventTest {

    private Promotion promotion;
    private PromotionActivatedEvent event;

    @BeforeEach
    void setUp() {
        promotion = new Promotion();
        promotion.setPromotionId(1);
        promotion.setPromotionName("Test Promotion");
        promotion.setDescription("Test Description");
        
        Status status = new Status();
        status.setStatusId(1);
        status.setStatusName("ACTIVE");
        promotion.setStatus(status);

        event = new PromotionActivatedEvent(this, promotion);
    }

    @Test
    @DisplayName("Should create event with promotion")
    void shouldCreateEventWithPromotion() {
        // When
        PromotionActivatedEvent newEvent = new PromotionActivatedEvent(this, promotion);

        // Then
        assertNotNull(newEvent);
        assertEquals(promotion, newEvent.getPromotion());
        assertEquals("Test Promotion", newEvent.getPromotion().getPromotionName());
    }

    @Test
    @DisplayName("Should return correct promotion")
    void shouldReturnCorrectPromotion() {
        // Then
        assertEquals(promotion, event.getPromotion());
        assertEquals(1, event.getPromotion().getPromotionId());
        assertEquals("Test Promotion", event.getPromotion().getPromotionName());
        assertEquals("ACTIVE", event.getPromotion().getStatus().getStatusName());
    }

    @Test
    @DisplayName("Should handle null promotion")
    void shouldHandleNullPromotion() {
        // When
        PromotionActivatedEvent nullEvent = new PromotionActivatedEvent(this, null);

        // Then
        assertNotNull(nullEvent);
        assertNull(nullEvent.getPromotion());
    }

    @Test
    @DisplayName("Should inherit from ApplicationEvent")
    void shouldInheritFromApplicationEvent() {
        // Then
        assertTrue(event instanceof org.springframework.context.ApplicationEvent);
        assertEquals(this, event.getSource());
    }
}
