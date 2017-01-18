package algorithm;


import util.ProcessUtil;


import java.awt.image.BufferedImage;
import java.util.Arrays;

import util.ProcessMath;
import util.ProcessUtil;

public class ImageEnhancement {



	/*
	 * 函数名称：灰度变换_灰度化
	 */
	public static BufferedImage grayScale(BufferedImage srcImage) {
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		int srcRGBs[] = srcImage.getRGB(0, 0, width, height, null, 0, width);
		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		int rgb[] = new int[3];
		for(int i=0; i<width; i++) {
			for(int j=0; j<height; j++) {
				rgb[0] = ProcessUtil.getBrightness(srcRGBs[j*width+i]);
				rgb[1] = rgb[2] = rgb[0];
				destImage.setRGB(i, j, ProcessUtil.encodeColor(rgb));
			}
		}	
		return destImage;
	}
	
	/*
	 * 函数名称：灰度变换_线性变换  
	 */
	public static BufferedImage linearTransformation(BufferedImage srcImage ,int a, int b, int c, int d) {
		int width = srcImage.getWidth(); 
		int height = srcImage.getHeight();
		int srcRGBs[] = grayScale(srcImage).getRGB(0, 0, width, height, null, 0, width);
		int maxGray = ProcessUtil.getMaxGray(srcRGBs);
		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		int rgb[] = new int[3];
		for(int i=0; i<width; i++){
			for(int j=0; j<height; j++){
				
					if(ProcessUtil.decodeColor(srcRGBs[j*width+i])<=b&ProcessUtil.decodeColor(srcRGBs[j*width+i])>=a){
						rgb[0] = (int)ProcessMath.linearFunction(a, b, c, d, ProcessUtil.decodeColor(srcRGBs[j*width+i]));
						rgb[1] = rgb[2] = rgb[0];
						destImage.setRGB(i, j, ProcessUtil.encodeColor(rgb));	
					}else{
						rgb[0] = srcRGBs[j*width+i];
						rgb[1] = rgb[2] =rgb[0];
						destImage.setRGB(i, j, ProcessUtil.encodeColor(rgb));
					}
				}
		}
		return destImage;
	}
	/*
	 * 函数名称：线性变换_分段线性变换
	 */
	public static BufferedImage NotSegmentationLinearTransformation(BufferedImage srcImage ,int a, int b, int c, int d) {
		int width = srcImage.getWidth(); 
		int height = srcImage.getHeight();
		int srcRGBs[] = grayScale(srcImage).getRGB(0, 0, width, height, null, 0, width);
		int maxGray = ProcessUtil.getMaxGray(srcRGBs);
		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		int rgb[] = new int[3];
		for(int i=0; i<width; i++){
			for(int j=0; j<height; j++){
					if(ProcessUtil.decodeColor(srcRGBs[j*width+i])<a)
					{
						rgb[0] = (int)((c/a)*ProcessUtil.decodeColor(srcRGBs[j*width+i]));
						rgb[1] = rgb[2] = rgb[0];
						destImage.setRGB(i, j, ProcessUtil.encodeColor(rgb));
					}else if(ProcessUtil.decodeColor(srcRGBs[j*width+i])<b&ProcessUtil.decodeColor(srcRGBs[j*width+i])>=a){
						rgb[0] = (int)ProcessMath.linearFunction(a, b, c, d, ProcessUtil.decodeColor(srcRGBs[j*width+i]));
						rgb[1] = rgb[2] = rgb[0];
						destImage.setRGB(i, j, ProcessUtil.encodeColor(rgb));
					}else if(ProcessUtil.decodeColor(srcRGBs[j*width+i])<maxGray-1&ProcessUtil.decodeColor(srcRGBs[j*width+i])>=b){
						rgb[0] = (int)ProcessMath.linearFunction(b, maxGray-1 , d, maxGray-1, ProcessUtil.decodeColor(srcRGBs[j*width+i]));
						rgb[1] = rgb[2] = rgb[0];
						destImage.setRGB(i, j, ProcessUtil.encodeColor(rgb));
					}else{
						destImage.setRGB(i, j, srcRGBs[j*width+i]);
					}
				}
		}
		return destImage;
	}
	
	
	
