if (typeof jQuery === "undefined") {
    throw new Error("jQuery plugins need to be before this file");
}

/* Custom js - Function ======================================================================================================
 *  Some custom functions
 *  
 */

let $datatable = $('#datatable');
let supervisorOptions = [];

/* Init datatable */
function initDatatable() {
    $datatable.DataTable({
        language: {
            search: '<i class="glyphicon glyphicon-search"></i>',
            searchPlaceholder: "Filter table ..."
        },
        columnDefs: [
            {orderable: false, targets: [5, 6]}
        ]
    });
}

/* Format datatable */
function formatTableInfo() {

    //Format title type
    $('.titletypetd').each(function () {
        const permission = $(this).siblings('.hiddeninfo').text().trim();
        $(this).css('color', permission == PERMISSION.SUPER_USER ? 'dodgerblue' : '#7393BC');
    });

    //Format supervisor
    $('.supervisortd').each(function () {
        const supervisor = $(this).text().trim();
        if (supervisor == '' || supervisor == 'None' || supervisor == 'No supervisor') {
            $(this).text("None");
            $(this).css('color', '#7393BC');
        } else {
            $(this).css('color', '#3D4756');
        }
    });

    //Format datetime
    $('.datetd').each(function () {
        const date = $(this).siblings('.hiddeninfo').text().trim();
        $(this).text(formatDate(date));
    });

    //Format lock/unlock btn
    $('.dttb-btn-lock-false, .dttb-btn-lock-true').each(function () {
        $(this).children("i").removeClass('fa-lock, fa-lock-open');
        $(this).children("i").addClass($(this).hasClass('dttb-btn-lock-false') ? 'fa fa-lock' : 'fa fa-lock-open');

        //Prevent lock current account
        if ($(this).closest('tr').find('td:eq(0)').text().trim() === $("#profileID").text().trim()) {
            $(this).css('display', 'none');
        }

    });
}

/* Reformat datatable when content changed */
$datatable.on('page.dt, order.dt, search.dt, draw.dt', function () {
    setTimeout(function () {
        formatTableInfo();
    }, 10);
});

/* Date picker */
function datepicker() {
    if (typeof ($.fn.daterangepicker) === 'undefined') {
        return;
    }

    $('#empJD, #empDoB').daterangepicker({
        singleDatePicker: true,
        showDropdowns: true,
        parentEl: $(this).attr('id') == "empJD" ? "#parentDatepickerJD" : "#parentDatepickerDoB",
        opens: "center",
        locale: {"format": "MMMM DD YYYY"},
        minDate: formatDateInput(new Date(1900, 0, 1)),
        maxDate: formatDateInput($.now())
    });
}

/* Trigger gender checkbox */
$('#maleChk, #femaleChk').click(function () {
    const checked = $(this).attr('id') == 'maleChk' ? $(this).is(':checked') : !$(this).is(':checked');
    $('#maleChk').prop("checked", checked);
    $('#femaleChk').prop("checked", !checked);
});

/* Format title selectbox */
function formatTitle() {
    const $titleList = $("#empTitle");
    const $super = $('<optgroup label="Super User"></optgroup>');
    const $normal = $('<optgroup label="Normal User"></optgroup>');
    $titleList.find('option').each(function (index) {
        if (index == 0) return;
        if ($(this).data('permission') == PERMISSION.SUPER_USER) {
            $super.append($(this));
        } else {
            $normal.append($(this));
        }
    });
    $titleList.empty().append($normal).append($super).selectpicker('refresh');
}

/* Format supervisor */
function formatSupervisor(exceptID) {
    const departments = new Map();

    // Make deep copy of supervisors
    const supervisors = [];
    supervisorOptions.forEach(supervisor => {
        supervisors.push(supervisor.clone(true));
    });

    supervisors.forEach(supervisor => {
        if (exceptID != undefined && exceptID == supervisor.val()) return;
        const department = supervisor.data('department');
        const optgroup = departments.has(department)
            ? departments.get(department)
            : $('<optgroup label="' + department + '"></optgroup>');
        optgroup.append(supervisor);
        departments.set(department, optgroup);
    });
    const $supervisorList = $("#empSupervisor");
    $supervisorList.empty();
    departments.forEach(optgroup => $supervisorList.append(optgroup));
    $supervisorList.selectpicker('refresh');
    $("#empDep").trigger("changed.bs.select");
}

