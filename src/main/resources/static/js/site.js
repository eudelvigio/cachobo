var js = [];
var css = [];

var gm = null;

var cmCSS = null;
var cmHTMLHeader = null;
var cmHTMLFooter = null;

$(document).ready(function () {
    
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
        
        console.log(window.revisions[i]);
    }
    
    $("#publish").click(function () {
        
        publishSite();
        
    });
    
    $("#show").click(function () {
        showSite();
    });
    
    
    $("#selectElements").dialog({
        autoOpen: false,
        show: {
            effect: "blind",
            duration: 500
        },
        hide: {
            effect: "blind",
            duration: 500
        },
        height: 600,
        width: 800,
        modal: true,
        resizable: false

    });

    $("#formSite").on("submit", function () {
        debugger;
        
        var bCSSEncontrado = false;
        var bHTMLHeaderEncontrado = false;
        var bHTMLFooterEncontrado = false;
        var cssValue = cmCSS.getValue();
        var htmlHeaderValue = cmHTMLHeader.getValue();
        var htmlFooterValue = cmHTMLFooter.getValue();

        for (var i = 0; i < getLastIndexOfMetadataContainer(); i++) {
            if ($("#siteMetadatas" + i + "\\.key").val() == "HTML") {
                if ($("#siteMetadatas" + i + "\\.where").val() == "FOOTER") {
                    $("#siteMetadatas" + i + "\\.value").val($("<div>").text(htmlFooterValue).html());
                    bHTMLFooterEncontrado = true;
                }
                if ($("#siteMetadatas" + i + "\\.where").val() == "HEADER") {
                    $("#siteMetadatas" + i + "\\.value").val($("<div>").text(htmlHeaderValue).html());
                    bHTMLHeaderEncontrado = true;
                }
            }
            if ($("#siteMetadatas" + i + "\\.key").val() == "CSS") {
                $("#siteMetadatas" + i + "\\.value").val(cssValue);
                bCSSEncontrado = true;
            }
        }
        if (!bCSSEncontrado) {

            var i = getLastIndexOfMetadataContainer();



            $("#metadatas-container").append("<input type='hidden' id='siteMetadatas" + i + ".site' name='siteMetadatas[" + i + "].site' value='" + $("#currentIdSite").val() + "' />");
            $("#metadatas-container").append("<input type='hidden' id='siteMetadatas" + i + ".id' name='siteMetadatas[" + i + "].id' value='' />");
            $("#metadatas-container").append("<input type='hidden' id='siteMetadatas" + i + ".key' name='siteMetadatas[" + i + "].key' value='CSS' />");
            $("#metadatas-container").append("<input type='hidden' id='siteMetadatas" + i + ".value' name='siteMetadatas[" + i + "].value' value='" + cssValue + "' />");

            $("#nextIOfMetaDatas").val(i + 1);

        }
        if (!bHTMLFooterEncontrado) {

            var i = getLastIndexOfMetadataContainer();



            $("#metadatas-container").append("<input type='hidden' id='siteMetadatas" + i + ".site' name='siteMetadatas[" + i + "].site' value='" + $("#currentIdSite").val() + "' />");
            $("#metadatas-container").append("<input type='hidden' id='siteMetadatas" + i + ".id' name='siteMetadatas[" + i + "].id' value='' />");
            $("#metadatas-container").append("<input type='hidden' id='siteMetadatas" + i + ".key' name='siteMetadatas[" + i + "].key' value='HTML' />");
            $("#metadatas-container").append("<input type='hidden' id='siteMetadatas" + i + ".where' name='siteMetadatas[" + i + "].where' value='FOOTER' />");
            $("#metadatas-container").append("<input type='hidden' id='siteMetadatas" + i + ".value' name='siteMetadatas[" + i + "].value' value='" + htmlFooterValue + "' />");

            $("#nextIOfMetaDatas").val(i + 1);
        }
        if (!bHTMLHeaderEncontrado) {

            var i = getLastIndexOfMetadataContainer();



            $("#metadatas-container").append("<input type='hidden' id='siteMetadatas" + i + ".site' name='siteMetadatas[" + i + "].site' value='" + $("#currentIdSite").val() + "' />");
            $("#metadatas-container").append("<input type='hidden' id='siteMetadatas" + i + ".id' name='siteMetadatas[" + i + "].id' value='' />");
            $("#metadatas-container").append("<input type='hidden' id='siteMetadatas" + i + ".key' name='siteMetadatas[" + i + "].key' value='HTML' />");
            $("#metadatas-container").append("<input type='hidden' id='siteMetadatas" + i + ".where' name='siteMetadatas[" + i + "].where' value='HEADER' />");
            $("#metadatas-container").append("<input type='hidden' id='siteMetadatas" + i + ".value' name='siteMetadatas[" + i + "].value' value='" + htmlHeaderValue + "' />");

            $("#nextIOfMetaDatas").val(i + 1);
        }
        debugger;
        return true;
    });
    var bUrlOfCSSEncontrado = false;
    var bUrlOfHTMLHeaderEncontrado = false;
    var bUrlOfHTMLFooterEncontrado = false;
    for (var i = 0; i < getLastIndexOfMetadataContainer(); i++) {
        if ($("#siteMetadatas" + i + "\\.key").val() == "HTML") {
            if ($("#siteMetadatas" + i + "\\.where").val() == "FOOTER") {
                var currentHTMLFooterUrl = $("#siteMetadatas" + i + "\\.value").val();
                if (currentHTMLFooterUrl) {
                    bUrlOfHTMLFooterEncontrado = true;
                    var iOfHTMLFooter = i;

                    $.ajax(currentHTMLFooterUrl).done(function (currentHTMLFooter) {
                        $("#html-footer").val(currentHTMLFooter);
                        $("#siteMetadatas" + iOfHTMLFooter + "\\.value").val(currentHTMLFooter);
                        cmHTMLFooter = CodeMirror.fromTextArea(document.getElementById("html-footer"), {
                            mode: "xml",
                            lineNumbers: true,
                            htmlMode: true,
                            viewportMargin: Infinity

                        });

                    })
                }
            }
            if ($("#siteMetadatas" + i + "\\.where").val() == "HEADER") {
                var currentHTMLHeaderUrl = $("#siteMetadatas" + i + "\\.value").val();
                if (currentHTMLHeaderUrl) {
                    bUrlOfHTMLHeaderEncontrado = true;
                    var iOfHTMLHeader = i;

                    $.ajax(currentHTMLHeaderUrl).done(function (currentHTMLHeader) {
                        $("#html-header").val(currentHTMLHeader);
                        $("#siteMetadatas" + iOfHTMLHeader + "\\.value").val(currentHTMLHeader);
                        cmHTMLHeader = CodeMirror.fromTextArea(document.getElementById("html-header"), {
                            mode: "xml",
                            lineNumbers: true,
                            htmlMode: true,
                            viewportMargin: Infinity

                        });

                    })
                }
            }
        }
        if ($("#siteMetadatas" + i + "\\.key").val() == "CSS") {
            var currentCSSUrl = $("#siteMetadatas" + i + "\\.value").val();
            if (currentCSSUrl) {
                bUrlOfCSSEncontrado = true;
                var iOfCSS = i;

                $.ajax(currentCSSUrl).done(function (currentCSS) {
                    $("#css").val(currentCSS);
                    $("#siteMetadatas" + iOfCSS + "\\.value").val(currentCSS);
                    cmCSS = CodeMirror.fromTextArea(document.getElementById("css"), {
                        mode: "text/css"

                    });

                });
            }
        }
    }
    
    
    if (!bUrlOfCSSEncontrado) {

        cmCSS = CodeMirror.fromTextArea(document.getElementById("css"), {
            mode: "text/css",
            lineNumbers: true,
            viewportMargin: Infinity

        });
    }
    
    
    if (!bUrlOfHTMLHeaderEncontrado) {
        cmHTMLHeader = CodeMirror.fromTextArea(document.getElementById("html-header"), {
            mode: "xml",
            lineNumbers: true,
            htmlMode: true,
            viewportMargin: Infinity

        });
    }
    if (!bUrlOfHTMLFooterEncontrado) {
        cmHTMLFooter = CodeMirror.fromTextArea(document.getElementById("html-footer"), {
            mode: "xml",
            lineNumbers: true,
            htmlMode: true,
            viewportMargin: Infinity

        });
    }

})



