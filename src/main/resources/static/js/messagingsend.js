$(document).ready(function () {
    
    tinymce.init({
        selector: '[voyasermce=true]',
        height: 300,
        //menubar: false,
        //inline: true,
        textcolor_map: [
          "e31c79", "Wanarosa",
          "000000", "Black",
          "993300", "Burnt orange",
          "333300", "Dark olive",
          "003300", "Dark green",
          "003366", "Dark azure",
          "000080", "Navy Blue",
          "333399", "Indigo",
          "333333", "Very dark gray",
          "800000", "Maroon",
          "FF6600", "Orange",
          "808000", "Olive",
          "008000", "Green",
          "008080", "Teal",
          "0000FF", "Blue",
          "666699", "Grayish blue",
          "808080", "Gray",
          "FF0000", "Red",
          "FF9900", "Amber",
          "99CC00", "Yellow green",
          "339966", "Sea green",
          "33CCCC", "Turquoise",
          "3366FF", "Royal blue",
          "800080", "Purple",
          "999999", "Medium gray",
          "FF00FF", "Magenta",
          "FFCC00", "Gold",
          "FFFF00", "Yellow",
          "00FF00", "Lime",
          "00FFFF", "Aqua",
          "00CCFF", "Sky blue",
          "993366", "Red violet",
          "FFFFFF", "White",
          "FF99CC", "Pink",
          "FFCC99", "Peach",
          "FFFF99", "Light yellow",
          "CCFFCC", "Pale green",
          "CCFFFF", "Pale cyan",
          "99CCFF", "Light sky blue",
          "CC99FF", "Plum"
        ],
        plugins: [
          'advlist autolink lists link image charmap print preview anchor',
          'searchreplace visualblocks code fullscreen',
          'insertdatetime media table contextmenu paste code',
          'textcolor'
        ],
        toolbar: 'undo redo | insert | styleselect | bold italic forecolor backcolor | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | link image',
        content_css: [],
        init_instance_callback: function (editor) {
            editor.on('Blur', function (e) {
                
            });
        },
        file_browser_callback : function(field_name, url, type, win) { 

        // from http://andylangton.co.uk/blog/development/get-viewport-size-width-and-height-javascript
            var w = window,
            d = document,
            e = d.documentElement,
            g = d.getElementsByTagName('body')[0],
            x = w.innerWidth || e.clientWidth || g.clientWidth,
            y = w.innerHeight|| e.clientHeight|| g.clientHeight;

            var filemanagerRouteBase = $(".currentFileManagerRoute").val();
            var cmsURL = '/wanaba/filemanager/index.html?field_name='+field_name+"&exclusiveFolder=/";

            if(type == 'image') {		    
                cmsURL = cmsURL + "&type=images";
            }

            tinyMCE.activeEditor.windowManager.open({
                file : cmsURL,
                title : 'Filemanager',
                width : x * 0.8,
                height : y * 0.8,
                resizable : "yes",
                close_previous : "no"
            });		    

        }
      });
    
});