if (typeof jQuery === "undefined") {
    throw new Error("jQuery plugins need to be before this file");
}

/* Custom js - Function ======================================================================================================
 *  Some custom functions
 *  
 */

const CSS_COLOR_NAMES = ["green", "red", "purple", "teal", "blue", "gold", "deepink", "slategray", "firebrick", "orange", "aero", "sienna", "slategray", "tan", "silver"];
const department_color = new Map();
const list_attendees = new Map();
const list_carreservation = new Map();
let calendar = null;
let events_source = [{}];
let exceptEventID = 0;
let firstLoad = true;

/* Date picker */
function datepicker() {
    if (typeof ($.fn.daterangepicker) === 'undefined') {
        return;
    }

    const allDay = $("#allDay").is(':checked');

    const options = {
        parentEl: '#eventDateParent',
        opens: "center",

        // isInvalidDate: function (date) {
        //     return moment(date).weekday() == 0 || moment(date).weekday() == 6;
        // },
    };

    const withTime = {
        timePicker: true,
        timePickerIncrement: 10,
        timePicker24Hour: true,
        locale: {
            "firstDay": 1,
            "format": "HH:mm, MMMM DD YYYY",
            "separator": " - "
        },
        minDate: moment().format('HH:mm, MMMM DD YYYY'),
    };

    const withoutTime = {
        locale: {
            "firstDay": 1,
            "format": "MMMM DD YYYY",
            "separator": " - "
        },
        minDate: moment().format('MMMM DD YYYY'),
    };

    $.extend(options, allDay ? withoutTime : withTime);
    $('#eventDate').daterangepicker(options).on('apply.daterangepicker', function () {
        formatCarSelect();
    });
}

/* Department color */
function init_departmentColor() {
    $("#department option").each(function (index) {
        if (index == 0) return;
        if (index > CSS_COLOR_NAMES.length) index %= CSS_COLOR_NAMES.length;
        $(this).data('icon', 'fas fa-circle ' + CSS_COLOR_NAMES[index]);
        department_color.set($(this).val(), CSS_COLOR_NAMES[index]);
    })
}

/* Calendar holidays */
function init_calendarHolidays() {
    return ajax({
        url: "GetHolidayAjax",
        type: 'post',
        dataType: "json"
    }).then(result => {
        const holidays = {};
        if (!result.hasOwnProperty('error')) {

            result.forEach(holiday => {

                //Recreate holidays array element
                const sDate = holiday.holidayStartDate.split("-")[2] + "-" + holiday.holidayStartDate.split("-")[1];
                const eDate = holiday.holidayEndDate.split("-")[2] + "-" + holiday.holidayEndDate.split("-")[1];
                holiday.holidayDate = sDate != eDate ? sDate + ">" + eDate : sDate;

                holidays[holiday.holidayDate] = holiday.holidayName;
            });
        }
        return holidays;
    }).catch(error => {
        console.log(error);
        checkSessionTimeout(error);
    });
}

/* Calendar event source */
function init_calendarEventSource() {
    return ajax({
        url: "GetEventAjax",
        type: 'post',
        data: {eventType: EVENT_TYPE.CUSTOMER_CARE},
        dataType: "json"
    }).then(result => {
        const events = [];
        if (!result.hasOwnProperty('error')) {
            result.forEach(event => {
                const departmentID = department_color.get(String(event.creator.department.departmentID));
                event.class = "event-custom-" + departmentID;
                events.push(event);
            });
        }
        events_source = events;
    }).catch(error => {
        console.log(error);
        checkSessionTimeout(error);
    });
}

/* Calendar options */
function init_calendarOptions() {
    return {
        first_day: 1,
        weekbox: false,
        display_week_numbers: false,
        events_source: [{}],
        tmpl_path: "/vendors/bootstrap-calendar/tmpls/",
        views: {
            year: {
                slide_events: 0,
                enable: 0
            },
            month: {
                slide_events: 1,
                enable: 1
            },
            week: {
                enable: 1
            },
            day: {
                enable: 0
            },
        },
        modal: "#events-modal",
        modal_type: "template",
        onAfterViewLoad: function () {
            $('#currentDate').text(this.getTitle());
        }
    };
}

