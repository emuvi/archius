package br.com.pointel.archius;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class DeskCatalogNamer<T extends ConfigNamer> extends JPanel {

    public static final DeskCatalogNamer<?> create(ConfigNamer configNamer) {
        if (configNamer instanceof ConfigNamerCombo configNamerCombo) {
            return new DeskCatalogNamerCombo(configNamerCombo);
        } else if (configNamer instanceof ConfigNamerField configNamerField) {
            return new DeskCatalogNamerField(configNamerField);
        } else if (configNamer instanceof ConfigNamerSeparator configNamerSeparator) {
            return new DeskCatalogNamerSeparator(configNamerSeparator);
        } else if (configNamer instanceof ConfigNamerSerial configNamerSerial) {
            return new DeskCatalogNamerSerial(configNamerSerial);
        }
        throw new IllegalArgumentException("Invalid config namer class: " + configNamer.getClass().getName());
    }

    private final T configNamer;
    private final JCheckBox checkPresent = new JCheckBox();
    private final JLabel labelName = new JLabel();
    private final JPanel panelNamer = new JPanel(new BorderLayout(4, 5));
    private final JPanel panelEditor = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));

    public DeskCatalogNamer(T configNamer) {
        super(new BorderLayout(4, 4));
        this.configNamer = configNamer;
        initComponents();
    }

    public abstract String getPartNamer();

    public T getConfigNamer() {
        return configNamer;
    }

    public boolean isPresent() {
        return checkPresent.isSelected();
    }

    public void addEditor(JComponent component) {
        panelEditor.add(component);
    }

    private void initComponents() {
        labelName.setText(configNamer.getName());
        checkPresent.setSelected(true);
        if (configNamer.isRequired()) {
            labelName.setFont(labelName.getFont().deriveFont(labelName.getFont().getStyle() | java.awt.Font.BOLD));
            checkPresent.setEnabled(false);
        }
        panelNamer.add(checkPresent, BorderLayout.WEST);
        panelNamer.add(labelName, BorderLayout.CENTER);
        add(panelNamer, BorderLayout.NORTH);
        add(panelEditor, BorderLayout.CENTER);
        labelName.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (checkPresent.isEnabled()) {
                    checkPresent.setSelected(!checkPresent.isSelected());
                }
            }
        });
    }

}
