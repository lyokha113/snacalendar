/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author yuu
 */
public class AccountDTO implements Serializable {

    private int accountID;
    private String email;
    private String password;
    private boolean admin;
    private boolean active;
    private LocalDate joinedDate;

    public AccountDTO() {
    }

    public AccountDTO(int accountID, String email, boolean admin, boolean active, LocalDate joinedDate) {
        this.accountID = accountID;
        this.email = email;
        this.admin = admin;
        this.active = active;
        this.joinedDate = joinedDate;
    }

    public static AccountDTO getAccountDTO(String empEmail, LocalDate empJD, boolean empAdmin) {
        AccountDTO account = new AccountDTO();
        account.setEmail(empEmail);
        account.setAdmin(empAdmin);
        account.setJoinedDate(empJD);
        return account;
    }

    /**
     * @return the accountID
     */
    public int getAccountID() {
        return accountID;
    }

    /**
     * @param accountID the accountID to set
     */
    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the admin
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * @param admin the admin to set
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the joinedDate
     */
    public LocalDate getJoinedDate() {
        return joinedDate;
    }

    /**
     * @param joinedDate the joinedDate to set
     */
    public void setJoinedDate(LocalDate joinedDate) {
        this.joinedDate = joinedDate;
    }


}
