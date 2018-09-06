package entity;

import java.io.Serializable;

public class EventTypeDTO implements Serializable {

    private int eventTypeID;
    private String eventTypeName;

    private DepartmentDTO department;

    public EventTypeDTO() {
    }

    public EventTypeDTO(int eventTypeID, String eventTypeName, DepartmentDTO department) {
        this.eventTypeID = eventTypeID;
        this.eventTypeName = eventTypeName;
        this.department = department;
    }

    public int getEventTypeID() {
        return eventTypeID;
    }

    public void setEventTypeID(int eventTypeID) {
        this.eventTypeID = eventTypeID;
    }

    public String getEventTypeName() {
        return eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    public DepartmentDTO getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDTO department) {
        this.department = department;
    }
}

