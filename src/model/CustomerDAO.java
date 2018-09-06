/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import entity.CustomerDTO;
import utils.DbUtil;

import javax.naming.NamingException;
import java.io.Serializable;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yuu
 */
public class CustomerDAO implements Serializable {

    private Connection conn = null;
    private PreparedStatement preStm = null;
    private ResultSet rs = null;

    public CustomerDAO() {
    }

    public List<CustomerDTO> getAll() throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT * FROM tblCustomer ";
            preStm = conn.prepareStatement(sql);
            rs = preStm.executeQuery();

            List<CustomerDTO> result = new ArrayList<>();
            while (rs.next()) {
                int customerID = rs.getInt("CustomerID");
                String customerName = rs.getString("CustomerName");
                String customerAddress = rs.getString("CustomerAddress");
                LocalDateTime createdDate = rs.getTimestamp("CreatedDate").toLocalDateTime();

                CustomerDTO dto = new CustomerDTO(customerID, customerName, customerAddress, createdDate);
                result.add(dto);
            }
            return result.isEmpty() ? null : result;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public boolean deleteCustomer(int customerID) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "DELETE FROM tblCustomer WHERE customerID = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, customerID);
            return (preStm.executeUpdate() > 0);
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public int addCustomer(CustomerDTO dto) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "INSERT INTO tblCustomer(CustomerName, CustomerAddress) VALUES(?,?)";
            preStm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preStm.setString(1, dto.getCustomerName());
            preStm.setString(2, dto.getCustomerAddress());
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

    public boolean updateCustomer(CustomerDTO dto) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "UPDATE tblCustomer SET CustomerName = ?, CustomerAddress = ? WHERE CustomerID = ? ";
            preStm = conn.prepareStatement(sql);
            preStm.setString(1, dto.getCustomerName());
            preStm.setString(2, dto.getCustomerAddress());
            preStm.setInt(3, dto.getCustomerID());
            return preStm.executeUpdate() > 0;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }
}
