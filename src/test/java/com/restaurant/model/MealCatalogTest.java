package com.restaurant.model;
import com.restaurant.factory.MealFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
class MealCatalogTest{
    private final MealFactory factory=new MealFactory();
    @Test
    void mealNameMatchesTypeTitle(){
        for(MealType type:MealType.values()){
            assertEquals(type.getTitle(),factory.createMeal(type).getName());
        }
    }
    @Test
    void onlyDrinksDoNotAllowToppings(){
        for(MealType type:MealType.values()){
            assertEquals(type!=MealType.DRINK,type.allowsToppings());
        }
    }

    @Test
    void everyMealHasDescription(){
        for(MealType type:MealType.values()){
            assertFalse(factory.createMeal(type).getDescription().isBlank());
        }
    }
}
