/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import datamapping.PermissionEnum;
import entity.EmployeeDTO;
import model.EmployeeDAO;
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
 * @author Yuu
 */
@WebServlet(name = "SendAnnualLeaveAjaxServlet", urlPatterns = {"/SendAnnualLeaveAjaxServlet"})
public class SendAnnualLeaveAjaxServlet extends HttpServlet {

    private final static String ERROR = "error";
    private final static String DONE = "done";
    private final static String SKIP = "skip";
    private final static String NONE = "none";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Send email to supervisor for new request annual leave and return state to ajax
     *
     * Request parameters:
     *              + eventTitle
     *
     * Respone return:
     *              + done
     *              + error
     *              + skip (no need to send mail)
     *              + none (no email address to send)
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String eventTitle = request.getParameter("eventTitle");
        String state = ERROR;
        try {
            if (eventTitle != null && !eventTitle.isEmpty()) {
                HttpSession session = request.getSession();
                EmployeeDTO currentUser = (EmployeeDTO) session.getAttribute("EMPLOYEEDTO");

                // Skip sending email if requested user is Supet user
                if (currentUser.getTitle().getPermission().getPermissionID() == PermissionEnum.SUPER_USER.getPermissionID()) {
                    state = SKIP;
                    return;
                }

                // Get supervisor of requested user
                String supervisorMail = getSupervisorMail(currentUser);
                if (supervisorMail == null) {
                    state = NONE;
                    return;
                }

                String sender = currentUser.getFullname();
                sendNewEventAnnualLeave(supervisorMail, sender, eventTitle);
                state = DONE;
            }
        } catch (Exception e) {
            log(e.getMessage());
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

    private void sendNewEventAnnualLeave(String manager, String sender, String title) {
        Mailer.sendNewEventAnnualLeave(manager, sender, title);
    }

    private String getSupervisorMail(EmployeeDTO emp) throws SQLException, NamingException {
        EmployeeDAO dao = new EmployeeDAO();
        return dao.getSupervisorMail(emp);
    }

}
