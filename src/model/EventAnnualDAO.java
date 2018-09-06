/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import datamapping.AnnualStatusEnum;
import datamapping.EventTypeEnum;
import entity.*;
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
public class EventAnnualDAO implements Serializable {

    private Connection conn = null;
    private PreparedStatement preStm = null;
    private ResultSet rs = null;

    public EventAnnualDAO() {
    }

    public int addEventAnnual(EventDTO event, int status) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            conn.setAutoCommit(false);

            String addEvent = "INSERT INTO tblEvent(CreatorID, EventTypeID, EventTitle, EventContent, "
                    + "EventCustomer, EventCustomerOther, EventStartDate, EventEndDate) "
                    + "VALUES(?,?,?,?,?,?,?,?)";
            String addEventAnnual = "INSERT INTO tblEventAnnual(EventID, Status) VALUES(?,?)";

            PreparedStatement addEventPreStm = conn.prepareStatement(addEvent, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement addEventAnnualPreStm = conn.prepareStatement(addEventAnnual);

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
                addEventAnnualPreStm.setInt(1, eventID);
                addEventAnnualPreStm.setInt(2, status);

                boolean update = addEventAnnualPreStm.executeUpdate() > 0;
                if (update) {
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

    public boolean updateStatus(int eventID, int status) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "UPDATE tblEventAnnual SET Status = ? WHERE EventID = ? ";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, status);
            preStm.setInt(2, eventID);
            return preStm.executeUpdate() > 0;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public List<EventDTO> getEmployeeAnnualLeave(int empID) throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT E.EventID, E.EventTitle, E.EventContent, E.EventStartDate, E.EventEndDate, EA.Status " +
                    "FROM tblEvent AS E, tblEventAnnual AS EA " +
                    "WHERE (YEAR(E.EventStartDate) = YEAR(CURDATE()) OR YEAR(E.EventEndDate) = YEAR(CURDATE())) " +
                    "AND E.EventID = EA.EventID AND EA.Status = ? AND E.CreatorID = ?";

            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, AnnualStatusEnum.APPROVED.getStatus());
            preStm.setInt(2, empID);
            rs = preStm.executeQuery();

            List<EventDTO> result = new ArrayList<>();
            while (rs.next()) {
                int eventID = rs.getInt("EventID");
                String eventTitle = rs.getString("EventTitle");
                String eventContent = rs.getString("EventContent");
                LocalDateTime eventStartDate = rs.getTimestamp("EventStartDate").toLocalDateTime();
                LocalDateTime eventEndDate = rs.getTimestamp("EventEndDate").toLocalDateTime();

                EventDTO dto = new EventDTO(eventID, null, null, eventTitle, eventContent,
                        null, null, eventStartDate, eventEndDate);
                result.add(dto);
            }
            return result.isEmpty() ? null : result;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public List<EventDTO> getListAnnualLeave(Integer depID) throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT EM.AccountID, E.EventStartDate, E.EventEndDate " +
                    "FROM (tblEmployee AS EM, tblAccount AS A) " +
                    "LEFT JOIN tblEvent AS E ON E.CreatorID = EM.AccountID " +
                    "LEFT JOIN tblEventAnnual AS EA ON EA.EventID = E.EventID " +
                    "WHERE (YEAR(E.EventStartDate) = YEAR(CURDATE()) OR YEAR(E.EventEndDate) = YEAR(CURDATE())) " +
                    "AND EM.AccountID = A.AccountID " +
                    "AND A.Active = ? " +
                    "AND E.EventTypeID = ? " +
                    "AND EA.Status = ? ";

            if (depID != null) {
                sql += "AND EM.Department = ? ";
            }

            preStm = conn.prepareStatement(sql);
            preStm.setBoolean(1, true);
            preStm.setInt(2, EventTypeEnum.ANNUAL_LEAVE.getEventTypeID());
            preStm.setInt(3, AnnualStatusEnum.APPROVED.getStatus());
            if (depID != null) {
                preStm.setInt(4, depID);
            }


            rs = preStm.executeQuery();

