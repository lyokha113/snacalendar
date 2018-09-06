package entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EventDTO implements Serializable {
    private int eventID;
    private EmployeeDTO creator;
    private EventTypeDTO eventType;
    private String eventTitle;
    private String eventContent;
    private CustomerDTO eventCustomer;
    private String eventCustomerOther;
    private LocalDateTime eventStartDate;
    private LocalDateTime eventEndDate;

    public EventDTO() {
    }

    public EventDTO(int eventID, EmployeeDTO creator, EventTypeDTO eventType, String eventTitle, String eventContent,
                    CustomerDTO eventCustomer, String eventCustomerOther,
                    LocalDateTime eventStartDate, LocalDateTime eventEndDate) {
        this.eventID = eventID;
        this.creator = creator;
        this.eventType = eventType;
        this.eventTitle = eventTitle;
        this.eventContent = eventContent;
        this.eventCustomer = eventCustomer;
        this.eventCustomerOther = eventCustomerOther;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
    }

    public EventDTO(EmployeeDTO creator, EventTypeDTO eventType, String eventTitle, String eventContent,
                    CustomerDTO eventCustomer, String eventCustomerOther,
                    LocalDateTime eventStartDate, LocalDateTime eventEndDate) {
        this.creator = creator;
        this.eventType = eventType;
        this.eventTitle = eventTitle;
        this.eventContent = eventContent;
        this.eventCustomer = eventCustomer;
        this.eventCustomerOther = eventCustomerOther;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
    }

    public EventDTO(String eventTitle, String eventContent,
                    CustomerDTO eventCustomer, String eventCustomerOther,
                    LocalDateTime eventStartDate, LocalDateTime eventEndDate) {
        this.eventTitle = eventTitle;
        this.eventContent = eventContent;
        this.eventCustomer = eventCustomer;
        this.eventCustomerOther = eventCustomerOther;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
    }


    // Check validate of event
    // Event which can be edit or delete must be have start date before current time at most 1 day
    // Creator event and current user to edit or delete must be same.
    // Event type must be same with request type (request from right page/servlet)
    public static boolean checkInvalidEvent(EventDTO event, int creator, int type) {
        return event.getEventStartDate().isBefore(LocalDate.now().minusDays(1).atTime(LocalTime.MAX))
                || event.getCreator().getAccount().getAccountID() != creator
                || event.getEventType().getEventTypeID() != type;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public EmployeeDTO getCreator() {
        return creator;
    }

    public void setCreator(EmployeeDTO creator) {
        this.creator = creator;
    }

    public EventTypeDTO getEventType() {
        return eventType;
    }

    public void setEventType(EventTypeDTO eventType) {
        this.eventType = eventType;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventContent() {
        return eventContent;
    }

    public void setEventContent(String eventDescription) {
        this.eventContent = eventContent;
    }

    public CustomerDTO getEventCustomer() {
        return eventCustomer;
    }

    public void setEventCustomer(CustomerDTO eventCustomer) {
        this.eventCustomer = eventCustomer;
    }

    public String getEventCustomerOther() {
        return eventCustomerOther;
    }

    public void setEventCustomerOther(String eventCustomerOther) {
        this.eventCustomerOther = eventCustomerOther;
    }

    public LocalDateTime getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(LocalDateTime eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public LocalDateTime getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(LocalDateTime eventEndDate) {
        this.eventEndDate = eventEndDate;
    }
}
