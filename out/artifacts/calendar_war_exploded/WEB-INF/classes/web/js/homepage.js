if (typeof jQuery === "undefined") {
    throw new Error("jQuery plugins need to be before this file");
}

/* Custom js - Function ======================================================================================================
 *  Some custom functions
 *  
 */

const CSS_COLOR_NAMES = ["green", "red", "purple", "teal", "blue", "gold", "deepink", "slategray", "firebrick", "orange", "aero", "sienna", "slategray", "tan", "silver"];
const list_attendees = new Map();
let loadtime = new Map();
let calendar = null;
let events_source = null;
let isPlaying = true;

/* Init load time pages */
function init_loadtime() {
    loadtime.set(EVENT_TYPE.CUSTOMER_CARE, 60 * 3); //60s 3m
    loadtime.set(EVENT_TYPE.CAR_RESERVATION, 30);
    loadtime.set(EVENT_TYPE.TRAINING, 30);
    loadtime.set(EVENT_TYPE.ANNUAL_LEAVE, 30);
    loadtime.set(EVENT_TYPE.BIZ_TRIP, 30);
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
function init_calendarEventSource(eventType) {
    return ajax({
        url: "GetEventAjax",
        type: 'post',
        data: {eventType: eventType},
        dataType: "json"
    }).then(result => {
        const events = [];
        if (!result.hasOwnProperty('error')) {
            switch (eventType) {
                case EVENT_TYPE.ANNUAL_LEAVE:
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
                    break;

                case EVENT_TYPE.BIZ_TRIP:
                case EVENT_TYPE.TRAINING:
                    result.forEach(event => {
                        event.class = "event-info";
                        events.push(event);
                    });
                    break;

                case EVENT_TYPE.CAR_RESERVATION:
                    result.forEach(event => {
                        event.class = event.type.eventTypeID == EVENT_TYPE.CAR_RESERVATION ? "event-info" : "event-important";
                        events.push(event);
                    });
                    break;

                case EVENT_TYPE.CUSTOMER_CARE:
                    result.forEach(event => {
                        event.class = "event-custom-" + CSS_COLOR_NAMES[event.creator.department.departmentID % CSS_COLOR_NAMES.length];
                        events.push(event);
                    });
                    break;
            }
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

/* Calendar view */
function init_calendarView() {
    $('.calendar-view-button button[data-calendar-view]').each(function () {
        $(this).click(function () {
            calendar.view($(this).data('calendar-view'));
            $(this).siblings('button').removeClass('active');
            $(this).addClass('active');
            if ($(this).data('calendar-view') == 'month') $(".cal-day-today").mouseenter().click();
        });
    });
}

/* Calendar */
function init_calendar() {
    return new Promise(resolve => {
        const options = init_calendarOptions();
        init_calendarHolidays()
            .then(holidays => {
                options.holidays = holidays;
                options.events_source = [{}];
                calendar = $('#calendar').calendar(options);
                init_calendarView();
                resolve();
            })
    });
}

$("#play-button").on('click', function () {
   isPlaying = true;
   $(this).css('border-left', '50px solid teal');
   $("#stop-button").css('background', 'gray');
});

$("#stop-button").on('click', function () {
    isPlaying = false;
    $(this).css('background', 'red');
    $("#play-button").css('border-left', '50px solid gray');
});

function autoChangeEventPage() {

    const pageTile = new Map();
    pageTile.set(EVENT_TYPE.ANNUAL_LEAVE, "ANNUAL LEAVE");
    pageTile.set(EVENT_TYPE.BIZ_TRIP, "BUSINESS TRIP");
    pageTile.set(EVENT_TYPE.TRAINING, "TRAINING");
    pageTile.set(EVENT_TYPE.CUSTOMER_CARE, "CUSTOMER CARE");
    pageTile.set(EVENT_TYPE.CAR_RESERVATION, "CAR RESERVATION");

    const waitFor = (ms) => new Promise(r => setTimeout(r, ms));
    const asyncForEach = async (list, callback) => {
        for (let [key, value] of list) {
            await callback(key, value);
        }
    };

    const change = async () => {
        while (isHomepage) {
            await asyncForEach(loadtime, async (key, value) => {
                if (!isHomepage) return;
                init_calendarEventSource(key).then(() => {
                    calendar.options.events_source = events_source;
                    calendar.view();
                    $(".title_left h3").text(pageTile.get(key));
                    $(".cal-day-today").mouseenter().click();
                    $("#timeout-changepage").text("Timeout " + value + "s");
                });

                let time = 1;
                while(time <= value && isHomepage) {
                    await waitFor(1000);
                    if (isPlaying) {
                        $("#timeout-changepage").text("Timeout " + (value - time) + "s");
                        time++;
                    }
                }
            })
        }

    };

    change();

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

/* Main */
$(function () {
    init_loadtime();
    hideWrapperSpinner();
    init_calendar().then(() => autoChangeEventPage());
});




