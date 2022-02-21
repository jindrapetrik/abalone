package abalone;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author JPEXS
 */
public class GamePanel extends JPanel implements MouseListener {

    private Board b;
    private int ballSize=50;
    private int boardX=20;
    private int boardY=20;
    private int separator=20;
    private Image whiteBall;
    private Image blackBall;
    private Image emptyBall;

    public GamePanel(Board b) {
        setBackground(Color.white);
        addMouseListener(this);
        this.b=b;
        try {
            whiteBall=ImageIO.read(this.getClass().getResourceAsStream("/white.png"));
            blackBall=ImageIO.read(this.getClass().getResourceAsStream("/black.png"));
            emptyBall=ImageIO.read(this.getClass().getResourceAsStream("/empty.png"));
        } catch (IOException ex) {

        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2=(Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        BasicStroke bStroke = new BasicStroke(2f);
        g2.setStroke(bStroke);


        int bw=(2*b.getSideWidth()-1)*2*(ballSize/2+3)-3+2*separator;
        int bh=(2*b.getSideWidth()-1)*(ballSize+3)-3+2*separator;
        int xpoints[]=new int[]{boardX,boardX+bw/4,boardX+bw*3/4,boardX+bw,boardX+bw*3/4,boardX+bw/4};
        int ypoints[]=new int[]{boardY+bh/2,boardY,boardY,boardY+bh/2,boardY+bh,boardY+bh};

        Polygon p=new Polygon(xpoints,ypoints,xpoints.length);
        g2.setColor(Color.black);
        //g2.setColor(new Color(0x80,0x40,0x40));
        g2.fill(p);
        g2.setColor(Color.black);
        g2.draw(p);


        for (int i = 0; i < b.getPiecesCount(); i++) {
            int y = b.getRow(i);
            int x = b.getGraphicCol(i);
            
                switch (b.getPiece(i)) {
                    case Board.PIECE_NONE:
                        g.setColor(Color.gray);
                        break;
                    case Board.PIECE_BLACK:
                        g.setColor(Color.black);
                        break;
                    case Board.PIECE_WHITE:
                        g.setColor(Color.white);
                        break;
                }
            Ellipse2D.Float e=new Ellipse2D.Float(boardX+separator+x * (ballSize/2+3), boardY+separator+y * (ballSize+3), ballSize, ballSize);
            
            switch (b.getPiece(i)) {
                    case Board.PIECE_NONE:
                        g.drawImage(emptyBall, (int)e.x, (int)e.y, null);
                        break;
                    case Board.PIECE_BLACK:
                        g.drawImage(blackBall, (int)e.x, (int)e.y, null);
                        break;
                    case Board.PIECE_WHITE:
                        g.drawImage(whiteBall, (int)e.x, (int)e.y, null);
                        break;
                }
            //g2.fill(e);
            
            if (b.isSelected(i)) {
                g.setColor(Color.red);
                g2.draw(e);
            }else{
                //g.setColor(Color.black);
            }
            
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if(Main.playerType[b.getPlayingSide()]==Main.PLAYER_NET) return;
        for (int i = 0; i < b.getPiecesCount(); i++) {
            int y = b.getRow(i);
            int x = b.getGraphicCol(i);
            Ellipse2D.Float el=new Ellipse2D.Float(boardX+separator+x * (ballSize/2+3), boardY+separator+y * (ballSize+3), ballSize, ballSize);

            if (el.contains(e.getX(), e.getY())) {
                b.pieceClicked(i);
                repaint();
                break;
            }
        }

    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
