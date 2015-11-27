/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package icp.camera;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import org.opencv.core.Mat;

/**
 *
 * @author spartan
 */
public class ShowFrame extends JFrame {

    private int sizeX;
    private int sizeY;
    private Mat matrix;
    private BufferedImage image;
    private Integer crossX, crossY;

    public void setImage(BufferedImage image) {
        this.image = image;
        this.revalidate();
    }

    public void setCross(Integer x, Integer y) {
        this.crossX = x;
        this.crossY = y;
        this.revalidate();
    }

    public ShowFrame(int sizeX, int sizeY) throws HeadlessException {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        setSize(sizeX, sizeY);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        //super.paint(grphcs); //To change body of generated methods, choose Tools | Templates.
        //g2d.drawRect(0, 0, sizeX, sizeY);

        g2d.drawImage(image, 0, 0, null);

        if (this.crossX != null && this.crossY != null) {
            g2d.drawLine(crossX - 10, crossY, crossX + 10, crossY);
            g2d.drawLine(crossX, crossY - 10, crossX, crossY + 10);
        }
    }

}
