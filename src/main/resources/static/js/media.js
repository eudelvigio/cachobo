var opened = null;

var whoAmI = null;

function launchFileManager(IAm) {
	whoAmI = IAm;
        var filemanagerRouteBase = $(".currentFileManagerRoute").val();
	opened = window.open("/wanaba/filemanager/index.html?exclusiveFolder="+filemanagerRouteBase, "_blank", "width=1000,height=700");

}
window.SetUrl = function () {
    console.log(arguments);
    
    $(whoAmI).parent().find("[data-field='xtraValue']").val(arguments[0]);
    $(whoAmI).parent().find("img").attr("src", arguments[0]);
    
};
   
function insertNewElement() {    
    $.ajax("/wanaba/medias/createmediadata/" + $(".currentIdMedia").val()).then(function () {location.reload();});
}
   
function addElement(){
    
    insertNewElement();
    //$("#submit").click();
    
};

function addServeSite() {
    $("#selectSites").show();
    $("#btnConfirmServeSite").show();
    $("#btnCancelServeSite").show();
    
}

function confirmServeSite() {
    if (confirm("Seguro?")) {
        $.ajax("/wanaba/medias/servemedia/" + $(".currentIdMedia").val() + "/tosite/" + $("#selectSites").val()).then(function () {location.reload();});
    }
}
function deleteServeSite(obj) {
    var siteidtodelete = $(obj).attr("site-id");
    if (confirm("Seguro?")) {
        $.ajax("/wanaba/medias/servemedia/" + $(".currentIdMedia").val() + "/quitsite/" + siteidtodelete).then(function () {location.reload();});
    }
}
function cancelServeSite() {
    $("#selectSites").hide();
    $("#btnConfirmServeSite").hide();
    $("#btnCancelServeSite").hide();
    
}

function deleteme(me) {
    var id = $(me).parent().find(".mini-element input").val();
    
    $.ajax(
        {
            url: "/wanaba/medias/deletemediadata/" + id,
            complete: function () {
                location.reload();
            }
        }
    );
    
}

var windowOpened = null;
function showConfiguratorMediadata(self) {
    if (!window.canEditNow) {
        alert("No se puede editar el elemento si no se está en la pestaña de edición");
        return;
    }
    
    self = $(self).find(".mini-element");
    
    var elementMetadata = $(self).parent().find("div.element-data");
    var htmlOfMediadataXtra = elementMetadata.html();
            
    if (windowOpened !== null) {
        windowOpened.close();
        
    }
    var idMediaData = (elementMetadata.find("input.id[id$='.id']").val());
    
    windowOpened = window.open("/wanaba/html/apacaelementeditor.html?idmediadata=" + idMediaData + "&idmedia=" + $(".currentIdMedia").val() + "&route=" + $(".currentFileManagerRoute").val(), "editor", "height=800,width=1500,menubar=0,titlebar=0,top=0,left=0");
    //windowOpened = window.open("/wanaba/html/mediaelementeditor.html", "editor", "height=800,width=1500,menubar=0,titlebar=0,top=0,left=0");
    
    windowOpened.onload = function () {
       
    };

}
window.actualizaMisDatosDePopup = function (datosACambiar) {
    for (var i = 0; i < datosACambiar.length; i++) {
        var obj = $("#" + datosACambiar[i].id.replace(/\./gi, "\\."));
        if (obj) {
            //Seteamos el valor lo primero
            obj.val(datosACambiar[i].val);

            //Y luego usamos la función para que se refleje en el html el cambio
            if (obj.is("[type='radio']") || obj.is("[type='radio']")) {
                if (obj.prop("checked")) {
                    obj.attr("checked", "checked");
                } else {
                    obj.removeAttr("checked");
                }
            } else {
                if (obj.is("select")) {
                    obj.find(":selected").attr("selected", "selected");
                } else {
                    obj.attr("value", obj.val());
                }
            }
        }

    }
};

var iPSMD = 0;
var orderPosible = {};
var hayQueOrdenar = false;




function procesaSMDS() {
    iPSMD = 0;
    $("#mediaContainer").find(".element-media-container").each(parseVisualData);
    
    ordenaSMDS();
    
    $("[voyasertagger=true]").tagit({singleField: true});
    
    $("#mediaContainer").sortable({ handle: '.handle' });
    $("#mediaContainer").disableSelection();
    
    $("#formMedia").off("submit", function ()  {preSubmit(); return true; });
    $("#formMedia").on("submit", function ()  {preSubmit(); return true; });
}