	/*
	 * 函数名称：直方图修正_绘制直方图
	 */
	public static int[] getHistInfo(BufferedImage srcImage, int histArray[]) {
		if(histArray == null) histArray = new int[256];
		for(int i=0; i<256; i++) histArray[i] = 0;
		
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		int srcRGBs[] = srcImage.getRGB(0, 0, width, height, null, 0, width);
		
		float yhs[] = new float[3];
		int rgb[] = new int[3];
		for(int i=0; i<width; i++) {
			for(int j=0; j<height; j++) {
				ProcessUtil.convertRGBToYHS(srcRGBs[j*width+i], yhs);
				int hist = Math.round(yhs[0]);
				hist = hist < 0 ? 0 :hist;
				hist = hist > 255 ? 255 : hist;
				histArray[hist]++;
			}
		}		
		return histArray;
	}
	
	
	/*
	 * 函数名称：直方图修正_直方图均衡化(只支持灰色图像，读入的图像会进行灰度化处理)
	 * 函数优化：如果要支持彩色图像，要对RGB三色的值进行处理，比如R的值为灰度值，通过其他两色可以求出R
	 *       的颜色，然后重新组装三色；
	 */
	public static BufferedImage histogramEqualization(BufferedImage srcImage) {
		
		int width = srcImage.getWidth(); 
		int height = srcImage.getHeight();
		int srcRGBs[] = grayScale(srcImage).getRGB(0, 0, width, height, null, 0, width);
		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int tempGray[] = new int[srcRGBs.length];

		//第一步：取得直方图数组(这里乘以一百的原因是概率是在太小，float表达不了，用double耗费大，之后除以100即可)
        int histArray1[] = getHistInfo(srcImage,new int[256]);
		float histArray[] = new float[histArray1.length];
        for(int i=0; i<histArray.length; i++){
			histArray[i]=(100*histArray1[i])/(width*height);
		}

        //第二步：取得直方图求和数组
        float sumArray[] = new float[histArray.length];
		for(int i=0; i<sumArray.length; i++){
			for(int j=0; j<=i; j++){
			sumArray[i]+=histArray[j];
			}
		}
		
		//第三步：取得所有灰度值数组
		for(int i=0; i<tempGray.length; i++){
			tempGray[i] = srcRGBs[i]&0x000000ff;
		}
		
		//第四步：求得最大最小灰度值
		int max,min;
		max= min = tempGray[0];
		for(int i=0; i<tempGray.length; i++){
			if(tempGray[i]>max){
				max= tempGray[i];
			}if(tempGray[i]<min){
				min = tempGray[i];
			}
		}
		
		//第五步：赋值
		int temp = 0;
		for(int i=0; i<tempGray.length; i++){
			temp = Math.round(sumArray[tempGray[i]]*(max-min)/100+min);
			temp = temp < 0 ? 0 :temp;
			temp = temp > 255 ? 255 : temp;
			tempGray[i] = temp;
		}
		

		//第五步：变成RGB格式
		for(int j=0; j<height; j++){
			for(int i=0; i<width; i++){
				destImage.setRGB(i, j,ProcessUtil.convertGRAYToRGB(tempGray[j*width+i]));
			}
		}	
		
		
		return destImage;
	}
	
	/*
	 * 函数名称：直方图修正_直方图规定化
	 * 设计思想：整一张灰度递增图
	 */
	public static BufferedImage  histogramSpecification(BufferedImage srcImage) {
		BufferedImage destImage = null;
		return destImage;
		
	}
	
