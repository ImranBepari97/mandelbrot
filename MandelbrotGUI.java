
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class MandelbrotGUI extends JFrame {

	/*
	 * Essentially, the MandebrotGUI class simply holds together the 3 panels
	 * that make this program. The main three parts are the Mandelbrot itself,
	 * the Julia set, and the editor.
	 */

	MandelPanel mandel;
	JuliaPanel julia;
	MandelEditor editor;

	public MandelbrotGUI(String title) {
		super(title);
		init();
	}

	public void init() {
		// Setting the layout for the 3 panels.
		this.setSize(1600, 900);
		Container c = new Container();
		c = this.getContentPane();

		c.setLayout(new BorderLayout(5, 5));
		// the main panel takes up 1000 of the 1600 width, more of it on screen
		// than the other two
		mandel = new MandelPanel(1000, 900);
		TitledBorder mandelBorder;
		mandelBorder = BorderFactory.createTitledBorder("Mandelbrot");

		mandel.setBorder(mandelBorder);
		c.add(mandel, BorderLayout.CENTER);

		JPanel right = new JPanel();
		right.setLayout(new GridLayout(2, 1));
		c.add(right, BorderLayout.LINE_END);

		julia = new JuliaPanel();
		mandelBorder = BorderFactory.createTitledBorder("Julia Set");
		julia.setBorder(mandelBorder);
		right.add(julia);

		editor = new MandelEditor(mandel, julia);
		right.add(editor);
		mandelBorder = BorderFactory.createTitledBorder("Editor");
		editor.setBorder(mandelBorder);

		this.setResizable(true);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		MandelbrotGUI mb = new MandelbrotGUI("Fractal Viewer");
	}
}

class MandelPanel extends JPanel {
	/*
	 * The MandelPanel is the main Mandelbrot image. Generally, the MandelPanel
	 * only accepts data from the MandelEditor, and displays the image. All
	 * mathsy calculations are done in here, calculated from the raw information
	 * given by the editor.
	 */

	// used in drawing the image
	private int iterations;
	private BufferedImage bi;
	private Complex c, z;
	private FractalFormula fractal;
	private String formula;

	// used in zooming and drawing the rectangle over the image
	private Point rectStart, rectEnd;
	private boolean hasDragged = false;

	// colour stuff, read from 3 sliders
	private int colour;
	private int red;
	private int green;
	private int blue;

	// max and min values for the axis. X is real numbers, Y is complex numbers
	private double maxX, maxY, minX, minY;

	public MandelPanel(int width, int height) {
		// sets the default values
		this.maxX = 2.0;
		this.maxY = 1.6;
		this.minX = -2.0;
		this.minY = -1.6;
		this.setSize(width, height);
		this.iterations = 50;
		fractal = new FractalFormula();
		formula = "Mandelbrot";

	}

	// getters and setters

	public double getMaxX() {
		return maxX;
	}

	public String getFormula() {
		return formula;
	}

	public void setTheFormula(String formula) {
		this.formula = formula;
	}

	public void setMaxX(double maxWidth) {
		this.maxX = maxWidth;
	}

	public double getMaxY() {
		return maxY;
	}

	public void setMaxY(double maxHeight) {
		this.maxY = maxHeight;
	}

	public double getMinX() {
		return minX;
	}

	public void setMinX(double minWidth) {
		this.minX = minWidth;
	}

	public double getMinY() {
		return minY;
	}