function ordenaSMDS() {
    
    var pubInfo = {
        "nopublicado": [],
        "expirado": [],
        "despublicado": []
    };
    
    var orderByInfo = [];
    
    
    var candemorcontador = 0;
    
    $("#mediaContainer").find(".element-media-container").each(function () {
        candemorcontador++;
        $elementMediaContainer = $(this);
        
        var shouldTakeCareOnOrderBy = true; //Con esta variable chequeamos si debemos tener en cuenta para la ordenación
        
        var orderByField = $(this).find(".orderOfMediaData");
        var publicationField = $(this).find(".publication");
        var expirationField = $(this).find(".expiration");
        var deletionField = $(this).find(".deletion");
        

        var val = $(publicationField).val();
        var currentDate = new Date();
        var publicationDate = new Date(val);

        if (val != null && val !="" && val != "Invalid Date") {
            if (publicationDate > currentDate) {
                //anterior a la publicación
                pubInfo.nopublicado.push($elementMediaContainer);
                shouldTakeCareOnOrderBy = false;
            } else {
                //posterior, amos, publicado, se queda en el mismo sitio

            }
        } else {
            //suponemos que publicado, se queda en el mismo sitio
        }

        var val = $(expirationField).val();
        var expirationDate = new Date(val);

        if (val != null && val !="" && val != "Invalid Date") {
            if (expirationDate < currentDate) {
                //anterior a la publicación
                pubInfo.expirado.push($elementMediaContainer);
                shouldTakeCareOnOrderBy = false;
            } else {
                //posterior, amos, publicado, se queda en el mismo sitio

            }
        } else {
            //suponemos que publicado, se queda en el mismo sitio
        }

        var val = $(deletionField).val();

        var deletionDate = new Date(val);
        if (val != null && val !="" && val != "Invalid Date") {
            if (deletionDate < currentDate) {
                pubInfo.despublicado.push($elementMediaContainer);
                shouldTakeCareOnOrderBy = false;
            } else {
                //posterior, amos, publicado, se queda en el mismo sitio
            }
        } else {
            //suponemos que publicado, se queda en el mismo sitio
        }

        
        //entonces, chequeamos el orden solo en el caso de que no esté expirado, despublicado o no publicado aún
        if (shouldTakeCareOnOrderBy) {
            var val = $(orderByField).val();
            orderByInfo[val] = $elementMediaContainer;
            
            
            
        }
        
        
        
    });
    
    
    
    for (var i = 0; i < pubInfo.nopublicado.length; i++) {
        $(pubInfo.nopublicado[i]).css("opacity", ".5");
        $("#" + $(pubInfo.nopublicado[i]).attr("id")).appendTo($("#mediaContainerNotYetPublished"));
    }
     
    
    for (var i = 0; i < pubInfo.expirado.length; i++) {
        $(pubInfo.expirado[i]).css("opacity", ".5");
        $("#" + $(pubInfo.expirado[i]).attr("id")).appendTo($("#mediaContainerExpired"));
    }
    
    
    for (var i = 0; i < pubInfo.despublicado.length; i++) {
        $(pubInfo.despublicado[i]).css("opacity", ".5");
        $("#" + $(pubInfo.despublicado[i]).attr("id")).appendTo($("#mediaContainerDeleted"));
    }
    
    
    $("#not-yet-published-counter").html($("#mediaContainerNotYetPublished").find(".element-media-container:not(.defaultMediaData)").length);
    $("#expired-counter").html($("#mediaContainerExpired").find(".element-media-container:not(.defaultMediaData)").length);
    $("#deleted-counter").html($("#mediaContainerDeleted").find(".element-media-container:not(.defaultMediaData)").length);
    
    
    for (var i = 0; i < orderByInfo.length; i++) {
        if (orderByInfo[i]) {
            
            $(orderByInfo[i]).appendTo($("#mediaContainer"));
            
        }
        
    }
    
}

