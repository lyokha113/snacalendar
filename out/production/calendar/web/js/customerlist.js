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
            {orderable: false, targets: [4, 5]}
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
        onValid: function (event) {
            promiseDetailModal(event);
        }
    });
}

/* Setup detail modal info */
function setModalInfo(id, name, address) {
    $("#detailsModalForm").trigger("reset");

    const $custID = $('#custID');
    $custID.text('');

    if (typeof id !== 'undefined') {
        $custID.text(id);
        $('#custName').val(name);
        $('#custAddress').val(address);
    }
}

/* Add modal */
$('.add-new-btn span').click(function () {
    setModalInfo();
    $('#detailsModalTitle').text("ADD CUSTOMER");
    $("#detailsID").css('display', 'none');
    $('#detailsModal').modal('show');
});

/* Update modal */
$datatable.on('click', '.dttb-btn-edit', function () {
    const tr = $(this).closest('tr');
    const id = tr.find('td:eq(0)').text().trim();
    const name = tr.find('td:eq(1)').text().trim();
    const address = tr.find('td:eq(2)').text().trim();

    setModalInfo(id, name, address);

    $('#detailsModalTitle').text("UPDATE CUSTOMER");
    $("#detailsID").css('display', 'block');
    $('#detailsModal').modal('show');
});

/* Handle promise operation detail modal */
function promiseDetailModal(event) {
    event.preventDefault();
    const isAdd = $('#detailsModalTitle').text().trim() == "ADD CUSTOMER";
    const id = $("#custID").text().trim();
    const data = $("#detailsModalForm").serialize() + '&custID=' + id;

    $("#detailsModalAction").button('loading');

    ajax({
        url: isAdd ? "AddCustomerAjax" : "UpdateCustomerAjax",
        type: 'post',
        data: data
    }).then(result => {

        checkSessionTimeout(result);

        const success = isAdd ? "Added customer successfully." : "Update customer id " + id + " successfully";
        const errordata = "Failed. Input data errors. Please check and try again.";
        const errorsystem = "Failed. System errors. Please refresh and try again.";
        const nameDuplicate = "Customer name is already used. Try another one.";

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
                const td0 = isAdd ? result : id;
                const td1 = $("#custName").val().trim();
                const td2 = $("#custAddress").val().trim();
                const td3 = '<div class="hiddeninfo">' + new Date($.now()) + '</div><div class="datetd">' + new Date($.now()) + '</div>';
                const td4 = '<button type="button" class="btn btn-default btn-circle dttb-btn-edit"><i class="fa fa-pencil-alt"></i></button>';
                const td5 = '<button type="button" class="btn btn-default btn-circle dttb-btn-delete"><i class="fa fa-trash-alt"></i></button>';

                if (isAdd) {
                    const newRow = $datatable.DataTable().row.add([
                        td0, td1, td2, td3, td4, td5
                    ]).draw(false).node();
                    $(newRow).find('td:gt(3)').addClass('dttb-button');
                } else {
                    $('tbody tr').each(function () {
                        if (id == $(this).find('td:eq(0)').text().trim()) {
                            const rowdata = $datatable.DataTable().row($(this)).data();
                            $datatable.DataTable().row($(this)).data([
                                td0, td1, td2, rowdata[3], td4, td5
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
    const customerID = tr.find('td:eq(0)').text().trim();
    const msg = "All relative informations with this will also be deleted. Do you want to delete ?";
    const successMsg = "Deleted successfully";
    const errorMsg = "There were some errors. Please try again.";

    const promiseConfirm = function () {
        return ajax({
            url: "DeleteCustomerAjax",
            type: 'post',
            data: {customerID: customerID}
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

/* Show venue map */
function showMap() {
    const modal = $("#venue-map-modal");
    const map = $("#map");
    const body = $("#venue-map-modal .modal-body");
    const errorDiv = $("#apiLoadError");
    const preloader = $("#venue-map-modal .modal-body .page-loader-wrapper");

    modal.on('show.bs.modal', function() {
        $("#detailsModal").modal('hide');
    });

    modal.on('hide.bs.modal', function() {
        $("#detailsModal").modal('show');
    });

    function setError(error) {
        body.css('height', '50px');
        errorDiv.removeClass('hiddeninfo');
        errorDiv.text(error);
        preloader.hide();
    }

    $("#showlocation").click(() => {

        const custAddress = $("#custAddress").val();
        console.log(custAddress);
        if (custAddress == "") return;
        const address = custAddress;

        body.css('height', '400px');
        errorDiv.addClass('hiddeninfo');
        map.addClass('hiddeninfo');
        preloader.fadeIn();
        modal.modal('show');

        getGoogleApiScript().then(() => {

            const gmap = new google.maps.Map(map[0], {
                center: {lat: 0, lng: 0},
                zoom: 15
            });

            const geocoder = new google.maps.Geocoder();
            geocoder.geocode({'address': address}, (results, status) => {
                if (status == google.maps.GeocoderStatus.OK) {

                    gmap.setCenter(results[0].geometry.location);
                    const infowindow = new google.maps.InfoWindow({
                        content: '<b>' + address + '</b>',
                        size: new google.maps.Size(150, 50)
                    });

                    const marker = new google.maps.Marker({
                        position: results[0].geometry.location,
                        map: gmap,
                        title: address
                    });

                    google.maps.event.addListener(marker, 'click', function () {
                        infowindow.open(gmap, marker);
                    });

                    preloader.hide();
                    map.removeClass('hiddeninfo');

                } else {
                    setError("This address location can't be found.");
                }
            });
        }).catch(() => {
            setError("Google api loaded error.");
        });
    });
}

/* Main */
$(function () {
    initDatatable();
    initDetailModalValidate();
    showMap();
    hideWrapperSpinner();
});





