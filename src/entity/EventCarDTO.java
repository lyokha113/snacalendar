package entity;

import java.io.Serializable;

public class EventCarDTO implements Serializable {

    private EventDTO event;
    private CarDTO car;

    public EventCarDTO() {
    }

    public EventCarDTO(EventDTO event, CarDTO car) {
        this.event = event;
        this.car = car;
    }

    public EventDTO getEvent() {
        return event;
    }

    public void setEvent(EventDTO event) {
        this.event = event;
    }

    public CarDTO getCar() {
        return car;
    }

    public void setCar(CarDTO car) {
        this.car = car;
    }
}
