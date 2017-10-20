package com.rf.services.filemanager;

import com.rf.data.entities.filemanager.File;
import com.rf.data.entities.filemanager.Folder;
import com.rf.data.repositories.FileManager.FileRepository;
/**
 * Las interfaces de servicios se introducen en los controladores para seguir la filosofía spring de inversión de depndencias
 * @author mortas
 */
public interface IFileService {

    void setFileRepositoryFromOutside(FileRepository fileRepository);

    Iterable<File> listAllFiles();

    File getFileById(Integer id);

    File saveFile(File file);

    void deleteFile(Integer id);

    File getFileByNameAndFolder(String name, Folder f);

    File getFileByName(String name);

    Iterable<File> getFilesByFolder(Folder f);

    Iterable<File> getRootFiles();
}
