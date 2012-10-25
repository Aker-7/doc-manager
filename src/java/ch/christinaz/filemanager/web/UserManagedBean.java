package ch.christinaz.filemanager.web;
import ch.christinaz.filemanager.bean.UserBean;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

/**
 * This managed bean manages the creation of a new user.
 * @author cedric
 */
@ManagedBean
@RequestScoped
public class UserManagedBean {
    
    @EJB
    private UserBean userBean;
    
    private String email;
    private String password;
    
    /**
     * Creates a new instance of UserManagedBean
     */
    public UserManagedBean() {
    }
    
    /**
     * This method saves a new user
     */
    public void saveUser(){
        boolean res = false;
        if(email != null && password != null){
            res = userBean.addNewUser(email, password, "USER");
            email="";
            password="";
        }
        if(res){
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "New user created.", "" );
            FacesContext.getCurrentInstance().addMessage(null, msg);       
        }   
        else{
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to create new user.", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);       
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
}
