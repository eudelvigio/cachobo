package com.rf.services;

import com.rf.data.entities.MediaData;
/**
 * Las interfaces de servicios se introducen en los controladores para seguir la filosofía spring de inversión de depndencias
 * @author mortas
 */
public interface IMediaDataService {

    Iterable<MediaData> listAllMedias();

    MediaData getMediaDataById(Integer id);

    MediaData saveMediaData(MediaData mediaData);

    void deleteMediaData(Integer id);
}
