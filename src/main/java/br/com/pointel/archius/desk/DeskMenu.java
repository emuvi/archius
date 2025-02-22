package br.com.pointel.archius.desk;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import br.com.pointel.jarch.mage.WizDesk;

public class DeskMenu extends JPopupMenu {

    private final Desk desk;
    
    private final JMenu menuResize = new JMenu("Resize");
    private final JMenuItem menuResize32 = new JMenuItem("32");
    private final JMenuItem menuResize64 = new JMenuItem("64");
    private final JMenuItem menuResize128 = new JMenuItem("128");
    private final JMenuItem menuResize256 = new JMenuItem("256");
    private final JCheckBoxMenuItem menuOnTop = new JCheckBoxMenuItem("OnTop");
    private final JMenuItem menuExit = new JMenuItem("Exit");
    
    public DeskMenu(Desk desk) {
        this.desk = desk;
        initPopupListener();
        initMenuItems();
    }
    
    private void initPopupListener() {
        addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
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
    
    private void initMenuItems() {
        WizDesk.addMenu(this, menuResize);
        WizDesk.addMenu(menuResize, menuResize32, e -> desk.setSize(32, 32));
        WizDesk.addMenu(menuResize, menuResize64, e -> desk.setSize(64, 64));
        WizDesk.addMenu(menuResize, menuResize128, e -> desk.setSize(128, 128));
        WizDesk.addMenu(menuResize, menuResize256, e -> desk.setSize(256, 256));
        WizDesk.addMenu(this, menuOnTop, e -> callOnTop());
        WizDesk.addMenu(this, menuExit, e -> callExit());
    }
    
    private void callOnTop() {
        desk.setAlwaysOnTop(!desk.isAlwaysOnTop());
        menuOnTop.setSelected(desk.isAlwaysOnTop());
    }
    
    private void callExit() {
        WizDesk.closeAll();
    }

}
