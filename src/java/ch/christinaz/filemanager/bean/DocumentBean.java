
package ch.christinaz.filemanager.bean;

import ch.christinaz.filemanager.entity.Document;
import ch.christinaz.filemanager.entity.Folder;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author cedric
 */
@Stateless
public class DocumentBean {
    
    /**
     * contains the path where the documents are stored on the server.
     */
    public static final String DOCUMENT_DIR = "/Users/cedric/filemanager_docdir/";
    
    @PersistenceContext(unitName = "FileManagerPU")
    private EntityManager em;
    
    
    
    /**
     * This method adds in the database a new document.
     * @param name - name of the document (given by the user)
     * @param remark - remark attached to the document
     * @param path - name of the document on the server
     * @param folder - folder in which the document is located
     * @return - The created document.
     */
    public Document addNewDocument(String name, String remark, String path, String originalFilename, String mimeType, Date dateDocument,  Folder folder){

        File file = new File(DOCUMENT_DIR + path);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateDocument);
        cal.set(Calendar.HOUR, 1);
        
        Document document = new Document();
        document.setName(name);
        document.setRemark(remark);
        document.setPath(path);
        document.setFolder(folder);
        document.setDocumentDate(cal.getTime());
        document.setSize(file.length());
        document.setOriginalFilename(originalFilename);
        document.setType(mimeType);
        document.setIdUser("test@test.com");

        folder.addDocument(document);

        em.persist(document);
        em.merge(folder);

        return document;

    }
    

}
