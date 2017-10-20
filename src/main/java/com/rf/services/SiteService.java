package com.rf.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rf.data.entities.Site;
import com.rf.data.repositories.SiteRepository;

/**
 * Los servicios spring implementan la interfaz correspondiente haciendo uso del repositorio, y exponen métodos para los controladores
 * @author mortas
 */
@Service
public class SiteService implements ISiteService {

    private SiteRepository siteRepository;

    @Autowired
    public void setProductRepository(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    @Override
    public Iterable<Site> listAllSites() {
        return siteRepository.findAll();
    }

    @Override
    public Site getSiteById(Integer id) {
        return siteRepository.findOne(id);
    }

    @Override
    public Site saveSite(Site site) {
        return siteRepository.save(site);
    }

    @Override
    public void deleteSite(Integer id) {
        siteRepository.delete(id);
    }

    @Override
    public Site getSiteByName(String name) {
        return siteRepository.getByName(name);
    }

}
