<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<div class="page-title">
    <div class="title_left">
        <h3>DEPARTMENT MANAGEMENT</h3>
    </div>
</div>

<div class="col-md-5 col-sm-6 col-xs-12">
    <div class="add-new-btn">
        <span><i class="fa fa-plus-circle"></i>  NEW DEPARTMENT</span>
    </div>
</div>

<!-- List -->
<div class="col-md-12 col-sm-12 col-xs-12">
    <div class="x_panel">
        <div class="x_title">
            <h2>Department List <a id="helpIcon" data-toggle="modal" data-target="#helpModal"><i class="fa fa-question-circle"></i></a></h2>
            <div class="clearfix"></div>
        </div>
        <div class="x_content">
            <table id="datatable" class="table row-bordered">
                <thead style="background-color: whitesmoke; color: gray;">
                    <tr>
                        <th>ID</th>
                        <th>DEPARTMENT NAME</th>
                        <th>AMOUNT</th>
                        <th>CREATED DATE</th>
                        <th style="text-align: center;">UPDATE</th>
                        <th style="text-align: center;">DELETE</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${requestScope.DEPARTMENTLIST}" var="entry">
                        <tr>
                            <td>${entry.key.departmentID}</td>
                            <td>${entry.key.departmentName}</td>

                            <td class="amounttd">${entry.value}</td>

                            <td><div class="hiddeninfo">${entry.key.createdDate}</div><div class="datetd">${entry.key.createdDate}</div></td>

                            <td class="dttb-button">     
                                <button type="button" class="btn btn-default btn-circle dttb-btn-edit">
                                    <i class="fa fa-pencil-alt"></i>
                                </button>
                            </td>

                            <td class="dttb-button">     
                                <button type="button" class="btn btn-default btn-circle dttb-btn-delete">
                                    <i class="fa fa-trash-alt"></i>
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
                <h4 class="modal-title" id="detailsModalTitle"></h4>
            </div>
            <div class="modal-body">
                <form id="detailsModalForm" class="form-horizontal form-label-left customdetailmodalform" action="#">

                    <div class="form-group" id="detailsID">
                        <div class="col-md-12 col-sm-12 col-xs-12 group-line">
                            <label class="control-label col-md-2 col-sm-2 col-xs-12" for="depID">Department ID
                            </label>
                            <div class="col-md-10 col-sm-10 col-xs-6">
                                <h2 id="depID"></h2>
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-12 col-sm-12 col-xs-12 group-line">
                            <label class="control-label col-md-2 col-sm-2 col-xs-12" for="depName">Department Name
                            </label>
                            <div class="col-md-10 col-sm-10 col-xs-12 input-form">
                                <input type="text" id="depName" name="depName" class="form-control col-md-7 col-xs-12" data-validetta="required,maxLength[250]" data-vd-message-required="* Can't be empty.">
                            </div>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-12 col-sm-12 col-xs-12 btn-action">
                            <button type="button" class="btn btn-close btn-default" data-dismiss="modal">Close</button>
                            <button type="submit" class="btn" id="detailsModalAction"
                                    data-loading-text="<i class='fa fa-spinner fa-spin'></i>  Processing">Submit</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<!-- #END# Details Modal -->

<script src="js/departmentlist.js"></script>
