/*

    Generalized Function for posting. Used to save a specific field

    divID - this is the id of the div you are loading (you must include the preceding # when passing parameters into the function)

*/
function saveFieldAs(divID, postURL, responseID){
        //Checks to see if actionButtons exist
        if(!$(divID+" .actionButtons").length)
            $(divID).append("<div class='actionButtons'></div>");
        if(!$(".loadingDivGif").length)
                    $(divID).append("<div class='loadingDivGif'></div>");

        $(divID+" .actionButtons").html('<a class="btn" id="button_save">Save</a><a id="button_cancel" class="submit2 button_cancel_class">Cancel</a>');

        $(divID+' #button_save').click(function(){
            /*$(divID +" ol").hide();
            $(divID +" .actionButtons").hide();
            divwidth = $(divID).width();
            divheight = $(divID).height();
            $(divID).width(100);
            $(divID).height(100);
            $('.fancybox-inner').width(100);
            $('.fancybox-wrap').width(130);
            $(".loadingDivGif").show();*/
            $(divID+' #button_save').unbind();
            $.post(postURL,{id: $(divID).attr('saveID'), content:$(divID+' input').val()}, function(data){
               $(responseID).html(data.title);
               parent.$.fancybox.close();

               /*$(".loadingDivGif").hide(function(){
                   $(divID +" ol").show();
                   $(divID +" .actionButtons").show();
                   $(divID).width(divwidth);
                   $(divID).height(divheight);
                });*/

            },'json');
        });

        $('.button_cancel_class').click(function(){
            parent.$.fancybox.close();
        });
}

function createEvent(divID, postURL, date, dateFormatted){
        //Checks to see if actionButtons exist
        if(!$(divID+" .actionButtons").length)
            $(divID).append("<div class='actionButtons'></div>");
        if(!$(".loadingDivGif").length)
                    $(divID).append("<div class='loadingDivGif'></div>");

        $(divID+" .actionButtons").html('<a class="btn" id="button_save" style="font-size: 12px;">Add Event</a><a id="button_cancel" class="submit2 button_cancel_class">Cancel</a>');

        $(divID+" .date").html(dateFormatted);

        $(divID+' #button_save').click(function(){
            $(divID+' #button_save').unbind();
            $.post(postURL,{id: $(divID).attr('saveID'), content:$(divID+' input').val()}, function(data){
               $(responseID).html(data.title);
               parent.$.fancybox.close();

            },'json');
        });

        $('.button_cancel_class').click(function(){
            parent.$.fancybox.close();
        });

        $('.clearOnClick').click(function(){
            $(this).val('');
            $(this).unbind();
        });
}
