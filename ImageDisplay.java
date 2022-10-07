
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;


public class ImageDisplay {

	JFrame frame;
	JLabel lbIm1;
	BufferedImage imgOne;
	int width = 640; // default image width and height
	int height = 480;

	/** Read Image RGB
	 *  Reads the image of given width and height at the given imgPath into the provided BufferedImage.
	 */
	private void readImageRGB(int width, int height, String foregroundFolderPath, String backgroundFolderPath, BufferedImage img, JFrame jframe, int mode)
	{
		try
		{
			int frameLength = width*height*3;
			File folder = new File(foregroundFolderPath);
			File folder2 = new File(backgroundFolderPath);
			File[] listOfFiles = folder.listFiles();
			File[] listOfFiles2 = folder2.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				RandomAccessFile raf = new RandomAccessFile(listOfFiles[i], "r");
				RandomAccessFile raf2 = new RandomAccessFile(listOfFiles2[i], "r");
				raf.seek(0);
				raf2.seek(0);

				long len = frameLength;
				byte[] bytes = new byte[(int) len];
				byte[] bytes2 = new byte[(int) len];

				raf.read(bytes);
				raf2.read(bytes2);

				int prevPixel = 0;
				int ind = 0;
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						byte a = 0;
						byte r = bytes[ind];
						byte g = bytes[ind + height * width];
						byte b = bytes[ind + height * width * 2];

						byte r2 = bytes2[ind];
						byte g2 = bytes2[ind + height * width];
						byte b2 = bytes2[ind + height * width * 2];

						int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
						int pix2 = 0xff000000 | ((r2 & 0xff) << 16) | ((g2 & 0xff) << 8) | (b2 & 0xff);
						// RGB to HSV conversion (for easier processing of finding green pixels)

						if (mode == 1) {
							double[] hsvPixel = RGBtoHSV(bytes[ind], bytes[ind + height * width], bytes[ind + height * width * 2]);
							double H_Value = hsvPixel[0];

							img.setRGB(x, y, pix);

							// If the H value falls in the range of green replace RGB value with RGB value of the background video
							if (310 >= H_Value) {
								img.setRGB(x, y, pix);
							} else {
								img.setRGB(x, y, pix2);
							}
						} else if (mode == 0) {
							if (prevPixel == pix) {
								img.setRGB(x, y, pix2);
							} else {
								img.setRGB(x, y, pix);
							}
							prevPixel = pix;
						}
						ind++;
					}
				}
				// Send img content to frame now that RGB has been processed
				GridBagLayout gLayout = new GridBagLayout();
				jframe.getContentPane().setLayout(gLayout);
				// Initialize label
				lbIm1 = new JLabel(new ImageIcon(img));

				GridBagConstraints c = new GridBagConstraints();
				c.fill = GridBagConstraints.HORIZONTAL;
				c.anchor = GridBagConstraints.CENTER;
				c.weightx = 0.5;
				c.gridx = 0;
				c.gridy = 0;

				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 1;
				// Add label
				jframe.setContentPane(lbIm1);
				jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

				jframe.pack();
				jframe.setVisible(true);
				// Sleep enforces fps (24 fps requirement for 20 seconds)
				Thread.sleep(9);

			}
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	double[] RGBtoHSV(double r, double g, double b)
	{

		// R, G, B values are divided by 255
		// to change the range from 0..255 to 0..1
		r = r / 255.0;
		g = g / 255.0;
		b = b / 255.0;

		// h, s, v = hue, saturation, value
		double cmax = Math.max(r, Math.max(g, b)); // maximum of r, g, b
		double cmin = Math.min(r, Math.min(g, b)); // minimum of r, g, b
		double diff = cmax - cmin; // diff of cmax and cmin.
		double h = -1, s = -1;

		// if cmax and cmax are equal then h = 0
		if (cmax == cmin)
			h = 0;

			// if cmax equal r then compute h
		else if (cmax == r)
			h = (60 * ((g - b) / diff) + 360) % 360;

			// if cmax equal g then compute h
		else if (cmax == g)
			h = (60 * ((b - r) / diff) + 120) % 360;

			// if cmax equal b then compute h
		else if (cmax == b)
			h = (60 * ((r - g) / diff) + 240) % 360;

		// if cmax equal zero
		if (cmax == 0)
			s = 0;
		else
			s = (diff / cmax) * 100;

		// compute v
		double v = cmax * 100;
		return new double[]{h,s,v};
	}

	public void showIms(String[] args){

		// Read a parameter from command line
		String param1 = args[1];
		System.out.println("The second parameter was: " + param1);

		// Initialize BufferedImage
		imgOne = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// Use label to display the image
		frame = new JFrame();
		// Convert mode command-line argument to integer
		int modeINT = Integer.parseInt(args[2]);
		// Populate bufferedImage
		readImageRGB(width, height, args[0], args[1], imgOne, frame, modeINT);

	}

	public static void main(String[] args) {
		ImageDisplay ren = new ImageDisplay();
		ren.showIms(args);
	}

}
