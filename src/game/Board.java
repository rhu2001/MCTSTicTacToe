package game;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static game.Piece.*;

/** Represents the Tic Tac Toe Board.
 *
 * @author Richard Hu
 * */
public class Board {

    /** The total number of possible rows or columns. */
    static final int BOARD_SIZE = 3;

    /** The regular expression for a square designation (e.g., a3). */
    static final Pattern SQ = Pattern.compile("([a-c][1-3])");

    /** Initializes board with all empty spaces. */
    public Board() {
        reset();
    }

    /** Initializes board by copying from another.
     *
     * @param board Board to copy from.
     * */
    public Board(Board board) {
        this.copy(board);
    }

    /** Initializes board with given configuration and starting turn.
     *
     * @param config Board configuration.
     * @param turn Starting turn.
     * */
    public Board(Piece[][] config, Piece turn) {
        for (int r = 0; r < BOARD_SIZE; r++) {
            System.arraycopy(config[r], 0, _board[r], 0, BOARD_SIZE);
        }
        _turn = turn;
        _emptyPlacesInitialized = false;
        _winnerKnown = false;
    }

    /** Copies board to this board.
     *
     * @param board Board to copy from.
     * */
    void copy(Board board) {
        for (int r = 0; r < BOARD_SIZE; r++) {
            System.arraycopy(board._board[r], 0, _board[r], 0, BOARD_SIZE);
        }
        _turn = board._turn;
        _moves.addAll(board._moves);
        _emptyPlacesInitialized = board._emptyPlacesInitialized;
        if (_emptyPlacesInitialized) {
            _emptyPlaces.addAll(board._emptyPlaces);
        }
        _winnerKnown = board._winnerKnown;
        _winner = board._winner;
    }

