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

    Node selection(Node node) {
        if (node.isLeaf()) {
            return node;
        } else {
            node.sortChildren();
            return selection(node.firstChild());
        }
    }

    Node expansion(Node node) {
        Node newChild = new Node(node.putRandom(), node, null);
        node.addChild(newChild);
        return newChild;
    }

    boolean rollout(Node node) {
        return node.play();
    }

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
