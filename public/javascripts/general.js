/*

    Generalized Function for posting. Used to save a specific field

    divID - this is the id of the div you are loading (you must include the preceding # when passing parameters into the function)

*/
function saveFieldAs(divID, postURL, responseID){
        //Checks to see if actionButtons exist
        if(!$(".actionButtons").length)
            $(divID).append("<div class='actionButtons'></div>");

        $(divID+" .actionButtons").html('<a class="btn" id="button_save">Save</a><a id="button_cancel" class="submit2">Cancel</a>');

        $('#button_save').click(function(){
            $.fancybox.showLoading();
            $.post(postURL,{id: $(divID).attr('saveID'), content:$(divID+' input').val()}, function(data){
               $(responseID).html(data.title);
               parent.$.fancybox.close();
               $.fancybox.hideLoading();
            },'json');
        });

        $('#button_cancel').click(function(){
            parent.$.fancybox.close();
        });
}
