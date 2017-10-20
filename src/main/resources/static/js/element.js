var prettyMetadater;
$(document).ready(function () {

	
	$("#update").on("click", function () {
            var elementId = $("#currentIdElement").val();
            window.open("/wanaba/element/updateme/"+elementId);
        });
	
	$("#formElement").on("submit", function () {
		$("#metadatas-container INPUT.key").each(function () {
			 if ($(this).val().indexOf("HTML") > -1) {
				 $(this).parent().find("INPUT.value").val($("<div>").text($(this).parent().find("INPUT.value").val()).html());
			 }	 
		 });	
	});
	
	$("#modalDependenciesContainer").dialog({
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
	
	
	$("#modalMetadataContainer").dialog({
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
		resizable: false,
		buttons: [
			{
				text: "OK",
				icons: {
					primary: "ui-icon-ok"
				},
				click: function () {
					
					var currentIndex = $("#nextIOfMetaDatas").val();
					
                                        if ($("#modalMetadataContainer").find(".newId").val() != "") {
                                            var Id = $("#modalMetadataContainer").find(".newId").val();
                                            $("#prettyMetadataContainer").find(".id").each(function () {
                                                
                                                if ($(this).val() == Id) {
                                                    currentIndex = $(this).parent().attr("current-index");
                                                }
                                                
                                            });
                                            $("#prettyMetadataContainer").find("LI.metadata[current-index="+currentIndex+"]").remove();
                                            
                                            
                                        } //else {
                                            $("#metadatas-container").append("<div class='metadata' current-index='"+currentIndex+"'></div>");
                                        //}
                                        
                                        
					
                                        
                                        
                                        var appendData = "";
                                        
                                        $("#modalMetadataContainer").find("INPUT[for-name],SELECT[for-name],TEXTAREA[for-name]").each(function () {
                                            var newId = $(this).attr("for-id").replace(/\|_count_\|/gi, currentIndex);
                                            var newName = $(this).attr("for-name").replace(/\|_count_\|/gi, currentIndex);
                                            
                                            appendData += "<input type='hidden' id='"+newId+"' name='"+newName+"' value='"+$(this).val()+"' />";
                                        });
                                        
                                        var prettyData = "";
                                        
                                        $("#modalMetadataContainer").find("INPUT[for-pretty],SELECT[for-pretty]").each(function () {
                                            prettyData += $(this).attr("for-pretty").replace(/\|_aqui_\|/gi, $(this).val());
                                        });
                                        
                                        $("#prettyMetadataContainer").append("<li><a href='javascript:editElementMetadata("+currentIndex+")'>" + prettyData + "</a></li>");
                                        $("#metadatas-container .metadata[current-index='"+currentIndex+"']").append(appendData);
                                        
                                        $("#nextIOfMetaDatas").val(parseInt(currentIndex) + 1);
                                        
                                        $("#modalMetadataContainer").find("INPUT[for-name],SELECT[for-name],TEXTAREA[for-name]").val('');
                                        
                                        $("#formElement").submit();
                                        
					$("#modalMetadataContainer").dialog("close");
				}
			}
		]		
	});
	
})
var opened = null;
function launchFilemanager() {
    
    var baseRoute = $(".currentFileManagerRoute").val();
    
    opened = window.open("/wanaba/filemanager/index.html?exclusiveFolder="+baseRoute, "_blank", "width=1000,height=700");
}

this.callback = function (id, name) {
	$("#modalMetadataContainer #newScript").val(id);
	$("#modalMetadataContainer #newScriptLbl").val(name);
	opened.close();
}

function getIndexOfMetadataContainer(element) {
	var name = $(element).attr("name");
	return name ? parseInt(name.replace(/\D/gi, ''), 10) : -1;
}

function addMetadata() {
    $("#modalMetadataContainer").find(".newElement").val($("#currentIdElement").val());
    $("#modalMetadataContainer").dialog("open");
}

function editElementMetadata(index) {
    $("#metadatas-container .metadata[current-index='"+index+"']").find("INPUT").each(function () {
        
        $("#modalMetadataContainer").find("[for-name='"+$(this).attr("name").replace(/\d/gi, "|_count_|")+"']").val($(this).val());;
        
    });
    
    $("#modalMetadataContainer").dialog("open");
}

function delElementMetadata(index){
    if (confirm("Seguro??")) {
        
        var metadataId = $("#metadatas-container .metadata[current-index='"+index+"']").find(".id").val();
        var elementId = $("#currentIdElement").val();
        
        $.ajax(
                {
                    url: "/wanaba/element/deletemetadata/"+elementId+"/"+metadataId,
                    complete: function () {
                        location.reload();
                        
                    }
                }
            );
    }
}
function delElementDependency(index){
    if (confirm("Seguro??")) {
        
        var dependencyId = $("#dependencies-container LI.dependency[current-index='"+index+"']").find(".dependency").val();
        var elementId = $("#currentIdElement").val();
        
        $.ajax(
                {
                    url: "/wanaba/element/deletedependency/"+elementId+"/"+dependencyId,
                    complete: function () {
                        location.reload();
                        
                    }
                }
            );
    }
}
function setDependency(dependencyId, dependencyName) {
    var currentIndex = $("#nextIOfDependencies").val();
    if (dependencyId === $("#currentIdElement").val()) {
        alert("no se puede depender de uno mismo");
    } else if ($("#prettyDependenciesContainer").find("input[type='hidden'].dependency[value='"+dependencyId+"']").length > 0) {
        alert("Ya existe esta dependencia");
    } else {
        $("#prettyDependenciesContainer").append(
                "<li>" +
                    "<input type='hidden' name='dependencies["+currentIndex+"]' id='dependencies"+currentIndex+"' value='"+dependencyId+"' />" +
                    "<span>"+dependencyName+"</span>" +
                "</li>");

        $("#nextIOfDependencies").val(parseInt(currentIndex) + 1);
    }
    $("#modalDependenciesContainer").dialog("close");
}

function addDependency() {
    $("#modalDependenciesContainer").dialog("open");
}