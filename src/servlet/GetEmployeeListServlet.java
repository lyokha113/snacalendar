/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import entity.DepartmentDTO;
import entity.EmpTypeDTO;
import entity.EmployeeDTO;
import entity.TitleDTO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.DepartmentDAO;
import model.EmpTypeDAO;
import model.EmployeeDAO;
import model.TitleDAO;

/**
 *
 * @author yuu
 */
@WebServlet(name = "GetEmployeeListServlet", urlPatterns = {"/GetEmployeeListServlet"})
public class GetEmployeeListServlet extends HttpServlet {

    private final static String PAGE = "employeelist.jsp";
    private final static String ERROR_PAGE_500 = "500.html";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Get list employee and relative things then forward to page.
     *
     * Page forward:
     *         + employeelist.jsp
     *         + 500.html (error page)
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String url = PAGE;

        try {
            List<EmployeeDTO> empList = getEmployeeList();
            request.setAttribute("EMPLOYEELIST", empList);

            List<DepartmentDTO> depList = getDepartmentList();
            request.setAttribute("DEPLIST", depList);

            List<TitleDTO> titleList = getTitleList();
            request.setAttribute("TITLELIST", titleList);

            List<EmpTypeDTO> empTypeList = getEmpTypeList();
            request.setAttribute("EMPTYPELIST", empTypeList);
        } catch (SQLException | NamingException e) {
            log(e.toString());
            url = ERROR_PAGE_500;
        } finally {
            RequestDispatcher rd = request.getRequestDispatcher(url);
            rd.forward(request, response);
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

    private List<EmployeeDTO> getEmployeeList() throws SQLException, NamingException {
        EmployeeDAO dao = new EmployeeDAO();
        return dao.getAll();
    }

    private List<DepartmentDTO> getDepartmentList() throws SQLException, NamingException {
        DepartmentDAO dao = new DepartmentDAO();
        return dao.getAll();
    }

    private List<TitleDTO> getTitleList() throws SQLException, NamingException {
        TitleDAO dao = new TitleDAO();
        return dao.getAll();
    }

    private List<EmpTypeDTO> getEmpTypeList() throws SQLException, NamingException {
        EmpTypeDAO dao = new EmpTypeDAO();
        return dao.getAll();
    }

}
