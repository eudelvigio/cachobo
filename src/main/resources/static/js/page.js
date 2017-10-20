var js = [];
var css = [];
var gm = null;

function launchLayouter() {
    gm = $("#layouter").gridmanager(
            {
                ckeditor: {
                    filebrowserBrowseUrl: "/filemanager/index.html",
                    handlerCustomElementor: handlerClickCustomControl
                }
            }
    );

}

var myCodeMirror = null;

$(document).ready(function () {
    var bEncontradoHTML = false;
    
    
    
    $("#listPageMetadatas").find("li[key='HTML']").each(function () {
        var currentHtmlUrl = $(this).find("[id^='metaDatas'][id$='value']").val();
        if (currentHtmlUrl != null && currentHtmlUrl != undefined) {
            bEncontradoHTML = true;
            $.ajax(currentHtmlUrl).done(function (currentHtml) {
                $("#html").val(currentHtml);
                //$("#metaDatas"+currentIOfHTML+"\\.value").val(currentHtml);
                //launchLayouter();
                myCodeMirror = CodeMirror.fromTextArea(document.getElementById("html"), {
                    mode: "xml",
                    lineNumbers: true,
                    htmlMode: true
                });
            });
        }
    });

    if (!bEncontradoHTML) {
        myCodeMirror = CodeMirror.fromTextArea(document.getElementById("html"), {
            mode: "xml",
            lineNumbers: true,
            htmlMode: true,
            viewportMargin: Infinity
        });
    }


    $("#selectElements").dialog({
        autoOpen: false,
        show: {
            effect: "blind",
            duration: 700
        },
        hide: {
            effect: "blind",
            duration: 700
        },
        height: 600,
        width: 800,
        modal: true,
        resizable: false

    });

    var bEncontrado = false;

    //////////////////////////////////////////////
    //Hay que añadir a pagemetadata la key, xq la vista no tiene element asociado como tal
    //////////////////////////////////////////////
    $("#formPage").on("submit", function () {
        //gm.data('gridmanager').deinitCanvas();
        var bEncontreVista = false;

        $("#listPageMetadatas").find("li[key='HTML']").each(function () {
            $(this).find("[id^='metaDatas'][id$='value']").val($("<div>").text(myCodeMirror.getValue()).html());
            bEncontreVista = true;
        });


        if (!bEncontreVista) {
            i = getLastIndexOfMetadataContainer();

            $("#metadatas-container").append("<input type='hidden' id='metaDatas" + i + ".page' name='metaDatas[" + i + "].page' value='" + $(".currentIdPage").val() + "' />");
            $("#metadatas-container").append("<input type='hidden' id='metaDatas" + i + ".id' name='metaDatas[" + i + "].id' value='' />");
            $("#metadatas-container").append("<input type='hidden' id='metaDatas" + i + ".version' name='metaDatas[" + i + "].version' value='' />");
            $("#metadatas-container").append("<input type='hidden' id='metaDatas" + i + ".value' name='metaDatas[" + i + "].value' value='" + $("<div>").text(myCodeMirror.getValue()).html() + "' />");
            $("#metadatas-container").append("<input type='hidden' id='metaDatas" + i + ".key' name='metaDatas[" + i + "].key' value='HTML' />");

            //$("#metadatas-container").append("<input type='hidden' id='metaDatas"+i+".elementMetadata' name='metaDatas["+i+"].elementMetadata' value='"+fkIDMetadataOfHTML+"' />");

            currentI = i;

            $("#nextIOfMetaDatas").val(i + 1);
        }
        //$("#content").val();
        return true;
    });



})

function showFM() {
    var opened = window.open("/wanaba/filemanager/index.html", "_blank", "width=1000,height=700");

}
window.SetUrl = function () {
    insertElement(arguments[0]);
};



