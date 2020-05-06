package server;

import core.Terminal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final Terminal term;
    private final ServerSocket socket;
    private boolean running;
    private final ThreadGroup threads;

    public Server(int port) throws IOException {
        this.term = new Terminal();
        this.socket = new ServerSocket(port);
        this.running = false;
        this.threads = new ThreadGroup("connections");
    }

    public void run() {
        this.running = true;
        this.term.info("Started Server.\n");
        while(this.running) {
            try {
                Socket conn = socket.accept();
                this.term.info(String.format("Got connection: %s\n", conn.getInetAddress().getHostAddress()));
                SocketHandler handler = new SocketHandler(this.term, conn);
                Thread thread = new Thread(this.threads, handler);
                thread.start();
            } catch (IOException e) {
                this.term.error(String.format("Something went wrong: %s\n", e.getMessage()));
                e.printStackTrace();
            }

        }
    }

}
