package model;

import datamapping.AnnualStatusEnum;
import datamapping.EventTypeEnum;
import entity.*;
import utils.DbUtil;

import javax.naming.NamingException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {


    private Connection conn = null;
    private PreparedStatement preStm = null;
    private ResultSet rs = null;

    public EventDAO() {
    }

    public List<EventCarDTO> getEventCustomerCare() throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT  E.*, EM.Fullname, EM.Department, C.CustomerName, C.CustomerAddress, " +
                    "D.DepartmentName, CA.CarID, CA.CarPlate, CA.CarBrand, CA.CarSlot " +
                    "FROM (tblEvent AS E, tblEmployee AS EM, tblDepartment AS D) " +
                    "LEFT JOIN tblCustomer AS C ON E.EventCustomer = C.CustomerID " +
                    "LEFT JOIN tblEventCar AS EC ON E.EventID = EC.EventID " +
                    "LEFT JOIN tblCar AS CA ON CA.CarID = EC.CarID " +
                    "WHERE E.CreatorID = EM.AccountID " +
                    "AND D.DepartmentID = EM.Department " +
                    "AND E.EventTypeID = ? ";

            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, EventTypeEnum.CUSTOMER_CARE.getEventTypeID());
            rs = preStm.executeQuery();

            List<EventCarDTO> result = new ArrayList<>();
            while (rs.next()) {
                int eventID = rs.getInt("EventID");
                EventDTO event = getDTOFromDB(rs, EventTypeEnum.CUSTOMER_CARE);
                event.setEventID(eventID);
                event.getCreator().setFullname(rs.getString("Fullname"));

                DepartmentDTO dep = new DepartmentDTO();
                dep.setDepartmentID(rs.getInt("Department"));
                dep.setDepartmentName(rs.getString("DepartmentName"));
                event.getCreator().setDepartment(dep);

                CustomerDTO eventCustomer = new CustomerDTO();
                eventCustomer.setCustomerID(rs.getInt("EventCustomer"));
                eventCustomer.setCustomerName(rs.getString("CustomerName"));
                eventCustomer.setCustomerAddress(rs.getString("CustomerAddress"));
                event.setEventCustomer(eventCustomer);

                String eventCustomerOther = rs.getString("EventCustomerOther");
                event.setEventCustomerOther(eventCustomerOther);

                CarDTO eventCar = new CarDTO();
                eventCar.setCarID(rs.getInt("CarID"));
                if (eventCar.getCarID() != 0) {
                    eventCar.setCarPlate(rs.getString("CarPlate"));
                    eventCar.setCarBrand(rs.getString("CarBrand"));
                    eventCar.setCarSlot(rs.getInt("CarSlot"));
                } else {
                    eventCar = null;
                }

                EventCarDTO dto = new EventCarDTO(event, eventCar);
                result.add(dto);
            }
            return result.isEmpty() ? null : result;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public List<EventCarDTO> getEventCarReservation() throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();

            String sql = "SELECT  E.*, EM.Fullname, EM.Department, C.CustomerName, C.CustomerAddress, " +
                    "D.DepartmentName, CA.CarID, CA.CarPlate, CA.CarBrand, CA.CarSlot " +
                    "FROM (tblEvent AS E, tblEmployee AS EM, tblDepartment AS D, tblEventCar AS EC) " +
                    "LEFT JOIN tblCustomer AS C ON E.EventCustomer = C.CustomerID " +
                    "LEFT JOIN tblCar AS CA ON CA.CarID = EC.CarID " +
                    "WHERE E.CreatorID = EM.AccountID " +
                    "AND E.EventID = EC.EventID " +
                    "AND D.DepartmentID = EM.Department " +
                    "AND (E.EventTypeID = ? OR E.EventTypeID = ?)";

            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, EventTypeEnum.CUSTOMER_CARE.getEventTypeID());
            preStm.setInt(2, EventTypeEnum.CAR_RESERVATION.getEventTypeID());
            rs = preStm.executeQuery();

            List<EventCarDTO> result = new ArrayList<>();
            while (rs.next()) {
                int eventID = rs.getInt("EventID");
                EventDTO event = getDTOFromDB(rs, EventTypeEnum.CAR_RESERVATION);
                event.setEventID(eventID);
                event.getCreator().setFullname(rs.getString("Fullname"));

                DepartmentDTO dep = new DepartmentDTO();
                dep.setDepartmentID(rs.getInt("Department"));
                dep.setDepartmentName(rs.getString("DepartmentName"));
                event.getCreator().setDepartment(dep);

                CustomerDTO eventCustomer = new CustomerDTO();
                eventCustomer.setCustomerID(rs.getInt("EventCustomer"));
                eventCustomer.setCustomerName(rs.getString("CustomerName"));
                eventCustomer.setCustomerAddress(rs.getString("CustomerAddress"));
                event.setEventCustomer(eventCustomer);

                String eventCustomerOther = rs.getString("EventCustomerOther");
                event.setEventCustomerOther(eventCustomerOther);

                CarDTO eventCar = new CarDTO();
                eventCar.setCarID(rs.getInt("CarID"));
                eventCar.setCarPlate(rs.getString("CarPlate"));
                eventCar.setCarBrand(rs.getString("CarBrand"));
                eventCar.setCarSlot(rs.getInt("CarSlot"));

                EventCarDTO dto = new EventCarDTO(event, eventCar);
                result.add(dto);
            }
            return result.isEmpty() ? null : result;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public List<EventDTO> getEventBizTrip() throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT E.*, EM.Fullname FROM tblEvent AS E, tblEmployee AS EM " +
                    "WHERE EM.AccountID = E.CreatorID " +
                    "AND EventTypeID = ? ";

            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, EventTypeEnum.BIZ_TRIP.getEventTypeID());
            rs = preStm.executeQuery();

            List<EventDTO> result = new ArrayList<>();
            while (rs.next()) {
                int eventID = rs.getInt("EventID");
                EventDTO event = getDTOFromDB(rs, EventTypeEnum.BIZ_TRIP);
                event.setEventID(eventID);
                event.getCreator().setFullname(rs.getString("Fullname"));
                result.add(event);
            }
            return result.isEmpty() ? null : result;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public List<EventDTO> getEventTraining() throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT E.*, EM.Fullname FROM tblEvent AS E, tblEmployee AS EM " +
                    "WHERE EM.AccountID = E.CreatorID " +
                    "AND EventTypeID = ? ";

            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, EventTypeEnum.TRAINING.getEventTypeID());
            rs = preStm.executeQuery();

            List<EventDTO> result = new ArrayList<>();
            while (rs.next()) {
                int eventID = rs.getInt("EventID");
                EventDTO event = getDTOFromDB(rs, EventTypeEnum.TRAINING);
                event.setEventID(eventID);
                event.getCreator().setFullname(rs.getString("Fullname"));
                result.add(event);
            }
            return result.isEmpty() ? null : result;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public List<EventAnnualDTO> getEventAnnualLeave() throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT E.*, EM.Fullname, EA.Status " +
                    "FROM tblEvent AS E, tblEmployee AS EM, tblEventAnnual AS EA " +
                    "WHERE EM.AccountID = E.CreatorID " +
                    "AND EA.EventID = E.EventID " +
                    "AND EventTypeID = ? ";

            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, EventTypeEnum.ANNUAL_LEAVE.getEventTypeID());
            rs = preStm.executeQuery();

            List<EventAnnualDTO> result = new ArrayList<>();
            while (rs.next()) {
                int eventID = rs.getInt("EventID");
                EventDTO event = getDTOFromDB(rs, EventTypeEnum.ANNUAL_LEAVE);
                event.setEventID(eventID);
                event.getCreator().setFullname(rs.getString("Fullname"));

                EventAnnualDTO dto = new EventAnnualDTO(event, AnnualStatusEnum.getAnnualStatus(rs.getInt("Status")));
                result.add(dto);
            }
            return result.isEmpty() ? null : result;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public EventDTO getEventDetail(int eventID) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "SELECT * FROM tblEvent WHERE EventID = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, eventID);
            rs = preStm.executeQuery();

            if (rs.next()) {

                EventDTO event = getDTOFromDB(rs, EventTypeEnum.getEventType(rs.getInt("EventTypeID")));
                event.setEventID(eventID);
                return event;

            }
            return null;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public int addEvent(EventDTO event) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "INSERT INTO tblEvent(CreatorID, EventTypeID, EventTitle, EventContent, "
                    + "EventCustomer, EventCustomerOther, EventStartDate, EventEndDate) "
                    + "VALUES(?,?,?,?,?,?,?,?)";
            preStm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preStm.setInt(1, event.getCreator().getAccount().getAccountID());
            preStm.setInt(2, event.getEventType().getEventTypeID());
            preStm.setString(3, event.getEventTitle());
            preStm.setString(4, event.getEventContent());
            if (event.getEventCustomer() != null) {
                preStm.setInt(5, event.getEventCustomer().getCustomerID());
            } else {
                preStm.setNull(5, Types.INTEGER);
            }
            preStm.setString(6, event.getEventCustomerOther());
            preStm.setObject(7, event.getEventStartDate());
            preStm.setObject(8, event.getEventEndDate());

            preStm.executeUpdate();

            rs = preStm.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public boolean updateEvent(EventDTO event) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "UPDATE tblEvent SET EventTitle = ?, EventContent = ?, " +
                    "EventCustomer = ?, EventCustomerOther = ?, " +
                    "EventStartDate = ?, EventEndDate = ? " +
                    "WHERE EventID = ? ";
            preStm = conn.prepareStatement(sql);
            preStm.setString(1, event.getEventTitle());
            preStm.setString(2, event.getEventContent());
            if (event.getEventCustomer() != null) {
                preStm.setInt(3, event.getEventCustomer().getCustomerID());
            } else {
                preStm.setNull(3, Types.INTEGER);
            }
            preStm.setString(4, event.getEventCustomerOther());
            preStm.setObject(5, event.getEventStartDate());
            preStm.setObject(6, event.getEventEndDate());
            preStm.setInt(7, event.getEventID());
            return preStm.executeUpdate() > 0;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public boolean deleteEvent(int eventID) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "DELETE FROM tblEvent WHERE EventID = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, eventID);
            return (preStm.executeUpdate() > 0);
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    private EventDTO getDTOFromDB(ResultSet result, EventTypeEnum type) throws SQLException {

        String eventTitle = result.getString("EventTitle");
        String eventContent = result.getString("EventContent");
        LocalDateTime eventStartDate = result.getTimestamp("EventStartDate").toLocalDateTime();
        LocalDateTime eventEndDate = result.getTimestamp("EventEndDate").toLocalDateTime();

        EventTypeDTO eventType = new EventTypeDTO();
        eventType.setEventTypeID(type.getEventTypeID());

        AccountDTO acc = new AccountDTO();
        acc.setAccountID(result.getInt("CreatorID"));

        EmployeeDTO creator = new EmployeeDTO();
        creator.setAccount(acc);

        return new EventDTO(creator, eventType, eventTitle, eventContent,
                null, null, eventStartDate, eventEndDate);
    }
}
