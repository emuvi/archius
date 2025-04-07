package br.com.pointel.archius;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Objects;
import java.util.stream.IntStream;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import br.com.pointel.jarch.mage.WizDesk;

public class DeskBrowse extends JFrame {

    private final ArchBase archBase;

    private final JPanel panelBody = new JPanel(new GridBagLayout());
    private final JButton buttonUp = new JButton("↑");
    private final DefaultComboBoxModel<DisplaySubFolder> modelSubFolders = new DefaultComboBoxModel<>();
    private final JComboBox<DisplaySubFolder> comboSubFolders = new JComboBox<>(modelSubFolders);
    private final JTextField fieldSearch = new JTextField(); 
    private final JButton buttonSearch = new JButton("Search");
    private final DefaultListModel<DisplayFolder> modelFolder = new DefaultListModel<>();
    private final JList<DisplayFolder> listFolder = new JList<>(modelFolder);
    private final JScrollPane scrollFolder = new JScrollPane(listFolder);
    private final DefaultListModel<DisplayAssets> modelAssets = new DefaultListModel<>();
    private final JList<DisplayAssets> listAssets = new JList<>(modelAssets);
    private final JScrollPane scrollAssets = new JScrollPane(listAssets);

    public DeskBrowse(ArchBase archBase) {
        this.archBase = archBase;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(DeskIcon.getLogo());
        setSize(500, 600);
        setName("browse on " + archBase.getRoot().getName());
        setTitle("Browse on " + archBase.getRoot().getName());
        WizDesk.initFrame(this);
        WizDesk.initEscaper(this);
        initComponents();
    }