	/*
	 * 函数名称：中值滤波
	 */
	public static BufferedImage medianFiltering(BufferedImage srcImage) {
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		int srcRGBs[] = new int[(height+2)*(width+2)];
		srcImage.getRGB(0, 0, width, height, srcRGBs, width+3, width+2);
		for(int i=1; i<width; i++) {
			srcRGBs[i] = srcRGBs[i+width+2];
			srcRGBs[i+(height+1)*(width+2)] = srcRGBs[i+height*(width+2)];
		}
		for(int i=0; i<height+2; i++) {
			srcRGBs[i*(width+2)] = srcRGBs[i*(width+2)+1];
			srcRGBs[i*(width+2)+width+1] = srcRGBs[i*(width+2)+width];
		}
		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		int rgb[] = new int[3];
		int rs[] = new int[9];
		int gs[] = new int[9];
		int bs[] = new int[9];
		for(int j=1; j<=height; j++) {
			for(int i=1; i<=width; i++) {
				ProcessUtil.decodeColor(srcRGBs[(j-1)*(width+2)+i-1], rgb);
				rs[0] = rgb[0];		gs[0] = rgb[1];		bs[0] = rgb[2];
				ProcessUtil.decodeColor(srcRGBs[(j-1)*(width+2)+i], rgb);
				rs[1] = rgb[0];		gs[1] = rgb[1];		bs[1] = rgb[2];
				ProcessUtil.decodeColor(srcRGBs[(j-1)*(width+2)+i+1], rgb);
				rs[2] = rgb[0];		gs[2] = rgb[1];		bs[2] = rgb[2];
				
				ProcessUtil.decodeColor(srcRGBs[(j)*(width+2)+i-1], rgb);
				rs[3] = rgb[0];		gs[3] = rgb[1];		bs[3] = rgb[2];
				ProcessUtil.decodeColor(srcRGBs[(j)*(width+2)+i], rgb);
				rs[4] = rgb[0];		gs[4] = rgb[1];		bs[4] = rgb[2];
				ProcessUtil.decodeColor(srcRGBs[(j)*(width+2)+i+1], rgb);
				rs[5] = rgb[0];		gs[5] = rgb[1];		bs[5] = rgb[2];
				
				ProcessUtil.decodeColor(srcRGBs[(j+1)*(width+2)+i-1], rgb);
				rs[6] = rgb[0];		gs[6] = rgb[1];		bs[6] = rgb[2];
				ProcessUtil.decodeColor(srcRGBs[(j+1)*(width+2)+i], rgb);
				rs[7] = rgb[0];		gs[7] = rgb[1];		bs[7] = rgb[2];
				ProcessUtil.decodeColor(srcRGBs[(j+1)*(width+2)+i+1], rgb);
				rs[8] = rgb[0];		gs[8] = rgb[1];		bs[8] = rgb[2];
				
				Arrays.sort(rs);	Arrays.sort(gs);	Arrays.sort(bs);
				rgb[0] = rs[4];		rgb[1] = gs[4];		rgb[2] = bs[4];
				
				destImage.setRGB(i-1, j-1, ProcessUtil.encodeColor(rgb));
			}
		}	
		
		
		return destImage;
		
	}
	
	/*
	 * 函数名称：高斯平滑
	 */
	public static BufferedImage gaussianSmoothing(BufferedImage srcImage) {
		float kernel[] = new float[] {1.0f/16, 2.0f/16, 1.0f/16, 2.0f/16, 4.0f/16, 2.0f/16, 1.0f/16, 2.0f/16, 1.0f/16};
		return ProcessMath.convolve(srcImage, kernel);
	}
	
	/*
	 * 函数名称：领域平均
	 */
	public static BufferedImage fieldAverage(BufferedImage srcImage) {
		float kernel[] = new float[] {1.0f/9, 1.0f/9, 1.0f/9, 1.0f/9, 1.0f/9, 1.0f/9, 1.0f/9, 1.0f/9, 1.0f/9};
		return ProcessMath.convolve(srcImage, kernel);
	}
	
	/*
	 * 函数名称：拉普拉斯变换
	 */

	public static BufferedImage laplacian(BufferedImage srcImage) {
		float kernel[] = new float[] {-1, -1, -1, -1, 8, -1, -1, -1, -1};
		return ProcessMath.convolve(srcImage, kernel);
	}
	
