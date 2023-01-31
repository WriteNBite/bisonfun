function enableOptions() {
    let contentChecks = document.querySelectorAll('input[name="content-type"]');
    for (let i = 0; i < contentChecks.length; i++) {
        contentChecks[i].addEventListener("change", enableType);
    }
    let settings = document.getElementsByClassName("setting");
    for(let i = 0; i < settings.length; i++){
        settings[i].lastElementChild.checked = true;
        settings[i].lastElementChild.addEventListener("change", settingTree);
    }
    buttonCheck();
}

function enableType() {
    let checks = this.nextElementSibling;
    if(this.checked){
        let settings = checks.getElementsByTagName("input");
        for(let i = 0; i < settings.length; i++){
            settings[i].checked = true;
        }
    }
    if(checks.style.display === "block" && !this.checked){
        checks.style.display = "none";
    }else{
        checks.style.display = "block";
    }
    buttonCheck();
}

function settingTree() {
    let parent = this.parentElement.parentElement;
    let settings = parent.getElementsByTagName("input");
    let someChecked = false;
    for(let i = 0; i < settings.length; i++){
        someChecked = someChecked || settings[i].checked;
    }
    parent.previousElementSibling.checked = someChecked;
    buttonCheck();
}

function buttonCheck(){
    let anime = document.getElementById("anime");
    let movie = document.getElementById("movie");
    let tv = document.getElementById("tv");
    let button = document.getElementById("personal-generate");
    button.disabled = !(anime.checked || movie.checked || tv.checked);
}

function wtwscript(){
    let types = document.getElementsByClassName("content-type");
    let returnString = "?";
    returnString+="username="+document.getElementById("username").value+"&";
    for(let i = 0; i < types.length; i++){
        if(types[i].checked){
            returnString+=types[i].id+"=";
            let options = types[i].nextElementSibling.querySelectorAll(".setting input:checked, .setting select");
            for(let j = 0; j < options.length; j++){
                if(options[j].checked){
                    returnString+=options[j].value;
                    if(j+1 === options.length){
                        returnString+="&";
                    }else{
                        returnString+=","
                    }
                }
            }
        }
    }
    getJSON(returnString);
}

function getJSON(query){
    $.get( "/random/video"+query, function( data ) {
        if(!data) {
            alert("There is nothing suitable for such a request");
        }else{
            readJSON(data);
        }
    });
}

function readJSON(json) {
    let jsObj;
    try {
        jsObj = JSON.parse(json);
    }catch (er){
        jsObj = json;
    }
    console.log(json);
    console.log(jsObj);
    let content = document.getElementById("content");
    content.getElementsByTagName("img")[0].src = jsObj.poster;
    content.getElementsByClassName("alert-link")[0].textContent = jsObj.title;
    let contentClass;
    if(jsObj.anime){
        contentClass = "anime"
    }else{
        contentClass = jsObj.type.toLowerCase();
    }
    content.getElementsByClassName("alert-link")[0].href = "/"+contentClass+"/"+jsObj.id;
    content.getElementsByClassName("type")[0].firstElementChild.textContent = camelizeText(jsObj.type);
    content.getElementsByClassName("type")[0].lastElementChild.textContent = jsObj.releaseYear;

    //alert color
    let color;
    if(contentClass === "movie"){
        color = "alert-danger";
    }else if(contentClass === "tv"){
        color = "alert-success";
    }else if(contentClass === "anime"){
        color = "alert-primary";
    }else{
        color = "alert-secondary";
    }
    content.firstElementChild.className = "alert mx-auto "+ color;
}
function camelizeText(str) {
    if(str === "TV"){
        return "TV"
    }
    return str.replace(/(?:^\w|[A-Z]|\b\w)/g, function(word, index) {
        return index === 0 ? word.toUpperCase() : word.toLowerCase();
    }).replace(/\s+/g, '');
}

const commonJson = "{\n" +
    "    \"id\": null,\n" +
    "    \"type\": \"Type\",\n" +
    "    \"title\": \"Title\",\n" +
    "    \"description\": null,\n" +
    "    \"runtime\": 0,\n" +
    "    \"releaseDate\": \"Year\",\n" +
    "    \"poster\": \"https://img.freepik.com/free-vector/flat-design-no-photo-sign-design_23-2149289006.jpg\",\n" +
    "    \"score\": 0,\n" +
    "    \"genres\": null,\n" +
    "    \"status\": null,\n" +
    "    \"releaseYear\": \"Year\",\n" +
    "    \"anime\": false,\n" +
    "    \"timeToWatch\": \"\"\n" +
    "}";