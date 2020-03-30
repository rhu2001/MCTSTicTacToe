package game.MCTS;

import game.Board;
import game.Piece;

import static game.Piece.*;

/** Facilitates Monte Carlo tree search.
 *
 * @author Richard Hu
 * */
public class MonteCarloTreeSearch {

    /** The computer's's side. */
    public static Piece SIDE;

    /** True iff MCTS has not been set up. */
    public static boolean REQUIRES_SETUP = true;

    /** The computer's's search tree. */
    private static TreeNode ROOT;

    /** Set up MCTS.
     *
     * @param firstBoard The initial board.
     * */
    public static void setUp(Board firstBoard) {
        Board state = new Board(firstBoard);
        ROOT = new TreeNode(state, null, null);
        REQUIRES_SETUP = false;
    }

    /** Find the best move on the current board state.
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

    /** Selection phase of MCTS. At each node, choose the child with the
     * highest UCT value until a leaf is reached
     *
     * @param treeNode Node to select children from.
     * @return Leaf node with highest UCT.
     * */
    static TreeNode selection(TreeNode treeNode) {
        while (!treeNode.isLeaf()) {
            treeNode = treeNode.highestUCTChild();
        }
        return treeNode;
    }

    /** Expansion phase of MCTS. Add all possible game states that are one
     * move forward from a node to that node's children. Then, return a random
     * child.
     *
     * @param treeNode Node to expand.
     * @return Random child.
     * */
    static TreeNode expansion(TreeNode treeNode) {
        treeNode.expand();
        return treeNode.randomChild();
    }

    /** Rollout/Simulation phase of MCTS. Start with the game state of a node and
     * play random moves until the game ends. Return the winner of the result of
     * this random play.
     *
     * @param treeNode Node to rollout.
     * @return Winning side of rollout.
     * */
    static Piece rollout(TreeNode treeNode) {
        return treeNode.play();
    }

    /** Back propagation phase of MCTS. Starting with a node, increment the number
     * of times it has been visited. If during rollout, the computer's side won
     * and this node's side is the opposite of the computer's side, increment this
     * node's win count. If the computer's tied during rollout, increment the win
     * count by 0.5. Do the same for the node's parent and repeat until the root is
     * reached.
     *
     * @param treeNode Node to back propagate.
     * @param winningSide Side that won on rollout.
     * */
    static void backPropagation(TreeNode treeNode, Piece winningSide) {
        while (treeNode != null) {
            treeNode.incrementVisited();
            if (treeNode._side != SIDE) {
                if (winningSide == SIDE) {
                    treeNode.incrementWins(1.0);
                } else if (winningSide == E) {
                    treeNode.incrementWins(0.5);
                }
            }
            treeNode = treeNode._parent;
        }
    }
}
