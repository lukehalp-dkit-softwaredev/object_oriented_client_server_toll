package core.types;

import java.time.Instant;
import java.util.Objects;

public class TollEvent {

    private String boothId;
    private String registration;
    private long imageId;
    private Instant timestamp;

    public TollEvent(String boothId, String registration, long imageId) {
        this.registration = registration;
        this.imageId = imageId;
        this.timestamp = Instant.now();
        this.boothId = boothId;
    }

    public TollEvent(String boothId, String registration, long imageId, Instant timestamp) {
        this.registration = registration;
        this.imageId = imageId;
        this.timestamp = timestamp;
        this.boothId = boothId;
    }

    public TollEvent(String registration, long imageId) {
        this.registration = registration;
        this.imageId = imageId;
        this.timestamp = Instant.now();
        this.boothId = null;
    }

    public TollEvent(String registration, long imageId, Instant timestamp) {
        this.registration = registration;
        this.imageId = imageId;
        this.timestamp = timestamp;
        this.boothId = null;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getBoothId() {
        return boothId;
    }

    public void setBoothId(String boothId) {
        this.boothId = boothId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TollEvent tollEvent = (TollEvent) o;
        return getImageId() == tollEvent.getImageId() &&
                Objects.equals(getBoothId(), tollEvent.getBoothId()) &&
                Objects.equals(getRegistration(), tollEvent.getRegistration()) &&
                Objects.equals(getTimestamp(), tollEvent.getTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBoothId(), getRegistration(), getImageId(), getTimestamp());
    }

    @Override
    public String toString() {
        return "TollEvent{" +
                "boothId='" + boothId + '\'' +
                ", registration='" + registration + '\'' +
                ", imageId=" + imageId +
                ", timestamp=" + timestamp +
                '}';
    }
}
