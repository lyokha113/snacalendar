<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

﻿<!DOCTYPE html>
<html>

    <head>
        <meta charset="UTF-8">
        <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
        <!-- Favicon-->
        <link rel="icon" href="images/favicon.ico" type="image/x-icon">
        <title>SNA Calendar</title>



        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css?family=Roboto:400,700&subset=latin,cyrillic-ext" rel="stylesheet" type="text/css">
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet" type="text/css">

        <!-- Bootstrap Core Css -->
        <link href="vendors/bootstrap/dist/css/bootstrap.min.css" rel="stylesheet" />

        <!-- Validetta Css-->
        <link href="vendors/validetta/validetta.min.css" rel="stylesheet"/>

        <!-- Custom Css -->
        <link href="css/login.css" rel="stylesheet">
    </head>

    <body class="login-page">

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

        <div class="login-box">
            <div class="logo">
                <img src="images/login-logo.png" alt="logo" />
            </div>
            <div class="card">
                <div class="header">
                    <h2>WORKING CALENDAR</h2>
                </div>
                <div class="body ">
                    <form action="login" id="login" method="POST">
                        <div class="input-group">
                            <span class="input-group-addon">
                                <i class="material-icons">perm_identity</i>
                            </span>
                            <div class="form-line">
                                <input type="email" class="form-control" id="email" name="email" value="${param.email}" placeholder="Email" data-validetta="required,email" data-vd-message-required="Can't be empty." data-vd-message-email="Must be correct email formation.">
                            </div>

                        </div>
                        <div class="input-group">
                            <span class="input-group-addon">
                                <i class="material-icons">lock</i>
                            </span>
                            <div class="form-line">
                                <input type="password" class="form-control" name="password" value="" placeholder="Password" data-validetta="required" data-vd-message-required="Can't be empty.">
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-6" style="padding-top: 5px !important">
                                <input type="checkbox" name="rememberme" id="rememberme" class="filled-in">
                                <label for="rememberme">Remember Me</label>
                            </div>
                            <div class="col-xs-6">
                                <button class="btn btn-block" type="submit">LOGIN</button>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-12" style="margin-bottom: 5px !important">
                                <a href="recovery.jsp">Forgot your password ?</a>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <div class="legit">
                &copy; 2018 SNA GLOBAL
            </div>
        </div>
        <!-- Jquery Core Js -->
        <script src="vendors/jquery/dist/jquery.min.js"></script>

        <!-- Bootstrap Core Js -->
        <script src="vendors/bootstrap/dist/js/bootstrap.min.js"></script>

        <!-- SweetAlert 2 Plugin Js -->
        <script src="vendors/sweetalert/sweetalert2.js"></script>

        <!-- Validetta Js-->
        <script src="vendors/validetta/validetta.min.js"></script>

        <!-- Custom Js -->
        <script src="js/dialogs.js"></script>
        <script src="js/login-recovery.js"></script>

    </body>

    <script>
        function loadStatus() {
        <c:if test="${not empty requestScope.LOGINSTATUS}" >
            var status = "${requestScope.LOGINSTATUS}";
            var msg = "${requestScope.LOGINMESS}";
            var icon = "error";
            showDialogMessage(status, msg, icon);
        </c:if>
        }
    </script>

</html>