package br.com.pointel.archius;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import br.com.pointel.jarch.mage.WizDesk;

public class Desk extends JFrame {

    private final DeskIcon deskIcon = new DeskIcon(this);
    private final DeskMenu deskMenu = new DeskMenu(this);

    public Desk() {
        initComponents();
    }
    
    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(WizDesk.getLogo());
        setSize(128, 128);
        setUndecorated(true);
        setName("archius");
        setTitle("Archius");
        setLayout(new BorderLayout());
        add(deskIcon, BorderLayout.CENTER);
        WizDesk.initFrame(this);
        WizDesk.initEscaper(this);
    }

    public void actMenu() {
        this.deskMenu.show(deskIcon, 0, deskIcon.getHeight());
    }
    
    public static void start(String[] args) {
        WizDesk.start("Archius", () -> new Desk().setVisible(true));
    }

}
