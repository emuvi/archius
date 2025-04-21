package br.com.pointel.archius;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import br.com.pointel.jarch.mage.WizDesk;

public class DeskConfigNamers extends JPanel {

    private final DeskConfig deskConfig;

    private final DefaultListModel<ConfigNamer> modelNamers = new DefaultListModel<>();
    private final JList<ConfigNamer> listNamers = new JList<>(modelNamers);
    private final JScrollPane scrollPane = new JScrollPane(listNamers);
    private final JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
    private final JButton buttonUp = new JButton("↑");
    private final JButton buttonDown = new JButton("↓");
    private final JButton buttonAdd = new JButton("+");;
    private final JButton buttonEdit = new JButton("&");
    private final JButton buttonDel = new JButton("-");

    public DeskConfigNamers(DeskConfig deskConfig) {
        super(new BorderLayout());
        this.deskConfig = deskConfig;
        initComponents();
    }

    public File getRoot() {
        return deskConfig.getRoot();
    }

    public List<ConfigNamer> getNamers() {
        var result = new ArrayList<ConfigNamer>();
        for (int i = 0; i < modelNamers.size(); i++) {
            result.add(modelNamers.get(i));
        }
        return result;
    }

    public void setNamers(List<ConfigNamer> namers) {
        modelNamers.clear();
        for (ConfigNamer namer : namers) {
            modelNamers.addElement(namer);
        }
    }

    public void addNamer(ConfigNamer namer) {
        var selected = listNamers.getSelectedIndex();
        modelNamers.add(selected + 1, namer);
        listNamers.setSelectedIndex(selected + 1);
    }

    private void initComponents() {
        setLayout(new BorderLayout(4, 4));
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        add(scrollPane, BorderLayout.CENTER);
        add(panelActions, BorderLayout.SOUTH);
        panelActions.add(buttonUp);
        panelActions.add(buttonDown);
        panelActions.add(buttonAdd);
        panelActions.add(buttonEdit);
        panelActions.add(buttonDel);
        buttonUp.addActionListener(e -> actUp());
        buttonDown.addActionListener(e -> actDown());
        buttonAdd.addActionListener(e -> actAdd());
        buttonEdit.addActionListener(e -> actEdit());
        buttonDel.addActionListener(e -> actDel());
        WizDesk.addDefaultAction(listNamers, e -> actEdit());
    }

    private void actUp() {
        var selected = listNamers.getSelectedIndex();
        if (selected > 0) {
            ConfigNamer namer = modelNamers.get(selected);
            modelNamers.remove(selected);
            modelNamers.add(selected - 1, namer);
            listNamers.setSelectedIndex(selected - 1);
        }
    }

    private void actDown() {
        var selected = listNamers.getSelectedIndex();
        if (selected < modelNamers.size() - 1) {
            ConfigNamer namer = modelNamers.get(selected);
            modelNamers.remove(selected);
            modelNamers.add(selected + 1, namer);
            listNamers.setSelectedIndex(selected + 1);
        }
    }

    private void actAdd() {
        new DeskConfigNamersEdit(this, namer -> addNamer(namer)).setVisible(true);
    }

    private void actEdit() {
        int selected = listNamers.getSelectedIndex();
        if (selected >= 0) {
            new DeskConfigNamersEdit(this, namer -> modelNamers.set(selected, namer))
                            .load(modelNamers.get(selected))
                            .setVisible(true);
        }
    }

    private void actDel() {
        var selected = listNamers.getSelectedIndex();
        if (selected >= 0) {
            modelNamers.remove(selected);
        }
    }

}
