package br.com.pointel.archius;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import br.com.pointel.jarch.mage.WizDesk;

public class DeskInsert extends JFrame {

    private final ArchBase archBase;

    private final JPanel panelBody = new JPanel();

    private final JPanel panelShelf = new JPanel();
    private final JLabel labelShelf = new JLabel("Shelf");
    private final DefaultComboBoxModel<File> modelShelf = new DefaultComboBoxModel<File>();
    private final JComboBox<File> comboShelf = new JComboBox<>(modelShelf);
    private final JButton buttonShelf = new JButton("*");

    private final JPanel panelNaming = new JPanel();
    private final JLabel labelNaming = new JLabel("Naming");
    private final JTextField fieldName = new JTextField();

    private final JTextArea textSource = new JTextArea();
    private final JScrollPane scrollSource = new JScrollPane(textSource);

    public DeskInsert(ArchBase archBase) {
        this.archBase = archBase;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(DeskIcon.getLogo());
        setSize(800, 600);
        setName("search on " + archBase.getRoot().getName());
        setTitle("Search on " + archBase.getRoot().getName());
        WizDesk.initFrame(this);
        WizDesk.initEscaper(this);
        initComponents();
    }

    private void initComponents() {
        setContentPane(panelBody);
        panelBody.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panelBody.setLayout(new GridBagLayout());
        panelShelf.setLayout(new GridBagLayout());
        panelNaming.setLayout(new GridBagLayout());
        insertComponentsBody();
        insertComponentsShelf();
        insertComponentsNaming();
    }

    private void insertComponentsBody() {
        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelBody.add(panelShelf, constraints);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 2;
        constraints.weighty = 0;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelBody.add(panelNaming, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        panelBody.add(scrollSource, constraints);
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
        panelShelf.add(comboShelf, constraints);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.weighty = 1;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.VERTICAL;
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
        panelNaming.add(fieldName, constraints);
    }

}
