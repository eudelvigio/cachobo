package com.rf.services.filemanager;

import com.rf.data.entities.filemanager.Folder;
import com.rf.data.repositories.FileManager.FolderRepository;
/**
 * Las interfaces de servicios se introducen en los controladores para seguir la filosofía spring de inversión de depndencias
 * @author mortas
 */
public interface IFolderService {

    void setFolderRepositoryFromOutside(FolderRepository folderRepository);

    Iterable<Folder> listAllFolders();

    Folder getFolderById(Integer id);

    Folder saveFolder(Folder folder);

    Folder findFolder(Folder folder);

    void deleteFolder(Integer id);

    Folder findFolderByName(String name);

    Iterable<Folder> findFoldersByParent(Folder f);
}
