/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rf.controllers;

import com.rf.data.entities.userdata.ConscientUserData;
import com.rf.data.enums.EnumConscientUserDataTypes;
import com.rf.services.SiteService;
import com.rf.services.UserData.ConscientUserDataService;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

/**
 * Esta clase, sin acabar, era el germen para el acceso a datos del usuario, como pudieran ser los informes de apuntados a promociones
 * @author mortas
 */
@Controller
public class ReportsController {

    private ConscientUserDataService userdataService;

    @Autowired
    public void setUserDataService(ConscientUserDataService userdataService) {
        this.userdataService = userdataService;
    }

    private SiteService siteService;

    @Autowired
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    @RequestMapping("reports")
    public String reportsIndex(Model m) {

        m.addAttribute("Sites", siteService.listAllSites());

        m.addAttribute("ConscientUserDataTypes", EnumConscientUserDataTypes.values());

        return "reports/reports";
    }

    @RequestMapping("reports/listreports")
    public String reportsList(Model m, @RequestParam("site") String site, @RequestParam("service_type") EnumConscientUserDataTypes serviceType) {

        Iterable<ConscientUserData> userDatasForReport = userdataService.listDistinctUserDatasForReportsBySiteAndConscientUserDataType(site, serviceType);
        m.addAttribute("userDatasForReport", userDatasForReport);

        return "reports/listreports";
    }

    @RequestMapping("reports/downloadreport")
    @ResponseBody
    public void downloadReport(@RequestParam("kv_key") String kv_key, @RequestParam("site") String site, @RequestParam("service_type") EnumConscientUserDataTypes serviceType, HttpServletResponse response) {
        //ICsvBeanWriter beanWriter = null;
        ICsvBeanWriter beanWriter = null;

        response.setContentType("text/csv");
        response.setHeader("content-disposition", "attachment;filename=report.csv");
        try {
            beanWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.EXCEL_PREFERENCE);

            Iterable<ConscientUserData> userDatasForReport = userdataService.listUserDatasForReportsBySiteAndConscientUserDataTypeAndKey(site, serviceType, kv_key);

            String[] jeaders = {"userId", "key", "value", "creation", "site"};

            beanWriter.writeHeader(jeaders);

            for (ConscientUserData cud : userDatasForReport) {
                beanWriter.write(cud, jeaders);
            }

            beanWriter.close();

            //response.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex);
            if (beanWriter != null) {
                try {
                    beanWriter.close();
                } catch (IOException ex1) {
                    Logger.getLogger(ReportsController.class.getName()).log(Level.SEVERE, null, ex1);
                    beanWriter = null;
                }

            }
        }

    }

}
