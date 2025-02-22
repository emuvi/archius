package br.com.pointel.archius.desk;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import br.com.pointel.jarch.mage.WizDesk;
import br.com.pointel.jarch.mage.WizSwing;

public class Desk extends JFrame {
    
    private final DeskIcon deskIcon = new DeskIcon(this);
    private final DeskMenu deskMenu = new DeskMenu(this);

    public Desk() {
        initComponents();
    }
    
    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(deskIcon.getLogo());
        setSize(128, 128);
        setUndecorated(true);
        setName("archius");
        setTitle("Archius");
        setLayout(new BorderLayout());
        add(deskIcon, BorderLayout.CENTER);
        WizSwing.initFrame(this);
        WizSwing.initEscaper(this);
    }

    public void actMenu() {
        this.deskMenu.show(deskIcon, 0, deskIcon.getHeight());
    }
    
    public static void start(String[] args) {
        WizDesk.start("Archius", () -> new Desk().setVisible(true));
    }

}
