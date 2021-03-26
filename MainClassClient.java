

import java.io.IOException;
import java.rmi.NotBoundException;

public class MainClassClient {
	public static void main(String[] args) throws IOException, NotBoundException, InterruptedException{
        // crea e avvia il server RMI
        Client c = new Client();
        c.start();
    }
}
