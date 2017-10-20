package com.rf.services.filemanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rf.data.entities.filemanager.Folder;
import com.rf.data.repositories.FileManager.FolderRepository;

/**
 * Los servicios spring implementan la interfaz correspondiente haciendo uso del repositorio, y exponen métodos para los controladores
 * @author mortas
 */
@Service
public class FolderService implements IFolderService {

    private FolderRepository folderRepository;

    @Autowired
    public void setFolderRepository(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    public void setFolderRepositoryFromOutside(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    @Override
    public Iterable<Folder> listAllFolders() {
        return folderRepository.findAll();

    }

    @Override
    public Folder getFolderById(Integer id) {
        return folderRepository.findOne(id);
    }

    @Override
    public Folder saveFolder(Folder folder) {
        return folderRepository.save(folder);

    }

    @Override
    public void deleteFolder(Integer id) {
        folderRepository.delete(id);
    }

    @Override
    public Folder findFolder(Folder folder) {
        return folderRepository.findByNameAndParentFolderId(folder.getName(), folder.getId());
    }

    @Override
    public Folder findFolderByName(String name) {
        return folderRepository.findByName(name);
    }

    @Override
    public Iterable<Folder> findFoldersByParent(Folder f) {
        return folderRepository.findByParentFolderId(f.getId());
    }

}
