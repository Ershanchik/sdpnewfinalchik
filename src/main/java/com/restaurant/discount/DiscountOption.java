package com.restaurant.discount;
import com.restaurant.model.DiscountStrategy;
import com.restaurant.model.NoDiscount;
import java.util.function.Supplier;
public enum DiscountOption{
    NONE(NoDiscount::new),
    STUDENT(StudentDiscount::new),
    HAPPY_HOUR(HappyHourDiscount::new),
    PROMO_CODE(PromoCodeDiscount::new);
    private final Supplier<DiscountStrategy>creator;
    DiscountOption(Supplier<DiscountStrategy>creator){
        this.creator=creator;
    }
    public DiscountStrategy createStrategy(){
        return creator.get();
    }

    public String getTitle(){
        return createStrategy().getName();
    }
}
