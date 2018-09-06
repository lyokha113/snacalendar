package model;

import entity.AccountDTO;
import entity.EmployeeDTO;
import entity.EventAttendeeDTO;
import utils.DbUtil;

import javax.naming.NamingException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventAttendeeDAO implements Serializable {
    private Connection conn = null;
    private PreparedStatement preStm = null;
    private ResultSet rs = null;

    public EventAttendeeDAO() {
    }

    public List<EmployeeDTO> getEventAttendees(int eventID) throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT E.AccountID, E.Fullname, E.Sex " +
                    "FROM tblEventAttendee AS EA, tblEmployee AS E " +
                    "WHERE EA.EmployeeID = E.AccountID " +
                    "AND EA.EventID = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, eventID);
            rs = preStm.executeQuery();
            List<EmployeeDTO> result = new ArrayList<>();
            while (rs.next()) {
                boolean sex = rs.getBoolean("Sex");
                String fullname = rs.getString("Fullname");
                int id = rs.getInt("AccountID");

                AccountDTO account = new AccountDTO();
                account.setAccountID(id);

                EmployeeDTO emp = new EmployeeDTO();
                emp.setAccount(account);
                emp.setFullname(fullname);
                emp.setSex(sex);
                result.add(emp);
            }
            return result.isEmpty() ? null : result;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public boolean addEventAttendees(EventAttendeeDTO dto) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();

            conn.setAutoCommit(false);

            String preInsert = "DELETE FROM tblEventAttendee WHERE EventID = ?";
            String sql = "INSERT INTO tblEventAttendee(EmployeeID, EventID) VALUES(?,?)";

            PreparedStatement preInsertStm = conn.prepareStatement(preInsert);
            preInsertStm.setInt(1, dto.getEventID());
            preInsertStm.executeUpdate();

            preStm = conn.prepareStatement(sql);
            for (Integer empID : dto.getListEmpID()) {
                preStm.setInt(1, empID);
                preStm.setInt(2, dto.getEventID());
                preStm.addBatch();
            }

            int[] results = preStm.executeBatch();
            for (int result : results) {
                if (result <= 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            return false;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

}
