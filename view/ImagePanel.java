package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public class ImagePanel extends JComponent {
    protected float scale = 1;          // 缩放比例
    protected Image image = null;       // 需要显示的图像
    
    public ImagePanel(Image image) {
        setImage(image);
    }
    
    public void setImage(Image image) {
        this.image = image;
        setSize(getPreferredSize());
    }
    
    /** 设置缩放比例*/
    public void setScale(float scale) {
        this.scale = scale;
        setSize(getPreferredSize());
    }
    
    /** 获取需要显示图像缩放后的尺寸*/
    protected Dimension getImageSize() {
        if(image != null) {
            return new Dimension(Math.round(image.getWidth(null)*scale), Math.round(image.getHeight(null)*scale));
        }
        else return new Dimension(0, 0);
    }
    
    /** 获取控件首选尺寸，由于没有边框，它等于getImageSize()*/
    public Dimension getPreferredSize() {
        return getImageSize();    
    }
    
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if(image == null) return ;
        Dimension destDim = getImageSize();
        g.drawImage(image, 0, 0, destDim.width, destDim.height,
                    0, 0, image.getWidth(null), image.getHeight(null), null);
    }
}
