package com.rf.shared;

import com.rf.controllers.SiteController;
import com.rf.data.entities.SiteSensitiveDatas;
import com.rf.data.enums.EnumMetadata;
import com.rf.data.enums.EnumWhere;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.commons.io.IOUtils;

/**
 * Esta clase contiene una serie de métodos estáticos para ayudar con la tarea de la manipulación de los ficheros en el momento de las generaciones y publicaciones 
 * de los sitios
 * @author mortas
 */
public class SharedFileUtils {

    /**
     * Esta función cambia la url de un fichero, venido desde un metadata, para el momento de la subida de los sitios.
     * La función chequea si la url que viene en la variable file contiene la ruta de subidas públicas del wanaba. En caso afirmativo, se trata de 
     * un fichero generado por un metadato de un sitio o página, por lo cual cambiamos la ruta del mismo a apuntar a una serie de carpetas basadas
     * en varios enums de la aplicación, dentro del servidor en el que se va a publicar el sitio. En caso de que no contenga el valor de la ruta pública
     * del wanaba, se asume que es algo como el script de integración de MT, que siempre vendrá desde fuera
     * @param file URL del fichero a comprobar
     * @param key EnumMetadata con el que se generará la primera parte de la ruta bajo el nuevo dominio
     * @param where EnumWhere con el que se generará la segunda parte de la ruta bajo el nuevo dominio
     * @param baseDomain Dominio base en el que se alojará el sitio
     * @param siteController referencia al controlador de sitio para obtener la url base de uploads del wanaba
     * @return ruta del fichero cambiada
     */
    public static String changeSingleRoute(String file, EnumMetadata key, EnumWhere where, String baseDomain, SiteController siteController) {
        if (file.contains(siteController.uploads_base_url)) {
            file = file.substring(file.lastIndexOf("/") + 1);
            file = baseDomain + "/" + key.name() + "_" + where.name() + "/" + file;
        }
        return file;
    }

    /**
     * Esta función cambia la url de una lista fichero, venidos desde varios metadata, para el momento de la subida de los sitios.
     * La función chequea si la url que viene en la variable file contiene la ruta de subidas públicas del wanaba. En caso afirmativo, se trata de 
     * un fichero generado por un metadato de un sitio o página, por lo cual cambiamos la ruta del mismo a apuntar a una serie de carpetas basadas
     * en varios enums de la aplicación, dentro del servidor en el que se va a publicar el sitio. En caso de que no contenga el valor de la ruta pública
     * del wanaba, se asume que es algo como el script de integración de MT, que siempre vendrá desde fuera
     * @param fileCollection lista de URLs de fichero a comprobar
     * @param key EnumMetadata con el que se generará la primera parte de la ruta bajo el nuevo dominio
     * @param where EnumWhere con el que se generará la segunda parte de la ruta bajo el nuevo dominio
     * @param baseDomain Dominio base en el que se alojará el sitio
     * @param siteController referencia al controlador de sitio para obtener la url base de uploads del wanaba
     * @return lista de rutas de ficheros cambiadas
     */
    public static List<String> changeRouteScript(List<String> fileCollection, EnumMetadata key, EnumWhere where, String baseDomain, SiteController siteController) {
        List<String> returnFileCollection = new ArrayList<>();
        for (String file : fileCollection) {
            returnFileCollection.add(changeSingleRoute(file, key, where, baseDomain, siteController));
        }
        return returnFileCollection;
    }

