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
import model.EventAttendeeDAO;

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
@WebServlet(name = "GetEventAttendeesAjaxServlet", urlPatterns = {"/GetEventAttendeesAjaxServlet"})
public class GetEventAttendeesAjaxServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Get list events attendees and return json
     *
     * Respone return json object:
     *              + new EmployeeDTO list as Json object array
     *              + new ErrorJson object with status "request error"
     *              + new ErrorJson object with status "No attendees"
     *              + new ErrorJson object with status "system error"
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String eventID = request.getParameter("eventID");

        // Init jackson object to return json
        ObjectMapper om = new ObjectMapper();
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String result = om.writerWithDefaultPrettyPrinter().writeValueAsString(new ErrorJson(true, "request error"));

        try {
            if (eventID != null && !eventID.isEmpty()) {
                List<EmployeeDTO> list = getEventAttendees(Integer.parseInt(eventID));
                result = om.writerWithDefaultPrettyPrinter().writeValueAsString(
                        list != null ? list : new ErrorJson(true, "No attendees"));
            }
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

    private List<EmployeeDTO> getEventAttendees(int eventID) throws SQLException, NamingException {
        EventAttendeeDAO dao = new EventAttendeeDAO();
        return dao.getEventAttendees(eventID);
    }
}
