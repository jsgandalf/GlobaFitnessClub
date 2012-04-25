// slideshow.js

$(document).ready(init);

function init() {
	// now to use the cycle plugin
	$(".switch_pictures").cycle({
		fx:     'scrollUp', 
		pause: 1,
		pager: ".splash_right",
		pagerAnchorBuilder: imagePager,
		timeout: 6000
	});	
}

function imagePager(index, slide) {
	var slide = jQuery(slide);
	var img = slide.children("img").get(0);
	var text = "";
	if(index==0){
		text = "Training";
	}else if(index==1){
		text = "Consultation";
	}else if(index==2){
		text = "Connect";
	}else if(index==3){
		text = "Business";
	}
	return '<div class="image_mini'+index+'"><div class="splash_text">'+text+'</div><img style="visibility:hidden; position:absolute; top:0;" src="' + img.src + '" /></div>'; 
}