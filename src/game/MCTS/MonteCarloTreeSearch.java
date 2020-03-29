package game.MCTS;

import game.Board;

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
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < maxTimeMillis) {
            node = selection(searchTree.getRoot());
            node = expansion(node);
            incrementWin = rollout(node);
            backpropagation(node, incrementWin);
        }
        searchTree.getRoot().sortChildren();
        return searchTree.getRoot().move();
    }

    /** Selection phase of MCTS.
     *
     * @param node Node to select children from.
     * @return Child with best UCT value.
     * */
    Node selection(Node node) {
        if (node.isLeaf()) {
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
        return node.play();
    }

    /** Back propagation phase of MCTS.
     *
     * @param node Node to backpropagate.
     * @param incrementWin if true, increment each parent's win counter.
     * */
    void backpropagation(Node node, boolean incrementWin) {
        while (node != searchTree.getRoot()) {
            node.incrementVisited();
            if (incrementWin) {
                node.incrementWins();
            }
        }
    }

    /** The computer's search tree. */
    private Tree searchTree;
}
