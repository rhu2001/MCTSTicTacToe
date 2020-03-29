package game.MCTS;

import game.Board;
import game.Piece;
import java.util.*;

/** Tree data structure for MCTS.
 *
 * @author Richard Hu
 * */
public class TreeNode {

    /** Square root of 2. */
    public static final double ROOT2 = Math.sqrt(2);

    /** Set this node's state to the board.
     *
     * @param board State of this node.
     * @param parent Parent of this node.
     * @param move Move that resulted in this node's state.
     * */
    public TreeNode(Board board, TreeNode parent, String move) {
        _state = board;
        _side = _state.turn();
        _parent = parent;
        _children = new ArrayList<>();
        _achievingMove = move;
        _timesVisited = 0;
        _timesWon = 0;
        _fullyExpanded = false;
        setUpRNG();
    }

    /** Expand this node by adding to its children all nodes with game states
     * one move forward of this node's state. */
    void expand() {
        if (!_fullyExpanded) {
            Board temp;
            for (String move : _state.emptyPlaces()) {
                temp = new Board(_state);
                temp.put(move);
                _children.add(new TreeNode(temp, this, move));
            }
            _fullyExpanded = true;
        }
    }

    /** Return the child of this node with highest UCT value.
     *
     * @return child with highest UCT.
     * */
    TreeNode highestUCTChild() {
        return Collections.max(_children, Comparator.comparing(TreeNode::uct));
    }

    /** Return a random child.
     *
     * @return Random element from _children.
     * */
    TreeNode randomChild() {
        int index = _rng.nextInt(_children.size());
        return _children.get(index);
    }

    /** Whether this node is a leaf.
     *
     * @return True iff this node is a leaf.
     * */
    boolean isLeaf() {
        return _children.size() == 0;
    }

    /** This state's winner.
     *
     * @return Winner.
     * */
    Piece winner() {
        return _state.winner();
    }

    /** Exploitation term of this node, determined by number of times won
     * divided by number of times visited.
     *
     * @return This node's average value.
     * */
    double score() {
        if (_timesVisited == 0) {
            return Double.NEGATIVE_INFINITY;
        }
        return _timesWon / _timesVisited;
    }

    /** Upper Confidence bounds applied to Trees (UCT) value of this node.
     * Value increases when this node is less visited or when this node tends
     * to result in more victories.
     *
     * @return UCT value of this node.
     * */
    double uct() {
        if (_timesVisited == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return score() + ROOT2 * Math.sqrt(Math.log(_parent._timesVisited) / _timesVisited);
    }

    /** Return the winner of the game starting at this node's game state and
     * playing random moves until the game ends.
     *
     * @return Winning side.
     * */
    Piece play() {
        Piece winner = _state.winner();
        Board temp = new Board(_state);
        while (winner == null) {
            int move = _rng.nextInt(temp.emptyPlaces().size());
            temp.put(temp.emptyPlaces().get(move));
            winner = temp.winner();
        }
        return winner;
    }

    /** Increment the number of times this node has been visited. */
    void incrementVisited() {
        _timesVisited += 1.0;
    }

    /** Increment the number of times that a win has been achieved
     * from this state. */
    void incrementWins(double amt) {
        _timesWon += amt;
    }

    /** Set up randomness. */
    void setUpRNG() {
        _rng = new Random();
    }

    @Override
    public String toString() {
        return _achievingMove + " : " + uct() + " : " + score();
    }

    /** State of current board. */
    Board _state;
    /** This node's side. */
    Piece _side;
    /** This node's parent. */
    TreeNode _parent;
    /** The move that resulted in this node's state. */
    String _achievingMove;
    /** This node's children. */
    List<TreeNode> _children;
    /** The number of times this node has been visited. */
    double _timesVisited;
    /** The number of times that a simulation passing through this node has won. */
    double _timesWon;
    /** True iff this node has been expanded. */
    private boolean _fullyExpanded;
    /** Random number generator. */
    Random _rng;
}
