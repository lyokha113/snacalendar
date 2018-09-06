function ajax(options) {
    return new Promise(function (resolve, reject) {
        $.ajax(options).done(resolve).fail(reject);
    });
}

function recoverypass(msg) {
    let confirm = function (password) {
        let changeResult = ajax({
            url: "RecoverAccountPasswordAjax",
            type: 'post',
            data: {token: msg, password: password}
        }).then(result => {
            return function () {
                if (result === "done") {
                    showDialogMessage("CHANGE PASSWORD", "Password changed successfully", "success");
                } else if (result === "error") {
                    showDialogMessageWithPromise("Password change request was executed or there is system error. Please try again.", "CHANGE PASSWORD", "error", promiseCancel);
                }
            };
        }).catch(error => {
            return error;
        });
        return changeResult;
    };

    let cancel = function () {
        showDialogMessage("CANCELED", "Action canceled. Password wasn't changed.", "warning");
    };

    showDialogMessageRecoverPass(confirm, cancel);
}

$(function () {
    $('.page-loader-wrapper').hide();
    $('#login, #recovery').validetta({
        realTime: true,
        bubblePosition: 'bottom',
        bubbleGapTop: 8,
        bubbleGapLeft: 0,
        onValid: function () {
            $('.page-loader-wrapper').fadeIn(300);
        }
    });
    loadStatus();
});