/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.christinaz.filemanager.bean;

import ch.christinaz.filemanager.entity.User;
import java.security.MessageDigest;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author cedric
 */
@Stateless
public class UserBean {

    @PersistenceContext(unitName = "FileManagerPU")
    private EntityManager em;

    public boolean addNewUser(String email, String password, String role) {
        boolean res = false;
        try {

            //The password is hashed with SHA-512
            MessageDigest mda = MessageDigest.getInstance("SHA-512");
            byte[] hashPassword = mda.digest(password.getBytes());

            //convert the byte to hex format
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < hashPassword.length; i++) {
                sb.append(Integer.toString((hashPassword[i] & 0xff) + 0x100, 16).substring(1));
            }

            User newUser = new User();
            newUser.setId(email); //the id is the email
            newUser.setPassword(sb.toString());
            newUser.setRole(role);
            em.persist(newUser);
            res = true;
        } catch (Exception e) {
            res = false;
        }
        return res;
    }
}
