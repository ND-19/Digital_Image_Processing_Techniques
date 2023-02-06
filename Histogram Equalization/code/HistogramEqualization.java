import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.geom.*;  
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;
import java.util.Scanner;

public class HistogramEqualization {

    public static void main(String[] args) {
		
		//Taking name of input file and name with which output file should be saved
		Scanner sc = new Scanner(System.in);
        System.out.print("Enter the name of input image file:");
		String input = sc.next();
		System.out.print("\nEnter the name of output image file:");
		String output = sc.next();
		
		HistogramEqualization h = new HistogramEqualization(input, output);

	}
	
	//Constructor used to setup the frame window 
	HistogramEqualization(String in, String out){
		try{
			JFrame frame = new JFrame("Histogram of image: "+in);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new GridLayout(2, 2, 10, 0));
			JPanel img1 = new JPanel();
			JPanel img2 = new JPanel();
			File f1 = new File(in);
			File f2 = new File(out);
			BufferedImage image1 = getGrayscaleImage(ImageIO.read(f1));
			img1.add(new JLabel(new ImageIcon(image1)));
			
			//image2 contains the histogram equalized image
			BufferedImage image2 = equalize(frame,image1);
			ImageIO.write(image2, "png", f2);
			img2.add(new JLabel(new ImageIcon(image2)));
			frame.add(img1);
			frame.add(img2);

			frame.setSize(1280,768);
			frame.setVisible(true);
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		
    }

	//Method for histogram equalization, returns the equalized image
    public BufferedImage equalize(JFrame frame,BufferedImage src) {
 
		//src contains original image
		BufferedImage nImg = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster wr = src.getRaster();
        WritableRaster er = nImg.getRaster();
        int totpix= wr.getWidth()*wr.getHeight();
        int[] histogram = new int[256];

		//Counting the occurence of each pixel intensity in histogram variable for original image
        for (int x = 1; x < wr.getWidth(); x++) {
            for (int y = 1; y < wr.getHeight(); y++) {
                histogram[wr.getSample(x, y, 0)]++;
            }
        }
		
		//Map to store count of each pixel intensity of the original image in key-value pair
        Map<Integer, Integer> unequalized = new TreeMap<Integer, Integer>();
        for (int c = 0; c < 256; c++) {
            unequalized.put(c, histogram[c]);
        }
		
		int[] chistogram = new int[256];
		
		//chistogram variable stores the cummulative count of pixel intensity from 0 to 255
        chistogram[0] = histogram[0];
        for(int i=1;i<256;i++){
            chistogram[i] = chistogram[i-1] + histogram[i];
        }
        
		//arr variable stores the equalized pixel intensity for all the pixels
        float[] arr = new float[256];
        for(int i=0;i<256;i++){
            arr[i] = (float)(((chistogram[i]-chistogram[0])*255.0)/(float)totpix);
        }

		//The new image is set with equalized pixel intensity
		for (int x = 0; x < wr.getWidth(); x++) {
            for (int y = 0; y < wr.getHeight(); y++) {
                int nVal = (int) arr[wr.getSample(x, y, 0)];
                er.setSample(x, y, 0, nVal);
            }
        }
		int[] histogrameq = new int[256];
		for (int x = 1; x < er.getWidth(); x++) {
            for (int y = 1; y < er.getHeight(); y++) {
                histogrameq[er.getSample(x, y, 0)]++;
            }
        }
		
		//Map to store count of each pixel intensity of the equalized image in key-value pair
		Map<Integer, Integer> equalized = new TreeMap<Integer, Integer>();
		 for (int c = 0; c < 256; c++) {
            equalized.put(c, histogrameq[c]);
        }
		
        nImg.setData(er);
		JPanel g1 = new JPanel();
        JPanel g2 = new JPanel();
		g1.setBorder(new EmptyBorder(15, 0, 0, 0));
		g2.setBorder(new EmptyBorder(15, 0, 0, 0));
        g1.add(new JScrollPane(new Graph(unequalized)));
		g2.add(new JScrollPane(new Graph(equalized)));
		frame.add(g1);
		frame.add(g2);
        frame.pack();
       

		return nImg;
    }
	
	//Method to convert our original image into grayscale image before performing histogram equalization, returns grayscale image
	 BufferedImage getGrayscaleImage(BufferedImage src) {
        BufferedImage gImg = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster wr = src.getRaster();
        WritableRaster gr = gImg.getRaster();
        for(int i=0;i<wr.getWidth();i++){
            for(int j=0;j<wr.getHeight();j++){
                gr.setSample(i, j, 0, wr.getSample(i, j, 0));
            }
        }
        gImg.setData(gr);
        return gImg;
    }

	/* Class to plot the histogram for both original image and the equalized image. Done using Graphics Library.
	   Returns histogram of image*/
    protected class Graph extends JPanel {

        protected static final int MIN_BAR_WIDTH = 2;
        private Map<Integer, Integer> maphistory;

        public Graph(Map<Integer, Integer> maphistory) {
            this.maphistory = maphistory;
            int width = (maphistory.size() * MIN_BAR_WIDTH) + 30;
            Dimension minSize = new Dimension(width, 128);
            Dimension prefSize = new Dimension(width, 256);
            setMinimumSize(minSize);
            setPreferredSize(prefSize);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (maphistory != null) {
                int xOffset = 25;
                int yOffset = 25;
                int width = getWidth() - 1 - (xOffset * 2);
                int height = getHeight() - 1 - (yOffset * 2);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(Color.DARK_GRAY);
				g2d.setFont(new Font("TimesRoman", Font.PLAIN, 10));
				g2d.draw(new Line2D.Double(xOffset, height + yOffset, xOffset, 1));  
				
                int barWidth = Math.max(MIN_BAR_WIDTH,
                        (int)Math.floor( (float)width
                        /(float)maphistory.size()));
 
                int maxValue = 0;
                for (Integer key : maphistory.keySet()) {
                    int value = maphistory.get(key);
                    maxValue = Math.max(maxValue, value);
                }
                int xPos = xOffset;
                for (Integer key : maphistory.keySet()) {
                    int value = maphistory.get(key);
                    int barHeight = Math.round(((float) value/(float) maxValue) * height);
                    g2d.setColor(new Color(key, key, key));
                    int yPos = height + yOffset - barHeight;
				
                    Rectangle2D bar = new Rectangle2D.Float(xPos, yPos, barWidth, barHeight);
                    g2d.fill(bar);
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.draw(bar);
                    xPos += barWidth;
                }
				
				//Setting x-axis intervals
				for(int i=0; i<=250; i+=50){
					g2d.drawString(String.valueOf(i),xOffset+i*MIN_BAR_WIDTH,yOffset + height + 10);
				}
				
				//Setting axis labels
				g2d.drawString("Intensity Value",(xOffset + width)/2, 2*yOffset + height);
				drawRotate(g2d,xOffset-10,(yOffset + height)/2,-90,"Count");
                g2d.dispose();
            }
        }
		
		// Method to write text at different angles. Used in writing y-axis label which is at -90 degree
		public void drawRotate(Graphics2D g2d, double x, double y, int angle, String text) 
		{    
			g2d.translate((float)x,(float)y);
			g2d.rotate(Math.toRadians(angle));
			g2d.drawString(text,0,0);
			g2d.rotate(-Math.toRadians(angle));
			g2d.translate(-(float)x,-(float)y);
		}  
    }
}