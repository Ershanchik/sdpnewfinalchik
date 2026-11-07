package com.restaurant.console;
import com.restaurant.builder.OrderBuilder;
import com.restaurant.decorator.ExtraCheese;
import com.restaurant.decorator.Mushrooms;
import com.restaurant.discount.StudentDiscount;
import com.restaurant.factory.MealFactory;
import com.restaurant.model.MealType;
import com.restaurant.model.Order;
import com.restaurant.model.OrderType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
class OrderPrinterTest {
    private final MealFactory factory=new MealFactory();
    @Test
    void summaryListsMealsAndTotal(){
        Order order=new Order(3);
        order.addMeal(factory.createMeal(MealType.PIZZA));
        order.addMeal(factory.createMeal(MealType.DRINK));
        String text=OrderPrinter.format(order);
        assertTrue(text.contains("Order #3"));
        assertTrue(text.contains("Margherita pizza"));
        assertTrue(text.contains("Total: 4100"));
    }
    @Test
    void emptyOrderHasZeroTotal(){
        String text=OrderPrinter.format(new Order(1));
        assertTrue(text.contains("Total: 0"));
    }
    @Test
    void summaryShowsToppingsAndTotalWithToppings(){
        Order order=new Order(1);
        order.addMeal(new Mushrooms(new ExtraCheese(factory.createMeal(MealType.PIZZA))));
        String text=OrderPrinter.format(order);
        assertTrue(text.contains("extra cheese"));
        assertTrue(text.contains("mushrooms"));
        assertTrue(text.contains("Total: 4200"));
    }

    @Test
    void summaryShowsDineInTableAndComment(){
        Order order=new OrderBuilder(5)
                .type(OrderType.DINE_IN)
                .table(4)
                .comment("no onions")
                .addMeal(factory.createMeal(MealType.BURGER))
                .build();
        String text=OrderPrinter.format(order);
        assertTrue(text.contains("dine-in, table 4"));
        assertTrue(text.contains("Comment: no onions"));
    }
    @Test
    void takeawaySummaryHasNoTable(){
        Order order=new OrderBuilder(6)
                .type(OrderType.TAKEAWAY)
                .addMeal(factory.createMeal(MealType.DRINK))
                .build();
        String text=OrderPrinter.format(order);
        assertTrue(text.contains("takeaway"));
        assertFalse(text.contains("table"));
        assertFalse(text.contains("Comment"));
    }
    @Test
    void summaryShowsDiscountLines(){
        Order order=new OrderBuilder(2)
                .type(OrderType.TAKEAWAY)
                .discount(new StudentDiscount())
                .addMeal(factory.createMeal(MealType.PIZZA))
                .addMeal(factory.createMeal(MealType.DRINK))
                .build();
        String text=OrderPrinter.format(order);
        assertTrue(text.contains("Subtotal: 4100"));
        assertTrue(text.contains("Discount (Student discount 10%): -410"));
        assertTrue(text.contains("Total: 3690"));
    }
    @Test
    void summaryWithoutDiscountHasNoDiscountLines(){
        Order order=new Order(1);
        order.addMeal(factory.createMeal(MealType.PIZZA));
        assertFalse(OrderPrinter.format(order).contains("Discount"));
    }
}
