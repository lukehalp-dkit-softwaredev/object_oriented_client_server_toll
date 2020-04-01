package client;

import core.Terminal;
import core.types.Vehicles;

import javax.json.*;
import javax.json.stream.JsonParsingException;
import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Client {

    private Terminal term;
    private Socket conn;
    private BufferedReader in;
    private PrintWriter out;
    private boolean running;
    private Vehicles vehicles;

    public Client(String host, int port) throws IOException {
        this.term = new Terminal();
        this.conn = new Socket(host, port);
        this.in = new BufferedReader(new InputStreamReader(this.conn.getInputStream()));
        this.out = new PrintWriter(this.conn.getOutputStream());
        this.term.info("Connected to server.\n");
    }

    public void run() {
        this.running = true;
        while(running) {
            printMenu();
            String cmd = this.term.readLine("> ");
            switch (cmd) {
                case "1":
                    heartbeat();
                    break;
                case "2":
                    getRegisteredVehicles();
                    break;
                case "3":
                    registerValidTollEvent();
                    break;
                case "4":
                    registerInvalidTollEvent();
                    break;
                case "q":
                    close();
                    this.running = false;
                    break;
                default:
                    this.term.info("&bInvalid command!&r\n");
                    break;
            }
        }
        this.term.info("&aGoodbye.&r");
    }

    public void printMenu() {
        this.term.info("&aToll System\n&c1. &rHeartbeat\n&c2. &rLoad Vehicles\n&c3. &rRegister Valid Toll Event\n&c4. &rRegister Invalid Toll Event\n&cq. &rQuit\n");
    }

    private void heartbeat() {
        this.out.println("{\"PacketType\":\"Heartbeat\"}");
        this.out.flush();
        String response = "";
        try {
            response = this.in.readLine();
            JsonReader reader = Json.createReader(new StringReader(response));
            JsonObject json = reader.readObject();
            if("Heartbeat response".equals(json.getString("PacketType"))) {
                this.term.info("Heartbeat Successful.\n");
            } else if("Error".equals(json.getString("PacketType"))) {
                term.warn(json.getJsonObject("Error").getString("Message")+"\n");
            } else {
                term.error(String.format("Unknown server response:\n%s\n", json.toString()));
            }
        } catch (JsonParsingException e) {
            term.error(String.format("Server sent malformed data:\n%s", response));
        } catch (IOException e) {
            this.term.error("Could not get repsonse from server.\n");
        }
    }

    private void getRegisteredVehicles() {
        this.out.println("{\"PacketType\":\"GetRegisteredVehicles\"}");
        this.out.flush();
        String response = "";
        try {
            response = this.in.readLine();
            JsonReader reader = Json.createReader(new StringReader(response));
            JsonObject json = reader.readObject();
            if("ReturnRegisteredVehicles".equals(json.getString("PacketType"))) {
                Vehicles vehicles = new Vehicles();
                for(JsonValue vehicle: json.getJsonArray("Vehicles")) {
                    vehicles.addVehicle(((JsonString) vehicle).getString());
                }
                this.vehicles = vehicles;
                this.term.info("Loaded vehicles from database.\n");
            } else if("Error".equals(json.getString("PacketType"))) {
                term.warn(json.getJsonObject("Error").getString("Message")+"\n");
            } else {
                term.error(String.format("Unknown server response:\n%s\n", json.toString()));
            }
        } catch (JsonParsingException e) {
            term.error(String.format("Server sent malformed data:\n%s", response));
        } catch (IOException e) {
            this.term.error("Could not get repsonse from server.\n");
        }
    }

    private void registerValidTollEvent() {
        if(this.vehicles == null) {
            this.term.error("Please load vehicles first.\n");
            return;
        }
        term.info(this.vehicles.toString());
        String boothId = this.term.readLine("Toll Booth ID > ");
        String registration = this.term.readLine("Vehicle Reg > ");;
        if(!this.vehicles.hasVehicle(registration)) {
            this.term.error(String.format("Invalid registration entered: %s\n", registration));
            return;
        }
        String imageId = this.term.readLine("Image ID > ");
        String dateTime = this.term.readLine("Time (YYYY-MM-ddThh:mm:ssZ)> ");
        JsonObject packet;
        try {
            packet = Json.createObjectBuilder()
                    .add("PacketType", "RegisterValidTollEvent")
                    .add("TollBoothID", boothId)
                    .add("Vehicle Registration", registration)
                    .add("Vehicle Image ID", Long.parseLong(imageId))
                    .add("LocalDateTime", Instant.parse(dateTime).toString())
                    .build();
        } catch (NumberFormatException e) {
            this.term.error(String.format("Invalid image ID: %s\n", imageId));
            return;
        } catch (DateTimeParseException e) {
            this.term.error(String.format("Invalid Datetime: %s\n", dateTime));
            return;
        }
        String req = packet.toString();
        this.out.println(req);
        this.out.flush();
        String response = "";
        try {
            response = this.in.readLine();
            JsonReader reader = Json.createReader(new StringReader(response));
            JsonObject json = reader.readObject();
            if("RegisteredValidTollEvent".equals(json.getString("PacketType"))) {
                this.term.info("Toll event registered.\n");
            } else if("Error".equals(json.getString("PacketType"))) {
                term.warn(json.getJsonObject("Error").getString("Message")+"\n");
            } else {
                term.error(String.format("Unknown server response:\n%s\n", json.toString()));
            }
        } catch (JsonParsingException e) {
            term.error(String.format("Server sent malformed data:\n%s", response));
        } catch (IOException e) {
            this.term.error("Could not get repsonse from server.\n");
        }
    }

    private void registerInvalidTollEvent() {
        if(this.vehicles == null) {
            this.term.error("Please load vehicles first.\n");
            return;
        }
        term.info(this.vehicles.toString());
        String boothId = this.term.readLine("Toll Booth ID > ");
        String registration = this.term.readLine("Vehicle Reg > ");
        if(this.vehicles.hasVehicle(registration)) {
            this.term.error(String.format("Valid registration entered: %s\n", registration));
            return;
        }
        String imageId = this.term.readLine("Image ID > ");
        String dateTime = this.term.readLine("Time (YYYY-MM-ddThh:mm:ssZ)> ");
        JsonObject packet;
        try {
            packet = Json.createObjectBuilder()
                    .add("PacketType", "RegisterInvalidTollEvent")
                    .add("TollBoothID", boothId)
                    .add("Vehicle Registration", registration)
                    .add("Vehicle Image ID", Long.parseLong(imageId))
                    .add("LocalDateTime", Instant.parse(dateTime).toString())
                    .build();
        } catch (NumberFormatException e) {
            this.term.error(String.format("Invalid image ID: %s\n", imageId));
            return;
        } catch (DateTimeParseException e) {
            this.term.error(String.format("Invalid Datetime: %s\n", dateTime));
            return;
        }
        String req = packet.toString();
        this.out.println(req);
        this.out.flush();
        String response = "";
        try {
            response = this.in.readLine();
            JsonReader reader = Json.createReader(new StringReader(response));
            JsonObject json = reader.readObject();
            if("RegisteredInvalidTollEvent".equals(json.getString("PacketType"))) {
                this.term.info("Toll event registered.\n");
            } else if("Error".equals(json.getString("PacketType"))) {
                term.warn(json.getJsonObject("Error").getString("Message")+"\n");
            } else {
                term.error(String.format("Unknown server response:\n%s\n", json.toString()));
            }
        } catch (JsonParsingException e) {
            term.error(String.format("Server sent malformed data:\n%s", response));
        } catch (IOException e) {
            this.term.error("Could not get repsonse from server.\n");
        }
    }

    private void close() {
        this.out.println("{\"PacketType\":\"Close\"}");
        this.out.flush();
        this.out.close();
        try {
            this.in.close();
            this.conn.close();
        } catch (IOException e) {
            this.term.error(String.format("Could not close socket: %s", e.getMessage()));
        }
    }

}
