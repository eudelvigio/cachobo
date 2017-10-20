/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function () {
    $("#goToReportBtn").click(function () {
        
        var site = $("#site").val();
        var serviceType = $("#service_type").val();
        
        if (site != "" && serviceType != "") {
            
            location.href = "/wanaba/reports/listreports?site=" + site + "&service_type=" + serviceType;
            
            
        }
        
        
        
    });
    
    
    
});