package br.com.pointel.archius;

import javax.swing.JTextField;
import br.com.pointel.jarch.mage.WizChars;

public class DeskCatalogNamerSerial extends DeskCatalogNamer<ConfigNamerSerial> {

    private final JTextField fieldPrefix = new JTextField();
    private final JTextField fieldLength = new JTextField();
    private final JTextField fieldSuffix = new JTextField();

    public DeskCatalogNamerSerial(ConfigNamerSerial configNamerSerial) {
        super(configNamerSerial);
        initComponents();
    }

    public String getPartNamer() {
        var result = new StringBuilder();
        result.append(fieldPrefix.getText());
        result.append(fieldLength.getText());
        result.append(fieldSuffix.getText());
        return result.toString();
    }

    private void initComponents() {
        fieldPrefix.setEnabled(false);
        fieldPrefix.setText(getConfigNamer().getPrefix());
        fieldPrefix.setColumns(fieldPrefix.getText().length());
        fieldLength.setEnabled(false);
        fieldLength.setText("/" + WizChars.fill('0', getConfigNamer().getLength()) + "/");
        fieldLength.setColumns(fieldSuffix.getText().length());
        fieldSuffix.setEnabled(false);
        fieldSuffix.setText(getConfigNamer().getSuffix());
        fieldSuffix.setColumns(fieldSuffix.getText().length());
        addEditor(fieldPrefix);
        addEditor(fieldLength);
        addEditor(fieldSuffix);
    }

}
