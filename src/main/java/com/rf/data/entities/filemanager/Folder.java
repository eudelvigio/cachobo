package com.rf.data.entities.filemanager;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.envers.Audited;

/**
 * Esta entidad almacena los datos referentes a directorios en el filemanager. Hay restricción de nombre bajo la misma carpeta
 * @author mortas
 */
@Entity
@Audited
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    private Date created;

    private Date modified;

    @ManyToOne
    private Folder parentFolder;

    /*
	@OneToMany(mappedBy="Folder")
	private List<Folder> childFolders = new ArrayList<Folder>();
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Folder getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(Folder parentFolder) {
        this.parentFolder = parentFolder;
    }

    /*public List<Folder> getChildFolders() {
		return childFolders;
	}

	public void setChildFolders(List<Folder> childFolders) {
		this.childFolders = childFolders;
	}*/
}
