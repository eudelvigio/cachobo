package com.rf.services.published_services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rf.data.entities.published_entities.PublishedSite;
import com.rf.data.repositories.published_repositories.PublishedSiteRepository;
/**
 * Los servicios spring implementan la interfaz correspondiente haciendo uso del repositorio, y exponen métodos para los controladores
 * @author mortas
 */
@Service
public class PublishedSiteService implements IPublishedSiteService {

    private PublishedSiteRepository siteRepository;

    @Autowired
    public void setProductRepository(PublishedSiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    @Override
    public Iterable<PublishedSite> listAllSites() {
        return siteRepository.findAll();
    }

    @Override
    public PublishedSite getSiteById(Integer id) {
        return siteRepository.findOne(id);
    }

    @Override
    public PublishedSite saveSite(PublishedSite site) {
        return siteRepository.save(site);
    }

    @Override
    public void deleteSite(Integer id) {
        siteRepository.delete(id);
    }

    @Override
    public PublishedSite getSiteByName(String name) {
        return siteRepository.getByName(name);
    }

    @Override
    public PublishedSite getPublishedSiteByparent_site_id(Integer parent_site_id) {
        return siteRepository.getByParentSiteId(parent_site_id);
    }

}
