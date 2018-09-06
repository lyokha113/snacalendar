/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import datamapping.PermissionEnum;
import entity.*;
import utils.DbUtil;

import javax.naming.NamingException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuu
 */
public class EmployeeDAO {

    private Connection conn = null;
    private PreparedStatement preStm = null;
    private ResultSet rs = null;

    public EmployeeDAO() {
    }

    public List<EmployeeDTO> getAll() throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT E.AccountID, E.Address, E.Fullname, E.Phone, E.Sex, E.DoB, "
                    + "ES.AccountID AS SupervisorID, ES.Fullname AS SupervisorName, "
                    + "A.Email, A.Active, A.Admin, A.JoinedDate, "
                    + "E.Department, D.DepartmentName, E.Title, T.TitleName, "
                    + "T.Permission, P.PermissionName, E.Type, ET.TypeName "
                    + "FROM (tblEmployee AS E, tblAccount AS A, tblPermission AS P) "
                    + "LEFT JOIN tblDepartment AS D ON E.Department = D.DepartmentID "
                    + "LEFT JOIN tblTitle AS T ON E.Title = T.TitleID "
                    + "LEFT JOIN tblEmpType AS ET ON E.Type = ET.TypeID "
                    + "LEFT JOIN tblEmployee AS ES ON E.Supervisor = ES.AccountID "
                    + "WHERE E.AccountID = A.AccountID "
                    + "AND T.Permission = P.PermissionID "
                    + "ORDER BY E.AccountID";
            preStm = conn.prepareStatement(sql);
            rs = preStm.executeQuery();

            List<EmployeeDTO> result = new ArrayList<>();
            while (rs.next()) {
                result.add(getDTOFromDB(rs, true));
            }
            return result.isEmpty() ? null : result;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    // Get all active employees with their annual leave limit days
    public List<EmployeeDTO> getActiveWithAnnualLeaveDay(Integer depID) throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT E.AccountID, E.Fullname, E.AnnualLeaveDay, D.DepartmentName, T.TitleName, P.PermissionID "
                    + "FROM (tblEmployee AS E, tblAccount AS A, tblPermission AS P) "
                    + "LEFT JOIN tblDepartment AS D ON E.Department = D.DepartmentID "
                    + "LEFT JOIN tblTitle AS T ON E.Title = T.TitleID "
                    + "WHERE E.AccountID = A.AccountID "
                    + "AND P.PermissionID = T.Permission "
                    + "AND A.Active = ? ";

            if (depID != null) sql += "AND D.DepartmentID = ? ";

            preStm = conn.prepareStatement(sql);
            preStm.setBoolean(1, true);
            if (depID != null) preStm.setInt(2, depID);
            rs = preStm.executeQuery();

