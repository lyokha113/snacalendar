package entity;

import datamapping.AnnualStatusEnum;
import datamapping.PermissionEnum;

import java.io.Serializable;

public class EventAnnualDTO implements Serializable {

    private EventDTO event;
    private AnnualStatusEnum status;

    public EventAnnualDTO() {
    }

    public EventAnnualDTO(EventDTO event, AnnualStatusEnum status) {
        this.event = event;
        this.status = status;
    }

    // Check validate of event
    // Event status must be aprroving for new event if creator is normal user
    public static boolean checkInvalidEvent(AnnualStatusEnum status, PermissionEnum permission) {
        return permission != PermissionEnum.SUPER_USER && status != AnnualStatusEnum.APPROVING;
    }

    public EventDTO getEvent() {
        return event;
    }

    public void setEvent(EventDTO event) {
        this.event = event;
    }

    public AnnualStatusEnum getStatus() {
        return status;
    }

    public void setStatus(AnnualStatusEnum status) {
        this.status = status;
    }
}
