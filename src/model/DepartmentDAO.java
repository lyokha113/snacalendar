/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import entity.DepartmentDTO;
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
public class DepartmentDAO implements Serializable {

    private Connection conn = null;
    private PreparedStatement preStm = null;
    private ResultSet rs = null;

    public DepartmentDAO() {
    }

    public List<DepartmentDTO> getAll() throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT * FROM tblDepartment";
            preStm = conn.prepareStatement(sql);
            rs = preStm.executeQuery();
            List<DepartmentDTO> result = new ArrayList<>();
            while (rs.next()) {
                int departmentID = rs.getInt("DepartmentID");
                String name = rs.getString("DepartmentName");
                LocalDateTime createdDate = rs.getTimestamp("CreatedDate").toLocalDateTime();

                DepartmentDTO dto = new DepartmentDTO(departmentID, name, createdDate);
                result.add(dto);
            }
            return result.isEmpty() ? null : result;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    // Get all department with amount of employees for each
    public Map<DepartmentDTO, Integer> getAllWithAmount() throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT D.*, " +
                    "(SELECT count(*) FROM tblEmployee WHERE D.DepartmentID = Department GROUP BY Department) AS Amount " +
                    "FROM tblDepartment AS D ";

            preStm = conn.prepareStatement(sql);
            rs = preStm.executeQuery();
            Map<DepartmentDTO, Integer> result = new HashMap<>();
            while (rs.next()) {
                int departmentID = rs.getInt("DepartmentID");
                String name = rs.getString("DepartmentName");
                int amount = rs.getInt("Amount");
                LocalDateTime createdDate = rs.getTimestamp("CreatedDate").toLocalDateTime();

                DepartmentDTO dto = new DepartmentDTO(departmentID, name, createdDate);
                result.put(dto, amount);
            }
            return result.isEmpty() ? null : result;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public int addDepartment(DepartmentDTO dto) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "INSERT INTO tblDepartment(DepartmentName) VALUES(?)";
            preStm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preStm.setString(1, dto.getDepartmentName());
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

    public boolean updateDepartment(DepartmentDTO dto) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "UPDATE tblDepartment SET DepartmentName = ? WHERE DepartmentID = ? ";
            preStm = conn.prepareStatement(sql);
            preStm.setString(1, dto.getDepartmentName());
            preStm.setInt(2, dto.getDepartmentID());
            return preStm.executeUpdate() > 0;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public boolean deleteDepartment(int departmentID) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "DELETE FROM tblDepartment WHERE DepartmentID = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, departmentID);
            return (preStm.executeUpdate() > 0);
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public int countEmployees(int departmentID) throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT count(*) AS Amount " +
                    "FROM tblEmployee AS E, tblDepartment AS D " +
                    "WHERE D.DepartmentID = E.Department " +
                    "AND D.DepartmentID = ? " +
                    "GROUP BY Department";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, departmentID);
            rs = preStm.executeQuery();
            return rs.next() ? rs.getInt("Amount") : 0;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }
}
