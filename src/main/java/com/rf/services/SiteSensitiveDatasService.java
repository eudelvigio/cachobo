package com.rf.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rf.data.entities.Site;
import com.rf.data.entities.SiteSensitiveDatas;
import com.rf.data.repositories.SiteSensitiveDatasRepository;

/**
 * Los servicios spring implementan la interfaz correspondiente haciendo uso del repositorio, y exponen métodos para los controladores
 * @author mortas
 */
@Service
public class SiteSensitiveDatasService implements ISiteSensitiveDatasService {

    private SiteSensitiveDatasRepository siteSensitiveDatasRepository;

    @Autowired
    public void ElementMetaDataRepository(SiteSensitiveDatasRepository siteSensitiveDatasRepository) {
        this.siteSensitiveDatasRepository = siteSensitiveDatasRepository;
    }

    @Override
    public SiteSensitiveDatas getBySite(Site s) {
        return siteSensitiveDatasRepository.getBySite(s);
    }

    @Override
    public SiteSensitiveDatas save(SiteSensitiveDatas ssd) {
        return siteSensitiveDatasRepository.save(ssd);
    }

}
