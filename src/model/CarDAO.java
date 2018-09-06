/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import entity.CarDTO;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import utils.DbUtil;

import javax.naming.NamingException;

/**
 *
 * @author Yuu
 */
public class CarDAO implements Serializable {

    private Connection conn = null;
    private PreparedStatement preStm = null;
    private ResultSet rs = null;

    public CarDAO() {
    }

    public List<CarDTO> getAll() throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT * FROM tblCar ";
            preStm = conn.prepareStatement(sql);
            rs = preStm.executeQuery();

            List<CarDTO> result = new ArrayList<>();
            while (rs.next()) {
                int carID = rs.getInt("carID");
                String carPlate = rs.getString("carPlate");
                String carBrand = rs.getString("carBrand");
                int carSlot = rs.getInt("carSlot");
                LocalDateTime createdDate = rs.getTimestamp("CreatedDate").toLocalDateTime();

                CarDTO dto = new CarDTO(carID, carPlate, carBrand, carSlot, createdDate);
                result.add(dto);
            }
            return result.isEmpty() ? null : result;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }
    
    public int addCar(CarDTO dto) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "INSERT INTO tblCar(CarPlate, CarBrand, CarSlot) VALUES(?,?,?)";
            preStm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preStm.setString(1, dto.getCarPlate());
            preStm.setString(2, dto.getCarBrand());
            preStm.setInt(3, dto.getCarSlot());
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

    public boolean updateCar(CarDTO dto) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "UPDATE tblCar SET CarPlate = ?, CarBrand = ?, CarSlot = ? WHERE CarID = ? ";
            preStm = conn.prepareStatement(sql);
            preStm.setString(1, dto.getCarPlate());
            preStm.setString(2, dto.getCarBrand());
            preStm.setInt(3, dto.getCarSlot());
            preStm.setInt(4, dto.getCarID());
            return preStm.executeUpdate() > 0;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public boolean deleteCar(int carID) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "DELETE FROM tblCar WHERE CarID = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, carID);
            return (preStm.executeUpdate() > 0);
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }
}
