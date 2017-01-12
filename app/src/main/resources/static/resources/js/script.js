$(document).ready(function(){

    // click sidebar toggle
    $('#sidebarToggle').on('click', function(e){
        e.preventDefault();
        $('body').toggleClass('mini');
    });

    // click table show userCards
    $('#cardBtn').on('click', function(e){
       e.preventDefault();
        if($(this).hasClass('active')) return false;
        $('.icon__wrap button').toggleClass('active');
        $('.container__table').slideUp('slow');
        $('.container__cards').slideDown('slow');
    });

    //  click table show list
    $('#listBtn').on('click', function(e){
        e.preventDefault();
        if($(this).hasClass('active')) return false;
        $('.icon__wrap button').toggleClass('active');
        $('.container__cards').slideUp('slow');
        $('.container__table').slideDown('slow');
    });

    // menu active status
    var patharray = window.location.pathname.split('/');
    for (var i = 1; i < patharray.length; i++ ){
        $('.col-md-3 .list-group .'+patharray[i]).addClass('active');

    }
});