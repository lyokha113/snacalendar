package datamapping;

import java.io.Serializable;

/**
 * Mapping object for eventDTO to car reservation event base on requirement
 *
 */
public class CarReservation implements Serializable {
    private long start;
    private long end;
    private int eventID;

    public CarReservation() {
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }
}
