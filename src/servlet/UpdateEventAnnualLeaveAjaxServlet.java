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
import datamapping.*;
import entity.EmployeeDTO;
import entity.EventAnnualDTO;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Yuu
 */
@WebServlet(name = "UpdateEventAnnualLeaveAjaxServlet", urlPatterns = {"/UpdateEventAnnualLeaveAjaxServlet"})
public class UpdateEventAnnualLeaveAjaxServlet extends HttpServlet {

    private final static int EVENT_TYPE = EventTypeEnum.ANNUAL_LEAVE.getEventTypeID();
    private final static String ERROR_DATA = "errordata";
    private final static String ERROR_SYSTEM = "errorsystem";
    private final static String ERROR_DATE = "errordate";
    private final static String ERROR_HOLIDAY = "errorholiday";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Update event object and returnjson object
     *
     * Request parameters:
     *              + eventID
     *              + eventDate
     *              + allDay (check box)
     *              + eventContent
     *
     * Respone return json object:
     *              + new EvenJson object
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
        String eventDate = request.getParameter("eventDate");
        String allDay = request.getParameter("allDay");
        String eventContent = request.getParameter("eventContent");

        // Init jackson object to return json
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String result = om.writerWithDefaultPrettyPrinter().writeValueAsString(new ErrorJson(true, ERROR_DATA));

        try {
            if (eventDate != null && !eventDate.isEmpty() && eventContent != null && !eventContent.isEmpty()
                    && eventID != null && !eventID.isEmpty()) {

                HttpSession session = request.getSession();
                EmployeeDTO currentUser = (EmployeeDTO) session.getAttribute("EMPLOYEEDTO");
                EventDTO eventDetail = getEventDetail(Integer.parseInt(eventID));

                // Valid event
                if (EventDTO.checkInvalidEvent(eventDetail, currentUser.getAccount().getAccountID(), EVENT_TYPE))
                    return;
                if (EventAnnualDTO.checkInvalidEvent(getEventStatus(Integer.parseInt(eventID)),
                        PermissionEnum.getEventType(currentUser.getTitle().getPermission().getPermissionID())))
                    return;

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

                // Valid with holidays
                int startDayOfYear = time.getStart().getDayOfYear();
                int endDayOfYear = time.getEnd().getDayOfYear();
                if (checkAnnualLeaveWithHoliday(startDayOfYear, endDayOfYear)) {
                    result = om.writerWithDefaultPrettyPrinter().writeValueAsString(new ErrorJson(true, ERROR_HOLIDAY));
                    return;
                }

                List<HolidayDTO> holidays = getHolidayList();
                String eventTitle = TimeUtils.getAnnualLeaveTitle(time.getStart(), time.getEnd(), holidays);

                EventDTO event = new EventDTO(eventTitle, eventContent,
                        null, null, eventStartDate, eventEndDate);
                event.setEventID(Integer.parseInt(eventID));

                result = updateEvent(event) ?
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

    private boolean updateEvent(EventDTO dto) throws SQLException, NamingException {
        EventDAO dao = new EventDAO();
        return dao.updateEvent(dto);
    }

    private EventDTO getEventDetail(int eventID) throws SQLException, NamingException {
        EventDAO dao = new EventDAO();
        return dao.getEventDetail(eventID);
    }

    private AnnualStatusEnum getEventStatus(int eventID) throws SQLException, NamingException {
        EventAnnualDAO dao = new EventAnnualDAO();
        return dao.getStatus(eventID);
    }

    private boolean checkAnnualLeaveWithHoliday(int start, int end) throws SQLException, NamingException {
        EventAnnualDAO dao = new EventAnnualDAO();
        return dao.checkAnnualLeaveWithHoliday(start, end);
    }

    private List<HolidayDTO> getHolidayList() throws SQLException, NamingException {
        HolidayDAO dao = new HolidayDAO();
        return dao.getAll();
    }

}
