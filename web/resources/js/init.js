(function ($) {
    $(function () {

        $('.sidenav').sidenav();
        $('.parallax').parallax();
        $('.dropdown-trigger').dropdown();

    }); // end of document ready
})(jQuery); // end of jQuery name space


$(document).ready(function () {
    $('select').formSelect();
});


function filter() {
    var isVisible = $("#filter").is(":visible");
    if (isVisible) {
        $("#filter").hide(500);
    } else {
        $("#filter").show(500);
    }

    console.log("filter function");
}