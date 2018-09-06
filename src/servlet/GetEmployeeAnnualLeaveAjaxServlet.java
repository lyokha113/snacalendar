/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import datamapping.ErrorJson;
import entity.EmployeeDTO;
import entity.EventDTO;
import entity.HolidayDTO;
import model.EventAnnualDAO;
import model.HolidayDAO;
import utils.TimeUtils;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Yuu
 */
@WebServlet(name = "GetEmployeeAnnualLeaveAjaxServlet", urlPatterns = {"/GetEmployeeAnnualLeaveAjaxServlet"})
public class GetEmployeeAnnualLeaveAjaxServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Get employee annual leave and return json object array
     *
     * Respone return json object:
     *              + a double number of days as json object
     *              + new ErrorJson object with status "request error"
     *              + new ErrorJson object with status "system error"
     *
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        // Init jackson object to return json
        ObjectMapper om = new ObjectMapper();
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String result = om.writerWithDefaultPrettyPrinter().writeValueAsString(new ErrorJson(true, "request error"));

        try {
            HttpSession session = request.getSession();
            int empID = ((EmployeeDTO) session.getAttribute("EMPLOYEEDTO")).getAccount().getAccountID();
            result = om.writerWithDefaultPrettyPrinter().writeValueAsString(getEmployeeAnnualLeave(empID));
        } catch (SQLException | NamingException e) {
            log(e.toString());
            result = om.writerWithDefaultPrettyPrinter().writeValueAsString(new ErrorJson(true, "system error"));
        } finally {
            response.getWriter().write(result);
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

    private double getEmployeeAnnualLeave(int empID) throws SQLException, NamingException {
        EventAnnualDAO dao = new EventAnnualDAO();
        List<EventDTO> list = dao.getEmployeeAnnualLeave(empID);
        List<HolidayDTO> holidays = getHolidayList();
        if (list == null || list.isEmpty()) return 0;
        return list.stream()
                .mapToDouble(event -> TimeUtils.countAnnualLeaveDays(event.getEventStartDate(),event.getEventEndDate(), holidays))
                .sum();
    }

    private List<HolidayDTO> getHolidayList() throws SQLException, NamingException {
        HolidayDAO dao = new HolidayDAO();
        return dao.getAll();
    }
}
