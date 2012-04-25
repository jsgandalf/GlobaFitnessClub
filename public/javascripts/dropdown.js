//dropdown.js

//http://css-tricks.com/simple-jquery-dropdowns/

//<p><a href="index-hoverIntent.html">With hoverIntent</a> | <a href="http://css-tricks.com/simple-jquery-dropdowns/">Article</a> | <a href="http://css-tricks.com/examples/SimplejQueryDropdowns.zip">Download</a></p>     

$(function(){

    $("ul.dropdown li").hover(function(){
        $(this).addClass("hover");
        $('ul:first',this).css('visibility', 'visible');
    
    }, function(){
    
        $(this).removeClass("hover");
        $('ul:first',this).css('visibility', 'hidden');
    
    });
    
    $("ul.dropdown li ul li:has(ul)").find("a:first").append(" &raquo; ");

});