package game;

import game.MCTS.MonteCarloTreeSearch;

import java.util.Scanner;

import static game.Piece.*;

public class Game {

    public static void main(String[] args) {
        Board board = new Board();
        Scanner keyboard = new Scanner(System.in);
        Piece winner = board.winner();

        boolean validCPU = false;

        while (!validCPU) {
            System.out.println("Should the CPU play as X or O?");
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

        while (winner == null) {
            System.out.println(board);
            String move;
            if (board.turn() == MonteCarloTreeSearch.SIDE) {
                move = MonteCarloTreeSearch.findMove(board, 10000);
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
                System.out.println(MonteCarloTreeSearch.SIDE.opposite() + " to " + move);
            }
            winner = board.winner();
        }
        if (winner == E) {
            System.out.println("It was a tie!");
        } else {
            System.out.println(winner + " won!");
        }
        keyboard.close();
    }
}
