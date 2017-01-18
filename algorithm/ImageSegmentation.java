package algorithm;

import java.awt.image.BufferedImage;

public class ImageSegmentation {
	
	/*
	 * 函数名称：简单阈值分割
	 */
	public static BufferedImage threshold(BufferedImage srcImage,int t) {
		int width = srcImage.getWidth(); 
		int height = srcImage.getHeight();
		int srcRGBs[] = ImageEnhancement.grayScale(srcImage).getRGB(0, 0, width, height, null, 0, width);
		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		for(int i =0; i<srcRGBs.length; i++){
			if((srcRGBs[i]&0x000000ff)<=t)
			{
				srcRGBs[i] = 0xff000000;
			}else{
				srcRGBs[i] = 0xffffffff;
			}
		     	
		}
		
		destImage.setRGB(0, 0, width, height, srcRGBs, 0, width);
		return destImage;
		
		
	}
	
	/*
	 * 函数名称：迭代阈值分割
	 */
	
	public static BufferedImage iterative(BufferedImage srcImage, int t0, int t1){
		int width = srcImage.getWidth(); 
		int height = srcImage.getHeight();
		int t2 = 0, temp1 = 0, temp2 = 0;
		int count1 = 0, count2 = 0;
		boolean flag = true;
		int srcRGBs[] = ImageEnhancement.grayScale(srcImage).getRGB(0, 0, width, height, null, 0, width);
		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		while(flag){
			for(int i =0; i<srcRGBs.length; i++){
				if((srcRGBs[i]&0x000000ff)<t1){
					temp1 = temp1+(srcRGBs[i]&0x000000ff);
					count1++;
				}
				else{
					temp2 = temp2+(srcRGBs[i]&0x000000ff);
					count2++;
				}
			}
			int u1 = Math.round(temp1/count1);
			int u2 = Math.round(temp2/count2);
			if(Math.abs((u2-u1)/2-t1)<=t0){
				flag = false;
			}else{				
				t1 = t2;
			}
		}
		
		for(int i =0; i<srcRGBs.length; i++){
			if((srcRGBs[i]&0x000000ff)<=t1)
			{
				srcRGBs[i] = 0xff000000;
			}else{
				srcRGBs[i] = 0xffffffff;
			}
		     	
		}
		
		destImage.setRGB(0, 0, width, height, srcRGBs, 0, width);
		return destImage;
		
		
		
	}

	/*
	 * 函数名称：Ostu法
	 */
	public static BufferedImage otsu(BufferedImage srcImage) {
		//A:拿到所有需要的数据,首先定义所有数据
		int histInfo[] = null;//直方图数组
		int UT = 0;           //全图平均灰度值μT
		int Uj1 = 0;          //第一个区间的平均灰度值μ1
		int Uj2 = 0;          //第二个区间的平均灰度值μ2
		double Wj1 = 0.0;     //第一个区间的概率ω1
		double Wj2 = 0.0;     //第二个区间的概率ω2
		int threshold = 0;    //最终确定的阈值
		
		int temp = 0;         //中间变量
		int temp1 = 0;        //中间变量
		double temp2 = 0;     //中间变量
		double temp3 = 0;     //中间变量
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		//①：利用以前写过的函数对直方图数组其赋值
		histInfo = ImageEnhancement.getHistInfo(srcImage, histInfo);
		//②：计算平均灰度值
		for(int i=0; i<histInfo.length; i++){
			temp = temp+histInfo[i]*i;
		}
	    UT = temp/(width*height);
		//B:计算最合适阈值
	    temp = 0;
		for(int i=0; i<histInfo.length; i++){
			for(int j=0; j<i; j++){
				temp = temp+histInfo[j]*j;
				temp1 = temp1+histInfo[j];
			}
			Uj1 = temp1==0? 0:temp/temp1;
			Wj1 = temp1/width*height;
			temp2 = Wj1*(Uj1-UT)*(Uj1-UT);
			temp = temp1 = 0;
			for(int j=i; j<histInfo.length; j++){
				temp = temp+histInfo[j]*j;
				temp1 = temp1+histInfo[j];
			}
			Uj2 = temp1==0? 0:temp/temp1;
			Wj2 = temp1/width*height;
			temp2 = temp2 + Wj2*(Uj2-UT)*(Uj2-UT);
			if(temp3<temp2){
				temp3 = temp2;
				threshold = i;
			}
		}
		//C:进行阈值分割
		return threshold(srcImage, threshold);
		
	}

	/*
	 * 函数名称：动态阈值分割法
	 */
	public static BufferedImage dynamic(BufferedImage srcImage) {
		//A：截图成为长宽为4的倍数，直接对数组操作
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		int destRGBs[] = new int[(width-width%4)*(height-height%4)];
		int srcRGBs[] = srcImage.getRGB(0, 0, width, height, null, 0, width);
		for(int j=0; j<height-height%4; j++){
			for(int i=0; i<width-width%4; i++){
				destRGBs[j*(width-width%4)+i] = srcRGBs[j*width+i];
				}
		}
		//B:分成16个小图像,并且进行otsu阈值分割
		for(int j=0; j<height-height%4; j=j+(height-height%4)/4){
			for(int i=0; i<width-width%4; i=i+(width-width%4)/4){
				BufferedImage tempImage = new BufferedImage((width-width%4)/4, (height-height%4)/4, BufferedImage.TYPE_INT_RGB);
				int tempRGBs[] = new int[(width-width%4)*(height-height%4)/16];
				for(int n=0; n<(height-height%4)/4; n++){
					for(int m=0; m<(width-width%4)/4; m++){
						tempRGBs[n*(width-width%4)/4+m] = destRGBs[(n+j)*(width-width%4)+i+m];
					}
				}
				tempImage.setRGB(0, 0, (width-width%4)/4, (height-height%4)/4, tempRGBs, 0, (width-width%4)/4);
				tempImage = otsu(tempImage);
				tempRGBs =tempImage.getRGB(0, 0, (width-width%4)/4, (height-height%4)/4, null, 0, (width-width%4)/4);
				for(int n=0; n<(height-height%4)/4; n++){
					for(int m=0; m<(width-width%4)/4; m++){
				destRGBs[(n+j)*(width-width%4)+i+m] = tempRGBs[n*(width-width%4)/4+m];
					}
				}
			}
		}
		//C:返回新图像
		BufferedImage destImage = new BufferedImage((width-width%4), (height-height%4), BufferedImage.TYPE_INT_RGB);
		destImage.setRGB(0, 0, width-width%4, height-height%4, destRGBs, 0, width-width%4);
		return destImage;
	}
	

}
