import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.GridLayout;
import java.util.Scanner;

/*191080053 Nityansh Doshi, VJTI, DIP lab Assignment 4 :EdgeDetection*/
public class EdgeDetection {
		
	public static void main(String[] args){
		
		//Taking name of input file and name with which output file should be saved
		Scanner sc = new Scanner(System.in);
        System.out.print("Enter the name of input image file:");
		String input = sc.next();
		System.out.print("\nEnter the name of output image file:");
		String output = sc.next();
		
		EdgeDetection e = new EdgeDetection(input, output);
	}
	
	EdgeDetection(String in, String out){
		try{
			//Sobel and Prewitt matrix
			int [][] sobel = {{-1, 0, 1},{-2, 0, 2},{-1, 0, 1}};
			int [][] prewitt = {{-1, 0, 1},{-1, 0, 1},{-1, 0, 1}};
			
			JFrame frame = new JFrame("Edge Detection of image: "+in);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new GridLayout(2, 2, 10, 10));
			JPanel img1 = new JPanel();
			JPanel img2 = new JPanel();
			JPanel img3 = new JPanel();
			JPanel img4 = new JPanel();
			File f1 = new File(in);
			File f2 = new File("sobel-"+out);
			File f3 = new File("prewitt-"+out);
			File f4 = new File("laplacian-"+out);
			BufferedImage image1 = ImageIO.read(f1);
			
			// create a label to display text
			JLabel label1 = new JLabel();
			JLabel label2 = new JLabel();
			JLabel label3 = new JLabel();
			JLabel label4 = new JLabel();
			
			// add text to label
			label1.setText(in);
			label2.setText("sobel-"+out);
			label3.setText("prewitt-"+out);
			label4.setText("laplacian-"+out);
			
			
			img1.add(label1);
			img1.add(new JLabel(new ImageIcon(image1)));
			
			//image2 contains the image sharpened with Sobel operator
			BufferedImage image2 = sharpening(image1, sobel);
			ImageIO.write(image2, "png", f2);
			img2.add(label2);
			img2.add(new JLabel(new ImageIcon(image2)));
			
			//image3 contains the image sharpened with prewitt operator
			BufferedImage image3 = sharpening(image1, prewitt);
			ImageIO.write(image3, "png", f3);
			img3.add(label3);
			img3.add(new JLabel(new ImageIcon(image3)));
			
			//image4 contains the image sharpened with laplacian operator
			BufferedImage image4 = laplaciansharpening(image1);
			ImageIO.write(image4, "png", f4);
			img4.add(label4);
			img4.add(new JLabel(new ImageIcon(image4)));
			
			frame.add(img1);
			frame.add(img2);
			frame.add(img3);
			frame.add(img4);

			frame.setSize(image1.getWidth()*2,image1.getHeight()*2);
			frame.setVisible(true);
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}
	//Function to apply sobel and prewitt sharpening
    BufferedImage sharpening(BufferedImage src, int[][] operator){

        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(),BufferedImage.TYPE_INT_RGB);
		
        int x = src.getWidth();
        int y = src.getHeight();
	
        int maxGval = 0;
        int [][] edgeColors = new int[x][y];
		int [][] pixel = new int[3][3];
        int maxGradient = -1;
		

        for (int i = 1; i < x - 1; i++) {
            for (int j = 1; j < y - 1; j++) {
				int gx = 0, gy = 0;
				for(int k = -1; k <= 1; k++){
					for(int l = -1; l <= 1; l++){						
						pixel[k+1][l+1] = getGrayScale(src.getRGB(i+k,j+l));
					}
				}
				for(int k = 0; k <= 2; k++){
					for(int l = 0; l <= 2; l++){						
						gx += operator[k][l]*pixel[k][l];
						gy += operator[l][k]*pixel[k][l];
					}
				}

                double gval = Math.sqrt((gx * gx) + (gy * gy));
                int g = (int) gval;

                if(maxGradient < g) {
                    maxGradient = g;
                }

                edgeColors[i][j] = g;
            }
        }

        double scale = 255.0 / maxGradient;

        for (int i = 1; i < x - 1; i++) {
            for (int j = 1; j < y - 1; j++) {
                int edgeColor = edgeColors[i][j];
                edgeColor = (int)(edgeColor * scale);
                edgeColor = 0xff000000 | (edgeColor << 16) | (edgeColor << 8) | edgeColor;

                out.setRGB(i, j, edgeColor);
            }
        }

       return out;
    }
	//Function to apply sobel and prewitt sharpening
	BufferedImage laplaciansharpening(BufferedImage src){

        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(),BufferedImage.TYPE_INT_RGB);
		int [][] laplacian = {{0, -1, 0},{-1, 4, -1},{0, -1, 0}};
        int x = src.getWidth();
        int y = src.getHeight();
	
        int maxGval = 0;
        int [][] edgeColors = new int[x][y];
		int [][] pixel = new int[3][3];
        int maxGradient = -1;
		

        for (int i = 1; i < x - 1; i++) {
            for (int j = 1; j < y - 1; j++) {
				int g = 0;
				for(int k = -1; k <= 1; k++){
					for(int l = -1; l <= 1; l++){						
						pixel[k+1][l+1] = getGrayScale(src.getRGB(i+k,j+l));
					}
				}
				for(int k = 0; k <= 2; k++){
					for(int l = 0; l <= 2; l++){						
						g += laplacian[k][l]*pixel[k][l];
					}
				}

                if(maxGradient < g) {
                    maxGradient = g;
                }
				
                edgeColors[i][j] = (g > 0)? g: 0;
            }
        }

        double scale = 255.0 / maxGradient;

        for (int i = 0; i < x - 1; i++) {
            for (int j = 0; j < y - 1; j++) {
                int edgeColor = edgeColors[i][j];
                edgeColor = (int)(edgeColor * scale);

                edgeColor = 0xff000000 | (edgeColor << 16) | (edgeColor << 8) | edgeColor;

                out.setRGB(i, j, edgeColor);
            }
        }

       return out;
    }

	//Function to get grayscale value from rgb
    public static int  getGrayScale(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;

        //from https://en.wikipedia.org/wiki/Grayscale, calculating luminance
        int gray = (int)(0.2126 * r + 0.7152 * g + 0.0722 * b);
        //int gray = (r + g + b) / 3;

        return gray;
    }
}