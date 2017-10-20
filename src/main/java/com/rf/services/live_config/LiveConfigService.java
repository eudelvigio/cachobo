/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rf.services.live_config;

import com.rf.data.entities.Site;
import com.rf.data.entities.live_config.LiveConfig;
import com.rf.data.repositories.live_config.LiveConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Los servicios spring implementan la interfaz correspondiente haciendo uso del repositorio, y exponen métodos para los controladores
 * @author mortas
 */
@Service
public class LiveConfigService implements ILiveConfigService {

    private LiveConfigRepository liveConfigRepository;

    @Autowired
    public void LiveConfigRepository(LiveConfigRepository liveConfigRepository) {
        this.liveConfigRepository = liveConfigRepository;
    }

    @Override
    public Iterable<com.rf.data.entities.live_config.LiveConfig> listAllLiveConfigs() {
        return liveConfigRepository.findAll();
    }

    @Override
    public com.rf.data.entities.live_config.LiveConfig getLiveConfigById(Integer id) {
        return liveConfigRepository.findOne(id);
    }

    @Override
    public Iterable<com.rf.data.entities.live_config.LiveConfig> getLiveConfigsBySite(Site site) {
        return liveConfigRepository.findAllBySite(site);
    }

    @Override
    public com.rf.data.entities.live_config.LiveConfig saveLiveConfig(com.rf.data.entities.live_config.LiveConfig lc) {
        return liveConfigRepository.save(lc);
    }

    @Override
    public LiveConfig getLiveConfigBySiteAndKey(Site site, String key) {
        return liveConfigRepository.findBySiteAndKey(site, key);
    }

}
