package br.com.pointel.archius;

import java.awt.FlowLayout;
import javax.swing.JPanel;
import br.com.pointel.jarch.gears.WrapLayout;

public class DeskCatalogNames extends JPanel {

    public DeskCatalogNames() {
        super(new WrapLayout(FlowLayout.LEFT, 4, 4));
    }

    public String getFullName() {
        return "";
    }

}
