/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import entity.DepartmentDTO;
import entity.EmployeeDTO;
import model.DepartmentDAO;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author yuu
 */
@WebServlet(name = "UpdateDepartmentAjaxServlet", urlPatterns = {"/UpdateDepartmentAjaxServlet"})
public class UpdateDepartmentAjaxServlet extends HttpServlet {

    private final static String DONE = "done";
    private final static String ERROR_DATA = "errordata";
    private final static String ERROR_SYSTEM = "errorsystem";
    private final static String NAME_UNIQUE = "name_unique";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Update department object and return the result state to ajax
     *
     * Request parameters:
     *              + depID
     *              + depName
     *
     * Respone return:
     *              + done
     *              + errordata (input data missed or error)
     *              + errorsystem (sql query error)
     *              + name_unique (duplicate for department name)
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String depID = request.getParameter("depID");
        String depName = request.getParameter("depName");
        String state = ERROR_DATA;

        try {
            if (depID != null && !depID.isEmpty() && depName != null && !depName.isEmpty()) {

                DepartmentDTO dto = DepartmentDTO.getDepartmentDTO(depName);
                dto.setDepartmentID(Integer.parseInt(depID));

                state = updateDepartment(dto) ? DONE : ERROR_SYSTEM;

                //Reset current account dto if it department is updated
                if (state.equals(DONE)) {
                    HttpSession session = request.getSession();
                    EmployeeDTO currentAccount = (EmployeeDTO) session.getAttribute("EMPLOYEEDTO");
                    if (currentAccount.getDepartment().getDepartmentID() == Integer.parseInt(depID)) {
                        currentAccount.setDepartment(dto);
                        session.setAttribute("EMPLOYEEDTO", currentAccount);
                    }
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

    private boolean updateDepartment(DepartmentDTO dto) throws SQLException, NamingException {
        DepartmentDAO dao = new DepartmentDAO();
        return dao.updateDepartment(dto);
    }
}
