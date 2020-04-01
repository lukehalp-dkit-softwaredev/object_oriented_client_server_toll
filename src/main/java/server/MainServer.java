package server;

import java.io.IOException;

public class MainServer {

    public static void main(String[] args) {
        try {
            Server server = new Server(50000);
            server.run();
        } catch (IOException e) {
            System.err.println("Uh oh! Something went wrong!");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
