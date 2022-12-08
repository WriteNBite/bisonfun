function mainBg() {
    let header = document.getElementById("background-header");
    let index = Math.floor(Math.random()*bg.length);
    console.log(index);
    header.style.backgroundImage = "url("+bg[index]+")";
}

const bg = [
    "https://i.ibb.co/M62ktXS/colorful-3d-shapes-vaporwave-style.jpg",
    "https://i.ibb.co/j5YngpQ/pexels-el-na-ar-ja-3319333.jpg",
    "https://i.ibb.co/Q9H71s9/pexels-francesco-ungaro-1525050.jpg",
    "https://i.ibb.co/jDwFJM9/pexels-jarno-van-loon-3039150.jpg",
    "https://i.ibb.co/yQpwF4X/pexels-pixabay-209620.jpg",
    "https://i.ibb.co/dgTChkF/pexels-pixabay-264905.jpg",
    "https://i.ibb.co/VvLGfxq/pexels-shonejai-1227497.jpg",
    "https://i.ibb.co/nnwGP4f/vintage-feminine-collage-element-printable-collage-mixed-media-art.jpg",
    "https://i.ibb.co/sHg5VXK/yasaka-pagoda-sannen-zaka-street-kyoto-japan.jpg"
];