/* Trigger department and title for supervisor */
$('#empTitle, #empDep').on('changed.bs.select', function () {
    const department = $('#empDep :selected').text().trim();
    const $supervisor = $('#empSupervisor');
    const $title = $('#empTitle :selected');
    if ($title.data('permission') == PERMISSION.SUPER_USER || $title.data('permission') == undefined || department == '') {
        $supervisor.find('optgroup').each(function () {
            $(this).prop('disabled', false);
        });
    } else {
        $supervisor.find('optgroup').each(function () {
            const optgroup = $(this).attr('label').trim();
            $(this).prop('disabled', department != optgroup);
        });
        if ($supervisor.find(':selected').parent('optgroup').prop('disabled') == true) {
            $supervisor.selectpicker('val', '');
        }
    }
    $supervisor.selectpicker('refresh');
});

/* Supervisor selectbox */
function init_supervisor(id) {
    return ajax({
        url: "GetSupervisorListAjax",
        type: 'post',
        dataType: "json"
    }).then(result => {
        if (!result.hasOwnProperty('error')) {
            supervisorOptions = [];

            const option = $("<option></option>");
            option.val(0);
            option.text("No supervisor");
            option.data('department', "Other");
            supervisorOptions.push(option);

            result.forEach(supervisor => {
                const option = $("<option></option>");
                option.val(supervisor.account.accountID);
                option.text(supervisor.fullname);
                option.data('department', supervisor.department.departmentName);
                option.data('subtext', supervisor.title.titleName);
                supervisorOptions.push(option);
            });
        }
    }).then(() => {
        formatSupervisor(id);
    }).catch(error => {
        console.log(error);
        checkSessionTimeout(error);
    });
}

/* Init detail modal validate */
function initDetailModalValidate() {
    $('#detailsModalForm').validetta({
        realTime: true,
        display: 'inline',
        errorTemplateClass: 'validetta-inline',
        validators: {
            regExp: {
                empPhone: {
                    pattern: /^\+?[0-9]+([\s\-][0-9]+){0,3}$/,
                    errorMessage: 'Must be correct phone formation.'
                }
            }
        },
        onValid: function (event) {
            promiseDetailModal(event);
        }
    });
}

/* Setup detail modal info */
function setModalInfo(id, email, name, dep, title, supervisor, type, address, phone, admin, sex, dob, jd) {
    $("#detailsModalForm").trigger("reset");
    $('.selectpicker').selectpicker('refresh');

    const $empID = $('#empID');
    const $empDoB = $('#empDoB');
    const $empJD = $('#empJD');
    const $empTitle = $('#empTitle');
    const $empDep = $('#empDep');

    $empID.text('');

    let initDoB = formatDateInput(new Date(1900, 0, 1));
    let initJD = formatDateInput(new Date());

    if (typeof id !== 'undefined') {
        $empID.text(id);
        $('#empEmail').val(email);
        $('#empName').val(name);
        $empDep.selectpicker('val', dep);
        $empTitle.selectpicker('val', title);
        formatSupervisor(id);
        $('#empSupervisor').selectpicker('val', supervisor);
        $('#empType').selectpicker('val', type);
        $('#empAddress').val(address);
        $('#empPhone').val(phone);
        $("#empAdmin").prop("checked", admin);
        $("#maleChk").prop("checked", sex);
        $("#femaleChk").prop("checked", !sex);
        initDoB = formatDateInput(new Date(dob));
        initJD = formatDateInput(new Date(jd));
    } else {
        formatSupervisor();
    }

    $empDoB.val(initDoB);
    $empDoB.data('daterangepicker').setStartDate(initDoB);
    $empDoB.data('daterangepicker').setEndDate(initDoB);
    $empJD.val(initJD);
    $empJD.data('daterangepicker').setStartDate(initJD);
    $empJD.data('daterangepicker').setEndDate(initJD);
}

/* Add modal */
$('.add-new-btn span').click(function () {
    setModalInfo();
    $('#detailsModalTitle').text("ADD EMPLOYEE");
    $("#detailsID").css('display', 'none');
    $(".password").css('display', 'block');
    $("#changepass").css('display', 'none');
    $("#empPass").attr("data-validetta", "required,minLength[6]");
    $("#empConfirm").attr("data-validetta", "required,equalTo[empPass]");
    $('#detailsModal').modal('show');
});

