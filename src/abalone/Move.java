package abalone;

import java.util.List;

/**
 *
 * @author JPEXS
 */
public class Move {
    public List<Integer> selection;
    public int index;

    public Move(List<Integer> selection, int index) {        
        this.selection = selection;
        this.index = index;
    }

    @Override
    public String toString() {
        String r="[";
        for(int i:selection){
            r+=""+i+" ";
        }
        r+="]->";
        r+=index;
        return r;
    }


}
