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
        $('.container__user-cards').slideDown('slow');
    });

    //  click table show list
    $('#listBtn').on('click', function(e){
        e.preventDefault();
        if($(this).hasClass('active')) return false;
        $('.icon__wrap button').toggleClass('active');
        $('.container__user-cards').slideUp('slow');
        $('.container__table').slideDown('slow');
    });

    //
    // $('.user-card').hover(function(){
    //     $(this).css('backgroundColor','#000');
    // });
});