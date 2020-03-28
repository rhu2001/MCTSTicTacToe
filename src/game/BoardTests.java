package game;

import org.junit.Test;

import static org.junit.Assert.*;

public class BoardTests {

    @Test
    public void stringTest() {
        Board b = new Board();
        System.out.println(b);
        b.put("a1");
        System.out.println(b);
        b.put("b2");
        System.out.println(b);
        b.put("b5");
        b.undo();
        System.out.println(b);
        b.put("c3");
        System.out.println(b);
    }
}
