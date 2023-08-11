$(document).ready(function(){
    $('.expnd').click(function(){
        let listType = this.classList;

        const regex = /expnd list-button (\w+)-button btn/;
        const match = listType.value.match(regex);
        if (match) {
            const customValue = match[1]; // This will contain the "Custom" value
            $("#"+customValue+"-table").toggle();
        } else {
            console.log("No match found");
        }
        this.classList.toggle('list-button__active');
    });
});

$(document).ready(function(){
    $("#filter").on("keyup", function(){
        let value = $(this).val().toLowerCase();
        $("tbody tr").filter(function(){
            $(this).toggle($(this).text()
                .toLowerCase().indexOf(value) > -1)
        });
    });
});