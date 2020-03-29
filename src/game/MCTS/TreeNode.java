package game.MCTS;

import game.Board;
import game.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Tree data structure for MCTS.
 *
 * @author Richard Hu
 * */
public class TreeNode {

    /** Square root of 2. */
    public static final double ROOT2 = Math.sqrt(2);

    /** Sets this node's state to the board and sets up RNG according
     * to the seed.
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
        setUpRNG();
    }

    /** Expands this node. */
    void expand() {
        Board temp;
        for (String move : _state.emptyPlaces()) {
            temp = new Board(_state);
            temp.put(move);
            _children.add(new TreeNode(temp, this, move));
        }
    }

    /** Picks out a random child.
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

    /** Average value of this node.
     *
     * @return This node's average value.
     * */
    double score() {
        if (_timesVisited == 0) {
            return Double.NEGATIVE_INFINITY;
        }
        return _timesWon / _timesVisited;
    }

    /** UCT value of this node.
     *
     * @return UCT value of this node.
     * */
    double uct() {
        if (_timesVisited == 0) {
            return Double.POSITIVE_INFINITY;
        }
        return score() + ROOT2 * Math.sqrt(Math.log(_parent._timesVisited) / _timesVisited);
    }

    /** Plays random moves until the game ends.
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

    /** Increments the number of times this node has been visited. */
    void incrementVisited() {
        _timesVisited += 1.0;
    }

    /** Increments the number of times that a win has been achieved
     * from this state. */
    void incrementWins(double amt) {
        _timesWon += amt;
    }

    /** Sets up randomness. */
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
    /** The move that got to this node. */
    String _achievingMove;
    /** This node's children. */
    List<TreeNode> _children;
    /** The number of times this node has been visited. */
    double _timesVisited;
    /** The number of times that a simulation passing through this node has won. */
    double _timesWon;
    /** Random number generator. */
    Random _rng;
}
