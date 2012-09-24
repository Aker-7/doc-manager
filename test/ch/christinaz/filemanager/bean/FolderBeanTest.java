/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.christinaz.filemanager.bean;

import ch.christinaz.filemanager.entity.Document;
import ch.christinaz.filemanager.entity.Folder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cedric
 */
public class FolderBeanTest {

    private static EJBContainer ec;
    private static Context ctx;

    public FolderBeanTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        ec = EJBContainer.createEJBContainer();
        ctx = ec.getContext();
    }

    @AfterClass
    public static void tearDownClass() {
        ec.close();
    }
    
    @Test
    public void createNewFolder(){
        try {
            FolderBean folderBean = (FolderBean) ctx.lookup("java:global/classes/FolderBean");
            Folder root = folderBean.getRootFolder();
            
            Assert.assertEquals("Root folder has no child", 0, root.getChildFolders().size());
            
            folderBean.addNewFolder("Test", root);
            
            root = folderBean.getRootFolder();
            System.out.println(root.getChildFolders().size());
            Assert.assertEquals("Root folder has one child",1, root.getChildFolders().size());
            
            
        } catch (NamingException ex) {
            Logger.getLogger(FolderBeanTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
/*
    @Test
    public void getRootFolder() {
        try {
            FolderBean folderBean = (FolderBean) ctx.lookup("java:global/classes/FolderBean");

            Folder folder = folderBean.getRootFolder();

            Assert.assertNotNull("Result of folderBean.getRootFolder() must not be null", folder);
            Assert.assertEquals("Root folder id must be 1", (long) folder.getId(), (long) 1);
            Assert.assertEquals("Root folder name must be Root", folder.getName(), "Root");

            Assert.assertNotNull("Root folder must have child folders", folder.getChildFolders());
            Assert.assertEquals("Root folder must have 2 child folders", folder.getChildFolders().size(), 2);

            for (Folder child : folder.getChildFolders()) {

                if (child.getId() == 2) {
                    Assert.assertEquals("Child folder with id=2 name must be Child", child.getName(), "Child");
                    Assert.assertNotNull("Child folder with id=2 must have document", child.getDocuments());
                    Assert.assertEquals("Child folder with id=2 must have 1 document", child.getDocuments().size(), 1);

                    Document doc = child.getDocuments().get(0);
                    Assert.assertEquals("Document name must be 'Document Test'", doc.getName(), "Document Test");
                }

                if (child.getId() == 3) {
                    Assert.assertEquals("Child folder with id=3 name must be Guarantees", child.getName(), "Guarantees");
                    Assert.assertEquals("Child folder with id=3 hasn't any document", child.getDocuments().size(), 0);
                }

            }

        } catch (NamingException ex) {
            Logger.getLogger(FolderBeanTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
}
