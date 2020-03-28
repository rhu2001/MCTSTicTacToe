package game.MCTS;

import game.Board;

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

    /** Root of this tree. */
    Node _root;
}

/** A single node of a tree.
 *
 * @author Richard Hu
 * */
class Node {

    /** Sets this node's state to the board and sets up RNG according
     * to the seed.
     *
     * @param board State of this node.
     * @param seed Random seed.
     * */
    public Node(Board board, long seed) {
        _state = new Board(board);
        _parent = null;
        _children = new ArrayList<>();
        _timesVisited = 0;
        setUpRNG(seed);
    }

    /** Sets this node's state to the board and sets up RNG without
     * specified seed.
     *
     * @param board State of this node.
     * */
    public Node(Board board) {
        _state = new Board(board);
        _parent = null;
        _children = new ArrayList<>();
        _timesVisited = 0;
        setUpRNG();
    }

    /** Sets this node's state to the board, sets up RNG according
     * to the seed, and sets parent.
     *
     * @param board State of this node.
     * @param seed Random seed.
     * @param parent This node's parent.
     * */
    public Node(Board board, long seed, Node parent) {
        this(board, seed);
        _parent = parent;
    }

    /** Sets this node's state to the board and sets parent.
     *
     * @param board State of this node.
     * @param parent This node's parent.
     * */
    public Node(Board board, Node parent) {
        this(board);
        _parent = parent;
    }

    boolean isLeaf() {
        return _children.size() == 0;
    }

    void setUpRNG(long seed) {
        _rng = new Random(seed);
    }

    void setUpRNG() {
        _rng = new Random();
    }

    boolean putRandom() {
        int move = _rng.nextInt(_state.emptyPlaces().size());
        _state.put(_state.emptyPlaces().get(move));
        return true;
    }

    Board _state;
    Node _parent;
    List<Node> _children;
    int _timesVisited;
    Random _rng;
}
