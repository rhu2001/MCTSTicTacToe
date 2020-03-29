package game.MCTS;

import game.Board;
import game.Piece;

import java.util.ArrayList;
import java.util.Collections;
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
class Node implements Comparable<Node> {

    /** The computer's side, either X or O. */
    public static Piece SIDE;
    /** Square root of 2. */
    public static final double ROOT2 = Math.sqrt(2);

    /** Sets this node's state to the board and sets up RNG according
     * to the seed.
     *
     * @param board State of this node.
     * @param parent Parent of this node.
     * @param move Move that resulted in this node's state.
     * @param seed Random seed.
     * */
    public Node(Board board, Node parent, String move, long seed) {
        _state = board;
        _parent = parent;
        _children = new ArrayList<>();
        _achievingMove = move;
        _timesVisited = 0;
        _timesWon = 0;
        setUpRNG(seed);
    }

    /** Sets this node's state to the board and sets up RNG according
     * to the seed.
     *
     * @param board State of this node.
     * @param parent Parent of this node.
     * @param move Move that resulted in this node's state.
     * */
    public Node(Board board, Node parent, String move) {
        _state = board;
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
        _children.add(node);
    }

    /** Whether this node is a leaf.
     *
     * @return True iff this node is a leaf.
     * */
    boolean isLeaf() {
        return _children.size() == 0;
    }

    /** Whether this node is terminal.
     *
     * @return True iff no more moves can be made from this state or this state is winner.
     * */
    boolean isTerminal() {
        return _state.winner() != null;
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
            return Double.POSITIVE_INFINITY;
        }
        return ((double) _timesWon) / ((double) _timesVisited);
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

    /** Sorts children according to uct() */
    void sortChildren() {
        _children.sort(Collections.reverseOrder());
    }

    /** Gets this node's first child.
     *
     * @return First child.
     * */
    Node firstChild() {
        return _children.get(0);
    }

    /** Gets the move that incurred this state.
     *
     * @return _achievingMove.
     * */
    String move() {
        return _achievingMove;
    }

    /** Sets up randomness.
     *
     * @param seed Seed.
     * */
    void setUpRNG(long seed) {
        _rng = new Random(seed);
    }

    /** Sets up randomness. */
    void setUpRNG() {
        _rng = new Random();
    }

    /** Plays random moves until the game ends.
     *
     * @return True iff won.
     * */
    boolean play() {
        Piece winner = _state.winner();
        int movesMade = 0;
        while (winner == null) {
            int move = _rng.nextInt(_state.emptyPlaces().size());
            _state.put(_state.emptyPlaces().get(move));
            movesMade++;
            winner = _state.winner();
        }
        while (movesMade > 0) {
            _state.undo();
            movesMade--;
        }
        return winner == Node.SIDE;
    }

    /** Makes a random move on the board. */
    Board putRandom() {
        int move = _rng.nextInt(_state.emptyPlaces().size());
        Board nextState = new Board(_state);
        nextState.put(_state.emptyPlaces().get(move));
        return nextState;
    }

    /** Increments the number of times this node has been visited. */
    void incrementVisited() {
        _timesVisited++;
    }

    /** Increments the number of times that a win has been achieved
     * from this state. */
    void incrementWins() {
        _timesWon++;
    }

    @Override
    public int compareTo(Node other) {
        return Double.compare(uct(), other.uct());
    }

    /** State of current board. */
    Board _state;
    /** This node's parent. */
    Node _parent;
    /** The move that got to this node. */
    String _achievingMove;
    /** This node's children. */
    List<Node> _children;
    /** The number of times this node has been visited. */
    int _timesVisited;
    /** The number of times that a simulation passing through this node has won. */
    int _timesWon;
    /** Random number generator. */
    Random _rng;
}
