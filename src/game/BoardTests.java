package game;

import org.junit.Test;
import static org.junit.Assert.*;
import static game.Piece.*;

import java.util.ArrayList;
import java.util.List;

/** Tests for the functionality of Board class.
 *
 * @author Richard Hu
 * */
public class BoardTests {

    @Test
    public void copyTest() {
        Board b = new Board();
        Board b1 = new Board(b);

        assertEquals(b1, b);

        b.put("b2");
        assertNotEquals(b1, b);

        b.undo();
        assertEquals(b1, b);
    }

    @Test
    public void movesListTest() {
        Board b = new Board();

        b.put("a1");
        b.put("a2");
        b.put("a3");
        b.put("b1");

        List<String> moves = new ArrayList<>();
        moves.add("a1");
        moves.add("a2");
        moves.add("a3");
        moves.add("b1");

        assertEquals(moves, b.moves());

        b.undo();
        moves.remove(3);
        assertEquals(moves, b.moves());

        Board b1 = new Board(b);
        assertEquals(b.moves(), b1.moves());

        b1.put("b2");
        assertNotEquals(b.moves(), b1.moves());
    }

    @Test
    public void emptyPlacesTest() {
        Board b = new Board();

        List<String> emptyPlaces = new ArrayList<>();
        emptyPlaces.add("a1");
        emptyPlaces.add("a2");
        emptyPlaces.add("a3");
        emptyPlaces.add("b1");
        emptyPlaces.add("b2");
        emptyPlaces.add("b3");
        emptyPlaces.add("c1");
        emptyPlaces.add("c2");
        emptyPlaces.add("c3");

        assertEquals(emptyPlaces, b.emptyPlaces());

        b.put("c3");
        b.put("c2");
        b.put("c1");
        emptyPlaces.remove(8);
        emptyPlaces.remove(7);
        emptyPlaces.remove(6);
        assertEquals(emptyPlaces, b.emptyPlaces());

        Board b1 = new Board(b);
        assertEquals(b.emptyPlaces(), b1.emptyPlaces());

        b1.put("b3");
        b1.put("b2");
        assertNotEquals(b.emptyPlaces(), b1.emptyPlaces());
    }

    @Test
    public void winnerTests() {
        Piece[][] config1 = new Piece[][] {
                {O, O, O},
                {E, E, E},
                {E, E, E}
        };
        Piece[][] config2 = new Piece[][] {
                {E, E, E},
                {O, O, O},
                {E, E, E}
        };
        Piece[][] config3 = new Piece[][] {
                {E, E, E},
                {E, E, E},
                {O, O, O}
        };
        Piece[][] config4 = new Piece[][] {
                {O, E, E},
                {O, E, E},
                {O, E, E}
        };
        Piece[][] config5 = new Piece[][] {
                {E, O, E},
                {E, O, E},
                {E, O, E}
        };
        Piece[][] config6 = new Piece[][] {
                {E, E, O},
                {E, E, O},
                {E, E, O}
        };
        Piece[][] config7 = new Piece[][] {
                {O, E, E},
                {E, O, E},
                {E, E, O}
        };
        Piece[][] config8 = new Piece[][] {
                {E, E, O},
                {E, O, E},
                {O, E, E}
        };

        assertEquals(O, new Board(config1, X).winner());
        assertEquals(O, new Board(config2, X).winner());
        assertEquals(O, new Board(config3, X).winner());
        assertEquals(O, new Board(config4, X).winner());
        assertEquals(O, new Board(config5, X).winner());
        assertEquals(O, new Board(config6, X).winner());
        assertEquals(O, new Board(config7, X).winner());
        assertEquals(O, new Board(config8, X).winner());
    }
}
