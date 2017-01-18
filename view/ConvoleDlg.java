package view;


import javax.swing.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

public class ConvoleDlg extends javax.swing.JDialog  {
	
	private javax.swing.JLabel lblPrompt;
	private javax.swing.JLabel lblPrompt1;
	private javax.swing.JTextArea areacoefficient;
	private javax.swing.JTextArea areaArray0;
	private javax.swing.JTextArea areaArray1;
	private javax.swing.JTextArea areaArray2;
	private javax.swing.JTextArea areaArray3;
	private javax.swing.JTextArea areaArray4;
	private javax.swing.JTextArea areaArray5;
	private javax.swing.JTextArea areaArray6;
	private javax.swing.JTextArea areaArray7;
	private javax.swing.JTextArea areaArray8;
	private javax.swing.JButton okBtn;
    private javax.swing.JButton cancelBtn;
    
	private int modResult=JOptionPane.CANCEL_OPTION;
 
    public ConvoleDlg(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

	public void initComponents() {
		// TODO Auto-generated method stub
		
        getContentPane().setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
		
		lblPrompt.setText("输入矩阵元素");
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.ipadx = 75;
        gridBagConstraints1.insets = new java.awt.Insets(3, 20, 10, 20);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(lblPrompt, gridBagConstraints1);
        
		lblPrompt1.setText("输入矩阵系数");
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.ipadx = 25;
        gridBagConstraints1.insets = new java.awt.Insets(3, 20, 3, 20);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTH;
        getContentPane().add(lblPrompt1, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 3;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.ipadx = 25;
        gridBagConstraints1.insets = new java.awt.Insets(3, 20, 3, 15);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(areacoefficient, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.ipadx = 25;
        gridBagConstraints1.insets = new java.awt.Insets(10, 20, 3, 3);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(areaArray0, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.ipadx = 25;
        gridBagConstraints1.insets = new java.awt.Insets(10, 20, 3, 3);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.CENTER;
        getContentPane().add(areaArray1, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.ipadx = 25;
        gridBagConstraints1.insets = new java.awt.Insets(10, 20, 3, 20);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.CENTER;
        getContentPane().add(areaArray2, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.ipadx = 25;
        gridBagConstraints1.insets = new java.awt.Insets(3, 20, 3, 3);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(areaArray3, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.ipadx = 25;
        gridBagConstraints1.insets = new java.awt.Insets(3, 3, 3, 20);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.CENTER;
        getContentPane().add(areaArray4, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.ipadx = 25;
        gridBagConstraints1.insets = new java.awt.Insets(3, 3, 3, 20);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.CENTER;
        getContentPane().add(areaArray5, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.ipadx = 25;
        gridBagConstraints1.insets = new java.awt.Insets(3, 20, 10, 3);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(areaArray6, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.ipadx = 25;
        gridBagConstraints1.insets = new java.awt.Insets(3, 3, 10, 20);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.CENTER;
        getContentPane().add(areaArray7, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.ipadx = 25;
        gridBagConstraints1.insets = new java.awt.Insets(3, 3, 10, 20);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.CENTER;
        getContentPane().add(areaArray8, gridBagConstraints1);
        
        okBtn.setText("确定(O)");
        okBtn.setMnemonic('O');
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 3;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.insets = new java.awt.Insets(10, 20, 15, 15);
        getContentPane().add(okBtn, gridBagConstraints1);
        okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okPerformed(e);
			}
		});		        	
        
        cancelBtn.setText("取消(C)");
        cancelBtn.setMnemonic('C');
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 3;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.insets = new java.awt.Insets(15, 20, 20, 15);
        getContentPane().add(cancelBtn, gridBagConstraints1);
        cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelPerformed(e);
			}
		});
        
        pack();
    }

    private void okPerformed(java.awt.event.ActionEvent evt) {
        modResult = JOptionPane.OK_OPTION;
		dispose();
    }

    private void cancelPerformed(java.awt.event.ActionEvent evt) {
        modResult = JOptionPane.CANCEL_OPTION;
		dispose();
    }
        
    public int showModal() {
    	show();
    	return modResult;
    }
    
    public String getAreacoefficient() {
    	return areacoefficient.getText();
    }
    
    
    public String getArray0() {
    	return areaArray0.getText();
    }
    
    public String getArray1() {
    	return areaArray1.getText();
    }
    
    public String getArray2() {
    	return areaArray2.getText();
    }
    
    public String getArray3() {
    	return areaArray3.getText();
    }
    
    public String getArray4() {
    	return areaArray4.getText();
    }
    
    public String getArray5() {
    	return areaArray5.getText();
    }
    
    public String getArray6() {
    	return areaArray6.getText();
    }
    
    public String getArray7() {
    	return areaArray7.getText();
    }
    
    public String getArray8() {
    	return areaArray8.getText();
    }
	

}
