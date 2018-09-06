/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import datamapping.ErrorJson;
import datamapping.EventJson;
import datamapping.EventTypeEnum;
import entity.EmployeeDTO;
import entity.EventDTO;
import model.EventCarDAO;
import model.EventDAO;
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
import java.time.LocalDateTime;

/**
 * @author Yuu
 */
@WebServlet(name = "UpdateEventCarReservationAjaxServlet", urlPatterns = {"/UpdateEventCarReservationAjaxServlet"})
public class UpdateEventCarReservationAjaxServlet extends HttpServlet {

    private final static int EVENT_TYPE = EventTypeEnum.CAR_RESERVATION.getEventTypeID();
    private final static String ERROR_DATA = "errordata";
    private final static String ERROR_SYSTEM = "errorsystem";
    private final static String ERROR_DATE = "errordate";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Update event object and return json object
     *
     * Request parameters:
     *              + eventID
     *              + eventTitle
     *              + eventContent (can be null/empty)
     *              + eventDate
     *              + allDay (check box)
     *              + eventVenueOther
     *              + eventCar
     *
     * Respone return:
     *              + updated EvenJson object
     *              + new ErrorJson object with errordata (input data missed or error)
     *              + new ErrorJson object with errorsystem (sql query error)
     *              + new ErrorJson object with errordate (start date is after end date)
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
        String eventTitle = request.getParameter("eventTitle");
        String eventContent = request.getParameter("eventContent");
        String eventDate = request.getParameter("eventDate");
        String allDay = request.getParameter("allDay");
        String eventVenueOther = request.getParameter("eventVenueOther");
        String eventCar = request.getParameter("eventCar");

        // Init jackson object to return json
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String result = om.writerWithDefaultPrettyPrinter().writeValueAsString(new ErrorJson(true, ERROR_DATA));

        try {
            if (eventTitle != null && !eventTitle.isEmpty() && eventDate != null && !eventDate.isEmpty()
                    && eventVenueOther != null && !eventVenueOther.isEmpty()
                    && eventCar != null && !eventCar.isEmpty() && eventID != null && !eventID.isEmpty()) {

                HttpSession session = request.getSession();
                EmployeeDTO currentUser = (EmployeeDTO) session.getAttribute("EMPLOYEEDTO");
                EventDTO eventDetail = getEventDetail(Integer.parseInt(eventID));

                // Valid event
                if (EventDTO.checkInvalidEvent(eventDetail, currentUser.getAccount().getAccountID(), EVENT_TYPE)) return;

                // Get localdatetime object from request input
                String[] date = eventDate.split(" - ");
                TimeUtils time = allDay != null
                        ? TimeUtils.getTimeOfDateRange(date[0], date[1])
                        : TimeUtils.getTimeOfDateTimeRange(date[0], date[1]);

                // Valid start/end date
                LocalDateTime eventStartDate = time.getStart(), eventEndDate = time.getEnd();
                if (eventStartDate.isAfter(eventEndDate)) {
                    result = om.writerWithDefaultPrettyPrinter().writeValueAsString(new ErrorJson(true, ERROR_DATE));
                    return;
                }

                EventDTO event = new EventDTO(eventTitle, eventContent.isEmpty() ? null : eventContent,
                        null, eventVenueOther, eventStartDate, eventEndDate);
                event.setEventID(Integer.parseInt(eventID));

                result = updateEventCar(event, Integer.parseInt(eventCar)) ?
                        om.writerWithDefaultPrettyPrinter().writeValueAsString(EventJson.parseToJson(event)) :
                        om.writerWithDefaultPrettyPrinter().writeValueAsString(new ErrorJson(true, ERROR_SYSTEM));

            }
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

    private boolean updateEventCar(EventDTO event, int carID) throws SQLException, NamingException {
        EventCarDAO dao = new EventCarDAO();
        return dao.updateEventCar(event, carID);
    }

    private EventDTO getEventDetail(int eventID) throws SQLException, NamingException {
        EventDAO dao = new EventDAO();
        return dao.getEventDetail(eventID);
    }

}
