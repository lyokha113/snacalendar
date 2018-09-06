/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import datamapping.PermissionEnum;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author yuu
 */
public class EmployeeDTO implements Serializable {

    private AccountDTO account;
    private String fullname;
    private String password;
    private String address;
    private String phone;
    private boolean sex;
    private LocalDate dob;
    private DepartmentDTO department;
    private TitleDTO title;
    private EmpTypeDTO type;
    private EmployeeDTO supervisor;
    private int annualLeaveDay;

    public EmployeeDTO() {
    }

    public EmployeeDTO(AccountDTO account, int annualLeaveDay) {
        this.account = account;
        this.annualLeaveDay = annualLeaveDay;
    }

    public EmployeeDTO(AccountDTO account, String fullname, String address, String phone, boolean sex, LocalDate dob, DepartmentDTO department, TitleDTO title, EmpTypeDTO type, EmployeeDTO supervisor) {
        this.account = account;
        this.fullname = fullname;
        this.address = address;
        this.phone = phone;
        this.sex = sex;
        this.dob = dob;
        this.department = department;
        this.title = title;
        this.type = type;
        this.supervisor = supervisor;
    }

    public static EmployeeDTO getEmployeeDTO(String empAddress, String empPhone, String empName, LocalDate empDoB, int empType, int empDep, int empTitle, boolean empSex) {

        EmployeeDTO emp = new EmployeeDTO();
        emp.setAddress(empAddress);
        emp.setPhone(empPhone);
        emp.setFullname(empName);
        emp.setDob(empDoB);
        emp.setSex(empSex);

        DepartmentDTO dep = new DepartmentDTO(empDep);
        emp.setDepartment(dep);

        EmpTypeDTO type = new EmpTypeDTO(empType);
        emp.setType(type);

        TitleDTO title = new TitleDTO(empTitle);
        emp.setTitle(title);

        return emp;
    }

    // Check validate of supervior
    // Supervisor of normal user must be a super user on same department
    // Supervisor of super user must be a super user of any department
    // Supervisor of a user can't be itself
    public static boolean checkValidSupervisor(int department, int permission, int empID, EmployeeDTO supervisor) {
        if (supervisor == null) return permission == PermissionEnum.SUPER_USER.getPermissionID();
        else if (empID == supervisor.getAccount().getAccountID()) return false;
        else return permission == PermissionEnum.SUPER_USER.getPermissionID()
                    || department == supervisor.department.getDepartmentID();
    }

    /**
     * @return the account
     */
    public AccountDTO getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(AccountDTO account) {
        this.account = account;
    }

    /**
     * @return the fullname
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * @param fullname the fullname to set
     */
    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the sex
     */
    public boolean isSex() {
        return sex;
    }

    /**
     * @param sex the sex to set
     */
    public void setSex(boolean sex) {
        this.sex = sex;
    }

    /**
     * @return the dob
     */
    public LocalDate getDob() {
        return dob;
    }

    /**
     * @param dob the dob to set
     */
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    /**
     * @return the department
     */
    public DepartmentDTO getDepartment() {
        return department;
    }

    /**
     * @param department the department to set
     */
    public void setDepartment(DepartmentDTO department) {
        this.department = department;
    }

    /**
     * @return the title
     */
    public TitleDTO getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(TitleDTO title) {
        this.title = title;
    }

    /**
     * @return the type
     */
    public EmpTypeDTO getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(EmpTypeDTO type) {
        this.type = type;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public EmployeeDTO getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(EmployeeDTO supervisor) {
        this.supervisor = supervisor;
    }


    public int getAnnualLeaveDay() {
        return annualLeaveDay;
    }

    public void setAnnualLeaveDay(int annualLeaveDay) {
        this.annualLeaveDay = annualLeaveDay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeDTO that = (EmployeeDTO) o;
        return Objects.equals(getAccount().getAccountID(), that.getAccount().getAccountID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccount().getAccountID());
    }
}