//function handlerClickCustomControl(editor) {
function showElementator(where) {
    $("#selectElements").dialog({
        buttons: [
            {
                text: "OK",
                icons: {
                    primary: "ui-icon-ok"
                },
                click: function () {
                    putElementOnEditor(where);
                }
            }
        ]

    });

    $("#selectElements").dialog("open");


}

function getLastIndexOfMetadataContainer() {

    var currentI = $("#nextIOfMetaDatas").val();

    return parseInt(currentI);

}

function getIndexOfMetadataContainer(element) {
    var name = $(element).attr("name");
    return name ? parseInt(name.replace(/\D/gi, ''), 10) : -1;
}

function putElementOnEditor(where) {
    //editor.insertText("adsgfñoigarehng hrgfwuirghoiudhf nvprishfnovirkeahsgndf");
    var htmlToInsertAfterClose = "";
    
    var slug = ""; //El slug es lo que posteriormente se usará para relacionar los datos con el slider. Ñapa de momento, esto hay q mejorarlo
    var replaces = [];
    $("#selectElements #element-configurator").find("input[id!='elementId'][replazator]").each(function () {
        debugger;
        var obj = {};
        obj[$(this).attr("id")] = $(this).val();
        replaces.push(obj);
        if ($(this).attr("id") === "slug") {
            slug = $(this).val();
        }

    });

    $("#selectElements #element-configurator").find("input[id!='elementId'][readonly]:not([replazator])").each(function () {
        debugger;
        var val = $(this).val();

        for (var i = 0; i < replaces.length; i++) {
            for (var k in replaces[i]) {
                val = val.replace("${|" + k + "|}", replaces[i][k]);
            }
        }

        htmlToInsertAfterClose += val;
    });

    
    
    var elementId = $("#selectElements #element-configurator").find("input#elementId").val();
    var elementName = $("#selectElements #element-configurator").find("input#elementName").val();
    var elementWithItsData = $(".elements-container").find(".element[element-id='"+elementId+"']");
    
    var identifierNewElement = Math.floor(100000 * Math.random());
    
    $("#listSiteMetadatas").append("<li id='newElement"+identifierNewElement+"'></li>");
    
    $("#newElement" + identifierNewElement).append("<span>"+elementName+"</span>");

    elementWithItsData.find(".elementMetadata").each(function () {
        var indexToCreateOnSitemetadataList = getLastIndexOfMetadataContainer();
       
        var val = $(this).find(".value").val();
        if ($(this).find(".key").val() === "SERVICE") {
            val = slug;
        }

            
        
        $("#newElement" + identifierNewElement).append("<input type='hidden' name='siteMetadatas["+indexToCreateOnSitemetadataList+"].id' id='siteMetadatas"+indexToCreateOnSitemetadataList+".id' />");
        $("#newElement" + identifierNewElement).append("<input type='hidden' value='0' name='siteMetadatas["+indexToCreateOnSitemetadataList+"].version' id='siteMetadatas"+indexToCreateOnSitemetadataList+".version' />");
        $("#newElement" + identifierNewElement).append("<input type='hidden' value='"+val+"' name='siteMetadatas["+indexToCreateOnSitemetadataList+"].value' id='siteMetadatas"+indexToCreateOnSitemetadataList+".value' />");
        $("#newElement" + identifierNewElement).append("<input type='hidden' value='0' name='siteMetadatas["+indexToCreateOnSitemetadataList+"].order' id='siteMetadatas"+indexToCreateOnSitemetadataList+".order' />");
        $("#newElement" + identifierNewElement).append("<input type='hidden' value='" + $("#currentIdSite").val() + "' name='siteMetadatas["+indexToCreateOnSitemetadataList+"].site' id='siteMetadatas"+indexToCreateOnSitemetadataList+".site' />");
        $("#newElement" + identifierNewElement).append("<input type='hidden' value='"+$(this).find(".id").val()+"' name='siteMetadatas["+indexToCreateOnSitemetadataList+"].elementMetadata' id='siteMetadatas"+indexToCreateOnSitemetadataList+".elementMetadata' />");
        $("#newElement" + identifierNewElement).append("<input type='hidden' value='"+$(this).find(".script").val()+"' name='siteMetadatas["+indexToCreateOnSitemetadataList+"].script' id='siteMetadatas"+indexToCreateOnSitemetadataList+".script' />");

        $("#nextIOfMetaDatas").val(indexToCreateOnSitemetadataList + 1);

    });
    
    insertElement(where, htmlToInsertAfterClose);
    //var newElement = CKEDITOR.dom.element.createFromHtml(htmlToInsertAfterClose, editor.document);
    //editor.insertElement(newElement);

    $("#selectElements").dialog("close");

}


