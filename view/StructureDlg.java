package view;

import javax.swing.*;

public class StructureDlg extends JDialog{

	public StructureDlg(java.awt.Frame parent, boolean modal){
	     super(parent, modal);
	     initComponents();
	     setTitle("¿ò¼ÜÔªËØÉè¶¨");
	}
	
	void initComponents(){
		
	}
	
	private JLabel setJbl;
	private JButton okBtn;
	private JButton cancelBtn; 
}
class PaintPanel extends JPanel{
	
}