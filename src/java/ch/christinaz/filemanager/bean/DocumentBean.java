package ch.christinaz.filemanager.bean;

import ch.christinaz.filemanager.entity.Document;
import ch.christinaz.filemanager.entity.Folder;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.swing.ImageIcon;

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
     *
     * @param name - name of the document (given by the user)
     * @param remark - remark attached to the document
     * @param path - name of the document on the server
     * @param folder - folder in which the document is located
     * @return - The created document.
     */
    public Document addNewDocument(String name, String remark, String path, String originalFilename, String mimeType, Date dateDocument, Folder folder) {

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

    /**
     * This method updates in the database an existing document.
     *
     * @param document Document to update
     */
    public void updateDocument(Document document) {
        em.merge(document);
    }

    /**
     * This method allows to delete a document
     *
     * @param folder - The folder to delete
     */
    public void deleteDocument(Document document) {
        Document toRemoved = em.merge(document);
        em.remove(toRemoved);
    }
    
    /**
     * This method returns the full filename (path + file name).
     * @param document  Document for which we want to return the full filename
     * @return Full filename of the document
     */
    public String downloadDocument(Document document){
        return DOCUMENT_DIR + document.getPath();
    }

    /**
     * This methods converts the document to an image and returns the 
     * path of the image
     * @param document Document to convert to an image
     * @return Path of the generated image
     */
    public String previewDocument(Document document) {

        File file = new File(DOCUMENT_DIR + document.getPath());
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(file, "r");
            FileChannel channel = raf.getChannel();
            ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            PDFFile pdffile = new PDFFile(buf);

            if(pdffile.getNumPages() > 0){
                //To keep it simple, I just convert the first page

                PDFPage page = pdffile.getPage(0);
                // get the width and height for the doc at the default zoom
                Rectangle rect = new Rectangle(0, 0, (int) page.getBBox().getWidth(), (int) page.getBBox().getHeight());

                Image img = page.getImage(rect.width, rect.height, // width & height
                    rect, // clip rect
                    null, // null for the ImageObserver
                    true, // fill background with white
                    true // block until drawing is done
                    );

                BufferedImage bImg = toBufferedImage(img);
                File yourImageFile = new File(DOCUMENT_DIR+ document.getPath() + ".png");
                ImageIO.write(bImg, "png", yourImageFile);

            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(DocumentBean.class.getName()).log(Level.SEVERE, null, ex);
        }catch (IOException ex) {
            Logger.getLogger(DocumentBean.class.getName()).log(Level.SEVERE, null, ex);
        }



        return DOCUMENT_DIR+ document.getPath() + ".png";


    }


    /**
     * This method returns a buffered image with the contents of an image
     * @param image
     * @return 
     */
    private static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
        // Determine if the image has transparent pixels; for this method's
        // implementation, see e661 Determining If an Image Has Transparent
        // Pixels
        boolean hasAlpha = hasAlpha(image);
        // Create a buffered image with a format that's compatible with the
        // screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }
            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }

    private static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage) image;
            return bimage.getColorModel().hasAlpha();
        }
        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }
        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }
}
