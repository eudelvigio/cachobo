package com.rf.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rf.data.entities.MediaData;
import com.rf.data.repositories.MediaDataRepository;

/**
 * Los servicios spring implementan la interfaz correspondiente haciendo uso del repositorio, y exponen métodos para los controladores
 * @author mortas
 */
@Service
public class MediaDataService implements IMediaDataService {

    private MediaDataRepository mediaDataRepository;

    @Autowired
    public void setMediaDataRepository(MediaDataRepository mediaDataRepository) {
        this.mediaDataRepository = mediaDataRepository;
    }

    @Override
    public Iterable<MediaData> listAllMedias() {
        return mediaDataRepository.findAll();
    }

    @Override
    public MediaData getMediaDataById(Integer id) {
        return mediaDataRepository.findOne(id);
    }

    @Override
    public MediaData saveMediaData(MediaData mediaData) {
        return mediaDataRepository.save(mediaData);
    }

    @Override
    public void deleteMediaData(Integer id) {
        mediaDataRepository.delete(id);
    }

}
