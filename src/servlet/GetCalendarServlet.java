/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import datamapping.EventTypeEnum;
import entity.CarDTO;
import entity.CustomerDTO;
import entity.DepartmentDTO;
import entity.EmployeeDTO;
import model.CarDAO;
import model.CustomerDAO;
import model.DepartmentDAO;
import model.EmployeeDAO;

import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
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
@WebServlet(name = "GetCalendarServlet", urlPatterns = {"/GetCalendarServlet"})
public class GetCalendarServlet extends HttpServlet {

    private final static String ERROR_PAGE_500 = "500.html";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * Get necessary list for request calendar page
     *
     * Page forward:
     *         + Request calendar page base on EventTypeEnum
     *         + 500.html (error page)
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String type = request.getParameter("type");
        String url = ERROR_PAGE_500;

        try {
            if (type != null && !type.isEmpty()) {
                EventTypeEnum eventType = EventTypeEnum.getEventType(Integer.parseInt(type));
                url = eventType.getEventPage();
                List<EmployeeDTO> employeeList;
                List<DepartmentDTO> departmentDTO;
                List<CustomerDTO> customerList;
                List<CarDTO> carList;
                switch (eventType) {
                    case CUSTOMER_CARE:
                        employeeList = getEmployeeList();
                        departmentDTO = getDepartmentList();
                        customerList = getCustomerList();
                        carList = getCarList();
                        request.setAttribute("EMPLOYEELIST", employeeList);
                        request.setAttribute("DEPARTMENTLIST", departmentDTO);
                        request.setAttribute("CUSTOMERLIST", customerList);
                        request.setAttribute("CARLIST", carList);
                        break;
                    case CAR_RESERVATION:
                        carList = getCarList();
                        request.setAttribute("CARLIST", carList);
                        break;
                    case TRAINING:
                        employeeList = getEmployeeList();
                        request.setAttribute("EMPLOYEELIST", employeeList);
                        break;
                    case BIZ_TRIP:
                    case ANNUAL_LEAVE:
                        break;
                }
            }

        } catch (SQLException | NamingException | NumberFormatException e) {
            log(e.toString());
        } finally {
            RequestDispatcher rd = request.getRequestDispatcher(url);
            rd.forward(request, response);
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

    private List<EmployeeDTO> getEmployeeList() throws SQLException, NamingException {
        EmployeeDAO dao = new EmployeeDAO();
        List<EmployeeDTO> all = dao.getAll();
        return all != null ? all.stream().filter(emp -> emp.getAccount().isActive()).collect(Collectors.toList()) : null;
    }

    private List<DepartmentDTO> getDepartmentList() throws SQLException, NamingException {
        DepartmentDAO dao = new DepartmentDAO();
        return dao.getAll();
    }

    private List<CustomerDTO> getCustomerList() throws SQLException, NamingException {
        CustomerDAO dao = new CustomerDAO();
        return dao.getAll();
    }

    private List<CarDTO> getCarList() throws SQLException, NamingException {
        CarDAO dao = new CarDAO();
        return dao.getAll();
    }

}
