package abalone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author JPEXS
 */
public class Board {

    private int pieces[];
    private int sideWidth;
    public static final int PIECE_NONE = 0;
    public static final int PIECE_BLACK = 1;
    public static final int PIECE_WHITE = 2;
    private int playingSide = PIECE_BLACK;
    private List<Integer> selection = new ArrayList<Integer>();
    private int scoreToWin = 6;
    private int whiteScore = 0;
    private int blackScore = 0;
    private boolean stopped = false;
    private List<BoardListener> boardListeners = new ArrayList<BoardListener>();

    public void doMove(Move m) {
        selection = m.selection;
        pieceClicked(m.index);
        informMoveListeners(m);
    }

    public Board() {
        this(5);
    }

    public boolean isStopped() {
        return stopped;
    }

    public int getPlayingSide() {
        return playingSide;
    }

    public int getWhiteScore() {
        return whiteScore;
    }

    public int getBlackScore() {
        return blackScore;
    }

    public void addBoardListener(BoardListener listener) {
        boardListeners.add(listener);
    }

    public void removeBoardListener(BoardListener listener) {
        if (boardListeners.contains(listener)) {
            boardListeners.remove(listener);
        }
    }

    public int secondSide(int side) {
        if (side == PIECE_BLACK) {
            return PIECE_WHITE;
        }
        if (side == PIECE_WHITE) {
            return PIECE_BLACK;
        }
        return PIECE_NONE;
    }

    private boolean isAdjacent(int index1, int index2) {
        return getDirection(index1, index2) > -1;
    }

    private int oppositeDirection(int direction) {
        if (direction == -1) {
            return -1;
        }
        return 5 - direction;
    }

    private int getDirection(int index1, int index2) {
        int adj[] = getAdjacent(index1);
        for (int i = 0; i < adj.length; i++) {
            if (adj[i] == index2) {
                return i;
            }
        }
        return -1;
    }

    private void finishMove(int index) {
        playingSide = secondSide(playingSide);
        informMoveListeners(new Move(selection, index));
        hideSelection();
    }

