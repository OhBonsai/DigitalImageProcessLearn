package util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

public class ProcessUtil {
	
	/** 取得三色**/
	public static int[] decodeColor(int color, int rgb[]) {
		if(rgb == null) rgb = new int[3];
		rgb[0] = (color & 0x00ff0000) >> 16;
		rgb[1] = (color & 0x0000ff00) >> 8;
		rgb[2] = (color & 0x000000ff);	
		return rgb;
	} 
	
	/** 取得单色**/
	public static int decodeColor(int color) {
		int g = (color & 0x000000ff);
		return g;
	}
	

	/** 合并三色**/
	public static int encodeColor(int rgb[]) {
		int color = (255 << 24) | (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
		return color;
	}

	/** 灰度化计算**/
	public static int getBrightness(int color) {
		// TODO Auto-generated method stub
		int r = (color & 0x00ff0000) >> 16;
		int g = (color & 0x0000ff00) >> 8;
		int b = (color & 0x000000ff);
		int y = Math.round(0.3f*r+0.59f*g+0.11f*b);
		y = y < 0 ? 0 : y;
		y = y > 255 ? 255 : y;
		return y;
	}
	
	/** 取得灰度最大值 **/
	public static int getMaxGray(int srcRGBs[])
	{
		int maxGray=0;
		for(int i=0; i<srcRGBs.length; i++)
		{
			if(srcRGBs[i]>maxGray){
				maxGray = srcRGBs[i];
			}
		}
		return maxGray;
	}
	
   /** GRAY_TO_RGB **/
	public static int convertGRAYToRGB(int gray){
		int rgb = (255 << 24) | (gray << 16) | (gray << 8) | gray;
		return rgb;
	}
	
   /** RGB_TO_YHS **/
	public static float[] convertRGBToYHS(int color, float yhs[]) {
		if(yhs == null) yhs = new float[3];
		int r = (color & 0x00ff0000) >> 16;
		int g = (color & 0x0000ff00) >> 8;
		int b = (color & 0x000000ff);	
		
		yhs[0] = (float)(0.3*r+0.59*g+0.11*b);
		double c1 = 0.7*r-0.59*g-0.11*b;
		double c2 = -0.3*r-0.59*g+0.89*b;
		yhs[2] = (float)Math.sqrt(c1*c1+c2*c2);
		if(yhs[2] < 0.005) yhs[1] = 0;
		else {
			yhs[1] = (float)Math.atan2(c1, c2);
			if(yhs[1] < 0) yhs[1] += (float)Math.PI*2;
		}
		
		
		return yhs;
	}
	
	 /** YHS_TO_RGB **/
	public static int convertYHSToRGB(float yhs[]) {
		double c1 = yhs[2]*Math.sin(yhs[1]);
		double c2 = yhs[2]*Math.cos(yhs[1]);
		int r = (int)Math.round(yhs[0]+c1);
		r = r < 0 ? 0 : r;
		r = r > 255 ? 255 : r;
		int g = (int)Math.round(yhs[0]-0.3*c1/0.9-0.11*c2/0.59);
		g = g < 0 ? 0 : g;
		g = g > 255 ? 255 : g;
		int b = (int)Math.round(yhs[0]+c2);
		b = b < 0 ? 0 : b;
		b = b > 255 ? 255 : b;		
		int color = (255 << 24) | (r << 16) | (g << 8) | b;
		return color;
	}
	
	/**对image格式的图像分散到点**/
	public static int[] image2pixels(Image image) {
        int ai[] = new int[image.getWidth(null)*image.getHeight(null)];
        int picsize = image.getWidth(null)*image.getHeight(null);
        PixelGrabber pixelgrabber =
                new PixelGrabber(image, 0, 0, image.getWidth(null), image.getHeight(null), ai, 0, image.getWidth(null));
        try {
                pixelgrabber.grabPixels();
        } catch (InterruptedException interruptedexception) {
                interruptedexception.printStackTrace();
        }
        boolean flag = false;
        int k1 = 0;
        do {
                if (k1 >= 16)
                        break;
                int i = (ai[k1] & 0xff0000) >> 16;
                int k = (ai[k1] & 0xff00) >> 8;
                int i1 = ai[k1] & 0xff;
                if (i != k || k != i1) {
                        flag = true;
                        break;
                }
                k1++;
        } while (true);
        if (flag) {
                for (int l1 = 0; l1 < picsize; l1++) {
                        int j = (ai[l1] & 0xff0000) >> 16;
                        int l = (ai[l1] & 0xff00) >> 8;
                        int j1 = ai[l1] & 0xff;
                        ai[l1] =
                               (int) (0.29799999999999999D * (double) j
                                       + 0.58599999999999997D * (double) l
                                       + 0.113D * (double) j1);
                }

        } else {
                for (int i2 = 0; i2 < picsize; i2++)
                        ai[i2] = ai[i2] & 0xff;

        }
        return ai;
 }


}