            List<EventDTO> result = new ArrayList<>();
            while (rs.next()) {

                LocalDateTime eventStartDate = rs.getTimestamp("EventStartDate").toLocalDateTime();
                LocalDateTime eventEndDate = rs.getTimestamp("EventEndDate").toLocalDateTime();

                AccountDTO acc = new AccountDTO();
                acc.setAccountID(rs.getInt("AccountID"));

                EmployeeDTO creator = new EmployeeDTO();
                creator.setAccount(acc);

                EventDTO event = new EventDTO();
                event.setCreator(creator);
                event.setEventStartDate(eventStartDate);
                event.setEventEndDate(eventEndDate);

                result.add(event);
            }
            return result.isEmpty() ? null : result;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public List<EventAnnualDTO> getListRequestAnnualLeave(EmployeeDTO emp) throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT EM.Fullname, E.*, EA.Status " +
                    "FROM tblEmployee AS EM, tblAccount AS A, tblEvent AS E, tblEventAnnual AS EA " +
                    "WHERE (YEAR(E.EventStartDate) = YEAR(CURDATE()) OR YEAR(E.EventEndDate) = YEAR(CURDATE())) " +
                    "AND EA.EventID = E.EventID " +
                    "AND E.CreatorID = EM.AccountID " +
                    "AND EM.AccountID = A.AccountID " +
                    "AND A.Active = ? " +
                    "AND EM.Department = ? " +
                    "AND EM.Supervisor = ? " +
                    "AND E.EventTypeID = ? " +
                    "AND EA.Status = ? ";

            preStm = conn.prepareStatement(sql);
            preStm.setBoolean(1, true);
            preStm.setInt(2, emp.getDepartment().getDepartmentID());
            preStm.setInt(3, emp.getAccount().getAccountID());
            preStm.setInt(4, EventTypeEnum.ANNUAL_LEAVE.getEventTypeID());
            preStm.setInt(5, AnnualStatusEnum.APPROVING.getStatus());

            rs = preStm.executeQuery();

            List<EventAnnualDTO> result = new ArrayList<>();
            while (rs.next()) {
                int eventID = rs.getInt("EventID");
                String eventTitle = rs.getString("EventTitle");
                String eventContent = rs.getString("EventContent");
                LocalDateTime eventStartDate = rs.getTimestamp("EventStartDate").toLocalDateTime();
                LocalDateTime eventEndDate = rs.getTimestamp("EventEndDate").toLocalDateTime();

                AccountDTO acc = new AccountDTO();
                acc.setAccountID(rs.getInt("CreatorID"));

                EmployeeDTO creator = new EmployeeDTO();
                creator.setFullname(rs.getString("Fullname"));
                creator.setAccount(acc);

                EventDTO event = new EventDTO(eventID, creator, null, eventTitle, eventContent,
                        null, null, eventStartDate, eventEndDate);
                EventAnnualDTO eventAnnual = new EventAnnualDTO(event, AnnualStatusEnum.getAnnualStatus(rs.getInt("Status")));

                result.add(eventAnnual);
            }
            return result.isEmpty() ? null : result;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public AnnualStatusEnum getStatus(int eventID) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "SELECT Status FROM tblEventAnnual WHERE EventID = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, eventID);
            rs = preStm.executeQuery();
            return rs.next() ? AnnualStatusEnum.getAnnualStatus(rs.getInt("Status")) : null;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public boolean checkAnnualLeaveWithHoliday(int start, int end) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "SELECT HolidayID FROM calendar.tblHoliday WHERE " +
                    "(? >= DAYOFYEAR(HolidayStartDate) || ? <= DAYOFYEAR(HolidayEndDate)) && " +
                    "(? >= DAYOFYEAR(HolidayStartDate) || ? >= DAYOFYEAR(HolidayStartDate)) && " +
                    "(? <= DAYOFYEAR(HolidayEndDate) || ? <= DAYOFYEAR(HolidayEndDate)) ";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, start);
            preStm.setInt(2, end);
            preStm.setInt(3, start);
            preStm.setInt(4, end);
            preStm.setInt(5, start);
            preStm.setInt(6, end);
            rs = preStm.executeQuery();
            return rs.next();
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

}
