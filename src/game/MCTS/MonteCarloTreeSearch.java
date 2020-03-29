package game.MCTS;

import game.Board;
import game.Piece;

import java.util.Collections;
import java.util.Comparator;

/** Facilitates Monte Carlo tree search.
 *
 * @author Richard Hu
 * */
public class MonteCarloTreeSearch {

    /** The CPU's side. */
    public static Piece SIDE;

    /** True iff MCTS has not been set up. */
    public static boolean REQUIRES_SETUP = true;

    /** The CPU's search tree. */
    private static TreeNode ROOT;

    /** Sets up MCTS to its first board.
     *
     * @param firstBoard The initial board.
     * */
    public static void setUp(Board firstBoard) {
        Board state = new Board(firstBoard);
        ROOT = new TreeNode(state, null, null);
        REQUIRES_SETUP = false;
    }

    /** Finds the best move on the current board state.
     *
     * @param playerMove Move that led to the current state.
     * @param maxTimeMillis Maximum allowed run time.
     * @return Best move found.
     * */
    public static String findMove(String playerMove, long maxTimeMillis) {
        if (playerMove != null) {
            for (TreeNode child : ROOT._children) {
                if (playerMove.equals(child._achievingMove)) {
                    ROOT = child;
                    break;
                }
            }
        }

        TreeNode node;
        Piece winningSide;
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < maxTimeMillis) {
            node = selection(ROOT);
            if (node.winner() == null) {
                node = expansion(node);
            }
            winningSide = rollout(node);
            backPropagation(node, winningSide);
        }

        double bestScore = Double.NEGATIVE_INFINITY;
        TreeNode bestChild = null;
        String bestMove = "";
        for (TreeNode child : ROOT._children) {
            if (child.score() > bestScore) {
                bestScore = child.score();
                bestChild = child;
                bestMove = child._achievingMove;
            }
        }
        ROOT = bestChild;
        return bestMove;
    }

    /** Selection phase of MCTS.
     *
     * @param treeNode Node to select children from.
     * @return Child with best UCT value.
     * */
    static TreeNode selection(TreeNode treeNode) {
        while (!treeNode.isLeaf()) {
            treeNode = Collections.max(treeNode._children, Comparator.comparing(TreeNode::uct));
        }
        return treeNode;
    }

    /** Expansion phase of MCTS.
     *
     * @param treeNode Node to expand.
     * @return Newly added child node.
     * */
    static TreeNode expansion(TreeNode treeNode) {
        treeNode.expand();
        return treeNode.randomChild();
    }

    /** Rollout/Simulation phase of MCTS.
     *
     * @param treeNode Node to rollout.
     * @return Winning side of rollout.
     * */
    static Piece rollout(TreeNode treeNode) {
        return treeNode.play();
    }

    /** Back propagation phase of MCTS.
     *
     * @param treeNode Node to back propagate.
     * @param winningSide Side that won on rollout.
     * */
    static void backPropagation(TreeNode treeNode, Piece winningSide) {
        while (treeNode != null) {
            treeNode.incrementVisited();
            if (treeNode._side != SIDE) {
                if (winningSide.opposite() != SIDE) {
                    treeNode.incrementWins(1.0);
                } else {
                    treeNode.incrementWins(-1.0);
                }
            }
            treeNode = treeNode._parent;
        }
    }
}
