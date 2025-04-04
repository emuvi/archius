package br.com.pointel.archius;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
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
    private final JButton buttonCopy = new JButton("Copy");
    private final JButton buttonMove = new JButton("Move");
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
        adding.setReadOnly();
        textSource.setText(adding.getAbsolutePath());
        textSource.append("");
        buttonShelf.addActionListener(e -> actShelf());
        buttonNaming.addActionListener(e -> actNaming());
        buttonCopy.setMnemonic('C');
        buttonCopy.addActionListener(e -> actCopy());
        buttonMove.setMnemonic('D');
        buttonMove.addActionListener(e -> actMove());
        buttonCancel.setMnemonic('X');
        buttonCancel.addActionListener(e -> actCancel());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                initLoader();
            }
        });        
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
        panelActions.add(buttonCopy);
        panelActions.add(buttonMove);
        panelActions.add(buttonCancel);
        panelBody.add(panelActions, BorderLayout.SOUTH);
    }

    private void initLoader() {
        new Thread(() -> {
            try {
                var source =  new DochReader(adding).read();
                SwingUtilities.invokeLater(() -> {
                    textSource.append("\n\n");
                    textSource.append(source);
                });
            } catch (Exception e) {
                WizDesk.showError(e);
            }
        }, "DeskCatalog - Loader").start();
    }

    private void actShelf() {
        // | TODO | implement actShelf
    }

    private void actNaming() {
        // | TODO | implement actNaming
    }

    private void actCopy() {
        actConfirm(false);
    }

    private void actMove() {
        actConfirm(true);
    }

    private void actConfirm(boolean move) {
        try {
            var folder = catalogShelves.getShelf();
            var fullNamer = catalogNamers.getFullNamer();
            var finalName = ArchUtils.getFinalName(folder, fullNamer);
            var finalFile = new File(folder, finalName + "." + FilenameUtils.getExtension(adding.getName()));
            Files.copy(adding.toPath(), finalFile.toPath());
            if (move) {
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
