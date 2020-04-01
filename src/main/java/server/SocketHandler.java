package server;

import core.Terminal;
import core.dao.DaoException;
import core.dao.TollEventDao;
import core.dao.VehiclesDao;
import core.types.TollEvent;
import core.types.TollEvents;
import core.types.Vehicles;

import javax.json.*;
import javax.json.stream.JsonParsingException;
import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.util.Scanner;

public class SocketHandler implements Runnable {

    private Terminal term;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean running;

    public SocketHandler(Terminal term, Socket socket) throws IOException {
        this.term = term;
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream());
    }

    public void run() {
        if(socket == null) {
            return;
        }
        running = true;
        try {
            while (running) {

                String data = "";
                try {
                    data = in.readLine();
                    JsonReader reader = Json.createReader(new StringReader(data));
                    JsonObject json = reader.readObject();
                    handleQuery(json);
                } catch (JsonParsingException e) {
                    term.warn(String.format("Client sent malformed data:\n%s\nIgnoring.\n", data));
                }
            }
            in.close();
            out.close();
            socket.close();
            term.info("Socket closed.\n");
        } catch (IOException e) {
            term.error(String.format("Error reading socket: %s", e.getMessage()));
        }
    }

    private void handleQuery(JsonObject json) {
        term.info(String.format("Got Request: %s\n", json.toString()));
        switch (json.getString("PacketType")) {
            case "Heartbeat": {
                term.info("Request type: Heartbeat\n");
                out.println(heartbeat());
                out.flush();
                break;
            }
            case "GetRegisteredVehicles": {
                term.info("Request type: GetRegisteredVehicles\n");
                out.println(getRegisteredVehicles());
                out.flush();
                break;
            }
            case "RegisterValidTollEvent": {
                term.info("Request type: RegisterValidTollEvent\n");
                out.println(registerValidTollEvent(json));
                out.flush();
                break;
            }
            case "RegisterInvalidTollEvent": {
                term.info("Request type: RegisterInvalidTollEvent\n");
                out.println(registerInvalidTollEvent(json));
                out.flush();
                break;
            }
            case "Close": {
                term.info("Request type: Close\n");
                close();
                break;
            }
        }
    }

    private String heartbeat() {
        return Json.createObjectBuilder().add("PacketType", "Heartbeat response").build().toString();
    }

    private String getRegisteredVehicles() {
        try {
            Vehicles vehicles = new VehiclesDao().getVehicles();
            JsonArray vehicleList = Json.createArrayBuilder(vehicles.getVehicles()).build();
            return Json.createObjectBuilder()
                    .add("PacketType", "ReturnRegisteredVehicles")
                    .add("Vehicles", vehicleList)
                    .build().toString();
        } catch (DaoException e) {
            term.error(e.getMessage());
            return createErrorPacket(500, "Could not fetch Vehicles from database.").toString();
        }
    }

    private String registerValidTollEvent(JsonObject json) {
        try {
            TollEvent event = new TollEvent(
                    json.getString("TollBoothID"),
                    json.getString("Vehicle Registration"),
                    json.getInt("Vehicle Image ID"),
                    Instant.parse(json.getString("LocalDateTime"))
            );
            Vehicles vehicles = new VehiclesDao().getVehicles();
            if(!vehicles.hasVehicle(event.getRegistration())) {
                return createErrorPacket(404, "Registration not found.").toString();
            }
            new TollEventDao().registerValidTollEvent(event);
            return Json.createObjectBuilder()
                    .add("PacketType", "RegisteredValidTollEvent")
                    .build().toString();
        } catch (DaoException e) {
            term.error(e.getMessage());
            return createErrorPacket(500, "Something went wrong").toString();
        }
    }

    private String registerInvalidTollEvent(JsonObject json) {
        try {
            TollEvent event = new TollEvent(
                    json.getString("TollBoothID"),
                    json.getString("Vehicle Registration"),
                    json.getInt("Vehicle Image ID"),
                    Instant.parse(json.getString("LocalDateTime"))
            );
            new TollEventDao().registerInvalidTollEvent(event);
            return Json.createObjectBuilder()
                    .add("PacketType", "RegisteredInvalidTollEvent")
                    .build().toString();
        } catch (DaoException e) {
            term.error(e.getMessage());
            return createErrorPacket(500, "Something went wrong").toString();
        }
    }

    private void close() {
        this.running = false;
    }

    private JsonObject createErrorPacket(int code, String message) {
        return Json.createObjectBuilder()
                .add("PacketType", "Error")
                .add("Error", Json.createObjectBuilder()
                    .add("Code", code)
                    .add("Message", message)
                )
                .build();
    }

}
