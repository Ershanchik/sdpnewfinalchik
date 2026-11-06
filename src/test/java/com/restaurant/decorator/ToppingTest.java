
package com.restaurant.decorator;
import com.restaurant.factory.MealFactory;
import com.restaurant.model.Meal;
import com.restaurant.model.MealType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
class ToppingTest{
    private final MealFactory factory=new MealFactory();
    @Test
    void applyingToppingAddsItsPrice(){
        Meal pizza=factory.createMeal(MealType.PIZZA);
        for(Topping topping:Topping.values()){
            Meal decorated=topping.applyTo(pizza);
            assertEquals(pizza.getPrice()+topping.getPrice(),decorated.getPrice(),0.001);
        }
    }
    @Test
    void everyToppingChangesDescription(){
        Meal pizza=factory.createMeal(MealType.PIZZA);
        for(Topping topping:Topping.values()){
            assertTrue(topping.applyTo(pizza).getDescription().startsWith(pizza.getDescription()+" + "));
        }
    }
}