    /** Resets the board to starting configuration. */
    void reset() {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                _board[r][c] = E;
            }
        }
        _turn = X;
        _emptyPlacesInitialized = false;
        _winnerKnown = false;
    }

    /** Converts a place string to board coordinates.
     *
     * @param place Place string.
     * @return Coordinates of place.
     * */
    int[] coords(String place) {
        return new int[] {place.charAt(0) - 'a', place.charAt(1) - '1'};
    }

    /** Converts a board coordinates to place string.
     *
     * @param col Column.
     * @param row Row.
     * @return Place string
     * */
    String place(int col, int row) {
        String place = "";
        switch (col) {
            case 0:
                place += "a";
                break;
            case 1:
                place += "b";
                break;
            case 2:
                place += "c";
                break;
        }
        switch (row) {
            case 0:
                place += "1";
                break;
            case 1:
                place += "2";
                break;
            case 2:
                place += "3";
                break;
        }
        return place;
    }

    /** List of all empty places on the Board.
     *
     * @return List of all empty place strings.
     * */
    public List<String> emptyPlaces() {
        if (_emptyPlacesInitialized) {
            return _emptyPlaces;
        }
        List<String> emptyPlaces = new ArrayList<>();
        for (int c = 0; c < BOARD_SIZE; c++) {
            for (int r = 0; r < BOARD_SIZE; r++) {
                if (_board[r][c] == E) {
                    emptyPlaces.add(place(c, r));
                }
            }
        }
        _emptyPlacesInitialized = true;
        _emptyPlaces = emptyPlaces;
        return emptyPlaces;
    }

    /** Puts a piece corresponding to the current turn on the board.
     *
     * @param col Column of place to put piece.
     * @param row Row of place to put piece.
     * @return True if piece was put on the place, false if the place
     * is not empty.
     * */
    public boolean put(int col, int row) {
        if (_board[row][col] == E) {
            _board[row][col] = _turn;
            _moves.add(place(col, row));
            _turn = _turn.opposite();
            _emptyPlacesInitialized = false;
            _winnerKnown = false;
            return true;
        }
        return false;
    }

    /** Puts a piece corresponding to the current turn on the board.
     *
     * @param place Place to put piece
     * @return True if piece was put on the place, false if place string
     * is malformed or place is not empty.
     * */
    public boolean put(String place) {
        if (SQ.matcher(place).matches()) {
            int[] coords = coords(place);
            return put(coords[0], coords[1]);
        }
        return false;
    }

    /** Returns the state of the board to one turn prior. */
    public void undo() {
        String recent = _moves.get(_moves.size() - 1);
        int[] coords = coords(recent);
        _board[coords[1]][coords[0]] = E;
        _moves.remove(_moves.size() - 1);
        _emptyPlacesInitialized = false;
        _winnerKnown = false;
        _turn = _turn.opposite();
    }

    /** Returns the move list.
     *
     * @return _moves.
     * */
    public List<String> moves() {
        return _moves;
    }

    /** Returns the current turn.
     *
     * @return _turn.
     * */
    public Piece turn() {
        return _turn;
    }

    /** Returns the winner.
     *
     * @return null if there is no winner, E if tie, or the winning Piece.
     * */
    public Piece winner() {
        if (_winnerKnown) {
            return _winner;
        }
        Piece winner = null;
        if (
                   (_board[0][0] == _board[0][1] && _board[0][1] == _board[0][2] && _board[0][0] != E)
                || (_board[1][0] == _board[1][1] && _board[1][1] == _board[1][2] && _board[1][0] != E)
                || (_board[2][0] == _board[2][1] && _board[2][1] == _board[2][2] && _board[2][0] != E)
                || (_board[0][0] == _board[1][0] && _board[1][0] == _board[2][0] && _board[0][0] != E)
                || (_board[0][1] == _board[1][1] && _board[1][1] == _board[2][1] && _board[0][1] != E)
                || (_board[0][2] == _board[1][2] && _board[1][2] == _board[2][2] && _board[0][2] != E)
                || (_board[0][0] == _board[1][1] && _board[1][1] == _board[2][2] && _board[1][1] != E)
                || (_board[0][2] == _board[1][1] && _board[1][1] == _board[2][0] && _board[1][1] != E)) {
            winner = _turn.opposite();
        } else if (emptyPlaces().size() == 0) {
            winner = E;
        }
        _winner = winner;
        _winnerKnown = true;
        return winner;
    }

    /** Returns the raw board.
     *
     * @return _board.
     * */
    public Piece[][] rawBoard() {
        return _board;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("===\n");
        for (int r = BOARD_SIZE - 1; r >= 0; r--) {
            sb.append("\t");
            sb.append(r + 1);
            for (int c = 0; c < BOARD_SIZE; c++) {
                sb.append(" ");
                if (_board[r][c] != E) {
                    sb.append(_board[r][c]);
                } else {
                    sb.append("-");
                }
            }
            sb.append("\n");
        }
        sb.append("\t  a b c\n");
        sb.append("===\n");
        sb.append("Next move:  ");
        sb.append(_turn);
        return sb.toString();
    }

    @Override
    public boolean equals(Object other) {
        try {
            Board otherBoard = (Board) other;
            for (int r = 0; r < BOARD_SIZE; r++) {
                for (int c = 0; c < BOARD_SIZE; c++) {
                    if (_board[r][c] != otherBoard._board[r][c]) {
                        return false;
                    }
                }
            }
            if (_turn != otherBoard._turn) {
                return false;
            }
            if (_moves.size() != otherBoard._moves.size()) {
                return false;
            }
            if (_moves.size() > 0) {
                for (int i = 0; i < _moves.size(); i++) {
                    if (!_moves.get(i).equals(otherBoard._moves.get(i))) {
                        return false;
                    }
                }
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    /** Board configuration. */
    private Piece[][] _board = new Piece[BOARD_SIZE][BOARD_SIZE];
    /** Current turn. */
    private Piece _turn;
    /** List of all moves made so far. */
    private List<String> _moves = new ArrayList<>();
    /** Cached list of all empty places on the board. */
    private List<String> _emptyPlaces = new ArrayList<>();
    /** True iff _emptyPlaces is up-to-date. */
    private boolean _emptyPlacesInitialized = false;
    /** This board's winner. */
    private Piece _winner;
    /** True iff the winner is known. */
    private boolean _winnerKnown;
}
