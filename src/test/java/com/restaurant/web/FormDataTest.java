package com.restaurant.web;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
class FormDataTest{
    @Test
    void readsSingleValues(){
        FormData form=FormData.parse("type=TAKEAWAY&table=5");
        assertEquals("TAKEAWAY",form.get("type","DINE_IN"));
        assertEquals("5",form.get("table","0"));
    }
    @Test
    void missingKeyReturnsDefault(){
        FormData form=FormData.parse("type=TAKEAWAY");
        assertEquals("0",form.get("table","0"));
    }
    @Test
    void repeatedKeysKeepAllValuesInOrder(){
        FormData form=FormData.parse("meal=PIZZA&meal=DRINK");
        assertEquals(List.of("PIZZA","DRINK"),form.getAll("meal"));
    }
    @Test
    void decodesPlusAndPercentSequences(){
        FormData form=FormData.parse("comment=no+onions%2C+please&meal=PIZZA%3ABACON");
        assertEquals("no onions, please",form.get("comment",""));
        assertEquals("PIZZA:BACON",form.get("meal",""));
    }
    @Test
    void blankBodyGivesEmptyForm(){
        assertEquals(List.of(),FormData.parse("").getAll("meal"));
        assertEquals(List.of(),FormData.parse(null).getAll("meal"));
    }
}
