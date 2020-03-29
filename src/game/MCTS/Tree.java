package game.MCTS;

import game.Board;
import game.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Tree data structure for Monte Carlo tree search.
 *
 * @author Richard Hu
 * */
public class Tree {

    /** Tree with given root.
     *
     * @param root Root of this tree.
     * */
    public Tree(Node root) {
        _root = root;
    }

    /** Gets the root of this tree.
     *
     * @return _root.
     * */
    public Node getRoot() {
        return _root;
    }

    /** Sets the root of this tree to specified node.
     *
     * @param node Node to set to root.
     * */
    void setRoot(Node node) {
        _root = node;
    }

    /** Root of this tree. */
    private Node _root;
}

/** A single node of a tree.
 *
 * @author Richard Hu
 * */
class Node {

    /** Square root of 2. */
    public static final double ROOT2 = Math.sqrt(2);

    /** Sets this node's state to the board and sets up RNG according
     * to the seed.
     *
     * @param board State of this node.
     * @param parent Parent of this node.
     * @param move Move that resulted in this node's state.
     * */
    public Node(Board board, Node parent, String move) {
        _state = board;
        _side = _state.turn();
        _parent = parent;
        _children = new ArrayList<>();
        _achievingMove = move;
        _timesVisited = 0;
        _timesWon = 0;
        setUpRNG();
    }

    /** Adds a child to this node.
     *
     * @param node Child to add.
     * */
    void addChild(Node node) {
        node._parent = this;
        _children.add(node);
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

    /** Sets up randomness. */
    void setUpRNG() {
        _rng = new Random();
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

    /** Makes a random move on the board. */
    Board putRandom() {
        Board nextState = new Board(_state);
        int move = _rng.nextInt(nextState.emptyPlaces().size());
        nextState.put(nextState.emptyPlaces().get(move));
        return nextState;
    }

    /** Increments the number of times this node has been visited. */
    void incrementVisited() {
        _timesVisited += 1.0;
    }

    /** Increments the number of times that a win has been achieved
     * from this state. */
    void incrementWins() {
        _timesWon += 1.0;
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
    Node _parent;
    /** The move that got to this node. */
    String _achievingMove;
    /** This node's children. */
    List<Node> _children;
    /** The number of times this node has been visited. */
    double _timesVisited;
    /** The number of times that a simulation passing through this node has won. */
    double _timesWon;
    /** Random number generator. */
    Random _rng;
}
