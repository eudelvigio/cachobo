package com.rf.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rf.data.entities.Site;
import com.rf.data.entities.SiteMetadata;
import com.rf.data.enums.EnumMetadata;
import com.rf.data.repositories.SiteMetadataRepository;

/**
 * Los servicios spring implementan la interfaz correspondiente haciendo uso del repositorio, y exponen métodos para los controladores
 * @author mortas
 */
@Service
public class SiteMetadataService implements ISiteMetadataService {

    private SiteMetadataRepository siteMetadataRepository;

    @Autowired
    public void ElementMetaDataRepository(SiteMetadataRepository siteMetadataRepository) {
        this.siteMetadataRepository = siteMetadataRepository;
    }

    @Override
    public Iterable<SiteMetadata> listAllMetadataOfSite(Site s) {
        return siteMetadataRepository.findBySite(s);
    }

    @Override
    public Iterable<SiteMetadata> listAllSiteMetadata() {
        return siteMetadataRepository.findAll();
    }

    @Override
    public SiteMetadata getSiteMetadataById(Integer id) {
        return siteMetadataRepository.findOne(id);
    }

    @Override
    public SiteMetadata saveSiteMetadata(SiteMetadata pm) {
        return siteMetadataRepository.save(pm);
    }

    @Override
    public Iterable<SiteMetadata> saveSiteMetadatas(Iterable<SiteMetadata> apm) {
        return siteMetadataRepository.save(apm);
    }

    @Override
    public Iterable<SiteMetadata> listSiteMetaDataByKeyAndValue(EnumMetadata key, String value) {
        return siteMetadataRepository.findByKeyAndValue(key, value);
    }

}
