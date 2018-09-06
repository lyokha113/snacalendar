/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import datamapping.PermissionEnum;
import entity.EmployeeDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handle request authorized and return url to Filter
 *
 */
public class Authorized implements Serializable {

    //Page
    private static final String NOTFOUND_PAGE = "404.html";
    private static final String ERROR_PAGE = "505.html";
    private static final String LOGIN_PAGE = "login.jsp";
    private static final String USER_PAGE = "user.jsp";
    private static final String RECOVERY_PAGE = "recovery.jsp";
    private static final String CARLIST_PAGE = "carlist.jsp";
    private static final String CUSTOMERLIST_PAGE = "customerlist.jsp";
    private static final String DEPARTMENTLIST_PAGE = "departmentlist.jsp";
    private static final String EMPLOYEELIST_PAGE = "employeelist.jsp";
    private static final String HOLIDAYLIST_PAGE = "holidaylist.jsp";
    private static final String TITLELIST_PAGE = "titlelist.jsp";
    private static final String CUSTOMERCARE_PAGE = "customercare.jsp";
    private static final String CARRESERVATION_PAGE = "carreservation.jsp";
    private static final String BIZTRIP_PAGE = "biztrip.jsp";
    private static final String TRAINING_PAGE = "training.jsp";
    private static final String ANNUALLEAVE_PAGE = "annualleave.jsp";
    private static final String HOME_PAGE = "homepage.jsp";

    // Common servlets
    private static final String LOGIN_SERVLET = "LoginServlet";
    private static final String LOGINCOOKIE_SERVLET = "LoginCookieServlet";
    private static final String SENDRECOVERYMAIL_SERVLET = "SendRecoveryMailServlet";
    private static final String GETRECOVERYLINK_SERVLET = "GetRecoveryLinkServlet";
    private static final String RECOVERACCOUNTPASSWORDAJAX_SERVLET = "RecoverAccountPasswordAjaxServlet";
    private static final String LOGOUT_SERVLET = "LogoutServlet";
    private static final String CHANGEUSERPROFILEPASSWORDAJAX_SERLVET = "ChangeUserProfilePasswordAjaxServlet";
    private static final String UPDATEUSERPROFILEAJAX_SERVLET = "UpdateUserProfileAjaxServlet";

    // Car servlets
    private static final String GETCARLIST_SERVLET = "GetCarListServlet";
    private static final String ADDCARAJAX_SERVLET = "AddCarAjaxServlet";
    private static final String UPDATECARAJAX_SERVLET = "UpdateCarAjaxServlet";
    private static final String DELETECARAJAX_SERVLET = "DeleteCarAjaxServlet";

    // Customer servlets
    private static final String GETCUSTOMERLIST_SERVLET = "GetCustomerListServlet";
    private static final String ADDCUTOMERAJAX_SERVLET = "AddCustomerAjaxServlet";
    private static final String UPDATECUTOMERAJAX_SERVLET = "UpdateCustomerAjaxServlet";
    private static final String DELETECUTOMERAJAX_SERVLET = "DeleteCustomerAjaxServlet";

    // Department servlets
    private static final String GETDEPARTMENTLIST_SERVLET = "GetDepartmentListServlet";
    private static final String ADDDEPARTMENTAJAX_SERVLET = "AddDepartmentAjaxServlet";
    private static final String UPDATEDEPARTMENTAJAX_SERVLET = "UpdateDepartmentAjaxServlet";
    private static final String DELETEDEPARTMENTAJAX_SERVLET = "DeleteDepartmentAjaxServlet";

    // Employee servlets
    private static final String GETEMPLOYEELIST_SERVLET = "GetEmployeeListServlet";
    private static final String GETSUPERVISORLISTAJAX_SERVLET = "GetSupervisorListAjaxServlet";
    private static final String ADDEMPLOYEEAJAX_SERVLET = "AddEmployeeAjaxServlet";
    private static final String UPDATEEMPLOYEEAJAX_SERVLET = "UpdateEmployeeAjaxServlet";
    private static final String CHANGEEMPLOYEEPASSWORDAJAX_SERLVET = "ChangeEmployeePasswordAjaxServlet";
    private static final String CHANGEACTIVEACCOUNTAJAX_SERVLET = "ChangeActiveAccountAjaxServlet";
    private static final String CHANGEANNUALLEAVELIMITTAJAX_SERVLET = "ChangeAnnualLeaveLimitAjaxServlet";

    // Holiday servlets
    private static final String GETHOLIDAYLIST_SERVLET = "GetHolidayListServlet";
    private static final String ADDHOLIDAYAJAX_SERVLET = "AddHolidayAjaxServlet";
    private static final String UPDATEHOLIDAYAJAX_SERVLET = "UpdateHolidayAjaxServlet";
    private static final String DELETEHOLIDAYAJAX_SERVLET = "DeleteHolidayAjaxServlet";

