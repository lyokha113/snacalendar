/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author Yuu
 */
public class HolidayDTO implements Serializable {

    private int holidayID;
    private String holidayName;
    private LocalDate holidayStartDate;
    private LocalDate holidayEndDate;
    private boolean holidayVacation;

    public HolidayDTO() {
    }

    public HolidayDTO(int holidayID, String holidayName, LocalDate holidayStartDate, LocalDate holidayEndDate, boolean holidayVacation) {
        this.holidayID = holidayID;
        this.holidayName = holidayName;
        this.holidayStartDate = holidayStartDate;
        this.holidayEndDate = holidayEndDate;
        this.holidayVacation = holidayVacation;
    }

    public static HolidayDTO getHolidayDTO(String holidayName, LocalDate start, LocalDate end, boolean vacation) {
        HolidayDTO dto = new HolidayDTO();
        dto.setHolidayName(holidayName);
        dto.setHolidayStartDate(start);
        dto.setHolidayEndDate(end);
        dto.setHolidayVacation(vacation);
        return dto;
    }

    /**
     * @return the holidayID
     */
    public int getHolidayID() {
        return holidayID;
    }

    /**
     * @param holidayID the holidayID to set
     */
    public void setHolidayID(int holidayID) {
        this.holidayID = holidayID;
    }

    /**
     * @return the holidayName
     */
    public String getHolidayName() {
        return holidayName;
    }

    /**
     * @param holidayName the holidayName to set
     */
    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }

    /**
     * @return the holidayStartDate
     */
    public LocalDate getHolidayStartDate() {
        return holidayStartDate;
    }

    /**
     * @param holidayStartDate the holidayStartDate to set
     */
    public void setHolidayStartDate(LocalDate holidayStartDate) {
        this.holidayStartDate = holidayStartDate;
    }

    /**
     * @return the holidayEndDate
     */
    public LocalDate getHolidayEndDate() {
        return holidayEndDate;
    }

    /**
     * @param holidayEndDate the holidayEndDate to set
     */
    public void setHolidayEndDate(LocalDate holidayEndDate) {
        this.holidayEndDate = holidayEndDate;
    }

    public boolean isHolidayVacation() {
        return holidayVacation;
    }

    public void setHolidayVacation(boolean holidayVacation) {
        this.holidayVacation = holidayVacation;
    }

}
