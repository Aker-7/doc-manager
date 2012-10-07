package ch.christinaz.filemanager.web;

import ch.christinaz.filemanager.bean.DocumentBean;
import ch.christinaz.filemanager.bean.FolderBean;
import ch.christinaz.filemanager.entity.Document;
import ch.christinaz.filemanager.entity.Folder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * This Managed Bean manages the user interface. The user interfaces is mainly
 * composed by: a tree which contains the folder, a table which contains the documents
 * of the selected folder. It also contains dialogs to add new folders, rename and delete existing folders.
 * @author cedric
 */
@ManagedBean
@SessionScoped
public class FolderManagedBean implements Serializable {
    
    //private static final String DOCUMENT_DIR = "/Users/cedric/filemanager_docdir/";
    
    @EJB
    private FolderBean folderBean;
    @EJB
    private DocumentBean documentBean;
    private Folder froot;
    private TreeNode root;
    private TreeNode selectedNode;
    private List<Document> documents;
    
    private String newFolderName;   //contains the name of the new folder that the user wants to create.
    
    private String uploadedOriginalFileName;  //contains the original file name as named in the computer of the user
    private String uploadedNewFileName;        //contains the new name of the uploaded file
    private String mimeType;
    private boolean isFileUploaded;             //if the file is uploaded, TRUE
    
    private String newDocName;
    private String newDocRemark;
    private Date dateDocument;
        
    public FolderManagedBean() {
        uploadedOriginalFileName="";
        uploadedNewFileName="";
        isFileUploaded=false;
    }
    
    /**
     * This method builds correctly the UI Tree. It will asks, via a EJB, the
     * root folder of the user and it will then loop on each folder to display 
     * them correctly in the UI Tree.
     */
    @PostConstruct
    public void initialize() {
        root = buildTree();
    }

    /**
     * This method returns to the JSF page the Root folder of the tree.
     * @return - TreeNode root folder of the tree
     */
    public TreeNode getRoot() {
        return root;
    }
    
    /*
     * This method retrives the folders from the db via the EJB folderBean and
     * then create the TreeNode objects correctly.
     */
    private TreeNode buildTree() {
        froot = folderBean.getRootFolder();

        root = new DefaultTreeNode("root", null);
        TreeNode realRoot = new DefaultTreeNode(froot, root);

        for (Folder child : froot.getChildFolders()) {
            TreeNode tnChild = new DefaultTreeNode(child, realRoot);
            tnChild.setParent(realRoot);
            buildTreeRecursively(tnChild);
        }
        return root;
    }
    
    /**
     * Recursive method to create the TreeNode structure.
     * @param currentNode 
     */
    private void buildTreeRecursively(TreeNode currentNode) {
        Folder folder = (Folder) (currentNode.getData());
        for (Folder child : folder.getChildFolders()) {
            TreeNode tnChild = new DefaultTreeNode(child, currentNode);
            tnChild.setParent(currentNode);

            buildTreeRecursively(tnChild);
        }
    }
    
    /**
     * This methods returns the list of documents that is contained in the
     * selected folder.
     * @return List of documents included in the selected folder.
     */
    public List<Document> getDocuments() {
        //The attribute documents is set in the method onFolderSelect()
        return documents;
    }
    
    /**
     * Getter to get the selected node.
     * @return the selected node
     */
    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    /**
     * Setter to set the selected node
     * @param selectedNode The selected node
     */
    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    /**
     * The method returns the name of the new folder that the user wants to add.
     * @return the name of the new folder that the user wants to add.
     */
    public String getNewFolderName() {
        return newFolderName;
    }
    
    /**
     * This method sets the name of the new folder that the user wants to add.
     * @param newFolderName the name of the new folder that the user wants to add.
     */
    public void setNewFolderName(String newFolderName) {
        this.newFolderName = newFolderName;
    }

    /**
     * This methods returns the original (as named on the user computer) name of the uploaded file. This method
     * is called in the step 2 of the add new document wizard.
     * @return 
     */
    public String getUploadedOriginalFileName() {
        return uploadedOriginalFileName;
    }

    public String getNewDocName() {
        return newDocName;
    }

    public void setNewDocName(String newDocName) {
        this.newDocName = newDocName;
    }

    public String getNewDocRemark() {
        return newDocRemark;
    }

