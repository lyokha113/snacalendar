/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utils.Encrypt;

/**
 *
 * @author Yuu
 */
@WebServlet(name = "GetRecoveryLinkServlet", urlPatterns = {"/GetRecoveryLinkServlet"})
public class GetRecoveryLinkServlet extends HttpServlet {

    private final static String RECOVERY_PAGE = "recovery.jsp";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Generate recovery link from user email to reset password
     *
     * Request parameters:
     *              + token (token was sended to user email)
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

        String token = request.getParameter("token");
        try {
            if (token == null || !token.isEmpty()) {
                String recoveryToken = Encrypt.verifyToken(token);
                if (recoveryToken != null && recoveryToken.equals("RECOVERYTOKEN")) {
                    request.setAttribute("LINKSTATUS", "SUCCESS !!!");
                    request.setAttribute("LINKMESS", token);
                } else {
                    request.setAttribute("LINKSTATUS", "ERROR !!!");
                    request.setAttribute("LINKMESS", "The request was time out or there was some error. Please try again.");
                }
            }
        } catch (Exception e) {
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

}
