if (typeof jQuery === "undefined") {
    throw new Error("jQuery plugins need to be before this file");
}

/* Custom js - Function ======================================================================================================
 *  Some custom functions
 *  
 */

let $datatable = $('#datatable');
let $datatablerequest = $('#datatable-request');

/* Init datatable */
function initDatatable() {

    let exportOptions = {
        title: 'Annual Leave Report - ' + moment().format('YYYY'),
        exportOptions: {
            columns: [0, 1, 2, 3, 4],
            format: {
                body: function ( data, row, column, node ) {
                    if (column === 0) return data;
                    if (column === 1) {
                        const text = node.innerText.trim().split("\n");
                        return text[0].trim() + "\n" + text[1].trim();
                    }
                    if (column === 2) {
                        const text = node.innerText.trim().split("\n");
                        return (text.length == 1) ? text[0].trim() : text[1].trim();
                    }
                    if (column === 3 || column === 4) {
                        return (data > 1.0) ? data + " Days" : data + " Day";
                    }
                }
            }
        }
    };

    $datatable.DataTable({
        language: {
            search: '<i class="glyphicon glyphicon-search"></i>',
            searchPlaceholder: "Filter table ..."
        },
        columnDefs: [{orderable: false, targets: [5]}],
        dom: 'Bfrtip',
        buttons: [
            $.extend( true, {}, exportOptions, {
                extend: 'excelHtml5'
            } ),
            $.extend( true, {}, exportOptions, {
                extend: 'pdfHtml5',
                customize: function ( doc ) {
                    doc.content[1].table.widths = [
                        '10%',
                        '35%',
                        '25%',
                        '15%',
                        '15%',
                    ]
                }
            } ),
        ]
    });

    $datatablerequest.DataTable({
        searching: false,
        ordering: false,
        paging: false,
        info: false
    });
}

/* Format datatable */
function formatTableInfo() {

    //Format name and title
    $('.titletd').each(function () {
        const permission = $(this).siblings(".hiddeninfo").text().trim();
        if (permission == PERMISSION.SUPER_USER) {
            $(this).parent().siblings().find('.nametd').css('color', 'dodgerblue');
            $(this).css('color', 'dodgerblue');
        }
    });

    //Format day number total
    $('.totaltd').each(function () {
        const day = parseFloat($(this).text().trim());
        $(this).text( day > 1.0 ? (day + " Days") : (day + " Day"));
    });

    //Format day number limit
    $('.limittd').each(function () {
        const day = parseInt($(this).text().trim());
        $(this).text( day > 1 ? (day + " Days") : (day + " Day"));
    });
}

/* Reformat datatable when content changed */
$datatablerequest.on('draw.dt', function () {
    setTimeout(function () {
        formatTableInfo();
    }, 10);
});
$datatable.on('draw.dt', function () {
    setTimeout(function () {
        formatTableInfo();
    }, 10);
});

/* Btn approve and deny */
$datatablerequest.on('click', '.dttb-btn-approve, .dttb-btn-deny', function () {
    const tr = $(this).closest('tr');
    const eventID = tr.find('td:eq(0)').text().trim();
    const creator = tr.find('td:eq(1)').find('.hiddeninfo').text().trim();
    const isApprove = $(this).hasClass('dttb-btn-approve');
    const msg = isApprove ? "Do you want to approve this annual leave ?" : "Do you want to deny this annual leave ?";
    const successMsg = isApprove ? "Approved successfully" : "Denied successfully";
    const errorMsg = "There were some errors. Please try again.";

    const promiseConfirm = function () {
        return ajax({
            url: "ChangeStatusAnnualLeaveAjax",
            type: 'post',
            data: {eventID: eventID, approve: isApprove}
        }).then(result => {
            return function () {

                checkSessionTimeout(result);
                if (result != 'error') {
                    $datatablerequest.DataTable().row(tr).remove().draw(false);

                    if (isApprove) {
                        $datatable.find('tbody tr').each(function () {
                            if (creator == $(this).find('td:eq(0)').text().trim()) {
                                const rowdata = $datatable.DataTable().row($(this)).data();
                                const days = parseFloat(rowdata[3]) + parseFloat(result.split("-")[1]) + " Days";
                                $datatable.DataTable().row($(this)).data([
                                    rowdata[0], rowdata[1], rowdata[2], days, rowdata[4], rowdata[5]
                                ]).draw(false);
                                $(this).fadeIn(100).fadeOut(100).fadeIn(100).fadeOut(100).fadeIn(100);
                            }
                        });
                    }

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

/* Btn change limit */
$datatable.on('click', '.dttb-btn-edit', function () {
    const tr = $(this).closest('tr');
    const empID = tr.find('td:eq(0)').text().trim();
    const successMsg = "Changed successfully.";
    const errorMsg = "There were some errors. Please try again.";

    const confirm = function (day) {
        return ajax({
            url: "ChangeAnnualLeaveLimitAjax",
            type: 'post',
            data: {empID: empID, day: day}
        }).then(result => {
            return function () {

                checkSessionTimeout(result);
                if (result != 'error') {

                    $datatable.find('tbody tr').each(function () {
                        if (empID == $(this).find('td:eq(0)').text().trim()) {
                            const rowdata = $datatable.DataTable().row($(this)).data();
                            $datatable.DataTable().row($(this)).data([
                                rowdata[0], rowdata[1], rowdata[2], rowdata[3], day, rowdata[5]
                            ]).draw(false);
                            $(this).fadeIn(100).fadeOut(100).fadeIn(100).fadeOut(100).fadeIn(100);
                        }
                    });

                    showDialogMessage("DONE", successMsg, "success");
                } else {
                    showDialogMessage("FAILED", errorMsg, "error");
                }
            };
        }).catch(error => {
            return error;
        });
    };

    showDialogMessageChangeAnnualLeaveLimit(confirm);
});

/* Main */
$(function () {
    initDatatable();
    hideWrapperSpinner();
});





