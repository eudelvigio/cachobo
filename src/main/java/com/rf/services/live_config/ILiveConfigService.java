/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rf.services.live_config;

import com.rf.data.entities.Site;
import com.rf.data.entities.live_config.LiveConfig;

/**
 * Las interfaces de servicios se introducen en los controladores para seguir la filosofía spring de inversión de depndencias
 * @author mortas
 */
public interface ILiveConfigService {

    Iterable<LiveConfig> listAllLiveConfigs();

    LiveConfig getLiveConfigById(Integer id);

    LiveConfig getLiveConfigBySiteAndKey(Site site, String key);

    Iterable<LiveConfig> getLiveConfigsBySite(Site site);

    LiveConfig saveLiveConfig(LiveConfig lc);

}
