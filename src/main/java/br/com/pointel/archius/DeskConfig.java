package br.com.pointel.archius;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import br.com.pointel.jarch.mage.WizDesk;

public class DeskConfig extends JFrame {

    private final ArchBase archBase;
    private final JPanel panelBody = new JPanel(new BorderLayout());
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final DeskConfigNamers deskConfigNamers;
    private final JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
    private final JButton buttonConfirm = new JButton("Confirm");
    private final JButton buttonCancel = new JButton("Cancel");

    public DeskConfig(ArchBase archBase) {
        this.archBase = archBase;
        this.deskConfigNamers = new DeskConfigNamers(this);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setIconImage(DeskIcon.getLogo());
        setSize(500, 400);
        setName("config on " + archBase.getRoot().getName());
        setTitle("Config on " + archBase.getRoot().getName());
        WizDesk.initFrame(this);
        WizDesk.initEscaper(this);
        initComponents();
    }

    public File getRoot() {
        return archBase.getRoot();
    }

    public Config getConfig() {
        return new Config(deskConfigNamers.getNamers());
    }

    public void setConfig(Config config) {
        deskConfigNamers.setNamers(config.getNamers());
    }

    private void initComponents() {
        setContentPane(panelBody);
        panelBody.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        panelBody.add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab("Namers", deskConfigNamers);
        panelBody.add(panelActions, BorderLayout.SOUTH);
        panelActions.add(buttonConfirm);
        panelActions.add(buttonCancel);
        buttonConfirm.addActionListener(e -> actConfirm());
        buttonCancel.addActionListener(e -> actCancel());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                try {
                    setConfig(Config.load(archBase.getRoot()));
                } catch (Exception ex) {
                    WizDesk.showError(ex);
                }
            }
        });
    }

    private void actConfirm() {
        try {
            Config.save(archBase.getRoot(), getConfig());
            WizDesk.close(this);
        } catch (Exception ex) {
            WizDesk.showError(ex);
        }
    }

    private void actCancel() {
        WizDesk.close(this);
    }

}
