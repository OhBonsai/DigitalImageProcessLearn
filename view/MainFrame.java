package view;
/*
 * 临时记录：文件选择器 取消有bug(已修复！)
 * 临时记录：没有添加对线性差值点位置的判断
 * 临时记录：当有dialog取消，列表依然更新....(已修复！)
 * 临时记录：极小点阈值法 对于极小点的确定方法①最小二乘法 拟合函数 图像太复杂不合实际
 *                                        ② 利用斜率数组判断，方法简单，但是图像复杂    （没做）      
 * 临时记录：查看梯度图，如果改变窗口大小会自动调用repaint方法    
 * 临时记录：梯度图效果不好，起始点设置的不好。          
 * 临时记录：Prewitt算子用哪个？              
 * 临时记录：a,b的值如何确定
 * 临时记录：关于HT变换的想法，对于噪声的去除，仅仅就直线而言，可以利用延展去实现。噪声是一个单独突出的一圈点。然后一些细节处理。
 */







import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.io.*;

import javax.imageio.*;

import test.TestForLinearDlg;

import algorithm.EdgeDetection;
import algorithm.GeoTransform;
import algorithm.ImageEnhancement;
import algorithm.ImageSegmentation;
import algorithm.Morphology;
import algorithm.WaveletTransform;


import java.util.*;

public class MainFrame extends JFrame implements ListSelectionListener {
	
	JMenuBar mb;
    JMenu fileMenu;
    JMenuItem openItem;
    JMenuItem saveItem;
    JMenuItem exitItem;
    
    JMenu editMenu;
    JMenuItem undoItem;
    JMenuItem redoItem;
    
    JMenu geoMenu;
    JMenuItem translationItem;
    JMenuItem horMirrorItem;
    JMenuItem verMirrorItem;
    JMenuItem scaleItem;
    JMenuItem rotateItem;
    
    JMenu ehanceMenu;
    JMenuItem grayScaleItem;
    JMenu grayTransformation;
    JMenu linearTransformation;
    JMenuItem liNotSegmentation;
    JMenuItem liSegmentation;
    JMenuItem nonLinearTransformation;
    JMenu histogramModification;
    JMenuItem histgramItem;
    JMenuItem histogramEqualizationItem;
    JMenuItem histogramSpecificationItem;
    JMenu imageSmoothing;
    JMenuItem medianFiltering; 
    JMenuItem gaussianSmoothingItem;
    JMenuItem fieldAverageItem;
    JMenu imageSharpening;
    JMenuItem laplacian;
    JMenuItem laplacianHiBoostFiltering;
    JMenuItem gaussianHiBoostFiletering;
    
    JMenu imageSegmentation;
    JMenu thresholdSeg;
    JMenuItem simpleThreshold;
    JMenuItem iterativeThreshold;
    JMenuItem otsuThreshold;
    JMenuItem dynamicThreshold;
    
    JMenu edgeMenu;
    JMenu gradientMenu;
    JMenuItem horGradientItem;
    JMenuItem verGradientItem;
    JMenuItem sobelItem;
    JMenuItem cannyItem;
    JMenuItem oriItem;

    
    JMenu frequencyDomainProcessing;
    JMenuItem fftItem;
    JMenuItem decomposeItem;
    JMenuItem markItem;
    
    
    JMenu morphology;
    JMenuItem corrosionItem;
    JMenuItem swellItem;
    JMenuItem getBone;
    
    
    JToolBar tb;
    JButton openBtn;
    JButton saveBtn;
    JButton exitBtn;
    
    
    ImagePanel imagePanel;
    JScrollPane scrollPane;
    JScrollPane scrollPane1;
    ImageIcon imageIcon;
    BufferedImage image;
    
    JFileChooser chooser;
    ImagePreviewer imagePreviewer;
    ImageFileView fileView;
    JList operationList;
    DefaultListModel dlm;

   
    
    ImageFileFilter bmpFilter;
    ImageFileFilter jpgFilter;
	ImageFileFilter gifFilter;
	ImageFileFilter bothFilter;
	
	LinkedList undoList;
	LinkedList redoList;
	LinkedList allList;
	int pointer = 0;
	int newImage = 0;
	boolean okFlag = false;
	private final static int MAX_UNDO_COUNT = 10;
	private final static int MAX_REDO_COUNT = 10;
	private final static int MAX_ALL_COUNT = 50;
    
