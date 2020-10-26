/** The URL might end with a frament identifier (starting with # sign),
    referenzing to a line number or a span of line numbers! */
    document.addEventListener("DOMContentLoaded", function() { // wait with JavaScript, unil DOM is available
        var fi = window.location.hash.substring(1); // fragment identifier behind the '#'
        if(fi != ''){ // if fragment identifier is not empty
            if(fi.includes('-')){ // if multiple lines are being selected
                var lines = fi.split("-");
                for ( var i = lines[0] ; i <= lines[1] ; i++ ) { // highlight multiple lines
                    document.querySelector('tr:nth-child(' + i + ')').classList.add("highlight");
                }
            }else{ // if fragment identifier is a single line        
                document.querySelector('tr:nth-child(' + fi + ')').classList.add("highlight");
            }
        }
    })