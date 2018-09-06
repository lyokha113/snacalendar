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
        const date = $(this).siblings('.hiddeninfo').text().trim();
        $(this).text(formatDate(date));
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
        validators: {
            regExp: {
                carSlot: {
                    pattern: /^[1-9][0-9]*$/i,
                    errorMessage: 'Must be a number greater than 0.'
                }
            }
        },
        onValid: function (event) {
            promiseDetailModal(event);
        },
    });
}

/* Setup detail modal info */
function setModalInfo(id, plate, brand, slot) {
    $("#detailsModalForm").trigger("reset");

    const $carID = $('#carID');
    $carID.text('');

    if (typeof id !== 'undefined') {
        $carID.text(id);
        $('#carPlate').val(plate);
        $('#carBrand').val(brand);
        $('#carSlot').val(slot);
    }
}

/* Add modal */
$('.add-new-btn span').click(function () {
    setModalInfo();
    $('#detailsModalTitle').text("ADD CAR");
    $("#detailsID").css('display', 'none');
    $('#detailsModal').modal('show');
});

/* Update modal */
$datatable.on('click', '.dttb-btn-edit', function () {
    const tr = $(this).closest('tr');
    const id = tr.find('td:eq(0)').text().trim();
    const plate = tr.find('td:eq(1)').text().trim();
    const brand = tr.find('td:eq(2)').text().trim();
    const slot = tr.find('td:eq(3)').text().trim();

    setModalInfo(id, plate, brand, slot);

    $('#detailsModalTitle').text("UPDATE CAR");
    $("#detailsID").css('display', 'block');
    $('#detailsModal').modal('show');
});

/* Handle promise operation detail modal */
function promiseDetailModal(event) {
    event.preventDefault();
    const isAdd = $('#detailsModalTitle').text().trim() == "ADD CAR";
    const id = $("#carID").text().trim();
    const data = $("#detailsModalForm").serialize() + '&carID=' + id;

    $("#detailsModalAction").button('loading');

    ajax({
        url: isAdd ? "AddCarAjax" : "UpdateCarAjax",
        type: 'post',
        data: data
    }).then(result => {

        // Session timeout
        if (result.includes("DOCTYPE html")) {
            location.href = "logout";
            return;
        }

        const success = isAdd ? "Added car successfully." : "Update car id " + id + " successfully";
        const errordata = "Failed. Input data errors. Please check and try again.";
        const errorsystem = "Failed. System errors. Please refresh and try again.";
        const plateDuplicate = "Car plate number is already used. Try another one.";

        switch (result) {
            case "errordata":
                showErrorNotification("ERROR", errordata);
                break;
            case "errorsystem":
                showErrorNotification("ERROR", errorsystem);
                break;
            case "plate_unique":
                showWarningNotification("WARNING", plateDuplicate);
                break;
            default:
                showSuccessNotification("COMPLETED", success);

                //Refresh datatable
                const td0 = isAdd ? result : id;
                const td1 = $("#carPlate").val().trim();
                const td2 = $("#carBrand").val().trim();
                const td3 = $("#carSlot").val().trim();
                const td4 = '<div class="hiddeninfo">' + new Date($.now()) + '</div><div class="datetd">' + new Date($.now()) + '</div>';
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
                            const rowdata = $datatable.DataTable().row($(this)).data();
                            $datatable.DataTable().row($(this)).data([
                                td0, td1, td2, td3, rowdata[4], td5, td6
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
    const carID = tr.find('td:eq(0)').text().trim();
    const msg = "All relative informations with this will also be deleted. Do you want to delete ?";
    const successMsg = "Deleted successfully";
    const errorMsg = "There were some errors. Please try again.";

    const promiseConfirm = function () {
        return  ajax({
            url: "DeleteCarAjax",
            type: 'post',
            data: {carID: carID}
        }).then(function fulfillHandler(result) {
            return function () {
                // Session timeout
                if (result.includes("DOCTYPE html")) {
                    location.href = "logout";
                    return;
                }

                if (result == "done") {
                    $datatable.DataTable().row(tr).remove().draw(false);
                    showDialogMessage("DONE", successMsg, "success");
                } else {
                    showDialogMessage("FAILED", errorMsg, "error");
                }
            };

        }).catch(function errorHandler(error) {
            return error;
        });
    };

    showDialogMessageConfirm(msg, promiseConfirm);
});

/* Main */
$(function () {
    initDatatable();
    initDetailModalValidate();
    hideWrapperSpinner();
});




