package com.rf.services.published_services;

import com.rf.data.entities.published_entities.CachedMedia;
/**
 * Las interfaces de servicios se introducen en los controladores para seguir la filosofía spring de inversión de depndencias
 * @author mortas
 */
public interface ICachedMediaService {

    Iterable<CachedMedia> listAllMedias();

    CachedMedia saveMedia(CachedMedia media);

    CachedMedia getMediaByName(String name);

    void deleteMedia(Integer id);
}
