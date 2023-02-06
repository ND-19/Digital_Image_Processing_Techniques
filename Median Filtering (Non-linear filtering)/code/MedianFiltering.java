import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import javax.imageio.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.util.Scanner;

class MedianFiltering{
    public static void main(String[] args){
		
		//Taking name of input file and name with which output file should be saved
		Scanner sc = new Scanner(System.in);
        System.out.print("Enter the name of input image file:");
		String input = sc.next();
		System.out.print("\nEnter the name of output image file:");
		String output = sc.next();
		System.out.print("\nEnter the mask size:");
		int masksize = sc.nextInt();
		
		MedianFiltering h = new MedianFiltering(input, output, masksize);
	}
	MedianFiltering(String in, String out, int masksize){
		try{
			JFrame frame = new JFrame("Median Filtering of image: "+in);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new GridLayout(1, 2, 10, 10));
			JPanel img1 = new JPanel();
			JPanel img2 = new JPanel();
			File f1 = new File(in);
			File f2 = new File(out);
			BufferedImage image1 = ImageIO.read(f1);
			// create a label to display text
			JLabel label1 = new JLabel();
			JLabel label2 = new JLabel();
			// add text to label
			label1.setText(in);
			label2.setText(out);
			/* BufferedImage image1 = getGrayscaleImage(ImageIO.read(f1)); */
			img1.add(label1);
			img1.add(new JLabel(new ImageIcon(image1)));
			
			//image2 contains the filtered image
			BufferedImage image2 = medianfilter(image1, masksize);
			ImageIO.write(image2, "png", f2);
			img2.add(label2);
			img2.add(new JLabel(new ImageIcon(image2)));
			
			frame.add(img1);
			frame.add(img2);

			frame.setSize(image1.getWidth()*2,image1.getHeight() + 100);
			frame.setVisible(true);
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}
	
	//Function for median filtering of the image
	public BufferedImage medianfilter(BufferedImage src, int masksize) {

		BufferedImage nImg = new BufferedImage(src.getWidth(), src.getHeight(),BufferedImage.TYPE_INT_RGB);
		
		int mask = masksize*masksize;
        Color[] pixel=new Color[mask];
        int[] R=new int[mask];
        int[] B=new int[mask];
        int[] G=new int[mask];
        for(int i = masksize/2; i < src.getWidth() - masksize/2; i++)
            for(int j = masksize/2; j < src.getHeight() - masksize/2; j++)
            {
				int c=0;
				int min = -masksize/2;
				int max = masksize/2;
				for(int k = min; k <= max; k++){
					for(int l = min; l <= max; l++){						
						pixel[c]=new Color(src.getRGB(i+k,j+l));
						c++;
					}
				}
				for(int k = 0;k < mask;k++){
				   R[k]=pixel[k].getRed();
				   B[k]=pixel[k].getBlue();
				   G[k]=pixel[k].getGreen();
				}
			    Arrays.sort(R);
			    Arrays.sort(G);
			    Arrays.sort(B);
                nImg.setRGB(i,j,new Color(R[c/2],G[c/2],B[c/2]).getRGB());
            }
        return nImg;
    }
}