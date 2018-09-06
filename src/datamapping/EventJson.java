package datamapping;

import entity.*;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Mapping json object for eventDTO to return to client
 *
 */
public class EventJson implements Serializable {
    private int id;
    private String content;
    private String title;
    private long start;
    private long end;
    private EventTypeDTO type;
    private EmployeeDTO creator;
    private CustomerDTO customer;
    private String customerOther;
    private CarDTO car;
    private int annualLeave;

    public EventJson() {
    }

    public static EventJson parseToJson(EventAnnualDTO event) {
        EventJson json = new EventJson();
        json.setId(event.getEvent().getEventID());
        json.setType(event.getEvent().getEventType());
        json.setCreator(event.getEvent().getCreator());
        json.setTitle(event.getEvent().getEventTitle());
        json.setContent(event.getEvent().getEventContent());
        json.setStart(Timestamp.valueOf(event.getEvent().getEventStartDate()).getTime());
        json.setEnd(Timestamp.valueOf(event.getEvent().getEventEndDate()).getTime());
        json.setCustomer(event.getEvent().getEventCustomer());
        json.setCustomerOther(event.getEvent().getEventCustomerOther());
        json.setAnnualLeave(event.getStatus().getStatus());
        return json;
    }

    public static EventJson parseToJson(EventCarDTO event) {
        EventJson json = new EventJson();
        json.setId(event.getEvent().getEventID());
        json.setType(event.getEvent().getEventType());
        json.setCreator(event.getEvent().getCreator());
        json.setTitle(event.getEvent().getEventTitle());
        json.setContent(event.getEvent().getEventContent());
        json.setStart(Timestamp.valueOf(event.getEvent().getEventStartDate()).getTime());
        json.setEnd(Timestamp.valueOf(event.getEvent().getEventEndDate()).getTime());
        json.setCustomer(event.getEvent().getEventCustomer());
        json.setCustomerOther(event.getEvent().getEventCustomerOther());
        json.setCar(event.getCar());
        return json;
    }

    public static EventJson parseToJson(EventDTO event) {
        EventJson json = new EventJson();
        json.setId(event.getEventID());
        json.setType(event.getEventType());
        json.setCreator(event.getCreator());
        json.setTitle(event.getEventTitle());
        json.setContent(event.getEventContent());
        json.setStart(Timestamp.valueOf(event.getEventStartDate()).getTime());
        json.setEnd(Timestamp.valueOf(event.getEventEndDate()).getTime());
        json.setCustomer(event.getEventCustomer());
        json.setCustomerOther(event.getEventCustomerOther());
        return json;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public EmployeeDTO getCreator() {
        return creator;
    }

    public void setCreator(EmployeeDTO creator) {
        this.creator = creator;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public String getCustomerOther() {
        return customerOther;
    }

    private void setCustomerOther(String customerOther) {
        this.customerOther = customerOther;
    }

    public EventTypeDTO getType() {
        return type;
    }

    public void setType(EventTypeDTO type) {
        this.type = type;
    }

    public CarDTO getCar() {
        return car;
    }

    public void setCar(CarDTO car) {
        this.car = car;
    }

    public int getAnnualLeave() {
        return annualLeave;
    }

    public void setAnnualLeave(int annualLeave) {
        this.annualLeave = annualLeave;
    }
}