/* Calendar navigator */
function init_calendarNav(calendar) {
    $('.calendar-nav-button button[data-calendar-nav]').each(function () {
        $(this).click(function () {
            calendar.navigate($(this).data('calendar-nav'));
        });
    });
    return calendar;
}

/* Calendar view */
function init_calendarView(calendar) {
    $('.calendar-view-button button[data-calendar-view]').each(function() {
        $(this).click(function () {
            calendar.view($(this).data('calendar-view'));
            $(this).siblings('button').removeClass('active');
            $(this).addClass('active');
        });
    });
    return calendar;
}

/* Calendar */
function init_calendar() {
    const options = init_calendarOptions();
    init_calendarHolidays().then(holidays => {
        options.holidays = holidays;
        return init_calendarEventSource();
    }).then(() => {
        options.events_source = events_source;
        return $('#calendar').calendar(options);
    }).then(cal => {
        calendar = init_calendarView(cal);
        calendar = init_calendarNav(cal);
    });
}

/* Get list car reservation */
function init_carResercation() {
    return ajax({
        url: "GetCarReservationAjax",
        type: 'post',
        dataType: "json"
    }).then(result => {
        list_carreservation.clear();
        for (const carID in result) {
            list_carreservation.set(carID, result[carID]);
        }
        formatCarSelect();
    }).catch(error => {
        console.log(error);
        checkSessionTimeout(error);
    });
}

/* Filter calendar */
$('#department, #customer').on('changed.bs.select', function () {
    const department = $("#department").val();
    const customer = $("#customer").val();
    let events = events_source;

    if (department != 'all') {
        events = events.filter(event => event.creator.department.departmentID == department);
    }

    if (customer != 'others' && customer != 'all') {
        events = events.filter(event => event.customer.customerID == customer);
    } else if (customer == 'others') {
        events = events.filter(event => event.customer.customerID == 0);
    }
    calendar.options.events_source = events;
    calendar.view();
});

/* Text editor */
function init_texteditor() {
    $('#texteditor').summernote({
        dialogsInBody: true,
        disableDragAndDrop: true,
        height: 150,
        minHeight: 150,
        maxHeight: 150,
        toolbar: [
            ['style', ['style']],
            ['para', ['ul', 'ol', 'paragraph', 'table', 'hr']],
            ['fontstyle', ['bold', 'italic', 'underline', 'strikethrough', 'superscript', 'subscript', 'clear']],
            ['font', ['fontname', 'fontsize', 'color', 'height']],
            ['misc', ['help']]
        ]
    });
}

/* Init add/update detail modal validate */
function init_ModalValidate() {
    $('#eventDetailsForm').validetta({
        realTime: true,
        display: 'inline',
        errorTemplateClass: 'validetta-inline',
        onValid: function (event) {
            promiseDetailModal(event);
        }
    });
}

/* Show venue map */
function showMap() {
    const modal = $("#venue-map-modal");
    const map = $("#map");
    const body = $("#venue-map-modal .modal-body");
    const errorDiv = $("#apiLoadError");
    const preloader = $("#venue-map-modal .modal-body .page-loader-wrapper");

    function setError(error) {
        body.css('height', '50px');
        errorDiv.removeClass('hiddeninfo');
        errorDiv.text(error);
        preloader.hide();
    }

    $("#showlocation").click(() => {

        const eventVenue = $("#eventVenue").val();
        const eventVenueOther = $("#eventVenueOther").val();
        if (eventVenue == "") return;
        if (eventVenue == 0 && eventVenueOther == "") return;
        const address = eventVenue == 0 ? eventVenueOther : $("#eventVenue :selected").data('subtext');

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
            setError("Google api was error.");
        });
    });

    //Fix modal-open issue padding
    modal.on('hidden.bs.modal shown.bs.modal', function () {
        $('body').addClass('modal-open');
    });
}

/* Get event attendee link */
function getAttendees(id) {
    const attendees = list_attendees.get(id);
    if (attendees != undefined) {
        return new Promise(resolve => resolve(attendees));
    } else {
        return ajax({
            url: "GetEventAttendeesAjax",
            type: 'post',
            data: {eventID: id},
            dataType: "json"
        }).then(result => {
            if (!result.hasOwnProperty('error')) {
                list_attendees.set(id, result);
            } else {
                if (result.status == "No attendees") {
                    list_attendees.set(id, result.status);
                }
            }
            return result;
        }).catch(error => {
            return error;
        });
    }
}