function showElementContainer() {

    $("#selectElements").dialog({
        buttons: [
            {
                text: "OK",
                icons: {
                    primary: "ui-icon-ok"
                },
                click: function () {
                    putElementOnEditor();
                }
            }
        ]

    })

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

var fkIDMetadataOfHTML = "";


function putElementOnEditor() {
    var htmlToInsertAfterClose = "";

    var slug = ""; //El slug es lo que posteriormente se usará para relacionar los datos con el slider. Ñapa de momento, esto hay q mejorarlo

    var replaces = [];
    $("#selectElements #element-configurator").find("input[id!='elementId'][replazator]").each(function () {
        var obj = {};
        obj[$(this).attr("id")] = $(this).val();
        replaces.push(obj);
        if ($(this).attr("id") === "slug") {
            slug = $(this).val();
        }

    });

    $("#selectElements #element-configurator").find("input[id!='elementId'][readonly]:not([replazator])").each(function () {
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
    var elementWithItsData = $(".elements-container").find(".element[element-id='" + elementId + "']");



    //$("#newElement" + identifierNewElement).append("<span>"+elementName+"</span>");

    elementWithItsData.find(".elementMetadata").each(function () {
        var indexToCreateOnSitemetadataList = getLastIndexOfMetadataContainer();

        var identifierNewElement = Math.floor(100000 * Math.random());

        if ($(this).find(".key").val() !== "HTML") {

            $("#listPageMetadatas").append("<li key='" + $(this).find(".key").val() + "' id='newElement" + identifierNewElement + "'></li>");

            var val = $(this).find(".value").val();
            if ($(this).find(".key").val() === "SERVICE") {
                val = slug;
            }

            $("#newElement" + identifierNewElement).append("<input type='hidden' name='metaDatas[" + indexToCreateOnSitemetadataList + "].id' id='metaDatas" + indexToCreateOnSitemetadataList + ".id' />");
            $("#newElement" + identifierNewElement).append("<input type='hidden' value='0' name='metaDatas[" + indexToCreateOnSitemetadataList + "].version' id='metaDatas" + indexToCreateOnSitemetadataList + ".version' />");
            $("#newElement" + identifierNewElement).append("<input type='hidden' value='" + val + "' name='metaDatas[" + indexToCreateOnSitemetadataList + "].value' id='metaDatas" + indexToCreateOnSitemetadataList + ".value' />");
            $("#newElement" + identifierNewElement).append("<input type='hidden' value='0' name='metaDatas[" + indexToCreateOnSitemetadataList + "].order' id='metaDatas" + indexToCreateOnSitemetadataList + ".order' />");
            $("#newElement" + identifierNewElement).append("<input type='hidden' value='" + $("#currentIdPage").val() + "' name='metaDatas[" + indexToCreateOnSitemetadataList + "].page' id='metaDatas" + indexToCreateOnSitemetadataList + ".page' />");
            $("#newElement" + identifierNewElement).append("<input type='hidden' value='" + $(this).find(".id").val() + "' name='metaDatas[" + indexToCreateOnSitemetadataList + "].elementMetadata' id='metaDatas" + indexToCreateOnSitemetadataList + ".elementMetadata' />");
            $("#newElement" + identifierNewElement).append("<input type='hidden' value='" + $(this).find(".script").val() + "' name='metaDatas[" + indexToCreateOnSitemetadataList + "].script' id='metaDatas" + indexToCreateOnSitemetadataList + ".script' />");

            $("#nextIOfMetaDatas").val(indexToCreateOnSitemetadataList + 1);
        }

    });


    /*var newElement = CKEDITOR.dom.element.createFromHtml(htmlToInsertAfterClose, editor.document);
    editor.insertElement(newElement);*/
    insertElement(htmlToInsertAfterClose);
    //$("#html").val($("#html").val() + htmlToInsertAfterClose);

    $("#selectElements").dialog("close");

}


function insertElement(what) {
    
    var selection = myCodeMirror.getSelection();

    if (selection.length > 0){
        myCodeMirror.replaceSelection(what);
    } else {

        var doc = myCodeMirror.getDoc();
        var cursor = doc.getCursor();

        var pos = {
           line: cursor.line,
           ch: cursor.ch
        };

        doc.replaceRange(what, pos);
    }
}


function selectElement(elementId, elementName) {

    $("#selectElements .elements-container").hide();
    $("#selectElements #element-configurator").show();

    var elementWithItsData = $(".elements-container").find(".element[element-id='" + elementId + "']");

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
            inputsToCreate.push("<label>" + elementName + "[" + name + "]</label><input readonly type='text' id='" + name + "' value='" + val + "' />");
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
        $("#selectElements #element-configurator").append(inputsToCreate[i] + "<br />");
    }

    $("#selectElements #element-configurator").append("<input type='hidden' id='elementId' value='" + elementId + "' />");
    $("#selectElements #element-configurator").append("<input type='hidden' id='elementName' value='" + elementName + "' />");




}



function eliminame(IAm) {
    
    if (!confirm("Estás seguro de eliminar este metadata?")) {
        return;
    }
    
    
    var whoMetadataAmI = IAm;
    var andMyIdIs = $(whoMetadataAmI).parent().find("[id^='metaDatas'][id$='.id']").val();
    
    var idPage = $("#currentIdPage").val();
    
    
    $.ajax(
        {
            url: "/wanaba/page/deletemetadata/" + idPage + "?metadatasIds=" + andMyIdIs,
            complete: function () {
                location.reload();
            }
        }
    );
    
    
    
   
}