    // Title servlets
    private static final String GETTITLELIST_SERVLET = "GetTitleListServlet";
    private static final String ADDTITLEAJAX_SERVLET = "AddTitleAjaxServlet";
    private static final String UPDATETITLEAJAX_SERVLET = "UpdateTitleAjaxServlet";
    private static final String DELETETITLEAJAX_SERVLET = "DeleteTitleAjaxServlet";

    //Calendar servlets
    private static final String GETCALENDAR_SERVLET = "GetCalendarServlet";
    private static final String GETHOLIDAYAJAX_SERVLET = "GetHolidayAjaxServlet";
    private static final String GETEVENTAJAX_SERVLET = "GetEventAjaxServlet";
    private static final String GETEVENTATTENDEESAJAX_SERVLET = "GetEventAttendeesAjaxServlet";
    private static final String DELETEEVENTAJAX_SERVLET = "DeleteEventAjaxServlet";
    private static final String ADDEVENTCUSTOMERCAREAJAX_SERVLET = "AddEventCustomerCareAjaxServlet";
    private static final String ADDEVENTCARRESERVATIONAJAX_SERVLET = "AddEventCarReservationAjaxServlet";
    private static final String ADDEVENTBIZTRIPAJAX_SERVLET = "AddEventBizTripAjaxServlet";
    private static final String ADDEVENTTRAININGAJAX_SERVLET = "AddEventTrainingAjaxServlet";
    private static final String ADDEVENTANNUALLEAVEAJAX_SERVLET = "AddEventAnnualLeaveAjaxServlet";
    private static final String UPDATEEVENTCUSTOMERCAREAJAX_SERVLET = "UpdateEventCustomerCareAjaxServlet";
    private static final String UPDATEEVENTBIZTRIPEAJAX_SERVLET = "UpdateEventBizTripAjaxServlet";
    private static final String UPDATEEVENTTRAININGAJAX_SERVLET = "UpdateEventTrainingAjaxServlet";
    private static final String UPDATEEVENTCARRESERVATIONAJAX_SERVLET = "UpdateEventCarReservationAjaxServlet";
    private static final String UPDATEEVENTANNUALLEAVEAJAX_SERVLET = "UpdateEventAnnualLeaveAjaxServlet";
    private static final String SENDANNUALLEAVEAJAX_SERVLET = "SendAnnualLeaveAjaxServlet";
    private static final String SENDEVENTATTENDEESAJAX_SERVLET = "SendEventAttendeesAjaxServlet";
    private static final String SETEVENTATTENDEESAJAX_SERVLET = "SetEventAttendeesAjaxServlet";
    private static final String GETCARRESERVATIONAJAX_SERVLET = "GetCarReservationAjaxServlet";
    private static final String GETEMPLOYEEANNUALLEAVEAJAX_SERVLET = "GetEmployeeAnnualLeaveAjaxServlet";
    private static final String GETANNUALLEAVELIST_SERVLET = "GetAnnualLeaveListServlet";
    private static final String CHANGESTATUSANNUALLEAVEAJAX_SERVLET = "ChangeStatusAnnualLeaveAjaxServlet";
    private static Map<String, List<String>> authorizedList;

