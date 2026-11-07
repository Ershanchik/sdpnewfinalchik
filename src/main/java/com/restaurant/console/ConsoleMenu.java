package com.restaurant.console;
import com.restaurant.builder.OrderBuilder;
import com.restaurant.decorator.Topping;
import com.restaurant.discount.DiscountOption;
import com.restaurant.factory.MealFactory;
import com.restaurant.model.Meal;
import com.restaurant.model.MealType;
import com.restaurant.model.Order;
import com.restaurant.model.OrderType;
import com.restaurant.model.OrderStatus;
import com.restaurant.observer.CustomerNotifier;
import com.restaurant.observer.KitchenDisplay;
import java.util.Scanner;
public class ConsoleMenu {
    private final MealFactory factory=new MealFactory();
    private final Scanner scanner=new Scanner(System.in);
    private int nextOrderId=1;
    public void run(){
        OrderBuilder builder=new OrderBuilder(nextOrderId++);
        askOrderDetails(builder);
        MealType[] types=MealType.values();
        int mealCount=0;
        while(true){
            printMenu(types);
            int choice=readNumber("Your choice: ",0,types.length);
            if(choice==0){
                if(mealCount==0){
                    System.out.println("Add at least one meal first.");
                    continue;
                }
                break;
            }
            MealType type=types[choice-1];
            Meal meal=factory.createMeal(type);
            if(type.allowsToppings())meal=chooseToppings(meal);
            builder.addMeal(meal);
            mealCount++;
            System.out.println("Added: "+meal.getDescription());
        }
        Order order=builder.build();
        System.out.println();
        System.out.print(OrderPrinter.format(order));
        trackOrder(order);
    }
    private void trackOrder(Order order){
        order.addObserver(new KitchenDisplay(System.out::println));
        order.addObserver(new CustomerNotifier(System.out::println));
        while(order.getStatus()!=OrderStatus.DELIVERED){
            System.out.print("Press Enter to move the order to the next status...");
            scanner.nextLine();
            order.advanceStatus();
        }
    }
    private void askOrderDetails(OrderBuilder builder){
        int typeChoice=readNumber("Order type (1 - dine-in, 2 - takeaway): ",1,2);
        if(typeChoice==1){
            builder.type(OrderType.DINE_IN);
            builder.table(readNumber("Table number (1-99): ",1,99));
        }else{
            builder.type(OrderType.TAKEAWAY);
        }
        System.out.print("Comment for the kitchen (Enter to skip): ");
        builder.comment(scanner.nextLine());
        DiscountOption[] options=DiscountOption.values();
        System.out.println("Discounts:");
        for(int i=0; i<options.length; i++){
            System.out.printf("%d. %s%n",i+1,options[i].getTitle());
        }
        int discountChoice=readNumber("Choose discount: ",1,options.length);
        builder.discount(options[discountChoice - 1].createStrategy());
    }
    private void printMenu(MealType[] types){
        System.out.println();
        for(int i=0;i<types.length;i++){
            Meal meal=factory.createMeal(types[i]);
            System.out.printf("%d. %s - %.0f%n",i+1,meal.getDescription(),meal.getPrice());
        }
        System.out.println("0. Finish order");
    }
    private Meal chooseToppings(Meal meal){
        Topping[] toppings=Topping.values();
        while(true){
            System.out.println("Toppings:");
            for(int i=0;i < toppings.length;i++){
                System.out.printf("%d. %s (+%.0f)%n",i+1,toppings[i].getTitle(),toppings[i].getPrice());
            }
            System.out.println("0. No more toppings");
            int choice=readNumber("Add topping: ",0,toppings.length);
            if(choice==0){
                return meal;
            }
            meal=toppings[choice - 1].applyTo(meal);
        }
    }
    private int readNumber(String prompt,int min,int max){
        while(true){
            System.out.print(prompt);
            String line=scanner.nextLine().trim();
            if(line.matches("\\d{1,2}")){
                int number=Integer.parseInt(line);
                if(number>=min&&number<=max)return number;
            }
            System.out.println("Please enter a number from "+min+" to "+max);
        }
    }
}
