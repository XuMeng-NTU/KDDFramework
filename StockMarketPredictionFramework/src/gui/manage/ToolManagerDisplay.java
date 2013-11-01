/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.manage;

import enumeration.component.ComponentType;
import manager.component.ComponentsManager;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import persistence.component.Component;
import persistence.component.parameter.ParameterDefinition;
import persistence.registry.RegistryItem;
import enumeration.registry.RegistryItemType;
import gui.util.mover.ComponentMover;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import manager.senario.SenarioManager;
import manager.dataset.DatasetsManager;
import manager.flow.FlowsManager;
import manager.registry.RegistryManager;
import persistence.dataset.Dataset;
import persistence.flow.edge.Edge;
import persistence.flow.Flow;
import persistence.component.method.Method;
import persistence.flow.node.Node;
import persistence.component.parameter.ParameterValue;
import persistence.dataset.attribute.Attribute;
import persistence.senario.Senario;
import persistence.senario.source.Source;
import workbench.main.Workbench;

/**
 *
 * @author Meng
 */
public class ToolManagerDisplay extends javax.swing.JFrame {

    private RegistryManager registry;
    private ComponentsManager components;
    private FlowsManager flows;
    private DatasetsManager datasets;
    private SenarioManager senarios;

    private boolean editing;
    private ComponentMover mover;
    /**
     * Creates new form ComponentsManager
     */
    public ToolManagerDisplay() {
        initComponents();
        this.setSize(1000, 600);
        nodeConfigDialog.setSize(500, 600);
        nodeAddDialog.setSize(600,600);
        edgeConfigDialog.setSize(500,600);
        edgeAddDialog.setSize(600,600);

        ((CardLayout)contentPanel.getLayout()).show(contentPanel, "category");
        
        registry = new RegistryManager();
        components = new ComponentsManager();
        flows = new FlowsManager();
        datasets = new DatasetsManager();
        senarios = new SenarioManager();
        
        mover = new ComponentMover();
        editing = false;
        
        generateRegistryTree();
        
        registryTree.setCellRenderer(new RegistryTreeCellRenderer());
        
        refreshProviderComboBox();
        
        initNodeConfigDialog();
        initNodeAddDialog();
        initEdgeAddDialog();
        initEdgeConfigDialog();
    }

    private void refreshFlowComboBox(){
        
        flowSelectedComboBox.removeAllItems();
        
        List<Flow> flowList = flows.getAllFlows();
        
        for(Flow flow : flowList){
            flowSelectedComboBox.addItem(flow);
        }
        
    }
    
