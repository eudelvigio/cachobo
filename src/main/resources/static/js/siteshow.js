$(document).ready(function () {
    if (window.liveConfigs.length > 0) {
        
        
        
        
        for (var i = 0; i < window.liveConfigs.length; i++) {
            $("#" + window.liveConfigs[i].key).val(window.liveConfigs[i].value);
        }
        
        
        
        
    }
    
    
});


function liveConfigurator() {
    
    var liveConfigs = [];
    var siteId = $("#site_id").html();
        
    $("#liveConfigsContainer").find("li").each(function () {
        var key = $(this).find("select").attr("id");
        var value = $(this).find("select").val();
        
        liveConfigs.push({"id": "", "key": key, "value": value, "site_id": parseInt(siteId)});
    });
    
    $.ajax(
        {
            url: "/wanaba/site/configs",
            data: JSON.stringify(liveConfigs),
            method: "POST",
            headers: { 
                'Accept': 'application/json',
                'Content-Type': 'application/json' 
            },
            dataType: 'json',
            complete: function () {
                alert("seteado");
                location.reload();
            }
        }
    );
    
    
    console.log(liveConfigs);
}