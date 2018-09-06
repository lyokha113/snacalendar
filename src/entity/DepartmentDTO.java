/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author yuu
 */
public class DepartmentDTO implements Serializable {

    private int departmentID;
    private String departmentName;
    private LocalDateTime createdDate;

    public DepartmentDTO() {
    }

    public DepartmentDTO(int departmentID) {
        this.departmentID = departmentID;
    }

    public DepartmentDTO(int departmentID, String departmentName, LocalDateTime createdDate) {
        this.departmentID = departmentID;
        this.departmentName = departmentName;
        this.createdDate = createdDate;
    }

    public static DepartmentDTO getDepartmentDTO(String depName) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setDepartmentName(depName);
        return dto;
    }

    /**
     * @return the departmentID
     */
    public int getDepartmentID() {
        return departmentID;
    }

    /**
     * @param departmentID the departmentID to set
     */
    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }

    /**
     * @return the departmentName
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * @param departmentName the departmentName to set
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepartmentDTO that = (DepartmentDTO) o;
        return getDepartmentID() == that.getDepartmentID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDepartmentID());
    }
}
