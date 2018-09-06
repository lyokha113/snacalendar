package entity;

import java.io.Serializable;
import java.util.List;

public class EventAttendeeDTO implements Serializable {
    private List<Integer> listEmpID;
    private int eventID;

    public EventAttendeeDTO() {
    }

    public EventAttendeeDTO(List<Integer> listEmpID, int eventID) {
        this.listEmpID = listEmpID;
        this.eventID = eventID;
    }

    public List<Integer> getListEmpID() {
        return listEmpID;
    }

    public void setListEmpID(List<Integer> listEmpID) {
        this.listEmpID = listEmpID;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }
}
