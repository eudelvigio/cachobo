package com.rf.data.loaders;

import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.rf.data.entities.auth.Role;
import com.rf.data.entities.auth.User;
import com.rf.data.entities.filemanager.Folder;
import com.rf.data.repositories.ElementMetaDataRepository;
import com.rf.data.repositories.ElementRepository;
import com.rf.data.repositories.PageMetadataRepository;
import com.rf.data.repositories.PageRepository;
import com.rf.data.repositories.SiteMetadataRepository;
import com.rf.data.repositories.SiteRepository;
import com.rf.data.repositories.FileManager.FileRepository;
import com.rf.data.repositories.FileManager.FolderRepository;
import com.rf.services.auth.RoleServiceImpl;
import com.rf.services.auth.UserServiceImpl;
import com.rf.services.filemanager.FileService;
import com.rf.services.filemanager.FolderService;

/**
 * Esta clase seedea en base de datos una serie de datos por defecto para el primer lanzado en una base de datos virgen de la aplicación
 * @author mortas
 */
@Component
public class Seeder implements ApplicationListener<ContextRefreshedEvent> {

    private PageRepository pageRepository;

    private PageMetadataRepository pageMetadataRepository;
    private SiteRepository siteRepository;
    private SiteMetadataRepository siteMetadataRepository;
    private ElementRepository elementRepository;
    private ElementMetaDataRepository elementMetaDataRepository;

    private FolderRepository folderRepository;

    private Logger log = Logger.getLogger(Seeder.class);

    @Autowired
    public void setPageMetadataRepository(PageMetadataRepository pageMetadataRepository) {
        this.pageMetadataRepository = pageMetadataRepository;
    }

    @Autowired
    public void setProductRepository(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @Autowired
    public void setSiteRepository(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    @Autowired
    public void setElementRepository(ElementRepository elementRepository) {
        this.elementRepository = elementRepository;
    }

    @Autowired
    public void setElementMetaDataRepository(ElementMetaDataRepository elementMetaDataRepository) {
        this.elementMetaDataRepository = elementMetaDataRepository;
    }

    @Autowired
    public void setSiteMetadataRepository(SiteMetadataRepository siteMetadataRepository) {
        this.siteMetadataRepository = siteMetadataRepository;
    }

    @Autowired
    public void setFolderRepository(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    private UserServiceImpl userService;
    private RoleServiceImpl roleService;

    @Autowired
    public void setUserService(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRoleService(RoleServiceImpl roleService) {
        this.roleService = roleService;
    }

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FolderService folderService;

    @Value("${uploads_directory}")
    private String uploads_directory;

    @Value("${uploads_base_url}")
    private String uploads_base_url;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent arg0) {
        List<Folder> folders = (List<Folder>) folderRepository.findAll();
        if (folders.size() > 0) {
            return;
        }
        /*Site s = new Site();
		s.setDomain("www.wanabet.es");
		s.setName("Wanabet");
		s = siteRepository.save(s);

                
		Page p = new Page();
		p.setName("Home");
		p.setCreation(new DateTime(2016, 1, 1, 12, 0).toDate());
		p.setDespublication(new DateTime(2016, 1, 1, 12, 30).toDate());
		p.setElimination(new DateTime(2016, 1, 12, 12, 30).toDate());
		p.setPublication(new DateTime(2016, 12, 12, 12, 30).toDate());
		p.setSite(s);
		
		pageRepository.save(p);
		
		
		
		p = new Page();
		p.setName("Slots");
		p.setCreation(new DateTime(2016, 1, 1, 12, 0).toDate());
		p.setDespublication(new DateTime(2016, 1, 1, 12, 30).toDate());
		p.setElimination(new DateTime(2016, 1, 12, 12, 30).toDate());
		p.setPublication(new DateTime(2016, 12, 12, 12, 30).toDate());
		p.setSite(s);
		p = pageRepository.save(p);
            
         */

        Folder parentFolder = new Folder();
        parentFolder.setCreated(DateTime.now().toDate());
        parentFolder.setModified(DateTime.now().toDate());
        parentFolder.setName("/");
        folderRepository.save(parentFolder);

        Folder f = new Folder();
        f.setCreated(DateTime.now().toDate());
        f.setModified(DateTime.now().toDate());
        f.setName("/common/");
        f.setParentFolder(parentFolder);
        folderRepository.save(f);

        Folder fScripts = new Folder();
        fScripts.setCreated(DateTime.now().toDate());
        fScripts.setModified(DateTime.now().toDate());
        fScripts.setName("/common/scripts/");
        fScripts.setParentFolder(f);
        folderRepository.save(fScripts);

        loadUsers();
        loadRoles();
        assignUsersToAdminRole();

    }

    private void loadUsers() {
        User user1 = new User();
        user1.setUsername("eudel");
        user1.setPassword("hortaleza");
        userService.saveOrUpdate(user1);

    }

    private void loadRoles() {

        Role adminRole = new Role();
        adminRole.setRole("SUPERADMIN");
        roleService.saveOrUpdate(adminRole);
        log.info("Saved role" + adminRole.getRole());

        Role siteRole = new Role();
        siteRole.setRole("SITE_ADMIN");
        roleService.saveOrUpdate(siteRole);
        log.info("Saved role" + siteRole.getRole());

        Role mediaRole = new Role();
        mediaRole.setRole("MEDIA_ADMIN");
        roleService.saveOrUpdate(mediaRole);
        log.info("Saved role" + mediaRole.getRole());

    }

    private void assignUsersToAdminRole() {
        List<Role> roles = (List<Role>) roleService.listAll();
        List<User> users = (List<User>) userService.listAll();

        roles.forEach(role -> {
            if (role.getRole().equalsIgnoreCase("SUPERADMIN")) {
                users.forEach(user -> {
                    if (user.getUsername().equals("eudel")) {
                        user.addRole(role);
                        userService.saveOrUpdate(user);
                    }
                });
            }
        });
    }

}
