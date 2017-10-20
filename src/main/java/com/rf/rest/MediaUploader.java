package com.rf.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rf.data.dto.filemanager.ArrayResponseJson;
import com.rf.data.dto.filemanager.DataJson;
import com.rf.data.dto.filemanager.ResponseJson;
import com.rf.data.repositories.FileManager.FileRepository;
import com.rf.data.repositories.FileManager.FolderRepository;
import com.rf.filemanager.FileManager;
import com.rf.services.filemanager.FileService;
import com.rf.services.filemanager.FolderService;

/**
 * La clase MediaUploader es un controlador rest que expone los métodos que necesita el filemanager
 * El front del filemanager se obtuvo del siguiente github https://github.com/servocoder/RichFilemanager, del cual se implementó el API REST que se encuentra
 * en la documentación del github. La implementación de las funciones se encuentra bajo el package filemanager
 * @author mortas
 */
@RestController
public class MediaUploader {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private FolderRepository folderRepository;

    @Value("${uploads_directory}")
    private String uploads_directory;

    @Value("${uploads_base_url}")
    private String uploads_base_url;

    @RequestMapping(value = "/filemanagermedia", method = RequestMethod.GET)
    @ResponseBody
    public Object mediaManager(
            @RequestParam(required = false, value = "mode") String mode,
            @RequestParam(required = false, value = "path") String path,
            @RequestParam(required = false, value = "name") String name,
            @RequestParam(required = false, value = "old") String old,
            @RequestParam(required = false, value = "new") String newf,
            @RequestParam(required = false, value = "type") String type,
            @RequestParam(required = false, value = "thumbnail") Boolean thumbnail,
            HttpServletRequest request,
            HttpServletResponse response) {
        FileManager fm = null;
        List<DataJson> ret = null;

        ArrayResponseJson aretJson = new ArrayResponseJson();
        ResponseJson retJson = new ResponseJson();

        try {
            fm = new FileManager(folderService, fileService, folderRepository, fileRepository, uploads_directory, uploads_base_url);

            ret = new ArrayList<DataJson>();

            switch (mode) {
                case "initiate":
                    retJson.setData(fm.getInitiate());
                    return retJson;
                case "getinfo":
                case "getfile":
                    retJson.setData(fm.getFile(path));
                    return retJson;
                case "download":
                    if (request.getHeader("X-Requested-With") != null && request.getHeader("X-Requested-With").contains("XMLHttpRequest")) {
                        DataJson dj = fm.getFile(path);
                        retJson.setData(dj);
                        return retJson;
                    } else {
                        fm.writeFileTo(response, path);
                        return null;
                    }

                case "rename":
                    retJson.setData(fm.rename(old, newf));
                    return retJson;
                case "addfolder":
                    retJson.setData(fm.addFolder(path, name));
                    return retJson;
                case "delete":
                    retJson.setData(fm.delete(path));
                    return retJson;
                case "getfolder":
                    ret = fm.getFolder(path, type);
                    break;

                case "move":
                    ret = fm.move(old, newf);
                    break;
                case "getimage":
                    ByteArrayOutputStream bao = fm.getImage(path, thumbnail);
                    return bao.toByteArray();
                case "editfile":
                    retJson.setData(fm.editFile(path));
                    return retJson;

                /*case "download":
		    		if (fm.setGetVar("path", path)) {
		    			responseData = fm.download(request, response);
		    		}
		    		break;
		    	
		    	case "readfile":
		    		if (fm.setGetVar("path", path)) {
		    			fm.preview(request, response);
		    		}
		    		break;
		    	case "move":
		    		if (fm.setGetVar("old", old) && fm.setGetVar("new", newf)) {
		    			responseData = fm.moveItem();
		    		}
		    		break;*/
                default:

                    //fm.error("parámetro desconocido");
                    break;

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        aretJson.setData(ret);
        return aretJson;

    }

    @RequestMapping(value = "/filemanagermedia", method = RequestMethod.POST)
    public Object uploadImage(
            @RequestParam(required = false, value = "files") MultipartFile[] files,
            @RequestParam(required = false, value = "path") String path,
            @RequestParam(required = false, value = "mode") String mode,
            @RequestParam(required = false, value = "content") String content
    ) {
        FileManager fm = new FileManager(folderService, fileService, folderRepository, fileRepository, uploads_directory, uploads_base_url);
        ArrayResponseJson response = new ArrayResponseJson();
        ResponseJson oresponse = new ResponseJson();
        List<DataJson> data = new ArrayList<>();
        DataJson dj = new DataJson();
        if (mode.equals("upload")) {

            try {

                data = fm.upload(path, files);

                response.setData(data);
                return response;
            } catch (IllegalStateException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // TODO Auto-generated catch block

        } else if (mode.equals("savefile")) {

            dj = fm.savEditedFile(path, content);

            oresponse.setData(dj);
            return oresponse;

        }

        return null;

    }
}
