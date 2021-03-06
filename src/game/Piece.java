package game;

/** Represents a piece that can occupy a space on the board.
 *
 * @author Richard Hu
 * */
public enum Piece {

    /** Names of pieces. E represents empty.*/
    E, X, O;

    /** Return the opposite of this piece.
     *
     * @return Opposite of this piece or E if this piece
     * is E.
     * */
    public Piece opposite() {
        switch (this) {
            case X:
                return O;
            case O:
                return X;
            default:
                return E;
        }
    }
}
