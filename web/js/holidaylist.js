if (typeof jQuery === "undefined") {
    throw new Error("jQuery plugins need to be before this file");
}

/* Custom js - Function ======================================================================================================
 *  Some custom functions
 *  
 */

const $datatable = $('#datatable');

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

    //Format datetime
    $('.datetd').each(function () {
        $(this).text(moment(new Date($(this).text().trim())).format('MMMM DD'));
    });

    //Format vacation
    $('.vacationtd').each(function () {
        const vacation = $(this).siblings('.hiddeninfo').text();
        $(this).text(vacation == 'true' ? "Off" : "Working");
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
    const options = {
        parentEl: '#holidayDateParent',
        opens: "center",
        locale: {"format": "MMMM DD", "separator": " - ",},
        minDate: moment(new Date(2018, 0, 1)).format('MMMM DD'),
        maxDate: moment(new Date(2018, 11, 31)).format('MMMM DD'),
    };

    $('#holidayDate').daterangepicker(options)
        .on('renderCalendar.daterangepicker', function setTitle() {
            const leftmonth = $('.drp-calendar.left .table-condensed thead tr .month');
            const righttmonth = $('.drp-calendar.right .table-condensed thead tr .month');
            leftmonth.text(leftmonth.text().split(" ")[0]);
            righttmonth.text(righttmonth.text().split(" ")[0]);
        });
}

/* Init detail modal validate */
function initDetailModalValidate() {
    $('#detailsModalForm').validetta({
        realTime: true,
        display: 'inline',
        errorTemplateClass: 'validetta-inline',
        onValid: function (event) {
            promiseDetailModal(event);
        }
    });
}

/* Setup detail modal info */
function setModalInfo(id, name, startDate, endDate, vacation) {
    let initStartDate = moment(new Date(2018, 0, 1)).format('MMMM DD');
    let initEndDate = initStartDate;

    $("#detailsModalForm").trigger("reset");

    const $holidayID = $('#holidayID');
    const $holidayDate = $('#holidayDate');
    $holidayID.text('');

    if (typeof id !== 'undefined') {
        $holidayID.text(id);
        $('#holidayName').val(name);
        initStartDate = moment(startDate).format("MMMM DD");
        initEndDate = moment(endDate).format("MMMM DD");
        $("#holidayVacation").prop('checked', vacation == HOLIDAY_VACATION.OFF);
    }

    $holidayDate.val(initStartDate + " - " + initEndDate);
    $holidayDate.data('daterangepicker').setStartDate(initStartDate);
    $holidayDate.data('daterangepicker').setEndDate(initEndDate);
}

/* Add modal */
$('.add-new-btn span').click(function () {
    setModalInfo();
    $('#detailsModalTitle').text("ADD HOLIDAY");
    $("#detailsID").css('display', 'none');
    $('#detailsModal').modal('show');
});

/* Update modal */
$datatable.on('click', '.dttb-btn-edit', function () {
    const tr = $(this).closest('tr');
    const id = tr.find('td:eq(0)').text().trim();
    const name = tr.find('td:eq(1)').text().trim();
    const startDate = tr.find('td:eq(2) .hiddeninfo').text().trim();
    const endDate = tr.find('td:eq(3) .hiddeninfo').text().trim();
    const vacation = tr.find('td:eq(4) .hiddeninfo').text().trim();

    setModalInfo(id, name, startDate, endDate, vacation == 'true');

    $('#detailsModalTitle').text("UPDATE HOLIDAY");
    $("#detailsID").css('display', 'block');
    $('#detailsModal').modal('show');
});

/* Handle promise operation detail modal */
function promiseDetailModal(event) {
    event.preventDefault();
    const isAdd = $("#detailsModalTitle").text() == "ADD HOLIDAY";
    const id = $("#holidayID").text().trim();
    const data = $("#detailsModalForm").serialize() + '&holidayID=' + id;

    $("#detailsModalAction").button('loading');

    ajax({
        url: isAdd ? "AddHolidayAjax" : "UpdateHolidayAjax",
        type: 'post',
        data: data
    }).then(result => {

        checkSessionTimeout(result);

        const success = isAdd ? "Added holiday successfully." : "Update holiday id " + id + " successfully";
        const errordata = "Failed. Input data errors. Please check and try again.";
        const errorsystem = "Failed. System errors. Please refresh and try again.";
        const errordate = "Start date must be before end date. Please try again.";
        const nameDuplicate = "Holiday name is already used. Try another one.";

        switch (result) {
            case "errordata":
                showErrorNotification("ERROR", errordata);
                break;
            case "errorsystem":
                showErrorNotification("ERROR", errorsystem);
                break;
            case "errordate":
                showErrorNotification("ERROR", errordate);
                break;
            case "name_unique":
                showWarningNotification("WARNING", nameDuplicate);
                break;
            default:
                showSuccessNotification("COMPLETED", success);

                const $holidayDate = $("#holidayDate");
                //Refresh datatable
                const td0 = isAdd ? result : id;
                const td1 = $("#holidayName").val().trim();
                const td2 = '<div class="hiddeninfo">' + $holidayDate.val().trim().split(" - ")[0]
                    + '</div><div class="datetd">' + $holidayDate.val().trim().split(" - ")[0] + '</div>';
                const td3 = '<div class="hiddeninfo">' + $holidayDate.val().trim().split(" - ")[1]
                    + '</div><div class="datetd">' + $holidayDate.val().trim().split(" - ")[1] + '</div>';
                const td4 = '<td><div class="hiddeninfo">' + $("#holidayVacation").prop('checked') + '</div>'
                    + '<div class="vacationtd"></div></td>';
                const td5 = '<button type="button" class="btn btn-default btn-circle dttb-btn-edit"><i class="fa fa-pencil-alt"></i></button>';
                const td6 = '<button type="button" class="btn btn-default btn-circle dttb-btn-delete"><i class="fa fa-trash-alt"></i></button>';

                if (isAdd) {
                    const newRow = $datatable.DataTable().row.add([
                        td0, td1, td2, td3, td4, td5, td6
                    ]).draw(false).node();
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

                //Refresh modal detail info
                if (isAdd)
                    setModalInfo();
                break;

        }
    }).then(() => {
        $("#detailsModalAction").button('reset');
    }).catch(error => {
        $("#detailsModalAction").button('reset');
        console.log(error);
    });
}

/* Btn delete */
$datatable.on('click', '.dttb-btn-delete', function () {
    const tr = $(this).closest('tr');
    const holidayID = tr.find('td:eq(0)').text().trim();
    const msg = "All relative informations with this will also be deleted. Do you want to delete ?";
    const successMsg = "Deleted successfully";
    const errorMsg = "There were some errors. Please try again.";

    const promiseConfirm = function () {
        return ajax({
            url: "DeleteHolidayAjax",
            type: 'post',
            data: {holidayID: holidayID}
        }).then(result => {
            return function () {

                checkSessionTimeout(result);

                if (result == "done") {
                    $datatable.DataTable().row(tr).remove().draw(false);
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

/* Main */
$(function () {
    datepicker();
    initDatatable();
    initDetailModalValidate();
    hideWrapperSpinner();
});




