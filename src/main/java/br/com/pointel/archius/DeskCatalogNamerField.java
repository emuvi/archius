package br.com.pointel.archius;

import javax.swing.JTextField;

public class DeskCatalogNamerField extends DeskCatalogNamer<ConfigNamerField> {

    private final JTextField fieldPrefix = new JTextField();
    private final JTextField fieldText = new JTextField(12);
    private final JTextField fieldSuffix = new JTextField();

    public DeskCatalogNamerField(ConfigNamerField configNamerField) {
        super(configNamerField);
        initComponents();
    }

    public String getPartNamer() {
        var result = new StringBuilder();
        result.append(fieldPrefix.getText());
        result.append(fieldText.getText());
        result.append(fieldSuffix.getText());
        return result.toString();
    }

    public void setPartNamer(String partNamer) {
        fieldText.setText(partNamer);
    }

    private void initComponents() {
        fieldPrefix.setEnabled(false);
        fieldPrefix.setText(getConfigNamer().getPrefix());
        fieldPrefix.setColumns(fieldPrefix.getText().length());
        fieldSuffix.setEnabled(false);
        fieldSuffix.setText(getConfigNamer().getSuffix());
        fieldSuffix.setColumns(fieldSuffix.getText().length());
        addEditor(fieldPrefix);
        addEditor(fieldText);
        addEditor(fieldSuffix);
    }

}
