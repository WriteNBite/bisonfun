$(document).ready(function(){
    $('.expnd').click(function(){
        let listType = this.classList;
        if(listType.contains("planned-button")){
            $("#planned-table").toggle();
        }else if(listType.contains("watching-button")){
            $("#watching-table").toggle();
        }else if(listType.contains("completed-button")){
            $("#completed-table").toggle();
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