	/*
	 * 函数名称：拉普拉斯高增滤波
	 */
	public static BufferedImage laplacianHiBoostFiltering(BufferedImage srcImage) {
		float kernel[] = new float[] {-1 ,-1, -1, -1, 9, -1, -1, -1, -1};
		return ProcessMath.convolve(srcImage, kernel);
	}
	
	/*
	 * 函数名称：高斯平滑高增滤波
	 */
	public static BufferedImage gaussianHiBoostFiletering(BufferedImage srcImage) {
		float kernel[] = new float[] {-3.0f/80, -6.0f/80, -3.0f/80, -6.0f/80, 29f/20, -6.0f/80, -3.0f/80, -6.0f/80, -3.0f/80};
		return ProcessMath.convolve(srcImage, kernel);
	}
	
	/*
	 * 函数名称：canny算法测试
	 */
	public static BufferedImage sobel(BufferedImage srcImage) {
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		
		
		/*******************第一步：高斯平滑*************************/

		float kernel0[] = new float[] {1.0f/16, 2.0f/16, 1.0f/16, 2.0f/16, 4.0f/16, 2.0f/16, 1.0f/16, 2.0f/16, 1.0f/16};
		srcImage = ProcessMath.convolve(srcImage, kernel0);
		
		/*******************第二步：计算幅值和梯度方向*****************/
		//sobel差分算子
		float kernel1[] = new float[] {-1 ,0, 1, -2, 0, 2, -1, 0, 1};
		float kernel2[] = new float[] {-1 ,-2, -1, 0, 0, 0, 1, 2, 1};
		
		//求出dx数组
		int srcRGBxs[] = ProcessMath.convolve(srcImage, kernel1).getRGB(0, 0, width, height, null, 0, width);
		for(int i=0; i<srcRGBxs.length; i++){
			srcRGBxs[i] = ProcessUtil.decodeColor(srcRGBxs[i]);
		}
		
		//求出dy数组
		int srcRGBys[] = ProcessMath.convolve(srcImage, kernel2).getRGB(0, 0, width, height, null, 0, width);
		for(int i=0; i<srcRGBxs.length; i++){
			srcRGBys[i] = ProcessUtil.decodeColor(srcRGBys[i]);
		}
		
		//求出梯度幅值数组
		double srcAmplitude[] = new double[srcRGBxs.length];
		double maxAmplitude =0.0;
		double minAmplitude =0.0;
		for(int i=0; i<srcAmplitude.length; i++){
			srcAmplitude[i] = Math.sqrt(srcRGBys[i]*srcRGBys[i]+srcRGBys[i]*srcRGBys[i]);
			if(srcAmplitude[i]>=255){
				srcAmplitude[i]=255;
			}
			maxAmplitude = srcAmplitude[i]>maxAmplitude? srcAmplitude[i]:maxAmplitude;
			minAmplitude = srcAmplitude[i]<minAmplitude? srcAmplitude[i]:minAmplitude;
		}
		int srcRGBs[] = new int[srcRGBxs.length];
		for(int i=0; i<srcAmplitude.length; i++){
			srcRGBs[i] = ProcessUtil.convertGRAYToRGB((int)srcAmplitude[i]);
		}
		
		//求出梯度方向数组（大小为数组中元素*π）
		double srcAngles[] = new double[srcRGBxs.length];
		for(int i =0; i<srcAngles.length; i++){
			if(srcRGBxs[i]!=0){
				srcAngles[i] =  2*Math.atan(srcRGBys[i]/srcRGBxs[i])/Math.PI;
			}else{
				srcAngles[i] = 1/2;
			}

		}
		for(int i =0;i<srcAngles.length;i=i+30){
			System.out.println(srcAngles[i]);
		}

		/*******************第三步：非最大抑制*************************/
		

		//遍历整个梯度方向数组，通过梯度方向角大小，在幅值数组中找出其对应比较的两个幅值
		for(int i=width+2; i<srcAmplitude.length-width-1; i++){
			if(srcAngles[i]>=3/8&srcAngles[i]<5/8){
				if(!(srcAmplitude[i]>srcAmplitude[i+1]|srcAmplitude[i]>srcAmplitude[i-1+width])) {
					srcRGBs[i] = 0xff000000;
				}
			}
			if(srcAngles[i]>=5/8&srcAngles[i]<7/8){
				if(!(srcAmplitude[i]>srcAmplitude[i+1-width]|srcAmplitude[i]>srcAmplitude[i+1+width])) {
					srcRGBs[i] = 0xff000000;
				}
			}
			if(srcAngles[i]>=7/8&srcAngles[i]<1){
				if(!(srcAmplitude[i]>srcAmplitude[i-width]|srcAmplitude[i]>srcAmplitude[i+width])) {
					srcRGBs[i] = 0xff000000;
				}
			}
			if(srcAngles[i]>=1/8&srcAngles[i]<3/8){
				if(!(srcAmplitude[i]>srcAmplitude[i-1-width]|srcAmplitude[i]>srcAmplitude[i+1+width])) {
					srcRGBs[i] = 0xff000000;
				}
			}
			if(srcAngles[i]>=0&srcAngles[i]<1/8){
				if(!(srcAmplitude[i]>srcAmplitude[i+1-width]|srcAmplitude[i]>srcAmplitude[i-1+width])) {
					srcRGBs[i] = 0xff000000;
				}
			}
			
		}
		
		/**********************第四步：双阈值检测和连接边缘******************************/
		
		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		destImage.setRGB(0, 0, width, height, srcRGBs, 0, width);
		return  destImage;
	}

