if (typeof jQuery === "undefined") {
    throw new Error("jQuery plugins need to be before this file");
}

/* Custom js - Function ======================================================================================================
 *  Some custom functions
 *  
 */

let $datatable = $('#datatable');

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

    //Format permission
    $('.permissiontd').each(function () {
        const text = $(this).siblings('.hiddeninfo').text().trim();
        if (text == PERMISSION.SUPER_USER) {
            $(this).parent().siblings('.titletd').css('color', 'dodgerblue');
            $(this).css('color', 'dodgerblue');
        } else  {
            $(this).parent().siblings('.titletd').css('color', 'black');
            $(this).css('color', 'black');
        }
    });

    //Format amount
    $('.amounttd').each(function () {
        const text = $(this).text().trim();
        if (text == "" || text == "None" || text == "0") {
            $(this).text("None");
            $(this).css('color', 'lightgrey');
        } else  {
            $(this).css('color', 'black');
        }
    });

    //Format datetime
    $('.datetd').each(function () {
        const date = $(this).siblings('.hiddeninfo').text().trim();
        $(this).text(formatDate(date));
    });

    //Prevent delete department if there are accounts belong to it.
    $('.dttb-btn-delete').each(function () {
        if ($(this).parent().siblings(".amounttd").text().trim() != "None") {
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

/* Init detail modal validate */
function initDetailModalValidate() {
    $('#detailsModalForm').validetta({
        realTime: true,
        display: 'inline',
        errorTemplateClass: 'validetta-inline',
        onValid: function (event) {
            promiseDetailModal(event);
        },
        onError: function (event) {
        }
    });
}

/* Setup detail modal info */
function setModalInfo(id, name, permission) {
    $("#detailsModalForm").trigger("reset");
    $('.selectpicker').selectpicker('refresh');

    const $titleID = $('#titleID');
    $titleID.text('');

    if (typeof id !== 'undefined') {
        $titleID.text(id);
        $('#titleName').val(name);
        $('#titlePer').selectpicker('val', permission);
    }
}

/* Add modal */
$('.add-new-btn span').click(function () {
    setModalInfo();
    $('#detailsModalTitle').text("ADD TITLE");
    $("#detailsID").css('display', 'none');
    $('#detailsModal').modal('show');
});

/* Update modal */
$datatable.on('click', '.dttb-btn-edit', function () {
    const tr = $(this).closest('tr');
    const id = tr.find('td:eq(0)').text().trim();
    const name = tr.find('td:eq(1)').text().trim();
    const permission = tr.find('td:eq(2) .hiddeninfo').text().trim();

    setModalInfo(id, name, permission);

    $('#detailsModalTitle').text("UPDATE TITLE");
    $("#detailsID").css('display', 'block');
    $('#detailsModal').modal('show');
});

/* Handle promise operation detail modal */
function promiseDetailModal(event) {
    event.preventDefault();
    const isAdd = $('#detailsModalTitle').text().trim() == "ADD TITLE";
    const id = $("#titleID").text().trim();
    const data = $("#detailsModalForm").serialize() + '&titleID=' + id;

    $("#detailsModalAction").button('loading');

    ajax({
        url: isAdd ? "AddTitleAjax" : "UpdateTitleAjax",
        type: 'post',
        data: data
    }).then(result => {

        checkSessionTimeout(result);

        const success = isAdd ? "Added title successfully." : "Update title id " + id + " successfully";
        const errordata = "Failed. Input data errors. Please check and try again.";
        const errorsystem = "Failed. System errors. Please refresh and try again.";
        const nameDuplicate = "Title name is already used. Try another one.";

        switch (result) {
            case "errordata":
                showErrorNotification("ERROR", errordata);
                break;
            case "errorsystem":
                showErrorNotification("ERROR", errorsystem);
                break;
            case "name_unique":
                showWarningNotification("WARNING", nameDuplicate);
                break;
            default:
                showSuccessNotification("COMPLETED", success);

                //Refresh datatable
                const permisson = $("#titlePer :selected");
                const td0 = isAdd ? result : id;
                const td1 = $("#titleName").val().trim();
                const td2 = '<div class="hiddeninfo">' + permisson.val() + '</div><div class="permissiontd">' + permisson.text().trim()  + '</div>';
                const td3 = "None";
                const td4 = '<div class="hiddeninfo">' + new Date($.now()) + '</div><div class="datetd">' + new Date($.now()) + '</div>';
                const td5 = '<button type="button" class="btn btn-default btn-circle dttb-btn-edit"><i class="fa fa-pencil-alt"></i></button>';
                const td6 = '<button type="button" class="btn btn-default btn-circle dttb-btn-delete"><i class="fa fa-trash-alt"></i></button>';

                if (isAdd) {
                    const newRow = $datatable.DataTable().row.add([
                        td0, td1, td2, td3, td4, td5, td6
                    ]).draw(false).node();
                    $(newRow).find('td:eq(1)').addClass('titletd');
                    $(newRow).find('td:eq(3)').addClass('amounttd');
                    $(newRow).find('td:gt(4)').addClass('dttb-button');
                } else {
                    $('tbody tr').each(function () {
                        if (id == $(this).find('td:eq(0)').text().trim()) {
                            const rowdata = $datatable.DataTable().row($(this)).data();
                            $datatable.DataTable().row($(this)).data([
                                td0, td1, td2, rowdata[3], rowdata[4], td5, td6
                            ]).draw(false);

                            //Update if title of current account was changed
                            const titleDiv = $('#profile_info_title');
                            const permissionDiv = $('#profile_info_permission');
                            console.log(titleDiv.text().trim(), permissionDiv.text().trim());
                            if (titleDiv.text().trim() == rowdata[1]) {
                                titleDiv.text(td1);
                                permissionDiv.text(permisson.val());
                                formatSideMenu();
                            }

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
    const titleID = tr.find('td:eq(0)').text().trim();
    const msg = "All relative informations with this will also be deleted. Do you want to delete ?";
    const successMsg = "Deleted successfully";
    const errorMsg = "There were some errors. Please try again.";

    const promiseConfirm = function () {
        return ajax({
            url: "DeleteTitleAjax",
            type: 'post',
            data: {titleID: titleID}
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
    initDatatable();
    initDetailModalValidate();
    $('.selectpicker').selectpicker();
    hideWrapperSpinner();
});




