package com.rf.services;

import com.rf.data.entities.Site;
import com.rf.data.entities.SiteSensitiveDatas;
/**
 * Las interfaces de servicios se introducen en los controladores para seguir la filosofía spring de inversión de depndencias
 * @author mortas
 */
public interface ISiteSensitiveDatasService {

    SiteSensitiveDatas getBySite(Site s);

    SiteSensitiveDatas save(SiteSensitiveDatas ssd);
}
