/*
    Styles an input, takes the id as the parameter.  Pass this argument without a # sign!
*/

function styleInput(id){
    $('select#'+id).selectmenu("widget").addClass("wrap");
    $('select#'+id).selectmenu({
        style:'popup',
        maxHeight: 150

    });
    $('select#'+id).selectmenu("widget").addClass("wrap");
}

/*
    Styles all inputs on the page
*/

function styleInputs(){
    $('select').selectmenu("widget").addClass("wrap");
    $('select').selectmenu({
        style:'popup',
        maxHeight: 250

    });
    $('select').selectmenu("widget").addClass("wrap");
}

/*

    Resets pagination back to its defaults

*/

function resetPagination(){
    $(".pagination li a").each(function(index){
        var text = $(this).text();
        $(this).parent().html("<a>"+text+"</a>");
    });
    $(".pagination li a").eq(0).addClass("Button");
    $('.Button').button();
}
/*

    Deletes an event

*/


function deleteEvent(postURL,pageRedirectURL,eventID,eventType){
     waitingDialog({});
    $("#dialog2").dialog("close");
    $.post(postURL,{id:eventID}, function(data){
        if(data.success=="success"){
             $('#tabs-2').load(pageRedirectURL, function(){
                $(".fc-button-today").click();
                closeWaitingDialog();
                if(eventType=="agendaWeek")
                    $(".fc-button-agendaWeek").click();
                else if(eventType=="agendaDay")
                    $(".fc-button-agendaDay").click();
             });
        }else{
            closeWaitingDialog();
            alert("Error has occured, please contact globa support!")
        }
     },'json');
}

/*

    Saves an event when it is moved

*/

function moveEvent(postURL,eventID, deltaDays, deltaMinutes){
    $.post(postURL,{id:eventID,deltaDays:deltaDays,deltaMinutes:deltaMinutes}, function(data){

    },'json');
}

/*

    To create an event when the user is in "Month" mode with Jquery UI Framework

*/

function createDayEvent(divID, date, postURL,pageRedirectURL,eventType){
    var start = $(divID+' #timeFrom option:selected').val();
    var end = $(divID+' #timeTo option:selected').val();
    start = start*1;
    end = end*1;
    if(start>=end){
       htmlString = "";
       if(start==end)
            htmlString = "<p>The starting time and ending time cannot be equal!</p>";
       else
            htmlString = "<p>The starting time must be before the ending time!</p>";
       $("#dialog3").html(htmlString);
       $("#dialog3").dialog({
           width: 400,
           title: "Error",
           buttons: [
                {
                    text: "Ok",
                    click: function() { $(this).dialog("close"); }
                }
           ],
           modal: true
       });
    }else{
        waitingDialog({});
        $("#dialog2").dialog("close");
        $.post(postURL,{date1:date, date2:date, start: $(divID+' #timeFrom option:selected').val(),end: $(divID+' #timeTo option:selected').val(), what:$(divID+' #whatInputCreate').val(), emailReminder:$("#emailReminderSelect option:selected").val()}, function(data){
            if(data.success=="success"){
                 $('#tabs-2').load(pageRedirectURL, function(){
                    $(".fc-button-today").click();
                    closeWaitingDialog();
                    if(eventType=="agendaWeek")
                        $(".fc-button-agendaWeek").click();
                    else if(eventType=="agendaDay")
                        $(".fc-button-agendaDay").click();
                 });
            }
         },'json');
     }
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