    private void refreshEdgeComponentList(){
        
        List<Component> componentList = components.getComponentsByType(ComponentType.LINKER);
        
        DefaultListModel componentListModel = new DefaultListModel();
        
        edgeComponentList.removeAll();
        for(Component comp : componentList){
            componentListModel.addElement(comp);
        }
        
        edgeComponentList.setModel(componentListModel);        
        edgeComponentList.addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent lse) {

                if (lse.getValueIsAdjusting()) {

                    Component selectedComponent = (Component) edgeComponentList.getSelectedValue();
                    //underlyingComponentAddLabel.setText(selectedComponent.toString());
                    
                    ((DefaultTableModel) parameterEdgeTable.getModel()).getDataVector().removeAllElements();
                    List<ParameterDefinition> parameters = selectedComponent.getParameters();
                    for (ParameterDefinition parameter : parameters) {
                        Object[] parameterData = new Object[2];
                        parameterData[0] = parameter;
                        parameterData[1] = parameter.getFormat();
                        ((DefaultTableModel) parameterEdgeTable.getModel()).addRow(parameterData);
                    }
                    parameterEdgeTable.repaint();
                    parameterEdgeTable.clearSelection();
                    
                    ((DefaultTableModel) inputEdgeTable.getModel()).getDataVector().removeAllElements();
                    List<ParameterDefinition> requiredInputs = selectedComponent.getRequiredInputs();
                    for (ParameterDefinition requiredInput : requiredInputs) {
                        Object[] requiredInputData = new Object[2];
                        requiredInputData[0] = requiredInput;
                        requiredInputData[1] = requiredInput.getFormat();
                        ((DefaultTableModel) inputEdgeTable.getModel()).addRow(requiredInputData);
                    }
                    inputEdgeTable.repaint();
                    inputEdgeTable.clearSelection();

                    ((DefaultTableModel) outputEdgeTable.getModel()).getDataVector().removeAllElements();
                    List<ParameterDefinition> generatedOutputs = selectedComponent.getGeneratedOutputs();
                    for (ParameterDefinition generatedOutput : generatedOutputs) {
                        Object[] generatedOutputData = new Object[2];
                        generatedOutputData[0] = generatedOutput;
                        generatedOutputData[1] = generatedOutput.getFormat();
                        ((DefaultTableModel) outputEdgeTable.getModel()).addRow(generatedOutputData);
                    }
                    outputEdgeTable.repaint();
                    outputEdgeTable.clearSelection();

                    ((DefaultTableModel) methodEdgeTable.getModel()).getDataVector().removeAllElements();
                    List<Method> methods = selectedComponent.getMethods();
                    for (Method method : methods) {
                        Object[] methodData = new Object[1];
                        methodData[0] = method;
                        ((DefaultTableModel) methodEdgeTable.getModel()).addRow(methodData);
                    }
                    methodEdgeTable.repaint();
                    methodEdgeTable.clearSelection();
                }
            }
        });
        edgeComponentList.clearSelection();
    }        
    
    private JComboBox generateDatasetComboBox(){
        JComboBox result = new JComboBox();
        
        List<Dataset> datasetList = datasets.getAllDatasets();
        for(Dataset dataset : datasetList){
            result.addItem(dataset);
        }
        
        return result;
    }
    
    private void refreshComponentList(){
        
        List<Component> componentList = components.getComponentsByType(ComponentType.CALCULATOR);
        
        DefaultListModel componentListModel = new DefaultListModel();
        
        componentConfigList.removeAll();
        for(Component comp : componentList){
            componentListModel.addElement(comp);
        }
        
        componentConfigList.setModel(componentListModel);        
        componentConfigList.addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent lse) {

                if (lse.getValueIsAdjusting()) {

                    Component selectedComponent = (Component) componentConfigList.getSelectedValue();
                    underlyingComponentAddLabel.setText(selectedComponent.toString());
                    
                    ((DefaultTableModel) parameterAddTable.getModel()).getDataVector().removeAllElements();
                    List<ParameterDefinition> parameters = selectedComponent.getParameters();
                    for (ParameterDefinition parameter : parameters) {
                        Object[] parameterData = new Object[2];
                        parameterData[0] = parameter;
                        parameterData[1] = parameter.getFormat();
                        ((DefaultTableModel) parameterAddTable.getModel()).addRow(parameterData);
                    }
                    parameterAddTable.repaint();
                    parameterAddTable.clearSelection();
                    
                    ((DefaultTableModel) inputAddTable.getModel()).getDataVector().removeAllElements();
                    List<ParameterDefinition> requiredInputs = selectedComponent.getRequiredInputs();
                    for (ParameterDefinition requiredInput : requiredInputs) {
                        Object[] requiredInputData = new Object[2];
                        requiredInputData[0] = requiredInput;
                        requiredInputData[1] = requiredInput.getFormat();
                        ((DefaultTableModel) inputAddTable.getModel()).addRow(requiredInputData);
                    }
                    inputAddTable.repaint();
                    inputAddTable.clearSelection();

                    ((DefaultTableModel) outputAddTable.getModel()).getDataVector().removeAllElements();
                    List<ParameterDefinition> generatedOutputs = selectedComponent.getGeneratedOutputs();
                    for (ParameterDefinition generatedOutput : generatedOutputs) {
                        Object[] generatedOutputData = new Object[2];
                        generatedOutputData[0] = generatedOutput;
                        generatedOutputData[1] = generatedOutput.getFormat();
                        ((DefaultTableModel) outputAddTable.getModel()).addRow(generatedOutputData);
                    }
                    outputAddTable.repaint();
                    outputAddTable.clearSelection();

                    ((DefaultTableModel) methodAddTable.getModel()).getDataVector().removeAllElements();
                    List<Method> methods = selectedComponent.getMethods();
                    for (Method method : methods) {
                        Object[] methodData = new Object[1];
                        methodData[0] = method;
                        ((DefaultTableModel) methodAddTable.getModel()).addRow(methodData);
                    }
                    methodAddTable.repaint();
                    methodAddTable.clearSelection();
                }
            }
        });
        componentConfigList.clearSelection();
    }    
    
    private void refreshProviderComboBox(){
        List<Class> componentImpl = components.scanForComponent();
        
        providerComboBox.removeAllItems();
        
        for(Class impl : componentImpl){    
            providerComboBox.addItem(impl);
        }
                
    }

    private void refreshNodesComboBox(Flow flow){
        List<Node> nodes = flow.getNodes();
        
        upstreamNodeComboBox.removeAllItems();
        downstreamNodeComboBox.removeAllItems();
        for(Node node : nodes){
            upstreamNodeComboBox.addItem(node);
            downstreamNodeComboBox.addItem(node);
        }
    }
    
    private JComponent createEdgeComponent(final Edge edge){
        JPanel result = new JPanel();
        result.setPreferredSize(new Dimension(180,80));
        final JLabel edgeName = new JLabel(edge.toString());
        JLabel edgeIcon = new JLabel(UIManager.getIcon("OptionPane.informationIcon"));       
        
        result.setLayout(new BorderLayout());
        
        edgeIcon.setHorizontalAlignment(SwingConstants.CENTER);
        result.add(edgeIcon, BorderLayout.CENTER);
        
        edgeName.setHorizontalAlignment(SwingConstants.CENTER);
        result.add(edgeName, BorderLayout.SOUTH);     
        

        result.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {
                
                if (me.getClickCount() == 2) {

                    edgeConfigDialog.setVisible(true);
                    edgeConfigComponentLabel.setText(edge.getComponent().toString());

                    upstreamNodeInfoLabel.setText(edge.getUpstream().getName());
                    downstreamNodeInfoLabel.setText(edge.getDownstream().getName());
                    
                    ((DefaultTableModel) parameterConfigEdgeTable.getModel()).getDataVector().removeAllElements();
                    List<ParameterValue> parameters = edge.getParameters();
                    for (ParameterValue parameter : parameters) {
                        Object[] parameterData = new Object[3];
                        parameterData[0] = parameter;
                        parameterData[1] = parameter.getDefinition().getFormat();
                        parameterData[2] = parameter.getValue();
                        ((DefaultTableModel) parameterConfigEdgeTable.getModel()).addRow(parameterData);
                    }
                    parameterConfigEdgeTable.repaint();
                    parameterConfigEdgeTable.clearSelection();

                    ((DefaultTableModel) inputConfigEdgeTable.getModel()).getDataVector().removeAllElements();
                    List<ParameterDefinition> requiredInputs = edge.getComponent().getRequiredInputs();
                    for (ParameterDefinition requiredInput : requiredInputs) {
                        Object[] requiredInputData = new Object[2];
                        requiredInputData[0] = requiredInput;
                        requiredInputData[1] = requiredInput.getFormat();
                        ((DefaultTableModel) inputConfigEdgeTable.getModel()).addRow(requiredInputData);
                    }
                    inputConfigEdgeTable.repaint();
                    inputConfigEdgeTable.clearSelection();

                    ((DefaultTableModel) outputConfigEdgeTable.getModel()).getDataVector().removeAllElements();
                    List<ParameterDefinition> generatedOutputs = edge.getComponent().getGeneratedOutputs();
                    for (ParameterDefinition generatedOutput : generatedOutputs) {
                        Object[] generatedOutputData = new Object[2];
                        generatedOutputData[0] = generatedOutput;
                        generatedOutputData[1] = generatedOutput.getFormat();
                        ((DefaultTableModel) outputConfigEdgeTable.getModel()).addRow(generatedOutputData);
                    }
                    outputConfigEdgeTable.repaint();
                    outputConfigEdgeTable.clearSelection();

                    ((DefaultTableModel) methodConfigEdgeTable.getModel()).getDataVector().removeAllElements();
                    List<Method> methods = edge.getComponent().getMethods();
                    for (Method method : methods) {
                        Object[] methodData = new Object[2];
                        methodData[0] = method;
                        if (edge.getMethod().equals(method)) {
                            methodData[1] = "true";
                        } else {
                            methodData[1] = null;
                        }
                        ((DefaultTableModel) methodConfigEdgeTable.getModel()).addRow(methodData);
                    }
                    methodConfigEdgeTable.repaint();   
                    methodConfigEdgeTable.clearSelection();
                    
                    edgeConfigActionPanel.removeAll();
                    
                    JButton saveEdgeConfigBtn = new JButton("Save");
                    if(!editing){
                        saveEdgeConfigBtn.setEnabled(false);
                    }
                    saveEdgeConfigBtn.addActionListener(new ActionListener(){

                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            int i;
                            int rowCount, columnCount;

                            DefaultTableModel parameterConfigEdgeTableModel = ((DefaultTableModel) parameterConfigEdgeTable.getModel());
                            rowCount = parameterConfigEdgeTableModel.getRowCount();
                            columnCount = parameterConfigEdgeTableModel.getColumnCount();

                            for (i = 0; i < rowCount; i++) {
                                ParameterValue param = (ParameterValue) parameterConfigEdgeTableModel.getValueAt(i, 0);
                                param.setValue((String) parameterConfigEdgeTableModel.getValueAt(i, columnCount - 1));
                            }

                            DefaultTableModel methodConfigEdgeTableModel = ((DefaultTableModel) methodConfigEdgeTable.getModel());
                            rowCount = methodConfigEdgeTableModel.getRowCount();
                            columnCount = methodConfigEdgeTableModel.getColumnCount();

                            for (i = 0; i < rowCount; i++) {
                                Method method = (Method) methodConfigEdgeTableModel.getValueAt(i, 0);
                                if (methodConfigEdgeTableModel.getValueAt(i, columnCount - 1).toString().equalsIgnoreCase("true")) {
                                    edge.setMethod(method);
                                    break;
                                }
                            }

                            flows.saveEdge(edge);
                            
                        }
                        
                    });
                    edgeConfigActionPanel.add(saveEdgeConfigBtn);

                    JButton removeEdgeConfigBtn = new JButton("Remove");
                    if(!editing){
                        removeEdgeConfigBtn.setEnabled(false);
                    }                    
                    removeEdgeConfigBtn.addActionListener(new ActionListener(){

                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            Flow currentFlow = edge.getFlow();
                            flows.removeEdge(edge);
                            edgeConfigDialog.dispose();
                            renderEdgesPanel(currentFlow);
                        }
                        
                    });
                    edgeConfigActionPanel.add(removeEdgeConfigBtn);                    
                    
                    JButton closeEdgeConfigBtn = new JButton("Close");
                    closeEdgeConfigBtn.addActionListener(new ActionListener(){

                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            edgeConfigDialog.dispose();
                        }
                        
                    });
                    edgeConfigActionPanel.add(closeEdgeConfigBtn);
                    
                    edgeConfigActionPanel.revalidate();
                    edgeConfigActionPanel.repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent me) {}

            @Override
            public void mouseReleased(MouseEvent me) {}

            @Override
            public void mouseEntered(MouseEvent me) {}

            @Override
            public void mouseExited(MouseEvent me) {}
            
        });
                
        
        return result;
        
    }
    
    private JComponent createNodeComponent(final Node node){
        JPanel result = new JPanel();
        result.setPreferredSize(new Dimension(80,80));
        final JLabel nodeName = new JLabel(node.getName());
        JLabel nodeIcon = new JLabel(UIManager.getIcon("OptionPane.informationIcon"));
        
        result.setLayout(new BorderLayout());
        
        nodeIcon.setHorizontalAlignment(SwingConstants.CENTER);
        result.add(nodeIcon, BorderLayout.CENTER);
        
        nodeName.setHorizontalAlignment(SwingConstants.CENTER);
        result.add(nodeName, BorderLayout.SOUTH);


        result.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent me) {
                
                if (me.getClickCount() == 2) {

                    nodeConfigDialog.setVisible(true);
                    underlyingComponentLabel.setText(node.getComponent().toString());
                    
                    nodeNameTextField.setText(node.getName());
                    
                    ((DefaultTableModel) parameterConfigTable.getModel()).getDataVector().removeAllElements();
                    List<ParameterValue> parameters = node.getParameters();
                    for (ParameterValue parameter : parameters) {
                        Object[] parameterData = new Object[3];
                        parameterData[0] = parameter;
                        parameterData[1] = parameter.getDefinition().getFormat();
                        parameterData[2] = parameter.getValue();
                        ((DefaultTableModel) parameterConfigTable.getModel()).addRow(parameterData);
                    }
                    parameterConfigTable.repaint();
                    parameterConfigTable.clearSelection();

                    ((DefaultTableModel) inputConfigTable.getModel()).getDataVector().removeAllElements();
                    List<ParameterDefinition> requiredInputs = node.getComponent().getRequiredInputs();
                    for (ParameterDefinition requiredInput : requiredInputs) {
                        Object[] requiredInputData = new Object[2];
                        requiredInputData[0] = requiredInput;
                        requiredInputData[1] = requiredInput.getFormat();
                        ((DefaultTableModel) inputConfigTable.getModel()).addRow(requiredInputData);
                    }
                    inputConfigTable.repaint();
                    inputConfigTable.clearSelection();

                    ((DefaultTableModel) outputConfigTable.getModel()).getDataVector().removeAllElements();
                    List<ParameterDefinition> generatedOutputs = node.getComponent().getGeneratedOutputs();
                    for (ParameterDefinition generatedOutput : generatedOutputs) {
                        Object[] generatedOutputData = new Object[2];
                        generatedOutputData[0] = generatedOutput;
                        generatedOutputData[1] = generatedOutput.getFormat();
                        ((DefaultTableModel) outputConfigTable.getModel()).addRow(generatedOutputData);
                    }
                    outputConfigTable.repaint();
                    outputConfigTable.clearSelection();

                    ((DefaultTableModel) methodConfigTable.getModel()).getDataVector().removeAllElements();
                    List<Method> methods = node.getComponent().getMethods();
                    for (Method method : methods) {
                        Object[] methodData = new Object[2];
                        methodData[0] = method;
                        if (node.getMethod().equals(method)) {
                            methodData[1] = "true";
                        } else {
                            methodData[1] = null;
                        }
                        ((DefaultTableModel) methodConfigTable.getModel()).addRow(methodData);
                    }
                    methodConfigTable.repaint();   
                    methodConfigTable.clearSelection();
                    
                    nodeConfigActionPanel.removeAll();
                    
                    JButton saveNodeConfigBtn = new JButton("Save");
                    if(!editing){
                        saveNodeConfigBtn.setEnabled(false);
                    }
                    saveNodeConfigBtn.addActionListener(new ActionListener(){

                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            int i;
                            int rowCount, columnCount;

                            DefaultTableModel parameterConfigTableModel = ((DefaultTableModel) parameterConfigTable.getModel());
                            rowCount = parameterConfigTableModel.getRowCount();
                            columnCount = parameterConfigTableModel.getColumnCount();

                            for (i = 0; i < rowCount; i++) {
                                ParameterValue param = (ParameterValue) parameterConfigTableModel.getValueAt(i, 0);
                                param.setValue((String) parameterConfigTableModel.getValueAt(i, columnCount - 1));
                            }

                            DefaultTableModel methodConfigTableModel = ((DefaultTableModel) methodConfigTable.getModel());
                            rowCount = methodConfigTableModel.getRowCount();
                            columnCount = methodConfigTableModel.getColumnCount();

                            for (i = 0; i < rowCount; i++) {
                                Method method = (Method) methodConfigTableModel.getValueAt(i, 0);
                                if (methodConfigTableModel.getValueAt(i, columnCount - 1).toString().equalsIgnoreCase("true")) {
                                    node.setMethod(method);
                                    break;
                                }
                            }

                            node.setName(nodeNameTextField.getText());
                            flows.saveNode(node);

                            nodeName.setText(nodeNameTextField.getText());
                            
                        }
                        
                    });
                    nodeConfigActionPanel.add(saveNodeConfigBtn);

                    JButton removeNodeConfigBtn = new JButton("Remove");
                    if(!editing){
                        removeNodeConfigBtn.setEnabled(false);
                    }                    
                    removeNodeConfigBtn.addActionListener(new ActionListener(){

                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            Flow currentFlow = node.getFlow();
                            flows.removeNode(node);
                            nodeConfigDialog.dispose();
                            renderNodesPanel(currentFlow);
                        }
                        
                    });
                    nodeConfigActionPanel.add(removeNodeConfigBtn);                    
                    
                    JButton closeNodeConfigBtn = new JButton("Close");
                    closeNodeConfigBtn.addActionListener(new ActionListener(){

                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            nodeConfigDialog.dispose();
                        }
                        
                    });
                    nodeConfigActionPanel.add(closeNodeConfigBtn);
                    
                    nodeConfigActionPanel.revalidate();
                    nodeConfigActionPanel.repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent me) {}

            @Override
            public void mouseReleased(MouseEvent me) {}

            @Override
            public void mouseEntered(MouseEvent me) {}

            @Override
            public void mouseExited(MouseEvent me) {}
            
        });
        
        return result;
        
    }
    
    private void initEdgeConfigDialog(){
        
        DefaultTableModel parameterConfigEdgeTableModel = new DefaultTableModel();

        parameterConfigEdgeTableModel.addColumn("Name");
        parameterConfigEdgeTableModel.addColumn("Format");    
        parameterConfigEdgeTableModel.addColumn("Value");        
        parameterConfigEdgeTable.setModel(parameterConfigEdgeTableModel);

        DefaultTableModel inputConfigEdgeTableModel = new DefaultTableModel();

        inputConfigEdgeTableModel.addColumn("Name");
        inputConfigEdgeTableModel.addColumn("Format");    
        inputConfigEdgeTableModel.addColumn("Value"); 
        
        inputConfigEdgeTable.setModel(inputConfigEdgeTableModel);        

        DefaultTableModel outputConfigEdgeTableModel = new DefaultTableModel();

        outputConfigEdgeTableModel.addColumn("Name");
        outputConfigEdgeTableModel.addColumn("Format");    
        outputConfigEdgeTableModel.addColumn("Value");
        
        outputConfigEdgeTable.setModel(outputConfigEdgeTableModel);               

        DefaultTableModel methodConfigEdgeTableModel = new DefaultTableModel();

        methodConfigEdgeTableModel.addColumn("Name"); 
        methodConfigEdgeTableModel.addColumn("Selected");
        
        methodConfigEdgeTable.setModel(methodConfigEdgeTableModel);            
    }
        
    private void initEdgeAddDialog(){
        
        DefaultTableModel parameterEdgeTableModel = new DefaultTableModel();

        parameterEdgeTableModel.addColumn("Name");
        parameterEdgeTableModel.addColumn("Format");    
        parameterEdgeTableModel.addColumn("Value");        
        parameterEdgeTable.setModel(parameterEdgeTableModel);

        DefaultTableModel inputEdgeTableModel = new DefaultTableModel();

        inputEdgeTableModel.addColumn("Name");
        inputEdgeTableModel.addColumn("Format");    
        inputEdgeTableModel.addColumn("Value"); 
        
        inputEdgeTable.setModel(inputEdgeTableModel);        

        DefaultTableModel outputEdgeTableModel = new DefaultTableModel();

        outputEdgeTableModel.addColumn("Name");
        outputEdgeTableModel.addColumn("Format");    
        outputEdgeTableModel.addColumn("Value");
        
        outputEdgeTable.setModel(outputEdgeTableModel);               

        DefaultTableModel methodEdgeTableModel = new DefaultTableModel();

        methodEdgeTableModel.addColumn("Name"); 
        methodEdgeTableModel.addColumn("Selected");
        
        methodEdgeTable.setModel(methodEdgeTableModel);            
    }
    
    private void initNodeAddDialog(){
        
        DefaultTableModel parameterAddTableModel = new DefaultTableModel();

        parameterAddTableModel.addColumn("Name");
        parameterAddTableModel.addColumn("Format");    
        parameterAddTableModel.addColumn("Value");        
        parameterAddTable.setModel(parameterAddTableModel);

        DefaultTableModel inputAddTableModel = new DefaultTableModel();

        inputAddTableModel.addColumn("Name");
        inputAddTableModel.addColumn("Format");    
        inputAddTableModel.addColumn("Value"); 
        
        inputAddTable.setModel(inputAddTableModel);        

        DefaultTableModel outputAddTableModel = new DefaultTableModel();

        outputAddTableModel.addColumn("Name");
        outputAddTableModel.addColumn("Format");    
        outputAddTableModel.addColumn("Value");
        
        outputAddTable.setModel(outputAddTableModel);               

        DefaultTableModel methodAddTableModel = new DefaultTableModel();

        methodAddTableModel.addColumn("Name"); 
        methodAddTableModel.addColumn("Selected");
        
        methodAddTable.setModel(methodAddTableModel);            
    }
    
    private void initNodeConfigDialog(){
        
        DefaultTableModel parameterConfigTableModel = new DefaultTableModel();

        parameterConfigTableModel.addColumn("Name");
        parameterConfigTableModel.addColumn("Format");    
        parameterConfigTableModel.addColumn("Value");        
        parameterConfigTable.setModel(parameterConfigTableModel);

        DefaultTableModel inputConfigTableModel = new DefaultTableModel();

        inputConfigTableModel.addColumn("Name");
        inputConfigTableModel.addColumn("Format");    
        inputConfigTableModel.addColumn("Value"); 
        
        inputConfigTable.setModel(inputConfigTableModel);        

        DefaultTableModel outputConfigTableModel = new DefaultTableModel();

        outputConfigTableModel.addColumn("Name");
        outputConfigTableModel.addColumn("Format");    
        outputConfigTableModel.addColumn("Value");
        
        outputConfigTable.setModel(outputConfigTableModel);               

        DefaultTableModel methodConfigTableModel = new DefaultTableModel();

        methodConfigTableModel.addColumn("Name"); 
        methodConfigTableModel.addColumn("Selected");
        
        methodConfigTable.setModel(methodConfigTableModel);            
    }
    
    private void renderNodesPanel(Flow selectedFlow){

        flowNodesPanel.removeAll();
        for (Node node : selectedFlow.getNodes()) {
            JComponent nodeComponent = createNodeComponent(node);
            mover.registerComponent(nodeComponent);
            flowNodesPanel.add(nodeComponent);
        }
        flowNodesPanel.revalidate();
        flowNodesPanel.repaint();        
    }
    
    private void renderEdgesPanel(Flow selectedFlow){

        flowEdgesPanel.removeAll();      
        for (Edge edge : selectedFlow.getEdges()) {
            JComponent edgeComponent = createEdgeComponent(edge);
            mover.registerComponent(edgeComponent);
            flowEdgesPanel.add(edgeComponent);
        }
        flowEdgesPanel.revalidate();
        flowEdgesPanel.repaint();        
    }    
    
    private void renderFlowPanel(RegistryItem selected){

        ((CardLayout)contentPanel.getLayout()).show(contentPanel, "flow");

        nodeActionPanel.setVisible(false);
        edgeActionPanel.setVisible(false);
        flowNameLabel.setText(selected.getName());
        
        Flow selectedFlow = flows.getFlow(selected);
        
        if(selectedFlow==null){
            ((CardLayout)flowLevelBtnPanel.getLayout()).show(flowLevelBtnPanel, "construct");
            
        } else{
            ((CardLayout)flowLevelBtnPanel.getLayout()).show(flowLevelBtnPanel, "edit");

            renderNodesPanel(selectedFlow);
            renderEdgesPanel(selectedFlow);
        }

    }
    
    private void renderCategoryPanel(RegistryItem selected){
        ((CardLayout)contentPanel.getLayout()).show(contentPanel, "category");
        
    }

    private void renderSenarioSourceViewPanel(Senario senario){

        sourceTable.clearSelection();

        TableColumn datasetColumn = sourceTable.getColumnModel().getColumn(1);
        datasetColumn.setCellEditor(new DefaultCellEditor(new JTextField()));        

        ((DefaultTableModel) (sourceTable.getModel())).getDataVector().removeAllElements();
        List<Source> sources = senario.getSources();
        for (Source source : sources) {
            Object[] sourceData = new Object[3];
            sourceData[0] = source;
            sourceData[1] = source.getDataset();
            sourceData[2] = source.getFilename();

            ((DefaultTableModel) (sourceTable.getModel())).addRow(sourceData);
        }

        sourceTable.repaint();        
    }

    private void renderSenarioSourceEditPanel(Senario senario){

        sourceTable.clearSelection();

        TableColumn datasetColumn = sourceTable.getColumnModel().getColumn(1);
        datasetColumn.setCellEditor(new DefaultCellEditor(generateDatasetComboBox()));           
        
        ((DefaultTableModel) (sourceTable.getModel())).getDataVector().removeAllElements();
        
        int i = 0;
        
        List<Source> sources = senario.getSources();
        for (Source source : sources) {
            Object[] sourceData = new Object[3];
            sourceData[0] = source;
            sourceData[1] = source.getDataset();
            sourceData[2] = source.getFilename();
            
            ((DefaultTableModel) (sourceTable.getModel())).addRow(sourceData);
            
            ((JComboBox)((DefaultCellEditor)sourceTable.getCellEditor(i,1)).getComponent()).setSelectedItem(source.getDataset());
        }
        
        sourceTable.repaint();        
    }
    
    private void renderDatasetAttributePanel(Dataset dataset){

        attributeTable.clearSelection();

        ((DefaultTableModel) (attributeTable.getModel())).getDataVector().removeAllElements();
        List<Attribute> attributes = dataset.getAttributes();
        for (Attribute attribute : attributes) {
            Object[] attributeData = new Object[3];
            attributeData[0] = attribute;
            attributeData[1] = attribute.getFormat();
            attributeData[2] = attribute.getFieldName();
            ((DefaultTableModel) (attributeTable.getModel())).addRow(attributeData);
        }

        attributeTable.repaint();
    }
    
    private void renderSenarioPanel(RegistryItem selected){
        ((CardLayout)contentPanel.getLayout()).show(contentPanel, "senario");
        
        senarioNameLabel.setText(selected.getName());
        sourceActionBtnPanel.setVisible(false);
        
        DefaultTableModel sourceTableModel = new DefaultTableModel();

        sourceTableModel.addColumn("Name");
        sourceTableModel.addColumn("Dataset"); 
        sourceTableModel.addColumn("File Path");
        
        sourceTable.setModel(sourceTableModel);

        
        Senario selectedSenario = senarios.getSenario(selected);
        
        if(selectedSenario==null){
            ((CardLayout)senarioLevelBtnPanel.getLayout()).show(senarioLevelBtnPanel, "construct");
            ((CardLayout)flowSelectionLevelPanel.getLayout()).show(flowSelectionLevelPanel, "edit");
            refreshFlowComboBox();
        } else{
            ((CardLayout)senarioLevelBtnPanel.getLayout()).show(senarioLevelBtnPanel, "edit");
            ((CardLayout)flowSelectionLevelPanel.getLayout()).show(flowSelectionLevelPanel, "view");
            flowSelectedLabel.setText(selectedSenario.getFlow().toString());
            if(editing){
                renderSenarioSourceEditPanel(selectedSenario);
            } else{
                renderSenarioSourceViewPanel(selectedSenario);
            }
        }             
        
    }
    
    private void renderDatasetPanel(RegistryItem selected){
        ((CardLayout)contentPanel.getLayout()).show(contentPanel, "dataset");
        
        datasetNameLabel.setText(selected.getName());
        addAttributePanel.setVisible(false);
        attributeActionTriggerPanel.setVisible(false);
        
        DefaultTableModel attributeTableModel = new DefaultTableModel();

        attributeTableModel.addColumn("Name");
        attributeTableModel.addColumn("Format"); 
        attributeTableModel.addColumn("Field Name");
        
        attributeTable.setModel(attributeTableModel);        
        
        Dataset selectedDataset = datasets.getDataset(selected);
        
        if(selectedDataset==null){
            ((CardLayout)datasetLevelBtnPanel.getLayout()).show(datasetLevelBtnPanel, "construct");
            
        } else{
            ((CardLayout)datasetLevelBtnPanel.getLayout()).show(datasetLevelBtnPanel, "edit");

            renderDatasetAttributePanel(selectedDataset);
        }        
    }
    
    private void renderComponentPanel(RegistryItem selected){
        
        ((CardLayout)contentPanel.getLayout()).show(contentPanel, "component");
        
        componentNameLabel.setText(selected.getName());

        providerComboBox.setVisible(false);
        providerLabel.setVisible(true);
        providerChangeBtn.setVisible(true);
        providerSetBtn.setVisible(false);

        DefaultTableModel parameterTableModel = new DefaultTableModel();

        parameterTableModel.addColumn("Name");
        parameterTableModel.addColumn("Format");    
        
        componentParameterTable.setModel(parameterTableModel);

        DefaultTableModel requiredInputTableModel = new DefaultTableModel();

        requiredInputTableModel.addColumn("Name");
        requiredInputTableModel.addColumn("Format");    
        
        componentInputTable.setModel(requiredInputTableModel);        

        DefaultTableModel generatedOutputTableModel = new DefaultTableModel();

        generatedOutputTableModel.addColumn("Name");
        generatedOutputTableModel.addColumn("Format");    
        
        componentOutputTable.setModel(generatedOutputTableModel);               

        DefaultTableModel methodTableModel = new DefaultTableModel();

        methodTableModel.addColumn("Name"); 
        
        componentMethodTable.setModel(methodTableModel);               
        
        Component selectedComponent = components.getComponent(selected);

        if (selectedComponent == null) {
            providerLabel.setText(null);
            componentTypeLabel.setText(null);
            providerDeleteBtn.setVisible(false);
        } else {
            componentTypeLabel.setText(selectedComponent.getType().name());
            providerLabel.setText(selectedComponent.getProvider().getName());
            providerDeleteBtn.setVisible(true);
            
            List<ParameterDefinition> parameters = selectedComponent.getParameters();
            for(ParameterDefinition parameter : parameters){                
                Object[] parameterData = new Object[2];
                parameterData[0] = parameter;
                parameterData[1] = parameter.getFormat();
                parameterTableModel.addRow(parameterData);
            }
            
            List<ParameterDefinition> requiredInputs = selectedComponent.getRequiredInputs();
            for(ParameterDefinition requiredInput : requiredInputs){                
                Object[] requiredInputData = new Object[2];
                requiredInputData[0] = requiredInput;
                requiredInputData[1] = requiredInput.getFormat();
                requiredInputTableModel.addRow(requiredInputData);
            }            

            List<ParameterDefinition> generatedOutputs = selectedComponent.getGeneratedOutputs();
            for(ParameterDefinition generatedOutput : generatedOutputs){                
                Object[] generatedOutputData = new Object[2];
                generatedOutputData[0] = generatedOutput;
                generatedOutputData[1] = generatedOutput.getFormat();
                generatedOutputTableModel.addRow(generatedOutputData);
            }        
            
            List<Method> methods = selectedComponent.getMethods();
            for(Method method : methods){
                Object[] methodData = new Object[1];
                methodData[0] = method;
                methodTableModel.addRow(methodData);                
            }
        }
    }
    
    private void generateRegistryTree(){

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
                } else if(childItem.getType()==RegistryItemType.COMPONENT || childItem.getType()==RegistryItemType.FLOW || childItem.getType()==RegistryItemType.DATASET || childItem.getType()==RegistryItemType.SENARIO ){
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childItem);
                    currentNode.add(childNode);              
                }
            }
        }
        
        registryTree.setModel(new DefaultTreeModel(root));
    }
    
    private static class RegistryTreeCellRenderer extends DefaultTreeCellRenderer {

        @Override
        public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            // decide what icons you want by examining the node
            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                
                RegistryItem item = (RegistryItem) node.getUserObject();
                if (item.getType()==RegistryItemType.CATEGORY) {               
                    setIcon(UIManager.getIcon("FileView.directoryIcon"));
                } else if (item.getType()==RegistryItemType.COMPONENT) {
                    setIcon(UIManager.getIcon("FileView.fileIcon"));
                } else if (item.getType()==RegistryItemType.FLOW){
                    setIcon(UIManager.getIcon("FileView.fileIcon"));
                } else if (item.getType()==RegistryItemType.DATASET){
                    setIcon(UIManager.getIcon("FileView.fileIcon"));
                } else if (item.getType()==RegistryItemType.SENARIO){
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
        addComponentItem = new javax.swing.JMenuItem();
        addFlowItem = new javax.swing.JMenuItem();
        addDatasetItem = new javax.swing.JMenuItem();
        addSenariotItem = new javax.swing.JMenuItem();
        renameCategory = new javax.swing.JMenuItem();
        removeCategory = new javax.swing.JMenuItem();
        componentPopupMenu = new javax.swing.JPopupMenu();
        renameComponent = new javax.swing.JMenuItem();
        removeComponent = new javax.swing.JMenuItem();
        flowPopupMenu = new javax.swing.JPopupMenu();
        renameFlow = new javax.swing.JMenuItem();
        removeFlow = new javax.swing.JMenuItem();
        datasetPopupMenu = new javax.swing.JPopupMenu();
        renameDataset = new javax.swing.JMenuItem();
        removeDataset = new javax.swing.JMenuItem();
        senarioPopupMenu = new javax.swing.JPopupMenu();
        renameSenario = new javax.swing.JMenuItem();
        removeSenario = new javax.swing.JMenuItem();
        nodeConfigDialog = new javax.swing.JDialog();
        nodeConfigHeaderPanel = new javax.swing.JPanel();
        nodeNamePanel = new javax.swing.JPanel();
        nodeNameConfigTag = new javax.swing.JLabel();
        nodeNameTextField = new javax.swing.JTextField();
        underlyingComponentPanel = new javax.swing.JPanel();
        underlyingComponentTag = new javax.swing.JLabel();
        underlyingComponentLabel = new javax.swing.JLabel();
        configContentPanel = new javax.swing.JPanel();
        parameterConfigPanel = new javax.swing.JPanel();
        parameterConfigTablePanel = new javax.swing.JScrollPane();
        parameterConfigTable = new javax.swing.JTable();
        inputConfigPanel = new javax.swing.JPanel();
        inputConfigTablePanel = new javax.swing.JScrollPane();
        inputConfigTable = new javax.swing.JTable();
        outputConfigPanel = new javax.swing.JPanel();
        outputConfigTablePanel = new javax.swing.JScrollPane();
        outputConfigTable = new javax.swing.JTable();
        methodConfigPanel = new javax.swing.JPanel();
        methodConfigTablePanel = new javax.swing.JScrollPane();
        methodConfigTable = new javax.swing.JTable();
        nodeConfigActionPanel = new javax.swing.JPanel();
        nodeAddDialog = new javax.swing.JDialog();
        nodeAddHeaderPanel = new javax.swing.JPanel();
        nodeAddNamePanel = new javax.swing.JPanel();
        nodeNameAddTag = new javax.swing.JLabel();
        nodeNameAddTextField = new javax.swing.JTextField();
        underlyingComponentAddPanel = new javax.swing.JPanel();
        underlyingComponentAddTag = new javax.swing.JLabel();
        underlyingComponentAddLabel = new javax.swing.JLabel();
        nodeAddConfigPanel = new javax.swing.JPanel();
        addNodeContentPanel = new javax.swing.JPanel();
        parameterAddPanel = new javax.swing.JPanel();
        parameterAddTablePanel = new javax.swing.JScrollPane();
        parameterAddTable = new javax.swing.JTable();
        inputAddPanel = new javax.swing.JPanel();
        inputAddTablePanel = new javax.swing.JScrollPane();
        inputAddTable = new javax.swing.JTable();
        outputAddPanel = new javax.swing.JPanel();
        outputAddTablePanel = new javax.swing.JScrollPane();
        outputAddTable = new javax.swing.JTable();
        methodAddPanel = new javax.swing.JPanel();
        methodAddTablePanel = new javax.swing.JScrollPane();
        methodAddTable = new javax.swing.JTable();
        componentListPanel = new javax.swing.JPanel();
        componentListScrollPanel = new javax.swing.JScrollPane();
        componentConfigList = new javax.swing.JList();
        nodeAddActionPanel = new javax.swing.JPanel();
        nodeAddSaveBtn = new javax.swing.JButton();
        nodeAddCloseBtn = new javax.swing.JButton();
        edgeConfigDialog = new javax.swing.JDialog();
        edgeConfigHeaderPanel = new javax.swing.JPanel();
        nodeInfoPanel = new javax.swing.JPanel();
        upstreamNodeInfoPanel = new javax.swing.JPanel();
        upstreamNodeInfoTag = new javax.swing.JLabel();
        upstreamNodeInfoLabel = new javax.swing.JLabel();
        downstreamNodeInfoPanel = new javax.swing.JPanel();
        downstreamNodeInfoTag = new javax.swing.JLabel();
        downstreamNodeInfoLabel = new javax.swing.JLabel();
        componentInfoPanel = new javax.swing.JPanel();
        edgeConfigComponentTag = new javax.swing.JLabel();
        edgeConfigComponentLabel = new javax.swing.JLabel();
        componentConfigInfoPanel = new javax.swing.JPanel();
        configEdgeContentPanel = new javax.swing.JPanel();
        parameterEdgeConfigPanel = new javax.swing.JPanel();
        parameterEdgeConfigTablePanel = new javax.swing.JScrollPane();
        parameterConfigEdgeTable = new javax.swing.JTable();
        inputEdgeConfigPanel = new javax.swing.JPanel();
        inputEdgeConfigTablePanel = new javax.swing.JScrollPane();
        inputConfigEdgeTable = new javax.swing.JTable();
        outputEdgeConfigPanel = new javax.swing.JPanel();
        outputEdgeConfigTablePanel = new javax.swing.JScrollPane();
        outputConfigEdgeTable = new javax.swing.JTable();
        methodEdgeConfigPanel = new javax.swing.JPanel();
        methodEdgeConfigTablePanel = new javax.swing.JScrollPane();
        methodConfigEdgeTable = new javax.swing.JTable();
        edgeConfigActionPanel = new javax.swing.JPanel();
        edgeAddDialog = new javax.swing.JDialog();
        nodeSelectionPanel = new javax.swing.JPanel();
        upstreamNodePanel = new javax.swing.JPanel();
        upstreamNodeTag = new javax.swing.JLabel();
        upstreamNodeComboBox = new javax.swing.JComboBox();
        downstreamNodePanel = new javax.swing.JPanel();
        downstreamNodeTag = new javax.swing.JLabel();
        downstreamNodeComboBox = new javax.swing.JComboBox();
        componentSelectionPanel = new javax.swing.JPanel();
        componentEdgeListPanel = new javax.swing.JPanel();
        componentEdgeListScrollPanel = new javax.swing.JScrollPane();
        edgeComponentList = new javax.swing.JList();
        addEdgeContentPanel = new javax.swing.JPanel();
        parameterEdgePanel = new javax.swing.JPanel();
        parameterEdgeTablePanel = new javax.swing.JScrollPane();
        parameterEdgeTable = new javax.swing.JTable();
        inputEdgePanel = new javax.swing.JPanel();
        inputEdgeTablePanel = new javax.swing.JScrollPane();
        inputEdgeTable = new javax.swing.JTable();
        outputEdgePanel = new javax.swing.JPanel();
        outputEdgeTablePanel = new javax.swing.JScrollPane();
        outputEdgeTable = new javax.swing.JTable();
        methodEdgePanel = new javax.swing.JPanel();
        methodEdgeTablePanel = new javax.swing.JScrollPane();
        methodEdgeTable = new javax.swing.JTable();
        edgeAddActionPanel = new javax.swing.JPanel();
        edgeAddSaveBtn = new javax.swing.JButton();
        edgeAddCloseBtn = new javax.swing.JButton();
        toolManagerPanel = new javax.swing.JPanel();
        registryPanel = new javax.swing.JPanel();
        registryTreePanel = new javax.swing.JPanel();
        registryTree = new javax.swing.JTree();
        registryBtnPanel = new javax.swing.JPanel();
        initBtn = new javax.swing.JButton();
        loadBtn = new javax.swing.JButton();
        saveBtn = new javax.swing.JButton();
        contentPanel = new javax.swing.JPanel();
        componentPanel = new javax.swing.JPanel();
        componentHeaderPanel = new javax.swing.JPanel();
        componentBasicPanel = new javax.swing.JPanel();
        componentNameTag = new javax.swing.JLabel();
        componentNameLabel = new javax.swing.JLabel();
        componentTypeTag = new javax.swing.JLabel();
        componentTypeLabel = new javax.swing.JLabel();
        providerPanel = new javax.swing.JPanel();
        providerChoicepanel = new javax.swing.JPanel();
        providerLabel = new javax.swing.JLabel();
        providerComboBox = new javax.swing.JComboBox();
        providerBtnPanel = new javax.swing.JPanel();
        providerSetBtn = new javax.swing.JButton();
        providerChangeBtn = new javax.swing.JButton();
        providerDeleteBtn = new javax.swing.JButton();
        componentParameterPanel = new javax.swing.JPanel();
        componentParameterTablePanel = new javax.swing.JScrollPane();
        componentParameterTable = new javax.swing.JTable();
        componentInputsPanel = new javax.swing.JPanel();
        componentInputTablePanel = new javax.swing.JScrollPane();
        componentInputTable = new javax.swing.JTable();
        componentOutputsPanel = new javax.swing.JPanel();
        componentOutputTablePanel = new javax.swing.JScrollPane();
        componentOutputTable = new javax.swing.JTable();
        componentMethodPanel = new javax.swing.JPanel();
        componentMethodTablePanel = new javax.swing.JScrollPane();
        componentMethodTable = new javax.swing.JTable();
        flowPanel = new javax.swing.JPanel();
        flowContentPanel = new javax.swing.JPanel();
        flowNodesPanel = new javax.swing.JPanel();
        flowEdgesPanel = new javax.swing.JPanel();
        flowHeaderPanel = new javax.swing.JPanel();
        flowBasicsPanel = new javax.swing.JPanel();
        flowNameTag = new javax.swing.JLabel();
        flowNameLabel = new javax.swing.JLabel();
        flowHeaderBtnPanel = new javax.swing.JPanel();
        flowActionBtnPanel = new javax.swing.JPanel();
        flowLevelBtnPanel = new javax.swing.JPanel();
        constructFlowBtn = new javax.swing.JButton();
        editFlowBtn = new javax.swing.JButton();
        viewFlowBtn = new javax.swing.JButton();
        deleteFlowBtn = new javax.swing.JButton();
        nodeActionPanel = new javax.swing.JPanel();
        addNewNodeBtn = new javax.swing.JButton();
        edgeActionPanel = new javax.swing.JPanel();
        addEdgeBtn = new javax.swing.JButton();
        datasetPanel = new javax.swing.JPanel();
        datasetHeaderPanel = new javax.swing.JPanel();
        datasetBasicsPanel = new javax.swing.JPanel();
        datasetNameTag = new javax.swing.JLabel();
        datasetNameLabel = new javax.swing.JLabel();
        datasetBtnPanel = new javax.swing.JPanel();
        datasetActionBtnPanel = new javax.swing.JPanel();
        datasetLevelBtnPanel = new javax.swing.JPanel();
        constructDatasetBtn = new javax.swing.JButton();
        editDatasetBtn = new javax.swing.JButton();
        viewDatasetBtn = new javax.swing.JButton();
        deleteDatasetBtn = new javax.swing.JButton();
        attributeActionTriggerPanel = new javax.swing.JPanel();
        addAttributeTriggerBtn = new javax.swing.JButton();
        saveAttributeBtn = new javax.swing.JButton();
        removeAttributeBtn = new javax.swing.JButton();
        datasetContentPanel = new javax.swing.JPanel();
        attributeTablePanel = new javax.swing.JScrollPane();
        attributeTable = new javax.swing.JTable();
        addAttributePanel = new javax.swing.JPanel();
        addAttributeNamePanel = new javax.swing.JPanel();
        addAttributeNameLabel = new javax.swing.JLabel();
        addAttributeNameTextField = new javax.swing.JTextField();
        addAttributeFormatPanel = new javax.swing.JPanel();
        addAttributeFormatLabel = new javax.swing.JLabel();
        addAttributeFormatTextField = new javax.swing.JTextField();
        addAttributeFieldNamePanel = new javax.swing.JPanel();
        addAttributeFieldNameLabel = new javax.swing.JLabel();
        addAttributeFieldNameTextField = new javax.swing.JTextField();
        attributeActionBtnPanel = new javax.swing.JPanel();
        addAttributeBtn = new javax.swing.JButton();
        senarioPanel = new javax.swing.JPanel();
        senarioHeaderPanel = new javax.swing.JPanel();
        senarioBasicsPanel = new javax.swing.JPanel();
        senarioNamePanel = new javax.swing.JPanel();
        senarioNameTag = new javax.swing.JLabel();
        senarioNameLabel = new javax.swing.JLabel();
        senarioFlowPanel = new javax.swing.JPanel();
        senarioFlowTag = new javax.swing.JLabel();
        flowSelectionLevelPanel = new javax.swing.JPanel();
        flowSelectedLabel = new javax.swing.JLabel();
        flowSelectedComboBox = new javax.swing.JComboBox();
        senarioBtnPanel = new javax.swing.JPanel();
        senarioActionBtnPanel = new javax.swing.JPanel();
        senarioLevelBtnPanel = new javax.swing.JPanel();
        constructSenarioBtn = new javax.swing.JButton();
        editSenarioBtn = new javax.swing.JButton();
        viewSenarioBtn = new javax.swing.JButton();
        deleteSenarioBtn = new javax.swing.JButton();
        sourceActionBtnPanel = new javax.swing.JPanel();
        saveSourcesBtn = new javax.swing.JButton();
        senarioContentPanel = new javax.swing.JPanel();
        sourceTablePanel = new javax.swing.JScrollPane();
        sourceTable = new javax.swing.JTable();
        workbenchPanel = new javax.swing.JPanel();
        workbenchActionPanel = new javax.swing.JPanel();
        runBtn = new javax.swing.JButton();
        categoryPanel = new javax.swing.JPanel();

        addCategoryItem.setText("Add Category");
        addCategoryItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCategoryItemActionPerformed(evt);
            }
        });
        categoryPopupMenu.add(addCategoryItem);

        addComponentItem.setText("Add Component");
        addComponentItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addComponentItemActionPerformed(evt);
            }
        });
        categoryPopupMenu.add(addComponentItem);

        addFlowItem.setText("Add Flow");
        addFlowItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFlowItemActionPerformed(evt);
            }
        });
        categoryPopupMenu.add(addFlowItem);

        addDatasetItem.setText("Add Dataset");
        addDatasetItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDatasetItemActionPerformed(evt);
            }
        });
        categoryPopupMenu.add(addDatasetItem);

        addSenariotItem.setText("Add Senario");
        addSenariotItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSenariotItemActionPerformed(evt);
            }
        });
        categoryPopupMenu.add(addSenariotItem);

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

        renameComponent.setText("Rename");
        renameComponent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameComponentActionPerformed(evt);
            }
        });
        componentPopupMenu.add(renameComponent);

        removeComponent.setText("Remove");
        removeComponent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeComponentActionPerformed(evt);
            }
        });
        componentPopupMenu.add(removeComponent);

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

        renameDataset.setText("Rename");
        renameDataset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameDatasetActionPerformed(evt);
            }
        });
        datasetPopupMenu.add(renameDataset);

        removeDataset.setText("Remove");
        removeDataset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeDatasetActionPerformed(evt);
            }
        });
        datasetPopupMenu.add(removeDataset);

        renameSenario.setText("Rename");
        renameSenario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameSenarioActionPerformed(evt);
            }
        });
        senarioPopupMenu.add(renameSenario);

        removeSenario.setText("Remove");
        removeSenario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSenarioActionPerformed(evt);
            }
        });
        senarioPopupMenu.add(removeSenario);

        nodeConfigDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        nodeConfigDialog.setTitle("Node Configuration");
        nodeConfigDialog.setResizable(false);

        nodeConfigHeaderPanel.setLayout(new java.awt.GridLayout(1, 2));

        nodeNamePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Node Basic"));
        nodeNamePanel.setLayout(new java.awt.GridLayout(1, 2));

        nodeNameConfigTag.setText("Node Name: ");
        nodeNamePanel.add(nodeNameConfigTag);
        nodeNamePanel.add(nodeNameTextField);

        nodeConfigHeaderPanel.add(nodeNamePanel);

        underlyingComponentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Component"));
        underlyingComponentPanel.setLayout(new java.awt.GridLayout(1, 2));

        underlyingComponentTag.setText("Underlying Component: ");
        underlyingComponentPanel.add(underlyingComponentTag);
        underlyingComponentPanel.add(underlyingComponentLabel);

        nodeConfigHeaderPanel.add(underlyingComponentPanel);

        nodeConfigDialog.getContentPane().add(nodeConfigHeaderPanel, java.awt.BorderLayout.NORTH);

        configContentPanel.setLayout(new java.awt.GridLayout(4, 1));

        parameterConfigPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Parameters"));
        parameterConfigPanel.setLayout(new java.awt.GridLayout(1, 0));

        parameterConfigTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        parameterConfigTablePanel.setViewportView(parameterConfigTable);

        parameterConfigPanel.add(parameterConfigTablePanel);

        configContentPanel.add(parameterConfigPanel);

        inputConfigPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Required Inputs"));
        inputConfigPanel.setLayout(new java.awt.GridLayout(1, 0));

        inputConfigTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        inputConfigTablePanel.setViewportView(inputConfigTable);

        inputConfigPanel.add(inputConfigTablePanel);

        configContentPanel.add(inputConfigPanel);

        outputConfigPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Generated Outputs"));
        outputConfigPanel.setLayout(new java.awt.GridLayout(1, 0));

        outputConfigTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        outputConfigTablePanel.setViewportView(outputConfigTable);

        outputConfigPanel.add(outputConfigTablePanel);

        configContentPanel.add(outputConfigPanel);

        methodConfigPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Functions"));
        methodConfigPanel.setLayout(new java.awt.GridLayout(1, 0));

        methodConfigTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        methodConfigTablePanel.setViewportView(methodConfigTable);

        methodConfigPanel.add(methodConfigTablePanel);

        configContentPanel.add(methodConfigPanel);

        nodeConfigDialog.getContentPane().add(configContentPanel, java.awt.BorderLayout.CENTER);

        nodeConfigActionPanel.setLayout(new java.awt.GridLayout(1, 3));
        nodeConfigDialog.getContentPane().add(nodeConfigActionPanel, java.awt.BorderLayout.SOUTH);

        nodeAddDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        nodeAddDialog.setTitle("Add Node");
        nodeAddDialog.setResizable(false);

        nodeAddHeaderPanel.setLayout(new java.awt.GridLayout(1, 2));

        nodeAddNamePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Node Basic"));
        nodeAddNamePanel.setLayout(new java.awt.GridLayout(1, 2));

        nodeNameAddTag.setText("Node Name: ");
        nodeAddNamePanel.add(nodeNameAddTag);
        nodeAddNamePanel.add(nodeNameAddTextField);

        nodeAddHeaderPanel.add(nodeAddNamePanel);

        underlyingComponentAddPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Component"));
        underlyingComponentAddPanel.setLayout(new java.awt.GridLayout(1, 2));

        underlyingComponentAddTag.setText("Underlying Component: ");
        underlyingComponentAddPanel.add(underlyingComponentAddTag);
        underlyingComponentAddPanel.add(underlyingComponentAddLabel);

        nodeAddHeaderPanel.add(underlyingComponentAddPanel);

        nodeAddDialog.getContentPane().add(nodeAddHeaderPanel, java.awt.BorderLayout.NORTH);

        nodeAddConfigPanel.setLayout(new java.awt.BorderLayout());

        addNodeContentPanel.setLayout(new java.awt.GridLayout(4, 1));

        parameterAddPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Parameters"));
        parameterAddPanel.setLayout(new java.awt.GridLayout(1, 0));

        parameterAddTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        parameterAddTablePanel.setViewportView(parameterAddTable);

        parameterAddPanel.add(parameterAddTablePanel);

        addNodeContentPanel.add(parameterAddPanel);

        inputAddPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Required Inputs"));
        inputAddPanel.setLayout(new java.awt.GridLayout(1, 0));

        inputAddTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        inputAddTablePanel.setViewportView(inputAddTable);

        inputAddPanel.add(inputAddTablePanel);

        addNodeContentPanel.add(inputAddPanel);

        outputAddPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Generated Outputs"));
        outputAddPanel.setLayout(new java.awt.GridLayout(1, 0));

        outputAddTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        outputAddTablePanel.setViewportView(outputAddTable);

        outputAddPanel.add(outputAddTablePanel);

        addNodeContentPanel.add(outputAddPanel);

        methodAddPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Functions"));
        methodAddPanel.setLayout(new java.awt.GridLayout(1, 0));

        methodAddTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        methodAddTablePanel.setViewportView(methodAddTable);

        methodAddPanel.add(methodAddTablePanel);

        addNodeContentPanel.add(methodAddPanel);

        nodeAddConfigPanel.add(addNodeContentPanel, java.awt.BorderLayout.CENTER);

        componentListPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Components"));
        componentListPanel.setMinimumSize(new java.awt.Dimension(2000, 2000));
        componentListPanel.setLayout(new java.awt.BorderLayout(0, 20));

        componentConfigList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        componentConfigList.setPreferredSize(new java.awt.Dimension(100, 80));
        componentListScrollPanel.setViewportView(componentConfigList);

        componentListPanel.add(componentListScrollPanel, java.awt.BorderLayout.CENTER);

        nodeAddConfigPanel.add(componentListPanel, java.awt.BorderLayout.EAST);

        nodeAddDialog.getContentPane().add(nodeAddConfigPanel, java.awt.BorderLayout.CENTER);

        nodeAddActionPanel.setLayout(new java.awt.GridLayout(1, 2));

        nodeAddSaveBtn.setText("Add");
        nodeAddSaveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nodeAddSaveBtnActionPerformed(evt);
            }
        });
        nodeAddActionPanel.add(nodeAddSaveBtn);

        nodeAddCloseBtn.setText("Close");
        nodeAddCloseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nodeAddCloseBtnActionPerformed(evt);
            }
        });
        nodeAddActionPanel.add(nodeAddCloseBtn);

        nodeAddDialog.getContentPane().add(nodeAddActionPanel, java.awt.BorderLayout.SOUTH);

        edgeConfigDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        edgeConfigDialog.setTitle("Add Edge");
        edgeConfigDialog.setResizable(false);

        edgeConfigHeaderPanel.setLayout(new java.awt.BorderLayout());

        nodeInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Node Selection"));
        nodeInfoPanel.setLayout(new java.awt.GridLayout(1, 2, 25, 0));

        upstreamNodeInfoPanel.setLayout(new java.awt.GridLayout(1, 2));

        upstreamNodeInfoTag.setText("Upsream: ");
        upstreamNodeInfoPanel.add(upstreamNodeInfoTag);
        upstreamNodeInfoPanel.add(upstreamNodeInfoLabel);

        nodeInfoPanel.add(upstreamNodeInfoPanel);

        downstreamNodeInfoPanel.setLayout(new java.awt.GridLayout(1, 2));

        downstreamNodeInfoTag.setText("Downstream: ");
        downstreamNodeInfoPanel.add(downstreamNodeInfoTag);
        downstreamNodeInfoPanel.add(downstreamNodeInfoLabel);

        nodeInfoPanel.add(downstreamNodeInfoPanel);

        edgeConfigHeaderPanel.add(nodeInfoPanel, java.awt.BorderLayout.CENTER);

        componentInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Component"));
        componentInfoPanel.setLayout(new java.awt.GridLayout(1, 0));

        edgeConfigComponentTag.setText("Component: ");
        componentInfoPanel.add(edgeConfigComponentTag);
        componentInfoPanel.add(edgeConfigComponentLabel);

        edgeConfigHeaderPanel.add(componentInfoPanel, java.awt.BorderLayout.EAST);

        edgeConfigDialog.getContentPane().add(edgeConfigHeaderPanel, java.awt.BorderLayout.NORTH);

        componentConfigInfoPanel.setToolTipText("");
        componentConfigInfoPanel.setLayout(new java.awt.BorderLayout());

        configEdgeContentPanel.setLayout(new java.awt.GridLayout(4, 1));

        parameterEdgeConfigPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Parameters"));
        parameterEdgeConfigPanel.setLayout(new java.awt.GridLayout(1, 0));

        parameterConfigEdgeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        parameterEdgeConfigTablePanel.setViewportView(parameterConfigEdgeTable);

        parameterEdgeConfigPanel.add(parameterEdgeConfigTablePanel);

        configEdgeContentPanel.add(parameterEdgeConfigPanel);

        inputEdgeConfigPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Required Inputs"));
        inputEdgeConfigPanel.setLayout(new java.awt.GridLayout(1, 0));

        inputConfigEdgeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        inputEdgeConfigTablePanel.setViewportView(inputConfigEdgeTable);

        inputEdgeConfigPanel.add(inputEdgeConfigTablePanel);

        configEdgeContentPanel.add(inputEdgeConfigPanel);

        outputEdgeConfigPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Generated Outputs"));
        outputEdgeConfigPanel.setLayout(new java.awt.GridLayout(1, 0));

        outputConfigEdgeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        outputEdgeConfigTablePanel.setViewportView(outputConfigEdgeTable);

        outputEdgeConfigPanel.add(outputEdgeConfigTablePanel);

        configEdgeContentPanel.add(outputEdgeConfigPanel);

        methodEdgeConfigPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Functions"));
        methodEdgeConfigPanel.setLayout(new java.awt.GridLayout(1, 0));

        methodConfigEdgeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        methodEdgeConfigTablePanel.setViewportView(methodConfigEdgeTable);

        methodEdgeConfigPanel.add(methodEdgeConfigTablePanel);

        configEdgeContentPanel.add(methodEdgeConfigPanel);

        componentConfigInfoPanel.add(configEdgeContentPanel, java.awt.BorderLayout.CENTER);

        edgeConfigDialog.getContentPane().add(componentConfigInfoPanel, java.awt.BorderLayout.CENTER);

        edgeConfigActionPanel.setLayout(new java.awt.GridLayout(1, 3));
        edgeConfigDialog.getContentPane().add(edgeConfigActionPanel, java.awt.BorderLayout.SOUTH);

        edgeAddDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        edgeAddDialog.setTitle("Add Edge");
        edgeAddDialog.setResizable(false);

        nodeSelectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Node Selection"));
        nodeSelectionPanel.setLayout(new java.awt.GridLayout(1, 2, 25, 0));

        upstreamNodePanel.setLayout(new java.awt.GridLayout(1, 2));

        upstreamNodeTag.setText("Upsream: ");
        upstreamNodePanel.add(upstreamNodeTag);

        upstreamNodeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        upstreamNodePanel.add(upstreamNodeComboBox);

        nodeSelectionPanel.add(upstreamNodePanel);

        downstreamNodePanel.setLayout(new java.awt.GridLayout(1, 2));

        downstreamNodeTag.setText("Downstream: ");
        downstreamNodePanel.add(downstreamNodeTag);

        downstreamNodeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        downstreamNodePanel.add(downstreamNodeComboBox);

        nodeSelectionPanel.add(downstreamNodePanel);

        edgeAddDialog.getContentPane().add(nodeSelectionPanel, java.awt.BorderLayout.PAGE_START);

        componentSelectionPanel.setToolTipText("");
        componentSelectionPanel.setLayout(new java.awt.BorderLayout());

        componentEdgeListPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Components"));
        componentEdgeListPanel.setLayout(new java.awt.BorderLayout());

        edgeComponentList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        edgeComponentList.setPreferredSize(new java.awt.Dimension(100, 80));
        componentEdgeListScrollPanel.setViewportView(edgeComponentList);

        componentEdgeListPanel.add(componentEdgeListScrollPanel, java.awt.BorderLayout.CENTER);

        componentSelectionPanel.add(componentEdgeListPanel, java.awt.BorderLayout.LINE_END);

        addEdgeContentPanel.setLayout(new java.awt.GridLayout(4, 1));

        parameterEdgePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Parameters"));
        parameterEdgePanel.setLayout(new java.awt.GridLayout(1, 0));

        parameterEdgeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        parameterEdgeTablePanel.setViewportView(parameterEdgeTable);

        parameterEdgePanel.add(parameterEdgeTablePanel);

        addEdgeContentPanel.add(parameterEdgePanel);

        inputEdgePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Required Inputs"));
        inputEdgePanel.setLayout(new java.awt.GridLayout(1, 0));

        inputEdgeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        inputEdgeTablePanel.setViewportView(inputEdgeTable);

        inputEdgePanel.add(inputEdgeTablePanel);

        addEdgeContentPanel.add(inputEdgePanel);

        outputEdgePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Generated Outputs"));
        outputEdgePanel.setLayout(new java.awt.GridLayout(1, 0));

        outputEdgeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        outputEdgeTablePanel.setViewportView(outputEdgeTable);

        outputEdgePanel.add(outputEdgeTablePanel);

        addEdgeContentPanel.add(outputEdgePanel);

        methodEdgePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Functions"));
        methodEdgePanel.setLayout(new java.awt.GridLayout(1, 0));

        methodEdgeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        methodEdgeTablePanel.setViewportView(methodEdgeTable);

        methodEdgePanel.add(methodEdgeTablePanel);

        addEdgeContentPanel.add(methodEdgePanel);

        componentSelectionPanel.add(addEdgeContentPanel, java.awt.BorderLayout.CENTER);

        edgeAddDialog.getContentPane().add(componentSelectionPanel, java.awt.BorderLayout.CENTER);

        edgeAddActionPanel.setLayout(new java.awt.GridLayout(1, 2));

        edgeAddSaveBtn.setText("Add");
        edgeAddSaveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edgeAddSaveBtnActionPerformed(evt);
            }
        });
        edgeAddActionPanel.add(edgeAddSaveBtn);

        edgeAddCloseBtn.setText("Close");
        edgeAddCloseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edgeAddCloseBtnActionPerformed(evt);
            }
        });
        edgeAddActionPanel.add(edgeAddCloseBtn);

        edgeAddDialog.getContentPane().add(edgeAddActionPanel, java.awt.BorderLayout.SOUTH);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("KDD Tools Manager");
        setResizable(false);

        toolManagerPanel.setLayout(new java.awt.BorderLayout());

        registryPanel.setLayout(new java.awt.BorderLayout());

        registryTreePanel.setLayout(new java.awt.BorderLayout());

        registryTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                registryTreeMouseClicked(evt);
            }
        });
        registryTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                registryTreeValueChanged(evt);
            }
        });
        registryTreePanel.add(registryTree, java.awt.BorderLayout.CENTER);

        registryPanel.add(registryTreePanel, java.awt.BorderLayout.CENTER);

        registryBtnPanel.setLayout(new java.awt.GridLayout(1, 3));

        initBtn.setText("Initialize");
        initBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                initBtnActionPerformed(evt);
            }
        });
        registryBtnPanel.add(initBtn);

        loadBtn.setText("Load");
        loadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadBtnActionPerformed(evt);
            }
        });
        registryBtnPanel.add(loadBtn);

        saveBtn.setText("Save");
        saveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBtnActionPerformed(evt);
            }
        });
        registryBtnPanel.add(saveBtn);

        registryPanel.add(registryBtnPanel, java.awt.BorderLayout.SOUTH);

        toolManagerPanel.add(registryPanel, java.awt.BorderLayout.WEST);

        contentPanel.setLayout(new java.awt.CardLayout());

        componentPanel.setLayout(new java.awt.GridLayout(5, 1));

        componentHeaderPanel.setLayout(new java.awt.BorderLayout());

        componentBasicPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Basic"));
        componentBasicPanel.setLayout(new java.awt.GridLayout(2, 2));

        componentNameTag.setText("Component Name:");
        componentBasicPanel.add(componentNameTag);
        componentBasicPanel.add(componentNameLabel);

        componentTypeTag.setText("Component Type:");
        componentBasicPanel.add(componentTypeTag);
        componentBasicPanel.add(componentTypeLabel);

        componentHeaderPanel.add(componentBasicPanel, java.awt.BorderLayout.WEST);

        providerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Provider"));
        providerPanel.setLayout(new java.awt.GridLayout(2, 1));

        providerChoicepanel.setLayout(new javax.swing.BoxLayout(providerChoicepanel, javax.swing.BoxLayout.LINE_AXIS));
        providerChoicepanel.add(providerLabel);

        providerComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        providerChoicepanel.add(providerComboBox);

        providerPanel.add(providerChoicepanel);

        providerBtnPanel.setLayout(new javax.swing.BoxLayout(providerBtnPanel, javax.swing.BoxLayout.LINE_AXIS));

        providerSetBtn.setText("Set Provider");
        providerSetBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                providerSetBtnActionPerformed(evt);
            }
        });
        providerBtnPanel.add(providerSetBtn);

        providerChangeBtn.setText("Assign Provider");
        providerChangeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                providerChangeBtnActionPerformed(evt);
            }
        });
        providerBtnPanel.add(providerChangeBtn);

        providerDeleteBtn.setText("Delete Provider");
        providerDeleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                providerDeleteBtnActionPerformed(evt);
            }
        });
        providerBtnPanel.add(providerDeleteBtn);

        providerPanel.add(providerBtnPanel);

        componentHeaderPanel.add(providerPanel, java.awt.BorderLayout.CENTER);

        componentPanel.add(componentHeaderPanel);

        componentParameterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Parameters"));
        componentParameterPanel.setLayout(new java.awt.GridLayout(1, 0));

        componentParameterTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        componentParameterTablePanel.setViewportView(componentParameterTable);

        componentParameterPanel.add(componentParameterTablePanel);

        componentPanel.add(componentParameterPanel);

        componentInputsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Required Inputs"));
        componentInputsPanel.setLayout(new java.awt.GridLayout(1, 0));

        componentInputTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        componentInputTablePanel.setViewportView(componentInputTable);

        componentInputsPanel.add(componentInputTablePanel);

        componentPanel.add(componentInputsPanel);

        componentOutputsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Generated Outputs"));
        componentOutputsPanel.setLayout(new java.awt.GridLayout(1, 0));

        componentOutputTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        componentOutputTablePanel.setViewportView(componentOutputTable);

        componentOutputsPanel.add(componentOutputTablePanel);

        componentPanel.add(componentOutputsPanel);

        componentMethodPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Functions"));
        componentMethodPanel.setLayout(new java.awt.GridLayout(1, 0));

        componentMethodTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        componentMethodTablePanel.setViewportView(componentMethodTable);

        componentMethodPanel.add(componentMethodTablePanel);

        componentPanel.add(componentMethodPanel);

        contentPanel.add(componentPanel, "component");

        flowPanel.setLayout(new java.awt.BorderLayout());

        flowContentPanel.setLayout(new java.awt.GridLayout(1, 2));

        flowNodesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Nodes"));
        flowNodesPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));
        flowContentPanel.add(flowNodesPanel);

        flowEdgesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Edges"));
        flowEdgesPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));
        flowContentPanel.add(flowEdgesPanel);

        flowPanel.add(flowContentPanel, java.awt.BorderLayout.CENTER);

        flowHeaderPanel.setLayout(new java.awt.BorderLayout());

        flowBasicsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Basics"));
        flowBasicsPanel.setLayout(new java.awt.GridLayout(1, 2));

        flowNameTag.setText("Flow Name:");
        flowBasicsPanel.add(flowNameTag);
        flowBasicsPanel.add(flowNameLabel);

        flowHeaderPanel.add(flowBasicsPanel, java.awt.BorderLayout.WEST);

        flowHeaderBtnPanel.setLayout(new java.awt.GridLayout(1, 2));

        flowActionBtnPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Flow Actions"));
        flowActionBtnPanel.setLayout(new java.awt.GridLayout(1, 2));

        flowLevelBtnPanel.setLayout(new java.awt.CardLayout());

        constructFlowBtn.setText("Construct");
        constructFlowBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                constructFlowBtnActionPerformed(evt);
            }
        });
        flowLevelBtnPanel.add(constructFlowBtn, "construct");

        editFlowBtn.setText("Edit");
        editFlowBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editFlowBtnActionPerformed(evt);
            }
        });
        flowLevelBtnPanel.add(editFlowBtn, "edit");

        viewFlowBtn.setText("View");
        viewFlowBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewFlowBtnActionPerformed(evt);
            }
        });
        flowLevelBtnPanel.add(viewFlowBtn, "view");

        flowActionBtnPanel.add(flowLevelBtnPanel);

        deleteFlowBtn.setText("Delete");
        deleteFlowBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteFlowBtnActionPerformed(evt);
            }
        });
        flowActionBtnPanel.add(deleteFlowBtn);

        flowHeaderBtnPanel.add(flowActionBtnPanel);

        nodeActionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Node Actions"));
        nodeActionPanel.setLayout(new java.awt.GridLayout(1, 0));

        addNewNodeBtn.setText("Add Node");
        addNewNodeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addNewNodeBtnActionPerformed(evt);
            }
        });
        nodeActionPanel.add(addNewNodeBtn);

        flowHeaderBtnPanel.add(nodeActionPanel);

        edgeActionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Edge Actions"));
        edgeActionPanel.setToolTipText("");
        edgeActionPanel.setLayout(new java.awt.GridLayout(1, 0));

        addEdgeBtn.setText("Add Edge");
        addEdgeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEdgeBtnActionPerformed(evt);
            }
        });
        edgeActionPanel.add(addEdgeBtn);

        flowHeaderBtnPanel.add(edgeActionPanel);

        flowHeaderPanel.add(flowHeaderBtnPanel, java.awt.BorderLayout.CENTER);

        flowPanel.add(flowHeaderPanel, java.awt.BorderLayout.NORTH);

        contentPanel.add(flowPanel, "flow");

        datasetPanel.setLayout(new java.awt.BorderLayout());

        datasetHeaderPanel.setLayout(new java.awt.BorderLayout());

        datasetBasicsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Basics"));
        datasetBasicsPanel.setLayout(new java.awt.GridLayout(1, 2));

        datasetNameTag.setText("Dataset Name: ");
        datasetBasicsPanel.add(datasetNameTag);
        datasetBasicsPanel.add(datasetNameLabel);

        datasetHeaderPanel.add(datasetBasicsPanel, java.awt.BorderLayout.WEST);

        datasetBtnPanel.setLayout(new java.awt.GridLayout(1, 2));

        datasetActionBtnPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Dataset Actions"));
        datasetActionBtnPanel.setLayout(new java.awt.GridLayout(1, 2));

        datasetLevelBtnPanel.setLayout(new java.awt.CardLayout());

        constructDatasetBtn.setText("Construct");
        constructDatasetBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                constructDatasetBtnActionPerformed(evt);
            }
        });
        datasetLevelBtnPanel.add(constructDatasetBtn, "construct");

        editDatasetBtn.setText("Edit");
        editDatasetBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editDatasetBtnActionPerformed(evt);
            }
        });
        datasetLevelBtnPanel.add(editDatasetBtn, "edit");

        viewDatasetBtn.setText("View");
        viewDatasetBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewDatasetBtnActionPerformed(evt);
            }
        });
        datasetLevelBtnPanel.add(viewDatasetBtn, "view");

        datasetActionBtnPanel.add(datasetLevelBtnPanel);

        deleteDatasetBtn.setText("Delete");
        deleteDatasetBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteDatasetBtnActionPerformed(evt);
            }
        });
        datasetActionBtnPanel.add(deleteDatasetBtn);

        datasetBtnPanel.add(datasetActionBtnPanel);

        attributeActionTriggerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Attribute Actions"));
        attributeActionTriggerPanel.setLayout(new java.awt.GridLayout(1, 3));

        addAttributeTriggerBtn.setText("Add");
        addAttributeTriggerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAttributeTriggerBtnActionPerformed(evt);
            }
        });
        attributeActionTriggerPanel.add(addAttributeTriggerBtn);

        saveAttributeBtn.setText("Save");
        saveAttributeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAttributeBtnActionPerformed(evt);
            }
        });
        attributeActionTriggerPanel.add(saveAttributeBtn);

        removeAttributeBtn.setText("Remove");
        removeAttributeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAttributeBtnActionPerformed(evt);
            }
        });
        attributeActionTriggerPanel.add(removeAttributeBtn);

        datasetBtnPanel.add(attributeActionTriggerPanel);

        datasetHeaderPanel.add(datasetBtnPanel, java.awt.BorderLayout.CENTER);

        datasetPanel.add(datasetHeaderPanel, java.awt.BorderLayout.NORTH);

        datasetContentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Attributes"));
        datasetContentPanel.setLayout(new java.awt.GridLayout(1, 0));

        attributeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        attributeTablePanel.setViewportView(attributeTable);

        datasetContentPanel.add(attributeTablePanel);

        datasetPanel.add(datasetContentPanel, java.awt.BorderLayout.CENTER);

        addAttributePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Add Attribute"));
        addAttributePanel.setLayout(new java.awt.GridLayout(1, 4, 20, 0));

        addAttributeNamePanel.setLayout(new java.awt.GridLayout(1, 0));

        addAttributeNameLabel.setText("Name: ");
        addAttributeNamePanel.add(addAttributeNameLabel);
        addAttributeNamePanel.add(addAttributeNameTextField);

        addAttributePanel.add(addAttributeNamePanel);

        addAttributeFormatPanel.setLayout(new java.awt.GridLayout(1, 0));

        addAttributeFormatLabel.setText("Format: ");
        addAttributeFormatPanel.add(addAttributeFormatLabel);
        addAttributeFormatPanel.add(addAttributeFormatTextField);

        addAttributePanel.add(addAttributeFormatPanel);

        addAttributeFieldNamePanel.setLayout(new java.awt.GridLayout(1, 0));

        addAttributeFieldNameLabel.setText("Field Name: ");
        addAttributeFieldNamePanel.add(addAttributeFieldNameLabel);
        addAttributeFieldNamePanel.add(addAttributeFieldNameTextField);

        addAttributePanel.add(addAttributeFieldNamePanel);

        attributeActionBtnPanel.setLayout(new java.awt.GridLayout(1, 0));

        addAttributeBtn.setText("Add Attribute");
        addAttributeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAttributeBtnActionPerformed(evt);
            }
        });
        attributeActionBtnPanel.add(addAttributeBtn);

        addAttributePanel.add(attributeActionBtnPanel);

        datasetPanel.add(addAttributePanel, java.awt.BorderLayout.SOUTH);

        contentPanel.add(datasetPanel, "dataset");

        senarioPanel.setLayout(new java.awt.BorderLayout());

        senarioHeaderPanel.setLayout(new java.awt.BorderLayout());

        senarioBasicsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Basics"));
        senarioBasicsPanel.setLayout(new java.awt.GridLayout(1, 2, 10, 0));

        senarioNamePanel.setLayout(new java.awt.GridLayout(1, 2));

        senarioNameTag.setText("Senario Name: ");
        senarioNamePanel.add(senarioNameTag);
        senarioNamePanel.add(senarioNameLabel);

        senarioBasicsPanel.add(senarioNamePanel);

        senarioFlowPanel.setLayout(new java.awt.GridLayout(1, 2));

        senarioFlowTag.setText("Flow Name: ");
        senarioFlowPanel.add(senarioFlowTag);

        flowSelectionLevelPanel.setLayout(new java.awt.CardLayout());

        flowSelectedLabel.setText("jLabel1");
        flowSelectionLevelPanel.add(flowSelectedLabel, "view");

        flowSelectedComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        flowSelectionLevelPanel.add(flowSelectedComboBox, "edit");

        senarioFlowPanel.add(flowSelectionLevelPanel);

        senarioBasicsPanel.add(senarioFlowPanel);

        senarioHeaderPanel.add(senarioBasicsPanel, java.awt.BorderLayout.CENTER);

        senarioBtnPanel.setLayout(new java.awt.GridLayout(1, 2));

        senarioActionBtnPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Senario Actions"));
        senarioActionBtnPanel.setLayout(new java.awt.GridLayout(1, 2));

        senarioLevelBtnPanel.setLayout(new java.awt.CardLayout());

        constructSenarioBtn.setText("Construct");
        constructSenarioBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                constructSenarioBtnActionPerformed(evt);
            }
        });
        senarioLevelBtnPanel.add(constructSenarioBtn, "construct");

        editSenarioBtn.setText("Edit");
        editSenarioBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editSenarioBtnActionPerformed(evt);
            }
        });
        senarioLevelBtnPanel.add(editSenarioBtn, "edit");

        viewSenarioBtn.setText("View");
        viewSenarioBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewSenarioBtnActionPerformed(evt);
            }
        });
        senarioLevelBtnPanel.add(viewSenarioBtn, "view");

        senarioActionBtnPanel.add(senarioLevelBtnPanel);

        deleteSenarioBtn.setText("Delete");
        deleteSenarioBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSenarioBtnActionPerformed(evt);
            }
        });
        senarioActionBtnPanel.add(deleteSenarioBtn);

        senarioBtnPanel.add(senarioActionBtnPanel);

        sourceActionBtnPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Source Actions"));
        sourceActionBtnPanel.setLayout(new java.awt.GridLayout(1, 0));

        saveSourcesBtn.setText("Save");
        saveSourcesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSourcesBtnActionPerformed(evt);
            }
        });
        sourceActionBtnPanel.add(saveSourcesBtn);

        senarioBtnPanel.add(sourceActionBtnPanel);

        senarioHeaderPanel.add(senarioBtnPanel, java.awt.BorderLayout.EAST);

        senarioPanel.add(senarioHeaderPanel, java.awt.BorderLayout.NORTH);

        senarioContentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Sources"));
        senarioContentPanel.setLayout(new java.awt.GridLayout(1, 0));

        sourceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        sourceTablePanel.setViewportView(sourceTable);

        senarioContentPanel.add(sourceTablePanel);

        senarioPanel.add(senarioContentPanel, java.awt.BorderLayout.CENTER);

        workbenchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Workbench"));
        workbenchPanel.setLayout(new java.awt.GridLayout(1, 0, 20, 0));

        workbenchActionPanel.setLayout(new java.awt.GridLayout(1, 0));

        runBtn.setText("Run");
        runBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runBtnActionPerformed(evt);
            }
        });
        workbenchActionPanel.add(runBtn);

        workbenchPanel.add(workbenchActionPanel);

        senarioPanel.add(workbenchPanel, java.awt.BorderLayout.SOUTH);

        contentPanel.add(senarioPanel, "senario");
        contentPanel.add(categoryPanel, "category");

        toolManagerPanel.add(contentPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(toolManagerPanel, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void initBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_initBtnActionPerformed

        registry.initRegistry();
        generateRegistryTree();
    }//GEN-LAST:event_initBtnActionPerformed

    private void registryTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_registryTreeMouseClicked
        // TODO add your handling code here:
        if(SwingUtilities.isRightMouseButton(evt)){
            int row = registryTree.getClosestRowForLocation(evt.getX(), evt.getY());
            registryTree.setSelectionRow(row);
            
            RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode)registryTree.getLastSelectedPathComponent()).getUserObject();
            
            if(selected.getType()==RegistryItemType.CATEGORY){
                categoryPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
            } else if(selected.getType()==RegistryItemType.COMPONENT){
                componentPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
            } else if(selected.getType()==RegistryItemType.FLOW){
                flowPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
            } else if(selected.getType()==RegistryItemType.DATASET){
                datasetPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
            } else if(selected.getType()==RegistryItemType.SENARIO){
                senarioPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
            } 
        }
    }//GEN-LAST:event_registryTreeMouseClicked

    private void addCategoryItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCategoryItemActionPerformed
        String categoryName = JOptionPane.showInputDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.QUESTION_MESSAGE);
        
        if (!(categoryName == null || categoryName.equals(""))) {

            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent();
            RegistryItem selectedCategory = (RegistryItem) parentNode.getUserObject();

            RegistryItem newCategory = registry.addRegistryItem(selectedCategory, categoryName, RegistryItemType.CATEGORY);

            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newCategory);
            ((DefaultTreeModel) registryTree.getModel()).insertNodeInto(newNode, parentNode, parentNode.getChildCount());
        }
    }//GEN-LAST:event_addCategoryItemActionPerformed

    private void addComponentItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addComponentItemActionPerformed
        String componentName = JOptionPane.showInputDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.QUESTION_MESSAGE);

        if (!(componentName == null || componentName.equals(""))) {

            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent();
            RegistryItem selectedCategory = (RegistryItem) parentNode.getUserObject();

            RegistryItem newComponent = registry.addRegistryItem(selectedCategory, componentName, RegistryItemType.COMPONENT);

            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newComponent);
            ((DefaultTreeModel) registryTree.getModel()).insertNodeInto(newNode, parentNode, parentNode.getChildCount());
        }
    }//GEN-LAST:event_addComponentItemActionPerformed

    private void renameCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameCategoryActionPerformed
        String newName = JOptionPane.showInputDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.QUESTION_MESSAGE);
        if (!(newName==null||newName.equals(""))) {
            RegistryItem selectedCategory = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
            registry.changeRegistryItemName(selectedCategory, newName);
        }
    }//GEN-LAST:event_renameCategoryActionPerformed

    private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
        registry.saveRegistry();
    }//GEN-LAST:event_saveBtnActionPerformed

    private void loadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadBtnActionPerformed
        registry.loadRegistry();
        generateRegistryTree();
    }//GEN-LAST:event_loadBtnActionPerformed

    private void renameComponentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameComponentActionPerformed
        String newName = JOptionPane.showInputDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.QUESTION_MESSAGE);
        if (!(newName==null||newName.equals(""))) {
            RegistryItem selectedComponent = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
            registry.changeRegistryItemName(selectedComponent, newName);
        }
    }//GEN-LAST:event_renameComponentActionPerformed

    private void removeCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeCategoryActionPerformed
        int choice = JOptionPane.showConfirmDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.YES_NO_CANCEL_OPTION);
        
        if(choice==JOptionPane.YES_OPTION){
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)registryTree.getLastSelectedPathComponent();
            RegistryItem selectedCategory = (RegistryItem)selectedNode.getUserObject();
            
            registry.removeRegistryItem(selectedCategory);
            
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
            ((DefaultTreeModel)registryTree.getModel()).removeNodeFromParent(selectedNode);
        }
    }//GEN-LAST:event_removeCategoryActionPerformed

    private void removeComponentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeComponentActionPerformed
        int choice = JOptionPane.showConfirmDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.YES_NO_CANCEL_OPTION);
        
        if(choice==JOptionPane.YES_OPTION){
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)registryTree.getLastSelectedPathComponent();
            RegistryItem selectedComponent = (RegistryItem)selectedNode.getUserObject();
            
            registry.removeRegistryItem(selectedComponent);
            
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
            ((DefaultTreeModel)registryTree.getModel()).removeNodeFromParent(selectedNode);
        }
    }//GEN-LAST:event_removeComponentActionPerformed

    private void providerChangeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_providerChangeBtnActionPerformed
        refreshProviderComboBox();
        providerLabel.setVisible(false);
        providerComboBox.setVisible(true);
        providerChangeBtn.setVisible(false);
        providerSetBtn.setVisible(true);      
    }//GEN-LAST:event_providerChangeBtnActionPerformed

    private void providerSetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_providerSetBtnActionPerformed
        RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
        components.createComponentFromClass(selected, (Class) providerComboBox.getSelectedItem());
        renderComponentPanel(selected);
    }//GEN-LAST:event_providerSetBtnActionPerformed

    private void providerDeleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_providerDeleteBtnActionPerformed
        RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
        Component selectedComponent = components.getComponent(selected);
        components.removeComponent(selectedComponent);
        renderComponentPanel(selected);
    }//GEN-LAST:event_providerDeleteBtnActionPerformed

    private void addFlowItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFlowItemActionPerformed
        String flowName = JOptionPane.showInputDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.QUESTION_MESSAGE);

        if (!(flowName == null || flowName.equals(""))) {

            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent();
            RegistryItem selectedCategory = (RegistryItem) parentNode.getUserObject();

            RegistryItem newFlow = registry.addRegistryItem(selectedCategory, flowName, RegistryItemType.FLOW);

            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newFlow);
            ((DefaultTreeModel) registryTree.getModel()).insertNodeInto(newNode, parentNode, parentNode.getChildCount());
        }
    }//GEN-LAST:event_addFlowItemActionPerformed

    private void renameFlowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameFlowActionPerformed
        String newName = JOptionPane.showInputDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.QUESTION_MESSAGE);
        if (!(newName==null||newName.equals(""))) {
            RegistryItem selectedFlow = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
            registry.changeRegistryItemName(selectedFlow, newName);
        }
    }//GEN-LAST:event_renameFlowActionPerformed

    private void removeFlowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFlowActionPerformed
        int choice = JOptionPane.showConfirmDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.YES_NO_CANCEL_OPTION);

        if(choice==JOptionPane.YES_OPTION){
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)registryTree.getLastSelectedPathComponent();
            RegistryItem selectedComponent = (RegistryItem)selectedNode.getUserObject();

            registry.removeRegistryItem(selectedComponent);

            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
            ((DefaultTreeModel)registryTree.getModel()).removeNodeFromParent(selectedNode);
        }
    }//GEN-LAST:event_removeFlowActionPerformed

    private void registryTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_registryTreeValueChanged
        if (evt.getNewLeadSelectionPath() != null) {
            
            editing = false;
            
            RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
            if (selected.getType() == RegistryItemType.COMPONENT) {
                renderComponentPanel(selected);
            } else if (selected.getType() == RegistryItemType.FLOW) {
                renderFlowPanel(selected);
            } else if (selected.getType() == RegistryItemType.CATEGORY) {
                renderCategoryPanel(selected);
            } else if (selected.getType() == RegistryItemType.DATASET) {
                renderDatasetPanel(selected);
            } else if (selected.getType() == RegistryItemType.SENARIO) {
                renderSenarioPanel(selected);
            } 
        }
    }//GEN-LAST:event_registryTreeValueChanged

    private void constructFlowBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_constructFlowBtnActionPerformed
        editing = true;
        nodeActionPanel.setVisible(editing);
        edgeActionPanel.setVisible(editing);
        
        RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
        flows.createFlow(selected);

        ((CardLayout)flowLevelBtnPanel.getLayout()).show(flowLevelBtnPanel, "view");
    }//GEN-LAST:event_constructFlowBtnActionPerformed

    private void editFlowBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editFlowBtnActionPerformed
        editing = true;
        nodeActionPanel.setVisible(editing);
        edgeActionPanel.setVisible(editing);

        ((CardLayout)flowLevelBtnPanel.getLayout()).show(flowLevelBtnPanel, "view");
    }//GEN-LAST:event_editFlowBtnActionPerformed

    private void viewFlowBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewFlowBtnActionPerformed
        editing = false;
        nodeActionPanel.setVisible(editing);
        edgeActionPanel.setVisible(editing);

        ((CardLayout)flowLevelBtnPanel.getLayout()).show(flowLevelBtnPanel, "edit");

    }//GEN-LAST:event_viewFlowBtnActionPerformed

    private void deleteFlowBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteFlowBtnActionPerformed
        RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
        Flow selectedFlow = flows.getFlow(selected);
        flows.removeFlow(selectedFlow);
        renderFlowPanel(selected);
    }//GEN-LAST:event_deleteFlowBtnActionPerformed

    private void addNewNodeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addNewNodeBtnActionPerformed
        nodeAddDialog.setVisible(true);
        refreshComponentList();
    }//GEN-LAST:event_addNewNodeBtnActionPerformed

    private void nodeAddCloseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nodeAddCloseBtnActionPerformed
        nodeAddDialog.dispose();
    }//GEN-LAST:event_nodeAddCloseBtnActionPerformed

    private void nodeAddSaveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nodeAddSaveBtnActionPerformed
        String name = nodeNameAddTextField.getText();
        if (!(name == null || name.equals(""))) {

            int i;
            int rowCount, columnCount;

            Node node = new Node();
            node.setComponent((Component) componentConfigList.getSelectedValue());
            node.setName(name);

            DefaultTableModel parameterAddTableModel = ((DefaultTableModel) parameterAddTable.getModel());
            rowCount = parameterAddTableModel.getRowCount();
            columnCount = parameterAddTableModel.getColumnCount();

            for (i = 0; i < rowCount; i++) {
                ParameterValue param = new ParameterValue();
                param.setDefinition((ParameterDefinition) parameterAddTableModel.getValueAt(i, 0));
                param.setValue((String) parameterAddTableModel.getValueAt(i, columnCount - 1));
                node.addParameter(param);               
            }

            DefaultTableModel methodAddTableModel = ((DefaultTableModel) methodAddTable.getModel());
            rowCount = methodAddTableModel.getRowCount();
            columnCount = methodAddTableModel.getColumnCount();

            for (i = 0; i < rowCount; i++) {
                Method method = (Method) methodAddTableModel.getValueAt(i, 0);
                if (methodAddTableModel.getValueAt(i, columnCount - 1).toString().equalsIgnoreCase("true")) {
                    node.setMethod(method);
                    break;
                }
            }

            RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
            Flow selectedFlow = flows.getFlow(selected);

            flows.addNode(selectedFlow, node);

            renderNodesPanel(selectedFlow);

        }
    }//GEN-LAST:event_nodeAddSaveBtnActionPerformed

    private void addEdgeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEdgeBtnActionPerformed
        edgeAddDialog.setVisible(true);
        refreshEdgeComponentList();
        
        RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
        Flow selectedFlow = flows.getFlow(selected);
        
        refreshNodesComboBox(selectedFlow);
        
    }//GEN-LAST:event_addEdgeBtnActionPerformed

    private void edgeAddSaveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edgeAddSaveBtnActionPerformed

        Node upstreamNode = (Node) upstreamNodeComboBox.getSelectedItem();
        Node downsteamNode = (Node) downstreamNodeComboBox.getSelectedItem();
        
        if (upstreamNode != null && downsteamNode != null) {

            int i;
            int rowCount, columnCount;

            Edge edge = new Edge();
            edge.setComponent((Component) edgeComponentList.getSelectedValue());

            edge.setUpstream(upstreamNode);
            edge.setDownstream(downsteamNode);
            
            DefaultTableModel parameterEdgeTableModel = ((DefaultTableModel) parameterEdgeTable.getModel());
            rowCount = parameterEdgeTableModel.getRowCount();
            columnCount = parameterEdgeTableModel.getColumnCount();

            for (i = 0; i < rowCount; i++) {
                ParameterValue param = new ParameterValue();
                param.setDefinition((ParameterDefinition) parameterEdgeTableModel.getValueAt(i, 0));
                param.setValue((String) parameterEdgeTableModel.getValueAt(i, columnCount - 1));
                edge.addParameter(param);
            }

            DefaultTableModel methodEdgeTableModel = ((DefaultTableModel) methodEdgeTable.getModel());
            rowCount = methodEdgeTableModel.getRowCount();
            columnCount = methodEdgeTableModel.getColumnCount();

            for (i = 0; i < rowCount; i++) {
                Method method = (Method) methodEdgeTableModel.getValueAt(i, 0);
                if (methodEdgeTableModel.getValueAt(i, columnCount - 1).toString().equalsIgnoreCase("true")) {
                    edge.setMethod(method);
                    break;
                }
            }

            RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
            Flow selectedFlow = flows.getFlow(selected);

            flows.addEdge(selectedFlow, edge);

            renderEdgesPanel(selectedFlow);

        }
    }//GEN-LAST:event_edgeAddSaveBtnActionPerformed

    private void edgeAddCloseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edgeAddCloseBtnActionPerformed
        edgeAddDialog.dispose();
    }//GEN-LAST:event_edgeAddCloseBtnActionPerformed

    private void constructDatasetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_constructDatasetBtnActionPerformed
        editing = true;
        
        attributeActionTriggerPanel.setVisible(editing);
        
        RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
        datasets.createDataset(selected);

        ((CardLayout)datasetLevelBtnPanel.getLayout()).show(datasetLevelBtnPanel, "view");        
    }//GEN-LAST:event_constructDatasetBtnActionPerformed

    private void editDatasetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editDatasetBtnActionPerformed
        editing = true;
        
        attributeActionTriggerPanel.setVisible(editing);
        
        ((CardLayout)datasetLevelBtnPanel.getLayout()).show(datasetLevelBtnPanel, "view");   
    }//GEN-LAST:event_editDatasetBtnActionPerformed

    private void viewDatasetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewDatasetBtnActionPerformed
        editing = false;
        
        attributeActionTriggerPanel.setVisible(editing);
        addAttributePanel.setVisible(false);
        
        ((CardLayout)datasetLevelBtnPanel.getLayout()).show(datasetLevelBtnPanel, "edit"); 
    }//GEN-LAST:event_viewDatasetBtnActionPerformed

    private void deleteDatasetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteDatasetBtnActionPerformed
        RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
        Dataset selectedDataset = datasets.getDataset(selected);
        datasets.removeDataset(selectedDataset);
        renderDatasetPanel(selected);
    }//GEN-LAST:event_deleteDatasetBtnActionPerformed

    private void addDatasetItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addDatasetItemActionPerformed
        String datasetName = JOptionPane.showInputDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.QUESTION_MESSAGE);

        if (!(datasetName == null || datasetName.equals(""))) {

            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent();
            RegistryItem selectedCategory = (RegistryItem) parentNode.getUserObject();

            RegistryItem newDataset = registry.addRegistryItem(selectedCategory, datasetName, RegistryItemType.DATASET);

            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newDataset);
            ((DefaultTreeModel) registryTree.getModel()).insertNodeInto(newNode, parentNode, parentNode.getChildCount());
        }
    }//GEN-LAST:event_addDatasetItemActionPerformed

    private void renameDatasetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameDatasetActionPerformed
        String newName = JOptionPane.showInputDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.QUESTION_MESSAGE);
        if (!(newName==null||newName.equals(""))) {
            RegistryItem selectedDataset = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
            registry.changeRegistryItemName(selectedDataset, newName);
        }
    }//GEN-LAST:event_renameDatasetActionPerformed

    private void removeDatasetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeDatasetActionPerformed
        int choice = JOptionPane.showConfirmDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.YES_NO_CANCEL_OPTION);
        
        if(choice==JOptionPane.YES_OPTION){
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)registryTree.getLastSelectedPathComponent();
            RegistryItem selectedDataset = (RegistryItem)selectedNode.getUserObject();
            
            registry.removeRegistryItem(selectedDataset);
            
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
            ((DefaultTreeModel)registryTree.getModel()).removeNodeFromParent(selectedNode);
        }
    }//GEN-LAST:event_removeDatasetActionPerformed

    private void addAttributeTriggerBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAttributeTriggerBtnActionPerformed
        addAttributePanel.setVisible(true);
    }//GEN-LAST:event_addAttributeTriggerBtnActionPerformed

    private void addAttributeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAttributeBtnActionPerformed
        if(addAttributeNameTextField.getText()!=null && !addAttributeNameTextField.getText().equals("")
                && addAttributeFormatTextField.getText()!=null && !addAttributeFormatTextField.getText().equals("")
                && addAttributeFieldNameTextField.getText()!=null && !addAttributeFieldNameTextField.getText().equals("")){
            
            Attribute attribute = new Attribute();
            attribute.setName(addAttributeNameTextField.getText());
            attribute.setFormat(addAttributeFormatTextField.getText());
            attribute.setFieldName(addAttributeFieldNameTextField.getText());

            RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
            Dataset selectedDataset = datasets.getDataset(selected); 
            
            datasets.addAttribute(selectedDataset, attribute);
            
            renderDatasetAttributePanel(selectedDataset);
            
            addAttributePanel.setVisible(false);
        }
    }//GEN-LAST:event_addAttributeBtnActionPerformed

    private void removeAttributeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAttributeBtnActionPerformed
        Attribute attribute = (Attribute) attributeTable.getModel().getValueAt(attributeTable.getSelectedRow(), 0);
        
        if (attribute != null) {

            RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
            Dataset selectedDataset = datasets.getDataset(selected);

            datasets.removeAttribute(selectedDataset, attribute);
            renderDatasetAttributePanel(selectedDataset);        
        }

    }//GEN-LAST:event_removeAttributeBtnActionPerformed

    private void saveAttributeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAttributeBtnActionPerformed
        int i,j;
        int rowCount;

        DefaultTableModel attributeTableModel = ((DefaultTableModel) attributeTable.getModel());
        rowCount = attributeTableModel.getRowCount();

        for (i = 0; i < rowCount; i++) {
            
            Attribute attribute = (Attribute)attributeTableModel.getValueAt(i, 0);
            
            attribute.setFormat((String) attributeTableModel.getValueAt(i, 1));
            attribute.setFieldName((String) attributeTableModel.getValueAt(i, 2));
            
            datasets.saveAttribute(attribute);
            
        }

        RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
        Dataset selectedDataset = datasets.getDataset(selected);
        renderDatasetAttributePanel(selectedDataset);
        
    }//GEN-LAST:event_saveAttributeBtnActionPerformed

    private void constructSenarioBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_constructSenarioBtnActionPerformed
        editing = true;
        
        sourceActionBtnPanel.setVisible(editing);
        
        Flow selectedFlow = (Flow) flowSelectedComboBox.getSelectedItem();

        if(selectedFlow!=null){

            RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
            senarios.createSenario(selected, selectedFlow); 
            renderSenarioPanel(selected);
            sourceActionBtnPanel.setVisible(editing);
            ((CardLayout)senarioLevelBtnPanel.getLayout()).show(senarioLevelBtnPanel, "view"); 
            
        }
    }//GEN-LAST:event_constructSenarioBtnActionPerformed

    private void editSenarioBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editSenarioBtnActionPerformed
        editing = true;
        
        sourceActionBtnPanel.setVisible(editing);
        
        RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
        Senario selectedSenario = senarios.getSenario(selected);        
        renderSenarioSourceEditPanel(selectedSenario);

        ((CardLayout)senarioLevelBtnPanel.getLayout()).show(senarioLevelBtnPanel, "view");   
    }//GEN-LAST:event_editSenarioBtnActionPerformed

    private void viewSenarioBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewSenarioBtnActionPerformed
        editing = false;
        
        sourceActionBtnPanel.setVisible(editing);
        
        RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
        Senario selectedSenario = senarios.getSenario(selected);        
        renderSenarioSourceViewPanel(selectedSenario);
        ((CardLayout)senarioLevelBtnPanel.getLayout()).show(senarioLevelBtnPanel, "edit"); 
    }//GEN-LAST:event_viewSenarioBtnActionPerformed

    private void deleteSenarioBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSenarioBtnActionPerformed
        RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
        Senario selectedSenario = senarios.getSenario(selected);
        senarios.removeSenario(selectedSenario);
        renderSenarioPanel(selected);
    }//GEN-LAST:event_deleteSenarioBtnActionPerformed

    private void renameSenarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameSenarioActionPerformed
        String newName = JOptionPane.showInputDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.QUESTION_MESSAGE);
        if (!(newName==null||newName.equals(""))) {
            RegistryItem selectedSenario = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
            registry.changeRegistryItemName(selectedSenario, newName);
        }
    }//GEN-LAST:event_renameSenarioActionPerformed

    private void removeSenarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeSenarioActionPerformed
        int choice = JOptionPane.showConfirmDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.YES_NO_CANCEL_OPTION);
        
        if(choice==JOptionPane.YES_OPTION){
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)registryTree.getLastSelectedPathComponent();
            RegistryItem selectedSenario = (RegistryItem)selectedNode.getUserObject();
            
            registry.removeRegistryItem(selectedSenario);
            
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
            ((DefaultTreeModel)registryTree.getModel()).removeNodeFromParent(selectedNode);
        }
    }//GEN-LAST:event_removeSenarioActionPerformed

    private void addSenariotItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSenariotItemActionPerformed
        String datasetName = JOptionPane.showInputDialog(this, evt.getActionCommand(), evt.getActionCommand(), JOptionPane.QUESTION_MESSAGE);

        if (!(datasetName == null || datasetName.equals(""))) {

            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent();
            RegistryItem selectedSenario = (RegistryItem) parentNode.getUserObject();

            RegistryItem newSenario = registry.addRegistryItem(selectedSenario, datasetName, RegistryItemType.SENARIO);

            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newSenario);
            ((DefaultTreeModel) registryTree.getModel()).insertNodeInto(newNode, parentNode, parentNode.getChildCount());
        }
    }//GEN-LAST:event_addSenariotItemActionPerformed

    private void saveSourcesBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSourcesBtnActionPerformed

        int i;
        int rowCount, columnCount;

        DefaultTableModel sourceTableModel = ((DefaultTableModel) sourceTable.getModel());
        rowCount = sourceTableModel.getRowCount();
        columnCount = sourceTableModel.getColumnCount();

        for (i = 0; i < rowCount; i++) {
            Source source = (Source) sourceTableModel.getValueAt(i, 0);
            source.setDataset((Dataset) ((JComboBox) ((DefaultCellEditor) sourceTable.getCellEditor(i, 1)).getComponent()).getSelectedItem());
            source.setFilename((String) sourceTableModel.getValueAt(i, columnCount - 1));
            senarios.saveSource(source);
        }

    }//GEN-LAST:event_saveSourcesBtnActionPerformed

    private void runBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runBtnActionPerformed
        RegistryItem selected = (RegistryItem) ((DefaultMutableTreeNode) registryTree.getLastSelectedPathComponent()).getUserObject();
        Senario selectedSenario = senarios.getSenario(selected);
        
        Workbench workbench = new Workbench(selectedSenario);
        
        workbench.run();
        
    }//GEN-LAST:event_runBtnActionPerformed

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
            java.util.logging.Logger.getLogger(ToolManagerDisplay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ToolManagerDisplay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ToolManagerDisplay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ToolManagerDisplay.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ToolManagerDisplay().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAttributeBtn;
    private javax.swing.JLabel addAttributeFieldNameLabel;
    private javax.swing.JPanel addAttributeFieldNamePanel;
    private javax.swing.JTextField addAttributeFieldNameTextField;
    private javax.swing.JLabel addAttributeFormatLabel;
    private javax.swing.JPanel addAttributeFormatPanel;
    private javax.swing.JTextField addAttributeFormatTextField;
    private javax.swing.JLabel addAttributeNameLabel;
    private javax.swing.JPanel addAttributeNamePanel;
    private javax.swing.JTextField addAttributeNameTextField;
    private javax.swing.JPanel addAttributePanel;
    private javax.swing.JButton addAttributeTriggerBtn;
    private javax.swing.JMenuItem addCategoryItem;
    private javax.swing.JMenuItem addComponentItem;
    private javax.swing.JMenuItem addDatasetItem;
    private javax.swing.JButton addEdgeBtn;
    private javax.swing.JPanel addEdgeContentPanel;
    private javax.swing.JMenuItem addFlowItem;
    private javax.swing.JButton addNewNodeBtn;
    private javax.swing.JPanel addNodeContentPanel;
    private javax.swing.JMenuItem addSenariotItem;
    private javax.swing.JPanel attributeActionBtnPanel;
    private javax.swing.JPanel attributeActionTriggerPanel;
    private javax.swing.JTable attributeTable;
    private javax.swing.JScrollPane attributeTablePanel;
    private javax.swing.JPanel categoryPanel;
    private javax.swing.JPopupMenu categoryPopupMenu;
    private javax.swing.JPanel componentBasicPanel;
    private javax.swing.JPanel componentConfigInfoPanel;
    private javax.swing.JList componentConfigList;
    private javax.swing.JPanel componentEdgeListPanel;
    private javax.swing.JScrollPane componentEdgeListScrollPanel;
    private javax.swing.JPanel componentHeaderPanel;
    private javax.swing.JPanel componentInfoPanel;
    private javax.swing.JTable componentInputTable;
    private javax.swing.JScrollPane componentInputTablePanel;
    private javax.swing.JPanel componentInputsPanel;
    private javax.swing.JPanel componentListPanel;
    private javax.swing.JScrollPane componentListScrollPanel;
    private javax.swing.JPanel componentMethodPanel;
    private javax.swing.JTable componentMethodTable;
    private javax.swing.JScrollPane componentMethodTablePanel;
    private javax.swing.JLabel componentNameLabel;
    private javax.swing.JLabel componentNameTag;
    private javax.swing.JTable componentOutputTable;
    private javax.swing.JScrollPane componentOutputTablePanel;
    private javax.swing.JPanel componentOutputsPanel;
    private javax.swing.JPanel componentPanel;
    private javax.swing.JPanel componentParameterPanel;
    private javax.swing.JTable componentParameterTable;
    private javax.swing.JScrollPane componentParameterTablePanel;
    private javax.swing.JPopupMenu componentPopupMenu;
    private javax.swing.JPanel componentSelectionPanel;
    private javax.swing.JLabel componentTypeLabel;
    private javax.swing.JLabel componentTypeTag;
    private javax.swing.JPanel configContentPanel;
    private javax.swing.JPanel configEdgeContentPanel;
    private javax.swing.JButton constructDatasetBtn;
    private javax.swing.JButton constructFlowBtn;
    private javax.swing.JButton constructSenarioBtn;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel datasetActionBtnPanel;
    private javax.swing.JPanel datasetBasicsPanel;
    private javax.swing.JPanel datasetBtnPanel;
    private javax.swing.JPanel datasetContentPanel;
    private javax.swing.JPanel datasetHeaderPanel;
    private javax.swing.JPanel datasetLevelBtnPanel;
    private javax.swing.JLabel datasetNameLabel;
    private javax.swing.JLabel datasetNameTag;
    private javax.swing.JPanel datasetPanel;
    private javax.swing.JPopupMenu datasetPopupMenu;
    private javax.swing.JButton deleteDatasetBtn;
    private javax.swing.JButton deleteFlowBtn;
    private javax.swing.JButton deleteSenarioBtn;
    private javax.swing.JComboBox downstreamNodeComboBox;
    private javax.swing.JLabel downstreamNodeInfoLabel;
    private javax.swing.JPanel downstreamNodeInfoPanel;
    private javax.swing.JLabel downstreamNodeInfoTag;
    private javax.swing.JPanel downstreamNodePanel;
    private javax.swing.JLabel downstreamNodeTag;
    private javax.swing.JPanel edgeActionPanel;
    private javax.swing.JPanel edgeAddActionPanel;
    private javax.swing.JButton edgeAddCloseBtn;
    private javax.swing.JDialog edgeAddDialog;
    private javax.swing.JButton edgeAddSaveBtn;
    private javax.swing.JList edgeComponentList;
    private javax.swing.JPanel edgeConfigActionPanel;
    private javax.swing.JLabel edgeConfigComponentLabel;
    private javax.swing.JLabel edgeConfigComponentTag;
    private javax.swing.JDialog edgeConfigDialog;
    private javax.swing.JPanel edgeConfigHeaderPanel;
    private javax.swing.JButton editDatasetBtn;
    private javax.swing.JButton editFlowBtn;
    private javax.swing.JButton editSenarioBtn;
    private javax.swing.JPanel flowActionBtnPanel;
    private javax.swing.JPanel flowBasicsPanel;
    private javax.swing.JPanel flowContentPanel;
    private javax.swing.JPanel flowEdgesPanel;
    private javax.swing.JPanel flowHeaderBtnPanel;
    private javax.swing.JPanel flowHeaderPanel;
    private javax.swing.JPanel flowLevelBtnPanel;
    private javax.swing.JLabel flowNameLabel;
    private javax.swing.JLabel flowNameTag;
    private javax.swing.JPanel flowNodesPanel;
    private javax.swing.JPanel flowPanel;
    private javax.swing.JPopupMenu flowPopupMenu;
    private javax.swing.JComboBox flowSelectedComboBox;
    private javax.swing.JLabel flowSelectedLabel;
    private javax.swing.JPanel flowSelectionLevelPanel;
    private javax.swing.JButton initBtn;
    private javax.swing.JPanel inputAddPanel;
    private javax.swing.JTable inputAddTable;
    private javax.swing.JScrollPane inputAddTablePanel;
    private javax.swing.JTable inputConfigEdgeTable;
    private javax.swing.JPanel inputConfigPanel;
    private javax.swing.JTable inputConfigTable;
    private javax.swing.JScrollPane inputConfigTablePanel;
    private javax.swing.JPanel inputEdgeConfigPanel;
    private javax.swing.JScrollPane inputEdgeConfigTablePanel;
    private javax.swing.JPanel inputEdgePanel;
    private javax.swing.JTable inputEdgeTable;
    private javax.swing.JScrollPane inputEdgeTablePanel;
    private javax.swing.JButton loadBtn;
    private javax.swing.JPanel methodAddPanel;
    private javax.swing.JTable methodAddTable;
    private javax.swing.JScrollPane methodAddTablePanel;
    private javax.swing.JTable methodConfigEdgeTable;
    private javax.swing.JPanel methodConfigPanel;
    private javax.swing.JTable methodConfigTable;
    private javax.swing.JScrollPane methodConfigTablePanel;
    private javax.swing.JPanel methodEdgeConfigPanel;
    private javax.swing.JScrollPane methodEdgeConfigTablePanel;
    private javax.swing.JPanel methodEdgePanel;
    private javax.swing.JTable methodEdgeTable;
    private javax.swing.JScrollPane methodEdgeTablePanel;
    private javax.swing.JPanel nodeActionPanel;
    private javax.swing.JPanel nodeAddActionPanel;
    private javax.swing.JButton nodeAddCloseBtn;
    private javax.swing.JPanel nodeAddConfigPanel;
    private javax.swing.JDialog nodeAddDialog;
    private javax.swing.JPanel nodeAddHeaderPanel;
    private javax.swing.JPanel nodeAddNamePanel;
    private javax.swing.JButton nodeAddSaveBtn;
    private javax.swing.JPanel nodeConfigActionPanel;
    private javax.swing.JDialog nodeConfigDialog;
    private javax.swing.JPanel nodeConfigHeaderPanel;
    private javax.swing.JPanel nodeInfoPanel;
    private javax.swing.JLabel nodeNameAddTag;
    private javax.swing.JTextField nodeNameAddTextField;
    private javax.swing.JLabel nodeNameConfigTag;
    private javax.swing.JPanel nodeNamePanel;
    private javax.swing.JTextField nodeNameTextField;
    private javax.swing.JPanel nodeSelectionPanel;
    private javax.swing.JPanel outputAddPanel;
    private javax.swing.JTable outputAddTable;
    private javax.swing.JScrollPane outputAddTablePanel;
    private javax.swing.JTable outputConfigEdgeTable;
    private javax.swing.JPanel outputConfigPanel;
    private javax.swing.JTable outputConfigTable;
    private javax.swing.JScrollPane outputConfigTablePanel;
    private javax.swing.JPanel outputEdgeConfigPanel;
    private javax.swing.JScrollPane outputEdgeConfigTablePanel;
    private javax.swing.JPanel outputEdgePanel;
    private javax.swing.JTable outputEdgeTable;
    private javax.swing.JScrollPane outputEdgeTablePanel;
    private javax.swing.JPanel parameterAddPanel;
    private javax.swing.JTable parameterAddTable;
    private javax.swing.JScrollPane parameterAddTablePanel;
    private javax.swing.JTable parameterConfigEdgeTable;
    private javax.swing.JPanel parameterConfigPanel;
    private javax.swing.JTable parameterConfigTable;
    private javax.swing.JScrollPane parameterConfigTablePanel;
    private javax.swing.JPanel parameterEdgeConfigPanel;
    private javax.swing.JScrollPane parameterEdgeConfigTablePanel;
    private javax.swing.JPanel parameterEdgePanel;
    private javax.swing.JTable parameterEdgeTable;
    private javax.swing.JScrollPane parameterEdgeTablePanel;
    private javax.swing.JPanel providerBtnPanel;
    private javax.swing.JButton providerChangeBtn;
    private javax.swing.JPanel providerChoicepanel;
    private javax.swing.JComboBox providerComboBox;
    private javax.swing.JButton providerDeleteBtn;
    private javax.swing.JLabel providerLabel;
    private javax.swing.JPanel providerPanel;
    private javax.swing.JButton providerSetBtn;
    private javax.swing.JPanel registryBtnPanel;
    private javax.swing.JPanel registryPanel;
    private javax.swing.JTree registryTree;
    private javax.swing.JPanel registryTreePanel;
    private javax.swing.JButton removeAttributeBtn;
    private javax.swing.JMenuItem removeCategory;
    private javax.swing.JMenuItem removeComponent;
    private javax.swing.JMenuItem removeDataset;
    private javax.swing.JMenuItem removeFlow;
    private javax.swing.JMenuItem removeSenario;
    private javax.swing.JMenuItem renameCategory;
    private javax.swing.JMenuItem renameComponent;
    private javax.swing.JMenuItem renameDataset;
    private javax.swing.JMenuItem renameFlow;
    private javax.swing.JMenuItem renameSenario;
    private javax.swing.JButton runBtn;
    private javax.swing.JButton saveAttributeBtn;
    private javax.swing.JButton saveBtn;
    private javax.swing.JButton saveSourcesBtn;
    private javax.swing.JPanel senarioActionBtnPanel;
    private javax.swing.JPanel senarioBasicsPanel;
    private javax.swing.JPanel senarioBtnPanel;
    private javax.swing.JPanel senarioContentPanel;
    private javax.swing.JPanel senarioFlowPanel;
    private javax.swing.JLabel senarioFlowTag;
    private javax.swing.JPanel senarioHeaderPanel;
    private javax.swing.JPanel senarioLevelBtnPanel;
    private javax.swing.JLabel senarioNameLabel;
    private javax.swing.JPanel senarioNamePanel;
    private javax.swing.JLabel senarioNameTag;
    private javax.swing.JPanel senarioPanel;
    private javax.swing.JPopupMenu senarioPopupMenu;
    private javax.swing.JPanel sourceActionBtnPanel;
    private javax.swing.JTable sourceTable;
    private javax.swing.JScrollPane sourceTablePanel;
    private javax.swing.JPanel toolManagerPanel;
    private javax.swing.JLabel underlyingComponentAddLabel;
    private javax.swing.JPanel underlyingComponentAddPanel;
    private javax.swing.JLabel underlyingComponentAddTag;
    private javax.swing.JLabel underlyingComponentLabel;
    private javax.swing.JPanel underlyingComponentPanel;
    private javax.swing.JLabel underlyingComponentTag;
    private javax.swing.JComboBox upstreamNodeComboBox;
    private javax.swing.JLabel upstreamNodeInfoLabel;
    private javax.swing.JPanel upstreamNodeInfoPanel;
    private javax.swing.JLabel upstreamNodeInfoTag;
    private javax.swing.JPanel upstreamNodePanel;
    private javax.swing.JLabel upstreamNodeTag;
    private javax.swing.JButton viewDatasetBtn;
    private javax.swing.JButton viewFlowBtn;
    private javax.swing.JButton viewSenarioBtn;
    private javax.swing.JPanel workbenchActionPanel;
    private javax.swing.JPanel workbenchPanel;
    // End of variables declaration//GEN-END:variables
}
