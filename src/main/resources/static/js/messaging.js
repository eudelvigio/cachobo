function anadeUsuarioALista(val) {
    
    if (!val) val = "";
    
    var currentIndex = $("#numeroelementosenlistadeusuarios").val();
    $("#listaUsuarios").append("<input ondblclick='borraUsuario(event)' name='users["+currentIndex+"]' value='"+val+"' />");
    
    $("#numeroelementosenlistadeusuarios").val(parseInt(currentIndex) + 1);
}

function borraUsuario(event) {
    $(event.target).remove();
    var iNumInArray = 0;
    $("#listaUsuarios").find("input").each(function () {
        
        $(this).attr("name", "users["+iNumInArray+"]");
        $(this).attr("id", "users"+iNumInArray+"");
        iNumInArray++;
    });
    $("#numeroelementosenlistadeusuarios").val(iNumInArray);
    
}

function limpiaLista() {
    $("#listaUsuarios").html("");
    $("#numeroelementosenlistadeusuarios").val(0);
}

function anadeUsuariosDesdeFichero(event) {
    var input = event.target;

    var reader = new FileReader();
    reader.onload = function(){
        var text = reader.result;

        var lineas = text.split("\n");

        for (var i = 0; i < lineas.length; i++) {
            anadeUsuarioALista(lineas[i]);
        }
    };
    reader.readAsText(input.files[0]);
    
}