/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Yuu
 */
public class CarDTO implements Serializable {

    private int carID;
    private String carPlate;
    private String carBrand;
    private int carSlot;
    private LocalDateTime createdDate;

    public CarDTO() {
    }

    public CarDTO(int carID, String carPlate, String carBrand, int carSlot, LocalDateTime createdDate) {
        this.carID = carID;
        this.carPlate = carPlate;
        this.carBrand = carBrand;
        this.carSlot = carSlot;
        this.createdDate = createdDate;
    }

    public static CarDTO getCarDTO(String carPlate, String carBrand, String carSlot) {
        CarDTO dto = new CarDTO();
        dto.setCarPlate(carPlate);
        dto.setCarBrand(carBrand);
        dto.setCarSlot(Integer.parseInt(carSlot));
        return dto;
    }

    /**
     * @return the carID
     */
    public int getCarID() {
        return carID;
    }

    /**
     * @param carID the carID to set
     */
    public void setCarID(int carID) {
        this.carID = carID;
    }

    /**
     * @return the carPlate
     */
    public String getCarPlate() {
        return carPlate;
    }

    /**
     * @param carPlate the carPlate to set
     */
    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }

    /**
     * @return the carBrand
     */
    public String getCarBrand() {
        return carBrand;
    }

    /**
     * @param carBrand the carBrand to set
     */
    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    /**
     * @return the carSlot
     */
    public int getCarSlot() {
        return carSlot;
    }

    /**
     * @param carSlot the carSlot to set
     */
    public void setCarSlot(int carSlot) {
        this.carSlot = carSlot;
    }

    /**
     * @return the createdDate
     */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

}