            List<EmployeeDTO> result = new ArrayList<>();
            while (rs.next()) {
                AccountDTO account = new AccountDTO();
                account.setAccountID(rs.getInt("AccountID"));

                DepartmentDTO dep = new DepartmentDTO();
                dep.setDepartmentName(rs.getString("DepartmentName"));

                PermissionDTO permisson = new PermissionDTO();
                permisson.setPermissionID(rs.getInt("PermissionID"));

                TitleDTO title = new TitleDTO();
                title.setTitleName(rs.getString("TitleName"));
                title.setPermission(permisson);

                EmployeeDTO employee = new EmployeeDTO();
                employee.setAccount(account);
                employee.setDepartment(dep);
                employee.setTitle(title);
                employee.setAnnualLeaveDay(rs.getInt("AnnualLeaveDay"));
                employee.setFullname(rs.getString("Fullname"));

                result.add(employee);
            }
            return result.isEmpty() ? null : result;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public EmployeeDTO getProfile(int accountID) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "SELECT E.Address, E.Fullname, E.Phone, E.Sex, E.DoB, "
                    + "ES.AccountID AS SupervisorID, ES.Fullname AS SupervisorName, "
                    + "E.Department, D.DepartmentName, E.Title, T.TitleName, "
                    + "T.Permission, P.PermissionName, E.Type, ET.TypeName "
                    + "FROM (tblEmployee AS E, tblDepartment AS D, tblTitle AS T, tblEmpType AS ET, tblPermission AS P) "
                    + "LEFT JOIN tblEmployee AS ES ON E.Supervisor = ES.AccountID "
                    + "WHERE E.Department = D.DepartmentID "
                    + "AND E.Title = T.TitleID "
                    + "AND E.Type = ET.TypeID "
                    + "AND E.AccountID = ?";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, accountID);
            rs = preStm.executeQuery();
            return rs.next() ? getDTOFromDB(rs, false) : null;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public int addEmployee(EmployeeDTO emp) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();

            conn.setAutoCommit(false);

            String addAccount = "INSERT INTO tblAccount(Email, Password, Admin, JoinedDate) VALUES(?,?,?,?)";
            String addEmp = "INSERT INTO tblEmployee(AccountID, Fullname, Address, Phone, Sex, DoB, Department, Title, Type, Supervisor) VALUES(?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement addAccountPreStm = conn.prepareStatement(addAccount, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement addEmpPreStm = conn.prepareStatement(addEmp);

            addAccountPreStm.setString(1, emp.getAccount().getEmail());
            addAccountPreStm.setString(2, emp.getAccount().getPassword());
            addAccountPreStm.setBoolean(3, emp.getAccount().isAdmin());
            addAccountPreStm.setObject(4, emp.getAccount().getJoinedDate());
            addAccountPreStm.executeUpdate();

            rs = addAccountPreStm.getGeneratedKeys();
            if (rs.next()) {
                int accountID = rs.getInt(1);
                addEmpPreStm.setInt(1, accountID);
                addEmpPreStm.setString(2, emp.getFullname());
                addEmpPreStm.setString(3, emp.getAddress());
                addEmpPreStm.setString(4, emp.getPhone());
                addEmpPreStm.setBoolean(5, emp.isSex());
                addEmpPreStm.setObject(6, emp.getDob());
                addEmpPreStm.setInt(7, emp.getDepartment().getDepartmentID());
                addEmpPreStm.setInt(8, emp.getTitle().getTitleID());
                addEmpPreStm.setInt(9, emp.getType().getTypeID());
                if (emp.getSupervisor() != null) {
                    addEmpPreStm.setInt(10, emp.getSupervisor().getAccount().getAccountID());
                } else {
                    addEmpPreStm.setNull(10, Types.INTEGER);
                }
                if (addEmpPreStm.executeUpdate() > 0) {
                    conn.commit();
                    return accountID;
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

    public boolean updateEmployee(EmployeeDTO emp) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();

            conn.setAutoCommit(false);

            String updateAccount = "UPDATE tblAccount SET Email = ?, Admin = ?, JoinedDate = ? WHERE AccountID = ?";
            String updateEmp = "UPDATE tblEmployee SET "
                    + "Fullname = ?, Phone = ?, Address = ?, Sex = ?, DoB = ?, Department = ?, Title = ?, Type = ?, Supervisor = ? "
                    + "WHERE AccountID = ? ";

            PreparedStatement updateAccountPreStm = conn.prepareStatement(updateAccount);
            PreparedStatement updateEmpPreStm = conn.prepareStatement(updateEmp);

            updateAccountPreStm.setString(1, emp.getAccount().getEmail());
            updateAccountPreStm.setBoolean(2, emp.getAccount().isAdmin());
            updateAccountPreStm.setObject(3, emp.getAccount().getJoinedDate());
            updateAccountPreStm.setInt(4, emp.getAccount().getAccountID());

            int resultUpdateAccount = updateAccountPreStm.executeUpdate();
            if (resultUpdateAccount == 1) {
                updateEmpPreStm.setString(1, emp.getFullname());
                updateEmpPreStm.setString(2, emp.getPhone());
                updateEmpPreStm.setString(3, emp.getAddress());
                updateEmpPreStm.setBoolean(4, emp.isSex());
                updateEmpPreStm.setObject(5, emp.getDob());
                updateEmpPreStm.setInt(6, emp.getDepartment().getDepartmentID());
                updateEmpPreStm.setInt(7, emp.getTitle().getTitleID());
                updateEmpPreStm.setInt(8, emp.getType().getTypeID());
                if (emp.getSupervisor() != null) {
                    updateEmpPreStm.setInt(9, emp.getSupervisor().getAccount().getAccountID());
                } else {
                    updateEmpPreStm.setNull(9, Types.INTEGER);
                }
                updateEmpPreStm.setInt(10, emp.getAccount().getAccountID());
                int resultUpdateEmp = updateEmpPreStm.executeUpdate();
                if (resultUpdateEmp == 1) {
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

    public boolean updateUserProfile(EmployeeDTO dto) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "UPDATE tblEmployee SET Fullname = ?, Phone = ?, Address = ? WHERE AccountID = ? ";
            preStm = conn.prepareStatement(sql);
            preStm.setString(1, dto.getFullname());
            preStm.setString(2, dto.getPhone());
            preStm.setString(3, dto.getAddress());
            preStm.setInt(4, dto.getAccount().getAccountID());
            return preStm.executeUpdate() > 0;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public String getSupervisorMail(EmployeeDTO emp) throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT A.Email " +
                    "FROM tblAccount AS A, tblEmployee AS E, tblEmployee AS E2 " +
                    "WHERE A.AccountID = E2.AccountID " +
                    "AND E.Supervisor = E2.AccountID " +
                    "AND E.Department = ? " +
                    "AND E.AccountID = ?";

            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, emp.getDepartment().getDepartmentID());
            preStm.setInt(2, emp.getAccount().getAccountID());
            rs = preStm.executeQuery();
            return rs.next() ? rs.getString("Email") : null;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public List<EmployeeDTO> getSupervisorList() throws SQLException, NamingException {
        try {

            conn = DbUtil.getConnection();
            String sql = "SELECT E.AccountID, E.Fullname, T.Permission, " +
                    "E.Department, D.DepartmentName, E.Title, T.TitleName " +
                    "FROM (tblEmployee AS E, tblAccount AS A) " +
                    "LEFT JOIN tblDepartment AS D ON E.Department = D.DepartmentID " +
                    "LEFT JOIN tblTitle AS T ON E.Title = T.TitleID " +
                    "WHERE E.AccountID = A.AccountID " +
                    "AND T.Permission = ? " +
                    "ORDER BY E.AccountID";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, PermissionEnum.SUPER_USER.getPermissionID());
            rs = preStm.executeQuery();

            List<EmployeeDTO> result = new ArrayList<>();
            while (rs.next()) {
                String fullname = rs.getString("Fullname");

                AccountDTO account = new AccountDTO();
                account.setAccountID(rs.getInt("AccountID"));

                DepartmentDTO department = new DepartmentDTO();
                department.setDepartmentID(rs.getInt("Department"));
                department.setDepartmentName(rs.getString("DepartmentName"));

                PermissionDTO permission = new PermissionDTO();
                permission.setPermissionID(rs.getInt("Permission"));

                TitleDTO title = new TitleDTO();
                title.setTitleID(rs.getInt("Title"));
                title.setTitleName(rs.getString("TitleName"));
                title.setPermission(permission);

                EmployeeDTO dto = new EmployeeDTO();
                dto.setAccount(account);
                dto.setFullname(fullname);
                dto.setDepartment(department);
                dto.setTitle(title);

                result.add(dto);
            }
            return result.isEmpty() ? null : result;

        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

    public EmployeeDTO getSupervisorProfile(String supervisor) throws SQLException, NamingException {
        EmployeeDTO dto = null;
        if (supervisor != null && !supervisor.equals("0")) {
            int supervisorID = Integer.parseInt(supervisor);
            dto = getProfile(supervisorID);

            AccountDTO supervisorAcc = new AccountDTO();
            supervisorAcc.setAccountID(supervisorID);
            dto.setAccount(supervisorAcc);
        }
        return dto;
    }

    private EmployeeDTO getDTOFromDB(ResultSet result, boolean withAccount) throws SQLException {
        String fullname = result.getString("Fullname");
        String address = result.getString("Address");
        String phone = result.getString("Phone");
        boolean sex = result.getBoolean("Sex");
        LocalDate dob = result.getDate("DoB").toLocalDate();

        AccountDTO account = null;
        if (withAccount) {
            account = new AccountDTO();
            account.setAccountID(result.getInt("AccountID"));
            account.setEmail(result.getString("Email"));
            account.setActive(result.getBoolean("Active"));
            account.setAdmin(result.getBoolean("Admin"));
            account.setJoinedDate(result.getDate("JoinedDate").toLocalDate());
        }

        DepartmentDTO department = new DepartmentDTO();
        department.setDepartmentID(result.getInt("Department"));
        department.setDepartmentName(result.getString("DepartmentName"));

        PermissionDTO permission = new PermissionDTO();
        permission.setPermissionID(result.getInt("Permission"));
        permission.setPermissionName(result.getString("PermissionName"));

        TitleDTO title = new TitleDTO();
        title.setTitleID(result.getInt("Title"));
        title.setTitleName(result.getString("TitleName"));
        title.setPermission(permission);

        EmpTypeDTO type = new EmpTypeDTO();
        type.setTypeID(result.getInt("Type"));
        type.setTypeName(result.getString("TypeName"));

        EmployeeDTO supervisor = null;
        String supervisorName = result.getString("SupervisorName");
        if (supervisorName != null) {
            int supervisorID = result.getInt("SupervisorID");
            AccountDTO supervisorAcc = new AccountDTO();
            supervisorAcc.setAccountID(supervisorID);

            supervisor = new EmployeeDTO();
            supervisor.setAccount(supervisorAcc);
            supervisor.setFullname(supervisorName);
        }

        return new EmployeeDTO(account, fullname, address, phone, sex, dob, department, title, type, supervisor);
    }


    public boolean changeAnnualLeaveLimit(int empID, int day) throws SQLException, NamingException {
        try {
            conn = DbUtil.getConnection();
            String sql = "UPDATE tblEmployee SET AnnualLeaveDay = ? WHERE AccountID = ? ";
            preStm = conn.prepareStatement(sql);
            preStm.setInt(1, day);
            preStm.setInt(2, empID);
            return preStm.executeUpdate() > 0;
        } finally {
            DbUtil.closeConnection(conn, preStm, rs);
        }
    }

}
