/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import datamapping.AnnualStatusEnum;
import entity.EventDTO;
import entity.HolidayDTO;
import model.EventAnnualDAO;
import model.EventDAO;
import model.HolidayDAO;
import utils.TimeUtils;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Yuu
 */
@WebServlet(name = "ChangeStatusAnnualLeaveAjaxServlet", urlPatterns = {"/ChangeStatusAnnualLeaveAjaxServlet"})
public class ChangeStatusAnnualLeaveAjaxServlet extends HttpServlet {

    private final static String DONE = "done";
    private final static String ERROR = "error";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Change event annual leave request status and return the result state to ajax
     *
     * Request parameters:
     *              + eventID
     *              + approve (approve/deny)
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
        String approve = request.getParameter("approve");
        String state = ERROR;

        try {
            if (eventID != null && !eventID.isEmpty() && approve != null && !approve.isEmpty()) {
                state = approve.equals("true")
                        ? updateStatus(Integer.parseInt(eventID), AnnualStatusEnum.APPROVED.getStatus())
                            ? (DONE + "-" + getApprovedDay(Integer.parseInt(eventID)))
                            : ERROR
                        : updateStatus(Integer.parseInt(eventID), AnnualStatusEnum.DENIED.getStatus())
                            ? (DONE + "-" + getApprovedDay(Integer.parseInt(eventID)))
                            : ERROR;
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

    private boolean updateStatus(int eventID, int status) throws SQLException, NamingException {
        EventAnnualDAO dao = new EventAnnualDAO();
        return dao.updateStatus(eventID, status);
    }

    private double getApprovedDay(int eventID) throws SQLException, NamingException {
        EventDAO dao = new EventDAO();
        EventDTO event = dao.getEventDetail(eventID);
        List<HolidayDTO> holidays = getHolidayList();
        return event != null ? TimeUtils.countAnnualLeaveDays(event.getEventStartDate(), event.getEventEndDate(), holidays) : 0;
    }

    private List<HolidayDTO> getHolidayList() throws SQLException, NamingException {
        HolidayDAO dao = new HolidayDAO();
        return dao.getAll();
    }
}
