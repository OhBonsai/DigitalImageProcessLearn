package test;

import javax.swing.*;

import util.ProcessUtil;
import view.ImagePanel;

import algorithm.ImageEnhancement;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;


public class TestForLinearDlg extends JDialog{
	private BufferedImage srcImage;
	private BufferedImage linearImage;
	ImagePanel imagePanel;
    JScrollPane scrollPane;
    JCheckBox isSegmentation;
    JButton okBtn;
    JButton cancelBtn;
    
    private int xValue,yValue;  //鼠标第一次单击坐标位置
    private int xValue1,yValue1;//鼠标第二次单击坐标位置
    private int isSecond = 0;   //是否为第二个鼠标点击
    private boolean okOption = false;//代替确认按钮，在主界面响应函数中判断
	private int min, max;	    //最大最小灰度百分比，百分表示
	private int  scaleX = 2;	//灰度/像素
	private int  scaleY = 2;	//百分比/像素
	private int leftMargin = 40, topMargin = 20, rightMargin = 20, bottomMargin = 30;
	

    public TestForLinearDlg(java.awt.Frame parent, boolean modal) {
        super(parent,modal);      
        initComponents();
        setTitle("坐标框架");
    }
    
    void initComponents() {
    	Container contentPane = getContentPane();
      //  contentPane.setLayout(new GridBagLayout());
       
    	imagePanel = new ImagePanel(linearImage);
        scrollPane = new JScrollPane(imagePanel);
    	contentPane.add(scrollPane,BorderLayout.CENTER);
        imagePanel.addMouseListener(new MouseListener() { 
       	 
            @Override 
            public void mouseClicked(MouseEvent e) { 

        		Graphics g1 = imagePanel.getGraphics();

                if(e.getClickCount() == 1){
                	isSecond = isSecond+1;
                }
            	if(isSecond == 1){
                    xValue = e.getX();
                    yValue = e.getY();
            	    g1.drawOval(xValue-5, yValue-5, 10, 10);
                    System.out.println(xValue+" "+yValue);
            	}
            	if(isSecond == 2){
            		xValue1 = e.getX();
            		yValue1 = e.getY();
            		System.out.println(xValue1+" "+yValue1);
            	    g1.drawOval(xValue1-5, yValue1-5, 10, 10);
            	    g1.drawLine(xValue, yValue, xValue1, yValue1);
            	    okOption = true;
            	}
            	
            	/*if((xValue<=xValue1|yValue>=yValue1)&(xValue>=xValue1|yValue<=yValue1)){
            		xValue = yValue =0;
            		xValue1 = yValue1  = 0;
            		isSecond = 0;
            		System.out.println(xValue1+" "+xValue+yValue1+yValue+" "+isSecond);

            	}*/
            	
            }

            public void mousePressed(MouseEvent e) { 
            	
            } 
 
            @Override 
            public void mouseReleased(MouseEvent e) { 
                  
            } 
 
            @Override 
            public void mouseEntered(MouseEvent e) { 
            } 
 
            @Override 
            public void mouseExited(MouseEvent e) {   
                
            } 
             
		});
        



        
        
        

        
 


    	
    	

    	setSize(new Dimension(800, 600));
    	
    	
    }
    

	int getLinearWidth() {
    	return 256*2;
    }
    
    int getLinearHeight() {
    	return 256*2;
    }
    
    //确定画线x点
    int lpXTodpX(int lx) {
    	return leftMargin+lx*scaleX;
    }
    
    //确定画线的y点
    int lpYTodpY(int ly) {
    	return topMargin+(max-ly)*scaleY;
    }
    
    void calcluteLinearInfo() {
    	
    	int width = srcImage.getWidth(); 
		int height = srcImage.getHeight();
		int srcRGBs[] = srcImage.getRGB(0, 0, width, height, null, 0, width);
	
	    max = min = 0;
	    for(int i=0; i<srcRGBs.length; i++){
	    	max = ProcessUtil.getBrightness(srcRGBs[i])>max ?  ProcessUtil.getBrightness(srcRGBs[i]): max;
	    	min = ProcessUtil.getBrightness(srcRGBs[i])<min ?  ProcessUtil.getBrightness(srcRGBs[i]): min;
	    }
	    
    	linearImage = new BufferedImage(getLinearWidth()+leftMargin+rightMargin, 
    								  getLinearHeight()+topMargin+bottomMargin,
    								  BufferedImage.TYPE_INT_RGB);
    	imagePanel.setImage(linearImage);
    }
    
    void drawLinear() {
    	
   		Graphics g = linearImage.getGraphics();
   		g.setColor(Color.white);
   		g.fillRect(0, 0, linearImage.getWidth(), linearImage.getHeight());
   		
   		drawCorrdinate(g);
   		
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
    	dx0 = lpXTodpX(0);		dy0 = lpYTodpY(256)-15;
    	g.drawLine(dx0, lpYTodpY(0), dx0, dy0);
    	g.drawLine(dx0-5, dy0+5, dx0, dy0);
    	g.drawLine(dx0+5, dy0+5, dx0, dy0);
    	
    	//画网格  
    	/*g.setColor(new Color(0, 0, 128));
    	for(int i=0; i<=256; i+=20) {
    		int dy = lpYTodpY(i);
    		int dx = lpXTodpX(i);
    		g.drawLine(dx, dy0, dx, dy);
    		g.drawLine(dx0, dy, dx, dy);
    	}*/	
    	
    	//y轴的横线，标识灰度级站的比例
    	g.setColor(Color.gray);
    	for(int i=5; i<256; i+=5) {
    		g.drawLine(lpXTodpX(0), lpYTodpY(i), lpXTodpX(256), lpYTodpY(i));	
    		g.drawLine(lpXTodpX(i), lpYTodpY(0), lpXTodpX(i), lpYTodpY(256));
    	}
    	
    	
    	//封装线
    	g.setColor(Color.red);
    	g.drawLine(lpXTodpX(min), lpYTodpY(min), lpXTodpX(min), lpYTodpY(max));
    	g.drawLine(lpXTodpX(min), lpYTodpY(min), lpXTodpX(max), lpYTodpY(min));
    	g.drawLine(lpXTodpX(min), lpYTodpY(max), lpXTodpX(max), lpYTodpY(max));
    	g.drawLine(lpXTodpX(max), lpYTodpY(max), lpXTodpX(max), lpYTodpY(min));
    	
        //y轴单位长度
    	for(int i=20; i<=256; i+=20) {
    		dx0 = lpXTodpX(0);
    		dy0 = lpYTodpY(i);
    		g.setColor(Color.black);
    		g.drawLine(dx0, dy0, dx0-5, dy0);
    		String str = String.valueOf(i);
    		g.drawString(str, dx0-25, dy0+4);		
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
    	calcluteLinearInfo();
    	drawLinear();
    }
    
    public boolean getokOption(){
    	return okOption;
    }
    
    public int getxValue(){
    	return xValue;
    }
    
    public int getxValue1(){
    	return xValue1;
    }
    
    public int getyValue(){
    	return yValue;
    }
    
    public int getyValue1(){
    	return yValue1;
    }
    

}
