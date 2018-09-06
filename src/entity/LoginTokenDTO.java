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
public class LoginTokenDTO implements Serializable {

    private int accountID;
    private String loginToken;

    public LoginTokenDTO() {
    }

    public LoginTokenDTO(int accountID, String loginToken) {
        this.accountID = accountID;
        this.loginToken = loginToken;
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
     * @return the loginToken
     */
    public String getLoginToken() {
        return loginToken;
    }

    /**
     * @param loginToken the loginToken to set
     */
    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }
}
