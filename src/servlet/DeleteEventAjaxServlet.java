/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import datamapping.AnnualStatusEnum;
import datamapping.PermissionEnum;
import entity.EmployeeDTO;
import entity.EventAnnualDTO;
import entity.EventDTO;
import model.EventAnnualDAO;
import model.EventDAO;

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
@WebServlet(name = "DeleteEventAjaxServlet", urlPatterns = {"/DeleteEventAjaxServlet"})
public class DeleteEventAjaxServlet extends HttpServlet {

    private final static String DONE = "done";
    private final static String ERROR = "error";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Delete event object and return the result state to ajax
     *
     * Request parameters:
     *              + eventID
     *              + eventType
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

        String eventID = request.getParameter("eventID");
        String eventType = request.getParameter("eventType");
        String state = ERROR;

        try {
            if (eventID != null && !eventID.isEmpty() && eventType != null && !eventType.isEmpty()) {

                HttpSession session = request.getSession();
                EmployeeDTO currentUser = (EmployeeDTO) session.getAttribute("EMPLOYEEDTO");
                EventDTO eventDetail = getEventDetail(Integer.parseInt(eventID));
                AnnualStatusEnum eventStatus = getEventStatus(Integer.parseInt(eventID));

                // Valid event
                if (eventStatus != null && EventAnnualDTO.checkInvalidEvent(eventStatus,
                        PermissionEnum.getEventType(currentUser.getTitle().getPermission().getPermissionID()))) return;
                if (EventDTO.checkInvalidEvent(eventDetail, currentUser.getAccount().getAccountID(), Integer.parseInt(eventType))) return;

                state = deleteEvent(Integer.parseInt(eventID)) ? DONE : ERROR;
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

    private boolean deleteEvent(int eventID) throws SQLException, NamingException {
        EventDAO dao = new EventDAO();
        return dao.deleteEvent(eventID);
    }

    private EventDTO getEventDetail(int eventID) throws SQLException, NamingException {
        EventDAO dao = new EventDAO();
        return dao.getEventDetail(eventID);
    }

    private AnnualStatusEnum getEventStatus(int eventID) throws SQLException, NamingException {
        EventAnnualDAO dao = new EventAnnualDAO();
        return dao.getStatus(eventID);
    }
}
