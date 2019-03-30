let url = "sendSoap"; //default

function setUrl(value) {
    url = value;
}

function send() {
    $('#result').html("Sending ...");
    let users = $("input:checkbox:checked").map(function () {
        return this.value;
    }).get();

    if (users.length === 0) {
        $('#result').html("Addresses are not selected");
        return;
    }

    // https://stackoverflow.com/a/5976031/548473
    let data = new FormData();
    data.append('users', users);
    data.append('subject', $("#subject").val());
    data.append('body', $("#body").val());
    let attach = $('#attach')[0].files;

    if (attach) $.each(attach, (k, v) => data.append("attachment" + k, v));


//        https://stackoverflow.com/a/22213543/548473
    $.post({
        url: url,
        data: data,
        contentType: false,
        processData: false
    }).done(function (result) {
        if (typeof result === "object") {
            result = JSON.stringify(result)
        }
        $('#result').html(result);
    }).fail(function (result) {
        $('#result').html(result.responseText);
    });
}