package algorithm;

import javax.swing.*;

import util.ProcessUtil;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

public class WaveletTransform {
	
	public static void wavelet(float s0[], int s0Len, float p[], float q[], int sup,
							   float s1[], float w1[]) {
		int n, k;
		int index;
		
		for(k=0; k<s0Len/2; k++) {
			s1[k] = 0;
			w1[k] = 0;
			for(n=0; n<sup; n++) {
				index = (n+2*k)%s0Len;
				s1[k] += p[n]*s0[index];
				w1[k] += q[n]*s0[index];
			}
		}
	}
	
	public static void iwavelet(float s1[], float w1[], int s1Len, float p[],
								float q[], int sup, float s0[]) {
		int n, k;
		int index, ofs;
		
		ofs = s1Len > 1024 ? s1Len : 1024;
		for(n=0; n<s1Len; n++) {
			s0[2*n+1] = 0;
			s0[2*n] = 0;
			for(k=0; k<sup/2; k++) {
				index = (n-k+ofs)%s1Len;
				s0[2*n+1] += p[2*k+1]*s1[index]+q[2*k+1]*w1[index];
				s0[2*n] += p[2*k]*s1[index]+q[2*k]*w1[index];
			}
		}
	}
	
	private static void rvmtx(float a[][], float b[][], int xsize, int ysize) {
		for(int j=0; j<ysize; j++) {
			for(int i=0; i<xsize; i++) {
				b[i][j] = a[j][i];
			}
		}
	}
	
	public static void wavelet2d(float s0[][], int s0Len, float s1[][],
								 float w1v[][], float w1h[][], float w1d[][]) {
		int i, j;
		
		float s1x[][] = new float[s0Len][];
		float w1x[][] = new float[s0Len][];
		for(i=0; i<s0Len; i++) {
			s1x[i] = new float[s0Len/2];
			w1x[i] = new float[s0Len/2];
		}
		
		float s1xt[][] = new float[s0Len/2][];
		float w1xt[][] = new float[s0Len/2][];
		for(i=0; i<s0Len/2; i++) {
			s1xt[i] = new float[s0Len];
			w1xt[i] = new float[s0Len];
		} 
		
		float s1t[][] = new float[s0Len/2][];
		float w1vt[][] = new float[s0Len/2][];
		float w1ht[][] = new float[s0Len/2][];
		float w1dt[][] = new float[s0Len/2][];
		for(i=0; i<s0Len/2; i++) {
			s1t[i] = new float[s0Len/2];
			w1vt[i] = new float[s0Len/2];
			w1ht[i] = new float[s0Len/2];
			w1dt[i] = new float[s0Len/2];	
		}
		
		int sup = 4;
		float p[] = new float[] {0.482962913145f, 0.836516303738f,
								 0.224143868042f, -0.129409522551f};
		float q[] = new float[4];
		for(i=0; i<sup; i++) q[i] = (float)Math.pow(-1, i)*p[sup-i-1];
		
		for(i=0; i<s0Len; i++) wavelet(s0[i], s0Len, p, q, sup, s1x[i], w1x[i]);
		rvmtx(s1x, s1xt, s0Len/2, s0Len);
		rvmtx(w1x, w1xt, s0Len/2, s0Len);
		
		for(i=0; i<s0Len/2; i++) {
			wavelet(s1xt[i], s0Len, p, q, sup, s1t[i], w1ht[i]);
			wavelet(w1xt[i], s0Len, p, q, sup, w1vt[i], w1dt[i]);
		}
		rvmtx(s1t, s1, s0Len/2, s0Len/2);
		rvmtx(w1ht, w1h, s0Len/2, s0Len/2);
		rvmtx(w1vt, w1v, s0Len/2, s0Len/2);
		rvmtx(w1dt, w1d, s0Len/2, s0Len/2);
	}
	
