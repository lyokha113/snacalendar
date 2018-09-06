/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import entity.RecoveryTokenDTO;
import utils.DbUtil;

import javax.naming.NamingException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Yuu
 */
public class RecoveryTokenDAO implements Serializable {

    private Connection conn = null;
    private PreparedStatement preStm = null;
    private ResultSet rs = null;

    public RecoveryTokenDAO() {
    }

    public boolean addRecoveryToken(RecoveryTokenDTO token) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "INSERT INTO tblRecoveryToken (AccountID, RecoveryToken) VALUES (?,?) "
                    + "ON DUPLICATE KEY UPDATE RecoveryToken = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, token.getAccountID());
            preStm.setString(2, token.getRecoveryToken());
            preStm.setString(3, token.getRecoveryToken());
            return preStm.executeUpdate() > 0;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public void deleteRecoveryToken(String token) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "DELETE FROM tblRecoveryToken WHERE RecoveryToken = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setString(1, token);
            preStm.executeUpdate();
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }
}
