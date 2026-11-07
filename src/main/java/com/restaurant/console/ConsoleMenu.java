package com.restaurant.console;
import com.restaurant.decorator.Topping;
import com.restaurant.discount.DiscountOption;
import com.restaurant.facade.RestaurantFacade;
import com.restaurant.model.Meal;
import com.restaurant.model.MealType;
import com.restaurant.model.Order;
import com.restaurant.model.OrderStatus;
import com.restaurant.model.OrderType;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class ConsoleMenu {
    private final Scanner scanner;
    private final PrintStream out;
    private final RestaurantFacade facade;
    public ConsoleMenu(){
        this(System.in,System.out);
    }
    public ConsoleMenu(InputStream in,PrintStream out){
        this.scanner=new Scanner(in);
        this.out=out;
        this.facade=new RestaurantFacade(out::println);
    }
    public void run(){
        OrderType type=readOrderType();
        int table=type==OrderType.DINE_IN?readNumber("Table number (1-99): ",1,99):0;
        out.print("Comment for the kitchen (Enter to skip): ");
        String comment=scanner.nextLine();
        DiscountOption discount=chooseDiscount();
        List<Meal> meals=chooseMeals();
        Order order=facade.placeOrder(type,table,comment,discount,meals);
        out.println();
        out.print(OrderPrinter.format(order));
        trackOrder(order);
    }
    private OrderType readOrderType(){
        int choice=readNumber("Order type (1 - dine-in, 2 - takeaway): ",1,2);
        return choice==1?OrderType.DINE_IN:OrderType.TAKEAWAY;
    }
    private DiscountOption chooseDiscount(){
        DiscountOption[] options=DiscountOption.values();
        out.println("Discounts:");
        for(int i=0;i<options.length;i++){
            out.printf("%d. %s%n",i+1,options[i].getTitle());
        }
        return options[readNumber("Choose discount: ",1,options.length)-1];
    }
    private List<Meal> chooseMeals(){
        MealType[] types=MealType.values();
        List<Meal> meals=new ArrayList<>();
        while(true){
            printMenu(types);
            int choice=readNumber("Your choice: ",0,types.length);
            if(choice==0){
                if(meals.isEmpty()){
                    out.println("Add at least one meal first.");
                    continue;
                }
                return meals;
            }
            MealType type=types[choice-1];
            List<Topping> toppings=type.allowsToppings()?chooseToppings():List.of();
            Meal meal=facade.createMeal(type,toppings);
            meals.add(meal);
            out.println("Added: "+meal.getDescription());
        }
    }
    private void printMenu(MealType[] types){
        out.println();
        for(int i=0;i<types.length;i++){
            Meal meal=facade.createMeal(types[i]);
            out.printf("%d. %s - %.0f%n",i+1,meal.getDescription(),meal.getPrice());
        }
        out.println("0. Finish order");
    }
    private List<Topping> chooseToppings(){
        Topping[] available=Topping.values();
        List<Topping> chosen=new ArrayList<>();
        while(true){
            out.println("Toppings:");
            for(int i=0;i<available.length;i++){
                out.printf("%d. %s (+%.0f)%n",i+1,available[i].getTitle(),available[i].getPrice());
            }
            out.println("0. No more toppings");
            int choice=readNumber("Add topping: ",0,available.length);
            if(choice==0){
                return chosen;
            }
            chosen.add(available[choice-1]);
        }
    }
    private void trackOrder(Order order){
        while(order.getStatus()!=OrderStatus.DELIVERED){
            out.print("Press Enter to move the order to the next status...");
            scanner.nextLine();
            facade.advanceOrder(order.getId());
        }
    }
    private int readNumber(String prompt,int min,int max){
        while(true){
            out.print(prompt);
            String line=scanner.nextLine().trim();
            if(line.matches("\\d{1,2}")){
                int number=Integer.parseInt(line);
                if(number>=min&&number<=max){
                    return number;
                }
            }
            out.println("Please enter a number from "+min+" to "+max);
        }
    }
}