    public void pieceClicked(int index) {
        if (stopped) {
            return;
        }
        if (index < 0) {
            return;
        }
        if (index > pieces.length - 1) {
            return;
        }

        if (pieces[index] == playingSide) {
            selsize:
            switch (selection.size()) {
                case 0:
                    selection.add(index);
                    break;
                case 1:
                    if (selection.get(0) == index) {
                        hideSelection();
                    } else {
                        int adj[] = getAdjacent(index);
                        int prev = selection.get(0);
                        for (int a : adj) {
                            if (a == prev) {
                                selection.add(index);
                                break selsize;
                            }
                        }
                        hideSelection();
                        selection.add(index);
                    }
                    break;
                case 2:
                    int direction = getDirection(selection.get(0), selection.get(1));
                    int direction1 = getDirection(index, selection.get(0));
                    int direction2 = getDirection(selection.get(1), index);

                    if (direction == direction1) {
                        selection.add(0, index);
                    } else if (direction == direction2) {
                        selection.add(index);
                    } else {
                        hideSelection();
                        selection.add(index);
                    }
                    break;
                default:
                    hideSelection();
                    selection.add(index);
            }
        } else if (pieces[index] == PIECE_NONE) {
            switch (selection.size()) {
                case 0:
                    break;
                case 1:
                    if (isAdjacent(index, selection.get(0))) {
                        pieces[index] = pieces[selection.get(0)];
                        pieces[selection.get(0)] = PIECE_NONE;
                        finishMove(index);
                    }
                    hideSelection();
                    break;
                default:
                    if (getDirection(selection.get(0), selection.get(1)) == getDirection(selection.get(selection.size() - 1), index)) {
                        Collections.reverse(selection);
                    }
                    if (getDirection(index, selection.get(0)) == getDirection(selection.get(0), selection.get(1))) {
                        pieces[index] = pieces[selection.get(0)];
                        pieces[selection.get(selection.size() - 1)] = PIECE_NONE;
                        finishMove(index);
                        break;
                    }


                    if (isAdjacent(index, selection.get(selection.size() - 1))) {
                        boolean ok = true;
                        for (int i = 0; i < selection.size() - 1; i++) {
                            if (isAdjacent(index, selection.get(i))) {
                                ok = false;
                            }
                        }
                        if (ok) {
                            Collections.reverse(selection);
                        }
                    }
                    if (isAdjacent(index, selection.get(0))) {
                        boolean ok = true;
                        for (int i = 1; i < selection.size(); i++) {
                            if (isAdjacent(index, selection.get(i))) {
                                ok = false;
                            }
                        }
                        if (ok) {
                            int selDirection = getDirection(selection.get(0), selection.get(1));
                            ok = true;
                            int next = index;
                            for (int i = 0; i < selection.size(); i++) {
                                if (next == -1) {
                                    ok = false;
                                }
                                if (getPiece(next) != PIECE_NONE) {
                                    ok = false;
                                }
                                next = getAdjacent(next)[selDirection];
                            }
                            if (ok) {
                                next = index;
                                for (int i = 0; i < selection.size(); i++) {
                                    setPiece(next, playingSide);
                                    setPiece(selection.get(i), PIECE_NONE);
                                    next = getAdjacent(next)[selDirection];
                                }
                                finishMove(index);
                                break;
                            }
                        }

                    }
                    break;
            }

        } else if (pieces[index] == secondSide(playingSide)) {
            if (selection.size() >= 2) {
                if (getDirection(selection.get(0), selection.get(1)) == getDirection(selection.get(selection.size() - 1), index)) {
                    Collections.reverse(selection);
                }
                if (getDirection(index, selection.get(0)) == getDirection(selection.get(0), selection.get(1))) {
                    int direction = getDirection(index, selection.get(0));
                    int opdirection = oppositeDirection(direction);
                    int next = selection.get(0);
                    int opponentsCount = 0;
                    for (int k = 0; k < selection.size(); k++) {
                        next = getAdjacent(next)[opdirection];
                        if (getPiece(next) == secondSide(playingSide)) {
                            opponentsCount++;
                        }
                        if (getPiece(next) == playingSide) {
                            break;
                        }
                        if (getPiece(next) == PIECE_NONE) {
                            if (opponentsCount < selection.size()) {
                                next = selection.get(selection.size() - 1);
                                int moved = PIECE_NONE;
                                for (int i = 0; i < selection.size() + opponentsCount + 1; i++) {
                                    int newmoved = getPiece(next);
                                    setPiece(next, moved);
                                    moved = newmoved;
                                    next = getAdjacent(next)[opdirection];
                                }

                            }
                            finishMove(index);
                            break;
                        }
                    }

                }
            }
        }

    }

    private void informWinListeners(int side) {
        for (BoardListener l : boardListeners) {
            l.win(side);
        }
    }

    private void informMoveListeners(Move m) {
        for (BoardListener l : boardListeners) {
            l.move(m);
        }
    }

    private void informScoreListeners(int side, int value) {
        for (BoardListener l : boardListeners) {
            l.scoreChanged(side, value);
        }
    }

    public void setPiece(int index, int piece) {
        if ((index < 0) || (index > pieces.length - 1)) {
            if (piece == PIECE_BLACK) {
                whiteScore++;
                informScoreListeners(PIECE_WHITE, whiteScore);
                if (whiteScore == scoreToWin) {
                    informWinListeners(PIECE_WHITE);
                    stopped = true;
                }
            }
            if (piece == PIECE_WHITE) {
                blackScore++;
                informScoreListeners(PIECE_BLACK, blackScore);
                if (blackScore == scoreToWin) {
                    informWinListeners(PIECE_BLACK);
                    stopped = true;
                }
            }
            return;
        }
        if (index > pieces.length - 1) {
            return;
        }
        pieces[index] = piece;
    }

    public int getPiece(int index) {
        if (index < 0) {
            return PIECE_NONE;
        }
        if (index > pieces.length - 1) {
            return PIECE_NONE;
        }
        return pieces[index];
    }

    public boolean isSelected(int index) {
        return selection.contains(index);
    }

    private void hideSelection() {
        selection.clear();
    }

    public void initGame() {
        for (int i = 0; i < pieces.length; i++) {
            pieces[i] = PIECE_NONE;
        }
        selection.clear();



        for (int i = 0; i < 11; i++) {
            pieces[i] = PIECE_BLACK;
        }
        pieces[13] = PIECE_BLACK;
        pieces[14] = PIECE_BLACK;
        pieces[15] = PIECE_BLACK;

        for (int i = pieces.length - 11; i < pieces.length; i++) {
            pieces[i] = PIECE_WHITE;
        }
        pieces[pieces.length - 14] = PIECE_WHITE;
        pieces[pieces.length - 15] = PIECE_WHITE;
        pieces[pieces.length - 16] = PIECE_WHITE;
    }

