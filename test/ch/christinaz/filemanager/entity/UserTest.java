/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.christinaz.filemanager.entity;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author cedric
 */
public class UserTest {
    
    private static EntityManagerFactory emf;
    private static EntityManager em;
    private static EntityTransaction tx;
    
    public UserTest() {
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
    public void createUser() {
        
        Date date = new Date();
         
        User user = new User();
        user.setId("cedric@christinaz.ch");
        user.setPassword("test");
        user.setRole("USER");
        user.setLastLoginDate(null);
        user.setInsertDate(date);
        user.setUpdateDate(date);
        
        tx.begin();
        em.persist(user);
        tx.commit();
        
        User newUser = em.find(User.class, "cedric@christinaz.ch");
        
        Assert.assertNotNull("User must not be NULL", newUser);
        Assert.assertEquals("USER ID should be cedric@christinaz.ch", newUser.getId(), "cedric@christinaz.ch");
        
    }
    
}
