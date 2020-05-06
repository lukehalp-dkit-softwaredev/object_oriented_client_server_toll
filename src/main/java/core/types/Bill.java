package core.types;

import javax.json.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Bill {

    private final int id;
    private final String name;
    private final String address;
    private final List<BillItem> items;

    public Bill(int id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.items = new ArrayList<>();
    }

    public Bill(JsonObject json) {
        this.id = json.getInt("customerId");
        this.name = json.getString("customerName");
        this.address = json.getString("customerAddress");
        this.items = new ArrayList<>();
        for(JsonValue item: json.getJsonArray("items")) {
            this.addItem(item.asJsonObject());
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public List<BillItem> getItems() {
        return items;
    }

    public double getTotal() {
        double total = 0;
        for(BillItem item: this.items) {
            total += item.cost;
        }
        return total;
    }

    public void addItem(String registration, Instant dateTime, String type, double cost) {
        BillItem item = new BillItem(registration, dateTime, type, cost);
        this.items.add(item);
    }

    public void addItem(JsonObject json) {
        BillItem item = new BillItem(json);
        this.items.add(item);
    }

    public String displayBill() {
        String bill = String.format("# Toll Bill\n\n## Customer #%d\n*Name:* %s\n\n*Address:* %s\n\n## Items\n",
                this.id, this.name, this.address);
        if(this.items.isEmpty()) {
            bill += "*No items available.*\n\n";
        } else {
            bill += String.format("| %12s | %20s | %8s | %6s |\n", "Registration", "Time", "Type", "Cost");
            bill += String.format("| %12s | %20s | %8s | %6s |\n",
                    "------------", "--------------------", "--------", "------");
            for (BillItem item : this.items) {
                bill += String.format("| %12s | %20s | %8s | €%05.2f |\n",
                        item.registration, item.dateTime, item.type, item.cost);
            }
            bill += "\n\n";
        }
        bill += String.format("*Total Payable:* €%.2f", this.getTotal());
        return bill;
    }

    public JsonObject toJson() {
        JsonArrayBuilder billItemsBuilder = Json.createArrayBuilder();
        for(BillItem item : this.items) {
            billItemsBuilder.add(item.toJson());
        }
        JsonArray billItems = billItemsBuilder.build();
        return Json.createObjectBuilder()
                .add("customerId", this.id)
                .add("customerName", this.name)
                .add("customerAddress", this.address)
                .add("items", billItems)
                .build();
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", items=" + items +
                '}';
    }
}

class BillItem {

    String registration;
    Instant dateTime;
    String type;
    double cost;

    public BillItem(String registration, Instant dateTime, String type, double cost) {
        this.registration = registration;
        this.dateTime = dateTime;
        this.type = type;
        this.cost = cost;
    }

    public BillItem(JsonObject json) {
        this.registration = json.getString("registration");
        this.dateTime = Instant.parse(json.getString("timestamp"));
        this.type = json.getString("type");
        this.cost = json.getJsonNumber("cost").doubleValue();
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("registration", this.registration)
                .add("timestamp", this.dateTime.toString())
                .add("type", this.type)
                .add("cost", this.cost)
                .build();
    }

    @Override
    public String toString() {
        return "BillItem{" +
                "registration='" + registration + '\'' +
                ", dateTime=" + dateTime +
                ", type='" + type + '\'' +
                ", cost=" + cost +
                '}';
    }
}
