/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import entity.AccountDTO;
import entity.EmployeeDTO;
import entity.LoginTokenDTO;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.AccountDAO;
import model.EmployeeDAO;
import model.LoginTokenDAO;
import utils.Encrypt;

/**
 *
 * @author junge
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    private final static String DONE = "user.jsp";
    private final static String FAIL = "login.jsp";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Check login detail, active and admin state then forward to page
     *
     * Request parameters:
     *              + email
     *              + password
     *              + rememberme (check box)
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

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String rememberme = request.getParameter("rememberme");
        String url = FAIL;

        try {
            if (email != null && password != null) {

                // Encryp password
                String passwordEncryped = Encrypt.EncryptPwd(password);

                AccountDTO account = checkLogin(email.trim(), passwordEncryped);
                if (account != null && account.isActive()) {
                    EmployeeDTO emp = getProfile(account.getAccountID());
                    if (emp != null) {
                        emp.setAccount(account);
                        HttpSession session = request.getSession();
                        session.setAttribute("EMPLOYEEDTO", emp);
                        url = DONE;

                        // Set respone login cookie to client if rememberme check box is checked
                        if (rememberme != null) {
                            String loginToken = Encrypt.generateToken("LOGINTOKEN", 1000 * 60 * 60 * 24 * 3); // 60s 60m 24h 3d
                            Cookie cookie = new Cookie("LOGINTOKEN", loginToken);
                            cookie.setMaxAge(60 * 60 * 24 * 3); // 60s 60m 24h 3d
                            cookie.setHttpOnly(true);
                            response.addCookie(cookie);

                            // Insert login token to database to validate
                            LoginTokenDTO token = new LoginTokenDTO(account.getAccountID(), Encrypt.EncryptText(loginToken));
                            addLoginToken(token);
                        }
                    } else {
                        request.setAttribute("LOGINSTATUS", "ERROR !!!");
                        request.setAttribute("LOGINMESS", "There are some problems with this account. Contact administrator for more details.");
                    }

                } else {
                    request.setAttribute("LOGINSTATUS", "ERROR !!!");
                    request.setAttribute("LOGINMESS", "Login details is invalid or account was blocked.");
                }

            } else {
                request.setAttribute("LOGINSTATUS", "ERROR !!!");
                request.setAttribute("LOGINMESS", "Please input details to login.");
            }
        } catch (SQLException | NoSuchAlgorithmException | NamingException e) {
            request.setAttribute("LOGINSTATUS", "ERROR !!!");
            request.setAttribute("LOGINMESS", "There were some errors. Please try again.");
            log(e.toString());
        } finally {
            if (url.equals(FAIL)) {
                RequestDispatcher rd = request.getRequestDispatcher(url);
                rd.forward(request, response);
            } else {
                response.sendRedirect(url);
            }
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

    private AccountDTO checkLogin(String phone, String password) throws SQLException, NamingException {
        AccountDAO dao = new AccountDAO();
        return dao.checkLogin(phone, password);
    }

    private EmployeeDTO getProfile(int accountID) throws SQLException, NamingException {
        EmployeeDAO dao = new EmployeeDAO();
        return dao.getProfile(accountID);
    }

    private void addLoginToken(LoginTokenDTO token) throws SQLException, NamingException {
        LoginTokenDAO dao = new LoginTokenDAO();
        dao.addLoginToken(token);
    }

}
