/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import entity.AccountDTO;
import entity.EmployeeDTO;
import model.EmployeeDAO;
import model.TitleDAO;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Yuu
 */
@WebServlet(name = "UpdateEmployeeAjaxServlet", urlPatterns = {"/UpdateEmployeeAjaxServlet"})
public class UpdateEmployeeAjaxServlet extends HttpServlet {

    private final static String DONE = "done";
    private final static String ERROR_DATA = "errordata";
    private final static String ERROR_SYSTEM = "errorsystem";
    private final static String PHONE_UNIQUE = "phone_unique";
    private final static String EMAIL_UNIQUE = "email_unique";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Update employee object and return the result state to ajax
     *
     * Request parameters:
     *              + empID
     *              + empEmail
     *              + empAddress
     *              + empPhone
     *              + empName
     *              + empDoB
     *              + empJD
     *              + empType
     *              + empDep
     *              + empTitle
     *              + empSupervisor
     *              + empAdmin (check box)
     *              + empSex (check box)
     *
     * Respone return:
     *              + done
     *              + errordata (input data missed or error)
     *              + errorsystem (sql query error)
     *              + phone_unique (duplicate for employee phone)
     *              + email_unique (duplicate for employee email)
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String empID = request.getParameter("empID");
        String empEmail = request.getParameter("empEmail");
        String empAddress = request.getParameter("empAddress");
        String empPhone = request.getParameter("empPhone");
        String empName = request.getParameter("empName");
        String empDoB = request.getParameter("empDoB");
        String empJD = request.getParameter("empJD");
        String empType = request.getParameter("empType");
        String empDep = request.getParameter("empDep");
        String empTitle = request.getParameter("empTitle");
        String empSupervisor = request.getParameter("empSupervisor");
        boolean empAdmin = request.getParameter("empAdmin") != null;
        boolean empSex = request.getParameter("maleChk") != null;
        String state = ERROR_DATA;

        try {
            if (empID != null && !empID.isEmpty()
                    && empAddress != null && !empAddress.isEmpty() && empEmail != null && !empEmail.isEmpty()
                    && empName != null && !empName.isEmpty() && empPhone != null && !empPhone.isEmpty()
                    && empDoB != null && !empDoB.isEmpty() && empJD != null && !empJD.isEmpty()
                    && empType != null && empDep != null && empTitle != null
                    && empSupervisor != null) {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd yyyy");
                int id = Integer.parseInt(empID);
                int depID = Integer.parseInt(empDep);
                int typeID = Integer.parseInt(empType);
                int titleID = Integer.parseInt(empTitle);

                // Check if supervisor is valid
                int permission = getPermission(titleID);
                EmployeeDTO supervisor = getSupervisorProfile(empSupervisor);
                if (!EmployeeDTO.checkValidSupervisor(depID, permission, id, supervisor)) return;

                AccountDTO account = AccountDTO.getAccountDTO(empEmail, LocalDate.parse(empJD, formatter), empAdmin);
                account.setAccountID(id);

                EmployeeDTO emp = EmployeeDTO.getEmployeeDTO(empAddress, empPhone, empName,
                        LocalDate.parse(empDoB, formatter), typeID, depID, titleID, empSex);
                emp.setAccount(account);
                emp.setSupervisor(supervisor);

                if (updateEmployee(emp)) {

                    //Reset session if updated employee is current user
                    HttpSession session = request.getSession();
                    EmployeeDTO currentAccount = (EmployeeDTO) session.getAttribute("EMPLOYEEDTO");
                    if (currentAccount.getAccount().getAccountID() == id) {
                        account.setActive(currentAccount.getAccount().isActive());
                        currentAccount = getProfile(id);
                        currentAccount.setAccount(account);
                        session.setAttribute("EMPLOYEEDTO", currentAccount);
                    }
                    state = DONE;
                } else {
                    state = ERROR_SYSTEM;
                }
            }
        } catch (SQLException | NamingException e) {
            log(e.toString());
            String errMess = e.getMessage();
            state = errMess.contains("Phone_UNIQUE") ? PHONE_UNIQUE :
                    errMess.contains("Email_UNIQUE") ? EMAIL_UNIQUE : ERROR_SYSTEM;
        } finally {
            response.getWriter().write(state);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private boolean updateEmployee(EmployeeDTO emp) throws SQLException, NamingException {
        EmployeeDAO dao = new EmployeeDAO();
        return dao.updateEmployee(emp);
    }

    private EmployeeDTO getProfile(int accountID) throws SQLException, NamingException {
        EmployeeDAO dao = new EmployeeDAO();
        return dao.getProfile(accountID);
    }

    private int getPermission(int titleID)throws SQLException, NamingException {
        TitleDAO dao = new TitleDAO();
        return dao.getPermission(titleID);
    }

    private EmployeeDTO getSupervisorProfile(String supervisor) throws SQLException, NamingException {
        EmployeeDAO emp = new EmployeeDAO();
        return emp.getSupervisorProfile(supervisor);
    }
}
