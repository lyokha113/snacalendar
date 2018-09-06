/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import entity.EmployeeDTO;
import entity.EventAnnualDTO;
import entity.EventDTO;
import entity.HolidayDTO;
import model.EmployeeDAO;
import model.EventAnnualDAO;
import model.HolidayDAO;
import utils.TimeUtils;

import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yuu
 */
@WebServlet(name = "GetAnnualLeaveListServlet", urlPatterns = {"/GetAnnualLeaveListServlet"})
public class GetAnnualLeaveListServlet extends HttpServlet {

    private final static String PAGE = "annualleavelist.jsp";
    private final static String ERROR_PAGE_500 = "500.html";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Get list annual leave then forward to page.
     *
     * Page forward:
     *         + annualleavelist.jsp
     *         + 500.html (error page)
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String url = PAGE;

        try {
            HttpSession session = request.getSession();
            EmployeeDTO currentUser = (EmployeeDTO) session.getAttribute("EMPLOYEEDTO");

            // Check if user is admin or super user. Admin will see annual leave of all employees
            Integer depID = currentUser.getAccount().isAdmin() ? null : currentUser.getDepartment().getDepartmentID();

            // Get list annual leave approved
            List<EmployeeDTO> employees = getActiveWithAnnualLeaveDay(depID);
            List<EventDTO> events = getListAnnualLeave(depID);
            Map<EmployeeDTO, Double> annualLeaveMap = getEmployeeListAnnualLeave(events, employees);
            request.setAttribute("ANNUALLEAVE", annualLeaveMap);

            // Get list request annual leave
            List<EventAnnualDTO> requestList = getListRequestAnnualLeave(currentUser);
            request.setAttribute("REQUESTLIST", requestList);

        } catch (SQLException | NamingException e) {
            log(e.toString());
            url = ERROR_PAGE_500;
        } finally {
            RequestDispatcher rd = request.getRequestDispatcher(url);
            rd.forward(request, response);
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

    private List<EventDTO> getListAnnualLeave(Integer depID) throws SQLException, NamingException {
        EventAnnualDAO dao = new EventAnnualDAO();
        return dao.getListAnnualLeave(depID);
    }

    private List<EventAnnualDTO> getListRequestAnnualLeave(EmployeeDTO emp) throws SQLException, NamingException {
        EventAnnualDAO dao = new EventAnnualDAO();
        return dao.getListRequestAnnualLeave(emp);
    }

    private Map<EmployeeDTO, Double> getEmployeeListAnnualLeave(List<EventDTO> events, List<EmployeeDTO> employees) throws SQLException, NamingException {
        if (employees == null) return null;

        List<HolidayDTO> holidays = getHolidayList();

        Map<EmployeeDTO, Double> result = new LinkedHashMap<>();

        for (EmployeeDTO employee : employees) {

            if (events == null || events.isEmpty()) {
                result.put(employee, 0.0);
                continue;
            }

            List<EventDTO> collect = events.stream()
                    .filter(event -> event.getCreator().equals(employee))
                    .collect(Collectors.toList());

            events.removeIf(event -> event.getCreator().equals(employee));

            double days = collect.stream()
                    .mapToDouble(event -> TimeUtils.countAnnualLeaveDays(
                            event.getEventStartDate(), event.getEventEndDate(), holidays))
                    .sum();
            result.put(employee, days);
        }
        return result;
    }

    private List<EmployeeDTO> getActiveWithAnnualLeaveDay(Integer depID) throws SQLException, NamingException {
        EmployeeDAO dao = new EmployeeDAO();
        return dao.getActiveWithAnnualLeaveDay(depID);
    }

    private List<HolidayDTO> getHolidayList() throws SQLException, NamingException {
        HolidayDAO dao = new HolidayDAO();
        return dao.getAll();
    }

}
