/*

    To create an event with Jquery UI Framework

*/

function createDayEvent(divID, postURL, date, dateFormatted,pageRedirectURL){
    waitingDialog({});
    $.post(postURL,{timeFrom: $(divID+' #timeFrom').val(),timeTo: $(divID+' #timeTo').val(), what:$(divID+' #whatInput').val(),date:date}, function(data){
        parent.$.fancybox.close();
        if(data.success=="success"){
             $('#tabs-2').load(pageRedirectURL, function(){
                $(".fc-button-today").click();
                closeWaitingDialog();
             });
             /*$.ajax({
               url: pageRedirectURL,
               context: document.body,
               success: function(s,x){
                 $(this).html(s, function(){
                    $('#calendarTab').click();
                    $(".fc-button-today").click();
                    $.fancybox('<h4 style="text-align:center; margin-top:10px;">Event</h4><h4 style="margin-top:10px;">Successfuly Scheduled!</h4>');
                 });

               }
             });*/

             //window.location.replace(pageRedirectURL);

        }
     },'json');
}

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

function createEvent(divID, postURL, date, dateFormatted,pageRedirectURL){
         //Checks to see if actionButtons exist
         if(!$(divID+" .actionButtons").length)
             $(divID).append("<div class='actionButtons'></div>");

         $(divID+" .actionButtons").html('<a class="btn" id="button_save" style="font-size: 12px; right:7px;">Add Event</a><a id="button_cancel" class="submit2 button_cancel_class">Cancel</a>');

         $(divID+" .date").html(dateFormatted);

         $(divID+' #button_save').click(function(){
             $(divID+' #button_save').unbind();
             $.post(postURL,{timeFrom: $(divID+' #timeFrom').val(),timeTo: $(divID+' #timeTo').val(), what:$(divID+' #whatInput').val(),date:date}, function(data){
                parent.$.fancybox.close();
                if(data.success=="success"){
                     $.fancybox('<h4 style="text-align:center; margin-top:10px;">Event</h4><h4 style="margin-top:10px;">Successfuly Scheduled!</h4>');
                     window.location.replace(pageRedirectURL);
                }
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

function editEvent(divID, eventID, eventTitle, postURL, date, dateFormatted,pageRedirectURL){
        //Checks to see if actionButtons exist
        if(!$(divID+" .actionButtons").length)
            $(divID).append("<div class='actionButtons'></div>");

        $(divID+" .actionButtons").html('<a class="btn" id="button_save" style="font-size: 12px; right:7px;">Delete</a><a id="button_cancel" class="submit2 button_cancel_class">Cancel</a>');

        $(divID+" .date").html(dateFormatted);
        $(divID+" #eventTitle").html(eventTitle)
        $(divID+' #button_save').click(function(){
            $(divID+' #button_save').unbind();
            $.post(postURL,{id:eventID}, function(data){
               parent.$.fancybox.close();
               if(data.success=="success"){
                    $.fancybox('<h4 style="text-align:center; margin-top:10px;">Event</h4><h4 style="margin-top:10px;">Successfuly Deleted!</h4>');
                    window.location.replace(pageRedirectURL);
               }
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



function initializeLoadingDialog(){
	// create the loading window and set autoOpen to false
	$("#loadingScreen").dialog({
		autoOpen: false,	// set this to false so we can manually open it
		dialogClass: "loadingScreenWindow",
		closeOnEscape: false,
		draggable: false,
		width: 100,
		minHeight: 150,
		modal: true,
		buttons: {},
		resizable: false,
		open: function() {
			// scrollbar fix for IE
			$('body').css('overflow','hidden');
		},
		close: function() {
			// reset overflow
			$('body').css('overflow','auto');
		}
	}); // end of dialog
}
function waitingDialog(waiting) { // I choose to allow my loading screen dialog to be customizable, you don't have to
	//$("#loadingScreen").html(waiting.message &amp;&amp; '' != waiting.message ? waiting.message : 'Please wait...');
	$("#loadingScreen").dialog('option', 'title', 'Loading');
	$("#loadingScreen").dialog('open');
}
function closeWaitingDialog() {
	$("#loadingScreen").dialog('close');
}