    public MainFrame() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit(e);
            }
        });
        
        undoList = new LinkedList();
        redoList = new LinkedList();
        allList = new LinkedList();
        initComponents();
    }
    
    private void initComponents() {
    	Container contentPane = getContentPane();
    	
    	imagePanel = new ImagePanel(image);
        scrollPane = new JScrollPane(imagePanel);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        
        
        chooser = new JFileChooser();
        imagePreviewer = new ImagePreviewer(chooser);
        fileView = new ImageFileView();
	    bmpFilter = new ImageFileFilter("bmp", "BMP Image Files");
	    jpgFilter = new ImageFileFilter("jpg", "JPEG Compressed Image Files");
		gifFilter = new ImageFileFilter("gif", "GIF Image Files");
		bothFilter = new ImageFileFilter(new String[] {"bmp", "jpg", "gif"}, "BMP, JPEG and GIF Image Files");
	    chooser.addChoosableFileFilter(gifFilter);
	    chooser.addChoosableFileFilter(bmpFilter);
        chooser.addChoosableFileFilter(jpgFilter);
        chooser.addChoosableFileFilter(bothFilter);
        chooser.setAccessory(imagePreviewer);
        chooser.setFileView(fileView);
        chooser.setAcceptAllFileFilterUsed(false);
                    
		Icon openIcon = new ImageIcon("open.gif");
		Icon saveIcon = new ImageIcon("save.gif");
		Icon exitIcon = new ImageIcon("exit.gif");
		Icon undoIcon = new ImageIcon("images/undo.gif");
		Icon redoIcon = new ImageIcon("images/redo.gif");
		
		//----操作列表---------------------------------------------------------
		operationList = new JList();
		scrollPane1 = new JScrollPane(operationList);
		contentPane.add(scrollPane1, BorderLayout.EAST);
		
		final DefaultListModel dlm = new DefaultListModel();
		operationList.setModel(dlm);
		operationList.addListSelectionListener(this);
		
		

		
		//----菜单条------------------------------------------------------------
		mb = new JMenuBar();
		setJMenuBar(mb);
		//----File菜单----------------------------------------------------------
		fileMenu = new JMenu("文件(F)");
		fileMenu.setMnemonic('F');
		mb.add(fileMenu);
		
		openItem = new JMenuItem("打开(O)", openIcon);
		openItem.setMnemonic('O');
		openItem.setAccelerator(KeyStroke.getKeyStroke('O', Event.CTRL_MASK));
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile(e);
				if(okFlag){
					newImage++;
			 	    dlm.addElement("第"+pointer+"步：新图像"+newImage+"                            ");
			 	    pointer++;
			 	    okFlag = false;
				}
			}
		});
		
		saveItem = new JMenuItem("保存(S)", saveIcon);
		saveItem.setMnemonic('S');
		saveItem.setAccelerator(KeyStroke.getKeyStroke('S', Event.CTRL_MASK));
		saveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFile(e);
			}
		});
		
		
		exitItem = new JMenuItem("退出(X)",exitIcon);
		exitItem.setMnemonic('X');
		exitItem.setAccelerator(KeyStroke.getKeyStroke('X', Event.CTRL_MASK));
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitSystem(e);
			}
		});
		
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		
		//----Edit菜单----------------------------------------------------------
		editMenu = new JMenu("编辑(E)");
		editMenu.setMnemonic('E');
		mb.add(editMenu);
		
		undoItem = new JMenuItem("撤销(U)", undoIcon);
		undoItem.setMnemonic('U');
		undoItem.setAccelerator(KeyStroke.getKeyStroke('Z', Event.CTRL_MASK));
		undoItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				undo(e);
			}
		});
		
		redoItem = new JMenuItem("重做(R)", redoIcon);
		redoItem.setMnemonic('R');
		redoItem.setAccelerator(KeyStroke.getKeyStroke('Y', Event.CTRL_MASK));
		redoItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				redo(e);
			}
		});
		
		editMenu.add(undoItem);
		editMenu.add(redoItem);
		
		//----Geo菜单----------------------------------------------------------
		geoMenu = new JMenu("几何变换(G)");
		geoMenu.setMnemonic('G');
		mb.add(geoMenu);
		
		translationItem =new JMenuItem("图片平移(T)");
		translationItem.setMnemonic('T');
		translationItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				translation(e);
				if(okFlag){
	        	dlm.addElement("第"+pointer+"步：图像"+newImage+"经图像平移");}
				pointer++;
				okFlag = false;
			}
		});
		
		horMirrorItem = new JMenuItem("水平镜象(H)");
		horMirrorItem.setMnemonic('H');
		horMirrorItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				horMirror(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经水平镜像");
				pointer++;
			}
		});
		
		verMirrorItem = new JMenuItem("垂直镜象(V)");
		verMirrorItem.setMnemonic('V');
		verMirrorItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				verMirror(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经垂直镜像");
				pointer++;
			}
		});
		
		scaleItem = new JMenuItem("比例缩放(S)");
		scaleItem.setMnemonic('S');
		scaleItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scale(e);
				if(okFlag){
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经比例缩放");
				pointer++;
				okFlag = false;
				}
			}
		});
		
		rotateItem = new JMenuItem("旋转(R)");
		rotateItem.setMnemonic('R');
		rotateItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rotate(e);
				if(okFlag){
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经旋转");
				pointer++;
				okFlag = false;
				}
			}
		});
		
		geoMenu.add(translationItem);
		geoMenu.add(horMirrorItem);
		geoMenu.add(verMirrorItem);
		geoMenu.add(scaleItem);
		geoMenu.add(rotateItem);
		
		//----Ehance菜单-------------------------------------------------------
		ehanceMenu = new JMenu("图像增强(E)");
		ehanceMenu.setMnemonic('E');
		mb.add(ehanceMenu);
		
		
		grayTransformation = new JMenu("灰度变换(T)");
		grayTransformation.setMnemonic('T');
		
		grayScaleItem =new JMenuItem("图片灰度化(G)");
		grayScaleItem.setMnemonic('G');
		grayScaleItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				graySacle(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经灰度化");
				pointer++;
			}
		});
		
		linearTransformation = new JMenu("线性变换(L)");
		linearTransformation.setMnemonic('L');
		
		liNotSegmentation = new JMenuItem("不分段线性(N)");
		liNotSegmentation.setMnemonic('N');
		liNotSegmentation.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e){
				liNotSegmentation(e);
				if(okFlag){
					dlm.addElement("第"+pointer+"步：图像"+newImage+"经不分段线性变换");
					pointer++;
					okFlag = false;
				}
			}
		});
		
		liSegmentation = new JMenuItem("分段线性(S)");
		liSegmentation.setMnemonic('S');
		liSegmentation.addActionListener(new ActionListener() {
			public  void actionPerformed (ActionEvent e){
				liSegmentation(e);
				if(okFlag){
					dlm.addElement("第"+pointer+"步：图像"+newImage+"经分段线性变换");
					pointer++;
					okFlag = false;
				}
			}
		});
		
		nonLinearTransformation = new JMenuItem("非线性变换(N)");
		nonLinearTransformation.setMnemonic('L');
		nonLinearTransformation.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e){
                nonlinearTran(e);
			}
		});
		
		histogramModification = new JMenu("直方图修正(H)");
		histogramModification.setMnemonic('H');
		
		histgramItem = new JMenuItem("灰度直方图(H)");
		histgramItem.setMnemonic('H');
		histgramItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				histgram(e);
			}
		});
		
		histogramEqualizationItem = new JMenuItem("直方图均衡化(E)");
		histogramEqualizationItem.setMnemonic('E');
		histogramEqualizationItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				equalization(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经直方图均衡化");
				pointer++;
			}
		});
		
		histogramSpecificationItem = new JMenuItem("直方图规定化(S)");
		histogramSpecificationItem.setMnemonic('E');
		histogramSpecificationItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				specification(e);
			}
		});
		
		imageSmoothing = new JMenu("图像平滑(S)");
		imageSmoothing.setMnemonic('S');
		
		
		medianFiltering = new JMenuItem("中值滤波(F)");
		medianFiltering.setMnemonic('F');
		medianFiltering.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				medianFiltering(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经中值滤波");
				pointer++;
			}
		});
		
		gaussianSmoothingItem = new JMenuItem("高斯平滑(G)");
		gaussianSmoothingItem.setMnemonic('G');
		gaussianSmoothingItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				gaussianSmoothing(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经高斯平滑");
				pointer++;
			}
		});
		
		fieldAverageItem = new JMenuItem("领域平均(F)");
		fieldAverageItem.setMnemonic('F');
		fieldAverageItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				fieldAverage(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经领域平均");
				pointer++;
			}
		});
		
		imageSharpening = new JMenu("图像锐化(S)");
		imageSharpening.setMnemonic('S');
		
		laplacian = new JMenuItem("拉普拉斯(L)");
		laplacian.setMnemonic('L');
		laplacian.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				laplacian(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经拉普拉斯锐化");
				pointer++;
			}
		});
		
		laplacianHiBoostFiltering = new JMenuItem("拉斯高增滤波(H)");
		laplacianHiBoostFiltering.setMnemonic('H');
		laplacianHiBoostFiltering.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				laplacianHiBoostFiltering(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经拉普拉斯高增滤波");
				pointer++;
			}
		});
		
		gaussianHiBoostFiletering = new JMenuItem("高斯高增滤波(G)");
		gaussianHiBoostFiletering.setMnemonic('G');
		gaussianHiBoostFiletering.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				gaussianHiBoostFiletering(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经高斯高增滤波");
				pointer++;
			}
		});
		
		
		
		ehanceMenu.add(grayTransformation);
		grayTransformation.add(grayScaleItem);
		grayTransformation.add(linearTransformation);
		linearTransformation.add(liNotSegmentation);
		linearTransformation.add(liSegmentation);
		grayTransformation.add(nonLinearTransformation);
		ehanceMenu.add(histogramModification);
		histogramModification.add(histgramItem);
		histogramModification.add(histogramEqualizationItem);
		histogramModification.add(histogramSpecificationItem);
		ehanceMenu.add(imageSmoothing);  
		imageSmoothing.add(medianFiltering);
		imageSmoothing.add(gaussianSmoothingItem);
		imageSmoothing.add(fieldAverageItem);
		ehanceMenu.add(imageSharpening);
		imageSharpening.add(laplacian);
		imageSharpening.add(laplacianHiBoostFiltering);
		imageSharpening.add(gaussianHiBoostFiletering);
		
		//----图像分割----------------------------------------------------------
		imageSegmentation = new JMenu("图像分割(I)");
		imageSegmentation.setMnemonic('I');
		mb.add(imageSegmentation);
		
		thresholdSeg = new JMenu("全局阈值分割(T)");
		thresholdSeg.setMnemonic('T');
		
		simpleThreshold = new JMenuItem("简单阈值(S)");
		simpleThreshold.setMnemonic('S');
		simpleThreshold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				threshold(e);
				if(okFlag){
				 	dlm.addElement("第"+pointer+"步：图像"+newImage+"经简单阈值分割");
				 	pointer++;
				 	okFlag = false;
				}
				
			}
		});
		
		iterativeThreshold = new JMenuItem("迭代阈值(I)");
		iterativeThreshold.setMnemonic('I');
		iterativeThreshold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				iterative(e);
				if(okFlag){
					dlm.addElement("第"+pointer+"步：图像"+newImage+"经迭代阈值分割");
					pointer++;
					okFlag = false;
				}
			}
		});
		
		otsuThreshold = new JMenuItem("M=2的Ostu法(O)");
		otsuThreshold.setMnemonic('O');
		otsuThreshold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				otsu(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经ostu阈值分割");
				pointer++;
			}
		});
		
		dynamicThreshold = new JMenuItem("动态阈值分割(D)");
		dynamicThreshold.setMnemonic('D');
		dynamicThreshold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				dynamicThreshold(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经动态阈值分割");
				pointer++;
			}
		});
		
		
		
		imageSegmentation.add(thresholdSeg);
		thresholdSeg.add(simpleThreshold);
		thresholdSeg.add(iterativeThreshold);
		thresholdSeg.add(otsuThreshold);
		imageSegmentation.add(dynamicThreshold);
		
		//----边缘检测-------------------------------------------------------
		edgeMenu = new JMenu("边缘检测(E)");
		edgeMenu.setMnemonic('E');
		mb.add(edgeMenu);
		
		gradientMenu =  new JMenu("梯度图(G)");
		gradientMenu.setMnemonic('G');
		
	    horGradientItem = new JMenuItem("水平梯度图(H)");
	    horGradientItem.setMnemonic('H');
	    horGradientItem.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e){
	    		horGradient(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经水平梯度");
				pointer++;
	    	}
	    });
	    
	    
	    verGradientItem = new JMenuItem("垂直梯度图(V)");
	    verGradientItem.setMnemonic('V');
	    verGradientItem.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e){
	    		verGradient(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经垂直梯度");
				pointer++;
	    	}
	    });
	   
	    sobelItem = new JMenuItem("Sobel梯度图(G)");
	    sobelItem.setMnemonic('G');
	    sobelItem.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e){
	    		sobel(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经Sobel梯度");
				pointer++;
	    	}
	    });
		
		cannyItem = new JMenuItem("canny边缘检测(C)");
		cannyItem.setMnemonic('C');
		cannyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				canny(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经canny边缘检测");
				pointer++;
				
				
			}
		});
		
		oriItem = new JMenuItem("梯度图(O)");
		oriItem.setMnemonic('O');
		oriItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				orientation(e);
			}
		});
		
		edgeMenu.add(gradientMenu);
		gradientMenu.add(horGradientItem);
		gradientMenu.add(verGradientItem);
		gradientMenu.add(sobelItem);
		edgeMenu.add(cannyItem);
		edgeMenu.add(oriItem);
		
		//----频域处理----------------------------------------------------------
		frequencyDomainProcessing = new JMenu("频域处理(F)");
		
		frequencyDomainProcessing.setMnemonic('F');
		mb.add(frequencyDomainProcessing);
		
		fftItem = new JMenuItem("傅里叶频谱(F)");
		fftItem.setMnemonic('F');
		fftItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fft(e);
				if(okFlag){
				 	dlm.addElement("第"+pointer+"步：图像"+newImage+"经傅里叶带通滤波器");
				 	pointer++;
				 	okFlag = false;
					}
			}
		});
		
		decomposeItem = new JMenuItem("小波分解(D)");
		decomposeItem.setMnemonic('D');
		decomposeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				decompose(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经小波分解");
				pointer++;
			}
		});
		
		markItem = new JMenuItem("小波水印(M)");
		markItem.setMnemonic('M');
		markItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mark(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经小波水印");
				pointer++;
			}
		});
		
		frequencyDomainProcessing.add(fftItem);
		frequencyDomainProcessing.add(decomposeItem);
		frequencyDomainProcessing.add(markItem);
		
		
		//----形态学---------------------------------------------------------
		morphology = new JMenu("形态学(M)");
		morphology.setMnemonic('M');
		mb.add(morphology);
		
		corrosionItem = new JMenuItem("腐蚀(C)");
		corrosionItem.setMnemonic('C');
		corrosionItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				corrosion(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经腐蚀");
				pointer++;
			}
		});
		
		swellItem = new JMenuItem("膨胀(S)");
		swellItem.setMnemonic('S');
		swellItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				swell(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经膨胀");
				pointer++;
			}
		});
		
		getBone = new JMenuItem("骨架提取(B)");
		getBone.setMnemonic('B');
		getBone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				getBone(e);
				dlm.addElement("第"+pointer+"步：图像"+newImage+"经骨架提取");
				pointer++;
			}
		});
		
		morphology.add(corrosionItem);
		morphology.add(swellItem);
		morphology.add(getBone);
		
		
		
		
				
		//----工具条------------------------------------------------------------
		tb = new JToolBar();
        contentPane.add(tb, BorderLayout.NORTH);
        //----------------------------------------------------------------------
		
		openBtn = new JButton(openIcon);
		openBtn.setToolTipText("打开");
		tb.add(openBtn);
		openBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile(e);
			}
		});
		
		saveBtn = new JButton(saveIcon);
		saveBtn.setToolTipText("保存");
		tb.add(saveBtn);
		saveBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFile(e);
			}
		});
		
		exitBtn = new JButton(exitIcon);
		exitBtn.setToolTipText("退出");
		tb.add(exitBtn);
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exitSystem(e);
			}
		});
		//----------------------------------------------------------------------
    }
    












	/** 退出程序事件 */
    private void exit(WindowEvent e) {
        System.exit(0);
    }
    
    void openFile(ActionEvent e) {
    	chooser.setDialogType(JFileChooser.OPEN_DIALOG);
    	if(chooser.showDialog(this, null) == JFileChooser.APPROVE_OPTION) {
    		try { image = ImageIO.read(chooser.getSelectedFile()); }
        	catch(Exception ex) { return ;}
        	imagePanel.setImage(image);
        	imagePanel.repaint();
        	undoList.clear();
        	redoList.clear();
        	saveRedoInfo(image);
        	saveAllInfo(image);
        	okFlag = true;
       
    	}
    }
    
    void exitSystem(ActionEvent e) {
    	System.exit(0);
    }
    
    void saveFile(ActionEvent e) {
    	chooser.setDialogType(JFileChooser.SAVE_DIALOG);
    	int index = chooser.showDialog(null, "保存文件");
    	if(index == JFileChooser.APPROVE_OPTION) {
    			image = (BufferedImage) undoList.get(0);
    			File f = chooser.getSelectedFile();
    			if(!f.exists()){
    				try {
						f.createNewFile();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
    			}
    			String fileName = chooser.getName(f)+"123";
    			String writePath = chooser.getCurrentDirectory().getAbsolutePath() + fileName;
    			File filePath = new File(writePath);
    			try {
					ImageIO.write(image, "jpg", filePath);
				} catch (IOException e1) {
					e1.printStackTrace();
				}	
    	}
    }
    
    void saveUndoInfo(BufferedImage image) {
    	if(undoList.size() == MAX_UNDO_COUNT) {
    		undoList.removeLast();
    	}
    	undoList.addFirst(image);
    }
    
    void saveRedoInfo(BufferedImage image) {
    	if(redoList.size() == MAX_REDO_COUNT) {
    		redoList.removeLast();
    	}
    	redoList.addFirst(image);	
    }
    
    void saveAllInfo(BufferedImage image) {
    	if(allList.size() == MAX_ALL_COUNT) {
    		allList.removeFirst();
    	}
    	allList.addLast(image);
    }
    
    void undo(ActionEvent e) {
    	if(undoList.size() > 0) {
    	//	saveRedoInfo(image);
    		image = (BufferedImage)undoList.get(0);
    		imagePanel.setImage(image);
        	imagePanel.repaint();
        	undoList.remove(0);
    	}
    }
    
    void redo(ActionEvent e) {
    	if(redoList.size() > 0) {
    		saveUndoInfo(image);
    		image = (BufferedImage)redoList.get(0);
    		imagePanel.setImage(image);
        	imagePanel.repaint();
        	//redoList.remove(0);
    	}
    }
    
    void translation(ActionEvent e) {
    	TranslationDlg translationDlg = new TranslationDlg(this, true);
    	translationDlg.setLocationRelativeTo(this);
    	translationDlg.setImageWidth(image.getWidth());
    	translationDlg.setImageHeight(image.getHeight());
    	if(translationDlg.showModal() == JOptionPane.OK_OPTION) {
    		saveUndoInfo(image);
    		image = GeoTransform.translation(image, translationDlg.getDistance(), translationDlg.getDistance());
    		imagePanel.setImage(image);
        	imagePanel.repaint();
        	saveAllInfo(image);
        	okFlag = translationDlg.getOkFlag();
    	}	
    }
    
    void horMirror(ActionEvent e) {
    	saveUndoInfo(image);
		image = GeoTransform.horMirror(image);
		imagePanel.setImage(image);
    	imagePanel.repaint();
    	saveAllInfo(image);
    }
    
    void verMirror(ActionEvent e) {
    	saveUndoInfo(image);
		image = GeoTransform.verMirror(image);
		imagePanel.setImage(image);
    	imagePanel.repaint();
    	saveAllInfo(image);
    }
    
    void scale(ActionEvent e) {
    	ScaleDlg scaleDlg = new ScaleDlg(this, true);
    	scaleDlg.setLocationRelativeTo(this);
    	scaleDlg.setImageWidth(image.getWidth());
    	scaleDlg.setImageHeight(image.getHeight());
    	if(scaleDlg.showModal() == JOptionPane.OK_OPTION) {
    		saveUndoInfo(image);
    		image = GeoTransform.scale(image, scaleDlg.getScale(), scaleDlg.getScale());
    		imagePanel.setImage(image);
        	imagePanel.repaint();
        	saveAllInfo(image);
        	okFlag = true;
    	}	
    }
    
    void rotate(ActionEvent e) {
    	RotateDlg rotateDlg = new RotateDlg(this, true);
    	rotateDlg.setLocationRelativeTo(this);
    	if(rotateDlg.showModal() == JOptionPane.OK_OPTION) {
    		saveUndoInfo(image);
    		image = GeoTransform.rotate(image, rotateDlg.getAngle(), rotateDlg.getIsResize());
    		imagePanel.setImage(image);
        	imagePanel.repaint();
        	saveAllInfo(image);
        	okFlag = true;
    	}	
    }
    
    void graySacle(ActionEvent e) {
    		saveUndoInfo(image);
    		image = ImageEnhancement.grayScale(image);
    		imagePanel.setImage(image);
        	imagePanel.repaint();
        	saveAllInfo(image);
	}
    
    void liNotSegmentation(ActionEvent e){
    	LinearTransformationDlg litranmadlg = new LinearTransformationDlg(this, true);
    	litranmadlg.setImage(image);
    	litranmadlg.setLocationRelativeTo(this);
    	litranmadlg.show();
    	if(litranmadlg.getokOption() == 1){
    		saveUndoInfo(image);
    		image = ImageEnhancement.linearTransformation(image, Math.round(litranmadlg.getxValue()/2)-20,
    				Math.round(litranmadlg.getxValue1()/2)-20, 300-Math.round(litranmadlg.getyValue()/2)-15,
    				300-Math.round(litranmadlg.getyValue1()/2)-15);
    	}
    	imagePanel.setImage(image);
    	imagePanel.repaint();
    	saveAllInfo(image);
    	okFlag = true;
    	litranmadlg.dispose();
    	
    }
    
    void liSegmentation(ActionEvent e){
    	NotLinearTransformationDlg noLitranmadlg = new NotLinearTransformationDlg(this, true);
    	noLitranmadlg.setImage(image);
    	noLitranmadlg.setLocationRelativeTo(this);
    	noLitranmadlg.show();
    	if(noLitranmadlg.getokOption() == 1){
    		saveUndoInfo(image);
    		image = ImageEnhancement.NotSegmentationLinearTransformation(image, Math.round(noLitranmadlg.getxValue()/2)-20,
    				Math.round(noLitranmadlg.getxValue1()/2)-20, 300-Math.round(noLitranmadlg.getyValue()/2)-15,
    				300-Math.round(noLitranmadlg.getyValue1()/2)-15);
    	}
    	imagePanel.setImage(image);
    	imagePanel.repaint();
    	saveAllInfo(image);
    	okFlag = true;
    	noLitranmadlg.dispose();
    	
    }
    
    
	void nonlinearTran(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
    
	
    void histgram(ActionEvent e) {
		HistgramDlg histgramDlg = new HistgramDlg(this, true);
		histgramDlg.setImage(image);
    	histgramDlg.setLocationRelativeTo(this);
    	histgramDlg.show();
    	
    	//TestForLinearDlg linearDlg = new TestForLinearDlg(this, true);
    	//linearDlg.setImage(image);
    	//linearDlg.setLocationRelativeTo(this);
    	//linearDlg.show();
    }
    

	void equalization(ActionEvent e) {
		saveUndoInfo(image);
		image = ImageEnhancement.histogramEqualization(image);
		imagePanel.setImage(image);
    	imagePanel.repaint();
    	saveAllInfo(image);
	}

	void specification(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	


	void medianFiltering(ActionEvent e) {
		saveUndoInfo(image);
		ImageEnhancement.findLine(image);
		image = ImageEnhancement.medianFiltering(image);
		imagePanel.setImage(image);
    	imagePanel.repaint();
    	saveAllInfo(image);
	}
	

	void gaussianSmoothing(ActionEvent e) {
		saveUndoInfo(image);
		image = ImageEnhancement.gaussianSmoothing(image);
		imagePanel.setImage(image);
    	imagePanel.repaint();
    	saveAllInfo(image);
	}
    
	void fieldAverage(ActionEvent e) {
		saveUndoInfo(image);
		image = ImageEnhancement.fieldAverage(image);
		imagePanel.setImage(image);
    	imagePanel.repaint();
    	saveAllInfo(image);
	}
	
    void laplacian(ActionEvent e) {
    	
    	/*saveUndoInfo(image);
    	EdgeDetector edgeDetector=new EdgeDetector();
        edgeDetector.setSourceImage(image);
        edgeDetector.setThreshold(128);
        edgeDetector.setWidGaussianKernel(5);
        edgeDetector.process();
        image = (BufferedImage) edgeDetector.getEdgeImage();
		imagePanel.setImage(image);
    	imagePanel.repaint();
    	saveAllInfo(image);*/
    	
		saveUndoInfo(image);
		image = ImageEnhancement.laplacian(image);
		imagePanel.setImage(image);
    	imagePanel.repaint();
    	saveAllInfo(image);
	
	}
    

	void gaussianHiBoostFiletering(ActionEvent e) {
		saveUndoInfo(image);
		image = ImageEnhancement.gaussianHiBoostFiletering(image);
		imagePanel.setImage(image);
    	imagePanel.repaint();
    	saveAllInfo(image);
	}

	void laplacianHiBoostFiltering(ActionEvent e) {
		saveUndoInfo(image);
		image = ImageEnhancement.laplacianHiBoostFiltering(image);
		imagePanel.setImage(image);
    	imagePanel.repaint();
    	saveAllInfo(image);
	
    	
	}

	
	public void valueChanged(ListSelectionEvent e) {
		Object source = e.getSource();
		if(source == operationList){
			saveUndoInfo(image);
			int selectNo = operationList.getSelectedIndex();
			image = (BufferedImage)allList.get(selectNo);
			imagePanel.setImage(image);
			imagePanel.repaint();
		}

		
	}
	
	public void threshold(ActionEvent e) {
		ThresholdDlg threshouldDlg = new ThresholdDlg(this, true);
		threshouldDlg.setLocationRelativeTo(this);
		threshouldDlg.show();
		if(threshouldDlg.getModelResult() == JOptionPane.OK_OPTION){
			saveUndoInfo(image);
			image = ImageSegmentation.threshold(image, threshouldDlg.getThreshold());
		    imagePanel.setImage(image);
		    imagePanel.repaint();
		    saveAllInfo(image);
		    okFlag = true;
		}
	}
	
	public void iterative(ActionEvent e) {
		IterativeDlg iterativeDlg = new IterativeDlg(this, true);
		iterativeDlg.setLocationRelativeTo(this);
		iterativeDlg.show();
		if(iterativeDlg.getModelResult() == JOptionPane.OK_OPTION){
			saveUndoInfo(image);
			image = ImageSegmentation.iterative(image, iterativeDlg.getJudgement(), iterativeDlg.getThreshold());
			imagePanel.setImage(image);
			imagePanel.repaint();
			saveAllInfo(image);
			okFlag = true;
		}
	}
	
	
	public void otsu(ActionEvent e) {
		saveUndoInfo(image);
		image = ImageSegmentation.otsu(image);
		imagePanel.setImage(image);
    	imagePanel.repaint();
    	saveAllInfo(image);
	}
	
	void dynamicThreshold(ActionEvent e) {
		saveUndoInfo(image);
		image = ImageSegmentation.dynamic(image);
		imagePanel.setImage(image);
		imagePanel.repaint();
		saveAllInfo(image);
		
	}

	void horGradient(ActionEvent e ) {
		saveUndoInfo(image);
		image = EdgeDetection.horGradient(image);
		imagePanel.setImage(image);
		imagePanel.repaint();
		saveAllInfo(image);
	}
	
	void verGradient(ActionEvent e ) {
		saveUndoInfo(image);
		image = EdgeDetection.verGradient(image);
		imagePanel.setImage(image);
		imagePanel.repaint();
		saveAllInfo(image);
	}
	
	void sobel(ActionEvent e ) {
		saveUndoInfo(image);
		image = EdgeDetection.sobel(image);
		imagePanel.setImage(image);
		imagePanel.repaint();
		saveAllInfo(image);
	}

	void canny(ActionEvent e) {
		saveUndoInfo(image);
		image = EdgeDetection.canny(image);
		imagePanel.setImage(image);
        imagePanel.repaint();
		saveAllInfo(image);
	}
	
	void orientation(ActionEvent e) {

	   
		
	
		//A:拿到需要的数据
		//1.1梯度方向数组
		int orient[];
		orient = EdgeDetection.orientation((BufferedImage) redoList.getFirst());
		//for(int i=0; i<orient.length ; i+=25){
		//	System.out.println(orient[i]);
		//}
		//2.1定义一个角度变量，用于画图。计算线的起始点和终点
		double angle;
		//3.1边缘点判定，需要进行canny边缘检测。拿到边缘图
		BufferedImage tempImage = image;
		//3.2边缘点的像素数组
		int tempRGBs[] = tempImage.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
		int width = image.getWidth();
		int height = image.getHeight();
		
		//B：对imagePanel进行绘图
		//1.1 定义绘图的graphics
		Graphics g = imagePanel.getGraphics();
		//2.1 判断哪些点进行绘图，绘图原则，对边缘图每一个25*25的方块进行检索。如果存在在黑点（边缘），则在25*25的方块中进行画箭头操作
		for(int j=0; j<height-height%25; j+=25){
			for(int i=0; i<width-width%25; i+=25){ //25*25方块移动
				for(int n=0; n<25; n++){
					for(int m=0; m<25; m++){ //方块中的点进行找寻
						if(tempRGBs[(n+j)*width+m+i] == 0xff000000){//如果是黑色，标识这个方块存在边缘。进行绘图
							//如果存在边缘点，跳出整个方块
							m = n = 25;
							//画箭头第一步：定义箭头开始点和结束点的x坐标和y坐标
							int x2, x1, y2, y1;
							//画箭头第二步：得到当前点的梯度角度Angle
							angle = orient[(n+j)*width+m+i] * Math.PI/180;
							System.out.println(angle);
							System.out.println(orient[(n+j)*width+m+i]);
							//画箭头第三步：通过梯度角度得到箭头开始点和结束点。首先进行梯度角度为正数的绘制
							if(angle>=0){ //判断是否为正数
								if(angle<=1* Math.PI & angle>=3/4* Math.PI){
			        				g.setColor(Color.blue);
			        			    x1 = i;
			        			    y1 = (int)(j+12-12*Math.tan(Math.PI-angle));
			        			    x2 = i + 24;
			        			    y2 = (int)(j+12+12*Math.tan(Math.PI-angle));
			        			    g.drawLine(x1, y1, x2, y2);
			        			    g.setColor(Color.red);
			        			    g.fillOval(x2-3, y2-3,6, 6);//箭头指向为一个小圆。如果是箭头的计算需要多计算两个点。麻烦！
			        			}
			        			if(angle<3/4* Math.PI & angle>=1/2* Math.PI){
			        				g.setColor(Color.blue);
			        				x1 = (int)(i+12-12*Math.tan(angle-1/2*Math.PI));
			        				y1 = j;
			        				x2 = (int)(i+12+12*Math.tan(angle-1/2*Math.PI));
			        				y2 = j + 24;
			        			    g.drawLine(x1, y1, x2, y2);
			        			    g.setColor(Color.red);
			        			    g.fillOval(x2-3, y2-3,6, 6);		
			        			}
			        			if(angle<1/2* Math.PI & angle>=1/4* Math.PI){
			        				g.setColor(Color.blue);
			        				x1 = (int)(i+12+12*Math.tan(1/2*Math.PI-angle));
			        				y1 = j;
			        				x2 = (int)(i+12-12*Math.tan(1/2*Math.PI-angle));
			        				y2 = j + 24;
			        			    g.drawLine(x1, y1, x2, y2);
			        			    g.setColor(Color.red);
			        			    g.fillOval(x2-3, y2-3,6, 6);
			        			}
			        			if(angle<1/4* Math.PI & angle>=0* Math.PI){
			        				g.setColor(Color.blue);
			        				x1 = i +24;
			        				y1 = (int)(j+12-12*Math.tan(angle));
			        				x2 = i;
			        				y2 = (int)(j+12+12*Math.tan(angle));
			        			    g.drawLine(x1, y1, x2, y2);
			        			    g.setColor(Color.red);
			        			    g.fillOval(x2-3, y2-3,6, 6);
			        			}
							}else{//如果为负数，和正数操作一个样，但是angle要加上一个π，且箭头方向相反
								angle = angle + Math.PI;
			        			if(angle<=1* Math.PI & angle>=3/4* Math.PI){
			        				g.setColor(Color.blue);
			        			    x1 = 5*i;
			        			    y1 = (int)(5*j+12-12*Math.tan(Math.PI-angle));
			        			    x2 = 5*i + 24;
			        			    y2 = (int)(5*j+12+12*Math.tan(Math.PI-angle));
			        			    g.drawLine(x1, y1, x2, y2);
			        			    g.setColor(Color.red);
			        			    g.fillOval(x1-3, y1-3,6, 6);
			        			}
			        			if(angle<3/4* Math.PI & angle>=1/2* Math.PI){
			        				g.setColor(Color.blue);
			        				x1 = (int)(5*i+12-12*Math.tan(angle-1/2*Math.PI));
			        				y1 = 5*j;
			        				x2 = (int)(5*i+12+12*Math.tan(angle-1/2*Math.PI));
			        				y2 = 5*j + 24;
			        			    g.drawLine(x1, y1, x2, y2);
			        			    g.setColor(Color.red);
			        			    g.fillOval(x1-3, y1-3,6, 6);		
			        			}
			        			if(angle<1/2* Math.PI & angle>=1/4* Math.PI){
			        				g.setColor(Color.blue);
			        				x1 = (int)(5*i+12+12*Math.tan(1/2*Math.PI-angle));
			        				y1 = 5*j;
			        				x2 = (int)(5*i+12-12*Math.tan(1/2*Math.PI-angle));
			        				y2 = 5*j + 24;
			        			    g.drawLine(x1, y1, x2, y2);
			        			    g.setColor(Color.red);
			        			    g.fillOval(x1-3, y1-3,6, 6);
			        			}
			        			if(angle<1/4* Math.PI & angle>0* Math.PI){
			        				g.setColor(Color.blue);
			        				x1 = 5*i +24;
			        				y1 = (int)(5*j+12-12*Math.tan(angle));
			        				x2 = 5*i;
			        				y2 = (int)(5*j+12+12*Math.tan(angle));
			        			    g.drawLine(x1, y1, x2, y2);
			        			    g.setColor(Color.red);
			        			    g.fillOval(x1-3, y1-3,6, 6);
			        			}
							}
						}
					}
				}
			}
		}
		saveAllInfo(image);
		
	}
	
    void fft(ActionEvent e) {
    	FFTFilterDlg fftDlg = new FFTFilterDlg(this, true);
    	fftDlg.setImage(image);
    	fftDlg.setLocationRelativeTo(this);
    	if(fftDlg.showModal() == JOptionPane.OK_OPTION) {
    		saveUndoInfo(image);
    		image = fftDlg.getDestImage();
    		imagePanel.setImage(image);
        	imagePanel.repaint();
        	saveAllInfo(image);
        	okFlag = true;
    	}		
    }
    
    void decompose(ActionEvent e) {
    	saveUndoInfo(image);
		image = WaveletTransform.decompose(image);
		imagePanel.setImage(image);
    	imagePanel.repaint();	
    	saveAllInfo(image);
    }
    
    void mark(ActionEvent e) {
    	saveUndoInfo(image);
    	String word = JOptionPane.showInputDialog("请输入您想说的话",null);
    	String word1 = "游剑涛爱王萍";
		image = WaveletTransform.waterMark(image,word1);
		imagePanel.setImage(image);
    	imagePanel.repaint();
    	saveAllInfo(image);
    }
    
    void corrosion(ActionEvent e){
    	saveUndoInfo(image);
		image = Morphology.corrosion(image);
		imagePanel.setImage(image);
    	imagePanel.repaint();
    	saveAllInfo(image);
    }
    
    void swell(ActionEvent e){
    	
    }
	
    
    void getBone(ActionEvent e) {
    	saveUndoInfo(image);
		image = Morphology.getBone(image);
		imagePanel.setImage(image);
    	imagePanel.repaint();
    	saveAllInfo(image);
    }

	
	
	
}
