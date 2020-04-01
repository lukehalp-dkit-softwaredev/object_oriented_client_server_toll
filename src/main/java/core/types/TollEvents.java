package core.types;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class TollEvents {

    private Vehicles vehicles;
    private HashMap<String, ArrayList<TollEvent>> events;
    private HashSet<TollEvent> invalidEvents;

    public TollEvents() {
        this.events = new HashMap<>();
        this.invalidEvents = new HashSet<>();
        this.vehicles = new Vehicles();
    }

    public TollEvents(Vehicles vehicles) {
        this.events = new HashMap<>();
        this.invalidEvents = new HashSet<>();
        this.vehicles = vehicles;
    }

    public Vehicles getVehicles() {
        return vehicles;
    }

    public void setVehicles(Vehicles vehicles) {
        this.vehicles = vehicles;
    }

    public HashMap<String, ArrayList<TollEvent>> getEvents() {
        return events;
    }

    public void setEvents(HashMap<String, ArrayList<TollEvent>> events) {
        this.events = events;
    }

    public HashSet<TollEvent> getInvalidEvents() {
        return invalidEvents;
    }

    public void setInvalidEvents(HashSet<TollEvent> invalidEvents) {
        this.invalidEvents = invalidEvents;
    }

    public boolean addTollEvent(TollEvent tollEvent) {
        String registration = tollEvent.getRegistration();
        if(vehicles.hasVehicle(registration)) {
            if(!events.containsKey(registration)) {
                events.put(registration, new ArrayList<>());
            }
            events.get(registration).add(tollEvent);
            return true;
        }
        invalidEvents.add(tollEvent);
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TollEvents that = (TollEvents) o;
        return Objects.equals(getVehicles(), that.getVehicles()) &&
                Objects.equals(getEvents(), that.getEvents()) &&
                Objects.equals(getInvalidEvents(), that.getInvalidEvents());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVehicles(), getEvents(), getInvalidEvents());
    }

    @Override
    public String toString() {
        return "TollEvents{" +
                "vehicles=" + vehicles +
                ", events=" + events +
                ", invalidEvents=" + invalidEvents +
                '}';
    }
}
