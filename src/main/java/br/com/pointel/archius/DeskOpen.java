package br.com.pointel.archius;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.apache.commons.lang3.tuple.Pair;
import br.com.pointel.jarch.mage.WizBase;
import br.com.pointel.jarch.mage.WizChars;
import br.com.pointel.jarch.mage.WizDesk;

public class DeskOpen extends JFrame {

    private final ArchBase archBase;

    private final JPanel panelBody = new JPanel();
    private final JTextField fieldSearch = new JTextField();
    private final JButton buttonSearch = new JButton();
    private final JButton buttonInsert = new JButton();
    private final JScrollPane scrollStatus = new JScrollPane();
    private final JTextArea textStatus = new JTextArea();

    private volatile String lastStatus = "";

    public DeskOpen(File folder) throws Exception {
        this.archBase = new ArchBase(folder);
        this.archBase.addListener(lastStatus -> this.lastStatus = lastStatus);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(DeskIcon.getLogo());
        setSize(500, 400);
        setName("archius on " + folder.getName());
        setTitle("Archius on " + folder.getName());
        WizDesk.initFrame(this);
        WizDesk.initEscaper(this);
        initComponents();
        archBase.load();
    }

    private void initComponents() {
        setContentPane(panelBody);
        panelBody.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panelBody.setLayout(new GridBagLayout());
        insertComponents();
        buttonSearch.setText("Search");
        buttonInsert.setText("Insert");
        scrollStatus.setViewportView(textStatus);
        textStatus.setLineWrap(true);
        textStatus.setWrapStyleWord(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                initUpdater();        
            }

            @Override
            public void windowClosed(WindowEvent e) {
                WizBase.closeAside(archBase);
            }
        });
    }

    private void insertComponents() {
        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.BOTH;
        panelBody.add(fieldSearch, constraints);
        constraints.gridx = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelBody.add(buttonSearch, constraints);
        constraints.gridx = 2;
        panelBody.add(buttonInsert, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 3;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        panelBody.add(scrollStatus, constraints);
    }

    private void initUpdater() {
        new Thread(() -> {
            while (isDisplayable()) {
                WizBase.sleep(500);
                updateStatus();
            }
        }, "DeskOpen - Updater").start();
    }

    private void updateStatus() {
        var status = mountStatus();
        var selectionStart = textStatus.getSelectionStart();
        var selectionEnd = textStatus.getSelectionEnd();
        var horizontalPosition = scrollStatus.getHorizontalScrollBar().getValue();
        var verticalPosition = scrollStatus.getVerticalScrollBar().getValue();
        SwingUtilities.invokeLater(() -> {
            textStatus.setText(status);
            textStatus.setSelectionStart(selectionStart);
            textStatus.setSelectionEnd(selectionEnd);
            scrollStatus.getHorizontalScrollBar().setValue(horizontalPosition);
            scrollStatus.getVerticalScrollBar().setValue(verticalPosition);
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
        grid.add(Pair.of("Number Of Erros",
                        archBase.getStatusNumberOfErros().toString()));
        var status = WizChars.mountGrid(grid).trim() + "\n\nLast Status:\n" + lastStatus;
        return status;
    }

}
