/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import datamapping.SendMailTypeEnum;
import entity.EmployeeDTO;
import utils.Mailer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Yuu
 */
@WebServlet(name = "SendEventAttendeesAjaxServlet", urlPatterns = {"/SendEventAttendeesAjaxServlet"})
public class SendEventAttendeesAjaxServlet extends HttpServlet {

    private final static String ERROR = "error";
    private final static String DONE = "done";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Send email to event attendees and return state
     *
     * Request parameters:
     *              + eventAttendeeMails (Email array of attendees)
     *              + eventTitle
     *              + type (sending type)
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

        String[] eventAttendeeMails = request.getParameterValues("emailList[]");
        String eventTitle = request.getParameter("eventTitle");
        String type = request.getParameter("type");
        String state = ERROR;
        try {
            if (eventAttendeeMails != null && eventAttendeeMails.length != 0
                    && eventTitle != null && !eventTitle.isEmpty() && type != null && !type.isEmpty()) {
                HttpSession session = request.getSession();
                String sender = ((EmployeeDTO) session.getAttribute("EMPLOYEEDTO")).getFullname();
                SendMailTypeEnum mailType = SendMailTypeEnum.getSendMailType(type);
                switch (mailType) {
                    case NEW_CUSTOMER_CARE:
                        sendNewCustomerCare(eventAttendeeMails, sender, eventTitle);
                        break;
                    case UPDATE_CUSTOMER_CARE:
                        sendUpdateCustomerCare(eventAttendeeMails, sender, eventTitle);
                        break;
                    case NEW_TRAINING:
                        sendNewEventTraining(eventAttendeeMails, sender, eventTitle);
                        break;
                    case UPDATE_TRAINING:
                        sendUpdateEventTraining(eventAttendeeMails, sender, eventTitle);
                        break;
                }
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

    private void sendNewCustomerCare(String[] eventAttendeeMails, String sender, String title) {
        for (String eventAttendeeMail : eventAttendeeMails) {
            Mailer.sendNewEventCustomerCare(eventAttendeeMail, sender, title);
        }
    }

    private void sendUpdateCustomerCare(String[] eventAttendeeMails, String sender, String title) {
        for (String eventAttendeeMail : eventAttendeeMails) {
            Mailer.sendUpdateEventCustomerCare(eventAttendeeMail, sender, title);
        }
    }

    private void sendNewEventTraining(String[] eventAttendeeMails, String sender, String title) {
        for (String eventAttendeeMail : eventAttendeeMails) {
            Mailer.sendNewEventTraining(eventAttendeeMail, sender, title);
        }
    }

    private void sendUpdateEventTraining(String[] eventAttendeeMails, String sender, String title) {
        for (String eventAttendeeMail : eventAttendeeMails) {
            Mailer.sendUpdateEventTraining(eventAttendeeMail, sender, title);
        }
    }
}
