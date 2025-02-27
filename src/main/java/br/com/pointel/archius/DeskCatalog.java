package br.com.pointel.archius;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import br.com.pointel.jarch.mage.WizDesk;

public class DeskCatalog extends JFrame {

    private final ArchBase archBase;

    private final JPanel panelBody = new JPanel(new BorderLayout(4, 4));

    private final JPanel panelShelf = new JPanel(new GridBagLayout());
    private final JLabel labelShelf = new JLabel("Shelf");
    private final DeskCatalogShelves panelShelves;
    private final JScrollPane scrollShelves;
    private final JButton buttonShelf = new JButton("*");

    private final JPanel panelNaming = new JPanel(new GridBagLayout());
    private final JLabel labelNaming = new JLabel("Name");
    private final DeskCatalogNamers panelNames;
    private final JScrollPane scrollNaming;
    private final JButton buttonNaming = new JButton("*");

    private final JTextArea textSource = new JTextArea();
    private final JScrollPane scrollSource = new JScrollPane(textSource);

    private final JSplitPane splitNaming = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelNaming, scrollSource);
    private final JSplitPane splitShelf = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelShelf, splitNaming);

    public DeskCatalog(ArchBase archBase) throws Exception {
        this.archBase = archBase;
        this.panelShelves = new DeskCatalogShelves(this);
        this.scrollShelves = new JScrollPane(panelShelves);
        this.panelNames = new DeskCatalogNamers(Config.load(archBase.getRoot()).getNamers());
        this.scrollNaming = new JScrollPane(panelNames);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(DeskIcon.getLogo());
        setSize(800, 600);
        setName("catalog on " + archBase.getRoot().getName());
        setTitle("Catalog on " + archBase.getRoot().getName());
        WizDesk.initFrame(this);
        WizDesk.initEscaper(this);
        initComponents();
    }

    private void initComponents() {
        setContentPane(panelBody);
        panelBody.add(splitShelf, BorderLayout.CENTER);
        insertComponentsShelf();
        insertComponentsNaming();
        splitNaming.setName("naming");
        splitShelf.setName("shelf");
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

    public File getRoot() {
        return archBase.getRoot();
    }

}
