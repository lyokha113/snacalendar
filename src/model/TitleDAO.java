/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import entity.PermissionDTO;
import entity.TitleDTO;
import utils.DbUtil;

import javax.naming.NamingException;
import java.io.Serializable;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuu
 */
public class TitleDAO implements Serializable {

    private Connection conn = null;
    private PreparedStatement preStm = null;
    private ResultSet rs = null;

    public TitleDAO() {
    }

    public List<TitleDTO> getAll() throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT T.*, P.PermissionName " +
                    "FROM tblTitle AS T, tblPermission as P " +
                    "WHERE T.Permission = P.PermissionID";
            preStm = conn.prepareStatement(sql);
            rs = preStm.executeQuery();

            List<TitleDTO> result = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("TitleID");
                String name = rs.getString("TitleName");
                LocalDateTime createdDate = rs.getTimestamp("CreatedDate").toLocalDateTime();

                int permissionID = rs.getInt("Permission");
                String permissionName = rs.getString("PermissionName");
                PermissionDTO permission = new PermissionDTO(permissionID, permissionName);

                TitleDTO dto = new TitleDTO(id, name, permission, createdDate);
                result.add(dto);
            }
            return result.isEmpty() ? null : result;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    // Get all title with amount of employees of each
    public Map<TitleDTO, Integer> getAllWithAmount() throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT T.*, P.PermissionName, " +
                    "(SELECT count(*) FROM tblEmployee WHERE T.TitleID = Title GROUP BY Title) AS Amount " +
                    "FROM tblTitle AS T, tblPermission as P " +
                    "WHERE T.Permission = P.PermissionID";
            preStm = conn.prepareStatement(sql);
            rs = preStm.executeQuery();

            Map<TitleDTO, Integer> result = new HashMap<>();
            while (rs.next()) {
                int id = rs.getInt("TitleID");
                String name = rs.getString("TitleName");
                LocalDateTime createdDate = rs.getTimestamp("CreatedDate").toLocalDateTime();

                int permissionID = rs.getInt("Permission");
                String permissionName = rs.getString("PermissionName");
                PermissionDTO permission = new PermissionDTO(permissionID, permissionName);

                TitleDTO dto = new TitleDTO(id, name, permission, createdDate);
                int amount = rs.getInt("Amount");
                result.put(dto, amount);
            }
            return result.isEmpty() ? null : result;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public int addTitle(TitleDTO dto) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "INSERT INTO tblTitle(TitleName, Permission) VALUES(?,?)";
            preStm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preStm.setString(1, dto.getTitleName());
            preStm.setInt(2, dto.getPermission().getPermissionID());
            preStm.executeUpdate();

            rs = preStm.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
        return -1;
    }

    public boolean updateTitle(TitleDTO dto) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "UPDATE tblTitle SET TitleName = ?, Permission = ? WHERE TitleID = ? ";
            preStm = conn.prepareStatement(sql);
            preStm.setString(1, dto.getTitleName());
            preStm.setInt(2, dto.getPermission().getPermissionID());
            preStm.setInt(3, dto.getTitleID());
            return preStm.executeUpdate() > 0;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public boolean deleteTitle(int titleID) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "DELETE FROM tblTitle WHERE TitleID = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, titleID);
            return (preStm.executeUpdate() > 0);
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public int countEmployees(int titleID) throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT count(*) AS Amount " +
                    "FROM tblEmployee AS E, tblTitle AS T " +
                    "WHERE T.TitleID = E.Title " +
                    "AND T.TitleID = ? " +
                    "GROUP BY Title";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, titleID);
            rs = preStm.executeQuery();
            return rs.next() ? rs.getInt("Amount") : 0;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public int getPermission(int titleID) throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT Permission FROM tblTitle WHERE TitleID = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, titleID);
            rs = preStm.executeQuery();
            return rs.next() ? rs.getInt("Permission") : 0;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }
}
