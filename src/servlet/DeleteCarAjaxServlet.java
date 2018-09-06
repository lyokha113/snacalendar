/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import java.io.IOException;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.CarDAO;
import model.EventCarDAO;

/**
 *
 * @author Yuu
 */
@WebServlet(name = "DeleteCarAjaxServlet", urlPatterns = {"/DeleteCarAjaxServlet"})
public class DeleteCarAjaxServlet extends HttpServlet {

    private final static String DONE = "done";
    private final static String ERROR = "error";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Delete car object and return the result state to ajax
     *
     * Request parameters:
     *              + carID
     *
     * Respone return:
     *              + done
     *              + error
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String carID = request.getParameter("carID");
        String state = ERROR;

        try {
            if (carID != null && !carID.isEmpty()) {
                state = deleteCar(Integer.parseInt(carID)) ? DONE : ERROR;

                // Delete car reservation relate to this carID
                if (state.equals(DONE)) deleteEventCarReservationEmpty();
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

    private boolean deleteCar(int carID) throws SQLException, NamingException {
        CarDAO dao = new CarDAO();
        return dao.deleteCar(carID);
    }

    private void deleteEventCarReservationEmpty() throws SQLException, NamingException {
        EventCarDAO dao = new EventCarDAO();
        dao.deleteEventCarReservationEmpty();
    }
}
