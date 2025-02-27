package br.com.pointel.archius;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class DeskCatalogNamerCombo extends DeskCatalogNamer<ConfigNamerCombo> {

    private final JTextField fieldPrefix = new JTextField();
    private final DefaultComboBoxModel<String> modelOptions = new DefaultComboBoxModel<>();
    private final JComboBox<String> comboOptions = new JComboBox<>(modelOptions);
    private final JTextField fieldSuffix = new JTextField();

    public DeskCatalogNamerCombo(ConfigNamerCombo configNamerCombo) {
        super(configNamerCombo);
        initComponents();
    }

    public String getPartNamer() {
        var result = new StringBuilder();
        result.append(fieldPrefix.getText());
        result.append(comboOptions.getSelectedItem() != null ? comboOptions.getSelectedItem().toString() : "");
        result.append(fieldSuffix.getText());
        return result.toString();
    }

    private void initComponents() {
        fieldPrefix.setEnabled(false);
        fieldPrefix.setText(getConfigNamer().getPrefix());
        fieldPrefix.setColumns(fieldPrefix.getText().length());
        fieldSuffix.setEnabled(false);
        fieldSuffix.setText(getConfigNamer().getSuffix());
        fieldSuffix.setColumns(fieldSuffix.getText().length());
        for (var item : getConfigNamer().getOptions()) {
            modelOptions.addElement(item);
        }
        addEditor(fieldPrefix);
        addEditor(comboOptions);
        addEditor(fieldSuffix);
    }

}
