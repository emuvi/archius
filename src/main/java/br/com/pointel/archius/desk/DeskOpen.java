package br.com.pointel.archius.desk;

import java.io.File;
import javax.swing.JFrame;
import br.com.pointel.jarch.mage.WizDesk;

public class DeskOpen extends JFrame {

    private final File folder;

    public DeskOpen(File folder) {
        this.folder = folder;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(DeskIcon.getLogo());
        setSize(800, 600);
        setName("archius folder " + folder.getName());
        setTitle("Archius Folder " + folder.getName());
        WizDesk.initFrame(this);
        WizDesk.initEscaper(this);
    }

}
