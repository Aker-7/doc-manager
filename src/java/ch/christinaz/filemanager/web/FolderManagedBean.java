package ch.christinaz.filemanager.web;

import ch.christinaz.filemanager.bean.FolderBean;
import ch.christinaz.filemanager.entity.Document;
import ch.christinaz.filemanager.entity.Folder;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * This Managed Bean manages the user interface.
 * @author cedric
 */
@ManagedBean
@SessionScoped
public class FolderManagedBean implements Serializable {

    @EJB
    private FolderBean folderBean;
    private Folder froot;
    private TreeNode root;
    private TreeNode selectedNode;
    private List<Document> documents;
    
    private String newFolderName;

    public FolderManagedBean() {
       
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

    public List<Document> getDocuments() {
        return documents;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public String getNewFolderName() {
        return newFolderName;
    }

    public void setNewFolderName(String newFolderName) {
        this.newFolderName = newFolderName;
    }

    public void onFolderSelect(NodeSelectEvent event) {
        Folder selectedFolder = (Folder) event.getTreeNode().getData();
        documents = selectedFolder.getDocuments();
    }

    public void renameFolder() {
        if (selectedNode != null) {
            folderBean.modifyFolder((Folder) selectedNode.getData());
            expandFromNode(selectedNode);
        }
    }

    public void addFolder() {
        if (this.selectedNode != null) {
            Folder newFolder = folderBean.addNewFolder(this.newFolderName, (Folder) selectedNode.getData());
            TreeNode tnNewNode = new DefaultTreeNode(newFolder, selectedNode);

            expandFromNode(tnNewNode);
            this.newFolderName = "";
        }
    }

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

    public void initDialog() {
        this.newFolderName = "";
        RequestContext.getCurrentInstance().reset(":fAdd:addFolderPanel");  
    }

    private void expandFromNode(TreeNode node) {
        while ((node = node.getParent()) != null) {
            node.setExpanded(true);
        }
    }
}
