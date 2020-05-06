package client;

import core.Terminal;
import core.types.Bill;
import core.types.Vehicles;

import javax.json.*;
import javax.json.stream.JsonParsingException;
import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Client {

    public static final String BILL_DIR = "./bills/";

    private final Terminal term;
    private final Socket conn;
    private final BufferedReader in;
    private final PrintWriter out;
    private boolean running;
    private Vehicles vehicles;

    public Client(String host, int port) throws IOException {
        this.term = new Terminal();
        this.conn = new Socket(host, port);
        this.in = new BufferedReader(new InputStreamReader(this.conn.getInputStream()));
        this.out = new PrintWriter(this.conn.getOutputStream());
        this.term.info("Connected to server.\n");
    }

    public boolean run() {
        this.running = true;
        while(running) {
            if(!checkConnection()) {
                this.term.warn("Lost connection to server! Reconnecting...\n");
                return true;
            }
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
                case "5":
                    doBilling();
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
        return false;
    }

    public void printMenu() {
        this.term.info("&aToll System\n&c1. &rHeartbeat\n&c2. &rLoad Vehicles\n&c3. &rRegister Valid Toll Event\n&c4. &rRegister Invalid Toll Event\n&c5. &rDo Billing\n&cq. &rQuit\n");
    }

    private boolean checkConnection() {
        this.out.println("{\"PacketType\":\"Heartbeat\"}");
        this.out.flush();
        String response;
        try {
            response = this.in.readLine();
            JsonReader reader = Json.createReader(new StringReader(response));
            JsonObject json = reader.readObject();
            if("Heartbeat response".equals(json.getString("PacketType"))) {
                return true;
            }
        } catch (JsonParsingException | IOException | NullPointerException e) {
            return false;
        }
        return false;
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
        } catch (IOException | NullPointerException e) {
            this.term.error("Could not get response from server.\n");
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
        } catch (IOException | NullPointerException e) {
            this.term.error("Could not get response from server.\n");
        }
    }

    private void registerValidTollEvent() {
        if(this.vehicles == null) {
            this.term.error("Please load vehicles first.\n");
            return;
        }
        String boothId = this.term.readLine("Toll Booth ID > ");
        String registration = this.term.readLine("Vehicle Reg > ");
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
        } catch (IOException | NullPointerException e) {
            this.term.error("Could not get response from server.\n");
        }
    }

    private void registerInvalidTollEvent() {
        if(this.vehicles == null) {
            this.term.error("Please load vehicles first.\n");
            return;
        }
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
        } catch (IOException | NullPointerException e) {
            this.term.error("Could not get response from server.\n");
        }
    }

    private void doBilling() {
        this.out.println("{\"PacketType\":\"DoBilling\"}");
        this.out.flush();
        String response = "";
        try {
            response = this.in.readLine();
            JsonReader reader = Json.createReader(new StringReader(response));
            JsonObject json = reader.readObject();
            if("DoneBilling".equals(json.getString("PacketType"))) {
                this.term.info("Got Bills.\n");
                createBillDirectory();
                JsonArray billJson = json.getJsonArray("Bills");
                List<Bill> bills = new ArrayList<>();
                for(JsonValue billValue: billJson) {
                    JsonObject billObj = billValue.asJsonObject();
                    Bill bill = new Bill(billObj);
                    bills.add(bill);
                    createBill(bill);
                }
            } else if("Error".equals(json.getString("PacketType"))) {
                term.warn(json.getJsonObject("Error").getString("Message")+"\n");
            } else {
                term.error(String.format("Unknown server response:\n%s\n", json.toString()));
            }
        } catch (JsonParsingException e) {
            term.error(String.format("Server sent malformed data:\n%s", response));
        } catch (IOException | NullPointerException e) {
            this.term.error("Could not get response from server.\n");
        }
    }

    private void createBillDirectory() {
        new File(BILL_DIR).mkdirs();
    }

    private void createBill(Bill bill) throws FileNotFoundException, UnsupportedEncodingException {
        String filename = String.format("%s_%s.md", bill.getName(), Instant.now().toString());
        PrintWriter writer = new PrintWriter(BILL_DIR+filename, "UTF-8");
        writer.println(bill.displayBill());
        writer.flush();
        writer.close();
        term.info(String.format("Wrote file &c%s&r\n", filename));
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
