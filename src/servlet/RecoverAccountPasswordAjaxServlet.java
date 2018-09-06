/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.AccountDAO;
import model.RecoveryTokenDAO;
import utils.Encrypt;

/**
 *
 * @author yuu
 */
@WebServlet(name = "RecoverAccountPasswordAjaxServlet", urlPatterns = {"/RecoverAccountPasswordAjaxServlet"})
public class RecoverAccountPasswordAjaxServlet extends HttpServlet {

    private final static String DONE = "done";
    private final static String ERROR = "error";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Recover new account password for user
     *
     * Request parameters:
     *              + token (token was sended to user email)
     *              + password (new password)
     *
     * Respone return:
     *              + done
     *              + error
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String state = ERROR;

        try {
            if (token != null && !token.isEmpty() && password != null && !password.isEmpty()) {

                // Set new user password if token is valid
                token = Encrypt.EncryptText(token);
                password = Encrypt.EncryptPwd(password);
                if (changePassword(token, password)) {
                    state = DONE;
                    deleteRecoveryToken(token);
                }

            }
        } catch (SQLException | NumberFormatException | NoSuchAlgorithmException | NamingException e) {
            log(e.toString());
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

    private boolean changePassword(String token, String password) throws SQLException, NamingException {
        AccountDAO dao = new AccountDAO();
        return dao.changePassword(token, password);
    }

    private void deleteRecoveryToken(String token) throws SQLException, NamingException {
        RecoveryTokenDAO dao = new RecoveryTokenDAO();
        dao.deleteRecoveryToken(token);
    }
}
