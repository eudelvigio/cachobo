package com.rf.services.published_services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rf.data.entities.published_entities.CachedMedia;
import com.rf.data.repositories.published_repositories.CachedMediaRepository;
/**
 * Los servicios spring implementan la interfaz correspondiente haciendo uso del repositorio, y exponen métodos para los controladores
 * @author mortas
 */
@Service
public class CachedMediaService implements ICachedMediaService {

    private CachedMediaRepository mediaRepository;

    @Autowired
    public void setMediaRepository(CachedMediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Override
    public Iterable<CachedMedia> listAllMedias() {
        return mediaRepository.findAll();
    }

    @Override
    public CachedMedia saveMedia(CachedMedia media) {

        return mediaRepository.save(media);
    }

    @Override
    public void deleteMedia(Integer id) {
        mediaRepository.delete(id);

    }

    @Override
    public CachedMedia getMediaByName(String name) {
        return mediaRepository.findByNameIgnoreCase(name);
    }

}
