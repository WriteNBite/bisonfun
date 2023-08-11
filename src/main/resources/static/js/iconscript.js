async function seasonIcon(){
    let icon = document.getElementById('icon');
    icon.style.height = '48px'
    let iconLink = await getIcons();
    console.log(iconLink);
    icon.src = iconLink;
}

async function getIcons() {
    jqueryCheck();
    return await $.get("/images/icon", function (data) {
        console.log(data);
        if(data) {
            return data.data;
        }else{
            return u;
        }
    })
}

function jqueryCheck() {
    // Check if jQuery is already included
    if (typeof jQuery === 'undefined') {
        // Include jQuery dynamically
        let script = document.createElement('script');
        script.src = 'https://code.jquery.com/jquery-3.6.0.min.js';
        script.onload = function() {
            // jQuery has been loaded
            console.log('jQuery has been loaded!');
        };
        document.head.appendChild(script);
    } else {
        // jQuery is already included
        console.log('jQuery is already included!');
    }
}

const u = "https://img.icons8.com/color/48/null/ukraine.png";