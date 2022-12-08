function seasonIcon(){
    const date = new Date();
    const month = date.getMonth();

    let icon = document.getElementById('icon');
    icon.setAttribute("height", "48px");
    if(month === 9){
        icon.src = h[Math.floor(Math.random() * h.length)];
    }else if(month === 11 || month === 0){
        icon.src = c[Math.floor(Math.random() * c.length)];
    }else{
        icon.src = u[Math.floor(Math.random() * u.length)];
    }
}

const h = [
    "https://img.icons8.com/color/48/null/werewolf.png",
    "https://img.icons8.com/color/48/null/jason-voorhees.png",
    "https://img.icons8.com/color/48/null/cute-pumpkin.png"
];

const c = [
    "https://img.icons8.com/color/48/null/santas-hat.png",
    "https://img.icons8.com/color/48/null/grinch.png"
];

const u = [
    "https://img.icons8.com/color/48/null/ukraine.png"
];