package br.com.pointel.archius;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.apache.commons.lang3.tuple.Pair;
import br.com.pointel.jarch.gears.SwingDropper;
import br.com.pointel.jarch.gears.SwingFramer;
import br.com.pointel.jarch.mage.WizBase;
import br.com.pointel.jarch.mage.WizChars;
import br.com.pointel.jarch.mage.WizDesk;
import br.com.pointel.jarch.mage.WizProps;

public class DeskOpen extends JFrame {

    private final ArchBase archBase;

    private final JPanel panelBody = new JPanel(new GridBagLayout());
    private final JButton buttonBrowse = new JButton("Browse");
    private final JTextField fieldSearch = new JTextField();
    private final JButton buttonSearch = new JButton("Search");
    private final JButton buttonCatalog = new JButton("Catalog");
    private final JButton buttonPlus = new JButton("+");
    private final JProgressBar progressBar = new JProgressBar();
    private final JPanel panelPlus = new JPanel(new GridBagLayout());
    private final JTextArea textStatus = new JTextArea();
    private final JScrollPane scrollStatus = new JScrollPane(textStatus);
    private final JButton buttonConfig = new JButton("Config");

    private volatile String lastStatus = "";

    private final SwingFramer framer;

    private Integer beforePlusWidth;
    private Integer beforePlusHeight;

    public DeskOpen(File folder) throws Exception {
        this.archBase = new ArchBase(folder);
        this.archBase.addListener(lastStatus -> this.lastStatus = lastStatus);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(WizDesk.getLogo());
        setSize(500, 400);
        setName("archius on " + folder.getName());
        setTitle("Archius on " + folder.getName());
        this.framer = WizDesk.initFrame(this);
        WizDesk.initEscaper(this);
        initComponents();
        archBase.load();
    }

    private void initComponents() {
        setContentPane(panelBody);
        panelBody.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        insertComponents();
        buttonBrowse.addActionListener(e -> actBrowse());
        buttonSearch.addActionListener(e -> actSearch());
        buttonCatalog.addActionListener(e -> actCatalog());
        new SwingDropper(files -> openCatalogsFor(files), buttonCatalog).init();
        buttonPlus.addActionListener(e -> actPlus());
        buttonConfig.addActionListener(e -> actConfig());
        textStatus.setEditable(false);
        textStatus.setLineWrap(true);
        textStatus.setWrapStyleWord(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                initUpdater();
                beforePlusWidth = WizProps.get(framer.getRootName() + "BEFORE_PLUS_WIDTH", getWidth());
                beforePlusHeight = WizProps.get(framer.getRootName() + "BEFORE_PLUS_HEIGHT", getHeight());
                panelPlus.setVisible(WizProps.get(framer.getRootName() + "PLUS_VISIBLE", panelPlus.isVisible()));
            }

            @Override
            public void windowClosing(WindowEvent e) {
                WizBase.closeAside(archBase);
                WizProps.set(framer.getRootName() + "BEFORE_PLUS_WIDTH", beforePlusWidth);
                WizProps.set(framer.getRootName() + "BEFORE_PLUS_HEIGHT", beforePlusHeight);
                WizProps.set(framer.getRootName() + "PLUS_VISIBLE", panelPlus.isVisible());
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
        panelBody.add(buttonBrowse, constraints);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.BOTH;
        panelBody.add(fieldSearch, constraints);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelBody.add(buttonSearch, constraints);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 3;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelBody.add(buttonCatalog, constraints);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 4;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelBody.add(buttonPlus, constraints);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 5;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelBody.add(progressBar, constraints);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 5;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        panelBody.add(panelPlus, constraints);
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        panelPlus.add(scrollStatus, constraints);
        constraints.anchor = GridBagConstraints.NORTHEAST;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelPlus.add(buttonConfig, constraints);
    }

    private void initUpdater() {
        new Thread(() -> {
            while (isDisplayable()) {
                updateStatus();
                WizBase.sleep(500);
            }
        }, "DeskOpen - Updater").start();
    }

    private void updateStatus() {
        var status = mountStatus();
        var selectionStart = textStatus.getSelectionStart();
        var selectionEnd = textStatus.getSelectionEnd();
        var horizontalPosition = scrollStatus.getHorizontalScrollBar().getValue();
        var verticalPosition = scrollStatus.getVerticalScrollBar().getValue();
        var progress = archBase.getProgress();
        SwingUtilities.invokeLater(() -> {
            textStatus.setText(status);
            textStatus.setSelectionStart(selectionStart);
            textStatus.setSelectionEnd(selectionEnd);
            scrollStatus.getHorizontalScrollBar().setValue(horizontalPosition);
            scrollStatus.getVerticalScrollBar().setValue(verticalPosition);
            progressBar.setValue(progress.intValue());
        });
    }

    private String mountStatus() {
        var grid = new ArrayList<Pair<String, String>>();
        grid.add(Pair.of("Loading Progress",
                        archBase.getProgressFormatted()));
        grid.add(Pair.of("Number Of Files",
                        archBase.getStatusNumberOfFiles().toString()));
        grid.add(Pair.of("Number Of Checked",
                        archBase.getStatusNumberOfChecked().toString()));
        grid.add(Pair.of("Number Of Verified",
                        archBase.getStatusNumberOfVerified().toString()));
        grid.add(Pair.of("Number Of Indexed",
                        archBase.getStatusNumberOfIndexed().toString()));
        grid.add(Pair.of("Number Of Linted",
                        archBase.getStatusNumberOfLinted().toString()));
        grid.add(Pair.of("Number Of Cleaned",
                        archBase.getStatusNumberOfCleaned().toString()));
        grid.add(Pair.of("Number Of Errors",
                        archBase.getStatusNumberOfErrors().toString()));
        var status = WizChars.mountGrid(grid).trim() + "\n\nLast Status:\n" + lastStatus;
        return status;
    }

    private void actBrowse() {
        try {
            new DeskBrowse(archBase).setVisible(true);
        } catch (Exception e) {
            WizDesk.showError(e);
        }
    }

    private void actSearch() {
        try {
            new DeskSearch(archBase.searchFor(fieldSearch.getText())).setVisible(true);
        } catch (Exception e) {
            WizDesk.showError(e);
        }
    }

    private void actCatalog() {
        try {
            var selected = WizDesk.selectFiles(null);
            if (selected != null) {
                openCatalogsFor(Arrays.asList(selected));
            }
        } catch (Exception e) {
            WizDesk.showError(e);
        }
    }

    private void openCatalogsFor(List<File> files) {
        for (var file : files) {
            try {
                new DeskCatalog(archBase, file).setVisible(true);
            } catch (Exception e) {
                WizDesk.showError(e);
            }
        }
    }

    private void actPlus() {
        var actualWidth = getWidth();
        var actualHeight = getHeight();
        panelPlus.setVisible(!panelPlus.isVisible());
        setSize(beforePlusWidth, beforePlusHeight);
        beforePlusWidth = actualWidth;
        beforePlusHeight = actualHeight;
    }

    private void actConfig() {
        try {
            new DeskConfig(archBase).setVisible(true);
        } catch (Exception e) {
            WizDesk.showError(e);
        }
    }

}