/* Update modal */
$datatable.on('click', '.dttb-btn-edit', function () {
    const tr = $(this).closest('tr');
    const id = tr.find('td:eq(0)').text().trim();
    const name = tr.find('td:eq(1)').find('.nametd').text().trim();
    const jd = tr.find('td:eq(4)').find('.hiddeninfo').text().trim();

    const hidden = $('#hidden-' + tr.find('td:eq(0)').text().trim()).find('span');
    const dep = $(hidden[0]).text().trim() == 0 ? "" : $(hidden[0]).text().trim();
    const title = $(hidden[1]).text().trim() == 0 ? "" : $(hidden[1]).text().trim();
    const supervisor = $(hidden[2]).text().trim();
    const type = $(hidden[3]).text().trim() == 0 ? "" : $(hidden[3]).text().trim();
    const address = $(hidden[4]).text().trim();
    const phone = $(hidden[5]).text().trim();
    const sex = $(hidden[6]).text().trim() == "true";
    const dob = moment($(hidden[7]).text().trim()).format('YYYY-MM-DD');
    const email = $(hidden[8]).text().trim();
    const admin = $(hidden[9]).text().trim() == "true";

    setModalInfo(id, email, name, dep, title, supervisor, type, address, phone, admin, sex, dob, jd);

    $('#detailsModalTitle').text("UPDATE EMPLOYEE");
    $("#detailsID").css('display', 'block');
    $(".password").css('display', 'none');
    $("#changepass").css('display', 'block');
    $("#empPass").removeAttr("data-validetta");
    $("#empConfirm").removeAttr("data-validetta");
    $('#detailsModal').modal('show');
});

/* Change password dialog */
$('#changepass').click(function () {
    const accountID = $('#empID').text().trim();
    $('#detailsModal').modal('hide');

    const cancel = function () {
        $('#detailsModal').modal('show');
    };

    const confirm = function (password) {
        return ajax({
            url: "ChangeEmployeePasswordAjax",
            type: 'post',
            data: {accountID: accountID, password: password}
        }).then(result => {
            return function () {
                checkSessionTimeout(result);

                if (result === "done") {
                    showDialogMessageWithPromise("Password changed successfully", "CHANGE PASSWORD", "success", cancel);
                } else {
                    showDialogMessageWithPromise("Failed. System errors. Please try again.", "CHANGE PASSWORD", "error", cancel);
                }
            };
        }).catch(error => {
            return error;
        });
    };

    showDialogMessageChangePass(confirm, cancel);
});

/* Btn lock/unlock */
$datatable.on('click', '.dttb-btn-lock-false, .dttb-btn-lock-true', function () {
    const isLock = $(this).hasClass('dttb-btn-lock-false');
    const tr = $(this).closest('tr');
    const accountID = tr.find('td:eq(0)').text().trim();
    const hidden = $('#hidden-' + accountID).find('span');
    const email = $(hidden[8]).text().trim();
    const btnLock = '<button type="button" class="btn btn-default btn-circle dttb-btn-lock-true"><i></i></button>';
    const btnUnlock = '<button type="button" class="btn btn-default btn-circle dttb-btn-lock-false"><i></i></button>';
    const btnAction = isLock ? btnLock : btnUnlock;
    const status = isLock ? 'unlock' : 'lock';
    const msg = isLock ? "Do you want to unlock this employee ?" : "Do you want to lock this employee ?";
    const successMsg = isLock ? "Unlock successfully" : "Lock successfully";
    const errorMsg = "There were some errors. Please try again.";

    const promiseConfirm = function () {
        return ajax({
            url: "ChangeActiveAccountAjax",
            type: 'post',
            data: {accountID: accountID, status: status, email: email}
        }).then(result => {
            return function () {
                checkSessionTimeout(result);

                if (result == "done") {
                    const data = $datatable.DataTable().row(tr).data();
                    $datatable.DataTable().row(tr).data([
                        data[0], data[1], data[2], data[3], data[4], data[5], btnAction
                    ]).draw(false);
                    showDialogMessage("DONE", successMsg, "success");
                } else {
                    showDialogMessage("FAILED", errorMsg, "error");
                }
            };
        }).catch(error => {
            return error;
        });
    };

    showDialogMessageConfirm(msg, promiseConfirm);
});

