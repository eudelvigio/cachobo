package com.rf.services;

import com.rf.data.entities.Site;
/**
 * Las interfaces de servicios se introducen en los controladores para seguir la filosofía spring de inversión de depndencias
 * @author mortas
 */
public interface ISiteService {

    Iterable<Site> listAllSites();

    Site getSiteById(Integer id);

    Site saveSite(Site site);

    void deleteSite(Integer id);

    Site getSiteByName(String name);
}
