/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import model.AccountDAO;
import utils.Encrypt;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author yuu
 */
@WebServlet(name = "ChangeUserProfilePasswordAjaxServlet", urlPatterns = {"/ChangeUserProfilePasswordAjaxServlet"})
public class ChangeUserProfilePasswordAjaxServlet extends HttpServlet {

    private final static String DONE = "done";
    private final static String ERROR_SYSTEM = "errorsystem";
    private final static String WRONG_CURRENT_PASSWORD = "wrong_current_password";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Change user profile password and return the result state to ajax
     *
     * Request parameters:
     *              + profileID
     *              + profileCurrent (current password)
     *              + profilePass (new password)
     *
     * Respone return:
     *              + done
     *              + errorsystem (sql query error)
     *              + wrong_current_password (current password is incorrect)
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String profileID = request.getParameter("profileID");
        String profileCurrent = request.getParameter("profileCurrent");
        String profilePass = request.getParameter("profilePass");
        String state = ERROR_SYSTEM;

        try {
            if (profileID != null && !profileID.isEmpty() && profileCurrent != null && !profileCurrent.isEmpty()
                    && profilePass != null && !profilePass.isEmpty()) {

                profileCurrent = Encrypt.EncryptPwd(profileCurrent);
                profilePass = Encrypt.EncryptPwd(profilePass);
                state = changePassword(Integer.parseInt(profileID), profilePass, profileCurrent) ? DONE : WRONG_CURRENT_PASSWORD;

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

    private boolean changePassword(int profileID, String newPass, String currentPass) throws SQLException, NamingException {
        AccountDAO dao = new AccountDAO();
        return dao.changePassword(profileID, newPass, currentPass);
    }
}
