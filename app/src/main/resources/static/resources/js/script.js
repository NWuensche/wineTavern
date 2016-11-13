var timer = setInterval(clock,30000);

function checkTime(i) {
    if (i < 10) {i = "0" + i};  // add zero in front of numbers < 10
    return i;
}

function clock(){
    now = new Date();
    datetime = document.getElementById("clock").innerHTML;
    document.getElementById("clock").innerHTML=(datetime.substr(0,datetime.indexOf(' '))
    + " " + checkTime(now.getHours()+offsetHours) + ":" + checkTime(now.getMinutes()+offsetMinutes));
}