function selectElement(elementId, elementName) {
    $("#selectElements .elements-container").hide();
    $("#selectElements #element-configurator").show();

    var elementWithItsData = $(".elements-container").find(".element[element-id='"+elementId+"']");
    
    var inputsToCreate = [];
    
    elementWithItsData.find(".elementMetadata").each(function () {
        var whereToInsert = $(this).find(".where").val();
        
        var name = $(this).find(".key").val() 
                + $(this).find(".scope").val() 
                + $(this).find(".service").val() 
                + $(this).find(".storage").val() 
                + $(this).find(".where").val();
        
        var val = $(this).find(".value").val();
        
        if (whereToInsert === "INLINE") {
            inputsToCreate.push("<label>"+elementName+"["+name+"]</label><input readonly type='text' id='"+name+"' value='"+val+"' />");
        }
        
        
        var userDefinedData = val.match(/\${\|([a-zA-Z])+\|}/gi);

        if (userDefinedData != null && userDefinedData.length > 0) {
            for (var i = 0; i < userDefinedData.length; i++) {
                var name = userDefinedData[i].replace(/\${\|/gi, "").replace(/\|}/gi, "");
                inputsToCreate.push("<label>" + name + "</label><input replazator type='text' id='" + name + "' /><br />");
            }

        }
        
    });
    
    for (var i = 0; i < inputsToCreate.length; i++) {
        $("#selectElements #element-configurator").append(inputsToCreate[i] + "<br/>");
    }
    
    $("#selectElements #element-configurator").append("<input type='hidden' id='elementId' value='"+elementId+"' />");
    $("#selectElements #element-configurator").append("<input type='hidden' id='elementName' value='"+elementName+"' />");
    
}

