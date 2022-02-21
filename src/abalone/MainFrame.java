package abalone;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author JPEXS
 */
public class MainFrame extends JFrame implements BoardListener{

    private JLabel scoreLabel=new JLabel("");
    private Board b;

    public MainFrame(Board b){
        setSize(600,600);
        this.b=b;
        setResizable(false);
        setTitle("JPEXS Abalone v"+Main.version);
        addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

        });
        Container c=getContentPane();
        c.setLayout(new BorderLayout());
        b.addBoardListener(this);
        c.add(new GamePanel(b),BorderLayout.CENTER);
        scoreLabel.setFont(new Font("Courier",Font.BOLD,20));
        c.add(scoreLabel,BorderLayout.SOUTH);
        updateStatus();
    }

    private String sideToStr(int side){
        if(side==Board.PIECE_BLACK) return "Black";
        if(side==Board.PIECE_WHITE) return "White";
        return "None";
    }

    public void win(int side) {
        repaint();     
        JOptionPane.showMessageDialog(this,sideToStr(side)+" player won!");
    }

    public void scoreChanged(int side, int value) {
        updateStatus();
        SoundPlayer.playSound("/out.mp3");
    }

    private void updateStatus(){

        scoreLabel.setText("                  White: "+b.getWhiteScore()+" Black:"+b.getBlackScore()+"      Playing:"+sideToStr(b.getPlayingSide()));
        repaint();
    }

    public void move(Move m) {
        SoundPlayer.playSound("/move.mp3");
        updateStatus();
    }

}