/* Trigger all day checkbox */
$('#allDay').click(function () {
    const $eventDate = $("#eventDate");
    const date = $eventDate.val().trim().split(" - ");
    datepicker();
    if ($(this).is(':checked')) {
        $eventDate.data('daterangepicker').setStartDate(date[0].split(", ")[1]);
        $eventDate.data('daterangepicker').setEndDate(date[1].split(", ")[1]);
    } else {
        $eventDate.data('daterangepicker').setStartDate(moment().format('HH:mm') + ", " + date[0]);
        $eventDate.data('daterangepicker').setEndDate(moment().format('HH:mm') + ", " + date[1]);
    }
    formatCarSelect();
});

/* Trigger car reservation checkbox */
$('#useCar').click(function () {
    const $eventCar = $("#eventCar");
    $eventCar.prop('disabled', !$(this).is(':checked'));
    $eventCar.selectpicker('refresh');
    $eventCar.parent("div").removeClass("validetta-error");
    $eventCar.siblings("span.validetta-inline").remove();
});

/* Venue select box change */
function init_venueChange() {
    $('#eventVenue').on('changed.bs.select', function () {
        const val = $(this).val();
        const other = $('#eventVenueOtherDiv');
        if (val == 0) {
            other.slideDown();
        } else {
            other.slideUp();
        }
    });
}

/* Attendee select-box */
function formatAttendeeSelect() {
    const departments = new Map();
    const attendeeSelect = $("#eventAttendees");
    const currentDep = $('#profile_info_dep').text();

    attendeeSelect.find('option').each(function () {
        const gender = $(this).data('gender') == true ? "Mr " : "Ms ";
        $(this).text(gender + $(this).text());

        const department = $(this).data('department');
        const optgroup = departments.has(department)
            ? departments.get(department)
            : $('<optgroup label="' + department + '"></optgroup>');
        optgroup.append($(this));
        departments.set(department, optgroup);

    });
    attendeeSelect.empty();

    // Set current user department to first position and others
    departments.forEach(function (value, key) {
        if (key == currentDep) attendeeSelect.prepend(value);
        else attendeeSelect.append(value);
    });

    attendeeSelect.selectpicker('refresh');
}

/* Car select-box */
function formatCarSelect() {
    function checkRangeTimeReserved(start, end, range) {
        if (start < range.start && end < range.start) return true;
        return start > range.end && end > range.end;

    }

    const selected = $("#eventDate").val().split(" - ");
    let start, end;
    if (($("#allDay").is(':checked'))) {
        start = moment(selected[0], 'MMMM DD YYYY').startOf('day').valueOf();
        end = moment(selected[1], 'MMMM DD YYYY').endOf('day').valueOf();
    } else {
        start = moment(selected[0], 'HH:mm, MMMM DD YYYY').valueOf();
        end = moment(selected[1], 'HH:mm, MMMM DD YYYY').valueOf();
    }

    // Disable car which reservated
    const eventCar = $("#eventCar");
    eventCar.find('option').each(function () {
        $(this).prop('disabled', false);
        $(this).prop('selected', false);
        const reservation = list_carreservation.get($(this).val());
        if (reservation != undefined) {
            for (let i = 0; i < reservation.length; i++) {
                if (exceptEventID == reservation[i].eventID) continue;
                if (!checkRangeTimeReserved(start, end, reservation[i])) {
                    $(this).prop('disabled', true);
                    break;
                }
            }
        }
    });
    eventCar.selectpicker('refresh');
}

/* Send email to attendees */
function sendEmail(emailList, eventTitle, type) {
    return ajax({
        url: "SendEventAttendeesAjax",
        type: 'post',
        data: {emailList: emailList, eventTitle: eventTitle, type: type},
    }).then(result => {
        const success = "Send email to attendees successfully";
        const error = "Send email to attendees failed. There was some errors";

        if (result.includes("done")) {
            showSuccessNotification("COMPLETED", success);
        } else {
            showErrorNotification("ERROR", error);
        }
    }).catch(error => {
        console.log(error);
    });
}

