/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import entity.AccountDTO;
import entity.EmployeeDTO;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.EmployeeDAO;
import model.LoginTokenDAO;
import utils.Encrypt;

/**
 *
 * @author Yuu
 */
@WebServlet(name = "LoginCookieServlet", urlPatterns = {"/LoginCookieServlet"})
public class LoginCookieServlet extends HttpServlet {

    private final static String DONE = "user.jsp";
    private final static String FAIL = "login.jsp";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Check login by using client login cookie
     *
     * Page forward:
     *         + user.jsp (login succcessfully)
     *         + login.jsp (login failed)
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession();
        String url = FAIL;

        try {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                return;
            }
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("LOGINTOKEN")) {

                    String loginToken = cookie.getValue();

                    // Valid login token
                    String verify = Encrypt.verifyToken(loginToken);
                    if (verify == null || !verify.equals("LOGINTOKEN")) {
                        continue;
                    }

                    // Check login token in database
                    AccountDTO account = checkLoginCookie(Encrypt.EncryptText(loginToken));
                    if (account != null && account.isActive()) {

                        // Get employee profile and set new session
                        EmployeeDTO emp = getProfile(account.getAccountID());
                        if (emp != null) {
                            emp.setAccount(account);
                            session.setAttribute("EMPLOYEEDTO", emp);
                            url = DONE;
                            break;
                        }
                    }

                }

            }

        } catch (SQLException | NoSuchAlgorithmException | NamingException e) {
            log(e.toString());
        } finally {
            response.sendRedirect(url);
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

    private AccountDTO checkLoginCookie(String loginToken) throws SQLException, NamingException {
        LoginTokenDAO dao = new LoginTokenDAO();
        return dao.checkLoginCookie(loginToken);
    }

    private EmployeeDTO getProfile(int accountID) throws SQLException, NamingException {
        EmployeeDAO dao = new EmployeeDAO();
        return dao.getProfile(accountID);
    }
}
