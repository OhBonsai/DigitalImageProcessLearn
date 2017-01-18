package view;

import javax.swing.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

public class ScaleDlg extends javax.swing.JDialog {

    public ScaleDlg(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }


    private void initComponents() {
        scaleSlider = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        scaleEdit = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        widthEdit = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        heightEdit = new javax.swing.JTextField();
        okBtn = new javax.swing.JButton();
        cancelBtn = new javax.swing.JButton();
        
        getContentPane().setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        scaleSlider.setMaximum(200);
        scaleSlider.setValue(100);
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.gridwidth = 2;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.insets = new java.awt.Insets(15, 10, 15, 1);
        getContentPane().add(scaleSlider, gridBagConstraints1);
        scaleSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                scaleChanged(evt);
            }
        });
        
        jLabel1.setText("缩放比例");
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.insets = new java.awt.Insets(3, 20, 3, 20);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(jLabel1, gridBagConstraints1);
        
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.ipadx = 80;
        gridBagConstraints1.insets = new java.awt.Insets(3, 20, 3, 20);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(scaleEdit, gridBagConstraints1);
        
        jLabel2.setText("图像高度");
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.insets = new java.awt.Insets(3, 20, 3, 20);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(jLabel2, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.ipadx = 80;
        gridBagConstraints1.insets = new java.awt.Insets(3, 20, 3, 20);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(widthEdit, gridBagConstraints1);
        
        jLabel3.setText("图像宽度");
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.insets = new java.awt.Insets(3, 20, 10, 20);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(jLabel3, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 3;
        gridBagConstraints1.ipadx = 80;
        gridBagConstraints1.insets = new java.awt.Insets(3, 20, 10, 20);
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(heightEdit, gridBagConstraints1);
        
        okBtn.setText("确定(O)");
        okBtn.setMnemonic('O');
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.insets = new java.awt.Insets(10, 10, 1, 20);
        getContentPane().add(okBtn, gridBagConstraints1);
        okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okPerformed(e);
			}
		});		        	
        
        cancelBtn.setText("取消(C)");
        cancelBtn.setMnemonic('C');
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridx = 2;
        gridBagConstraints1.gridy = 1;
        gridBagConstraints1.insets = new java.awt.Insets(1, 10, 1, 20);
        getContentPane().add(cancelBtn, gridBagConstraints1);
        cancelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelPerformed(e);
			}
		});
        
        pack();
        scaleEdit.setText("1");
    }

    private void okPerformed(java.awt.event.ActionEvent evt) {
        modResult = JOptionPane.OK_OPTION;
		dispose();
    }

    private void cancelPerformed(java.awt.event.ActionEvent evt) {
        modResult = JOptionPane.CANCEL_OPTION;
		dispose();
    }
    
    private void scaleChanged(javax.swing.event.ChangeEvent evt) {
    	int v = scaleSlider.getValue();
    	if(v <= 100) scale = 0.1f+v*0.009f;
    	else scale = 1+0.01f*(v-100);
    	scale = (int)(scale*100)/100.0f;
    	scaleEdit.setText(String.valueOf(scale));
    	widthEdit.setText(String.valueOf(Math.round(scale*imageWidth)));
    	heightEdit.setText(String.valueOf(Math.round(scale*imageHeight)));
    }

    public int showModal() {
    	show();
    	return modResult;
    }
    
    public int modResult() {
    	return modResult;
    }
    
    public float getScale() {
    	return scale;
    }
    
    public void setImageWidth(int width) {
    	this.imageWidth = width;
    	widthEdit.setText(String.valueOf(width));
    }
    
    public void setImageHeight(int height) {
    	this.imageHeight = height;
    	heightEdit.setText(String.valueOf(height));
    }


    private javax.swing.JSlider scaleSlider;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField scaleEdit;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField widthEdit;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField heightEdit;
    private javax.swing.JButton okBtn;
    private javax.swing.JButton cancelBtn;


	private int modResult = JOptionPane.CANCEL_OPTION;
	private float scale = 1;
	private int imageWidth;
	private int imageHeight;
}