	/*
	 * 函数名称：查找直线
	 */
	public static int[] findLine(BufferedImage srcImage){
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		int srcRGBs[] = srcImage.getRGB(0, 0, width, height, null, 0, width);
		int temp1 = 0;
		int temp2 = 0;
		int temp3 = 0;
		int temp4 = 0;
		for(int i=0; i<srcRGBs.length; i++){
			temp1= srcRGBs[i]==0xff000000? temp1+1: temp1;
			
		}
		int srcBlackX[] = new int[temp1];
		int srcBlackY[] = new int[temp1];
		
		for(int i=0; i<srcRGBs.length; i++){
			if(srcRGBs[i] == 0xff000000){
				srcBlackX[temp2] = i%width;
				srcBlackY[temp2] = i/width;
				temp2++;
			}
		}
		int tempAngle[] = new int[90];//存储斜率的个数。
		int tempX[] = new int[90];//记录斜率的第一个X值
		int tempY[] = new int[90];//记录斜率的第一个Y值
		for(int i=0; i<tempX.length; i++){
			tempX[i] = tempY[i] = -1;
			tempAngle[i] = 0;
		}
		
		for(int i=0; i<srcBlackX.length; i++){
			int random = (int)(Math.random()*temp1);
			temp3 = (int) ((srcBlackX[i]-srcBlackX[random])==0 ? 
					45:Math.atan(Math.abs(srcBlackY[i]-srcBlackY[random])/Math.abs(srcBlackX[i]-srcBlackX[random]))*90/Math.PI);
			tempAngle[temp3] = tempAngle[temp3] + 1;
			if(tempX[temp3] == -1){
				tempX[temp3] = srcBlackX[i];
			}
			if(tempY[temp3] == -1){
				tempY[temp3] = srcBlackY[i];
			}
		}
		
		int arg = 0;
		int result[] = new int[90];
		for(int i =0; i<result.length; i++){
			result[i] = -1;
		}
		for(int i=0; i<tempAngle.length; i++){
			arg = (int) (tempAngle[i]+arg);
		}
		arg = arg/tempAngle.length;
		for(int i=0; i<tempAngle.length; i++){
			if(tempAngle[i]>5*arg){
				result[temp4] = tempX[i];
				temp4++;
				result[temp4] = tempY[i];
				temp4++;
				result[temp4] = tempAngle[i];
				temp4++;
			}
		}
		
		for(int i=0; i<result.length; i++){
			if(result[i]!= -1){
				System.out.println(result[i]);
			}
		}
		return result;
	}
}
