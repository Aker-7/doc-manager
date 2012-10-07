package ch.christinaz.filemanager.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * This class is a JPA entity that represent the table t_folder. This table
 * stores the folders of the document manager. One folder can have several sub
 * folders but must have just one parent folder.
 * @author Cédric Christinaz
 */
@Entity
@Table(name="t_folder")
@NamedQuery(name="Folder.findRoot", query="select f from Folder f where f.idUser = :fIdUser and f.parentFolder IS NULL ")

public class Folder implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_folder")
    private Long id;
    
    @Column(name="folder_name")
    private String name;
    @Column(name="date_inserted")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date insertDate;
    @Column(name="date_updated")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updateDate;
    
    @Column(name="id_user")
    private String idUser;
    
    @ManyToOne
    @JoinColumn(name = "fk_folder_id_parent", referencedColumnName = "id_folder")
    private Folder parentFolder;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentFolder", orphanRemoval =  true)
    private List<Folder> childFolders;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "folder")
    private List<Document> documents;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public List<Folder> getChildFolders() {
        return childFolders;
    }

    public Folder getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(Folder parentFolder) {
        this.parentFolder = parentFolder;
    }   
    
    public void addChildFolder(Folder child){
        this.childFolders.add(child);
    }
    
    public void setChildFolders(List<Folder> childFolders) {
        this.childFolders = childFolders;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
    
    public void addDocument(Document document){
        this.documents.add(document);
    }

    
    @PrePersist
    private void automaticDateInsert(){
        Date date = new Date();
        if(this.getId() == null){
            this.setInsertDate(date);
        }
        this.setUpdateDate(date);
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }
    
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Folder)) {
            return false;
        }
        Folder other = (Folder) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ch.christinaz.filemanager.entity.Folder[ id=" + id + " ]";
    }
    
}
