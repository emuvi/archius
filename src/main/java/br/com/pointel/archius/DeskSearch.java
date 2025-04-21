package br.com.pointel.archius;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import br.com.pointel.jarch.mage.WizBase;
import br.com.pointel.jarch.mage.WizDesk;

public class DeskSearch extends JFrame {

    private final ArchSearch archSearch;
    private final Integer rootLength;

    private final JPanel panelBody = new JPanel();
    private final JProgressBar progressBar = new JProgressBar(0, 100);
    private final DefaultListModel<Found> modelFounds = new DefaultListModel<>();
    private final JList<Found> listFounds = new JList<>(modelFounds);
    private final JScrollPane scrollFounds = new JScrollPane(listFounds);

    public DeskSearch(ArchSearch archSearch) {
        this.archSearch = archSearch;
        this.rootLength = archSearch.getRoot().getAbsolutePath().length();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(DeskIcon.getLogo());
        setSize(500, 400);
        setName("search on " + archSearch.getRoot().getName());
        setTitle("Search on " + archSearch.getRoot().getName());
        WizDesk.initFrame(this, Desk.DEFAULT_FONT);
        WizDesk.initEscaper(this);
        initComponents();
    }

    private void initComponents() {
        setContentPane(panelBody);
        panelBody.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panelBody.setLayout(new GridBagLayout());
        insertComponents();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                initUpdater();        
            }

            @Override
            public void windowClosed(WindowEvent e) {
                archSearch.stop();
            }
        });
        WizDesk.addDefaultAction(listFounds, e -> actOpen());
    }

    private void insertComponents() {
        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        panelBody.add(scrollFounds, constraints);
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelBody.add(progressBar, constraints);
    }

    private void initUpdater() {
        new Thread(() -> {
            while (isDisplayable()) {
                updateStatus();
                if (archSearch.gotAll()) {
                    break;
                }
                WizBase.sleep(500);
            }
        }, "DeskSearch - Updater").start();
    }

    private void updateStatus() {
        var list = new ArrayList<Found>();
        File file;
        while ((file = archSearch.pollFound()) != null) {
            list.add(new Found(file));
        }
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(archSearch.getProgress().intValue());
            modelFounds.addAll(list);
        });
    }

    private void actOpen() {
        var selected = listFounds.getSelectedValue();
        if (selected != null) {
            try {
                WizDesk.open(selected.getFile());
            } catch (Exception e) {
                WizDesk.showError(e);
            }
        }
    }

    private class Found {

        private final File file;

        public Found(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        @Override
        public String toString() {
            return file.getAbsolutePath().substring(rootLength);
        }

    }

}
