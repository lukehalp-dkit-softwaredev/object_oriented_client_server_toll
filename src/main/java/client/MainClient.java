package client;

import java.io.IOException;

public class MainClient {

    public static void main(String[] args) {
        int wait = 1;
        boolean running = true;
        while(running) {
            try {
                Client client = new Client("localhost", 50000);
                wait = 1;
                running = client.run();
            } catch(IOException e) {
                String time = String.format("%d seconds", wait);
                if(wait > 59) {
                    time = String.format("%d minutes", wait/60);
                }
                if(wait > 60*60 - 1) {
                    time = String.format("%d hours", wait/60/60);
                }
                System.out.printf("Could not connect to server. Trying again in %s...\n", time);
                try {
                    Thread.sleep(1000*wait);
                } catch (InterruptedException ignored) {
                }
                wait *= 2;
            }
        }


    }

}
