package game.MCTS;

import game.Board;
import game.Piece;

/** Facilitates Monte Carlo tree search. This Monte Carlo tree search algorithm
 * relies on the TreeNode data structure, which is essentially a tree in which
 * each node represents a particular state of a game. A node is a child of another
 * node if and only if the game state stored in the child node can be achieved by
 * making one move on the parent node's game state.
 *
 * Initially, the tree is small, consisting only of a single node, and as the algorithm
 * runs, it grows in size as it explores possible game states. Note that a leaf node
 * does not necessarily entail the end of a game; it could simply be that the algorithm
 * has not explored any game states beyond that node.
 *
 * Each node stores information used by the algorithm.
 *     - the state of the game at that node
 *     - the side that is allowed to make a move at that state
 *     - the number of times the algorithm has visited the node
 *     - the number of times the algorithm has won after visiting the node
 *     - the parent of the node
 *     - the children of the node
 *
 * In this implementation of MCTS, other information is stored that is specific to this
 * implementation and could be handled in different ways in other implementations.
 *     - the move on the parent's state that resulted in the node (i.e. what
 *       move led to this node's state)
 *     - whether the node has been expanded (more on expansion later)
 *
 * Every time the computer needs to make a move the root of the tree moves to the node
 * that represents the current game state and the following four phases are repeated until
 * the runtime of the algorithm has exceeded a specified maximum.
 *
 * 1. Selection
 *     In the selection phase, the algorithm begins at the root node, and takes a path
 *     through successive child nodes until it reaches a leaf. The child node that is
 *     chosen is the child with the greatest Upper Confidence bounds applied to Trees (UCT)
 *     value, which is a metric that increases with the win rate of the child
 *     (exploitation) and decreases the more the child has been visited by the algorithm
 *     (exploration). This encourages the algorithm to not only pick children that win
 *     but also make sure that each child is well-represented.
 *
 * 2. Expansion
 *     In the expansion phase, the algorithm takes the leaf that was found in selection
 *     and adds to its children new nodes containing all possible game states that can
 *     be obtained by making one move on the former leaf's game state. The algorithm then
 *     returns a random child of the former leaf.
 *
 * 3. Simulation/Rollout
 *     In the simulation phase (also called the rollout phase), the algorithm takes the
 *     random child obtained from expansion and plays out its game state using completely
 *     random moves until the game ends. The algorithm then records the outcome of the
 *     random play (in this implementation, either a win, loss, or tie).
 *
 * 4. Backpropagation
 *     In the backpropagation phase, the algorithm begins at the node that rollout occurred
 *     at and increments its visit count. If the node's side is the opposite of the computer's
 *     side, then the node's win count is updated (in this implementation, the win count is
 *     incremented on a win or tie and is decremented on a loss). The reason that only opposite
 *     sides have their win counts updated is because when the algorithm is selecting a move,
 *     the move incurs a game state in which the opposite side is moving. The process of
 *     updating visit counts and win counts is repeated on the successive parents of the node
 *     until the root is reached.
 *
 * The 4 phases of the algorithm are repeated until the runtime has exceeded a limit. At that
 * point, the algorithm selects the move that will lead it to the child of the root with the
 * highest win count / times visited ratio.
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

    /** Back propagation phase of MCTS. Starting with a node, increment the number of
     * times it has been visited. If during rollout, the CPU side did not lose and this
     * node's side is the opposite of the CPU side, increment this node's win count.
     * Do the same for the node's parent and repeat until the root is reached.
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