    /**
     * Esta función se encarga de copiar un fichero de metadatos a un directorio temporal, desde dónde apache camel podrá recogerlo y replicarlo
     * en el servidor destino
     * @param file Ruta en el sistema de ficheros del servidor del fichero a copiar
     * @param key EnumMetadata con el que se generará la primera parte de la ruta bajo el directorio temporal
     * @param where EnumWhere con el que se generará la segunda parte de la ruta bajo el directorio temporal
     * @param tempDirectory directorio temporal en que dejar el fichero
     * @throws IOException Problemas con ficheros, IO y eso
     */
    public static void copyToTempDirectory(String file, EnumMetadata key, EnumWhere where, File tempDirectory) throws IOException {

        File currentDir = null;
        if (where != null && key != null) {
            currentDir = new File(tempDirectory.getAbsoluteFile() + File.separator + key.name() + "_" + where.name() + File.separator);
        } else {
            currentDir = new File(tempDirectory.getAbsoluteFile() + File.separator);
        }
        currentDir.mkdirs();
        File fileToCopy = new File(file);

        File fileFinal = new File(currentDir.getAbsolutePath() + File.separator + fileToCopy.getName());
        if (fileToCopy.exists() && !fileToCopy.getAbsolutePath().equals(fileFinal.getAbsolutePath())) {
            org.apache.commons.io.FileUtils.copyFile(fileToCopy, fileFinal);

        }
    }

    /**
     * Esta función se encarga de copiar los ficheros de los metadatos a un directorio temporal, desde dónde apache camel podrá recogerlos y replicarlos
     * en el servidor destino
     * @param fileCollection Lista de rutas en el sistema de ficheros del servidor del fichero a copiar
     * @param key EnumMetadata con el que se generará la primera parte de la ruta bajo el directorio temporal
     * @param where EnumWhere con el que se generará la segunda parte de la ruta bajo el directorio temporal
     * @param tempDirectory directorio temporal en que dejar el fichero
     * @throws IOException Problemas con ficheros, IO y eso
     */
    public static void copyToTempDirectory(List<String> fileCollection, EnumMetadata key, EnumWhere where, File tempDirectory) throws IOException {

        for (String file : fileCollection) {
            copyToTempDirectory(file, key, where, tempDirectory);
        }
    }

    /**
     * Esta función, a partir de una lista de ficheros de metadatos, se encarga de copiarlos a un directorio temporal y posteriormente enviarlos con
     * apache camel al ftp que viene en el SiteSensitiveDatas
     * @param totalFiles Lista de rutas del sistema de archivos a subir
     * @param enumMetadata con el que se generará la primera parte de la ruta bajo el directorio temporal
     * @param enumWhere con el que se generará la segunda parte de la ruta bajo el directorio temporal
     * @param tempDirectory directorio temporal en que dejar el fichero
     * @param ssd siteSensitiveDatas del que coger los datos del FTP
     * @param os Stream en el que escribir el progreso de camel
     * @throws IOException Excepciones con los ficheros, permisos y tal
     * @throws Exception 
     */
    public static void sendFileList(List<String> totalFiles, EnumMetadata enumMetadata, EnumWhere enumWhere, File tempDirectory, SiteSensitiveDatas ssd, OutputStream os) throws IOException, Exception {
        Logger.getLogger(SharedFileUtils.class.getName()).log(Level.INFO, "Empezando a enviar lista de ficheros");

        CamelContext context = new DefaultCamelContext();
        copyToTempDirectory(totalFiles, enumMetadata, enumWhere, tempDirectory);

        Logger.getLogger(SharedFileUtils.class.getName()).log(Level.INFO, "Copiados a ruta temporal");

        String pathFileLocal = "file:" + tempDirectory.getAbsolutePath().replace("\\", "/") + "?recursive=true&noop=true&autoCreate=true";

        String pathFileFtp = ssd.getConnectionStringFtp();

        Logger.getLogger(SharedFileUtils.class.getName()).log(Level.INFO, "N\u00famero de ficheros: [{0}]", totalFiles.size());

        CountDownLatch latch = new CountDownLatch(totalFiles.size());

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from(pathFileLocal)
                        .log("empiezo a subir ${file:name}")
                        .to(pathFileFtp)
                        .log("acabo de subir ${file:name}")
                        .process((Exchange exchng) -> {

                            IOUtils.write("[" + (totalFiles.size() - latch.getCount() + 1) + "/" + totalFiles.size() + "]" + exchng.getIn().getHeader("CamelFileName", String.class) + "\n", os, "UTF-8");
                            latch.countDown();
                        });
            }

        });

        try {
            context.start();
            latch.await();
        } catch (Exception e) {

        } finally {
            context.stop();
        }

    }

}