    public int getSideWidth() {
        return sideWidth;
    }

    public Board(int sideWidth) {
        this.sideWidth = sideWidth;
        int piecesCount = 0;
        int row = 0;
        while (true) {
            int rs = getRowSize(row);
            if (rs == 0) {
                break;
            }
            piecesCount += rs;
            row++;
        }
        pieces = new int[piecesCount];
    }

    public int getRowCount() {
        return sideWidth * 2 - 1;
    }

    public int getPiecesCount() {
        return pieces.length;
    }

    public int getRow(int index) {
        for (int i = 0; i < sideWidth * 2 - 1; i++) {
            int rs = getRowSize(i);
            if (index - rs < 0) {
                return i;
            }
            index = index - rs;
        }
        return -1;
    }

    public int getGraphicCol(int index) {
        int col = index;
        int row = 0;
        for (int i = 0; i < sideWidth * 2 - 1; i++) {
            int rs = getRowSize(i);
            if (col - rs < 0) {
                row = i;
                break;
            }
            col = col - rs;
        }
        if (row < sideWidth) {
            col = (sideWidth - 1 - row) + ((col + 1) * 2 - 2);
        } else {
            col = (row - sideWidth + 1) + ((col + 1) * 2 - 2);
        }
        return col;
    }

    public int getCol(int index) {
        int col = index;
        for (int i = 0; i < sideWidth * 2 - 1; i++) {
            int rs = getRowSize(i);
            if (col - rs < 0) {
                break;
            }
            col = col - rs;
        }
        return col;
    }

    public int[] getAdjacent(int index) {
        int ret[] = new int[]{-1, -1, -1, -1, -1, -1};
        int row = getRow(index);
        int col = getCol(index);
        int rowSize = getRowSize(row);


        if (row < sideWidth - 1) {
            ret[4] = index + rowSize;
            ret[5] = index + rowSize + 1;
        } else {
            if (col > 0) {
                ret[4] = index + rowSize - 1;
            }
            if (col < rowSize - 1) {
                ret[5] = index + rowSize;
            }
        }

        if (row < sideWidth) {
            if (col > 0) {
                ret[0] = index - rowSize;
            }
            if (col < rowSize - 1) {
                ret[1] = index - rowSize + 1;
            }

        } else {
            ret[0] = index - rowSize - 1;
            ret[1] = index - rowSize;
        }


        if (col > 0) {
            ret[2] = index - 1;
        }
        if (col < rowSize - 1) {
            ret[3] = index + 1;
        }

        return ret;
    }

    public int getRowSize(int rowIndex) {
        if (rowIndex < 0) {
            return 0;
        }
        if (rowIndex > sideWidth * 2 - 2) {
            return 0;
        }
        if (rowIndex < sideWidth) {
            return sideWidth + rowIndex;
        } else {
            return 3 * sideWidth - rowIndex - 2;
        }
    }

    public int getIndexFromColAndRow(int row, int col) {
        if(row<0) return -1;
        if(col<0) return -1;
        if(row>=getRowCount()) return -1;
        if(col>=getRowSize(row)) return -1;

        int index = 0;
        for (int i = 0; i < row; i++) {
            index += getRowSize(i);
        }

        index += col;
        return index;
    }

    public int officialToIndex(String official) {
        int row = getRowCount() - (official.charAt(0) - 'A') - 1;
        int scol = Integer.parseInt("" + official.charAt(1));
        int col = 0;
        if (row > sideWidth - 1) {
            col = scol - 1;
        } else {
            col = scol - (sideWidth - row);
        }
        return getIndexFromColAndRow(row, col);
    }

    public String indexToOfficial(int index) {
        String ret = "";
        int row = getRow(index);
        if (row == -1) {
            ret += "-";
        } else {
            ret += (char) ('A' + (getRowCount() - 1 - row));
        }
        int col = getCol(index);
        if (row == -1) {
            ret += "-";
        } else {
            if (row >= sideWidth - 1) {
                ret += (col + 1);
            } else {
                ret += (sideWidth - row + col);
            }
        }
        return ret;
    }
}
