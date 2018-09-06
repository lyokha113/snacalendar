/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import entity.RecoveryTokenDTO;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.AccountDAO;
import model.RecoveryTokenDAO;
import utils.Encrypt;
import utils.Mailer;

/**
 *
 * @author Yuu
 */
@WebServlet(name = "SendRecoveryMailServlet", urlPatterns = {"/SendRecoveryMailServlet"})
public class SendRecoveryMailServlet extends HttpServlet {

    private final static String RECOVERY_PAGE = "recovery.jsp";
    private static final String TOKENPARAM = "/GetRecoveryLink?token=";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Send token for recovering password to account email
     *
     * Request parameters:
     *              + email
     *
     * Forward page:
     *              + recovery.jsp
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String email = request.getParameter("email");
        try {
            if (email != null && !email.isEmpty()) {
                int accountID = getAccountIDByEmail(email);
                if (accountID > 0) {

                    // Generate unique token with time experid
                    String recoveryToken = Encrypt.generateToken("RECOVERYTOKEN", 1000 * 60 * 15); //60s 15m
                    String link = TOKENPARAM + recoveryToken;

                    // Insert token to database for validate and send this token to user by email
                    RecoveryTokenDTO token = new RecoveryTokenDTO(accountID, Encrypt.EncryptText(recoveryToken));
                    if (addRecoveryToken(token)) {
                        Mailer.sendRecoveryMail(email, link);
                        request.setAttribute("RECOVERYSTATUS", "SUCCESS !!!");
                        request.setAttribute("RECOVERYMESS", "Recovery email was sent. Please check your inbox mail (or spam)");
                    } else {
                        request.setAttribute("RECOVERYSTATUS", "ERROR !!!");
                        request.setAttribute("RECOVERYMESS", "There was some problem. Please try again.");
                    }
                } else {
                    request.setAttribute("RECOVERYSTATUS", "ERROR !!!");
                    request.setAttribute("RECOVERYMESS", "This email is not registed.");
                }
            }
        } catch (SQLException | RuntimeException | NoSuchAlgorithmException | NamingException e) {
            log(e.getMessage());
        } finally {
            RequestDispatcher rd = request.getRequestDispatcher(RECOVERY_PAGE);
            rd.forward(request, response);
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

    private boolean addRecoveryToken(RecoveryTokenDTO token) throws SQLException, NamingException {
        RecoveryTokenDAO dao = new RecoveryTokenDAO();
        return dao.addRecoveryToken(token);
    }

    private int getAccountIDByEmail(String email) throws SQLException, NamingException {
        AccountDAO dao = new AccountDAO();
        return dao.getAccountIDByEmail(email);
    }
}
