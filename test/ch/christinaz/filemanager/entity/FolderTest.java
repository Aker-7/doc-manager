/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.christinaz.filemanager.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author cedric
 */
public class FolderTest {

    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static EntityTransaction tx;

    public FolderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("TestFileManagerPU");
        em = emf.createEntityManager();
    }

    @AfterClass
    public static void tearDownClass() {
        em.close();
        emf.close();
    }

    @Before
    public void setUp() {
        tx = em.getTransaction();
    }

    @Test
    public void createFolder() {
        
        //Creation of the root folder
        Folder root = new Folder();
        root.setName("Root");
        root.setIdUser("cedric@christinaz.ch");
        
        //Creation of a child folder
        Folder child = new Folder();
        child.setName("Child");
        child.setIdUser("cedric@christinaz.ch");
        child.setParentFolder(root);
        
        //Adding the child folder to its parent folder (the root folder9
        List<Folder> childFolders = new ArrayList();
        childFolders.add(child);
        root.setChildFolders(childFolders);
        
        //Creation of a document
        Document doc = new Document();
        doc.setName("Invoices July");
        doc.setOriginalFilename("test.pdf");
        doc.setDocumentDate(new Date());
        doc.setFolder(child);
        doc.setIdUser("cedric@christinaz.ch");
        doc.setPath("myPath");
        doc.setSize(1200);
        doc.setType("PDF");
        List<Document> docs = new ArrayList();
        docs.add(doc);
        
        //Adding the document to the child folder
        child.setDocuments(docs);
        
        tx.begin(); 
        //We persist just the root folder because we have the annotation cascade ALL
        em.persist(root);
        tx.commit();
        
        Assert.assertNotNull("IDFolder must no be empty", root.getId());
        Assert.assertNotNull("Root Folder has no child folder", root.getChildFolders());
        Assert.assertEquals("Root folder should have 1 child", root.getChildFolders().size(), 1);
    }
    
    @Test
    public void testQuery(){
        
        Query query = em.createNamedQuery("Folder.findRoot");
        query.setParameter("fIdUser", "cedric@christinaz.ch");
        
        Folder root = (Folder) query.getSingleResult();
        
        Assert.assertNotNull("root must not be null", root);
    }
}
