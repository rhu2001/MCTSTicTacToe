package game.MCTS;

import game.Board;
import game.Piece;

import java.util.Collections;
import java.util.Comparator;

import static game.Piece.*;

/** Facilitates Monte Carlo tree search.
 *
 * @author Richard Hu
 * */
public class MonteCarloTreeSearch {

    /** The CPU's side. */
    public static Piece SIDE;

    /** Sets up Monte Carlo tree search on given board.
     *
     * @param board Initial board state.
     * */
    public MonteCarloTreeSearch(Board board) {
        Board state = new Board(board);
        searchTree = new Tree(new Node(state, null, null));

        for (String place : board.emptyPlaces()) {
            state = new Board(board);
            state.put(place);
            searchTree.getRoot().addChild(new Node(state, searchTree.getRoot(), place));
        }
    }

    /** Finds the best move on the current board state.
     *
     * @param maxTimeMillis Maximum allowed run time.
     * @return Best move found.
     * */
    public String findMove(long maxTimeMillis) {
        Node node;
        Piece winningSide;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < maxTimeMillis) {
            node = selection(searchTree.getRoot());
            if (node.winner() == null) {
                node = expansion(node);
            }
            winningSide = rollout(node);
            backpropagation(node, winningSide);
        }

        double bestScore = Double.NEGATIVE_INFINITY;
        String bestMove = "";
        for (Node child : searchTree.getRoot()._children) {
            if (child.score() > bestScore) {
                bestScore = child.score();
                bestMove = child._achievingMove;
            }
        }
        return bestMove;
    }

    /** Selection phase of MCTS.
     *
     * @param node Node to select children from.
     * @return Child with best UCT value.
     * */
    Node selection(Node node) {
        while (!node.isLeaf()) {
            node = Collections.max(node._children, Comparator.comparing(Node::uct));
        }
        System.out.println(node);
        return node;
    }

    /** Expansion phase of MCTS.
     *
     * @param node Node to expand.
     * @return Newly added child node.
     * */
    Node expansion(Node node) {
        Node newChild = new Node(node.putRandom(), node, null);
        node.addChild(newChild);
        return newChild;
    }

    /** Rollout/Simulation phase of MCTS.
     *
     * @param node Node to rollout.
     * @return Winning side of rollout.
     * */
    Piece rollout(Node node) {
        return node.play();
    }

    /** Back propagation phase of MCTS.
     *
     * @param node Node to backpropagate.
     * @param winningSide Side that won on rollout.
     * */
    void backpropagation(Node node, Piece winningSide) {
        while (node != null) {
            node.incrementVisited();
            if (winningSide.opposite() == node._side) {
                node.incrementWins();
            }
            node = node._parent;
        }
    }

    /** The computer's search tree. */
    private Tree searchTree;
}
