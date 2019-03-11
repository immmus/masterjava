function send() {
    $('#result').html("Sending ...");
    let users = $("input:checkbox:checked").map(function () {
        return this.value;
    }).get();
    let formData = new FormData();
    let files = $('#attachment')[0].files;
    let subject = $('#subject').val();
    let body = $('#body').val();
    if (files) {
        $.each(files, (k, v) => {
            formData.append("attachment" + k, v)
        });
    }
    formData.append("users", users);
    formData.append("body", body);
    formData.append("subject", subject);
//        https://stackoverflow.com/a/22213543/548473
    $.post({
            url: 'send',
            data: formData,
            contentType: false,
            processData: false
        }
    ).done(function (result) {
        $('#result').html(result);
    }).fail(function (result) {
        $('#result').html(result);
    });
}