package com.restaurant.builder;
import com.restaurant.factory.MealFactory;
import com.restaurant.model.MealType;
import com.restaurant.model.Order;
import com.restaurant.model.OrderStatus;
import com.restaurant.model.OrderType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
class OrderBuilderTest {
    private final MealFactory factory=new MealFactory();
    @Test
    void buildsDineInOrderWithAllFields(){
        Order order=new OrderBuilder(7)
                .type(OrderType.DINE_IN)
                .table(5)
                .comment("no onions")
                .addMeal(factory.createMeal(MealType.PIZZA))
                .addMeal(factory.createMeal(MealType.DRINK))
                .build();
        assertEquals(7,order.getId());
        assertEquals(OrderType.DINE_IN,order.getType());
        assertEquals(5,order.getTableNumber());
        assertEquals("no onions",order.getComment());
        assertEquals(2,order.getMeals().size());
        assertEquals(OrderStatus.NEW,order.getStatus());
    }
    @Test
    void takeawayOrderIgnoresTableNumber(){
        Order order=new OrderBuilder(1)
                .type(OrderType.TAKEAWAY)
                .table(3)
                .addMeal(factory.createMeal(MealType.BURGER))
                .build();
        assertEquals(0,order.getTableNumber());
    }
    @Test
    void nullCommentBecomesEmptyString(){
        Order order=new OrderBuilder(1)
                .table(2)
                .comment(null)
                .addMeal(factory.createMeal(MealType.PASTA))
                .build();
        assertEquals("",order.getComment());
    }
    @Test
    void emptyOrderIsRejected(){
        OrderBuilder builder=new OrderBuilder(1).table(2);
        assertThrows(IllegalStateException.class,builder::build);
    }
    @Test
    void dineInOrderWithoutTableIsRejected(){
        OrderBuilder builder=new OrderBuilder(1).addMeal(factory.createMeal(MealType.PIZZA));
        assertThrows(IllegalStateException.class,builder::build);
    }
}
