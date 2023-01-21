function tabs() {
    let radios = document.querySelectorAll('input[name="css-tabs"]');
    for (let i = 0; i < radios.length; i++) {
        radios[i].addEventListener("change", colors);
        if(radios[i].checked){
            radios[i].dispatchEvent(new CustomEvent("change"));
        }
    }
}

function colors(){
    if (this.checked) {
        let color = this.value;
        document.getElementById("tabs").style.background = color;
        let labels = document.getElementsByClassName("tab-label");
        for (let j = 0; j < labels.length; j++) {
            if (labels[j].previousElementSibling !== this) {
                labels[j].style.background = color;
                labels[j].style.borderRight = color;
            }else{
                labels[j].style.background = "#fff";
                labels[j].style.borderRight = "#fff";
            }
        }
    }
}