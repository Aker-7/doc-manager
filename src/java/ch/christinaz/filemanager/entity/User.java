package ch.christinaz.filemanager.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 * This class is a JPA entity that represent the table t_user. Users are stored
 * in this table and are used to securing the web application.
 *
 * @author Cédric Christinaz
 */
@Entity
@Table(name = "t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "user_email")
    private String id;
    @Column(name = "user_pass")
    private String password;
    @Column(name = "date_inserted")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date insertDate;
    @Column(name = "date_updated")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date updateDate;
    @Column(name = "date_last_login")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lastLoginDate;
    @Column(name = "user_role")
    private String role;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @PrePersist
    private void automaticDateInsert() {
        Date date = new Date();
        if (this.getInsertDate() == null) {
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
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ch.christinaz.filemanager.entity.User[ id=" + id + " ]";
    }
}
