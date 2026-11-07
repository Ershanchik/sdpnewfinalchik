package com.restaurant.facade;
import com.restaurant.decorator.Topping;
import com.restaurant.discount.DiscountOption;
import com.restaurant.model.Meal;
import com.restaurant.model.MealType;
import com.restaurant.model.Order;
import com.restaurant.model.OrderStatus;
import com.restaurant.model.OrderType;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
class RestaurantFacadeTest {
    private final List<String> live=new ArrayList<>();
    private final RestaurantFacade facade=new RestaurantFacade(live::add);
    @Test
    void createMealAppliesAllToppings(){
        Meal meal=facade.createMeal(MealType.PIZZA,List.of(Topping.EXTRA_CHEESE,Topping.BACON));
        assertEquals(4500,meal.getPrice(),0.001);
        assertTrue(meal.getDescription().endsWith("extra cheese + bacon"));
    }
    @Test
    void toppingsOnDrinkAreRejected(){
        assertThrows(IllegalArgumentException.class,()->facade.createMeal(MealType.DRINK,List.of(Topping.BACON)));
    }
    @Test
    void placedOrdersGetIncreasingIdsAndDiscount(){
        List<Meal> meals=List.of(facade.createMeal(MealType.PIZZA),facade.createMeal(MealType.DRINK));
        Order first=facade.placeOrder(OrderType.TAKEAWAY,0,"",DiscountOption.STUDENT,meals);
        Order second=facade.placeOrder(OrderType.TAKEAWAY,0,"",DiscountOption.NONE,meals);
        assertEquals(1,first.getId());
        assertEquals(3690,first.getTotal(),0.001);
        assertEquals(2,second.getId());
        assertEquals(4100,second.getTotal(),0.001);
    }
    @Test
    void failedOrderDoesNotUseUpAnId(){
        assertThrows(IllegalStateException.class,()->facade.placeOrder(OrderType.TAKEAWAY,0,"",DiscountOption.NONE,List.of()));
        Order order=facade.placeOrder(OrderType.TAKEAWAY,0,"",DiscountOption.NONE,List.of(facade.createMeal(MealType.BURGER)));
        assertEquals(1,order.getId());
    }
    @Test
    void advancingNotifiesKitchenAndCustomer(){
        Order order=facade.placeOrder(OrderType.DINE_IN,4,"no onions",DiscountOption.NONE,List.of(facade.createMeal(MealType.PASTA)));
        facade.advanceOrder(order.getId());
        assertEquals(OrderStatus.COOKING,order.getStatus());
        assertEquals(List.of("Kitchen: order #1 NEW -> COOKING (note: no onions)","Customer: your order #1 is being prepared"),live);
        assertEquals(live,facade.getNotifications(order.getId()));
    }

    @Test
    void unknownOrderIsHandled(){
        assertTrue(facade.findOrder(5).isEmpty());
        assertTrue(facade.getNotifications(5).isEmpty());
        assertThrows(IllegalArgumentException.class,()->facade.advanceOrder(5));
    }
}
