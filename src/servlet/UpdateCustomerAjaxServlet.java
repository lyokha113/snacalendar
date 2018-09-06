/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import entity.CustomerDTO;
import java.io.IOException;
import java.sql.SQLException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.CustomerDAO;

/**
 *
 * @author Yuu
 */
@WebServlet(name = "UpdateCustomerAjaxServlet", urlPatterns = {"/UpdateCustomerAjaxServlet"})
public class UpdateCustomerAjaxServlet extends HttpServlet {

    private final static String DONE = "done";
    private final static String ERROR_DATA = "errordata";
    private final static String ERROR_SYSTEM = "errorsystem";
    private final static String NAME_UNIQUE = "name_unique";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Update customer object and return the result state to ajax
     *
     * Request parameters:
     *              + custID
     *              + custName
     *              + custAddress
     *
     * Respone return:
     *              + done
     *              + errordata (input data missed or error)
     *              + errorsystem (sql query error)
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

        String custID = request.getParameter("custID");
        String custName = request.getParameter("custName");
        String custAddress = request.getParameter("custAddress");
        String state = ERROR_DATA;

        try {
            if (custName != null && !custName.isEmpty() && custAddress != null && !custAddress.isEmpty()
                    && custID != null && !custID.isEmpty()) {

                CustomerDTO dto = CustomerDTO.getCustomerDTO(custName, custAddress);
                dto.setCustomerID(Integer.parseInt(custID));

                state = updateCustomer(dto) ? DONE : ERROR_SYSTEM;
                
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

    private boolean updateCustomer(CustomerDTO dto) throws SQLException, NamingException {
        CustomerDAO dao = new CustomerDAO();
        return dao.updateCustomer(dto);
    }
}
