package com.rf.services.filemanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rf.data.entities.filemanager.File;
import com.rf.data.entities.filemanager.Folder;
import com.rf.data.repositories.FileManager.FileRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Los servicios spring implementan la interfaz correspondiente haciendo uso del repositorio, y exponen métodos para los controladores
 * @author mortas
 */
@Service
public class FileService implements IFileService {

    private FileRepository fileRepository;

    @Autowired
    public void setFileRepository(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public void setFileRepositoryFromOutside(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public Iterable<File> listAllFiles() {
        return fileRepository.findAll();

    }

    @Override
    public File getFileById(Integer id) {
        return fileRepository.findOne(id);
    }

    @Override
    public File saveFile(File file) {
        //Si el fichero es nuevo (o no), deberíamos comprobar si existe alguno ya con el mismo nombre en la misma ruta
        //En el caso que exista otro del mismo nombre en el mismo folder, le cambiamos al antiguo el nombre para evitar que se den las repeticiones bajo el mismo folder
        File auxFile = getFileByNameAndFolder(file.getName(), file.getParentFolder());

        if (auxFile != null && (file.getId() == null || !Objects.equals(file.getId(), auxFile.getId()))) {
            auxFile.setName("old_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + RandomStringUtils.randomAlphabetic(5) + auxFile.getName());
            fileRepository.save(auxFile);
        }

        //}
        return fileRepository.save(file);

    }

    @Override
    public void deleteFile(Integer id) {
        fileRepository.delete(id);
    }

    @Override
    public File getFileByNameAndFolder(String name, Folder folder) {
        return fileRepository.getByNameAndParentFolder(name, folder);
    }

    @Override
    public Iterable<File> getFilesByFolder(Folder f) {
        return fileRepository.getByParentFolder(f);
    }

    @Override
    public Iterable<File> getRootFiles() {
        return fileRepository.getByParentFolder(null);
    }

    @Override
    public File getFileByName(String name) {
        return fileRepository.getByName(name);
    }

}
