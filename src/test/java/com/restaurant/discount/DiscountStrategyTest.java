package com.restaurant.discount;
import com.restaurant.factory.MealFactory;
import com.restaurant.model.Meal;
import com.restaurant.model.MealType;
import com.restaurant.model.NoDiscount;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
class DiscountStrategyTest{
    private final MealFactory factory=new MealFactory();
    private List<Meal>meals(MealType... types){
        List<Meal>meals=new ArrayList<>();
        for(MealType type : types){
            meals.add(factory.createMeal(type));
        }
        return meals;
    }
    @Test
    void noDiscountIsZero(){
        assertEquals(0,new NoDiscount().calculateDiscount(meals(MealType.PIZZA)),0.001);
    }
    @Test
    void studentDiscountIsTenPercentOfSubtotal(){
        double discount=new StudentDiscount().calculateDiscount(meals(MealType.PIZZA,MealType.DRINK));
        assertEquals(410,discount,0.001);
    }
    @Test
    void happyHourHalvesOnlyDrinks(){
        HappyHourDiscount happyHour=new HappyHourDiscount();
        assertEquals(600,happyHour.calculateDiscount(meals(MealType.PIZZA,MealType.DRINK,MealType.DRINK)),0.001);
        assertEquals(0,happyHour.calculateDiscount(meals(MealType.PIZZA)),0.001);
    }
    @Test
    void promoCodeNeedsMinimumSubtotal(){
        PromoCodeDiscount promo=new PromoCodeDiscount();
        assertEquals(0,promo.calculateDiscount(meals(MealType.BURGER)),0.001);
        assertEquals(500,promo.calculateDiscount(meals(MealType.PASTA)),0.001);
        assertEquals(500,promo.calculateDiscount(meals(MealType.PIZZA,MealType.DRINK)),0.001);
    }
}
