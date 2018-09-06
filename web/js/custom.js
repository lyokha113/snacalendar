if (typeof jQuery === "undefined") {
    throw new Error("jQuery plugins need to be before this file");
}

let isHomepage = false;
const EVENT_TYPE = {CUSTOMER_CARE: 1, TRAINING: 2,  BIZ_TRIP: 3, CAR_RESERVATION: 4, ANNUAL_LEAVE: 5};
const ANNUAL_LEAVE_STATUS = {APPROVING: 1, APPROVED: 2,  DENIED: 3};
const PERMISSION = {SUPER_USER: 1, USER: 2};
const SEND_MAIL_TYPE = {NEW_CUSTOMER_CARE: "new_customer_care", UPDATE_CUSTOMER_CARE: "update_customer_care",
    NEW_TRAINING: "new_training", UPDATE_TRAINING: "update_training"};
const HOLIDAY_VACATION = {OFF: true, WORKING: false};
const GOOGLE_MAP_API = "https://maps.googleapis.com/maps/api/js?key=AIzaSyCRu19SbIoinq7DV3GRXXcKwp2DFW1KtTs";
let apiLoaded = false;
let $BODY = $('body'), $MENU_TOGGLE = $('#menu_toggle'), CURRENT_PAGE = "",
        $SIDEBAR_MENU = $('#sidebar-menu'), $TOP_NAV_MENU_ITEMS = $('.top_nav_menu_items');

/* Set main content and active menu */
$SIDEBAR_MENU.find('a').on('click', function () {

    //Active menu highlight
    $SIDEBAR_MENU.find("li").removeClass("active active-sm");
    if ($BODY.hasClass('nav-md')) {
        $(this).parent().addClass("active");
    } else {
        $(this).parent().addClass("active-sm");
    }

    //Main content
    CURRENT_PAGE = $(this).data('page');
    isHomepage = CURRENT_PAGE == "homepage.jsp";

    if (CURRENT_PAGE == "") {
        $('#main-content').empty();
    } else {
        $('.page-loader-wrapper').fadeIn(200);
        $('#main-content').load(CURRENT_PAGE, () => {
            if ($(".page-title").length == 0) {
                location.href = "logout";
            }
        });
    }

});

/* Set top nav modal content */
$TOP_NAV_MENU_ITEMS.click(function () {

    let $profileModalTitle = $("#profileModalTitle");
    let $infodetail = $(".infodetail");
    let $passworddetail = $(".passworddetail");
    let $profileModal = $("#profileModal");
    let $profileCurrent = $("#profileCurrent");
    let $profilePass = $("#profilePass");
    let $profileConfirm = $("#profileConfirm");
    let $profileName = $("#profileName");
    let $profileAddress = $("#profileAddress");
    let $profilePhone = $("#profilePhone");
    let $profileDoB = $("#profileDoB");
    let $profileJD= $("#profileJD");

    $profileCurrent.prop("disabled", true);
    $profilePass.prop("disabled", true);
    $profileConfirm.prop("disabled", true);
    $profileName.prop("disabled", true);
    $profileAddress.prop("disabled", true);
    $profilePhone.prop("disabled", true);

    let action = $(this).data('page');
    switch (action) {
        case "userprofile":
            $profileModalTitle.text("USER PROFILE");
            $profileModal.modal('show');

            $infodetail.css('display', 'block');
            $passworddetail.css('display', 'none');

            $profileDoB.text(formatDate($profileDoB.text().trim()));
            $profileJD.text(formatDate($profileJD.text().trim()));
            $profileName.prop("disabled", false);
            $profileAddress.prop("disabled", false);
            $profilePhone.prop("disabled", false);
            break;
        case "changepassword":
            $profileModalTitle.text("CHANGE PASSWORD");
            $profileModal.modal('show');

            $infodetail.css('display', 'none');
            $passworddetail.css('display', 'block');

            $profileCurrent.val("");
            $profilePass.val("");
            $profileConfirm.val("");
            $profileCurrent.prop("disabled", false);
            $profilePass.prop("disabled", false);
            $profileConfirm.prop("disabled", false);
            break;
        case "logout":
            location.href = "logout";
            break;
        default:
            break;
    }
});

