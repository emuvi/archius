package br.com.pointel.archius;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import br.com.pointel.jarch.mage.WizDesk;
import br.com.pointel.jarch.mage.WizString;

public class DeskConfigNamersEdit extends JFrame {

    private final DeskConfigNamers deskConfigNamers;
    private final Consumer<ConfigNamer> consumer;

    private final JPanel panelBody = new JPanel(new GridBagLayout());
    private final JLabel labelName = new JLabel("Name");
    private final JTextField fieldName = new JTextField();
    private final JLabel labelRequired = new JLabel("Required");
    private final JCheckBox checkRequired = new JCheckBox();
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JPanel panelCombo = new JPanel(new GridBagLayout());
    private final JLabel labelComboOptions = new JLabel("Options");
    private final JTextArea textComboOptions = new JTextArea();
    private final JScrollPane scrollComboOptions = new JScrollPane(textComboOptions);
    private final JLabel labelComboPrefix = new JLabel("Prefix");
    private final JTextField fieldComboPrefix = new JTextField();
    private final JLabel labelComboSuffix = new JLabel("Suffix");
    private final JTextField fieldComboSuffix = new JTextField();
    private final JPanel panelField = new JPanel(new GridBagLayout());
    private final JLabel labelFieldPrefix = new JLabel("Prefix");
    private final JTextField fieldFieldPrefix = new JTextField();
    private final JLabel labelFieldSuffix = new JLabel("Suffix");
    private final JTextField fieldFieldSuffix = new JTextField();
    private final JPanel panelSeparator = new JPanel(new GridBagLayout());
    private final JLabel labelSeparatorChars = new JLabel("Chars");
    private final JTextField fieldSeparatorChars = new JTextField();
    private final JPanel panelSerial = new JPanel(new GridBagLayout());
    private final JLabel labelSerialLength = new JLabel("Length");
    private final JSpinner fieldSerialLength = new JSpinner();
    private final JLabel labelSerialPrefix = new JLabel("Prefix");
    private final JTextField fieldSerialPrefix = new JTextField();
    private final JLabel labelSerialSuffix = new JLabel("Suffix");
    private final JTextField fieldSerialSuffix = new JTextField();
    private final JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
    private final JButton buttonConfirm = new JButton("Confirm");
    private final JButton buttonCancel = new JButton("Cancel");

    public DeskConfigNamersEdit(DeskConfigNamers deskConfigNamers, Consumer<ConfigNamer> consumer) {
        this.deskConfigNamers = deskConfigNamers;
        this.consumer = consumer;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(WizDesk.getLogo());
        setSize(300, 200);
        setName("namers edit on " + deskConfigNamers.getRoot().getName());
        setTitle("Namers Edit on " + deskConfigNamers.getRoot().getName());
        WizDesk.initFrame(this);
        WizDesk.initEscaper(this);
        initComponents();
    }

    public File getRoot() {
        return deskConfigNamers.getRoot();
    }

    public DeskConfigNamersEdit load(ConfigNamer namer) {
        fieldName.setText(namer.getName());
        checkRequired.setSelected(namer.isRequired());
        if (namer instanceof ConfigNamerCombo namerCombo) {
            tabbedPane.setSelectedIndex(0);
            textComboOptions.setText(String.join("\n", namerCombo.getOptions()));
            fieldComboPrefix.setText(namerCombo.getPrefix());
            fieldComboSuffix.setText(namerCombo.getSuffix());
        } else if (namer instanceof ConfigNamerField namerField) {
            tabbedPane.setSelectedIndex(1);
            fieldFieldPrefix.setText(namerField.getPrefix());
            fieldFieldSuffix.setText(namerField.getSuffix());
        } else if (namer instanceof ConfigNamerSeparator namerSeparator) {
            tabbedPane.setSelectedIndex(2);
            fieldSeparatorChars.setText(namerSeparator.getChars());
        } else if (namer instanceof ConfigNamerSerial namerSerial) {
            tabbedPane.setSelectedIndex(3);
            fieldSerialLength.setValue(namerSerial.getLength());
            fieldSerialPrefix.setText(namerSerial.getPrefix());
            fieldSerialSuffix.setText(namerSerial.getSuffix());
        }
        return this;
    }

