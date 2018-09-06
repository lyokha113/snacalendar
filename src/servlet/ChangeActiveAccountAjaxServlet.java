/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import entity.EmployeeDTO;
import model.AccountDAO;
import utils.Mailer;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author yuu
 */
@WebServlet(name = "ChangeActiveAccountAjaxServlet", urlPatterns = {"/ChangeActiveAccountAjaxServlet"})
public class ChangeActiveAccountAjaxServlet extends HttpServlet {

    private final static String DONE = "done";
    private final static String ERROR = "error";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Lock/Unlock account and returnthe result state to ajax
     *
     * Request parameters:
     *              + accountID
     *              + email
     *              + status (lock/unlock)
     *
     * Respone return:
     *              + done
     *              + error
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String accountID = request.getParameter("accountID");
        String email = request.getParameter("email");
        String status = request.getParameter("status");
        String state = ERROR;

        try {
            if (accountID != null && !accountID.isEmpty() && status != null && !status.isEmpty()
                    && email != null && !email.isEmpty()) {

                HttpSession session = request.getSession();
                EmployeeDTO currentUser = (EmployeeDTO) session.getAttribute("EMPLOYEEDTO");
                if (currentUser.getAccount().getAccountID() == Integer.parseInt(accountID)) return;

                boolean active = status.equals("unlock");
                if (changeActive(Integer.parseInt(accountID), active)) {
                    // Send email for employee
                    Mailer.sendChangeActiveAccountMail(email, active);
                    state = DONE;
                }

            }
        } catch (SQLException | NumberFormatException | NamingException e) {
            log(e.toString());
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

    private boolean changeActive(int accountID, boolean active) throws SQLException, NamingException {
        AccountDAO dao = new AccountDAO();
        return dao.changeActive(accountID, active);
    }
}
