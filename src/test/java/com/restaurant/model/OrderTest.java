package com.restaurant.model;
import com.restaurant.decorator.Bacon;
import com.restaurant.discount.StudentDiscount;
import com.restaurant.factory.MealFactory;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
class OrderTest {
    private static class TestMeal implements Meal {
        private final String name;
        private final double price;
        TestMeal(String name,double price){
            this.name=name;
            this.price=price;
        }
        @Override
        public String getName(){
            return name;
        }
        @Override
        public String getDescription(){
            return name;
        }
        @Override
        public double getPrice(){
            return price;
        }
    }
    @Test
    void newOrderIsEmptyWithNewStatus(){
        Order order=new Order(1);
        assertEquals(0,order.getTotal(),0.001);
        assertEquals(OrderStatus.NEW,order.getStatus());
    }
    @Test
    void totalIsSumOfMealPrices(){
        Order order=new Order(1);
        order.addMeal(new TestMeal("Pizza",3500));
        order.addMeal(new TestMeal("Cola",600));
        assertEquals(4100,order.getTotal(),0.001);
    }
    @Test
    void totalIncludesToppingsOfDecoratedMeals(){
        MealFactory factory=new MealFactory();
        Order order=new Order(1);
        order.addMeal(new Bacon(factory.createMeal(MealType.BURGER)));
        order.addMeal(factory.createMeal(MealType.DRINK));
        assertEquals(4000,order.getTotal(),0.001);
    }
    @Test
    void discountIsSubtractedFromTotal(){
        MealFactory factory=new MealFactory();
        Order order=new Order(1);
        order.addMeal(factory.createMeal(MealType.PIZZA));
        order.addMeal(factory.createMeal(MealType.DRINK));
        order.setDiscount(new StudentDiscount());
        assertEquals(4100,order.getSubtotal(),0.001);
        assertEquals(410,order.getDiscountAmount(),0.001);
        assertEquals(3690,order.getTotal(),0.001);
    }
    @Test
    void discountCannotMakeTotalNegative(){
        Order order=new Order(1);
        order.addMeal(new TestMeal("Cola",600));
        order.setDiscount(new DiscountStrategy(){
            @Override
            public String getName(){
                return "Huge discount";
            }
            @Override
            public double calculateDiscount(List<Meal>meals){
                return 10_000;
            }
        });
        assertEquals(0,order.getTotal(),0.001);
    }
}
