package br.com.pointel.archius;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.UIManager;

public class DeskIcon extends JLabel {

    private static Image LOGO;

    static {
        try {
            LOGO = ImageIO.read(Desk.class.getResourceAsStream("/img/logo.png"));
        } catch (Exception e) {
            LOGO = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        }
    }
    
    public static Image getLogo() {
        return LOGO;
    }

    private final Desk desk;
    private Point mouseDownCompCoords = null;

    public DeskIcon(Desk desk) {
        this.desk = desk;
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(UIManager.getBorder("Button.border"));
        this.initActions();
    }
    
    private void initActions() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                desk.actMenu();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                mouseDownCompCoords = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mouseDownCompCoords = null;
            }
        });
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                var currCoords = e.getLocationOnScreen();
                desk.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(getLogo(), 0, 0, getWidth(), getHeight(), this);
        g2d.dispose();
    }
    
}
