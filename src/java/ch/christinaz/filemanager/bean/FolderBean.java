package ch.christinaz.filemanager.bean;

import ch.christinaz.filemanager.entity.Folder;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * This bean manages the folders in the database.
 * It provides methods to get folder from the database, to add folder in the database, to rename a folder and remove a folder from the database.
 * @author cedric
 */
@Stateless
public class FolderBean {

    @PersistenceContext(unitName = "FileManagerPU")
    private EntityManager em;
    @Resource
    private SessionContext context;
    
    /**
     * This method returns the Root folder for the logged in user.
     * @return - Root folder object 
     */
    public Folder getRootFolder() {
        //String user = context.getCallerPrincipal().getName();
        String user = "test@test.com";

        Query query = em.createNamedQuery("Folder.findRoot");
        query.setParameter("fIdUser", user);

        return (Folder) query.getSingleResult();
    }
    
    /**
     * This method allows to add a new folder
     * @param FolderName - name of the new folder
     * @param parent - parent folder of the new folder
     * @return - the new folder
     */
    public Folder addNewFolder(String FolderName, Folder parent) {

        Folder newFolder = new Folder();
        newFolder.setIdUser("test@test.com");
        newFolder.setName(FolderName);
        newFolder.setParentFolder(parent);

        parent.addChildFolder(newFolder);

        em.persist(newFolder);
        em.merge(parent);
        
        return newFolder;
    }
    
    
    /**
     * This method allows to rename an existing folder
     * @param folder - The folder to rename
     */
    public void modifyFolder(Folder folder) {
        em.merge(folder);
    }

    /**
     * This method allows to delete a folder
     * @param folder - The folder to delete
     */
    public void deleteFolder(Folder folder) {
        Folder toRemoved = em.merge(folder);
        em.remove(toRemoved);
    }
}
