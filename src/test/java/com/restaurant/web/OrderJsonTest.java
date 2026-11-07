package com.restaurant.web;
import com.restaurant.builder.OrderBuilder;
import com.restaurant.discount.StudentDiscount;
import com.restaurant.factory.MealFactory;
import com.restaurant.model.MealType;
import com.restaurant.model.Order;
import com.restaurant.model.OrderType;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
class OrderJsonTest{
    private final MealFactory factory=new MealFactory();
    @Test
    void menuContainsMealsAndToppings(){
        String json=OrderJson.menu(factory);
        assertTrue(json.contains("\"type\":\"PIZZA\""));
        assertTrue(json.contains("\"allowsToppings\":false"));
        assertTrue(json.contains("\"id\":\"EXTRA_CHEESE\""));
    }
    @Test
    void orderContainsFieldsAndNotifications(){
        Order order=new OrderBuilder(4)
                .type(OrderType.DINE_IN)
                .table(2)
                .comment("extra \"crispy\"")
                .addMeal(factory.createMeal(MealType.PIZZA))
                .build();
        String json=OrderJson.order(order,List.of("Kitchen: hello"));
        assertTrue(json.contains("\"id\":4"));
        assertTrue(json.contains("\"status\":\"NEW\""));
        assertTrue(json.contains("\"table\":2"));
        assertTrue(json.contains("\"comment\":\"extra \\\"crispy\\\"\""));
        assertTrue(json.contains("\"total\":3500"));
        assertTrue(json.contains("\"notifications\":[\"Kitchen: hello\"]"));
    }
    @Test
    void quoteEscapesSpecialCharacters(){
        assertEquals("\"a\\\"b\\\\c\\nd\"",OrderJson.quote("a\"b\\c\nd"));
        assertEquals("\"\\u0001\"",OrderJson.quote("\u0001"));
    }
    @Test
    void menuContainsDiscountOptions(){
        String json=OrderJson.menu(factory);
        assertTrue(json.contains("\"id\":\"HAPPY_HOUR\""));
        assertTrue(json.contains("Happy hour"));
    }
    @Test
    void orderShowsSubtotalDiscountAndTotal(){
        Order order=new OrderBuilder(1)
                .type(OrderType.TAKEAWAY)
                .discount(new StudentDiscount())
                .addMeal(factory.createMeal(MealType.PIZZA))
                .addMeal(factory.createMeal(MealType.DRINK))
                .build();
        String json=OrderJson.order(order,List.of());
        assertTrue(json.contains("\"subtotal\":4100"));
        assertTrue(json.contains("\"discount\":410"));
        assertTrue(json.contains("\"total\":3690"));
    }
}
