/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import datamapping.ErrorJson;
import entity.EventAttendeeDTO;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Yuu
 */
@WebServlet(name = "SetEventAttendeesAjaxServlet", urlPatterns = {"/SetEventAttendeesAjaxServlet"})
public class SetEventAttendeesAjaxServlet extends HttpServlet {

    private final static String ERROR_DATA = "errordata";
    private final static String ERROR_SYSTEM = "errorsystem";
    private final static String DONE = "done";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Set event attendees for event object and return json
     *
     * Request parameters:
     *              + eventID
     *              + eventAttendeeMails (Email array of attendees)
     *
     * Respone return json object:
     *              + new ErrorJson object with status "done" and error false
     *              + new ErrorJson object with errordata (input data missed or error)
     *              + new ErrorJson object with errorsystem (sql query error)
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        String[] eventAttendees = request.getParameterValues("attendees[]");
        String eventID = request.getParameter("eventID");

        // Init jackson object to return json
        ObjectMapper om = new ObjectMapper();
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String result = om.writerWithDefaultPrettyPrinter().writeValueAsString(new ErrorJson(true, ERROR_DATA));

        try {
            List<Integer> listEmpID = Stream.of(eventAttendees).map(Integer::parseInt).collect(Collectors.toList());
            EventAttendeeDTO attendees = new EventAttendeeDTO(listEmpID, Integer.parseInt(eventID));
                result = addEventAttendees(attendees)
                       ? om.writerWithDefaultPrettyPrinter().writeValueAsString(new ErrorJson(false, DONE))
                       : om.writerWithDefaultPrettyPrinter().writeValueAsString(new ErrorJson(true, ERROR_SYSTEM));

        } catch (SQLException | NumberFormatException | NamingException e) {
            log(e.toString());
            result = om.writerWithDefaultPrettyPrinter().writeValueAsString(new ErrorJson(true, ERROR_SYSTEM));
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

    private boolean addEventAttendees(EventAttendeeDTO dto) throws SQLException, NamingException {
        EventAttendeeDAO dao = new EventAttendeeDAO();
        return dao.addEventAttendees(dto);
    }
}
