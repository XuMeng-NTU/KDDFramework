/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.flow;

import manager.flow.FlowsManager;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import persistence.registry.RegistryItem;
import enumeration.registry.RegistryItemType;
import manager.registry.RegistryManager;

/**
 *
 * @author Meng
 */
public class FlowsManagerDisplay extends javax.swing.JFrame {

    private RegistryManager registry;
    private FlowsManager flows;
    /**
     * Creates new form FlowsManagerDisplay
     */
    public FlowsManagerDisplay() {
        initComponents();
        this.setSize(700, 500);
        
        registry = new RegistryManager();
         
        flowsTree.setCellRenderer(new ComponentsTreeCellRenderer());
        generateFlowsTree();
        
        flowsTree.addTreeSelectionListener(new TreeSelectionListener(){

            @Override
            public void valueChanged(TreeSelectionEvent tse) {
                
                if (tse.getNewLeadSelectionPath() != null) {
                    RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) flowsTree.getLastSelectedPathComponent()).getUserObject();

                    if (selected.getType() == RegistryItemType.FLOW) {
                        
                    } else {

                    }
                }
            }
            
        });       
        
    }

    
    public void generateFlowsTree(){

        RegistryItem rootItem = registry.getRoot();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootItem);
        
        LinkedList<RegistryItem> categoryQueue = new LinkedList<>();
        LinkedList<DefaultMutableTreeNode> nodeQueue = new LinkedList<>();
        
        categoryQueue.push(rootItem);
        nodeQueue.push(root);
        
        while(!categoryQueue.isEmpty()){
            DefaultMutableTreeNode currentNode = nodeQueue.pop();
            RegistryItem currentItem = categoryQueue.pop();
        
            for (RegistryItem childItem : currentItem.getChildren()) {
                
                if(childItem.getType()==RegistryItemType.CATEGORY){
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childItem);
                    currentNode.add(childNode);

                    categoryQueue.push(childItem);
                    nodeQueue.push(childNode);
                } else if(childItem.getType()==RegistryItemType.FLOW){
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childItem);
                    currentNode.add(childNode);              
                }
            }
        }
        
        flowsTree.setModel(new DefaultTreeModel(root));
    }    

    
    private static class ComponentsTreeCellRenderer extends DefaultTreeCellRenderer {

        @Override
        public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            // decide what icons you want by examining the node
            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                
                RegistryItem item = (RegistryItem) node.getUserObject();
                if (item.getType()==RegistryItemType.CATEGORY) {               
                    setIcon(UIManager.getIcon("FileView.directoryIcon"));
                } else if (item.getType()==RegistryItemType.FLOW) {
                    setIcon(UIManager.getIcon("FileView.fileIcon"));
                }
            }

            return this;
        }

    }    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        categoryPopupMenu = new javax.swing.JPopupMenu();
        addCategoryItem = new javax.swing.JMenuItem();
        addFlowItem = new javax.swing.JMenuItem();
        renameCategory = new javax.swing.JMenuItem();
        removeCategory = new javax.swing.JMenuItem();
        flowPopupMenu = new javax.swing.JPopupMenu();
        renameFlow = new javax.swing.JMenuItem();
        removeFlow = new javax.swing.JMenuItem();
        flowsPanel = new javax.swing.JPanel();
        flowsTreePanel = new javax.swing.JPanel();
        flowsTree = new javax.swing.JTree();
        flowsBtnPanel = new javax.swing.JPanel();
        initBtn = new javax.swing.JButton();
        loadBtn = new javax.swing.JButton();
        saveBtn = new javax.swing.JButton();

        addCategoryItem.setText("Add Category");
        addCategoryItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCategoryItemActionPerformed(evt);
            }
        });
        categoryPopupMenu.add(addCategoryItem);

        addFlowItem.setText("Add Flow");
        addFlowItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFlowItemActionPerformed(evt);
            }
        });
        categoryPopupMenu.add(addFlowItem);

        renameCategory.setText("Rename");
        renameCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameCategoryActionPerformed(evt);
            }
        });
        categoryPopupMenu.add(renameCategory);

        removeCategory.setText("Remove");
        removeCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeCategoryActionPerformed(evt);
            }
        });
        categoryPopupMenu.add(removeCategory);

        renameFlow.setText("Rename");
        renameFlow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameFlowActionPerformed(evt);
            }
        });
        flowPopupMenu.add(renameFlow);

        removeFlow.setText("Remove");
        removeFlow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFlowActionPerformed(evt);
            }
        });
        flowPopupMenu.add(removeFlow);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Flows Manager");
        setResizable(false);

        flowsPanel.setLayout(new java.awt.BorderLayout());

        flowsTreePanel.setLayout(new java.awt.BorderLayout());

        flowsTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                flowsTreeMouseClicked(evt);
            }
        });
        flowsTreePanel.add(flowsTree, java.awt.BorderLayout.CENTER);

        flowsPanel.add(flowsTreePanel, java.awt.BorderLayout.CENTER);

        flowsBtnPanel.setLayout(new java.awt.GridLayout(1, 3));

        initBtn.setText("Initialize");
        initBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                initBtnActionPerformed(evt);
            }
        });
        flowsBtnPanel.add(initBtn);

        loadBtn.setText("Load");
        loadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadBtnActionPerformed(evt);
            }
        });
        flowsBtnPanel.add(loadBtn);

        saveBtn.setText("Save");
        saveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBtnActionPerformed(evt);
            }
        });
        flowsBtnPanel.add(saveBtn);

        flowsPanel.add(flowsBtnPanel, java.awt.BorderLayout.SOUTH);

        getContentPane().add(flowsPanel, java.awt.BorderLayout.WEST);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void flowsTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_flowsTreeMouseClicked
        // TODO add your handling code here:
        if(SwingUtilities.isRightMouseButton(evt)){
            int row = flowsTree.getClosestRowForLocation(evt.getX(), evt.getY());
            flowsTree.setSelectionRow(row);

            RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode)flowsTree.getLastSelectedPathComponent()).getUserObject();

            if(selected.getType()==RegistryItemType.CATEGORY){
                categoryPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
            } else if(selected.getType()==RegistryItemType.FLOW){
                flowPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    }//GEN-LAST:event_flowsTreeMouseClicked

    private void initBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_initBtnActionPerformed

        registry.initRegistry();
        generateFlowsTree();
    }//GEN-LAST:event_initBtnActionPerformed

    private void loadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadBtnActionPerformed
        registry.loadRegistry();
        generateFlowsTree();
    }//GEN-LAST:event_loadBtnActionPerformed

    private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
        registry.saveRegistry();
    }//GEN-LAST:event_saveBtnActionPerformed

    private void addCategoryItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCategoryItemActionPerformed
        String categoryName = JOptionPane.showInputDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.QUESTION_MESSAGE);

        if (!(categoryName == null || categoryName.equals(""))) {

            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) flowsTree.getLastSelectedPathComponent();
            RegistryItem selectedCategory = (RegistryItem) parentNode.getUserObject();

            RegistryItem newCategory = registry.addRegistryItem(selectedCategory, categoryName, RegistryItemType.CATEGORY);

            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newCategory);
            ((DefaultTreeModel) flowsTree.getModel()).insertNodeInto(newNode, parentNode, parentNode.getChildCount());
        }
    }//GEN-LAST:event_addCategoryItemActionPerformed

    private void addFlowItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFlowItemActionPerformed
        String flowName = JOptionPane.showInputDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.QUESTION_MESSAGE);

        if (!(flowName == null || flowName.equals(""))) {

            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) flowsTree.getLastSelectedPathComponent();
            RegistryItem selectedCategory = (RegistryItem) parentNode.getUserObject();

            RegistryItem newFlow = registry.addRegistryItem(selectedCategory, flowName, RegistryItemType.FLOW);

            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newFlow);
            ((DefaultTreeModel) flowsTree.getModel()).insertNodeInto(newNode, parentNode, parentNode.getChildCount());
        }
    }//GEN-LAST:event_addFlowItemActionPerformed

    private void renameCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameCategoryActionPerformed
        String newName = JOptionPane.showInputDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.QUESTION_MESSAGE);
        if (!(newName==null||newName.equals(""))) {
            RegistryItem selectedCategory = (RegistryItem) ((DefaultMutableTreeNode) flowsTree.getLastSelectedPathComponent()).getUserObject();
            registry.changeRegistryItemName(selectedCategory, newName);
        }
    }//GEN-LAST:event_renameCategoryActionPerformed

    private void removeCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeCategoryActionPerformed
        int choice = JOptionPane.showConfirmDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.YES_NO_CANCEL_OPTION);

        if(choice==JOptionPane.YES_OPTION){
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)flowsTree.getLastSelectedPathComponent();
            RegistryItem selectedCategory = (RegistryItem)selectedNode.getUserObject();

            registry.removeRegistryItem(selectedCategory);

            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
            ((DefaultTreeModel)flowsTree.getModel()).removeNodeFromParent(selectedNode);
        }
    }//GEN-LAST:event_removeCategoryActionPerformed

    private void renameFlowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameFlowActionPerformed
        String newName = JOptionPane.showInputDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.QUESTION_MESSAGE);
        if (!(newName==null||newName.equals(""))) {
            RegistryItem selectedFlow = (RegistryItem) ((DefaultMutableTreeNode) flowsTree.getLastSelectedPathComponent()).getUserObject();
            registry.changeRegistryItemName(selectedFlow, newName);
        }
    }//GEN-LAST:event_renameFlowActionPerformed

    private void removeFlowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFlowActionPerformed
        int choice = JOptionPane.showConfirmDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.YES_NO_CANCEL_OPTION);

        if(choice==JOptionPane.YES_OPTION){
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)flowsTree.getLastSelectedPathComponent();
            RegistryItem selectedComponent = (RegistryItem)selectedNode.getUserObject();

            registry.removeRegistryItem(selectedComponent);

            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
            ((DefaultTreeModel)flowsTree.getModel()).removeNodeFromParent(selectedNode);
        }
    }//GEN-LAST:event_removeFlowActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FlowsManagerDisplay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FlowsManagerDisplay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FlowsManagerDisplay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FlowsManagerDisplay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FlowsManagerDisplay().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addCategoryItem;
    private javax.swing.JMenuItem addFlowItem;
    private javax.swing.JPopupMenu categoryPopupMenu;
    private javax.swing.JPopupMenu flowPopupMenu;
    private javax.swing.JPanel flowsBtnPanel;
    private javax.swing.JPanel flowsPanel;
    private javax.swing.JTree flowsTree;
    private javax.swing.JPanel flowsTreePanel;
    private javax.swing.JButton initBtn;
    private javax.swing.JButton loadBtn;
    private javax.swing.JMenuItem removeCategory;
    private javax.swing.JMenuItem removeFlow;
    private javax.swing.JMenuItem renameCategory;
    private javax.swing.JMenuItem renameFlow;
    private javax.swing.JButton saveBtn;
    // End of variables declaration//GEN-END:variables
}