/* Set event attendees */
function setAttendees(attendees, eventID) {
    return ajax({
        url: "SetEventAttendeesAjax",
        type: 'post',
        data: {attendees: attendees, eventID: eventID},
    }).then(result => {
        const success = "Set event attendees successfully";
        const errordata = "Set event attendees failed. Input data error";
        const errorsystem = "Set event attendees failed. System error";

        if (result.error) {
            switch (result.status) {
                case "errordata":
                    showErrorNotification("ERROR", errordata);
                    break;
                case "errorsystem":
                    showErrorNotification("ERROR", errorsystem);
                    break;
            }
        } else {
            showSuccessNotification("COMPLETED", success);
        }
    }).catch(error => {
        console.log(error);
    });
}

/* Delete event */
$("#event-delete").on('click', function () {
    const $modal = $("#events-modal");
    const eventID = $("#modal-id").text();
    const msg = "Do you want to delete this event ?";
    const successMsg = "Deleted successfully";
    const errorMsg = "There were some errors. Please try again.";

    const promiseConfirm = function () {
        return ajax({
            url: "DeleteEventAjax",
            type: 'post',
            data: {eventID: eventID, eventType: EVENT_TYPE.CUSTOMER_CARE}
        }).then(result => {
            return function () {
                // Session timeout
                checkSessionTimeout(result);

                if (result == "done") {
                    init_calendarEventSource().then(() => {
                        $('#department').trigger('changed.bs.select');
                    });

                    init_carResercation();

                    showDialogMessage("DONE", successMsg, "success");
                    $modal.modal('hide');
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

/* Setup detail modal info */
function setModalInfo() {
    if (firstLoad) {
        $("#eventDetailsForm").trigger("reset");

        const $eventDate = $("#eventDate");
        datepicker();
        $eventDate.data('daterangepicker').setStartDate(new Date($.now()));
        $eventDate.data('daterangepicker').setEndDate(new Date($.now()));

        $("#eventAttendees").selectpicker('refresh');
        $("#eventVenue").selectpicker('refresh');

        const $eventCar = $("#eventCar");
        $eventCar.prop('disabled', true);
        $eventCar.selectpicker('refresh');

        const content_template = '<ul><li><span style="font-size: 14px;"><b>Customer:</b>&nbsp;</span></li><li><span style="font-size: 14px;"><b>Issue:</b>&nbsp;</span></li><li><span style="font-size: 14px;"><b>Request:</b>&nbsp;</span></li><li><span style="font-size: 14px;"><b>Content:</b>&nbsp;</span></li><li style="margin: 0; padding: 0; border: 0; font-style: inherit; font-variant: inherit; font-stretch: inherit; font-size: inherit; line-height: inherit; font-family: inherit;"><span style="font-size: 14px;"><b>Action plan:</b>&nbsp;</span></li></ul>';
        $('#texteditor').summernote('code', content_template);

        firstLoad = false;
    }
    formatCarSelect();
}

/* Add modal */
$('.add-new-btn span').click(function () {
    exceptEventID = 0;
    setModalInfo();
    $('#event-details-modal .modal-title').text('ADD EVENT');
    $('#event-details-modal').modal('show');
});

/* Set event to modal for update */
$("#event-update").on('click', function () {
    firstLoad = true;
    const id = $("#modal-id").text();
    $("#eventID").text(id);
    exceptEventID = id;

    const title = $("#modal-title").text();
    $("#eventTitle").val(title);

    const $eventDate = $("#eventDate");
    const start = $("#modal-startdate").text();
    const end = $("#modal-enddate").text();
    const date = formatDateTimeRangeInput(start, end);
    $("#allDay").prop('checked', date.allDay);
    datepicker();
    $eventDate.data('daterangepicker').setStartDate(date.start);
    $eventDate.data('daterangepicker').setEndDate(date.end);

    const $eventVenue = $("#eventVenue");
    const customer = $("#modal-customer").text();
    const customerOther = $(".modal-customer-other").text();
    if (customer != '' && customerOther == '') {
        $eventVenue.selectpicker('val', customer);
        $eventVenue.trigger("changed.bs.select");
    } else {
        $eventVenue.selectpicker('val', 0);
        $eventVenue.trigger("changed.bs.select");
        $("#eventVenueOther").val(customerOther);
    }

    const $eventAttendees = $("#eventAttendees");
    getAttendees(id).then(attendees => {
        $eventAttendees.selectpicker('val', '');
        $eventAttendees.selectpicker('val', attendees.map(emp => emp.account.accountID));
    });

    const $usecar = $("#useCar");
    const eventCar = $("#eventCar");
    const car = $(".modal-car").text();
    const usecar = car != '';
    $usecar.prop('checked', usecar);
    eventCar.prop('disabled', !usecar);
    formatCarSelect();
    eventCar.selectpicker('val', car);

    const content = $(".eventcontent div").html();
    $("#texteditor").summernote('code', content == undefined ? '' : content);

    const $modaldetail = $("#event-details-modal");
    const $modalevent = $("#events-modal");
    $modaldetail.find('.modal-title').text('UPDATE EVENT');
    $modalevent.modal('hide');
    $modaldetail.modal('show');

    //Fix modal-open issue padding
    $modalevent.on('hidden.bs.modal', function () {
        $('body').addClass('modal-open');
    });
});

/* Handle promise operation detail modal */
function promiseDetailModal(e) {
    e.preventDefault();
    const isAdd = $('#event-details-modal .modal-title').text() == "ADD EVENT";
    const eventID = $("#eventID").text().trim();
    const data = $("#eventDetailsForm").serialize() + '&eventID=' + eventID;
    const $button = $("#detailsModalAction");

    const attendees = $("#eventAttendees :selected");
    const isSendMail = $("#sendMail").is(':checked');
    const eventTitle = $("#eventTitle").val();
    const emailList = attendees.map(function () {
        return $(this).data('email');
    }).get();
    const idList = attendees.map(function () {
        return $(this).val();
    }).get();

    const $eventCar = $("#eventCar");
    const car = $eventCar.is(':disabled') ? '' : $eventCar.val();

    $button.button('loading');

    ajax({
        url: isAdd ? "AddEventCustomerCareAjax" : "UpdateEventCustomerCareAjax",
        type: 'post',
        data: data,
        dataType: "json"
    }).then(result => {
        const success = isAdd ? "Added event successfully." : "Update event successfully";
        const errordata = "Failed. Input data errors. Please check and try again.";
        const errorsystem = "Failed. System errors. Please refresh and try again.";
        const errordate = "Start date must be before end date. Please try again.";

        if (result.hasOwnProperty("error") && result.error) {
            switch (result.status) {
                case "errordata":
                    showErrorNotification("ERROR", errordata);
                    break;
                case "errorsystem":
                    showErrorNotification("ERROR", errorsystem);
                    break;
                case "errordate":
                    showErrorNotification("ERROR", errordate);
                    break;
            }
            throw 'error';
        } else {
            init_calendarEventSource().then(() => {
                $('#department').trigger('changed.bs.select');
            });

            init_carResercation().then(() => {
                //Refresh modal detail info
                if (isAdd) {
                    firstLoad = true;
                    setModalInfo();
                } else {
                    $("#eventCar").selectpicker('val', car);
                    list_attendees.delete(eventID);
                }
            });

            showSuccessNotification("COMPLETED", success);
            return result;
        }
    }).then(event => {
        if (event != undefined && event.hasOwnProperty('eventID')) {
            return setAttendees(idList, event.eventID);
        }
    }).then(() => {
        if (isSendMail) {
            $button.data('loading-text', "<i class='fa fa-spinner fa-spin'></i>  Sending");
            $button.button('loading');
            return sendEmail(emailList, eventTitle, isAdd ? SEND_MAIL_TYPE.NEW_CUSTOMER_CARE : SEND_MAIL_TYPE.UPDATE_CUSTOMER_CARE);
        }
    }).then(() => {
        $button.button('reset');
        $button.data('loading-text', "<i class='fa fa-spinner fa-spin'></i>  Processing");
    }).catch(error => {
        $button.button('reset');
        console.log(error);
        checkSessionTimeout(error);
    });
}

/* Main */
$(function () {
    init_venueChange();
    init_departmentColor();
    init_carResercation();
    init_calendar();
    init_texteditor();
    init_ModalValidate();
    formatAttendeeSelect();
    $('.selectpicker').selectpicker();
    setModalInfo();
    showMap();
    hideWrapperSpinner();
});




