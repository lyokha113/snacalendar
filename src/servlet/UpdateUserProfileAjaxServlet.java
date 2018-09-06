/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import entity.AccountDTO;
import entity.EmployeeDTO;
import java.io.IOException;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.EmployeeDAO;

/**
 *
 * @author Yuu
 */
@WebServlet(name = "UpdateUserProfileAjaxServlet", urlPatterns = {"/UpdateUserProfileAjaxServlet"})
public class UpdateUserProfileAjaxServlet extends HttpServlet {

    private final static String DONE = "done";
    private final static String ERROR_DATA = "errordata";
    private final static String ERROR_SYSTEM = "errorsystem";
    private final static String PHONE_UNIQUE = "phone_unique";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Update user profile and return the result state to ajax
     *
     * Request parameters:
     *              + profileID
     *              + profileName
     *              + profileAddress
     *              + profilePhone
     *
     * Respone return:
     *              + done
     *              + errordata (input data missed or error)
     *              + errorsystem (sql query error)
     *              + phone_unique (duplicate for employee phone)
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String profileID = request.getParameter("profileID");
        String profileName = request.getParameter("profileName");
        String profileAddress = request.getParameter("profileAddress");
        String profilePhone = request.getParameter("profilePhone");
        String state = ERROR_DATA;

        try {
            if (profileID != null && !profileID.isEmpty() && profileAddress != null && !profileAddress.isEmpty()
                    && profileName != null && !profileName.isEmpty() && profilePhone != null && !profilePhone.isEmpty()) {

                int id = Integer.parseInt(profileID);

                AccountDTO account = new AccountDTO();
                account.setAccountID(id);

                EmployeeDTO emp = new EmployeeDTO();
                emp.setAccount(account);
                emp.setAddress(profileAddress);
                emp.setPhone(profilePhone);
                emp.setFullname(profileName);

                if (updateUserProfile(emp)) {
                    // Reset current user session
                    HttpSession session = request.getSession();
                    EmployeeDTO currentAccount = (EmployeeDTO) session.getAttribute("EMPLOYEEDTO");
                    currentAccount.setFullname(profileName);
                    currentAccount.setAddress(profileAddress);
                    currentAccount.setPhone(profilePhone);
                    session.setAttribute("EMPLOYEEDTO", currentAccount);
                    state = DONE;
                } else {
                    state = ERROR_SYSTEM;
                }
                
            }
        } catch (SQLException | NamingException e) {
            if (e.getMessage().contains("Duplicate")) {
                state = PHONE_UNIQUE;
            } else {
                log(e.toString());
                state = ERROR_SYSTEM;
            }
        } finally {
            response.getWriter().write(state);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
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

    private boolean updateUserProfile(EmployeeDTO emp) throws SQLException, NamingException {
        EmployeeDAO dao = new EmployeeDAO();
        return dao.updateUserProfile(emp);
    }

}
