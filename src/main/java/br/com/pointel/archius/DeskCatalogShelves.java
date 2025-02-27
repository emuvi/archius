package br.com.pointel.archius;

import java.awt.FlowLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import br.com.pointel.jarch.gears.WrapLayout;

public class DeskCatalogShelves extends JPanel {

    private final DeskCatalog deskCatalog;
    private final List<JComboBox<String>> listCombos = new ArrayList<>();

    public DeskCatalogShelves(DeskCatalog deskCatalog) {
        super(new WrapLayout(FlowLayout.LEFT, 4, 4));
        this.deskCatalog = deskCatalog;
        setShelf(deskCatalog.getRoot());
    }

    private Boolean selecting = false;

    public void setShelf(File folder) {
        selecting = true;
        clear();
        var stack = getStack(folder);
        JComboBox<String> prior = null;
        while (!stack.isEmpty()) {
            var actual = stack.pop();
            if (prior != null) {
                prior.setSelectedItem(actual.getName());
            }
            var model = new DefaultComboBoxModel<String>();
            model.addElement("");
            for (var inside : actual.listFiles()) {
                if (inside.isDirectory()) {
                    model.addElement(inside.getName());
                }
            }
            if (model.getSize() <= 1) {
                continue;
            }
            var combo = new JComboBox<String>(model);
            combo.addActionListener(e -> actOnSelect(combo, actual));
            listCombos.add(combo);
            add(combo);
            combo.requestFocus();
            prior = combo;
        }
        selecting = false;
        SwingUtilities.updateComponentTreeUI(this);
    }

    public File getShelf() {
        var result = deskCatalog.getRoot();
        for (var combo : listCombos) {
            var selected = (String) combo.getSelectedItem();
            if (selected == null || selected.isEmpty()) {
                break;
            }
            result = new File(result, selected);
        }
        return result;
    }

    private Stack<File> getStack(File folder) {
        var root = deskCatalog.getRoot();
        var stack = new Stack<File>();
        stack.push(folder);
        while (!Objects.equals(folder, root)) {
            folder = folder.getParentFile();
            if (folder == null) {
                break;
            }
            stack.push(folder);
        }
        return stack;
    }

    private void clear() {
        for (var combo : listCombos) {
            remove(combo);
        }
        listCombos.clear();
    }

    private void actOnSelect(JComboBox<String> ofCombo, File onFolder) {
        if (selecting) {
            return;
        }
        var selected = (String) ofCombo.getSelectedItem();
        if (selected == null || selected.isEmpty()) {
            return;
        }
        var actual = new File(onFolder, selected);
        setShelf(actual);
    }

}
