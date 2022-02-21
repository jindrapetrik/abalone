package abalone;

import java.io.IOException;

/**
 *
 * @author JPEXS
 */
public class Main {
    private static Board b;
    public static String version="0.9.2";
    
    public static final int PLAYER_HUMAN=1;
    public static final int PLAYER_NET=2;

    public static int playerType[]=new int[]{0,PLAYER_HUMAN,PLAYER_HUMAN};

    private static NetPlayer netPlayer;


    private static void printArgs(){
        System.out.println("Usage:");
        System.out.println("Singleplayer game: java -jar abalone.jar");
        System.out.println("For network game - server: java -jar abalone.jar -s <PORT>");
        System.out.println("For network game - client: java -jar abalone.jar -c <ADDRESS> <PORT>");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        b=new Board();
        b.initGame();

        if(args.length>0){
            if(args[0].equals("-s")){
                if(args.length<2){
                    System.err.println("2 arguments needed");
                    printArgs();
                    System.exit(1);
                }
                int port=1234;
                try{
                    port=Integer.parseInt(args[1]);
                }catch(NumberFormatException nex){
                    System.err.println("Invalid port");
                    printArgs();
                    System.exit(1);
                }
                try {
                    netPlayer = new NetPlayer(b, Board.PIECE_WHITE, port);
                    playerType[Board.PIECE_WHITE]=PLAYER_NET;
                    netPlayer.start();
                } catch (IOException ex) {
                    System.err.println("TCP error");
                    System.exit(1);
                }
            }else if(args[0].equals("-c")){
                if(args.length<3){
                    System.err.println("3 arguments needed");
                    printArgs();
                    System.exit(1);
                }
                int port=1234;
                try{
                    port=Integer.parseInt(args[2]);
                }catch(NumberFormatException nex){
                    System.err.println("Invalid port");
                    System.exit(1);
                }
                try {
                    netPlayer = new NetPlayer(b, Board.PIECE_BLACK, args[1],port);
                    playerType[Board.PIECE_BLACK]=PLAYER_NET;
                    netPlayer.start();
                } catch (IOException ex) {
                    System.err.println("TCP error");
                    System.exit(1);
                }
            }else{
                printArgs();
                System.exit(1);
            }
        }

        (new MainFrame(b)).setVisible(true);
        
    }

}
