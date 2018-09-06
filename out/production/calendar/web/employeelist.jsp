<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>

<div class="page-title">
    <div class="title_left">
        <h3>EMPLOYEE MANAGEMENT</h3>
    </div>
</div>

<div class="col-md-5 col-sm-6 col-xs-12">
    <div class="add-new-btn">
        <span><i class="fa fa-plus-circle"></i>  ADD EMPLOYEE</span>
    </div>
</div>

<!-- List -->
<div class="col-md-12 col-sm-12 col-xs-12">
    <div class="x_panel">
        <div class="x_title">
            <h2>Employee List <a id="helpIcon" data-toggle="modal" data-target="#helpModal"><i
                    class="fa fa-question-circle"></i></a></h2>
            <div class="clearfix"></div>
        </div>
        <div class="x_content">
            <table id="datatable" class="table row-bordered">
                <thead style="background-color: whitesmoke; color: gray;">
                <tr>
                    <th>ID</th>
                    <th>NAME</th>
                    <th>SUPERVISOR</th>
                    <th>DEPARTMENT</th>
                    <th>JOINED DATE</th>
                    <th style="text-align: center;">UPDATE</th>
                    <th style="text-align: center;">ACTIVE</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${requestScope.EMPLOYEELIST}" var="dto">
                    <div class="hiddeninfo" id="hidden-${dto.account.accountID}">
                        <span>${dto.department.departmentID}</span>
                        <span>${dto.title.titleID}</span>
                        <span>${dto.supervisor.account.accountID}</span>
                        <span>${dto.type.typeID}</span>
                        <span>${dto.address}</span>
                        <span>${dto.phone}</span>
                        <span>${dto.sex}</span>
                        <span>${dto.dob}</span>
                        <span>${dto.account.email}</span>
                        <span>${dto.account.admin}</span>
                    </div>
                    <tr>
                        <td>${dto.account.accountID}</td>
                        <td>
                            <div class="nametd">${dto.fullname}</div>
                            <div class="hiddeninfo">${dto.title.permission.permissionID}</div>
                            <div class="titletypetd">${dto.title.titleName}/${dto.type.typeName}</div>
                        </td>
                        <td class="supervisortd">${dto.supervisor.fullname}</td>
                        <td>${dto.department.departmentName}</td>

                        <td>
                            <div class="hiddeninfo">${dto.account.joinedDate}</div>
                            <div class="datetd">${dto.account.joinedDate}</div>
                        </td>

                        <td class="dttb-button">
                            <button type="button" class="btn btn-default btn-circle dttb-btn-edit">
                                <i class="fa fa-pencil-alt"></i>
                            </button>
                        </td>

                        <td class="dttb-button">
                            <button type="button"
                                    class="btn btn-default btn-circle dttb-btn-lock-${dto.account.active}">
                                <i></i>
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

