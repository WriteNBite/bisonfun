function timer(sec){
    let timer = setInterval(function(){
        let time_sec = ("0"+sec).slice(-2);
        document.getElementById('timer').innerHTML='00:'+time_sec;
        sec--;
        if (sec < 0) {
            clearInterval(timer);
        }
    }, 1000);
}