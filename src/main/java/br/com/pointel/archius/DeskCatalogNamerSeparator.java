package br.com.pointel.archius;

import javax.swing.JTextField;

public class DeskCatalogNamerSeparator extends DeskCatalogNamer<ConfigNamerSeparator> {

    private final JTextField fieldChars = new JTextField();

    public DeskCatalogNamerSeparator(ConfigNamerSeparator configNamerSeparator) {
        super(configNamerSeparator);
        initComponents();
    }

    public String getPartNamer() {
        return fieldChars.getText();
    }

    public void setPartNamer(String partNamer) {
        throw new UnsupportedOperationException("The Namer Separator is only configured.");	
    }

    private void initComponents() {
        fieldChars.setEnabled(false);
        fieldChars.setText(getConfigNamer().getChars());
        fieldChars.setColumns(fieldChars.getText().length());
        addEditor(fieldChars);
    }

}