    public void setNewDocRemark(String newDocRemark) {
        this.newDocRemark = newDocRemark;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public Date getDateDocument() {
        return dateDocument;
    }

   
    public void setDateDocument(Date dateDocument) {
        this.dateDocument = dateDocument;
    }
    
    
    /**
     * This method is called each time the user clicks on a node of the folder tree.
     * @param event 
     */
    public void onFolderSelect(NodeSelectEvent event) {
        Folder selectedFolder = (Folder) event.getTreeNode().getData();
        documents = selectedFolder.getDocuments();
    }
    
    /**
     * This method is called when the user wants to rename an existing folder.
     */
    public void renameFolder() {
        if (selectedNode != null) {
            folderBean.modifyFolder((Folder) selectedNode.getData());
            expandFromNode(selectedNode);
        }
    }
    
    /**
     * This method is called when the user wants to add a new folder.
     */
    public void addFolder() {
        if (this.selectedNode != null) {
            Folder newFolder = folderBean.addNewFolder(this.newFolderName, (Folder) selectedNode.getData());
            TreeNode tnNewNode = new DefaultTreeNode(newFolder, selectedNode);

            expandFromNode(tnNewNode);
            this.newFolderName = "";
        }
    }
    
    /**
     * This method is called when the user wants to delete an existing folder.
     */
    public void deleteFolder() {
        if (selectedNode != null) {
            folderBean.deleteFolder((Folder) selectedNode.getData());

            expandFromNode(selectedNode);

            selectedNode.getChildren().clear();
            selectedNode.getParent().getChildren().remove(selectedNode);
            selectedNode.setParent(null);

            selectedNode = null;
        }
    }
    
    /**
     * This method rests the dialog to create a new folder.
     */
    public void initDialog() {
        this.newFolderName = "";
        RequestContext.getCurrentInstance().reset(":fAdd:addFolderPanel");  
    }
    
    /**
     * This method is called when the wizard to add a new document is closed.
     * It reset all the field of the wizard in order to be ready the next time
     * the user wants to upload another document.
     */
    public void initWizard(){
        this.newDocName = "";
        this.newDocRemark = "";
        this.uploadedNewFileName = "";
        this.uploadedOriginalFileName = "";
        this.isFileUploaded = false;
        this.mimeType = "";
        this.dateDocument = null;
        RequestContext.getCurrentInstance().execute("wiz.loadStep(wiz.cfg.steps[0],true);");
    }

    /**
     * This method is called when the user clicks on the save button to add
     * a new document in the system.
     * @param actionEvent 
     */
    public void saveDocument(ActionEvent actionEvent){

        Document doc = documentBean.addNewDocument(newDocName, newDocRemark, this.uploadedNewFileName, this.uploadedOriginalFileName, this.mimeType, this.dateDocument, (Folder) selectedNode.getData());

        this.newDocName = "";
        this.newDocRemark = "";
        this.uploadedNewFileName = "";
        this.uploadedOriginalFileName = "";
        this.isFileUploaded = false;
        this.dateDocument = null;
        this.mimeType = "";
    }
    
    /**
     * This method handles the upload of a new file. It checks the file type
     * and copies the file to its final destination. At the end, it forces the
     * wizard to go to the next step.
     * @param event 
     */
    public void handleFileUpload(FileUploadEvent event){

        isFileUploaded = false;

        //Check again file type (allowed type: PDF)
        String fileType = event.getFile().getContentType();
        if(fileType.equalsIgnoreCase("application/pdf")){        
            try {
                //Copy file in the documents directories
                File file = File.createTempFile("doc_", ".pdf", new File(DocumentBean.DOCUMENT_DIR));
                IOUtils.copy(event.getFile().getInputstream(), new FileOutputStream(file));
                uploadedNewFileName = file.getName();
                uploadedOriginalFileName = event.getFile().getFileName();
                mimeType = event.getFile().getContentType();
                isFileUploaded = true;
                dateDocument = new Date();           
            } catch (IOException ex) {
               isFileUploaded = false;
            }               
        }   
        if(!isFileUploaded){
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "A fatal error occurs. Unable to upload file: "+event.getFile().getFileName());  
            FacesContext.getCurrentInstance().addMessage(null, msg);  
        }
        else{
            //All is ok, force Next step of the wizard
            RequestContext.getCurrentInstance().execute("wiz.loadStep(wiz.cfg.steps[1], true)");
        }
    }
   
   /**
    * This method manages the flow control of the wizard to add a new document.
    * @param event
    * @return 
    */
   public String onFlowProcess(FlowEvent event){
       
       if(event.getOldStep().equals("fileUpload") &&  !this.isFileUploaded){
           //Error, the user does not choose any file to upload. Stay in the current step
           FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "It is necessary to upload a document first.");  
           FacesContext.getCurrentInstance().addMessage(null, msg);  
           RequestContext.getCurrentInstance().update(":fUpload:msgFileUpload");  
           return event.getOldStep();      
       }
       
       //Ok, go to next step
       this.isFileUploaded=false;
       return event.getNewStep();
   }
    
    
    /*
     * This method browses form the specfied node to the root node and expand
     * each folders.
     */
    private void expandFromNode(TreeNode node) {
        while ((node = node.getParent()) != null) {
            node.setExpanded(true);
        }
    }
}