/* Menu scroll bar */
function initMenuScrollBar() {
    if ($.fn.mCustomScrollbar) {
        $('.menu_fixed').mCustomScrollbar({
            autoHideScrollbar: true,
            theme: 'minimal',
            mouseWheel: {preventDefault: true}
        });
    }
}

/* Toggle menu */
$MENU_TOGGLE.on('click', function () {
    if ($BODY.hasClass('nav-md')) {
        $SIDEBAR_MENU.find('li.active ul').hide();
        $SIDEBAR_MENU.find('li.active').addClass('active-sm').removeClass('active');
        $(".site_title").find("img").css("width", "58px");
    } else {
        $SIDEBAR_MENU.find('li.active-sm ul').show();
        $SIDEBAR_MENU.find('li.active-sm').addClass('active').removeClass('active-sm');
        $(".site_title").find("img").css("width", "80px");
    }

    $BODY.toggleClass('nav-md nav-sm');
});

/* Help modal */
function initHelpModal() {
    let help = '<i class="glyphicon glyphicon-sort"></i>&nbsp;&nbsp;<strong>ORDERING</strong><br>' +
            'By default, the list was sorting by ID<br>' +
            'Change sorting type by clicking on column title<br>' +
            '<br>' +
            '<i class="glyphicon glyphicon-search"></i>&nbsp;&nbsp;<strong>FILTERING</strong><br>' +
            'The list will be filtered by any word on filter input form<br>' +
            'To find exactly phrasal word, put them into quotation mark. For example: "Mr Abc"<br>' +
            'To filter by any date column, enter date with format "YYYY-MM-DD". For example: "2018-05-30"<br>' +
            'Lowercase and upper case are ignored. <br>' +
            '<br>';
    $("#helpModal .modal-body").html(help);
}

/* Hide wrapper spinner */
function hideWrapperSpinner() {
    setTimeout(function () {
        $('.page-loader-wrapper').fadeOut(200);
    }, 200);
}

/* Format date for display */
function formatDate(date) {
    return moment(new Date(date)).format('MMM DD, YYYY');
}

/* Format date for input */
function formatDateInput(date) {
    return moment(new Date(date)).format('MMMM DD YYYY');
}

/* Format datetime range for display */
function formatDateTimeRange(start, end) {
    start = moment(Number(start));
    end = moment(Number(end));

    if (start.hour() == 0 && start.minute() == 0 && start.second() == 0
    && end.hour() == 23 && end.minute() == 59 && end.second() == 59) {
        return start.dayOfYear() == end.dayOfYear()
            ? start.format('MMM DD, YYYY')
            : start.format('MMM DD, YYYY') + " - " + end.format('MMM DD, YYYY');
    } else {
        return start.dayOfYear() == end.dayOfYear()
            ? start.format('HH:mm') + " - " + end.format('HH:mm') + ", " + start.format('MMM DD, YYYY')
            : start.format('HH:mm, MMM DD, YYYY') + " - " + end.format('HH:mm, MMM DD, YYYY');
    }
}

/* Format datetime range for input */
function formatDateTimeRangeInput(start, end) {
    start = moment(Number(start));
    end = moment(Number(end));
    return (start.hour() == 0 && start.minute() == 0 && start.second() == 0
        && end.hour() == 23 && end.minute() == 59 && end.second() == 59)
        ? {allDay: true, start: start.format('MMMM DD YYYY'), end: end.format('MMMM DD YYYY')}
        : {allDay: false, start: start.format('HH:mm, MMMM DD YYYY'), end: end.format('HH:mm, MMMM DD YYYY')};
}

/* Define ajax promise */
function ajax(options) {
    return new Promise(function (resolve, reject) {
        $.ajax(options).done(resolve).fail(reject);
    });
}

