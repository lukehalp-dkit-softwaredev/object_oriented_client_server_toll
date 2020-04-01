package core.types;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Vehicles {

    HashSet<String> vehicles;

    public Vehicles() {
        this.vehicles = new HashSet<>();
    }

    public boolean addVehicle(String registration) {
        return this.vehicles.add(registration);
    }

    public boolean removeVehicle(String registration) {
        return this.vehicles.remove(registration);
    }

    public boolean hasVehicle(String registration) {
        return this.vehicles.contains(registration);
    }

    public Set<String> getVehicles() {
        return vehicles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicles vehicles1 = (Vehicles) o;
        return Objects.equals(vehicles, vehicles1.vehicles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicles);
    }

    @Override
    public String toString() {
        return "Vehicles{" +
                "vehicles=" + vehicles +
                '}';
    }
}
