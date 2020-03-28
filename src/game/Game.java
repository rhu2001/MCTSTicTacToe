package game;

import java.util.Scanner;

import static game.Piece.*;

public class Game {

    public static void main(String[] args) {
        Board board = new Board();
        Scanner keyboard = new Scanner(System.in);
        Piece winner = board.winner();

        while (winner == null) {
            System.out.println(board);
            String move;
            do {
                System.out.print(board.turn() + "> ");
                move = keyboard.nextLine();
                if (move.equals("quit")) {
                    keyboard.close();
                    System.exit(0);
                }
            } while (!board.put(move));
            winner = board.winner();
        }
        System.out.println(board);
        if (winner == E) {
            System.out.println("It was a tie!");
        } else {
            System.out.println(winner + " won!");
        }
        keyboard.close();
    }
}
