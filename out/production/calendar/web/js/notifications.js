function showNotification(title, text, type) {
    new PNotify({
        title: title,
        text: text,
        type: type,
        styling: 'bootstrap3',
        icons: "fontawesome5"
    });
}

function showWarningNotification(title, text) {
    showNotification(title, text, 'notice');
}

function showSuccessNotification(title, text) {
    showNotification(title, text, 'success');
}

function showErrorNotification(title, text) {
    showNotification(title, text, 'error');
}

function showInfoNotification(title, text) {
    showNotification(title, text, 'info');
}