function parseVisualData() {
    $elementMediaContainer = $(this);
    iPSMD++;
    
    $elementMediaContainer.find("[data-field=\"xtraType\"]").each(function () {
        if ($(this).val() == "PRINCIPAL_IMAGE") {
            $elementMediaContainer.css("background-image", "url('"+$(this).parent().find("[data-field=\"xtraValue\"]").val() + "')");
            $elementMediaContainer.css("background-size", "contain");
        }

        
        if ($(this).val() == "ID") {
            //En caso de ser de tipo id, hay que autogenerar un id si no hay ninguno, que debería ser único
            var txt = $(this).parent().find("[data-field=\"xtraValue\"]").val();
            $(this).parent().hide();
            if (txt == "") {
                $(this).parent().find("[data-field=\"xtraValue\"]").val(generateUUID());
            }
        }

        if ($(this).val() == "PRINCIPAL_NAME") {
            var txt = $(this).parent().find("[data-field=\"xtraValue\"]").val();
            if (txt != "")
                $elementMediaContainer.find(".mini-element h1").text(txt);
        }
    }); 
    
}

function generateUUID () { // Public Domain/MIT
    var d = new Date().getTime();
    if (typeof performance !== 'undefined' && typeof performance.now === 'function'){
        d += performance.now(); //use high-precision timer if available
    }
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = (d + Math.random() * 16) % 16 | 0;
        d = Math.floor(d / 16);
        return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
    });
}



function preSubmit() {
    //Hay que setear los valores del orden porlomenos
    var i = 0;
     $("#mediaContainer").find(".element-media-container").each(function () {
        $elementMediaContainer = $(this);
        i++;
        $elementMediaContainer.find(".orderOfMediaData").attr("value", i);
     });
     debugger;
}

function goToNextRev() {
    for (var i = 0; i < window.revisions.length; i++) {
        if (window.revisions[i] == window.currentRevision) {
            if (i == window.revisions.length - 1) {
                location.href = "/wanaba/medias/edit/" + $(".currentIdMedia").val();
            } else {
                location.href = "/wanaba/medias/edit/" + $(".currentIdMedia").val() + "?revNumber=" + window.revisions[i + 1];
            }
            break;
        }
    }
        
}
function goToPrevRev() {
    if (window.currentRevision == -1) {
        location.href = "/wanaba/medias/edit/" + $(".currentIdMedia").val() + "?revNumber=" + window.revisions[window.revisions.length - 1];
    } else {
        
        for (var i = 0; i < window.revisions.length; i++) {
            if (window.revisions[i] == window.currentRevision) {
                location.href = "/wanaba/medias/edit/" + $(".currentIdMedia").val() + "?revNumber=" + window.revisions[i - 1];
                break;
            }
        }
        
    }
}

function openConfiguratorMedia() {
    if (windowOpened !== null) {
        windowOpened.close();
    }
    windowOpened = window.open("/wanaba/html/apacaelementeditor.html?idmedia=" + $(".currentIdMedia").val() + "&route=" + $(".currentFileManagerRoute").val(), "editor", "height=800,width=1500,menubar=0,titlebar=0,top=0,left=0");
    
}

$(document).ready(function () {
    $('[data-toggle="popover"]').popover(); 
    procesaSMDS();
    window.currentRevision = -1;
    
    for (var i = 0; i < window.revisions.length; i++) {
        if (location.href.indexOf("revNumber") > -1) {
            window.currentRevision = parseInt(location.href.substring(location.href.indexOf("revNumber=") + "revNumber=".length));
        }
        if (window.currentRevision == -1) {
            $("#goToNextRev").hide();
        }
        
        if (window.currentRevision == window.revisions[0]) {
            $("#goToPrevRev").hide();
        }
    }
    
    $("#btnCompile").click(function () {
        
        var siteId = $(".fkIdsite").val();
        var idMedia = $(".currentIdMedia").val();
        var rev = -1;
        if (location.href.indexOf("revNumber=") > -1) {
            rev = location.href.substring(location.href.indexOf("revNumber=") + "revNumber=".length);
        }
        
        if (confirm("¿Seguro que desea publicar esta revisión? Se publicarán los datos actuales de esta revisión, y se recalcularán todos los servicios para todos los sitios a los que sirve este media")) {
            $.ajax(
                {
                    url: "/wanaba/medias/compileandcachemedia/" + idMedia + "/" + rev,
                    complete: function () {
                        alert("Datos publicados correctamente.");
                        location.reload();
                    }
                }
            );
        }
        
    });
    $("#tabs-elements").tabs();
    window.canEditNow = true;
    
});
