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
import entity.EventAnnualDTO;
import entity.EventCarDTO;
import entity.EventDTO;
import model.EventDAO;

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

/**
 * @author Yuu
 */
@WebServlet(name = "GetEventAjaxServlet", urlPatterns = {"/GetEventAjaxServlet"})
public class GetEventAjaxServlet extends HttpServlet {


    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Get list events base on event type and return json object array
     *
     * Request parameters:
     *              + eventType
     *
     * Respone return json object:
     *              + new EventJson list as Json object array
     *              + new ErrorJson object with status "request error"
     *              + new ErrorJson object with status "no data"
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


        String eventType = request.getParameter("eventType");

        // Init jackson object to return json
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        ErrorJson error = new ErrorJson(true, "request error");
        String result = om.writerWithDefaultPrettyPrinter().writeValueAsString(error);

        try {
            if (eventType != null && !eventType.isEmpty()) {
                EventTypeEnum eventTypeID = EventTypeEnum.getEventType(Integer.parseInt(eventType));
                error.setStatus("no data");
                List<EventJson> events = null;
                switch (eventTypeID) {
                    case CUSTOMER_CARE:
                        events = getEventCustomerCareJson();
                        break;
                    case TRAINING:
                        events = getEventTraining();
                        break;
                    case BIZ_TRIP:
                        events = getEventBizTripJson();
                        break;
                    case CAR_RESERVATION:
                        events = getEventCarReservation();
                        break;
                    case ANNUAL_LEAVE:
                        events = getEventAnnualLeave();
                        break;
                }
                result = om.writerWithDefaultPrettyPrinter().writeValueAsString(events != null ? events : error);
            }
        } catch (SQLException | NamingException e) {
            log(e.toString());
            error.setStatus("system error");
            result = om.writerWithDefaultPrettyPrinter().writeValueAsString(error);
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

    private List<EventJson> getEventCustomerCareJson() throws SQLException, NamingException {
        List<EventCarDTO> eventList = new EventDAO().getEventCustomerCare();
        return parseToJsonCar(eventList);
    }

    private List<EventJson> getEventCarReservation() throws SQLException, NamingException {
        List<EventCarDTO> eventList = new EventDAO().getEventCarReservation();
        return parseToJsonCar(eventList);
    }

    private List<EventJson> getEventBizTripJson() throws SQLException, NamingException {
        List<EventDTO> eventList = new EventDAO().getEventBizTrip();
        return parseToJson(eventList);
    }

    private List<EventJson> getEventTraining() throws SQLException, NamingException {
        List<EventDTO> eventList = new EventDAO().getEventTraining();
        return parseToJson(eventList);
    }

    private List<EventJson> getEventAnnualLeave() throws SQLException, NamingException {
        List<EventAnnualDTO> eventList = new EventDAO().getEventAnnualLeave();
        return parseToJsonAnnual(eventList);
    }

    private List<EventJson> parseToJson(List<EventDTO> eventList) {
        if (eventList == null || eventList.isEmpty()) return null;
        return eventList.stream().map(EventJson::parseToJson).collect(Collectors.toList());
    }

    private List<EventJson> parseToJsonCar(List<EventCarDTO> eventList) {
        if (eventList == null || eventList.isEmpty()) return null;
        return eventList.stream()
                .map(EventJson::parseToJson)
                .collect(Collectors.toList());
    }

    private List<EventJson> parseToJsonAnnual(List<EventAnnualDTO> eventList) {
        if (eventList == null || eventList.isEmpty()) return null;
        return eventList.stream()
                .map(EventJson::parseToJson)
                .collect(Collectors.toList());
    }
}

