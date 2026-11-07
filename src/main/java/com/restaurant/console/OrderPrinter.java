package com.restaurant.console;
import com.restaurant.model.Meal;
import com.restaurant.model.Order;
import com.restaurant.model.OrderType;
public final class OrderPrinter {
    private OrderPrinter(){}
    public static String format(Order order){
        StringBuilder text=new StringBuilder();
        text.append(String.format("Order #%d [%s] %s%n",order.getId(),order.getStatus(),describePlace(order)));
        if(!order.getComment().isEmpty()){
            text.append(String.format("Comment: %s%n",order.getComment()));
        }
        for(Meal meal:order.getMeals()){
            text.append(String.format("  %-55s %6.0f%n",meal.getDescription(),meal.getPrice()));
        }
        if(order.getDiscountAmount()>0){
            text.append(String.format("Subtotal: %.0f%n",order.getSubtotal()));
            text.append(String.format("Discount (%s): -%.0f%n",order.getDiscount().getName(),order.getDiscountAmount()));
        }
        text.append(String.format("Total: %.0f%n",order.getTotal()));
        return text.toString();
    }

    private static String describePlace(Order order){
        if(order.getType()==OrderType.DINE_IN){
            return "dine-in, table "+order.getTableNumber();
        }
        return "takeaway";
    }
}
