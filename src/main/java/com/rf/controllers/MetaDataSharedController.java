/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rf.controllers;

import com.rf.data.entities.ElementMetaData;
import com.rf.data.entities.PageMetadata;
import com.rf.data.entities.SiteMetadata;
import com.rf.data.entities.filemanager.File;
import com.rf.data.entities.filemanager.Folder;
import com.rf.data.enums.EnumMetadata;
import com.rf.data.enums.EnumWhere;
import com.rf.filemanager.FileManager;
import com.rf.services.MediaService;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Esta clase tiene métodos estáticos compartidos para el proceso de los metadatos de elementos
 * @author mortas
 */
public class MetaDataSharedController {

    /**
     * Este método procesará los metadatas cuando se han insertado un elemento en una página
     * @param pmd PageMetadata insertado
     * @param fStorage Folder a fStorage correspondiente
     * @param fCss Folder a fCss correspondiente
     * @param fScripts Folder a fScripts correspondiente
     * @param fViews Folder a fViews correspondiente
     * @param fm Instancia del fileManager
     * @param mediaService Servicio para guardar en Medias
     * @param uploads_directory Directorio base dónde se suben los ficheros
     */
    public static void processPageMetadata(PageMetadata pmd, Folder fStorage, Folder fCss, Folder fScripts, Folder fViews, FileManager fm, MediaService mediaService, String uploads_directory) {
        //sobreescriturta para añadir el booleano que haga que se actualice en vez de crear uno nuevo

        processPageMetadata(pmd, fStorage, fCss, fScripts, fViews, fm, mediaService, uploads_directory, false);

    }