function checkSessionTimeout(result) {
    if ((typeof result == 'string' && result.includes("DOCTYPE html"))
        || (Object(result) === result && result.responseText.includes("DOCTYPE html"))) {
        location.href = "logout";
    }
}

/* Handle promise operation profile modal */
function promiseProfileModal(event) {
    event.preventDefault();
    let isUpdate = $("#profileModalTitle").text().trim() == "USER PROFILE";
    let id = $("#profileID").text().trim();
    let data = $("#profileModalForm").serialize() + '&profileID=' + id;

    $("#profileModalAction").button('loading');

    ajax({
        url: isUpdate ? "UpdateUserProfileAjax" : "ChangeUserProfilePasswordAjax",
        type: 'post',
        data: data
    }).then(result => {
        let success = isUpdate ? "Updated user profile successfully." : "Password changed successfully";
        let errordata = "Failed. Input data errors. Please check and try again.";
        let errorsystem = "Failed. System errors. Please refresh and try again.";
        let wrongCurrentPass = "Failed. Current password is incorrect.";
        let phoneDuplicate = "Phone is already used. Try another one.";

        checkSessionTimeout(result);

        switch (result) {
            case "errordata":
                showErrorNotification("ERROR", errordata);
                break;
            case "errorsystem":
                showErrorNotification("ERROR", errorsystem);
                break;
            case "wrong_current_password":
                showWarningNotification("WARNING", wrongCurrentPass);
                break;
            case "phone_unique":
                showWarningNotification("WARNING", phoneDuplicate);
                break;
            default:
                showSuccessNotification("COMPLETED", success);
                if (isUpdate) {
                    $('#profile_info_name').text($("#profileName").val().trim());
                    if (CURRENT_PAGE != "") {
                        $('#main-content').load(CURRENT_PAGE);
                    }
                } else {
                    $("#profileCurrent").val("");
                    $("#profilePass").val("");
                    $("#profileConfirm").val("");
                }
                break;

        }
    }).then(() => {
        $("#profileModalAction").button('reset');
    }).catch(error => {
        console.log(error);
    });
}

/* Init profile modal validate */
function initProfileModalValidate() {
    $('#profileModalForm').validetta({
        realTime: true,
        display: 'inline',
        errorTemplateClass: 'validetta-inline',
        validators: {
            regExp: {
                profilePhone: {
                    pattern: /^\+?[0-9]+([\s\-][0-9]+){0,3}$/,
                    errorMessage: 'Must be correct phone formation.'
                }
            }
        },
        onValid: function (event) {
            promiseProfileModal(event);
        }
    });
}

/* Format side menu with permission authorized */
function formatSideMenu() {
    let isAdmin = $("#profile_info_admin").text().trim();
    let permission =$("#profile_info_permission").text().trim();

    if (isAdmin == 'true') $("#adminsidemenu").css('display', 'block');
    else $("#adminsidemenu").css('display', 'none');

    if (isAdmin == 'true' || permission == PERMISSION.SUPER_USER) $("#superusersidemenu").css('display', 'block');
    else $("#superusersidemenu").css('display', 'none');
}

/* Get Google API Script */
function getGoogleApiScript() {
    return new Promise(function (resolve, reject) {
        if (apiLoaded != true) {
            // Cached ajax
            jQuery.cachedScript = function (url, options) {
                options = $.extend(options || {}, {
                    dataType: "script",
                    cache: true,
                    url: url
                });
                return jQuery.ajax(options);
            };

            // Load api key
            $.cachedScript(GOOGLE_MAP_API)
                .done(() => {
                    apiLoaded = true;
                    resolve();
                })
                .fail(() => {
                    reject();
                });
        } else {
            resolve();
        }
    });
}

/* Main */
$(document).ready(function () {
    initMenuScrollBar();
    initHelpModal();
    initProfileModalValidate();
    formatSideMenu();
    hideWrapperSpinner();
});



