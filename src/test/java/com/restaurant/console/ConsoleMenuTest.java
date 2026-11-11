package com.restaurant.console;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
class ConsoleMenuTest {
    private String run(String input){
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        PrintStream out=new PrintStream(bytes,true,StandardCharsets.UTF_8);
        ConsoleMenu menu=new ConsoleMenu(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),out);
        menu.run();
        return bytes.toString(StandardCharsets.UTF_8);
    }
    @Test
    void dineInOrderWithToppingAndDiscountIsPlacedAndDelivered(){
        String output=run("1\n3\nno onions\n2\n1\n1\n0\n4\n0\n\n\n\n");
        assertTrue(output.contains("Order #1 [NEW] dine-in, table 3"));
        assertTrue(output.contains("Comment: no onions"));
        assertTrue(output.contains("Margherita pizza + extra cheese"));
        assertTrue(output.contains("Subtotal: 4500"));
        assertTrue(output.contains("Discount (Student discount 10%): -450"));
        assertTrue(output.contains("Total: 4050"));
        assertTrue(output.contains("Kitchen: order #1 NEW -> COOKING (note: no onions)"));
        assertTrue(output.contains("Customer: order #1 delivered, enjoy your meal"));
    }
    @Test
    void takeawayOrderDoesNotAskForTable(){
        String output=run("2\n\n1\n4\n0\n\n\n\n");
        assertFalse(output.contains("Table number"));
        assertTrue(output.contains("Order #1 [NEW] takeaway"));
    }
    @Test
    void invalidNumbersAreAskedAgain(){
        String output=run("abc\n7\n2\n\n1\n4\n0\n\n\n\n");
        assertTrue(output.contains("Please enter a number from 1 to 2"));
        assertTrue(output.contains("Order #1 [NEW] takeaway"));
    }
    @Test
    void emptyOrderCannotBeFinished(){
        String output=run("2\n\n1\n0\n4\n0\n\n\n\n");
        assertTrue(output.contains("Add at least one meal first."));
        assertTrue(output.contains("Cola 0.5 l"));
    }
    @Test
    void closedInputEndsWithException(){
        assertThrows(NoSuchElementException.class,()->run("1\n"));
    }
}
