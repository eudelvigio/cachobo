$(document).ready(function () {
   for (var k in window.revisionsMedia) {
       
       var selectVersionesOfMedia = "<div id='container_"+k+"' style='border: 1px solid #ccc'><SELECT id='version_"+k+"'>";
       
       for (var i = window.revisionsMedia[k].length - 1; i >= 0; i--) {
           selectVersionesOfMedia += "<option value='"+window.revisionsMedia[k][i]+"'>"+window.revisionsMedia[k][i]+"</option>";
       }
       selectVersionesOfMedia += "</SELECT>";
       selectVersionesOfMedia += "<div id='publishedVersionOf"+k+"'>Ninguna versión publicada</div>";
       selectVersionesOfMedia += "<input type='button' value='Mostrar edición' onclick='abreEdicion(\""+k+"\")' />";
       selectVersionesOfMedia += "<input type='button' value='Marcar como publicada' onclick='marcaPublicada(\""+k+"\")' /></div>";
       
       
       $("#" + k).after(selectVersionesOfMedia);
       
       
   }
   
   for (var i = 0; i < window.publishedMedia.length; i++) {
       var name = window.publishedMedia[i].name;
       if ($("#publishedVersionOf" + name).length > 0) {
           $("#publishedVersionOf" + name).html("Versión publicada ["+window.publishedMedia[i].revisionNumber+"]");
           $("#version_" + name).val(window.publishedMedia[i].revisionNumber);
       }
   }
   
   
    
});

function marcaPublicada(k) {
    var currentMedia = null;
    var siteSelected = $("#siteId").val();
    for (var i = 0; i < window.allMedia.length; i++) {
        if (window.allMedia[i].name == k) {
            currentMedia = window.allMedia[i];
        }
    }
    $.ajax({
        url: "/wanaba/revisions/" + siteSelected + "/setmediapublished/" + currentMedia.id + "/" + $("#version_" + k).val(),
        async: false
    }).success(function (data) {

        location.reload();
    });
    
}

function abreEdicion(k) {
    var currentMedia = null;
    for (var i = 0; i < window.allMedia.length; i++) {
        if (window.allMedia[i].name == k) {
            currentMedia = window.allMedia[i];
        }
    }
    window.open("/wanaba/medias/edit/" + currentMedia.id + "?revNumber=" + $("#version_" + k).val());
}

function showSelectedRevision() {
    var revSelected = ($("#selectRevisions").val());
    var siteSelected = $("#siteId").val();
    if (revSelected == -1) {
        window.open("/wanaba/site/showhtml/" + siteSelected);
    } else {
        window.open("/wanaba/site/showhtml/"+siteSelected+"?revNumber=" + revSelected);
    }
    
}
function setRevisionAsPublished() {
    var revSelected = ($("#selectRevisions").val());
    var siteSelected = $("#siteId").val();
    if (revSelected == -1) {
        $.ajax({
            url: "/wanaba/revisions/" + siteSelected + "/createrevisionactual",
            async: false
        }).success(function (data) {
            
            //revSelected = data;
            location.reload();
        });
    }
    $.ajax({
        url: "/wanaba/revisions/" + siteSelected + "/setrevisionaspublished/" + revSelected,
        async: false
        
    }).success(function (data) {
            
            //revSelected = data;
            location.reload();
        });
    
    
}

function showPublished() {
    var siteSelected = $("#siteId").val();
       window.open("/wanaba/revisions/" + siteSelected + "/showhtml");
    
}
function downloadPublished() {
    var siteSelected = $("#siteId").val();
       window.open("/wanaba/revisions/" + siteSelected + "/gethtml");
    
}

function compilaDatos() {
    
    var siteSelected = $("#siteId").val();
    
    $.ajax({
        url: "/wanaba/revisions/" + siteSelected + "/compilemedias",
        async: false
    }).success(function (data) {

        location.reload();
    });
    
}

