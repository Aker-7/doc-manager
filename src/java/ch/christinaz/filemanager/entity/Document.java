package ch.christinaz.filemanager.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * This class is a JPA entity that represent the table t_document. This table
 * stores the documents of the document manager. Documents are organized in folders.
 * @author Cédric Christinaz
 */
@Entity
@Table(name="t_document")
public class Document implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_document")
    private Long id;
    
    @Column(name="document_name")
    private String name;
    @Column(name="date_inserted")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date insertDate;
    @Column(name="date_updated")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updateDate;
    @Column(name="date_document")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date documentDate;
    @Column(name="remark")
    private String remark;
    @Column(name="file_type")
    private String type;
    @Column(name="file_size")
    private long size;
    @Column(name="file_original_name")
    private String originalFilename;
    @Column(name="path")
    private String path;
    @Column(name="id_user")
    private String idUser;
    
    @ManyToOne
    @JoinColumn(name = "fk_folder_document", referencedColumnName = "id_folder")
    private Folder folder;
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(Date documentDate) {
        this.documentDate = documentDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
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
        if (!(object instanceof Document)) {
            return false;
        }
        Document other = (Document) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ch.christinaz.filemanager.entity.Document[ id=" + id + " ]";
    }
    
}
