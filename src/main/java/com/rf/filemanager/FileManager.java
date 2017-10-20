package com.rf.filemanager;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.springframework.web.multipart.MultipartFile;

import com.rf.data.dto.filemanager.AttributesJson;
import com.rf.data.dto.filemanager.DataJson;
import com.rf.data.entities.filemanager.Folder;
import com.rf.data.repositories.FileManager.FileRepository;
import com.rf.data.repositories.FileManager.FolderRepository;
import com.rf.services.filemanager.FileService;
import com.rf.services.filemanager.FolderService;
import com.rf.services.filemanager.IFileService;
import com.rf.services.filemanager.IFolderService;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * En la clase filemanager se controla el acceso a base de datos y al sistema de ficheros del sistema en que se aloja el wanaba
 * El front del filemanager se obtuvo del siguiente github https://github.com/servocoder/RichFilemanager, del cual se implementó el API REST que se encuentra
 * en la documentación del github. Esta clase tiene la implementación de las funciones, y bajo el package rest se encuentra la clase MediaUploader que controla el REST
 * @author mortas
 */
public class FileManager {

    public FileManager(
            FolderService folderService,
            FileService fileService,
            FolderRepository folderRepository,
            FileRepository fileRepository,
            String uploads_directory,
            String uploads_base_url
    ) {
        this.folderService = folderService;
        this.folderService.setFolderRepositoryFromOutside(folderRepository);
        this.uploads_base_url = uploads_base_url;
        this.uploads_directory = uploads_directory;

        this.fileService = fileService;
        this.fileService.setFileRepositoryFromOutside(fileRepository);

    }

    private IFolderService folderService;

    private IFileService fileService;

    private String uploads_directory;

    private String uploads_base_url;

    public DataJson getInitiate() {
        DataJson ret = new DataJson();
        AttributesJson aJson = new AttributesJson();

        aJson.setConfig(new Object[0]);

        String[] capabilities = new String[]{"select", "upload", "download", "rename", "copy", "move", "replace", "delete"};

        aJson.setCapabilities(capabilities);

        ret.setId("/");
        ret.setType("initiate");
        ret.setAttributes(aJson);

        return ret;

    }

    public DataJson getFile(String path) {
        DataJson ret = new DataJson();

        com.rf.data.entities.filemanager.File fileDB = getFileByPath(path);

        ret.setId(path);
        ret.setType("file");
        ret.setAttributes(getAttributesFromFile(fileDB));

        return ret;
    }

    private com.rf.data.entities.filemanager.File getFileByPath(String path) {
        String pathFolder = path.substring(0, path.lastIndexOf("/") + 1);
        String fileName = path.substring(path.lastIndexOf("/") + 1);

        Folder f = folderService.findFolderByName(pathFolder);

        com.rf.data.entities.filemanager.File fileDB = fileService.getFileByNameAndFolder(fileName, f);
        return fileDB;
    }

    public List<DataJson> getFolder(String path, String type) {
        List<DataJson> ret = new ArrayList<DataJson>();

        Folder f = folderService.findFolderByName(path);
        DataJson dJson = new DataJson();
        if (f != null) {
            Iterable<Folder> foldersInPath = folderService.findFoldersByParent(f);
            Iterable<com.rf.data.entities.filemanager.File> filesInPath = fileService.getFilesByFolder(f);

            for (Folder fInPath : foldersInPath) {
                dJson = new DataJson();
                dJson.setId(fInPath.getName());
                dJson.setType("folder");
                dJson.setAttributes(getAttributesFromFolder(fInPath));

                ret.add(dJson);
            }

            for (com.rf.data.entities.filemanager.File filePath : filesInPath) {
                dJson = new DataJson();
                dJson.setId(filePath.getParentFolder().getName() + filePath.getName());
                dJson.setType("file");
                dJson.setAttributes(getAttributesFromFile(filePath));

                ret.add(dJson);
            }
        } else {
            //Iterable<Folder> foldersInPath = folderService.findFoldersByParent(f);
            Iterable<com.rf.data.entities.filemanager.File> filesInPath = fileService.getRootFiles();
            for (com.rf.data.entities.filemanager.File filePath : filesInPath) {
                dJson = new DataJson();
                dJson.setId(filePath.getParentFolder().getName() + filePath.getName());
                dJson.setType("file");
                dJson.setAttributes(getAttributesFromFile(filePath));

                ret.add(dJson);
            }
        }

        return ret;
    }

    public DataJson addFolder(String path, String name) {
        Folder f = createFolder(path, name);

        DataJson dj = new DataJson();
        dj.setId(f.getName());
        dj.setType("folder");
        dj.setAttributes(getAttributesFromFolder(f));

        return dj;
    }