	public void setMinY(double minHeight) {
		this.minY = minHeight;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public int getIterations() {
		return this.iterations;
	}

	public void setDragged(boolean what) {
		hasDragged = what;
	}

	public boolean getDragged() {
		return hasDragged;
	}

	public void setStart(Point points) {
		this.rectStart = points;
		System.out.println("start is now " + points.getX());
	}

	public void setEnd(Point points) {
		this.rectEnd = points;
		System.out.println("end is now " + points.getX());
	}

	public Point getStart() {
		return rectStart;
	}

	public Point getEnd() {
		return rectEnd;
	}

	public int getColour() {
		return colour;
	}

	public void setColour(int colour) {
		this.colour = colour;
	}

	public int getRed() {
		return red;
	}

	public void setRed(int red) {
		this.red = red;
	}

	public int getGreen() {
		return green;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public int getBlue() {
		return blue;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}

	// method that calculates the image
	public void calcImage() {
		// creates a colour from the RGB values
		Color tempColour = new Color(red, green, blue);
		// buffered image takes RGB integer, so convert is needed
		colour = tempColour.getRGB();

		bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		// scrolls through all of the pixels in the panel
		for (int x = 0; x < this.getWidth(); x++) {
			for (int y = 0; y < this.getHeight(); y++) {
				// int curIterations = 0;

				// converts the pixel to complex number, no matter what the
				// scale is
				double creal = (maxX - minX) * (x / (double) this.getWidth()) + minX;
				double cimag = (maxY - minY) * (y / (double) this.getHeight()) + minY;

				z = new Complex(0, 0);
				// creates the complex based on pixel coord
				c = new Complex(creal, cimag);

				// keeps carrying out formula until the modulus rises above 2,
				// or runs out of iterations
				// to carry out

				int curIterations = fractal.calcFractal(c, z, this.iterations, formula);

				// sets the colour based on the amount of iterations
				if (curIterations == iterations) {
					bi.setRGB(x, y, 0);
				} else {

					bi.setRGB(x, y, colour / (curIterations + 1));

				}
			}
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Doesnt calculate the image while drawing rectangle, stops lag
		if (!hasDragged) {
			calcImage();
		}
		// Draws the image
		g.drawImage(bi, 0, 0, this);

		// Drag to draw rectangle
		if (hasDragged) {
			g.setColor(Color.blue);
			// The absolute values and min values make the rectangle work in 4
			// directions.
			g.drawRect(Math.min(rectStart.x, rectEnd.x), Math.min(rectStart.y, rectEnd.y),
					Math.abs(rectEnd.x - rectStart.x), Math.abs(rectEnd.y - rectStart.y));
		}

	}

}

class JuliaPanel extends JPanel {

	private int iterations;
	private BufferedImage bi;
	private Complex c, z;
	private FractalFormula fractal;
	private String formula;
	private boolean liveUpdate;

	private double maxX, maxY, minX, minY;

	public JuliaPanel() {
		this.maxX = 2.0;
		this.maxY = 1.6;
		this.minX = -2.0;
		this.minY = -1.6;
		this.setSize(600, 450);
		this.iterations = 100;
		this.z = new Complex(0, 0);
		formula = "Mandelbrot";
		fractal = new FractalFormula();
		liveUpdate = true;

	}

	public boolean isLiveUpdate() {
		return liveUpdate;
	}

	public void setLiveUpdate(boolean liveUpdate) {
		this.liveUpdate = liveUpdate;
	}

	public String getFormula() {
		return formula;
	}

	public void setTheFormula(String formula) {
		this.formula = formula;
	}

	public void setComplex(Complex z) {
		this.z = z;
	}

	public Complex getComplex() {
		return z;
	}

	public BufferedImage getBi() {
		return bi;
	}

	// same calculations for mandelbrot, but uses an initial complex input
	// instead of 0,0
	public void calcImage() {
		bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < this.getWidth(); x++) {
			for (int y = 0; y < this.getHeight(); y++) {

				double creal = (maxX - minX) * (x / (double) this.getWidth()) + minX;
				double cimag = (maxY - minY) * (y / (double) this.getHeight()) + minY;

				c = new Complex(creal, cimag);

				int curIterations = fractal.calcFractal(z, c, this.iterations, formula);

				// sets the colour based on iterations
				if (curIterations == iterations) {
					bi.setRGB(x, y, 0);
				} else {
					bi.setRGB(x, y, 51455 / (curIterations + 1));
				}
			}
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		calcImage();
		g.drawImage(bi, 0, 0, this);
	}

}
