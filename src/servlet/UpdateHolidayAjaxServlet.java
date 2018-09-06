/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import datamapping.HolidayVacationEnum;
import entity.HolidayDTO;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HolidayDAO;

/**
 *
 * @author Yuu
 */
@WebServlet(name = "UpdateHolidayAjaxServlet", urlPatterns = {"/UpdateHolidayAjaxServlet"})
public class UpdateHolidayAjaxServlet extends HttpServlet {

    private final static String DONE = "done";
    private final static String ERROR_DATA = "errordata";
    private final static String ERROR_SYSTEM = "errorsystem";
    private final static String ERROR_DATE = "errordate";
    private final static String NAME_UNIQUE = "name_unique";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Update holiday object and return the result state to ajax
     *
     * Request parameters:
     *              + holidayID
     *              + holidayName
     *              + holidayDate
     *              + holidayVacation (working or vacation day off)
     *
     * Respone return:
     *              + done
     *              + errordata (input data missed or error)
     *              + errorsystem (sql query error)
     *              + errordate (start date is after end date)
     *              + name_unique (duplicate for customer name)
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String holidayID = request.getParameter("holidayID");
        String holidayName = request.getParameter("holidayName");
        String holidayDate = request.getParameter("holidayDate");
        String holidayVacation = request.getParameter("holidayVacation");
        String state = ERROR_DATA;

        try {
            if (holidayName != null && !holidayName.isEmpty() && holidayID != null && !holidayID.isEmpty()
                    && holidayDate != null && !holidayDate.isEmpty()) {

                // Split input date string and set default year to insert database
                final int YEAR_DEFAULT = 2018;
                String [] date = holidayDate.split(" - ");
                Month startMonth = Month.valueOf(date[0].split(" ")[0].toUpperCase());
                Month endMonth = Month.valueOf(date[1].split(" ")[0].toUpperCase());
                int startDay = Integer.parseInt(date[0].split(" ")[1]);
                int endDay = Integer.parseInt(date[1].split(" ")[1]);

                HolidayDTO dto = HolidayDTO.getHolidayDTO(holidayName,
                        LocalDate.of(YEAR_DEFAULT, startMonth, startDay), LocalDate.of(YEAR_DEFAULT, endMonth, endDay),
                        HolidayVacationEnum.getHolidayVacation(holidayVacation != null).isVacation());
                dto.setHolidayID(Integer.parseInt(holidayID));

                if (dto.getHolidayStartDate().isAfter(dto.getHolidayEndDate())) {
                    state = ERROR_DATE;
                } else {
                    state = updateHoliday(dto) ? DONE : ERROR_SYSTEM;
                }

            }
        } catch (SQLException | NumberFormatException | NamingException e) {
            if (e.getMessage().contains("Duplicate")) {
                state = NAME_UNIQUE;
            } else {
                log(e.toString());
                state = ERROR_SYSTEM;
            }
        } finally {
            response.getWriter().write(state);
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
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

    private boolean updateHoliday(HolidayDTO dto) throws SQLException, NamingException {
        HolidayDAO dao = new HolidayDAO();
        return dao.updateHoliday(dto);
    }
}
