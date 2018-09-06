function showDialogMessage(msg, title, icon) {
    swal(msg, title, icon);
}

function showDialogMessageWithPromise(msg, title, icon, promise) {
    swal({
        title: title,
        text: msg,
        type: icon
    }).then(() => {
        promise();
    });
}

function showDialogMessageConfirm(msg, promiseConfirm) {
    swal({
        title: 'CONFIRMATION',
        text: msg,
        type: 'warning',
        showCancelButton: true,
        showLoaderOnConfirm: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Accept',
        allowOutsideClick: false,
        preConfirm: () => {
            return promiseConfirm();
        }
    }).then(result => {
        if (!result.dismiss) {
            result.value();
        }
    });
}

function showDialogMessageChangePass(promiseConfirm, promiseCancel) {
    swal({
        title: 'CHANGE PASSWORD',
        text: "Do you want to change password of this account ?",
        input: 'password',
        inputPlaceholder: "Enter new password",
        showLoaderOnConfirm: true,
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Accept',
        allowOutsideClick: false,
        inputValidator: (password) => {
            if (password.length < 6)
                return "Must be at least 6 characters";
        },
        preConfirm: (password) => {
            return promiseConfirm(password);
        }
    }).then(result => {
        if (result.dismiss) {
            promiseCancel();
        } else {
            result.value();
        }
    }).catch(() => {
        showDialogMessageWithPromise("Failed. System errors", "ERROR", "error", promiseCancel);
    });
}

function showDialogMessageRecoverPass(promiseConfirm, promiseCancel) {
    swal({
        title: 'RECOVER PASSWORD',
        input: 'password',
        inputPlaceholder: "Enter new password",
        showLoaderOnConfirm: true,
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Accept',
        allowOutsideClick: false,
        inputValidator: (password) => {
            if (password.length < 6)
                return "Must be at least 6 characters";
        },
        preConfirm: (password) => {
            return promiseConfirm(password);
        }
    }).then(result => {
        if (result.dismiss) {
            promiseCancel();
        } else {
            result.value();
        }
    }).catch(() => {
        showDialogMessageWithPromise("Failed. System errors", "ERROR", "error", promiseCancel);
    });
}

function showDialogMessageChangeAnnualLeaveLimit(promiseConfirm) {
    swal({
        title: 'ANNUAL LEAVE',
        text: "Change annual leave day limit day of this employee.",
        input: 'number',
        inputPlaceholder: "Enter limit day",
        showLoaderOnConfirm: true,
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Accept',
        allowOutsideClick: false,
        inputValidator: (day) => {
            if (!Number.isInteger(parseInt(day)) || day < 0)
                return "Must be a number and can't lower than 0";
        },
        preConfirm: (day) => {
            return promiseConfirm(day);
        }
    }).then(result => {
        if (!result.dismiss) {
            result.value();
        }
    });
}
