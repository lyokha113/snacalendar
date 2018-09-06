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
import utils.Encrypt;
import utils.Mailer;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author yuu
 */
@WebServlet(name = "AddEmployeeAjaxServlet", urlPatterns = {"/AddEmployeeAjaxServlet"})
public class AddEmployeeAjaxServlet extends HttpServlet {

    private final static String ERROR_DATA = "errordata";
    private final static String ERROR_SYSTEM = "errorsystem";
    private final static String PHONE_UNIQUE = "phone_unique";
    private final static String EMAIL_UNIQUE = "email_unique";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Add employee object and return the result state to ajax
     *
     * Request parameters:
     *              + empEmail
     *              + empAddress
     *              + empPhone
     *              + empPass
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
     *              + new object id when successful
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

        String empEmail = request.getParameter("empEmail");
        String empAddress = request.getParameter("empAddress");
        String empPhone = request.getParameter("empPhone");
        String empPass = request.getParameter("empPass");
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
            if (empEmail != null && !empEmail.isEmpty()
                    && empPass != null && !empPass.isEmpty() && empAddress != null && !empAddress.isEmpty()
                    && empName != null && !empName.isEmpty() && empPhone != null && !empPhone.isEmpty()
                    && empDoB != null && !empDoB.isEmpty() && empJD != null && !empJD.isEmpty()
                    && empType != null && empDep != null && empTitle != null
                    && empSupervisor != null) {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd yyyy");
                int depID = Integer.parseInt(empDep);
                int typeID = Integer.parseInt(empType);
                int titleID = Integer.parseInt(empTitle);

                // Check if supervisor is valid
                int permission = getPermission(titleID);
                EmployeeDTO supervisor = getSupervisorProfile(empSupervisor);
                if (!EmployeeDTO.checkValidSupervisor(depID, permission, -1, supervisor)) return;

                // Encrypt password
                AccountDTO account = AccountDTO.getAccountDTO(empEmail, LocalDate.parse(empJD, formatter), empAdmin);
                account.setPassword(Encrypt.EncryptPwd(empPass));

                EmployeeDTO emp = EmployeeDTO.getEmployeeDTO(empAddress, empPhone, empName,
                        LocalDate.parse(empDoB, formatter), typeID, depID, titleID, empSex);
                emp.setAccount(account);
                emp.setSupervisor(supervisor);

                int addResult = addEmployee(emp);
                if (addResult != -1) {
                    // Send email if insert sucessful
                    Mailer.sendNewAccountMail(emp.getAccount().getEmail(), empPass);
                    state = String.valueOf(addResult);
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

    private int addEmployee(EmployeeDTO emp) throws SQLException, NamingException {
        EmployeeDAO dao = new EmployeeDAO();
        return dao.addEmployee(emp);
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
