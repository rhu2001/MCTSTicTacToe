package game;

import game.MCTS.MonteCarloTreeSearch;
import java.util.Scanner;

import static game.Piece.*;

/** Main class that handles gameplay.
 *
 * @author Richard Hu
 * */
public class Game {

    /** Amount of time the MCTS is allowed to take. */
    static final long MAX_TIME = 1000;

    public static void main(String[] args) {
        Board board = new Board();
        Scanner keyboard = new Scanner(System.in);
        Piece winner = board.winner();

        boolean validCPU = false;

        while (!validCPU) {
            System.out.println("Should the CPU play as X or O? (X goes first)");
            System.out.print("> ");
            String cpu = keyboard.nextLine();
            if (cpu.equalsIgnoreCase("X")) {
                MonteCarloTreeSearch.SIDE = X;
                validCPU = true;
            } else if (cpu.equalsIgnoreCase("O")) {
                MonteCarloTreeSearch.SIDE = O;
                validCPU = true;
            } else {
                System.out.println("Invalid side. Must be 'X' or 'O'.");
            }
        }

        String playerMove = null;

        while (winner == null) {
            System.out.println(board);
            String move;
            if (board.turn() == MonteCarloTreeSearch.SIDE) {
                if (MonteCarloTreeSearch.REQUIRES_SETUP) {
                    MonteCarloTreeSearch.setUp(board);
                }
                move = MonteCarloTreeSearch.findMove(playerMove, MAX_TIME);
                board.put(move);
                System.out.println(MonteCarloTreeSearch.SIDE + " to " + move);
            } else {
                do {
                    System.out.print(board.turn() + "> ");
                    move = keyboard.nextLine();
                    if (move.equals("quit")) {
                        keyboard.close();
                        System.exit(0);
                    }
                } while (!board.put(move));
                playerMove = move;
                System.out.println(MonteCarloTreeSearch.SIDE.opposite() + " to " + move);
            }
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
