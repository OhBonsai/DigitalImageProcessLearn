package algorithm;



import java.awt.image.BufferedImage;

import javax.swing.*;

import util.ProcessMath;

public class Morphology {
	
	
	/*
	 * 函数名称：菱形结构腐蚀运算
	 */
	public static BufferedImage corrosion(BufferedImage srcImage){
		
		int kernel[] = new int[] {0,1,0,1,0,1,0,1,0};
		return ProcessMath.corrosion(srcImage, kernel);
	}
	
	
	/*
	 * 函数名称：骨架提取
	 */
	public static BufferedImage getBone(BufferedImage  srcImage){
		
		int width = srcImage.getWidth(); 
		int height = srcImage.getHeight();
		int srcRGBs[] = ImageEnhancement.grayScale(srcImage).getRGB(0, 0, width, height, null, 0, width);
		int temp1[] = new int[srcRGBs.length];
		int temp2[] = new int[temp1.length];
		for(int i=0; i<temp1.length; i++){
			temp1[i] = 0;
			temp2[i] = 0xffffffff;
		}
		
		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		for(int j=1; j<height-1; j++){
			for(int i =1; i<width-1; i++){
				if(!(srcRGBs[j*width+i] == 0xffffffff)){
					int p = 1;
				    int q = 0;
				    int temp = 0;
				    boolean flag = true;
				    while(flag){
				    	p+=2;
				    	q+=1;
				    	for(int m=0; m<p; m++){
				    		for(int n=0; n<p; n++){
				    			if(m==q & n==q){
				    				
				    			}
				    			else{
				    				if(!(srcRGBs[j*width+i+(m-q)*width+n-q] == 0xffffffff)){
				    					temp = temp + 1;
				    				}
				    				else{
				    					flag = false;
				    				}
				    			}
				    		}
				    	}
				    	temp1[j*width+i] = temp;
					}
			    }
			}
		}
		for(int j=1; j<height-1; j++){
			for(int i=1; i<width-1; i++){
				int temp = 0;
				if(!(temp1[j*width+i] == 0)){
			
					for(int m=0; m<4; m++){
						for(int n=0; n<4; n++){
							if(temp1[j*width+i]<temp1[j*width+i+(m-1)*width+n-1]){
								temp = temp + 1;
							}
						}
					}
					if(temp<=2){
						temp2[j*width+i] = 0xff000000;
					}
					
				}				
				
			}
		}
		
		for(int i=0; i<width; i++) {
			for(int j=0; j<height; j++) {
				destImage.setRGB(i, j, temp2[j*width+i]);
			}
		}	
		return destImage;
		
	}

}