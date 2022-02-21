package abalone;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author JPEXS
 */
public class NetPlayer extends Thread implements BoardListener {
    private Socket socket;
    private DataInputStream is;
    private DataOutputStream os;
    private Board b;
    private int side;
    private boolean looping=true;

    public NetPlayer(Board b,int side,String address,int port) throws IOException{
        socket=new Socket(InetAddress.getByName(address),port);
        os=new DataOutputStream(socket.getOutputStream());
        is=new DataInputStream(socket.getInputStream());
        this.b=b;
        b.addBoardListener(this);
        this.side=side;
    }

    public NetPlayer(Board b,int side,int port) throws IOException{
        ServerSocket ssocket=new ServerSocket(port);
        socket=ssocket.accept();
        os=new DataOutputStream(socket.getOutputStream());
        is=new DataInputStream(socket.getInputStream());
        this.b=b;
        this.side=side;
        b.addBoardListener(this);
    }

    public void sendMove(Move m) throws IOException{
        os.write('M');
        os.write(m.selection.size()+1);
        os.write(m.index);
        for(int i=0;i<m.selection.size();i++){
            os.write(m.selection.get(i));
        }        
    }

    public Move receiveMove() throws IOException{
        int type=is.read();
        byte data[]=new byte[is.read()];
        is.readFully(data);
        if(type=='M'){
            List<Integer> selection=new ArrayList<Integer>();
            int index=data[0] & 0xff;
            for(int i=1;i<data.length;i++){
                selection.add(data[i] & 0xff);
            }
            Move m=new Move(selection,index);
            return m;
        }
        return receiveMove();
    }

    public void win(int side) {
        looping=false;
        try{
            is.close();
        }catch(IOException iex){

        }
        try{
            os.close();
        }catch(IOException iex){

        }

        try{
            socket.close();
        }catch(IOException iex){

        }
    }

    public void scoreChanged(int side, int value) {

    }

    public void move(Move m) {
       if(b.getPlayingSide()==side){
            try {
                sendMove(m);
            } catch (IOException ex) {
                
            }
       }
    }

    @Override
    public void run() {
        while(looping){
            try {
                Move m = receiveMove();
                b.doMove(m);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,"Disconnected");
                System.exit(0);
            }
        }
    }


}
