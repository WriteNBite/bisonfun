function loginBg(){
    let bg = document.getElementsByClassName("bg-image").item(0);
    let index = Math.floor(Math.random()*bgs.length);
    bg.style.backgroundImage = "url("+bgs[index]+")";
}

const bgs = [
    "https://i.ibb.co/7V21zXk/girl-g46b2f7714-1920.jpg",
    "https://i.ibb.co/HKXpYfc/landscape-g4c354f54a-1920.jpg",
    "https://i.ibb.co/FbfPyzX/woman-g27c25ba4a-1920.jpg",
    "https://i.ibb.co/jJxPK9y/young-g1371a2fed-1920.jpg",
    "https://i.ibb.co/zRw3yNV/berlin-wall-gc6646c675-1280.jpg",
    "https://i.ibb.co/6mV5Mnh/man-g18cbd6e90-1920.jpg",
    "https://i.ibb.co/nLrDYzs/palm-tree-gc93308299-1920.jpg",
    "https://i.ibb.co/Ry9JmZ8/sand-castle-g4400346bd-1920.jpg",
    "https://i.ibb.co/Ht8gGBR/pexels-cottonbro-studio-6153354.jpg",
    "https://i.ibb.co/Jv1GXZF/pexels-cottonbro-studio-8720581.jpg",
    "https://i.ibb.co/XZDfLkX/pexels-instawalli-176842.jpg",
    "https://i.ibb.co/0FJ30VG/pexels-mikhail-nilov-8107906.jpg",
    "https://i.ibb.co/JcNbCPr/pexels-rodnae-productions-6069513.jpg",
    "https://i.ibb.co/KbsNDBW/pexels-ryutaro-tsukata-5472337.jpg"
];