package util;


import java.awt.image.BufferedImage;

public class ProcessMath {
	
	/** 双线性插值*/
	public static float biLinear(int d00, int d01, int d10, int d11, 
							 float p, float q) {
		return (1-q)*( (1-p)*d00+p*d01 ) + q*( (1-p)*d10+p*d11 );
	}
	
	/** 灰度线性变换计算**/
	public static int linearFunction(int a ,int b, int c, int d,int f){
		return(d-c)*(f-a)/(b-a)+c;
	}
	
	/** 函数名称：卷积计算  **/
	public static BufferedImage convolve(BufferedImage srcImage, float kernel[]) {
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		int srcRGBs[] = new int[(height+2)*(width+2)];
		srcImage.getRGB(0, 0, width, height, srcRGBs, width+3, width+2);
		//给四周的空白赋值
		for(int i=1; i<width; i++) {
			srcRGBs[i] = srcRGBs[i+width+2];
			srcRGBs[i+(height+1)*(width+2)] = srcRGBs[i+height*(width+2)];
		}
		for(int j=0; j<height+2; j++) {
			srcRGBs[j*(width+2)] = srcRGBs[j*(width+2)+1];
			srcRGBs[j*(width+2)+width+1] = srcRGBs[j*(width+2)+width];
		}
		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		float frgb[] = new float[3];
		int rgb[] = new int[3];
		int rgbs[][] = new int[9][];
		for(int i=0; i<9; i++) rgbs[i] = new int[3];
		for(int j=1; j<=height; j++) {
			for(int i=1; i<=width; i++) {
				ProcessUtil.decodeColor(srcRGBs[(j-1)*(width+2)+i-1], rgbs[0]);
				ProcessUtil.decodeColor(srcRGBs[(j-1)*(width+2)+i], rgbs[1]);
				ProcessUtil.decodeColor(srcRGBs[(j-1)*(width+2)+i+1], rgbs[2]);
				
				ProcessUtil.decodeColor(srcRGBs[(j)*(width+2)+i-1], rgbs[3]);
				ProcessUtil.decodeColor(srcRGBs[(j)*(width+2)+i], rgbs[4]);
				ProcessUtil.decodeColor(srcRGBs[(j)*(width+2)+i+1], rgbs[5]);
				
				ProcessUtil.decodeColor(srcRGBs[(j+1)*(width+2)+i-1], rgbs[6]);
				ProcessUtil.decodeColor(srcRGBs[(j+1)*(width+2)+i], rgbs[7]);
				ProcessUtil.decodeColor(srcRGBs[(j+1)*(width+2)+i+1], rgbs[8]);
				
				frgb[0] = frgb[1] = frgb[2] = 0;
				
				//卷积运算
				for(int k=0; k<9; k++) {
					frgb[0] += kernel[k]*rgbs[k][0];
					frgb[1] += kernel[k]*rgbs[k][1];
					frgb[2] += kernel[k]*rgbs[k][2];	
				}
				rgb[0] = (int)frgb[0];
				rgb[0] = rgb[0] < 0 ? 0 : rgb[0];
				rgb[0] = rgb[0] > 255 ? 255 : rgb[0];
				rgb[1] = (int)frgb[1];
				rgb[1] = rgb[1] < 0 ? 0 : rgb[1];
				rgb[1] = rgb[1] > 255 ? 255 : rgb[1];
				rgb[2] = (int)frgb[2];
				rgb[2] = rgb[2] < 0 ? 0 : rgb[2];
				rgb[2] = rgb[2] > 255 ? 255 : rgb[2];
				
				destImage.setRGB(i-1, j-1, ProcessUtil.encodeColor(rgb));
			}
		}	
		
		
		return destImage;
	}
	

	/*********高斯计算*******************************/
    public static float gaussian(float f, float f1) {
    	
        return (float) Math.exp((-f * f) / ((float) 2 * f1 * f1));
        
    }
    
    
    public static float hypotenuse(float f, float f1) {
        if (f == 0.0F && f1 == 0.0F)
        	return 0.0F;
        else
            return (float) Math.sqrt(f * f + f1 * f1);
     }
    
    /************腐蚀运算***************************/
    public static BufferedImage corrosion(BufferedImage srcImage , int kernel[]){
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		int srcRGBs[] = srcImage.getRGB(0, 0, width, height, null, 0, width);
		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		destImage = srcImage;
		int destRGBs[] = srcRGBs;
		int temp[] = new int[9];
		for(int j=1; j<=height-1; j++){
			for(int i=1; i<=width-1; i++){
				if(srcRGBs[j*(width)+i] == 0xff000000){
					
			        temp[0] = srcRGBs[(j-1)*(width)+i-1];
			        temp[1] = srcRGBs[(j-1)*(width)+i];
			        temp[2] = srcRGBs[(j-1)*(width)+i+1];
					
			        temp[3] = srcRGBs[(j)*(width)+i-1];
			        temp[4] = srcRGBs[(j)*(width)+i];
			        temp[5] = srcRGBs[(j)*(width)+i+1];
					
			        temp[6] = srcRGBs[(j+1)*(width)+i-1];
			        temp[7] = srcRGBs[(j+1)*(width)+i];
			        temp[8] = srcRGBs[(j+1)*(width)+i+1];
					for(int m=0; m<9; m++){
						if(kernel[m]==1 && temp[m]==0xffffffff){
							destRGBs[j*(width)+i] = 0xffffffff;
						}
					}
				}
				destImage.setRGB(i-1, j-1, destRGBs[j*(width)+i]);
			}
		}
		
		
		
    	return destImage;
    }
    
    /************膨胀运算***************************/
    public static BufferedImage swell(BufferedImage srcImage , int kernel[]){
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		int srcRGBs[] = srcImage.getRGB(0, 0, width, height, null, 0, width);
		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		destImage = srcImage;
		int destRGBs[] = srcRGBs;
		int temp[] = new int[9];
		for(int j=1; j<=height-1; j++){
			for(int i=1; i<=width-1; i++){
				if(srcRGBs[j*width+i] == 0xff000000){
					
			        temp[0] = srcRGBs[(j-1)*(width)+i-1];
			        temp[1] = srcRGBs[(j-1)*(width)+i];
			        temp[2] = srcRGBs[(j-1)*(width)+i+1];
					
			        temp[3] = srcRGBs[(j)*(width)+i-1];
			        temp[4] = srcRGBs[(j)*(width)+i];
			        temp[5] = srcRGBs[(j)*(width)+i+1];
					
			        temp[6] = srcRGBs[(j+1)*(width)+i-1];
			        temp[7] = srcRGBs[(j+1)*(width)+i];
			        temp[8] = srcRGBs[(j+1)*(width)+i+1];
					for(int m=0; m<9; m++){
						if(kernel[m]==1 && temp[m]==0xffffffff){
							destRGBs[j*(width)+i] = 0xffffffff;
						}
					}
				}
				destImage.setRGB(i-1, j-1, destRGBs[j*(width)+i]);
			}
		}
		
		
		
    	return destImage;
    }
}
