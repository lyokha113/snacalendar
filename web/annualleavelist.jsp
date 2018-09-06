<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>

<div class="page-title">
    <div class="title_left">
        <h3>ANNUAL LEAVE MANAGEMENT</h3>
    </div>
</div>

<!-- List -->
<div class="col-md-12 col-sm-12 col-xs-12">
    <div class="x_panel">
        <div class="x_title">
            <h2>TOTAL THIS YEAR</h2>
            <div class="clearfix"></div>
        </div>
        <div class="x_content">
            <table id="datatable" class="table row-bordered">
                <thead style="background-color: whitesmoke; color: gray;">
                <tr>
                    <th>ID</th>
                    <th>NAME</th>
                    <th>TITLE</th>
                    <th>TOTAL</th>
                    <th>LIMIT</th>
                    <th style="text-align: center;">CHANGE LIMIT</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${requestScope.ANNUALLEAVE}" var="entry">
                    <tr>
                        <td>${entry.key.account.accountID}</td>
                        <td>
                            <div class="nametd">${entry.key.fullname}</div>
                            <div class="deptd">${entry.key.department.departmentName}</div>
                        </td>
                        <td>
                            <div class="hiddeninfo">${entry.key.title.permission.permissionID}</div>
                            <div class="titletd">${entry.key.title.titleName}</div>
                        </td>
                        <td class="totaltd">${entry.value}</td>
                        <td class="limittd">${entry.key.annualLeaveDay}</td>
                        <td class="dttb-button">
                            <button type="button" class="btn btn-default btn-circle dttb-btn-edit">
                                <i class="fa fa-pencil-alt"></i>
                            </button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

        </div>
    </div>
</div>
<!-- #END List -->


<!-- List -->
<div class="col-md-12 col-sm-12 col-xs-12">
    <div class="x_panel">
        <div class="x_title">
            <h2>ANNUAL LEAVE REQUEST</h2>
            <div class="clearfix"></div>
        </div>
        <div class="x_content">
            <table id="datatable-request" class="table row-bordered">
                <thead style="background-color: whitesmoke; color: gray;">
                <tr>
                    <th>ID</th>
                    <th>NAME</th>
                    <th>REASON</th>
                    <th>TIME</th>
                    <th style="text-align: center;">APPROVE</th>
                    <th style="text-align: center;">DENY</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${requestScope.REQUESTLIST}" var="dto">
                    <tr>
                        <td>${dto.event.eventID}</td>
                        <td>
                            <div class="hiddeninfo">${dto.event.creator.account.accountID}</div>
                            <div>${dto.event.creator.fullname}</div>
                        </td>
                        <td><div>${dto.event.eventContent}</div></td>
                        <td>${dto.event.eventTitle}</td>
                        <td class="dttb-button">
                            <button type="button" class="btn btn-default btn-circle dttb-btn-approve">
                                <i class="fas fa-check"></i>
                            </button>
                        </td>
                        <td class="dttb-button">
                            <button type="button" class="btn btn-default btn-circle dttb-btn-deny">
                                <i class="fas fa-ban"></i>
                            </button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

        </div>
    </div>
</div>
<!-- #END List -->

<script src="js/annualleavelist.js"></script>
