/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import entity.HolidayDTO;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import utils.DbUtil;

import javax.naming.NamingException;

/**
 *
 * @author Yuu
 */
public class HolidayDAO implements Serializable {

    private Connection conn = null;
    private PreparedStatement preStm = null;
    private ResultSet rs = null;

    public HolidayDAO() {
    }
    
    public List<HolidayDTO> getAll() throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT * FROM tblHoliday ";
            preStm = conn.prepareStatement(sql);
            rs = preStm.executeQuery();

            List<HolidayDTO> result = new ArrayList<>();
            while (rs.next()) {
                int holidayID = rs.getInt("HolidayID");
                String holidayName = rs.getString("HolidayName");
                LocalDate holidayStartDate = rs.getDate("HolidayStartDate").toLocalDate();
                LocalDate holidayEndDate = rs.getDate("HolidayEndDate").toLocalDate();
                boolean holidayVacation = rs.getBoolean("HolidayVacation");

                HolidayDTO dto = new HolidayDTO(holidayID, holidayName, holidayStartDate, holidayEndDate, holidayVacation);
                result.add(dto);
            }
            return result.isEmpty() ? null : result;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }
    
    public int addHoliday(HolidayDTO dto) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "INSERT INTO tblHoliday(HolidayName, HolidayStartDate, HolidayEndDate, HolidayVacation) VALUES(?,?,?,?)";
            preStm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preStm.setString(1, dto.getHolidayName());
            preStm.setObject(2, dto.getHolidayStartDate());
            preStm.setObject(3, dto.getHolidayEndDate());
            preStm.setBoolean(4, dto.isHolidayVacation());
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

    public boolean updateHoliday(HolidayDTO dto) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "UPDATE tblHoliday SET HolidayName = ?, HolidayStartDate = ?, HolidayEndDate = ?, HolidayVacation = ? WHERE HolidayID = ? ";
            preStm = conn.prepareStatement(sql);
            preStm.setString(1, dto.getHolidayName());
            preStm.setObject(2, dto.getHolidayStartDate());
            preStm.setObject(3, dto.getHolidayEndDate());
            preStm.setBoolean(4, dto.isHolidayVacation());
            preStm.setInt(5, dto.getHolidayID());
            return preStm.executeUpdate() > 0;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public boolean deleteHoliday(int holidayID) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "DELETE FROM tblHoliday WHERE HolidayID = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, holidayID);
            return (preStm.executeUpdate() > 0);
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }
}