function insertElement(where, what) {
    var cm = null;
    
    if (where === "header") {
        cm = cmHTMLHeader;
    } else if (where === "footer") {
        cm = cmHTMLFooter;
    }
     var selection = cm.getSelection();

    if (selection.length > 0){
        cm.replaceSelection(what);
    } else {

        var doc = cm.getDoc();
        var cursor = doc.getCursor();

        var pos = {
           line: cursor.line,
           ch: cursor.ch
        };

        doc.replaceRange(what, pos);
    }
}


var opened = null;

var whoAmI = null;

function showFM(where) {
	whoAmI = where;

	opened = window.open("/wanaba/filemanager/index.html", "_blank", "width=1000,height=700");

}

window.SetUrl = function () {
    insertElement(whoAmI, arguments[0]);
};


function eliminame(IAm) {
    
    if (!confirm("Estás seguro de eliminar este metadata?")) {
        return;
    }
    
    
    var whoMetadataAmI = IAm;
    var andMyIdIs = $(whoMetadataAmI).parent().find("[id^='siteMetadatas'][id$='.id']").val();
    
    var idSite = $("#currentIdSite").val();
    
    
    $.ajax(
        {
            url: "/wanaba/site/deletemetadata/" + idSite + "?metadatasIds=" + andMyIdIs,
            complete: function () {
                location.reload();
            }
        }
    );
    
    
    
   
}
function goToNextRev() {
    for (var i = 0; i < window.revisions.length; i++) {
        if (window.revisions[i] == window.currentRevision) {
            if (i == window.revisions.length - 1) {
                location.href = "/wanaba/site/edit/" + $("#currentIdSite").val();
            } else {
                location.href = "/wanaba/site/edit/" + $("#currentIdSite").val() + "?revNumber=" + window.revisions[i + 1];
            }
            break;
        }
    }
        
}
function goToPrevRev() {
    if (window.currentRevision == -1) {
        location.href = "/wanaba/site/edit/" + $("#currentIdSite").val() + "?revNumber=" + window.revisions[window.revisions.length - 1];
    } else {
        
        for (var i = 0; i < window.revisions.length; i++) {
            if (window.revisions[i] == window.currentRevision) {
                location.href = "/wanaba/site/edit/" +$("#currentIdSite").val() + "?revNumber=" + window.revisions[i - 1];
                break;
            }
        }
        
    }
}

function showSite() {
    var siteId = $("#currentIdSite").val();
    var rev = window.currentRevision;
    window.open("/wanaba/site/showhtml/" + siteId + (rev != -1 ? "?rev=" + rev:""));
}

function publishSite() {
    var siteId = $("#currentIdSite").val();
    
    if (confirm("¿Seguro que desea publicar esta revisión? Se publicarán los datos actuales de esta revisión")) {
        $.ajax(
            {
                url: "/wanaba/site/publish/" + siteId + (window.currentRevision!=-1?"?revNumber=" + window.currentRevision:""),
                complete: function () {
                    alert("Datos publicados correctamente.");
                    location.reload();
                }
            }
        );
        
    }
    
    
}