    private void initComponents() {
        setContentPane(panelBody);
        panelBody.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panelCombo.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panelField.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panelSeparator.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panelSerial.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        tabbedPane.addTab("Combo", panelCombo);
        tabbedPane.addTab("Field", panelField);
        tabbedPane.addTab("Separator", panelSeparator);
        tabbedPane.addTab("Serial", panelSerial);
        buttonConfirm.addActionListener(e -> actConfirm());
        buttonCancel.addActionListener(e -> actCancel());
        insertComponents();
        WizDesk.cleanAllNames(panelBody);
    }

    private void insertComponents() {
        insertComponentsBody();
        insertComponentsCombo();
        insertComponentsField();
        insertComponentsSeparator();
        insertComponentsSerial();
        insertComponentsActions();
    }

    private void insertComponentsBody() {
        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelBody.add(labelName, constraints);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelBody.add(labelRequired, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelBody.add(fieldName, constraints);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelBody.add(checkRequired, constraints);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        panelBody.add(tabbedPane, constraints);
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelBody.add(panelActions, constraints);
    }

    private void insertComponentsCombo() {
        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelCombo.add(labelComboOptions, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        panelCombo.add(scrollComboOptions, constraints);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelCombo.add(labelComboPrefix, constraints);
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelCombo.add(labelComboSuffix, constraints);
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelCombo.add(fieldComboPrefix, constraints);
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelCombo.add(fieldComboSuffix, constraints);
    }

    private void insertComponentsField() {
        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelField.add(labelFieldPrefix, constraints);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelField.add(labelFieldSuffix, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelField.add(fieldFieldPrefix, constraints);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelField.add(fieldFieldSuffix, constraints);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        panelField.add(Box.createGlue(), constraints);
    }

    private void insertComponentsSeparator() {
        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelSeparator.add(labelSeparatorChars, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelSeparator.add(fieldSeparatorChars, constraints);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        panelSeparator.add(Box.createGlue(), constraints);
    }

    private void insertComponentsSerial() {
        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelSerial.add(labelSerialLength, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelSerial.add(fieldSerialLength, constraints);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelSerial.add(labelSerialPrefix, constraints);
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        panelSerial.add(labelSerialSuffix, constraints);
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelSerial.add(fieldSerialPrefix, constraints);
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panelSerial.add(fieldSerialSuffix, constraints);
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        panelSerial.add(Box.createGlue(), constraints);
    }

    private void insertComponentsActions() {
        panelActions.add(buttonConfirm);
        panelActions.add(buttonCancel);
    }

    private void actConfirm() {
        ConfigNamer namer = null;
        switch (tabbedPane.getSelectedIndex()) {
            case 0:
                namer = new ConfigNamerCombo(fieldName.getText(), checkRequired.isSelected(), WizString.getLines(textComboOptions.getText()), fieldComboPrefix.getText(), fieldComboSuffix.getText());
                break;
            case 1:
                namer = new ConfigNamerField(fieldName.getText(), checkRequired.isSelected(), fieldFieldPrefix.getText(), fieldFieldSuffix.getText());
                break;
            case 2:
                namer = new ConfigNamerSeparator(fieldName.getText(), checkRequired.isSelected(), fieldSeparatorChars.getText());
                break;
            case 3:
                namer = new ConfigNamerSerial(fieldName.getText(), checkRequired.isSelected(), (Integer) fieldSerialLength.getValue(), fieldSerialPrefix.getText(), fieldSerialSuffix.getText());
                break;
        }
        consumer.accept(namer);
        WizDesk.close(this);
    }

    private void actCancel() {
        WizDesk.close(this);
    }

}
