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
public class TitleDTO implements Serializable {

    private int titleID;
    private String titleName;
    private PermissionDTO permission;
    private LocalDateTime createdDate;

    public TitleDTO() {
    }

    public TitleDTO(int titleID) {
        this.titleID = titleID;

    }

    public TitleDTO(int titleID, String titleName, PermissionDTO permission, LocalDateTime createdDate) {
        this.titleID = titleID;
        this.titleName = titleName;
        this.permission = permission;
        this.createdDate = createdDate;
    }

    /**
     * @return the titleID
     */
    public int getTitleID() {
        return titleID;
    }

    /**
     * @param titleID the titleID to set
     */
    public void setTitleID(int titleID) {
        this.titleID = titleID;
    }

    /**
     * @return the titleName
     */
    public String getTitleName() {
        return titleName;
    }

    /**
     * @param titleName the titleName to set
     */
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public PermissionDTO getPermission() {
        return permission;
    }

    public void setPermission(PermissionDTO permission) {
        this.permission = permission;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public static TitleDTO getTitleDTO(String titleName, String titlePer) {
        PermissionDTO permission = new PermissionDTO();
        permission.setPermissionID(Integer.parseInt(titlePer));

        TitleDTO dto = new TitleDTO();
        dto.setTitleName(titleName);
        dto.setPermission(permission);
        return dto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TitleDTO titleDTO = (TitleDTO) o;
        return getTitleID() == titleDTO.getTitleID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitleID());
    }
}