    /**
     * Este método procesará los metadatas cuando se ha insertado un elemento en una página o cuando se ha actualizado dicho elemento desde los elementos
     * @param pmd PageMetadata insertado
     * @param fStorage Folder a fStorage correspondiente
     * @param fCss Folder a fCss correspondiente
     * @param fScripts Folder a fScripts correspondiente
     * @param fViews Folder a fViews correspondiente
     * @param fm Instancia del fileManager
     * @param mediaService Servicio para guardar en Medias
     * @param uploads_directory Directorio base dónde se suben los ficheros
     * @param updateFilesInsteadOfRecreate Si es true, se actualizarán los valores de los ficheros ya subidos, en vez de volver a crearlos, cosa que ocurre si es false
     */
    public static void processPageMetadata(PageMetadata pmd, Folder fStorage, Folder fCss, Folder fScripts, Folder fViews, FileManager fm, MediaService mediaService, String uploads_directory, Boolean updateFilesInsteadOfRecreate) {
        ElementMetaData em = pmd.getElementMetadata();

        if (em != null) {
            pmd.setKey(em.getKey());
            switch (em.getKey()) {
                case CSS:
                    fStorage = fCss;
                    break;
                case JS:
                    fStorage = fScripts;
                    break;
                case HTML:
                    pmd.setValue(StringEscapeUtils.unescapeHtml4(pmd.getValue()));
                    fStorage = fViews;
                    break;
            }
            if (em.getStorage() != null) {
                switch (em.getStorage()) {
                    case EXTERNAL:
                        //Siendo external, se cargará en la página como
                        //Si es css, un link del tipo <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
                        //Si es js, un script del tipo <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
                        //A priori no debería poder ser html, pero si lo es, se insertará con un ng-include TODO
                        break;
                    case FILEMANAGER_NOT_CREATE:

                        if (updateFilesInsteadOfRecreate) {
                            if (pmd.getScript() != null) {
                                // Hay que copiar el contenido de em.getScript() a pmd.getScript, y nada mas
                                //no es necesario setear el value, por que el fichero será el mismo
                                java.io.File orig = new java.io.File(em.getScript().getFilePath());
                                java.io.File dest = new java.io.File(pmd.getScript().getFilePath());

                                try {
                                    FileUtils.copyFile(orig, dest);
                                } catch (IOException ex) {
                                    Logger.getLogger(MetaDataSharedController.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }
                        } else {
                            //Siendo de tipo filemanager_not_create, se copiará desde el filemanager a la carpeta específica del sitio
                            //y se insertará el fichero en la web, ya sea como link, script o include
                            pmd.setScript(getFileAfterCopyInFileManager(em, fm, fStorage));
                            pmd.setValue(pmd.getScript().getUrlPath());
                        }
                        break;
                    case FILEMANAGER_CREATE:
                        //Siendo de tipo filemanager_create, se creará un fichero temporal que guardaremos en la raíz del directorio uploads, luego se copiará al
                        //lugar deseado, para finalizar eliminando el temporal
                        pmd.setScript(getFileAfterCreateAndStoreInFileManager(em.getKey(), pmd.getValue(), fm, fStorage, uploads_directory));
                        pmd.setValue(pmd.getScript().getUrlPath());
                        break;
                    case INLINE:
                        //Siendo inline, aquí deberíamos haber acabado, ya que estará insertado en el html de la página o sitio
                        break;
                    default:
                        //Esto es raro... únicamente si fuese un servicio, pero no deberían poder ir por aquí los servicios
                        break;
                }
            }
        } else {
            //A priori será la vista, de momento solo guardamos
            pmd.setValue(StringEscapeUtils.unescapeHtml4(pmd.getValue()));
            //parece buena idea darle el key al pagemetadata 20170325
            pmd.setKey(EnumMetadata.HTML);

            pmd.setScript(getFileAfterCreateAndStoreInFileManager(EnumMetadata.HTML, pmd.getValue(), fm, fViews, uploads_directory));
            pmd.setValue(pmd.getScript().getUrlPath());
        }

    }

    /**
     * Este método procesará los metadatas cuando se ha insertado un elemento en un sitio
     * @param smd SiteMetadata insertado
     * @param fStorage Folder a fStorage correspondiente
     * @param fCss Folder a fCss correspondiente
     * @param fScripts Folder a fScripts correspondiente
     * @param fViews Folder a fViews correspondiente
     * @param fm Instancia del fileManager
     * @param mediaService Servicio para guardar en Medias
     * @param uploads_directory Directorio base dónde se suben los ficheros
     */
    public static void processSiteMetadata(SiteMetadata smd, Folder fStorage, Folder fCss, Folder fScripts, Folder fViews, FileManager fm, MediaService mediaService, String uploads_directory) {
        //sobreescriturta para añadir el booleano que haga que se actualice en vez de crear uno nuevo
        processSiteMetadata(smd, fStorage, fCss, fScripts, fViews, fm, mediaService, uploads_directory, false);

    }

    /**
     * Este método procesará los metadatas cuando se han insertado en un sitio o cuando se han actualizado desde los elementos
     * @param smd SiteMetadata insertado
     * @param fStorage Folder a fStorage correspondiente
     * @param fCss Folder a fCss correspondiente
     * @param fScripts Folder a fScripts correspondiente
     * @param fViews Folder a fViews correspondiente
     * @param fm Instancia del fileManager
     * @param mediaService Servicio para guardar en Medias
     * @param uploads_directory Directorio base dónde se suben los ficheros
     * @param updateFilesInsteadOfRecreate Si es true, se actualizarán los valores de los ficheros ya subidos, en vez de volver a crearlos, cosa que ocurre si es false
     */
    public static void processSiteMetadata(SiteMetadata smd, Folder fStorage, Folder fCss, Folder fScripts, Folder fViews, FileManager fm, MediaService mediaService, String uploads_directory, Boolean updateFilesInsteadOfRecreate) {

        ElementMetaData em = smd.getElementMetadata();

        if (em != null) {

            switch (em.getKey()) {
                case CSS:
                    fStorage = fCss;
                    break;
                case JS:
                    fStorage = fScripts;
                    break;
                case HTML:
                    smd.setValue(StringEscapeUtils.unescapeHtml4(smd.getValue()));
                    fStorage = fViews;
                    break;
            }

            switch (em.getStorage()) {
                case EXTERNAL:
                    //Siendo external, se cargará en la página como
                    //Si es css, un link del tipo <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
                    //Si es js, un script del tipo <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
                    //A priori no debería poder ser html, pero si lo es, se insertará con un ng-include TODO
                    break;
                case FILEMANAGER_NOT_CREATE:
                    //Siendo de tipo filemanager_not_create, se copiará desde el filemanager a la carpeta específica del sitio
                    //y se insertará el fichero en la web, ya sea como link, script o include
                    if (updateFilesInsteadOfRecreate) {
                        if (smd.getScript() != null) {
                            // Hay que copiar el contenido de em.getScript() a smd.getScript, y nada mas
                            //no es necesario setear el value, por que el fichero será el mismo
                            java.io.File orig = new java.io.File(em.getScript().getFilePath());
                            java.io.File dest = new java.io.File(smd.getScript().getFilePath());

                            try {
                                FileUtils.copyFile(orig, dest);
                            } catch (IOException ex) {
                                Logger.getLogger(MetaDataSharedController.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                    } else {

                        smd.setScript(em.getScript());
                        smd.setValue(em.getScript().getUrlPath());

                        /*smd.setScript(getFileAfterCopyInFileManager(em, fm, fStorage));
                            smd.setValue(smd.getScript().getUrlPath());*/
                    }

                    break;
                case FILEMANAGER_CREATE:
                    //Siendo de tipo filemanager_create, se creará un fichero temporal que guardaremos en la raíz del directorio uploads, luego se copiará al
                    //lugar deseado, para finalizar eliminando el temporal
                    smd.setScript(getFileAfterCreateAndStoreInFileManager(em.getKey(), smd.getValue(), fm, fStorage, uploads_directory));
                    smd.setValue(smd.getScript().getUrlPath());
                    break;
                case INLINE:
                    //Siendo inline, aquí deberíamos haber acabado, ya que estará insertado en el html de la página o sitio
                    break;
                default:
                    //Esto es raro... únicamente si fuese un servicio, pero no deberían poder ir por aquí los servicios
                    break;
            }
        } else {
            if (smd.getKey() == EnumMetadata.HTML) {
                //A priori será o header o footer, ya vemos qué hacer
                smd.setValue(StringEscapeUtils.unescapeHtml4(smd.getValue()));
                //parece buena idea darle el key al sitemetadata 20170325
                smd.setKey(EnumMetadata.HTML);
                smd.setScript(getFileAfterCreateAndStoreInFileManager(EnumMetadata.HTML, smd.getValue(), fm, fViews, uploads_directory));
                smd.setValue(smd.getScript().getUrlPath());
            } else if (smd.getKey() == EnumMetadata.CSS) {

                //parece buena idea darle el key al sitemetadata 20170325
                smd.setKey(EnumMetadata.CSS);
                smd.setScript(getFileAfterCreateAndStoreInFileManager(EnumMetadata.CSS, smd.getValue(), fm, fCss, uploads_directory));
                smd.setValue(smd.getScript().getUrlPath());

                //Añadimos el where al tipo css, para que no se nos duplique
                smd.setWhere(EnumWhere.LIBRARY);
            }

        }
    }

    /**
     * Este método guarda el valor de value en un fichero temporal, para luego copiarlo en la carpeta fStorage y eliminarlo, finalizando devolviendo 
     * la representación en BBDD del fichero
     * @param emd ElementMetadata que está provocando la creación del fichero
     * @param value Valor a insertar en el fichero
     * @param fm Instancia inicializada del filemanager
     * @param fStorage Carpeta (representación en BBDD) en la que se guardará el fichero
     * @param uploads_directory Directorio desde el que cuelgan todos los uploads
     * @return 
     */
    private static File getFileAfterCreateAndStoreInFileManager(EnumMetadata emd, String value, FileManager fm, Folder fStorage, String uploads_directory) {
        java.io.File file = new java.io.File(uploads_directory + java.io.File.separator + emd.toString() + emd.getExtension());
        try {

            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            Writer out = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));

            //FileWriter fw = new FileWriter(file);
            out.write(value);
            out.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        File scriptFileTo = fm.createAndSaveFileFromCode(file, fStorage.getName());
        file.delete();

        return scriptFileTo;
    }

    /**
     * Este método coge el fichero que hay en el ElementMetadata, y lo copia al directorio (representación en BBDD) fStorage, sobreescribiéndolo en caso de que existiese
     * @param emd ElementMetadata cuyo fichero se va a copiar
     * @param fm Instancia inicializada del filemanager
     * @param fStorage Carpeta (BBDD) en la que se guardará la copia
     * @return El fichero (representación BBDD) copiado
     */
    private static File getFileAfterCopyInFileManager(ElementMetaData emd, FileManager fm, Folder fStorage) {
        //Si soy un fichero js o css o librería que se debe añadir como librería, -copio el original y se lo asigno a mi sitemetadata
        File scriptFileFrom = emd.getScript();
        if (scriptFileFrom != null) {
            File scriptFileTo = fm.copyFileToFolderOrGetCurrentFileAfterOverwriteIt(scriptFileFrom, fStorage);

            return scriptFileTo;
        } else {
            return null;
        }
    }

}
