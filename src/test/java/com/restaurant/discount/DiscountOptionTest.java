package com.restaurant.discount;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
class DiscountOptionTest{
    @Test
    void everyOptionCreatesStrategyWithTitle(){
        for(DiscountOption option : DiscountOption.values()){
            assertNotNull(option.createStrategy());
            assertFalse(option.getTitle().isBlank());
        }
    }
}