    public Folder createFolder(String path, String name) {
        Folder parentF = folderService.findFolderByName(path);

        Folder f = new Folder();
        f.setCreated(DateTime.now().toDate());
        f.setModified(DateTime.now().toDate());
        f.setName(parentF.getName() + name + "/");
        f.setParentFolder(parentF);

        folderService.saveFolder(f);
        return f;
    }

    public Folder createOrGetFolderIfExists(String path, String name) {
        Folder existo = folderService.findFolderByName(path + name + "/");
        if (existo != null) {
            return existo;
        } else {
            return createFolder(path, name);
        }
    }

    public com.rf.data.entities.filemanager.File createAndSaveFileFromUrl(String url, String path) {

        File f = null;
        com.rf.data.entities.filemanager.File fi = null;
        try {
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            f = new File(uploads_directory + fileName);

            String restOfUrl = url.replace(fileName, "");

            URL uri = new URL(restOfUrl + URLEncoder.encode(fileName, "UTF-8"));

            FileUtils.copyURLToFile(uri, f);
            fi = createAndSaveFileFromCode(f, path);

        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return fi;

    }

    public com.rf.data.entities.filemanager.File createAndSaveFileFromCode(File f, String path) {
        com.rf.data.entities.filemanager.File fi = createFileFromCode(f, path);

        fileService.saveFile(fi);

        return fi;
    }

    public com.rf.data.entities.filemanager.File createFileFromCode(File f, String path) {
        File dir = new File(uploads_directory + DateTime.now().getYear()
                + File.separator + DateTime.now().getMonthOfYear()
                + File.separator);
        dir.mkdirs();

        String fileName = f.getName();

        if (fileName.startsWith("\\")) {
            fileName = fileName.substring(1);
        }

        File file = new File(uploads_directory + DateTime.now().getYear()
                + File.separator + DateTime.now().getMonthOfYear()
                + File.separator + getRandomString(6) + fileName);

        com.rf.data.entities.filemanager.File fileDB = null;
        try {
            FileUtils.copyFile(f, file);

            Folder parentFolder = folderService.findFolderByName(path);

            fileDB = saveFileDB(file, parentFolder, file.getName());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fileDB;
    }

    public List<DataJson> upload(String path, MultipartFile[] mpFiles) throws IllegalStateException, IOException {
        List<DataJson> aret = new ArrayList<DataJson>();
        File file = null;

        for (MultipartFile mpFile : mpFiles) {
            DataJson ret = new DataJson();
            File dir = new File(uploads_directory + DateTime.now().getYear()
                    + File.separator + DateTime.now().getMonthOfYear()
                    + File.separator);
            dir.mkdirs();

            String fileName = mpFile.getOriginalFilename();

            if (fileName.startsWith("\\")) {
                fileName = fileName.substring(1);
            }

            file = new File(uploads_directory + DateTime.now().getYear()
                    + File.separator + DateTime.now().getMonthOfYear()
                    + File.separator + getRandomString(6) + fileName);

            Folder parentFolder = folderService.findFolderByName(path);

            mpFile.transferTo(file);

            String originalName = mpFile.getOriginalFilename();

            com.rf.data.entities.filemanager.File fileDB = saveFileDB(file, parentFolder, originalName);

            fileService.saveFile(fileDB);

            ret.setType("file");
            ret.setId(path + fileDB.getName());
            ret.setAttributes(getAttributesFromFile(fileDB));

            aret.add(ret);
        }

        return aret;
    }

    private com.rf.data.entities.filemanager.File saveFileDB(File file, Folder parentFolder, String originalName)
            throws IOException {
        String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);

        com.rf.data.entities.filemanager.File fileDB = new com.rf.data.entities.filemanager.File();

        fileDB.setCreated(DateTime.now().toDate());
        fileDB.setModified(DateTime.now().toDate());
        fileDB.setParentFolder(parentFolder);
        fileDB.setSize(file.length());
        fileDB.setName(originalName);
        fileDB.setFilePath(file.getAbsolutePath());
        fileDB.setUrlPath(uploads_base_url + DateTime.now().getYear() + "/" + DateTime.now().getMonthOfYear() + "/" + file.getName());

        if (extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("gif")) {

            BufferedImage bi = ImageIO.read(file);

            fileDB.setWidth(bi.getWidth());
            fileDB.setHeight(bi.getHeight());
        }
        return fileDB;
    }

    public DataJson rename(String oldFilename, String newFilename) {
        DataJson dj = new DataJson();

        Folder f = folderService.findFolderByName(oldFilename);
        if (f == null) {
            //Será fichero
            f = folderService.findFolderByName(oldFilename.substring(0, oldFilename.lastIndexOf("/") + 1));

            com.rf.data.entities.filemanager.File file = fileService.getFileByNameAndFolder(oldFilename.substring(oldFilename.lastIndexOf("/") + 1), f);

            file.setName(newFilename);
            fileService.saveFile(file);

            dj.setId(file.getParentFolder().getName() + newFilename);
            dj.setType("file");
            dj.setAttributes(getAttributesFromFile(file));

        } else {
            String oldDirName = f.getName();
            oldDirName = oldDirName.substring(0, oldDirName.lastIndexOf("/")); // Quitamos la ultima barra
            oldDirName = oldDirName.substring(0, oldDirName.lastIndexOf("/")); // Quitamos la penultima barra

            newFilename = oldDirName + newFilename + "/";

            f.setName(newFilename);

            folderService.saveFolder(f);

            checkChildsOfFolder(f, oldFilename, newFilename);

            dj.setId(newFilename);
            dj.setType("folder");
            dj.setAttributes(getAttributesFromFolder(f));

        }

        return dj;
    }

    public List<DataJson> move(String oldFilename, String newFilename) {
        List<DataJson> ret = new ArrayList<DataJson>();
        DataJson dj = new DataJson();

        com.rf.data.entities.filemanager.File fil = getFileByPath(oldFilename);

        Folder newFolder = folderService.findFolderByName(newFilename);

        fil.setParentFolder(newFolder);

        fileService.saveFile(fil);

        dj.setId(fil.getParentFolder().getName() + fil.getName());
        dj.setType("file");
        dj.setAttributes(getAttributesFromFile(fil));

        ret.add(dj);

        return ret;
    }

    public ByteArrayOutputStream getImage(String path, Boolean thumbnail) {
        try {
            com.rf.data.entities.filemanager.File fil = getFileByPath(path);

            File fi = new File(fil.getFilePath());

            BufferedImage bi = ImageIO.read(fi);

            ByteArrayOutputStream bao = new ByteArrayOutputStream();

            String extension = fi.getName().substring(fi.getName().lastIndexOf(".") + 1);

            ImageIO.write(bi, extension, bao);

            return bao;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    public DataJson editFile(String path) {
        try {
            com.rf.data.entities.filemanager.File fil = getFileByPath(path);

            File fi = new File(fil.getFilePath());

            FileReader fr = new FileReader(fi);

            BufferedReader br = new BufferedReader(fr);

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

            DataJson dj = new DataJson();

            dj.setId(fil.getParentFolder().getName() + fil.getName());
            dj.setType("file");

            AttributesJson aj = getAttributesFromFile(fil);

            aj.setContent(sb.toString());
            dj.setAttributes(aj);

            br.close();
            return dj;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    public DataJson savEditedFile(String path, String content) {
        try {
            com.rf.data.entities.filemanager.File fil = getFileByPath(path);

            File fi = new File(fil.getFilePath());

            FileWriter fw = new FileWriter(fi);
            fw.write(content);

            DataJson dj = new DataJson();

            dj.setId(fil.getParentFolder().getName() + fil.getName());
            dj.setType("file");

            AttributesJson aj = getAttributesFromFile(fil);

            dj.setAttributes(aj);

            fw.close();
            return dj;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    private void checkChildsOfFolder(Folder f, String oldFilename, String newFilename) {
        Iterable<Folder> childs = folderService.findFoldersByParent(f);
        if (childs != null) {
            for (Folder child : childs) {
                String oldName = child.getName();
                if (oldName.contains(oldFilename)) {
                    oldName.replace(oldFilename, newFilename);

                    folderService.saveFolder(child);

                    checkChildsOfFolder(child, oldFilename, newFilename);
                }

            }
        }

    }

    private AttributesJson getAttributesFromFile(com.rf.data.entities.filemanager.File fileDB) {
        AttributesJson attJson = new AttributesJson();

        attJson.setCapabilities(new String[]{"select", "upload", "download", "rename", "copy", "move", "replace", "delete"});
        attJson.setBytes(fileDB.getSize().toString());
        attJson.setCreated(fileDB.getCreated().toString());
        attJson.setExtension(fileDB.getName().substring(fileDB.getName().lastIndexOf(".") + 1));
        attJson.setHeight(fileDB.getHeight());
        attJson.setModified(fileDB.getModified().toString());
        attJson.setName(fileDB.getName());
        //attJson.setPath(fileDB.getFilePath());
        attJson.setPath(fileDB.getUrlPath());
        attJson.setProtectedField(0);
        attJson.setTimestamp(fileDB.getModified().getTime());
        attJson.setWidth(fileDB.getWidth());
        attJson.setBbddID(fileDB.getId());

        return attJson;
    }

    private AttributesJson getAttributesFromFolder(Folder f) {
        AttributesJson attJson = new AttributesJson();

        attJson.setCapabilities(new String[]{"select", "upload", "download", "rename", "copy", "move", "replace", "delete"});
        attJson.setCreated(f.getCreated().toString());
        attJson.setModified(f.getModified().toString());
        attJson.setName(f.getName().substring(0, f.getName().lastIndexOf("/") + 1));
        attJson.setPath(f.getName());
        attJson.setProtectedField(0);
        attJson.setTimestamp(f.getModified().getTime());

        attJson.setBbddID(f.getId());
        return attJson;
    }

    private String getRandomString(Integer length) {
        String ret = "";
        char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        Random r = new Random();
        for (Integer i = 0; i < length; i++) {
            ret += chars[r.nextInt(chars.length)];
        }

        return ret;

    }

    private void deleteRecursiveFromFolder(Folder f) {
        Iterable<Folder> subFolders = folderService.findFoldersByParent(f);
        Iterable<com.rf.data.entities.filemanager.File> fils = fileService.getFilesByFolder(f);

        for (com.rf.data.entities.filemanager.File fi : fils) {

            File fileFS = new File(fi.getFilePath());

            fileFS.delete();
            fileService.deleteFile(fi.getId());
        }

        for (Folder subFolder : subFolders) {
            deleteRecursiveFromFolder(subFolder);

        }

        folderService.deleteFolder(f.getId());
    }

    public DataJson delete(String path) {
        Folder f = folderService.findFolderByName(path);
        DataJson dj = new DataJson();
        if (f != null) {

            dj.setId(f.getName());
            dj.setType("folder");

            AttributesJson aj = getAttributesFromFolder(f);

            dj.setAttributes(aj);
            deleteRecursiveFromFolder(f);

        } else {
            com.rf.data.entities.filemanager.File fil = getFileByPath(path);
            File fi = new File(fil.getFilePath());

            dj.setId(fil.getParentFolder().getName() + fil.getName());
            dj.setType("file");

            AttributesJson aj = getAttributesFromFile(fil);

            dj.setAttributes(aj);

            fi.delete();
            fileService.deleteFile(fil.getId());
        }

        return dj;
    }

    public void writeFileTo(HttpServletResponse response, String path) {
        try {
            com.rf.data.entities.filemanager.File fil = getFileByPath(path);
            File fi = new File(fil.getFilePath());
            FileInputStream fis = new FileInputStream(fi);

            response.setHeader("Content-Disposition", "attachment; filename=" + fil.getName());

            byte[] c = new byte[1024];

            while (fis.read(c, 0, 1024) != -1) {
                response.getOutputStream().write(c);
                response.getOutputStream().flush();
            }
            response.getOutputStream().close();
            fis.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

    }

    public com.rf.data.entities.filemanager.File copyFileToFolderOrGetCurrentFile(com.rf.data.entities.filemanager.File scriptFile, Folder fScripts) {
        File dir = new File(uploads_directory + DateTime.now().getYear()
                + File.separator + DateTime.now().getMonthOfYear()
                + File.separator);
        dir.mkdirs();

        File destFileSO = new File(uploads_directory + DateTime.now().getYear()
                + File.separator + DateTime.now().getMonthOfYear()
                + File.separator + getRandomString(6) + scriptFile.getName());

        File origFileSO = new File(scriptFile.getFilePath());

        com.rf.data.entities.filemanager.File posibleExistente = fileService.getFileByNameAndFolder(scriptFile.getName(), fScripts);

        if (posibleExistente != null) {
            return posibleExistente;
        } else {
            try {
                FileUtils.copyFile(origFileSO, destFileSO);
                com.rf.data.entities.filemanager.File finalfile = saveFileDB(destFileSO, fScripts, scriptFile.getName());

                finalfile = fileService.saveFile(finalfile);
                return finalfile;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
    }

    public com.rf.data.entities.filemanager.File copyFileToFolderOrGetCurrentFileAfterOverwriteIt(com.rf.data.entities.filemanager.File scriptFile, Folder fScripts) {
        File dir = new File(uploads_directory + DateTime.now().getYear()
                + File.separator + DateTime.now().getMonthOfYear()
                + File.separator);
        dir.mkdirs();

        File destFileSO = new File(uploads_directory + DateTime.now().getYear()
                + File.separator + DateTime.now().getMonthOfYear()
                + File.separator + getRandomString(6) + scriptFile.getName());

        File origFileSO = new File(scriptFile.getFilePath());

        com.rf.data.entities.filemanager.File posibleExistente = fileService.getFileByNameAndFolder(scriptFile.getName(), fScripts);

        if (posibleExistente != null) {
            return posibleExistente;
        } else {
            try {
                FileUtils.copyFile(origFileSO, destFileSO);
                com.rf.data.entities.filemanager.File finalfile = saveFileDB(destFileSO, fScripts, scriptFile.getName());

                finalfile = fileService.saveFile(finalfile);
                return finalfile;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
    }

}
