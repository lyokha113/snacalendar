if (typeof jQuery === "undefined") {
    throw new Error("jQuery plugins need to be before this file");
}

/* Custom js - Function ======================================================================================================
 *  Some custom functions
 *  
 */

let calendar = null;
let events_source = null;

/* Date picker */
function datepicker() {
    if (typeof ($.fn.daterangepicker) === 'undefined') {
        return;
    }

    const allDay = $("#allDay").is(':checked');

    const options = {
        parentEl: '#eventDateParent',
        opens: "center",

        isInvalidDate: function (date) {
            return moment(date).weekday() == 0 || moment(date).weekday() == 6;
        },
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
    $('#eventDate').daterangepicker(options);
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
                let sDate = holiday.holidayStartDate.split("-")[2] + "-" + holiday.holidayStartDate.split("-")[1];
                let eDate = holiday.holidayEndDate.split("-")[2] + "-" + holiday.holidayEndDate.split("-")[1];
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
        data: {eventType: EVENT_TYPE.ANNUAL_LEAVE},
        dataType: "json"
    }).then(result => {
        const events = [];
        if (!result.hasOwnProperty('error')) {
            result.forEach(event => {
                switch (event.annualLeave) {
                    case ANNUAL_LEAVE_STATUS.APPROVING:
                        event.class = "event-warning";
                        break;
                    case ANNUAL_LEAVE_STATUS.APPROVED:
                        event.class = "event-success";
                        break;
                    case ANNUAL_LEAVE_STATUS.DENIED:
                        event.class = "event-important";
                        break;
                }
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
            $(this).siblings('button').removeClass('active');
            $(this).addClass('active');
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
    }).then((cal) => {
        calendar = init_calendarView(cal);
        calendar = init_calendarNav(cal);
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
        },
        onError: function (event) {
            console.log(event);
        }
    });
}

/* Trigger all day checkbox */
$('#allDay').click(function () {
    const $eventDate = $("#eventDate");
    let date = $eventDate.val().trim().split(" - ");
    datepicker();
    if ($(this).is(':checked')) {
        $eventDate.data('daterangepicker').setStartDate(date[0].split(", ")[1]);
        $eventDate.data('daterangepicker').setEndDate(date[1].split(", ")[1]);
    } else {
        $eventDate.data('daterangepicker').setStartDate(moment().format('HH:mm') + ", " + date[0]);
        $eventDate.data('daterangepicker').setEndDate(moment().format('HH:mm') + ", " + date[1]);
    }
});

/* Delete event */
$("#event-delete").on('click', function () {
    const $modal = $("#events-modal");
    const eventID = $("#modal-id").text().trim();
    const msg = "Do you want to delete this event ?";
    const successMsg = "Deleted successfully";
    const errorMsg = "There were some errors. Please try again.";

    const promiseConfirm = function () {
        return ajax({
            url: "DeleteEventAjax",
            type: 'post',
            data: {eventID: eventID, eventType: EVENT_TYPE.ANNUAL_LEAVE}
        }).then(result => {
            return function () {
                // Session timeout
                checkSessionTimeout(result);

                if (result == "done") {
                    init_calendarEventSource().then(() => {
                        calendar.options.events_source = events_source;
                        calendar.view();
                    });
                    if ($("#profile_info_permission").text().trim() == PERMISSION.SUPER_USER) getEmployeeTotalAnnualLeave();
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
    $("#eventDetailsForm").trigger("reset");

    const $eventDate = $("#eventDate");
    datepicker();
    $eventDate.data('daterangepicker').setStartDate(new Date($.now()));
    $eventDate.data('daterangepicker').setEndDate(new Date($.now()));
}

function sendEmail(eventTitle) {
    return ajax({
        url: "SendAnnualLeaveAjax",
        type: 'post',
        data: {eventTitle: eventTitle},
    }).then(result => {
        const success = "Send email to supervisor successfully";
        const error = "Send email to supervisor failed. There was some errors";
        const none = "You don't have any supervisor. Email won't be sended.";
        switch (result) {
            case "done":
                showSuccessNotification("COMPLETED", success);
                break;
            case "error":
                showErrorNotification("ERROR", error);
                break;
            case "none":
                showWarningNotification("WARNING", none);
                break;
            case "skip":
                break;
        }
    }).catch(error => {
        console.log(error);
    });
}

/* Add modal */
$('.add-new-btn span').click(function () {
    setModalInfo();
    $('#event-details-modal .modal-title').text('ADD EVENT');
    $('#event-details-modal').modal('show');
});

/* Set event to modal for update */
$("#event-update").on('click', function () {
    const id = $("#modal-id").text().trim();
    $("#eventID").text(id);

    const $eventDate = $("#eventDate");
    const start = $("#modal-startdate").text().trim();
    const end = $("#modal-enddate").text().trim();
    const date = formatDateTimeRangeInput(start, end);
    $("#allDay").prop('checked', date.allDay);
    datepicker();
    $eventDate.data('daterangepicker').setStartDate(date.start);
    $eventDate.data('daterangepicker').setEndDate(date.end);

    const reason = $("#annual_leave_reason").text().trim();
    $("#eventContent").val(reason);

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
    const isAdd = $('#event-details-modal .modal-title').text().trim() == "ADD EVENT";
    const eventID = $("#eventID").text().trim();
    const data = $("#eventDetailsForm").serialize() + '&eventID=' + eventID;
    const $button = $("#detailsModalAction");

    $button.button('loading');

    ajax({
        url: isAdd ? "AddEventAnnualLeaveAjax" : "UpdateEventAnnualLeaveAjax",
        type: 'post',
        data: data,
        dataType: "json"
    }).then(result => {
        const success = isAdd ? "Added event successfully." : "Update event successfully";
        const errordata = "Failed. Input data errors. Please check and try again.";
        const errorsystem = "Failed. System errors. Please refresh and try again.";
        const errordate = "Start date must be before end date. Please try again.";
        const errorholiday = "Day off should not start or end or during the holiday vacation.";

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
                case "errorholiday":
                    showWarningNotification("ERROR", errorholiday);
                    break;
            }
            throw 'error';
        } else {
            init_calendarEventSource().then(() => {
                calendar.options.events_source = events_source;
                calendar.view();
            });

            if ($("#profile_info_permission").text().trim() == PERMISSION.SUPER_USER) getEmployeeTotalAnnualLeave();

            //Refresh modal detail info
            if (isAdd) {
                setModalInfo();
            }
            showSuccessNotification("COMPLETED", success);
            return result;
        }
    }).then((result) => {
        if (result != undefined && result.hasOwnProperty('title') && isAdd) {
            $button.data('loading-text', "<i class='fa fa-spinner fa-spin'></i>  Sending");
            $button.button('loading');
            return sendEmail(result.title);
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

function getEmployeeTotalAnnualLeave() {
    ajax({
        url: "GetEmployeeAnnualLeaveAjax",
        type: 'post',
    }).then(result => {
        const $totaldays = $("#totaldays div");
        $totaldays.first().text("Total approved in " + (new Date()).getFullYear() + ":");
        $totaldays.last().text(result + " days");
    }).catch(error => {
        console.log(error);
    });
}

/* Main */
$(function () {
    init_calendar();
    init_ModalValidate();
    getEmployeeTotalAnnualLeave();
    $('.selectpicker').selectpicker();
    setModalInfo();
    hideWrapperSpinner();
});




