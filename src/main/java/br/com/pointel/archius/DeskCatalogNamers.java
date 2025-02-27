package br.com.pointel.archius;

import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import br.com.pointel.jarch.gears.WrapLayout;

public class DeskCatalogNamers extends JPanel {

    private final List<ConfigNamer> listConfigNamers;
    private final List<DeskCatalogNamer<?>> listNamers = new ArrayList<>();

    public DeskCatalogNamers(List<ConfigNamer> listConfigNamers) {
        super(new WrapLayout(FlowLayout.LEFT, 4, 4));
        this.listConfigNamers = listConfigNamers;
        initComponents();
    }

    public String getFullNamer() {
        var result = new StringBuilder();
        for (var namer : listNamers) {
            if (namer.isPresent()) {
                result.append(namer.getPartNamer());
            }
        }
        return result.toString();
    }

    private void initComponents() {
        for (var configNamer : listConfigNamers) {
            var namer = DeskCatalogNamer.create(configNamer);
            listNamers.add(namer);
            add(namer);
        }
    }

}
