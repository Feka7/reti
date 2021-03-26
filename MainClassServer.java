

import java.io.IOException;
import java.rmi.NotBoundException;

public class MainClassServer {

    public static void main(String[] args) throws IOException, NotBoundException, IllegalArgumentException, InterruptedException{
     
        // crea e avvia il server RMI
        ServerRMI server_rmi = new ServerRMI();
        server_rmi.start();
        
        ServerWorth server_tcp = new ServerWorth();
        server_tcp.start();
    }
}
