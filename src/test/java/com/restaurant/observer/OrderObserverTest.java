package com.restaurant.observer;
import com.restaurant.builder.OrderBuilder;
import com.restaurant.factory.MealFactory;
import com.restaurant.model.MealType;
import com.restaurant.model.Order;
import com.restaurant.model.OrderObserver;
import com.restaurant.model.OrderStatus;
import com.restaurant.model.OrderType;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
class OrderObserverTest{
    private final MealFactory factory=new MealFactory();
    private Order orderWithComment(String comment){
        return new OrderBuilder(1)
                .type(OrderType.TAKEAWAY)
                .comment(comment)
                .addMeal(factory.createMeal(MealType.PIZZA))
                .build();
    }
    @Test
    void observerGetsOldStatusAndSeesNewOne(){
        Order order=orderWithComment("");
        List<String> events=new ArrayList<>();
        order.addObserver((changed,oldStatus) -> events.add(oldStatus+"->"+changed.getStatus()));
        order.setStatus(OrderStatus.COOKING);
        assertEquals(List.of("NEW->COOKING"),events);
    }
    @Test
    void settingSameStatusDoesNotNotify(){
        Order order=orderWithComment("");
        List<String> events=new ArrayList<>();
        order.addObserver((changed,oldStatus) -> events.add("changed"));
        order.setStatus(OrderStatus.NEW);
        assertTrue(events.isEmpty());
    }
    @Test
    void removedObserverIsNotNotified(){
        Order order=orderWithComment("");
        List<String> events=new ArrayList<>();
        OrderObserver observer=(changed,oldStatus) -> events.add("changed");
        order.addObserver(observer);
        order.removeObserver(observer);
        order.advanceStatus();
        assertTrue(events.isEmpty());
    }
    @Test
    void advanceStatusStopsAtDelivered(){
        Order order=orderWithComment("");
        List<String> events=new ArrayList<>();
        order.addObserver((changed,oldStatus) -> events.add(changed.getStatus().name()));
        for(int i=0;i < 5;i++){
            order.advanceStatus();
        }
        assertEquals(List.of("COOKING","READY","DELIVERED"),events);
    }
    @Test
    void kitchenSeesTransitionAndNoteWhenCookingStarts(){
        Order order=orderWithComment("no onions");
        List<String> messages=new ArrayList<>();
        order.addObserver(new KitchenDisplay(messages::add));
        order.advanceStatus();
        order.advanceStatus();
        assertEquals("Kitchen: order #1 NEW -> COOKING (note: no onions)",messages.get(0));
        assertEquals("Kitchen: order #1 COOKING -> READY",messages.get(1));
    }
    @Test
    void customerGetsMessageForEachStatus(){
        Order order=orderWithComment("");
        List<String> messages=new ArrayList<>();
        order.addObserver(new CustomerNotifier(messages::add));
        order.advanceStatus();
        order.advanceStatus();
        order.advanceStatus();
        assertEquals(3,messages.size());
        assertTrue(messages.get(0).contains("being prepared"));
        assertTrue(messages.get(1).contains("is ready"));
        assertTrue(messages.get(2).contains("delivered"));
    }
}
