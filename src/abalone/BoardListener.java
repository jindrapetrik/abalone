package abalone;

/**
 *
 * @author JPEXS
 */
public interface BoardListener {
    public void win(int side);
    public void scoreChanged(int side,int value);
    public void move(Move m);

}
