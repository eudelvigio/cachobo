package com.rf.services.published_services;

import com.rf.data.entities.published_entities.PublishedSite;
/**
 * Las interfaces de servicios se introducen en los controladores para seguir la filosofía spring de inversión de depndencias
 * @author mortas
 */
public interface IPublishedSiteService {

    Iterable<PublishedSite> listAllSites();

    PublishedSite getSiteById(Integer id);

    PublishedSite saveSite(PublishedSite site);

    void deleteSite(Integer id);

    PublishedSite getSiteByName(String name);

    PublishedSite getPublishedSiteByparent_site_id(Integer parent_site_id);
}
