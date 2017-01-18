package test;

import javax.swing.*;

import view.ImagePanel;

import algorithm.ImageEnhancement;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

public class HistgramDlg extends JDialog{
	private BufferedImage srcImage;
	private BufferedImage histImage;
	ImagePanel imagePanel;
    JScrollPane scrollPane;
    
	int histArray[];		    //灰度表
	float values[];			    //灰度百分比表
	private float min, max;	    //最大最小灰度百分比，百分表示
	private float scaleX = 1;	//灰度/像素
	private float scaleY = 1;	//百分比/像素
	private int leftMargin = 40, topMargin = 20, rightMargin = 20, bottomMargin = 30;
	

    public HistgramDlg(java.awt.Frame parent, boolean modal) {
        super(parent, modal);      
        histArray = new int[256];
        values = new float[256];
        initComponents();
        setTitle("灰度直方图");
    }
    
    void initComponents() {
    	Container contentPane = getContentPane();
    	
    	imagePanel = new ImagePanel(histImage);

        scrollPane = new JScrollPane(imagePanel);
        contentPane.add(scrollPane, BorderLayout.CENTER);
    	setSize(new Dimension(640, 500));
    }
    
    int getHistgramWidth() {
    	return 256*2;
    }
    
    int getHistgramHeight() {
    	return 300;
    }
    
    //确定画线x点
    int lpXTodpX(float lx) {
    	return Math.round(leftMargin+lx*scaleX);
    }
    
    //确定画线的y点
    int lpYTodpY(float ly) {
    	return Math.round(topMargin+(max-ly)*scaleY);
    }
    
    void calcluteHistInfo() {
    	ImageEnhancement.getHistInfo(srcImage, histArray);
    	//共有多少个点
    	int count = srcImage.getWidth()*srcImage.getHeight();
    	
    	values[0] = (float)histArray[0]*100/count;
    	max = values[0];	min = 0;
    	for(int i=1; i<histArray.length; i++) {
    		values[i] = (float)histArray[i]*100/count;
    		if(values[i] > max) max = values[i];
    	}
    	max = (int)max+1;
    	
    	//坐标基数长度
    	scaleX = getHistgramWidth()/256.0f;
    	scaleY = getHistgramHeight()/max;
    	
    	histImage = new BufferedImage(getHistgramWidth()+leftMargin+rightMargin, 
    								  getHistgramHeight()+topMargin+bottomMargin,
    								  BufferedImage.TYPE_INT_RGB);
    	imagePanel.setImage(histImage);
    }
    
    void drawHistgram() {
    	
   		Graphics g = histImage.getGraphics();
   		g.setColor(Color.white);
   		g.fillRect(0, 0, histImage.getWidth(), histImage.getHeight());
   		
   		drawCorrdinate(g);
   		drawContents(g);	
    }
    
    //画每个灰度级百分比的线
    void drawContents(Graphics g) {
    	int dy0 = lpYTodpY(0);
    	g.setColor(new Color(0, 0, 128));
    	for(int i=0; i<values.length; i++) {
    		int dy = lpYTodpY(values[i]);
    		int dx = lpXTodpX(i);
    		g.drawLine(dx, dy0, dx, dy);
    	}	
    }
    
    //坐标轴需要的图形
    void drawCorrdinate(Graphics g) {
    	int dx0, dy0, dx1, dy1;
    	g.setColor(Color.black);
    	
    	//x轴和箭头
    	dx0 = lpXTodpX(256)+15;		dy0 = lpYTodpY(0);
    	g.drawLine(lpXTodpX(0), dy0, dx0, dy0);
    	g.drawLine(dx0-5, dy0-5, dx0, dy0);
    	g.drawLine(dx0-5, dy0+5, dx0, dy0);
    	
    	//y轴和箭头
    	dx0 = lpXTodpX(0);		dy0 = lpYTodpY(max)-15;
    	g.drawLine(dx0, lpYTodpY(0), dx0, dy0);
    	g.drawLine(dx0-5, dy0+5, dx0, dy0);
    	g.drawLine(dx0+5, dy0+5, dx0, dy0);
    	
    	//y轴的横线，标识灰度级站的比例
    	g.setColor(Color.gray);
    	for(int i=1; i<(int)max; i++) {
    		g.drawLine(lpXTodpX(0), lpYTodpY(i), lpXTodpX(256), lpYTodpY(i));	
    	}
    	
    	//封装线
    	g.setColor(Color.black);
    	g.drawLine(lpXTodpX(0), lpYTodpY(max), lpXTodpX(256), lpYTodpY(max));
    	g.drawLine(lpXTodpX(256), lpYTodpY(0), lpXTodpX(256), lpYTodpY(max));
    	
        //y轴单位长度
    	for(int i=0; i<=(int)max; i++) {
    		dx0 = lpXTodpX(0);
    		dy0 = lpYTodpY(i);
    		g.drawLine(dx0, dy0, dx0-5, dy0);
    		g.drawString(String.valueOf(i), dx0-25, dy0+4);	
    	}
    	
    	//x轴单位长度
    	for(int i=0; i<256; i+=20) {
    		dx0 = lpXTodpX(i);
    		dy0 = lpYTodpY(0);
    		g.drawLine(dx0, dy0, dx0, dy0+5);
    		String str = String.valueOf(i);
    		int strWidth = g.getFontMetrics().stringWidth(str);
    		g.drawString(str, dx0-strWidth/2, dy0+20);	
    	}
    }
    
    public void setImage(BufferedImage image) {
    	srcImage = image;
    	calcluteHistInfo();
    	drawHistgram();
    }
    
    public void drawLine() {
    	
    }
}
