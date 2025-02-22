package br.com.pointel.archius.desk;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import br.com.pointel.jarch.mage.WizSwing;

public class DeskMenu extends JPopupMenu {

    private final Desk desk;
    
    private final JMenuItem menuResize = new JMenuItem("Resize");
    private final JCheckBoxMenuItem menuOnTop = new JCheckBoxMenuItem("OnTop");
    private final JMenuItem menuExit = new JMenuItem("Exit");
    
    public DeskMenu(Desk desk) {
        this.desk = desk;
        initPopupListener();
    }
    
    private void initPopupListener() {
        addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                menuClean();
                menuPutEnding();
                menuOnTop.setSelected(desk.isAlwaysOnTop());
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }
            
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
    }
    
    private void menuClean() {
        removeAll();
    }
    
    private void menuPutEnding() {
        this.addSeparator();
        WizSwing.addMenuItem(this, menuResize, e -> callResize());
        WizSwing.addMenuItem(this, menuOnTop, e -> callOnTop());
        WizSwing.addMenuItem(this, menuExit, e -> callExit());
    }
    
    private void callResize() {
        switch (desk.getWidth()) {
            case 64 -> desk.setSize(128, 128);
            case 128 -> desk.setSize(192, 192);
            case 192 -> desk.setSize(256, 256);
            default -> desk.setSize(64, 64);
        }
    }
    
    private void callOnTop() {
        desk.setAlwaysOnTop(!desk.isAlwaysOnTop());
        menuOnTop.setSelected(desk.isAlwaysOnTop());
    }
    
    private void callExit() {
        WizSwing.closeAll();
    }

}
