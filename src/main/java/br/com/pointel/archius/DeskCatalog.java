package br.com.pointel.archius;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import org.apache.commons.io.FilenameUtils;
import br.com.pointel.jarch.mage.WizDesk;

public class DeskCatalog extends JFrame {

    private final ArchBase archBase;
    private final File adding;

    private final JPanel panelBody = new JPanel(new BorderLayout(4, 4));

    private final JPanel panelShelf = new JPanel(new GridBagLayout());
    private final JLabel labelShelf = new JLabel("Shelf");
    private final DeskCatalogShelves catalogShelves;
    private final JScrollPane scrollShelves;
    private final JButton buttonShelf = new JButton("*");

    private final JPanel panelNaming = new JPanel(new GridBagLayout());
    private final JLabel labelNaming = new JLabel("Name");
    private final List<ConfigNamer> configNamers;
    private final DeskCatalogNamers catalogNamers;
    private final JScrollPane scrollNaming;
    private final JButton buttonNaming = new JButton("*");

    private final JTextArea textSource = new JTextArea();
    private final JScrollPane scrollSource = new JScrollPane(textSource);

    private final JSplitPane splitNaming = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelNaming, scrollSource);
    private final JSplitPane splitShelf = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelShelf, splitNaming);

    private final JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
    private final ButtonGroup groupActions = new ButtonGroup();
    private final JRadioButton radioCopy = new JRadioButton("Copy");
    private final JRadioButton radioMove = new JRadioButton("Move");
    private final JButton buttonConfirm = new JButton("Confirm");
    private final JButton buttonCancel = new JButton("Cancel");

    public DeskCatalog(ArchBase archBase, File adding) throws Exception {
        this.archBase = archBase;
        this.adding = adding;
        this.catalogShelves = new DeskCatalogShelves(this);
        this.scrollShelves = new JScrollPane(catalogShelves);
        this.configNamers = Config.load(archBase.getRoot()).getNamers();
        this.catalogNamers = new DeskCatalogNamers(configNamers);
        this.scrollNaming = new JScrollPane(catalogNamers);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(DeskIcon.getLogo());
        setSize(800, 600);
        setName("catalog on " + archBase.getRoot().getName());
        setTitle("Catalog on " + archBase.getRoot().getName());
        WizDesk.initFrame(this);
        WizDesk.initEscaper(this);
        initComponents();
    }

    public File getRoot() {
        return archBase.getRoot();
    }

    private void initComponents() {
        setContentPane(panelBody);
        insertComponentsShelf();
        insertComponentsNaming();
        insertComponentsActions();
        splitNaming.setName("naming");
        splitShelf.setName("shelf");
        radioCopy.setSelected(true);
        buttonShelf.addActionListener(e -> actShelf());
        buttonNaming.addActionListener(e -> actNaming());
        buttonConfirm.addActionListener(e -> actConfirm());
        buttonCancel.addActionListener(e -> actCancel());
    }

    private void insertComponentsShelf() {
        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelShelf.add(labelShelf, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.BOTH;
        panelShelf.add(scrollShelves, constraints);
        constraints.anchor = GridBagConstraints.SOUTHEAST;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        panelShelf.add(buttonShelf, constraints);
        panelBody.add(splitShelf, BorderLayout.CENTER);
    }

    private void insertComponentsNaming() {
        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelNaming.add(labelNaming, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.BOTH;
        panelNaming.add(scrollNaming, constraints);
        constraints.anchor = GridBagConstraints.SOUTHEAST;
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        panelNaming.add(buttonNaming, constraints);
    }

    private void insertComponentsActions() {
        groupActions.add(radioCopy);
        groupActions.add(radioMove);
        panelActions.add(radioCopy);
        panelActions.add(radioMove);
        panelActions.add(buttonConfirm);
        panelActions.add(buttonCancel);
        panelBody.add(panelActions, BorderLayout.SOUTH);
    }

    private void actShelf() {
        // |TODO| implement actShelf
    }

    private void actNaming() {
        // |TODO| implement actNaming
    }

    private void actConfirm() {
        try {
            var folder = catalogShelves.getShelf();
            var fullNamer = catalogNamers.getFullNamer();
            var finalName = ArchNamer.getFinalName(folder, fullNamer);
            var finalFile = new File(folder, finalName + "." + FilenameUtils.getExtension(adding.getName()));
            Files.copy(adding.toPath(), finalFile.toPath());
            if (radioMove.isSelected()) {
                adding.delete();
            }
            WizDesk.showInfo("Cataloged: " + finalFile.getName() + "\non Shelf: " + folder.getName());
            WizDesk.close(this);
        } catch (Exception e) {
            WizDesk.showError(e);
        }
    }

    private void actCancel() {
        WizDesk.close(this);
    }

}
