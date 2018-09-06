/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;

/**
 *
 * @author Yuu
 */
public class RecoveryTokenDTO implements Serializable {

    private int accountID;
    private String recoveryToken;

    public RecoveryTokenDTO() {
    }

    public RecoveryTokenDTO(int accountID, String recoveryToken) {
        this.accountID = accountID;
        this.recoveryToken = recoveryToken;
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
     * @return the recoveryToken
     */
    public String getRecoveryToken() {
        return recoveryToken;
    }

    /**
     * @param recoveryToken the recoveryToken to set
     */
    public void setRecoveryToken(String recoveryToken) {
        this.recoveryToken = recoveryToken;
    }

}