    public static void setAuthorizedList() {
        if (authorizedList == null) {
            authorizedList = new HashMap<>();

            // Guest list page and servlets allowed
            List<String> guest = new ArrayList<>();
            guest.add(LOGIN_PAGE);
            guest.add(ERROR_PAGE);
            guest.add(RECOVERY_PAGE);

            guest.add(LOGINCOOKIE_SERVLET);
            guest.add(LOGIN_SERVLET);
            guest.add(SENDRECOVERYMAIL_SERVLET);
            guest.add(GETRECOVERYLINK_SERVLET);
            guest.add(RECOVERACCOUNTPASSWORDAJAX_SERVLET);
            authorizedList.put("GUEST", guest);

            // Normal user list page and servlets allowed
            List<String> user = new ArrayList<>();
            user.add(USER_PAGE);
            user.add(CUSTOMERCARE_PAGE);
            user.add(CARRESERVATION_PAGE);
            user.add(BIZTRIP_PAGE);
            user.add(TRAINING_PAGE);
            user.add(ANNUALLEAVE_PAGE);
            user.add(HOME_PAGE);

            user.add(LOGOUT_SERVLET);
            user.add(CHANGEUSERPROFILEPASSWORDAJAX_SERLVET);
            user.add(UPDATEUSERPROFILEAJAX_SERVLET);
            user.add(GETCALENDAR_SERVLET);
            user.add(GETEVENTAJAX_SERVLET);
            user.add(GETEVENTATTENDEESAJAX_SERVLET);
            user.add(GETHOLIDAYAJAX_SERVLET);
            user.add(DELETEEVENTAJAX_SERVLET);
            user.add(ADDEVENTCUSTOMERCAREAJAX_SERVLET);
            user.add(ADDEVENTBIZTRIPAJAX_SERVLET);
            user.add(ADDEVENTTRAININGAJAX_SERVLET);
            user.add(ADDEVENTCARRESERVATIONAJAX_SERVLET);
            user.add(ADDEVENTANNUALLEAVEAJAX_SERVLET);
            user.add(UPDATEEVENTCUSTOMERCAREAJAX_SERVLET);
            user.add(UPDATEEVENTBIZTRIPEAJAX_SERVLET);
            user.add(UPDATEEVENTTRAININGAJAX_SERVLET);
            user.add(UPDATEEVENTCARRESERVATIONAJAX_SERVLET);
            user.add(UPDATEEVENTANNUALLEAVEAJAX_SERVLET);
            user.add(SENDANNUALLEAVEAJAX_SERVLET);
            user.add(SENDEVENTATTENDEESAJAX_SERVLET);
            user.add(SETEVENTATTENDEESAJAX_SERVLET);
            user.add(GETCARRESERVATIONAJAX_SERVLET);
            user.add(GETEMPLOYEEANNUALLEAVEAJAX_SERVLET);
            authorizedList.put("USER", user);

            // SuperUser list page and servlets allowed
            List<String> superUser = new ArrayList<>(user);
            superUser.add(GETCUSTOMERLIST_SERVLET);
            superUser.add(ADDCUTOMERAJAX_SERVLET);
            superUser.add(UPDATECUTOMERAJAX_SERVLET);
            superUser.add(DELETECUTOMERAJAX_SERVLET);
            superUser.add(GETANNUALLEAVELIST_SERVLET);
            superUser.add(CHANGESTATUSANNUALLEAVEAJAX_SERVLET);
            superUser.add(CHANGEANNUALLEAVELIMITTAJAX_SERVLET);
            authorizedList.put("SUPERUSER", superUser);

            // System admin list page and servlets allowed
            List<String> admin = new ArrayList<>(superUser);
            admin.add(CARLIST_PAGE);
            admin.add(CUSTOMERLIST_PAGE);
            admin.add(DEPARTMENTLIST_PAGE);
            admin.add(EMPLOYEELIST_PAGE);
            admin.add(HOLIDAYLIST_PAGE);
            admin.add(TITLELIST_PAGE);

            admin.add(GETCARLIST_SERVLET);
            admin.add(ADDCARAJAX_SERVLET);
            admin.add(UPDATECARAJAX_SERVLET);
            admin.add(DELETECARAJAX_SERVLET);
            admin.add(GETCUSTOMERLIST_SERVLET);
            admin.add(ADDCUTOMERAJAX_SERVLET);
            admin.add(UPDATECUTOMERAJAX_SERVLET);
            admin.add(DELETECUTOMERAJAX_SERVLET);
            admin.add(GETDEPARTMENTLIST_SERVLET);
            admin.add(ADDDEPARTMENTAJAX_SERVLET);
            admin.add(UPDATEDEPARTMENTAJAX_SERVLET);
            admin.add(DELETEDEPARTMENTAJAX_SERVLET);
            admin.add(GETEMPLOYEELIST_SERVLET);
            admin.add(GETSUPERVISORLISTAJAX_SERVLET);
            admin.add(ADDEMPLOYEEAJAX_SERVLET);
            admin.add(UPDATEEMPLOYEEAJAX_SERVLET);
            admin.add(CHANGEEMPLOYEEPASSWORDAJAX_SERLVET);
            admin.add(CHANGEACTIVEACCOUNTAJAX_SERVLET);
            admin.add(GETHOLIDAYLIST_SERVLET);
            admin.add(ADDHOLIDAYAJAX_SERVLET);
            admin.add(UPDATEHOLIDAYAJAX_SERVLET);
            admin.add(DELETEHOLIDAYAJAX_SERVLET);
            admin.add(GETTITLELIST_SERVLET);
            admin.add(ADDTITLEAJAX_SERVLET);
            admin.add(UPDATETITLEAJAX_SERVLET);
            admin.add(DELETETITLEAJAX_SERVLET);
            authorizedList.put("ADMIN", admin);
        }
    }

    public static String defaultPage() {
        return LOGIN_PAGE;
    }

    public static String authorizedLoginCookie() {
        return LOGINCOOKIE_SERVLET;
    }

    public static String authorizedURL(String url, EmployeeDTO dto) {

        if (dto == null || !dto.getAccount().isActive()) {
            return authorizedGuest(url);
        }

        if (authorizedList.get("GUEST").contains(url)) {
            return USER_PAGE;
        }

        if (!dto.getAccount().isAdmin()
                && dto.getTitle().getPermission().getPermissionID() == PermissionEnum.NORMAL_USER.getPermissionID()
                && authorizedList.get("USER").contains(url)) {
            return url;
        }

        if (!dto.getAccount().isAdmin()
                && dto.getTitle().getPermission().getPermissionID() == PermissionEnum.SUPER_USER.getPermissionID()
                && authorizedList.get("SUPERUSER").contains(url)) {
            return url;
        }

        if (dto.getAccount().isAdmin() && authorizedList.get("ADMIN").contains(url)) {
            return url;
        }

        return NOTFOUND_PAGE;
    }

    private static String authorizedGuest(String url) {
        return authorizedList.get("GUEST").contains(url) ? url : LOGIN_PAGE;
    }

}
