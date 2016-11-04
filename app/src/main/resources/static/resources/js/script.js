/**
 * Created by SEV on 03.11.2016.
 */

$(function(){
    $('[data-toggle="tooltip"]').tooltip();
});

function login(username){
    document.getElementById("name").value=username;
    document.getElementById("title").innerHTML=username;
}
var hidden = false;
function change(){
    if(hidden){
        document.getElementsByClassName("hide")[0].parentElement.style.left = "0px";
        document.getElementsByClassName("hide")[0].innerHTML =
            '<span class="fa fa-caret-square-o-left"></span>';
        document.getElementsByTagName("body")[0].style.paddingLeft = "430px";
        document.getElementsByClassName("navigation-top")[0].style.left = "400px";
        hidden = false;
    } else {
        document.getElementsByClassName("hide")[0].parentElement.style.left = "-380px";
        document.getElementsByClassName("hide")[0].innerHTML = '<span class="fa fa-caret-square-o-right"></span>';
        document.getElementsByTagName("body")[0].style.paddingLeft = "50px";
        document.getElementsByClassName("navigation-top")[0].style.left = "20px";
        hidden = true;
    }
}