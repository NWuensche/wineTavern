$(document).ready(function(){

    // click sidebar toggle
    $('#sidebarToggle').on('click', function(e){
        e.preventDefault();
        $('body').toggleClass('mini');
    });

    // click table show userCards
    $('#cardBtn').on('click', function(e){
       e.preventDefault();
        $('table').slideUp('slow');
        $('.container__user-cards').slideDown('slow').removeAttr('display');
    });

    //  click table show list
    $('#listBtn').on('click', function(e){
        e.preventDefault();
        $('.container__user-cards').slideUp('slow').css('display', 'none');
        $('table').slideDown('slow');
    });
});