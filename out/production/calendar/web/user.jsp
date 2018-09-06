<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <!-- Meta, title, CSS, favicons, etc. -->
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link rel="icon" href="images/favicon.ico" type="image/x-icon">
        <title>SNA Calendar</title>

        <!-- Bootstrap -->
        <link href="vendors/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet">

        <!-- Materialize -->
        <link href="css/materialize.css" rel="stylesheet">

        <!-- Font Awesome -->
        <link href="vendors/font-awesome/css/fontawesome-all.min.css" rel="stylesheet">

        <!-- Google prettify -->
        <link href="vendors/google-code-prettify/bin/prettify.min.css" rel="stylesheet">

        <!-- jQuery custom content scroller -->
        <link href="vendors/malihu-custom-scrollbar-plugin/jquery.mCustomScrollbar.min.css" rel="stylesheet"/>

        <!-- Data range picker-->
        <link href="vendors/bootstrap-daterangepicker/daterangepicker.css" rel="stylesheet"/>

        <!-- Bootstrap selector-->
        <link href="vendors/bootstrap-select/css/bootstrap-select.min.css" rel="stylesheet"/>

        <!-- Validetta Css-->
        <link href="vendors/validetta/validetta.min.css" rel="stylesheet"/>

        <!-- Calendar Bootstrap -->
        <link href="vendors/bootstrap-calendar/css/calendar.css" rel="stylesheet"/>

        <!-- PNotify -->
        <link href="vendors/pnotify/pnotify.css" rel="stylesheet">
        <link href="vendors/pnotify/pnotify.buttons.css" rel="stylesheet">
        <link href="vendors/pnotify/pnotify.nonblock.css" rel="stylesheet">

        <!-- Summernote-->
        <link href="vendors/summernote/summernote.css" rel="stylesheet">

        <!-- Datatables -->
        <link href="vendors/DataTables/DataTables-1.10.18/css/dataTables.bootstrap.min.css" rel="stylesheet">
        <link href="vendors/DataTables/Buttons-1.5.2/css/buttons.bootstrap.min.css" rel="stylesheet">

        <!-- Custom styling plus plugins -->
        <link href="css/custom.css" rel="stylesheet">
    </head>

    <c:set var="emp" value="${sessionScope.EMPLOYEEDTO}" />

    <body class="nav-md">

        <!-- Page Loader -->
        <div class="page-loader-wrapper">
            <div class="loader">
                <div class="preloader">
                    <div class="spinner-layer pl-red">
                        <div class="circle-clipper left">
                            <div class="circle"></div>
                        </div>
                        <div class="circle-clipper right">
                            <div class="circle"></div>
                        </div>
                    </div>
                </div>
                <p>Processing.....</p>
            </div>
        </div>
        <!-- #END# Page Loader -->

        <div class="container body full-height">
            <div class="main_container">
                <div class="col-md-3 left_col menu_fixed">
                    <div class="left_col scroll-view">
                        <div class="navbar nav_title" style="border: 0;">
                            <a href="#" class="site_title"><img src="images/logo.png" alt="..."><span>CALENDAR</span></a>
                        </div>

                        <div class="clearfix"></div>

                        <!-- menu profile quick info -->
                        <div class="profile clearfix">
                            <div class="profile_pic">
                                <img src="images/user-avatar.png" alt="..." class="img-circle profile_img">
                            </div>
                            <div class="profile_info">
                                <div class="hiddeninfo" id="profile_info_id">${emp.account.accountID}</div>
                                <div class="hiddeninfo" id="profile_info_admin">${emp.account.admin}</div>
                                <div class="hiddeninfo" id="profile_info_permission">${emp.title.permission.permissionID}</div>
                                <span id="profile_info_name">${emp.fullname}</span><br>
                                <h2 id="profile_info_dep">${emp.department.departmentName}</h2>
                                <h2 id="profile_info_title">${emp.title.titleName}</h2>
                                <h2 id="profile_info_type">${emp.type.typeName}</h2>
                            </div>
                        </div>
                        <!-- /menu profile quick info -->

                        <br />

                        <!-- sidebar menu -->
                        <div id="sidebar-menu" class="main_menu_side hidden-print main_menu">
                            <div class="menu_section">
                                <h3>HOME PAGE</h3>
                                <ul class="nav side-menu">
                                    <li><a data-page="homepage.jsp"><i class="fas fa-home fa-fw"></i>&nbsp; Home</a></li>
                                </ul>
                            </div>
                            <div class="menu_section" id="usersidemenu">
                                <h3>USER MENU</h3>
                                <ul class="nav side-menu">
                                    <li><a data-page="GetCalendar?type=1"><i class="fas fa-clipboard-list fa-fw"></i>&nbsp; Customer care</a></li>
                                    <li><a data-page="GetCalendar?type=2"><i class="fas fa-chalkboard-teacher fa-fw"></i>&nbsp; Training</a></li>
                                    <li><a data-page="GetCalendar?type=3"><i class="fas fa-plane fa-fw"></i>&nbsp; Business Trip</a></li>
                                    <li><a data-page="GetCalendar?type=4"><i class="fas fa-car fa-fw"></i>&nbsp; Car Reservation</a></li>
                                    <li><a data-page="GetCalendar?type=5"><i class="far fa-calendar-check fa-fw"></i>&nbsp; Annual Leave</a></li>
                                </ul>
                            </div>
                            <div class="menu_section" id="superusersidemenu">
                                <h3>SUPER USER MENU</h3>
                                <ul class="nav side-menu">
                                    <li><a data-page="GetCustomerList"><i class="fas fa-user-tie fa-fw"></i>&nbsp; Customer Management</a></li>
                                    <li><a data-page="GetAnnualLeaveList"><i class="fas fa-user-check fa-fw"></i>&nbsp; Annual Leave Management</a></li>
                                </ul>
                            </div>
                            <div class="menu_section" id="adminsidemenu">
                                <h3>ADMINISTRATOR MENU</h3>
                                <ul class="nav side-menu">
                                    <li><a data-page="GetEmployeeList"><i class="far fa-id-card fa-fw"></i>&nbsp; Employee Management</a></li>
                                    <li><a data-page="GetDepartmentList"><i class="fas fa-university fa-fw"></i>&nbsp; Department Management</a></li>                                   
                                    <li><a data-page="GetCustomerList"><i class="fas fa-user-tie fa-fw"></i>&nbsp; Customer Management</a></li>
                                    <li><a data-page="GetTitleList"><i class="fab fa-font-awesome-flag fa-fw"></i>&nbsp; Title Management</a></li>
                                    <li><a data-page="GetCarList"><i class="fas fa-truck fa-fw"></i>&nbsp; Car Management</a></li>
                                    <li><a data-page="GetHolidayList"><i class="fas fa-birthday-cake fa-fw"></i>&nbsp; Holiday Management</a></li>
                                </ul>
                            </div>                          
                        </div>
                        <!-- /sidebar menu -->

                        <!-- /menu footer buttons -->
                        <div class="sidebar-footer hidden-small">
                            <h2>&copy; 2018 SNA Global</h2>
                        </div>
                        <!-- /menu footer buttons -->
                    </div>
                </div>

                <!-- top navigation -->
                <div class="top_nav">
                    <div class="nav_menu">
                        <nav>
                            <div class="nav toggle">
                                <a id="menu_toggle"><i class="fa fa-bars"></i></a>
                            </div>

                            <ul class="nav navbar-nav navbar-right">
                                <li class="">
                                    <a href="javascript:;" class="user-profile dropdown-toggle" data-toggle="dropdown" aria-expanded="false" id="profile_info_email">
                                        <img src="images/user-avatar.png" alt="">${emp.account.email}
                                        <span class=" fa fa-angle-down"></span>
                                    </a>
                                    <ul class="dropdown-menu dropdown-usermenu pull-right">
                                        <li><a class="top_nav_menu_items" href="#" data-page="userprofile"><i class="fa fa-user-edit pull-right"></i> User Profile</a></li>
                                        <li><a class="top_nav_menu_items" href="#" data-page="changepassword"><i class="fa fa-sync pull-right"></i> Change Password</a></li>
                                        <li><a class="top_nav_menu_items" href="#" data-page="logout"><i class="fa fa-sign-out-alt pull-right"></i> Log Out</a></li>
                                    </ul>
                                </li>

                                <%--<li role="presentation" class="dropdown">--%>
                                    <%--<a href="#" class="dropdown-toggle info-number" data-toggle="dropdown" aria-expanded="false">--%>
                                        <%--<i class="far fa-bell"></i>--%>
                                        <%--<span class="badge bg-green">6</span>--%>
                                    <%--</a>--%>
                                <%--</li>--%>
                            </ul>
                        </nav>
                    </div>
                </div>
                <!-- /top navigation -->

                <!-- page content -->
                <div class="right_col" role="main">
                    <div class="" id="main-content">
                    </div>
                </div>
                <!-- /page content -->
            </div>
        </div>

        <!-- User Profile Modal -->
        <div class="modal fade customdetailmodal" id="profileModal" data-backdrop="static" data-keyboard="false"
             tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">×</span>
                        </button>
                        <h4 class="modal-title" id="profileModalTitle"></h4>
                    </div>
                    <div class="modal-body">
                        <form id="profileModalForm" class="form-horizontal form-label-left customdetailmodalform" action="#">

                            <div class="form-group infodetail">
                                <div class="col-md-6 col-sm-6 col-xs-12 group-line">
                                    <label class="control-label col-md-3 col-sm-4 col-xs-6" for="profileID">ID
                                    </label>
                                    <div class="col-md-3 col-sm-4 col-xs-6">
                                        <h2 id="profileID">${emp.account.accountID}</h2>
                                    </div>
                                </div>
                            </div>

                            <div class="form-group infodetail">
                                <div class="col-md-6 col-sm-6 col-xs-12 group-left">
                                    <label class="control-label col-md-3 col-sm-4 col-xs-12" for="profileEmail">Email*
                                    </label>
                                    <div class="col-md-9 col-sm-8 col-xs-12 input-form">
                                        <h2 id="profileEmail" class="profile_fixed">${emp.account.email}</h2>
                                    </div>
                                </div>
                                <div class="col-md-6 col-sm-6 col-xs-12 group-right">
                                    <label class="control-label col-md-3 col-sm-4 col-xs-12" for="profileName">Name
                                    </label>
                                    <div class="col-md-9 col-sm-8 col-xs-12 input-form">
                                        <input type="text" id="profileName" name="profileName" value="${emp.fullname}" class="form-control col-md-7 col-xs-12" data-validetta="required,maxLength[250]" data-vd-message-required="* Can't be empty.">
                                    </div>
                                </div>
                            </div>

                            <div class="form-group infodetail">
                                <div class="col-md-6 col-sm-6 col-xs-12 group-left">
                                    <label class="control-label col-md-3 col-sm-4 col-xs-12" for="profileDoB">D.O.B*
                                    </label>
                                    <div class="col-md-9 col-sm-8 col-xs-12 input-form">
                                        <h2 id="profileDoB" class="profile_fixed">${emp.dob}</h2>
                                    </div>
                                </div>
                                <div class="col-md-6 col-sm-6 col-xs-12 group-right">
                                    <label class="control-label col-md-3 col-sm-4 col-xs-12" for="profileAddress">Address
                                    </label>
                                    <div class="col-md-9 col-sm-8 col-xs-12 input-form">
                                        <input type="text" id="profileAddress" name="profileAddress" value="${emp.address}" class="form-control col-md-7 col-xs-12" data-validetta="required,maxLength[250]" data-vd-message-required="* Can't be empty.">
                                    </div>
                                </div>
                            </div>

                            <div class="form-group infodetail">
                                <div class="col-md-6 col-sm-6 col-xs-12 group-left">
                                    <label class="control-label col-md-3 col-sm-4 col-xs-12" for="profileJD">Joined.D*
                                    </label>
                                    <div class="col-md-9 col-sm-8 col-xs-12 input-form">
                                        <h2 id="profileJD" class="profile_fixed">${emp.account.joinedDate}</h2>
                                    </div>
                                </div>
                                <div class="col-md-6 col-sm-6 col-xs-12 group-right">
                                    <label class="control-label col-md-3 col-sm-4 col-xs-12" for="profilePhone">Phone
                                    </label>
                                    <div class="col-md-9 col-sm-8 col-xs-12 input-form">
                                        <input type="text" id="profilePhone" name="profilePhone" value="${emp.phone}" class="form-control col-md-7 col-xs-12" data-validetta="required,regExp[profilePhone]" data-vd-message-required="* Can't be empty.">
                                    </div>
                                </div>
                            </div>


                            <div class="form-group passworddetail">
                                <div class="col-md-12 col-sm-12 col-xs-12 group-line">
                                    <label class="control-label col-md-2 col-sm-2 col-xs-12" for="profileCurrent">Current Password
                                    </label>
                                    <div class="col-md-10 col-sm-10 col-xs-12 input-form">
                                        <input type="password" id="profileCurrent" name="profileCurrent" class="form-control col-md-7 col-xs-12" data-validetta="required,minLength[6],maxLength[250]" data-vd-message-required="* Can't be empty." data-vd-message-minLength="* Must be atleast 6 characters.">
                                    </div>
                                </div>
                            </div>

                            <div class="form-group passworddetail">
                                <div class="col-md-12 col-sm-12 col-xs-12 group-line">
                                    <label class="control-label col-md-2 col-sm-2 col-xs-12" for="profilePass">New Password
                                    </label>
                                    <div class="col-md-10 col-sm-10 col-xs-12 input-form">
                                        <input type="password" id="profilePass" name="profilePass" class="form-control col-md-7 col-xs-12" data-validetta="required,minLength[6],maxLength[250]" data-vd-message-required="* Can't be empty." data-vd-message-minLength="* Must be atleast 6 characters.">
                                    </div>
                                </div>
                            </div>

                            <div class="form-group passworddetail">
                                <div class="col-md-12 col-sm-12 col-xs-12 group-line">
                                    <label class="control-label col-md-2 col-sm-2 col-xs-12" for="profileConfirm">Confirm Password
                                    </label>
                                    <div class="col-md-10 col-sm-10 col-xs-12 input-form">
                                        <input type="password" id="profileConfirm" name="profileConfirm" class="form-control col-md-7 col-xs-12" data-validetta="required,equalTo[profilePass]" data-vd-message-required="* Can't be empty." data-vd-message-equalTo="* Password must be matched.">
                                    </div>
                                </div>
                            </div>

                            <div class="form-group">
                                <div class="col-md-12 col-sm-12 col-xs-12 btn-action">
                                    <button type="button" class="btn btn-close btn-default" data-dismiss="modal">Close</button>
                                    <button type="submit" class="btn" id="profileModalAction"
                                            data-loading-text="<i class='fa fa-spinner fa-spin'></i>  Processing">Submit</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <!-- #END# User Profile Modal -->                       

        <!-- Help modal -->
        <div class="modal fade" id="helpModal" tabindex="-1" data-backdrop="static" data-keyboard="false"
             role="dialog" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">×</span>
                        </button>
                        <h4 class="modal-title">HELP</h4>
                    </div>
                    <div class="modal-body"> 
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    </div>
                </div>
            </div>
        </div>
        <!-- #END# Help modal -->

        <!-- jQuery -->
        <script src="vendors/jquery/dist/jquery.min.js"></script>

        <!-- Bootstrap -->
        <script src="vendors/bootstrap/dist/js/bootstrap.min.js"></script>

        <!-- Google prettify -->
        <script src="vendors/google-code-prettify/src/prettify.js"></script>

        <!-- jQuery custom content scroller -->
        <script src="vendors/malihu-custom-scrollbar-plugin/jquery.mCustomScrollbar.concat.min.js"></script>

        <!-- Bootstrap selector-->
        <script src="vendors/bootstrap-select/js/bootstrap-select.min.js"></script>

        <!-- Sweet alert 2-->
        <script src="vendors/sweetalert/sweetalert2.js"></script>

        <!-- JSZip-->
        <script src="vendors/DataTables/JSZip-2.5.0/jszip.min.js"></script>

        <!-- PDFMake-->
        <script src="vendors/DataTables/pdfmake/pdfmake.min.js"></script>
        <script src="vendors/DataTables/pdfmake/vfs_fonts.js"></script>

        <!-- Datatables -->
        <script src="vendors/DataTables/DataTables-1.10.18/js/jquery.dataTables.min.js"></script>
        <script src="vendors/DataTables/DataTables-1.10.18/js/dataTables.bootstrap.min.js"></script>
        <script src="vendors/DataTables/Buttons-1.5.2/js/dataTables.buttons.min.js"></script>
        <script src="vendors/DataTables/Buttons-1.5.2/js/buttons.jqueryui.min.js"></script>
        <script src="vendors/DataTables/Buttons-1.5.2/js/buttons.html5.min.js"></script>
        <script src="vendors/DataTables/Buttons-1.5.2/js/buttons.bootstrap.min.js"></script>
        <script src="vendors/DataTables/Buttons-1.5.2/js/buttons.print.min.js"></script>

        <!-- Moment-->
        <script src="vendors/moment/min/moment.min.js"></script>

        <!-- Data range picker-->
        <script src="vendors/bootstrap-daterangepicker/daterangepicker.js"></script>

        <!-- Validetta Js-->
        <script src="vendors/validetta/validetta.min.js"></script>

        <!-- PNotify -->
        <script src="vendors/pnotify/pnotify.js"></script>
        <script src="vendors/pnotify/pnotify.buttons.js"></script>
        <script src="vendors/pnotify/pnotify.nonblock.js"></script>

        <!-- Underscore JS -->
        <script src="vendors/underscore/underscore-min.js"></script>

        <!-- Calendar Bootstrap -->
        <script src="vendors/bootstrap-calendar/js/calendar.js"></script>

        <!-- Summernote-->
        <script src="vendors/summernote/summernote.js"></script>

        <!-- Custom Theme Scripts -->
        <script src="js/custom.js"></script>
        <script src="js/dialogs.js"></script>
        <script src="js/notifications.js"></script>
    </body>
</html>