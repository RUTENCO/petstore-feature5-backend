package com.petstore.backend.event;

import org.springframework.context.ApplicationEvent;
import com.petstore.backend.entity.Promotion;

public class PromotionActivatedEvent extends ApplicationEvent {
    private final Promotion promotion;

    public PromotionActivatedEvent(Object source, Promotion promotion) {
        super(source);
        this.promotion = promotion;
    }

    public Promotion getPromotion() {
        return promotion;
    }
}
