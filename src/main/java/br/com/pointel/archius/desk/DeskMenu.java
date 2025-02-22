package br.com.pointel.archius.desk;

import java.io.File;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import br.com.pointel.jarch.mage.WizDesk;
import br.com.pointel.jarch.mage.WizProps;

public class DeskMenu extends JPopupMenu {

    private final Desk desk;
    
    private final JMenuItem menuOpen = new JMenuItem("Open");

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
        WizDesk.addMenu(this, menuOpen, e -> callOpen());
        addSeparator();
        WizDesk.addMenu(this, menuResize);
        WizDesk.addMenu(menuResize, menuResize32, e -> desk.setSize(32, 32));
        WizDesk.addMenu(menuResize, menuResize64, e -> desk.setSize(64, 64));
        WizDesk.addMenu(menuResize, menuResize128, e -> desk.setSize(128, 128));
        WizDesk.addMenu(menuResize, menuResize256, e -> desk.setSize(256, 256));
        WizDesk.addMenu(this, menuOnTop, e -> callOnTop());
        WizDesk.addMenu(this, menuExit, e -> callExit());
    }

    private final String LAST_SELECTED_FOLDER = "ArchiusLastSelectedFolder";

    private void callOpen() {
        var lastSelectedFolder = WizProps.get(LAST_SELECTED_FOLDER, "");
        var selectedFolder = new File(System.getProperty("user.home"));
        if (!lastSelectedFolder.isEmpty()) {
            selectedFolder = new File(lastSelectedFolder);
        }
        selectedFolder = WizDesk.selectFolder(selectedFolder);
        if (selectedFolder != null) {
            WizProps.set(LAST_SELECTED_FOLDER, selectedFolder.getAbsolutePath());
            try {
                new DeskOpen(selectedFolder).setVisible(true);
            } catch (Exception e) {
                WizDesk.showError(e);
            }
        }
    }
    
    private void callOnTop() {
        desk.setAlwaysOnTop(!desk.isAlwaysOnTop());
        menuOnTop.setSelected(desk.isAlwaysOnTop());
    }
    
    private void callExit() {
        WizDesk.closeAll();
    }

}