/* Handle promise operation detail modal */
function promiseDetailModal(event) {
    event.preventDefault();
    const isAdd = $('#detailsModalTitle').text().trim() == "ADD EMPLOYEE";
    const id = $("#empID").text().trim();
    const data = $("#detailsModalForm").serialize() + '&empID=' + id;

    $("#detailsModalAction").button('loading');

    ajax({
        url: isAdd ? "AddEmployeeAjax" : "UpdateEmployeeAjax",
        type: 'post',
        data: data
    }).then(result => {

        checkSessionTimeout(result);

        const success = isAdd ? "Added employee successfully." : "Update employee id " + id + " successfully";
        const errordata = "Failed. Input data errors. Please check and try again.";
        const errorsystem = "Failed. System errors. Please refresh and try again.";
        const phoneDuplicate = "Phone is already used. Try another one.";
        const emailDuplicate = "Email is already used. Try another one.";

        switch (result) {
            case "errordata":
                showErrorNotification("ERROR", errordata);
                break;
            case "errorsystem":
                showErrorNotification("ERROR", errorsystem);
                break;
            case "phone_unique":
                showWarningNotification("WARNING", phoneDuplicate);
                break;
            case "email_unique":
                showWarningNotification("WARNING", emailDuplicate);
                break;
            default:
                showSuccessNotification("COMPLETED", success);

                const $empName = $('#empName');
                const $empJD = $('#empJD');
                const $empEmail = $("#empEmail");
                const $empDepSelected = $('#empDep :selected');
                const $empTitleSelected = $('#empTitle :selected');
                const $empSupervisorSelected = $('#empSupervisor :selected');
                const $empTypeSelected = $('#empType :selected');
                const $empAdmin = $("#empAdmin").is(':checked');

                //Refresh datatable
                const td0 = isAdd ? result : id;
                const td1 = '<div class="nametd">' + $empName.val() + '</div>'
                    + '<div class="hiddeninfo">' + $empTitleSelected.data('permission') + '</div>'
                    + '<div class="titletypetd">' + $empTitleSelected.text().trim() + '/' + $empTypeSelected.text().trim() + '</div>';
                const td2 = $empSupervisorSelected.val() == "0" ? "" : $empSupervisorSelected.text().trim();
                const td3 = $empDepSelected.text().trim();
                const td4 = '<div class="hiddeninfo">' + $empJD.val() + '</div><div class="datetd">' + $empJD.val() + '</div>';
                const td5 = '<button type="button" class="btn btn-default btn-circle dttb-btn-edit"><i class="fa fa-pencil-alt"></i></button>';
                const td6 = '<button type="button" class="btn btn-default btn-circle dttb-btn-lock-true"><i></i></button>';

                if (isAdd) {
                    const newRow = $datatable.DataTable().row.add([
                        td0, td1, td2, td3, td4, td5, td6
                    ]).draw(false).node();
                    $(newRow).find('td:eq(2)').addClass('supervisortd');
                    $(newRow).find('td:gt(4)').addClass('dttb-button');
                } else {
                    $('tbody tr').each(function () {
                        if (id == $(this).find('td:eq(0)').text().trim()) {
                            $datatable.DataTable().row($(this)).data([
                                td0, td1, td2, td3, td4, td5, td6
                            ]).draw(false);
                            $(this).fadeIn(100).fadeOut(100).fadeIn(100).fadeOut(100).fadeIn(100);
                        }
                    });
                }

                //Refresh hidden info
                if (isAdd) {
                    const newHiddenInfo = '<div class="hiddeninfo" id="hidden-' + td0 + '"></div>';
                    $(".x_content").append(newHiddenInfo);
                }

                const $hidden = $("#hidden-" + td0);
                $hidden.empty();
                $hidden.append('<span>' + $empDepSelected.val() + '</span>');
                $hidden.append('<span>' + $empTitleSelected.val() + '</span>');
                $hidden.append('<span>' + ($empSupervisorSelected.val() == "0" ? "" : $empSupervisorSelected.val()) + '</span>');
                $hidden.append('<span>' + $empTypeSelected.val() + '</span>');
                $hidden.append('<span>' + $("#empAddress").val() + '</span>');
                $hidden.append('<span>' + $("#empPhone").val() + '</span>');
                $hidden.append('<span>' + $("#maleChk").is(':checked') + '</span>');
                $hidden.append('<span>' + moment($("#empDoB").val(), 'MMMM DD YYYY').format('YYYY-MM-DD') + '</span>');
                $hidden.append('<span>' + $empEmail.val() + '</span>');
                $hidden.append('<span>' + $empAdmin + '</span>');

                //Refresh modal detail info for add
                if (isAdd)
                    setModalInfo();
                else if (id == $("#profile_info_id").text().trim()) {
                    // Update if current account updated
                    const name = $empName.val().trim();
                    const dep = $empDepSelected.text().trim();
                    const title = $empTitleSelected.text().trim();
                    const type = $empTypeSelected.text().trim();
                    const email = '<img src="images/user-avatar.png" alt="">' + $empEmail.val().trim() + ' <span class=" fa fa-angle-down"></span>';
                    $('#profile_info_name').text(name);
                    $('#profile_info_dep').text(dep);
                    $('#profile_info_title').text(title);
                    $('#profile_info_type').text(type);
                    $('#profile_info_email').html(email);
                    $('#profile_info_admin').text($empAdmin);
                    $('#profile_info_permission').text($empTitleSelected.data('permission'));
                    if ($empAdmin == 'false') {
                        $('#main-content').empty();
                    }
                    formatSideMenu();
                }

                //Refresh supervisor checkbox
                init_supervisor(id);
                break;

        }
    }).then(() => {
        $("#detailsModalAction").button('reset');
    }).catch(error => {
        $("#detailsModalAction").button('reset');
        console.log(error);
    });

}

/* Main */
$(function () {
    datepicker();
    initDatatable();
    initDetailModalValidate();
    init_supervisor();
    $('.selectpicker').selectpicker();
    formatTitle();
    hideWrapperSpinner();
});