    private void initComponents() {
        setContentPane(panelBody);
        panelBody.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        insertComponents();
        buttonUp.addActionListener(e -> actUp(e));
        comboSubFolders.addActionListener(e -> selectSubFolder(e));
        buttonSearch.addActionListener(e -> actSearch(e));
        listFolder.addListSelectionListener(e -> listFolderSelectionChanged(e));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                loadFolders();
            }
        });
    }

    private void insertComponents() {
        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelBody.add(buttonUp, constraints);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelBody.add(comboSubFolders, constraints);
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelBody.add(fieldSearch, constraints);
        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelBody.add(buttonSearch, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 4;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        panelBody.add(scrollFolder, constraints);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 4;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        panelBody.add(scrollAssets, constraints);
    }

    private void loadFolders() {
        new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                modelSubFolders.removeAllElements();
                modelFolder.removeAllElements();
                modelAssets.removeAllElements();
            });
            loadFolders(archBase.getRoot(), 0);
        }).start();
    }

    private void loadFolders(File path, int depth) {
        if (path.isDirectory()) {
            SwingUtilities.invokeLater(() -> modelFolder.addElement(new DisplayFolder(path, depth)));
            for (var inside : path.listFiles()) {
                loadFolders(inside, depth + 1);
            }
        }
    }

    private void actUp(ActionEvent evt) {
        var selected = listFolder.getSelectedValue();
        if (selected != null) {
            select(selected.path.getParentFile());
        }
    }

    private void listFolderSelectionChanged(ListSelectionEvent evt) {
        try {
            loadingSubFolders = true;
            modelAssets.removeAllElements();
            modelSubFolders.removeAllElements();
            modelSubFolders.addElement(new DisplaySubFolderTitle());
            var allSelected = listFolder.getSelectedValuesList();
            if (allSelected == null || allSelected.isEmpty()) {
                return;
            }
            for (var selected : allSelected) {
                for (var inside : selected.path.listFiles()) {
                    if (inside.isFile()) {
                        modelAssets.addElement(new DisplayAssets(inside));
                    } else if (inside.isDirectory()) {
                        modelSubFolders.addElement(new DisplaySubFolder(inside));
                    }
                }
            }
            comboSubFolders.setSelectedIndex(0);
        } finally {
            loadingSubFolders = false;
        }
    }

    private void selectSubFolder(ActionEvent evt) {
        if (loadingSubFolders) {
            return;
        }
        var selected = comboSubFolders.getSelectedItem();
        if (selected != null) {
            select(((DisplaySubFolder) selected).path);
        }
    }

    private void select(File path) {
        listFolder.clearSelection();
        listAssets.clearSelection();
        if (path == null) {
            path = archBase.getRoot();
        }
        for (int i = 0; i < modelFolder.getSize(); i++) {
            if (Objects.equals(path.getParentFile(), modelFolder.get(i).path)) {
                listFolder.setSelectedValue(modelFolder.get(i), true);
            }
        }
        for (int i = 0; i < modelFolder.getSize(); i++) {
            if (Objects.equals(path, modelFolder.get(i).path)) {
                listFolder.setSelectedValue(modelFolder.get(i), true);
            }
        }
        listFolderSelectionChanged(null);
        for (int i = 0; i < modelAssets.getSize(); i++) {
            if (Objects.equals(path, modelAssets.get(i).path)) {
                listAssets.setSelectedValue(modelAssets.get(i), true);
            }
        }
        loadSubFolders();
    }

    private boolean loadingSubFolders = false;

    private void loadSubFolders() {
        try {
            loadingSubFolders = true;
            modelSubFolders.removeAllElements();
            modelSubFolders.addElement(new DisplaySubFolderTitle());
            var selectedFolder = listFolder.getSelectedValue();
            if (selectedFolder == null) {
                return;
            }
            for (var inside : selectedFolder.path.listFiles()) {
                if (inside.isDirectory()) {
                    modelSubFolders.addElement(new DisplaySubFolder(inside));
                }
            }
            comboSubFolders.setSelectedIndex(0);
        } finally {
            loadingSubFolders = false;
        }
    }

    private void actSearch(ActionEvent evt) {
        var search = fieldSearch.getText().toLowerCase().trim();
        if (search == null || search.isEmpty()) {
            return;
        }
        var selectedAsset = listAssets.getSelectedIndex();
        for (int iAsset = selectedAsset +1; iAsset < modelAssets.getSize(); iAsset++) {
            var asset = modelAssets.get(iAsset);
            if (asset.path.getName().toLowerCase().contains(search)) {
                listAssets.setSelectedValue(asset, true);
                listAssets.ensureIndexIsVisible(iAsset);
                return;
            }
        }
        var selectedFolder = listFolder.getSelectedIndex();
        for (int iFolder = selectedFolder +1; iFolder < modelFolder.getSize(); iFolder++) {
            var folder = modelFolder.get(iFolder);
            listFolder.setSelectedValue(folder, true);
            listFolder.ensureIndexIsVisible(iFolder);
            listFolderSelectionChanged(null);
            if (folder.path.getName().toLowerCase().contains(search)) {
                return;
            }
            for (int iAsset = 0; iAsset < modelAssets.getSize(); iAsset++) {
                var asset = modelAssets.get(iAsset);
                if (asset.path.getName().toLowerCase().contains(search)) {
                    listAssets.setSelectedValue(asset, true);
                    listAssets.ensureIndexIsVisible(iAsset);
                    return;
                }
            }
        }
        for (int iFolder = 0; iFolder < selectedFolder; iFolder++) {
            var folder = modelFolder.get(iFolder);
            listFolder.setSelectedValue(folder, true);
            listFolder.ensureIndexIsVisible(iFolder);
            listFolderSelectionChanged(null);
            if (folder.path.getName().toLowerCase().contains(search)) {
                return;
            }
            for (int iAsset = 0; iAsset < modelAssets.getSize(); iAsset++) {
                var asset = modelAssets.get(iAsset);
                if (asset.path.getName().toLowerCase().contains(search)) {
                    listAssets.setSelectedValue(asset, true);
                    listAssets.ensureIndexIsVisible(iAsset);
                    return;
                }
            }
        }
    }

    private class DisplayItem {
        File path;
        
        public DisplayItem(File path) {
            this.path = path;
        }
    }
    
    private class DisplayFolder extends DisplayItem {
        int depth;
        
        public DisplayFolder(File path, int depth) {
            super(path);
            this.depth = depth;
        }
        
        @Override
        public String toString() {
            var result = new StringBuilder("|");
            IntStream.range(0, depth)
                    .forEach((i) -> result.append( "-"));
            result.append("-> ");
            result.append(path.getName());
            return result.toString();
        }
    }
    
    private class DisplayAssets extends DisplayItem {
        public DisplayAssets(File path) {
            super(path);
        }
        
        @Override
        public String toString() {
            return "|-> " + path.getName();
        }
    }
    
    private class DisplaySubFolderTitle extends DisplaySubFolder {
        public DisplaySubFolderTitle() {
            super(null);
        }
        
        @Override
        public String toString() {
            return "<-- SubFolders -->";
        }
    }
    
    private class DisplaySubFolder {
        public final File path;
        
        public DisplaySubFolder(File path) {
            this.path = path;
        }
        
        @Override
        public String toString() {
            return path.getName();
        }
    }

}
