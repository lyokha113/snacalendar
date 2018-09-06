/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import entity.AccountDTO;
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
 * @author yuu
 */
public class AccountDAO implements Serializable {

    private Connection conn = null;
    private PreparedStatement preStm = null;
    private ResultSet rs = null;

    public AccountDAO() {
    }

    public AccountDTO checkLogin(String email, String password) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "SELECT * FROM tblAccount WHERE Email = ? AND Password = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setString(1, email);
            preStm.setString(2, password);
            rs = preStm.executeQuery();
            if (rs.next()) {
                int accountID = rs.getInt("AccountID");
                boolean admin = rs.getBoolean("Admin");
                boolean active = rs.getBoolean("Active");
                LocalDate joinedDate = rs.getDate("JoinedDate").toLocalDate();
                return new AccountDTO(accountID, email, admin, active, joinedDate);
            } else {
                return null;
            }
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public int getAccountIDByEmail(String email) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "SELECT AccountID FROM tblAccount WHERE Email = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setString(1, email);
            rs = preStm.executeQuery();
            return rs.next() ? rs.getInt("AccountID") : 0;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public boolean changeActive(int accountID, boolean active) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "UPDATE tblAccount SET Active = ? WHERE AccountID = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setBoolean(1, active);
            preStm.setInt(2, accountID);
            return preStm.executeUpdate() > 0;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public boolean changePassword(String token, String password) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "UPDATE tblAccount AS A, tblRecoveryToken AS R "
                    + "SET Password = ? WHERE A.AccountID = R.AccountID "
                    + "AND R.RecoveryToken = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setString(1, password);
            preStm.setString(2, token);
            return preStm.executeUpdate() > 0;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public boolean changePassword(int accountID, String password) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "UPDATE tblAccount SET Password = ? WHERE AccountID = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setString(1, password);
            preStm.setInt(2, accountID);
            return preStm.executeUpdate() > 0;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public boolean changePassword(int accountID, String newPass, String currentPass) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "UPDATE tblAccount SET Password = ? WHERE AccountID = ? AND Password = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setString(1, newPass);
            preStm.setInt(2, accountID);
            preStm.setString(3, currentPass);
            return preStm.executeUpdate() > 0;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }
}
