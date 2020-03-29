package game.MCTS;

import game.Board;

/** Facilitates Monte Carlo tree search.
 *
 * @author Richard Hu
 * */
public class MonteCarloTreeSearch {

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
        boolean incrementWin;
        long start = System.currentTimeMillis(), currTime = System.currentTimeMillis();
        while (currTime < maxTimeMillis) {
            node = selection(searchTree.getRoot());
            node = expansion(node);
            incrementWin = rollout(node);
            backpropagation(node, incrementWin);
            currTime += System.currentTimeMillis() - start;
        }
        double bestScore = Double.NEGATIVE_INFINITY;
        String bestMove = "";
        for (Node child : searchTree.getRoot().children()) {
            if (child.score() > bestScore) {
                bestMove = child.move();
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
        if (node.isLeaf()) {
            //System.out.println("selection");
            return node;
        } else {
            node.sortChildren();
            return selection(node.firstChild());
        }
    }

    /** Expansion phase of MCTS.
     *
     * @param node Node to expand.
     * @return Newly added child node.
     * */
    Node expansion(Node node) {
        //System.out.println("expansion");
        //System.out.println(node._state);
        if (node.isTerminal()) {
            return node;
        }
        Node newChild = new Node(node.putRandom(), node, null);
        node.addChild(newChild);
        return newChild;
    }

    /** Rollout/Simulation phase of MCTS.
     *
     * @param node Node to rollout.
     * @return True iff rollout resulted in a victory.
     * */
    boolean rollout(Node node) {
        //System.out.println("rollout");
        return node.play();
    }

    /** Back propagation phase of MCTS.
     *
     * @param node Node to backpropagate.
     * @param incrementWin if true, increment each parent's win counter.
     * */
    void backpropagation(Node node, boolean incrementWin) {
        //System.out.println("backprop");
        while (node != searchTree.getRoot()) {
            node.incrementVisited();
            if (incrementWin) {
                node.incrementWins();
            }
            node = node.parent();
        }
    }

    /** The computer's search tree. */
    private Tree searchTree;
}