	public static void iwavelet2d(float s1[][], int s1Len, float w1v[][], float w1h[][], 
								  float w1d[][], float s0[][]) {
		int i, j;
		
		float s1xt[][] = new float[s1Len*2][];
		float w1xt[][] = new float[s1Len*2][];
		for(i=0; i<s1Len*2; i++) {
			s1xt[i] = new float[s1Len];
			w1xt[i] = new float[s1Len];
		}
		
		float s1x[][] = new float[s1Len][];
		float w1x[][] = new float[s1Len][];
		for(i=0; i<s1Len; i++) {
			s1x[i] = new float[s1Len*2];
			w1x[i] = new float[s1Len*2];
		} 
		
		float s1t[][] = new float[s1Len][];
		float w1vt[][] = new float[s1Len][];
		float w1ht[][] = new float[s1Len][];
		float w1dt[][] = new float[s1Len][];
		for(i=0; i<s1Len; i++) {
			s1t[i] = new float[s1Len];
			w1vt[i] = new float[s1Len];
			w1ht[i] = new float[s1Len];
			w1dt[i] = new float[s1Len];	
		}
		
		int sup = 4;
		float p[] = new float[] {0.482962913145f, 0.836516303738f,
								 0.224143868042f, -0.129409522551f};
		float q[] = new float[4];
		for(i=0; i<sup; i++) q[i] = (float)Math.pow(-1, i)*p[sup-i-1];
		
		rvmtx(s1, s1t, s1Len, s1Len);
		rvmtx(w1h, w1ht, s1Len, s1Len);
		rvmtx(w1v, w1vt, s1Len, s1Len);
		rvmtx(w1d, w1dt, s1Len, s1Len);	
		for(i=0; i<s1Len; i++) {
			iwavelet(s1t[i], w1ht[i], s1Len, p, q, sup, s1x[i]);
			iwavelet(w1vt[i], w1dt[i], s1Len, p, q, sup, w1x[i]);
		}
		
		rvmtx(s1x, s1xt, s1Len*2, s1Len);
		rvmtx(w1x, w1xt, s1Len*2, s1Len);
		for(i=0; i<s1Len*2; i++) iwavelet(s1xt[i], w1xt[i], s1Len, p, q, sup, s0[i]);
	}
	
	public static BufferedImage decompose(BufferedImage srcImage) {
		int i, j;
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		int srcRGBs[] = srcImage.getRGB(0, 0, width, height, null, 0, width);
		int s0Len = width > height ? height : width;
		BufferedImage destImage = new BufferedImage(s0Len, s0Len, BufferedImage.TYPE_INT_RGB);
		
		float yhs[] = new float[3];
		float s0[][] = new float[s0Len][];
		for(i=0; i<s0Len; i++) s0[i] = new float[s0Len];
		for(i=0; i<s0Len; i++) {
			for(j=0; j<s0Len; j++) {
				ProcessUtil.convertRGBToYHS(srcRGBs[i*width+j], yhs);
				s0[i][j] = yhs[0];
			}
		}
		
		float s1[][] = new float[s0Len/2][];
		float w1v[][] = new float[s0Len/2][];
		float w1h[][] = new float[s0Len/2][];
		float w1d[][] = new float[s0Len/2][];
		for(i=0; i<s0Len/2; i++) {
			s1[i] = new float[s0Len/2];
			w1v[i] = new float[s0Len/2];
			w1h[i] = new float[s0Len/2];
			w1d[i] = new float[s0Len/2];	
		}
		
		wavelet2d(s0, s0Len, s1, w1v, w1h, w1d);
		
		float max = 0, min = 500;
		float maxv = 0, minv = 500;
		float maxh = 0, minh = 500;
		float maxd = 0, mind = 500;
		for(i=0; i<s0Len/2; i++) {
			for(j=0; j<s0Len/2; j++) {
				if(max < s1[i][j]) max = s1[i][j];
				if(min > s1[i][j]) min = s1[i][j];
				
				if(maxv < w1v[i][j]) maxv = w1v[i][j];
				if(minv > w1v[i][j]) minv = w1v[i][j];
				
				if(maxh < w1h[i][j]) maxh = w1h[i][j];
				if(minh > w1h[i][j]) minh = w1h[i][j];
				
				if(maxd < w1d[i][j]) maxd = w1d[i][j];
				if(mind > w1d[i][j]) mind = w1d[i][j];
			}
		}
		
		int rgb[] = new int[3];
		for(i=0; i<s0Len/2; i++) {
			for(j=0; j<s0Len/2; j++) {
				ProcessUtil.convertRGBToYHS(srcRGBs[i*2*width+j*2], yhs);	
				yhs[0] = (s1[i][j]-min)*255/(max-min);
				destImage.setRGB(j, i, ProcessUtil.convertYHSToRGB(yhs));
				
				rgb[0] = (int)((w1h[i][j]-minh)*255/(maxh-minh));
				rgb[1] = rgb[2] = rgb[0];
				destImage.setRGB(j+s0Len/2, i, ProcessUtil.encodeColor(rgb));
				
				rgb[0] = (int)((w1v[i][j]-minv)*255/(maxv-minv));
				rgb[1] = rgb[2] = rgb[0];
				destImage.setRGB(j, i+s0Len/2, ProcessUtil.encodeColor(rgb));
				
				rgb[0] = (int)((w1d[i][j]-mind)*255/(maxd-mind));
				rgb[1] = rgb[2] = rgb[0];
				destImage.setRGB(j+s0Len/2, i+s0Len/2, ProcessUtil.encodeColor(rgb));
			}
		}
		
		return destImage;
	}
	