<!-- Details Modal -->
<div class="modal fade customdetailmodal" id="detailsModal" data-backdrop="static" data-keyboard="false"
     tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">×</span>
                </button>
                <h4 class="modal-title" id="detailsModalTitle"></h4>
            </div>
            <div class="modal-body">
                <form id="detailsModalForm" class="form-horizontal form-label-left customdetailmodalform" action="#">

                    <div class="form-group" id="detailsID">
                        <div class="col-md-6 col-sm-6 col-xs-12 group-line">
                            <label class="control-label col-md-3 col-sm-4 col-xs-6" for="empID">Employee ID
                            </label>
                            <div class="col-md-3 col-sm-4 col-xs-6">
                                <h2 id="empID"></h2>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-6 col-sm-6 col-xs-12 group-left">
                            <label class="control-label col-md-3 col-sm-4 col-xs-12" for="empEmail">Email
                            </label>
                            <div class="col-md-9 col-sm-8 col-xs-12 input-form">
                                <input type="text" id="empEmail" name="empEmail" class="form-control col-md-7 col-xs-12"
                                       data-validetta="required,email,maxLength[250]"
                                       data-vd-message-required="* Can't be empty."
                                       data-vd-message-email="* Must be correct email formation.">
                            </div>
                        </div>
                        <div class="col-md-6 col-sm-6 col-xs-12 group-right">
                            <label class="control-label col-md-3 col-sm-4 col-xs-12" for="empName">Name
                            </label>
                            <div class="col-md-9 col-sm-8 col-xs-12 input-form">
                                <input type="text" id="empName" name="empName" class="form-control col-md-7 col-xs-12"
                                       data-validetta="required,maxLength[250]"
                                       data-vd-message-required="* Can't be empty.">
                            </div>
                        </div>
                    </div>

                    <div class="form-group password">
                        <div class="col-md-6 col-sm-6 col-xs-12 group-left">
                            <label class="control-label col-md-3 col-sm-4 col-xs-12" for="empPass">Password
                            </label>
                            <div class="col-md-9 col-sm-8 col-xs-12 input-form">
                                <input type="password" id="empPass" name="empPass"
                                       class="form-control col-md-7 col-xs-12"
                                       data-validetta="required,minLength[6],maxLength[250]"
                                       data-vd-message-required="* Can't be empty."
                                       data-vd-message-minLength="* Must be atleast 6 characters.">
                            </div>
                        </div>
                        <div class="col-md-6 col-sm-6 col-xs-12 group-right">
                            <label class="control-label col-md-3 col-sm-4 col-xs-12" for="empConfirm">Confirm
                            </label>
                            <div class="col-md-9 col-sm-8 col-xs-12 input-form">
                                <input type="password" id="empConfirm" name="empConfirm"
                                       class="form-control col-md-7 col-xs-12"
                                       data-validetta="required,equalTo[empPass]"
                                       data-vd-message-required="* Can't be empty."
                                       data-vd-message-equalTo="* Password must be matched.">
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-6 col-sm-6 col-xs-12 group-left">
                            <label class="control-label col-md-3 col-sm-4 col-xs-12" for="empDep">Department
                            </label>
                            <div class="col-md-9 col-sm-8 col-xs-12 input-form">
                                <select class="selectpicker show-tick show-menu-arrow" title="Choose one of department"
                                        id="empDep" name="empDep" data-validetta="required"
                                        data-vd-message-required="* Must be selected.">
                                    <c:forEach items="${requestScope.DEPLIST}" var="dto">
                                        <option value="${dto.departmentID}">${dto.departmentName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6 col-sm-6 col-xs-12 group-right">
                            <label class="control-label col-md-3 col-sm-4 col-xs-12" for="empAddress">Address
                            </label>
                            <div class="col-md-9 col-sm-8 col-xs-12 input-form">
                                <input type="text" id="empAddress" name="empAddress"
                                       class="form-control col-md-7 col-xs-12" data-validetta="required,maxLength[250]"
                                       data-vd-message-required="* Can't be empty.">
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-6 col-sm-6 col-xs-12 group-left">
                            <label class="control-label col-md-3 col-sm-4 col-xs-12" for="empTitle">Title
                            </label>
                            <div class="col-md-9 col-sm-8 col-xs-12 input-form">
                                <select class="selectpicker show-tick show-menu-arrow" title="Choose one of title"
                                        id="empTitle" name="empTitle" data-validetta="required"
                                        data-vd-message-required="* Must be selected.">
                                    <c:forEach items="${requestScope.TITLELIST}" var="dto">
                                        <option value="${dto.titleID}"
                                                data-permission="${dto.permission.permissionID}">${dto.titleName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6 col-sm-6 col-xs-12 group-right">
                            <label class="control-label col-md-3 col-sm-4 col-xs-12" for="empPhone">Phone
                            </label>
                            <div class="col-md-9 col-sm-8 col-xs-12 input-form">
                                <input type="text" id="empPhone" name="empPhone" class="form-control col-md-7 col-xs-12"
                                       data-validetta="required,regExp[empPhone]"
                                       data-vd-message-required="* Can't be empty.">
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-6 col-sm-6 col-xs-12 group-left">
                            <label class="control-label col-md-3 col-sm-4 col-xs-12" for="empSupervisor">Supervisor
                            </label>
                            <div class="col-md-9 col-sm-8 col-xs-12 input-form">
                                <select class="selectpicker show-tick show-menu-arrow" title="Choose one of supervisor"
                                        id="empSupervisor" name="empSupervisor" data-validetta="required"
                                        data-vd-message-required="* Must be selected.">
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6 col-sm-6 col-xs-12 group-right">
                            <label class="control-label col-md-3 col-sm-4 col-xs-12" for="empDoB">D.O.B
                            </label>
                            <div class="col-md-9 col-sm-8 col-xs-12 input-form" id="parentDatepickerDoB">
                                <input type="text" class="form-control col-md-7 col-xs-12 datepicker" id="empDoB"
                                       name="empDoB" placeholder="Date of birth" readonly data-validetta="required"
                                       data-vd-message-required="* Can't be empty.">
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-6 col-sm-6 col-xs-12 group-left">
                            <label class="control-label col-md-3 col-sm-4 col-xs-12" for="empType">Type
                            </label>
                            <div class="col-md-9 col-sm-8 col-xs-12 input-form">
                                <select class="selectpicker show-tick show-menu-arrow"
                                        title="Choose one of employee type" id="empType" name="empType"
                                        data-validetta="required" data-vd-message-required="* Must be selected.">
                                    <c:forEach items="${requestScope.EMPTYPELIST}" var="dto">
                                        <option value="${dto.typeID}">${dto.typeName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6 col-sm-6 col-xs-12 group-right">
                            <label class="control-label col-md-3 col-sm-4 col-xs-12" for="empJD">Joined.D
                            </label>
                            <div class="col-md-9 col-sm-8 col-xs-12 input-form" id="parentDatepickerJD">
                                <input type="text" class="form-control col-md-7 col-xs-12 datepicker" id="empJD"
                                       name="empJD" placeholder="Joined date" readonly data-validetta="required"
                                       data-vd-message-required="* Can't be empty.">
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-6 col-sm-6 col-xs-12 group-left">
                            <label class="control-label col-md-3 col-sm-4 col-xs-7" for="empAdmin">Role
                            </label>
                            <div class="col-md-9 col-sm-8 col-xs-5 input-form">

                            </div>
                            <div class="col-md-9 col-sm-8 col-xs-12 check-form">
                                <div class="col-md-12 col-sm-12 col-xs-12">
                                    <label class="control-label col-md-8 col-sm-9 col-xs-11" for="empAdmin">Administrator
                                    </label>
                                    <div class="col-md-4 col-sm-3 col-xs-1">
                                        <input type="checkbox" name="empAdmin" id="empAdmin" class="filled-in">
                                        <label for="empAdmin"></label>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6 col-sm-6 col-xs-12 group-right">
                            <label class="control-label col-md-3 col-sm-4 col-xs-12">Gender
                            </label>
                            <div class="col-md-9 col-sm-8 col-xs-12 check-form">
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <label class="control-label col-md-8 col-sm-7 col-xs-11" for="maleChk">Male
                                    </label>
                                    <div class="col-md-4 col-sm-5 col-xs-1">
                                        <input type="checkbox" name="maleChk" id="maleChk" class="filled-in" checked>
                                        <label for="maleChk"></label>
                                    </div>
                                </div>
                                <div class="col-md-6 col-sm-6 col-xs-12">
                                    <label class="control-label col-md-8 col-sm-7 col-xs-11" for="femaleChk">Female
                                    </label>
                                    <div class="col-md-4 col-sm-5 col-xs-1">
                                        <input type="checkbox" name="femaleChk" id="femaleChk" class="filled-in">
                                        <label for="femaleChk"></label>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-12 col-sm-12 col-xs-12 group-line">
                            <div class="col-md-12 col-sm-12 col-xs-12 input-form" id="changepass">
                                <h3>Change password</h3>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-12 col-sm-12 col-xs-12 btn-action">
                            <button type="button" class="btn btn-close btn-default" data-dismiss="modal">Close</button>
                            <button type="submit" class="btn" id="detailsModalAction"
                                    data-loading-text="<i class='fa fa-spinner fa-spin'></i>  Processing">Submit
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<!-- #END# Details Modal -->

<script src="js/employeelist.js"></script>



