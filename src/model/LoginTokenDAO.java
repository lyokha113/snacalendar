/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import entity.AccountDTO;
import entity.LoginTokenDTO;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import utils.DbUtil;

import javax.naming.NamingException;

/**
 *
 * @author Yuu
 */
public class LoginTokenDAO implements Serializable {

    private Connection conn = null;
    private PreparedStatement preStm = null;
    private ResultSet rs = null;

    public LoginTokenDAO() {
    }

    public AccountDTO checkLoginCookie(String loginToken) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "SELECT A.AccountID, A.Admin, A.Active, A.JoinedDate, A.Email "
                    + "FROM tblLoginToken AS L, tblAccount AS A "
                    + "WHERE LoginToken = ? AND A.AccountID = L.AccountID";
            preStm = conn.prepareStatement(sql);
            preStm.setString(1, loginToken);
            rs = preStm.executeQuery();
            if (rs.next()) {
                int accountID = rs.getInt("AccountID");
                boolean admin = rs.getBoolean("Admin");
                boolean active = rs.getBoolean("Active");
                String email = rs.getString("Email");
                LocalDate joinedDate = rs.getDate("JoinedDate").toLocalDate();
                return new AccountDTO(accountID, email, admin, active, joinedDate);
            } else {
                return null;
            }
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public void addLoginToken(LoginTokenDTO token) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "INSERT INTO tblLoginToken (AccountID, LoginToken) VALUES (?,?) "
                    + "ON DUPLICATE KEY UPDATE LoginToken = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, token.getAccountID());
            preStm.setString(2, token.getLoginToken());
            preStm.setString(3, token.getLoginToken());
            preStm.executeUpdate();

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public void deleteLoginToken(int accountID) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "DELETE FROM tblLoginToken WHERE AccountID = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, accountID);
            preStm.executeUpdate();
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

}