	public static BufferedImage waterMark(BufferedImage srcImage, String word) {
		int i, j;
		int width = srcImage.getWidth();
		int height = srcImage.getHeight();
		int s0Len = width > height ? height : width;
		j = 1;
		for(i=s0Len/2; i>0; i/=2) j *= 2;
		s0Len = j > 1 ? j : 0;
		
		int srcRGBs[] = srcImage.getRGB(0, 0, width, height, null, 0, width);
		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		float yhs[] = new float[3];
		float s0[][] = new float[s0Len][];
		for(i=0; i<s0Len; i++) s0[i] = new float[s0Len];
		for(i=0; i<s0Len; i++) {
			for(j=0; j<s0Len; j++) {
				ProcessUtil.convertRGBToYHS(srcRGBs[i*width+j], yhs);
				s0[i][j] = yhs[0];
			}
		}
		
		float s1[][] = new float[s0Len/2][];
		float w1v[][] = new float[s0Len/2][];
		float w1h[][] = new float[s0Len/2][];
		float w1d[][] = new float[s0Len/2][];
		for(i=0; i<s0Len/2; i++) {
			s1[i] = new float[s0Len/2];
			w1v[i] = new float[s0Len/2];
			w1h[i] = new float[s0Len/2];
			w1d[i] = new float[s0Len/2];	
		}
		
		wavelet2d(s0, s0Len, s1, w1v, w1h, w1d);
		
		BufferedImage markImage = new BufferedImage(s0Len/2, s0Len/2, BufferedImage.TYPE_INT_RGB);
		Graphics g = markImage.getGraphics();
		g.setColor(Color.white);
		g.drawString(word, 10, s0Len/4);
		int []markRGBs = markImage.getRGB(0, 0, s0Len/2, s0Len/2, null, 0, s0Len/2);

		float maxd = 0, mind = 500;
		for(i=0; i<s0Len/2; i++) {
			for(j=0; j<s0Len/2; j++) {
				if(maxd < w1d[i][j]) maxd = w1d[i][j];
				if(mind > w1d[i][j]) mind = w1d[i][j];
			}
		}
		for(i=0; i<s0Len/2; i++) {
			for(j=0; j<s0Len/2; j++) {
				ProcessUtil.convertRGBToYHS(markRGBs[i*s0Len/2+j], yhs);
				if(yhs[0] > 100) w1d[i][j] = maxd/2;
			}
		}
		iwavelet2d(s1, s0Len/2, w1v, w1h, w1d, s0);
		
		destImage.setRGB(0, 0, width, height, srcRGBs, 0, width);
		for(i=0; i<s0Len; i++) {
			for(j=0; j<s0Len; j++) {
				ProcessUtil.convertRGBToYHS(srcRGBs[i*width+j], yhs);	
				yhs[0] = s0[i][j];
				destImage.setRGB(j, i, ProcessUtil.convertYHSToRGB(yhs));
			}
		}
		
		return destImage;
	}
}
