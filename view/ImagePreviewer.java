package view;

import java.io.File;
import java.awt.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.beans.*;
import javax.imageio.*;

class ImagePreviewer extends JComponent implements PropertyChangeListener {
	ImageIcon thumbnail = null;

	public ImagePreviewer(JFileChooser fc) {
	    setPreferredSize(new Dimension(100, 50));
	    fc.addPropertyChangeListener(this);
	}

	public void loadImage(File f) {
        if (f == null) {
            thumbnail = null;
        } 
        else {
			ImageIcon tmpIcon = null;
			try{ tmpIcon = new ImageIcon(ImageIO.read(f)); }
			catch(Exception e){};
			if(tmpIcon == null) return ;
			if(tmpIcon.getIconWidth() > 120) {
			    thumbnail = new ImageIcon(
					tmpIcon.getImage().getScaledInstance(120, -1, Image.SCALE_DEFAULT));
			} 
			else {
			    thumbnail = tmpIcon;
			}
	    }
	}

	public void propertyChange(PropertyChangeEvent e) {
	    String prop = e.getPropertyName();
	    if(prop == JFileChooser.SELECTED_FILE_CHANGED_PROPERTY) {
			if(isShowing()) {
	            loadImage((File) e.getNewValue());
			    repaint();
			}
	    }
	}

	public void paint(Graphics g) {
	    if(thumbnail != null) {
		int x = getWidth()/2 - thumbnail.getIconWidth()/2;
		int y = getHeight()/2 - thumbnail.getIconHeight()/2;
		if(y < 0) {
		    y = 0;
		}

		if(x < 5) {
		    x = 5;
		}
		thumbnail.paintIcon(this, g, x, y);
	    }
	}
}