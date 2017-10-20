/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

$(document).ready(function () {
    $("#downloadReportBtn").click(function () {
        
        var site = getParameterByName("site");
        var serviceType = getParameterByName("service_type");
        
        var report = $("#reportSelector").val();
        
        if (site != "" && serviceType != "" && report != "") {
            
            window.open("/wanaba/reports/downloadreport?site=" + site + "&service_type=" + serviceType + "&kv_key=" + report);
            
            
        }
        
        
        
    });
    
    
    
});