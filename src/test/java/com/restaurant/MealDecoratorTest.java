package com.restaurant.decorator;
import com.restaurant.factory.MealFactory;
import com.restaurant.model.Meal;
import com.restaurant.model.MealType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
class MealDecoratorTest {
    private final MealFactory factory=new MealFactory();
    @Test
    void toppingAddsItsPriceAndDescription(){
        Meal meal=new ExtraCheese(factory.createMeal(MealType.PIZZA));
        assertEquals(3900,meal.getPrice(),0.001);
        assertTrue(meal.getDescription().endsWith("extra cheese"));
    }
    @Test
    void toppingsCanBeStacked(){
        Meal meal=new Bacon(new ExtraCheese(factory.createMeal(MealType.PIZZA)));
        assertEquals(4500,meal.getPrice(),0.001);
        assertTrue(meal.getDescription().contains("extra cheese"));
        assertTrue(meal.getDescription().contains("bacon"));
    }
    @Test
    void decoratorKeepsMealName(){
        Meal meal=new Mushrooms(factory.createMeal(MealType.PASTA));
        assertEquals("Pasta",meal.getName());
    }
    @Test
    void nullMealIsRejected(){
        assertThrows(NullPointerException.class,()->new Bacon(null));
    }
}
