/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import datamapping.CarReservation;
import datamapping.EventTypeEnum;
import entity.EventDTO;
import utils.DbUtil;

import javax.naming.NamingException;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yuu
 */
public class EventCarDAO implements Serializable {

    private Connection conn = null;
    private PreparedStatement preStm = null;
    private ResultSet rs = null;

    public EventCarDAO() {
    }

    public Map<Integer, List<CarReservation>> getAll() throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT  E.EventID, E.EventStartDate, E.EventEndDate, CA.CarID " +
                    "FROM tblEvent AS E , tblEventCar AS EC " +
                    "LEFT JOIN tblCar AS CA ON CA.CarID = EC.CarID " +
                    "WHERE E.EventID = EC.EventID " +
                    "AND E.EventEndDate > NOW() " +
                    "AND (E.EventTypeID = ? OR E.EventTypeID = ?)";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, EventTypeEnum.CUSTOMER_CARE.getEventTypeID());
            preStm.setInt(2, EventTypeEnum.CAR_RESERVATION.getEventTypeID());
            rs = preStm.executeQuery();

            Map<Integer, List<CarReservation>> result = new HashMap<>();
            while (rs.next()) {

                CarReservation cr = new CarReservation();
                cr.setEventID(rs.getInt("EventID"));
                cr.setStart(rs.getTimestamp("EventStartDate").getTime());
                cr.setEnd(rs.getTimestamp("EventEndDate").getTime());

                int carID = rs.getInt("CarID");
                List<CarReservation> list = result.get(carID);
                list = list == null ? new ArrayList<>() : list;
                list.add(cr);

                result.put(carID, list);
            }
            return result.isEmpty() ? null : result;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public int addEventCar(EventDTO event, int carID) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            conn.setAutoCommit(false);

            String addEvent = "INSERT INTO tblEvent(CreatorID, EventTypeID, EventTitle, EventContent, "
                    + "EventCustomer, EventCustomerOther, EventStartDate, EventEndDate) "
                    + "VALUES(?,?,?,?,?,?,?,?)";
            String addEventCar = "INSERT INTO tblEventCar(EventID, CarID) VALUES(?,?)";

            PreparedStatement addEventPreStm = conn.prepareStatement(addEvent, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement addEventCarPreStm = conn.prepareStatement(addEventCar);

            addEventPreStm.setInt(1, event.getCreator().getAccount().getAccountID());
            addEventPreStm.setInt(2, event.getEventType().getEventTypeID());
            addEventPreStm.setString(3, event.getEventTitle());
            addEventPreStm.setString(4, event.getEventContent());
            if (event.getEventCustomer() != null) {
                addEventPreStm.setInt(5, event.getEventCustomer().getCustomerID());
            } else {
                addEventPreStm.setNull(5, Types.INTEGER);
            }
            addEventPreStm.setString(6, event.getEventCustomerOther());
            addEventPreStm.setObject(7, event.getEventStartDate());
            addEventPreStm.setObject(8, event.getEventEndDate());
            addEventPreStm.executeUpdate();

            rs = addEventPreStm.getGeneratedKeys();
            if (rs.next()) {
                int eventID = rs.getInt(1);
                addEventCarPreStm.setInt(1, eventID);
                addEventCarPreStm.setInt(2, carID);
                if (addEventCarPreStm.executeUpdate() > 0) {
                    conn.commit();
                    return eventID;
                }
            }

            return -1;
        } catch (SQLException e) {
            conn.rollback();
            throw new SQLException(e);
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public boolean updateEventCar(EventDTO event, int carID) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            conn.setAutoCommit(false);

            String updateEvent = "UPDATE tblEvent SET EventTitle = ?, EventContent = ?, " +
                    "EventCustomer = ?, EventCustomerOther = ?, " +
                    "EventStartDate = ?, EventEndDate = ? " +
                    "WHERE EventID = ? ";
            String updateEventCar = "INSERT INTO tblEventCar(EventID, CarID) VALUES (?,?) " +
                    "ON DUPLICATE KEY UPDATE CarID = ?";
            PreparedStatement updateEventPreStm = conn.prepareStatement(updateEvent);
            PreparedStatement updateEventCarPreStm = conn.prepareStatement(updateEventCar);

            updateEventPreStm.setString(1, event.getEventTitle());
            updateEventPreStm.setString(2, event.getEventContent());
            if (event.getEventCustomer() != null) {
                updateEventPreStm.setInt(3, event.getEventCustomer().getCustomerID());
            } else {
                updateEventPreStm.setNull(3, Types.INTEGER);
            }
            updateEventPreStm.setString(4, event.getEventCustomerOther());
            updateEventPreStm.setObject(5, event.getEventStartDate());
            updateEventPreStm.setObject(6, event.getEventEndDate());
            updateEventPreStm.setInt(7, event.getEventID());

            if (updateEventPreStm.executeUpdate() > 0) {
                updateEventCarPreStm.setInt(1, event.getEventID());
                updateEventCarPreStm.setInt(2, carID);
                updateEventCarPreStm.setInt(3, carID);
                if (updateEventCarPreStm.executeUpdate() > 0) {
                    conn.commit();
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            conn.rollback();
            throw new SQLException(e);
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public void deleteEventCar(int eventID) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "DELETE FROM tblEventCar WHERE EventID = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, eventID);
            preStm.executeUpdate();
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public void deleteEventCarReservationEmpty() throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "DELETE FROM tblEvent " +
                    "WHERE EventID NOT IN (SELECT EventID FROM tblEventCar) " +
                    "AND EventTypeID = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, EventTypeEnum.CAR_RESERVATION.getEventTypeID());
            preStm.executeUpdate();
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }
}
