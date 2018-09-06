package datamapping;

import java.util.Arrays;

/**
 * Enum for list of event type base on requirement
 *
 */
public enum EventTypeEnum {

    CUSTOMER_CARE(1, "customercare.jsp"),
    TRAINING(2, "training.jsp"),
    BIZ_TRIP(3, "biztrip.jsp"),
    CAR_RESERVATION(4, "carreservation.jsp"),
    ANNUAL_LEAVE(5, "annualleave.jsp");

    private int eventTypeID;

    private String eventPage;

    EventTypeEnum(int eventTypeID, String eventPage) {
        this.eventTypeID = eventTypeID;
        this.eventPage = eventPage;
    }

    public int getEventTypeID() {
        return eventTypeID;
    }

    public String getEventPage() {
        return eventPage;
    }

    public static EventTypeEnum getEventType(int eventTypeID) {
        return Arrays.stream(EventTypeEnum.values()).filter(eventType -> eventType.eventTypeID == eventTypeID)
                .findFirst().orElse(null);
    }

}
