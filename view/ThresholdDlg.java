package view;

import javax.swing.*;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

public class ThresholdDlg extends JDialog{
	

    
    public ThresholdDlg(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    private void initComponents() {
    	
    	jLbl = new JLabel();
    	okBtn = new JButton();
    	cancelBtn = new JButton();
    	inputArea = new JTextField();

    	
    	getContentPane().setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        jLbl.setText("请输入阈值(0~255的数字)");
        gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.gridwidth = 4;
        gridBagConstraints1.gridheight = 1;
        gridBagConstraints1.insets = new Insets(5,5,5,5);
        //gridBagConstraints1.fill = GridBagConstraints.NORTH;
        getContentPane().add(jLbl, gridBagConstraints1);

        gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 2;
        gridBagConstraints1.gridwidth = 5;
        gridBagConstraints1.gridheight = 1;
        gridBagConstraints1.ipadx = 200;
        gridBagConstraints1.insets = new Insets(5,5,5,5);
      //  gridBagConstraints1.fill = GridBagConstraints.CENTER;
        getContentPane().add(inputArea, gridBagConstraints1);

        okBtn.setText("确定");
        gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.gridwidth = 2;
        gridBagConstraints1.gridheight = 1;
        gridBagConstraints1.insets = new Insets(5,5,5,5);
       // gridBagConstraints1.fill = GridBagConstraints.SOUTHWEST;
        getContentPane().add(okBtn, gridBagConstraints1);
        okBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e){
        		okPerform();
        	}
        });
        
        cancelBtn.setText("取消");
        gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 4;
        gridBagConstraints1.gridy = 4;
        gridBagConstraints1.gridwidth = 2;
        gridBagConstraints1.gridheight = 1;
        gridBagConstraints1.insets = new Insets(5,5,5,5);
       // gridBagConstraints1.fill = GridBagConstraints.SOUTHEAST;
        getContentPane().add(cancelBtn, gridBagConstraints1);
        cancelBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e){
        		cancelPerform();
        	}
        });
        
        pack();
        
    	
    	
    }

	private void cancelPerform() {
		inputArea.setText("");
        modelResult = JOptionPane.CANCEL_OPTION;
		dispose();
		
	}

	private void okPerform() {

		String s = inputArea.getText();
		String sPattern = "(\\d{0,1})+(\\d{0,1})+(\\d{0,1})";
		if(s.matches(sPattern)){
			threshold = Integer.parseInt(s);
			if(threshold > 255){
				JOptionPane.showMessageDialog(getContentPane()
						, "请不要输入255以上数字！","系统信息",JOptionPane.WARNING_MESSAGE);
				inputArea.setText("");
			}else{
				modelResult = JOptionPane.OK_OPTION;
				dispose();
				
			}
		}else{
			JOptionPane.showMessageDialog(getContentPane()
					, "请输入0~255的数字！","系统信息",JOptionPane.WARNING_MESSAGE);
			inputArea.setText("");
		}
	}
	
	public int getModelResult(){
		return modelResult;
	}
	
	public int getThreshold() {
		return threshold;
	}
	
    private JLabel jLbl;
    private JButton okBtn , cancelBtn;
    private JTextField inputArea;
    private JPanel jpnl;
    private int modelResult = JOptionPane.CANCEL_OPTION;
    private int threshold;
}
