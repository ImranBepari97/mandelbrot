

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MandelEditor extends JPanel {

	/*
	 * The MandelEditor handles all of the user input and feeds all the
	 * information to the images. It has references to both the MandelPanel and
	 * the JuliaPanel to do this.
	 */

	// references to images
	private MandelPanel mandel;
	private JuliaPanel julia;

	// the clicked complex value the mandelbrot gives to the julia
	private Complex clicked;

	// the files and buffered objects that save the julia sets
	private File savedComplexes;
	private BufferedReader reader;
	private BufferedWriter writer;
	private HashMap<String, String> saved;

	// tabs for gui organizations
	private JTabbedPane tabs;

	// stack for storing the previous zooms
	private Stack zooms;

	public MandelEditor(MandelPanel panel, JuliaPanel julia) {
		// initializing
		this.mandel = panel;
		clicked = new Complex(0, 0);
		this.julia = julia;
		this.setSize(600, 300);
		this.readSaved();
		zooms = new Stack<Double>();

		tabs = new JTabbedPane();
		tabs.setPreferredSize(new Dimension(450, 390));
		tabs.addTab("Mandelbrot", new MandelControls(this));
		tabs.addTab("Julia", new JuliaControls(this));

		this.add(tabs);

	}

	// getters and setters
	public JTabbedPane getTabs() {
		return tabs;
	}

	public Stack<Double> getZooms() {
		return zooms;
	}

	public void setZooms(Stack zooms) {
		this.zooms = zooms;
	}

	public MandelPanel getMandel() {
		return mandel;
	}

	public JuliaPanel getJulia() {
		return julia;
	}

	public File getSavedComplexes() {
		return savedComplexes;
	}

	public HashMap<String, String> getSaved() {
		return saved;
	}

	// reads the file and the saved julias. adds to panel
	public void readSaved() {
		savedComplexes = new File("savedComplexes.txt");
		saved = new HashMap<String, String>();

		// first block checks if the file exists, if it doesnt then create it
		if (!savedComplexes.exists()) {
			try {
				savedComplexes.createNewFile();
				System.out.println("New save file created.");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("savedComplexes does not exist, and cannot create one.");
			}
		}

		// reading
		try {
			reader = new BufferedReader(new FileReader(savedComplexes));
			while (reader.ready()) {
				String currentLine = reader.readLine();

				if (currentLine != null) {
					// the name of the julia save and the actual complex values
					// are split by a colon
					String[] split = currentLine.split(":");
					// puts the values into the hashmap, hashmap used to keep
					// keys and values
					saved.put(split[0], split[1]);
					// System.out.println(split[0]);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setComplex(Complex clicked) {
		this.clicked = clicked;
	}

	public Complex getComplex() {
		return clicked;
	}
}

class MandelControls extends JPanel {

	// This panel is the set of controls used to control the Mandelbrot.
	// Separated from the Julia.

	private MandelEditor editor;
	private MandelPanel mandel;
	private JuliaPanel julia;
	private Color currentColour;

	public MandelControls(MandelEditor editor) {
		// initializing
		this.editor = editor;
		mandel = editor.getMandel();
		julia = editor.getJulia();

		this.setLayout(new GridLayout(2, 1));
		JPanel upper = new JPanel(new GridLayout(8, 10));
		JPanel RGBSliders = new JPanel(new GridLayout(4, 1));

		RGBPanel red = new RGBPanel("Red", editor);
		RGBPanel green = new RGBPanel("Green", editor);
		RGBPanel blue = new RGBPanel("Blue", editor);

		RGBSliders.add(red);
		RGBSliders.add(green);
		RGBSliders.add(blue);

		this.add(upper);
		this.add(RGBSliders);

		// create all the textfields and initialize the,
		JTextField widthMaxField = new JTextField(5);
		upper.add(new JLabel("Max Width: "));
		widthMaxField.setText(String.valueOf(mandel.getMaxX()));
		upper.add(widthMaxField);

		JTextField heightMaxField = new JTextField(5);
		heightMaxField.setText(String.valueOf(mandel.getMaxY()));
		upper.add(new JLabel("Max Height: "));
		upper.add(heightMaxField);

		JTextField heightMinField = new JTextField(5);
		heightMinField.setText(String.valueOf(mandel.getMinY()));
		upper.add(new JLabel("Min Height: "));
		upper.add(heightMinField);

		JTextField widthMinField = new JTextField(7);
		widthMinField.setText(String.valueOf(mandel.getMinX()));
		upper.add(new JLabel("Min Width:"));
		upper.add(widthMinField);

		JTextField iterField = new JTextField(5);
		iterField.setText(String.valueOf(mandel.getIterations()));
		upper.add(new JLabel("Iterations"));
		upper.add(iterField);

		JTextField compnumField = new JTextField(20);

		upper.add(new JLabel("Clicked Complex: "));
		compnumField.setText(String.valueOf(editor.getComplex().getReal()) + " + "
				+ String.valueOf(editor.getComplex().getImaginary()) + "i");
		upper.add(compnumField);

		upper.add(new JLabel("Fractal:"));
		String[] theFractals = { "Mandelbrot", "Burning Ship", "Multibrot", "Tricorn" };
		JComboBox fractalChoose = new JComboBox(theFractals);
		fractalChoose.setSelectedIndex(0);
		
		//updates the julia and the mandelbrot set when the new fractal is chosen
		fractalChoose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String chosen = fractalChoose.getSelectedItem().toString();
				mandel.setTheFormula(chosen);
				julia.setTheFormula(chosen);
				mandel.repaint();
				julia.repaint();

			}
		});
		upper.add(fractalChoose);

		JButton render = new JButton("Render");

		// Listener added to Mandelbrot for zooming and changing the JuliaSet
		mandel.addMouseListener(new MouseAdapter() {
			// different functions for right and left mouse button
			@Override
			public void mouseClicked(MouseEvent e) {
				// on left click, change the Julia set
				if (SwingUtilities.isLeftMouseButton(e)) {
					julia.setLiveUpdate(!julia.isLiveUpdate());
				}
				// on right click, zoom out using the stack
				if (SwingUtilities.isRightMouseButton(e)) {
					if (!editor.getZooms().isEmpty()) {
						mandel.setMinY(editor.getZooms().pop());
						mandel.setMaxY(editor.getZooms().pop());
						mandel.setMinX(editor.getZooms().pop());
						mandel.setMaxX(editor.getZooms().pop());
						mandel.repaint();
					}
				}
			}

			// Methods for zooming

			@Override
			public void mousePressed(MouseEvent e) {
				//sets start point
				mandel.setStart(e.getPoint());
			}

			public void mouseReleased(MouseEvent e) {
				if (mandel.getDragged()) {
					// before doing anything, the current boundary values are
					// stored so that
					// the user can zoom out
					editor.getZooms().push(mandel.getMaxX());
					editor.getZooms().push(mandel.getMinX());
					editor.getZooms().push(mandel.getMaxY());
					editor.getZooms().push(mandel.getMinY());

					double newMaxX, newMinX, newMinY, newMaxY;

					/*
					 * The new values for the zoom are calculated based on the
					 * dragging start point and end point. The values must be
					 * stored locally first however. If you change the min X
					 * value after changing the max X value, it will be skewed
					 * since the calculations require eachother Storing them
					 * locally ensures there are no conflicts or
					 * miscalculations.s
					 */
					mandel.setEnd(e.getPoint());
					newMaxX = (mandel.getMaxX() - mandel.getMinX()) * (e.getX() / (double) mandel.getWidth())
							+ mandel.getMinX();
					newMinX = (mandel.getMaxX() - mandel.getMinX()) * (mandel.getStart().x / (double) mandel.getWidth())
							+ mandel.getMinX();
					newMaxY = (mandel.getMaxY() - mandel.getMinY()) * (e.getY() / (double) mandel.getHeight())
							+ mandel.getMinY();

					newMinY = (mandel.getMaxY() - mandel.getMinY())
							* (mandel.getStart().y / (double) mandel.getHeight()) + mandel.getMinY();

					// After all calculations, you set the new boundaries and
					// stop drawing the rectangle.
					mandel.setMaxX(Math.max(newMaxX, newMinX));
					mandel.setMinX(Math.min(newMaxX, newMinX));
					mandel.setMaxY(Math.max(newMaxY, newMinY));
					mandel.setMinY(Math.min(newMaxY, newMinY));
					mandel.setDragged(false);
					mandel.repaint();
				}
			}
		});

		mandel.addMouseMotionListener(new MouseAdapter() {
			// Draws rectangle
			@Override
			public void mouseDragged(MouseEvent e) {
				// Only draws on left click
				if (SwingUtilities.isLeftMouseButton(e)) {
					// Sets the end point of the rectangle constantly
					mandel.setEnd(e.getPoint());
					// The boolean operator for dragging makes sure
					// there are no conflicts and makes sure the rectangle
					// doesn't get stuck on the screen when not drawing.
					mandel.setDragged(true);
					mandel.repaint();
				}
			}

			public void mouseMoved(MouseEvent e) {
				if (julia.isLiveUpdate()) {
					// sets Julia Complex to be mouse click
					editor.setComplex(new Complex(
							(mandel.getMaxX() - mandel.getMinX()) * (e.getX() / (double) mandel.getWidth())
									+ mandel.getMinX(),
							(mandel.getMaxY() - mandel.getMinY()) * (e.getY() / (double) mandel.getHeight())
									+ mandel.getMinY()));
					compnumField.setText(String.valueOf(editor.getComplex().getReal()) + " + "
							+ String.valueOf(editor.getComplex().getImaginary()) + "i");
					julia.setComplex(editor.getComplex());
					julia.repaint();
				}
			}

		});

		// the render button just takes the stuff the user has entered, and
		// applies it to
		// the mandelbrot.
		render.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mandel.setMaxY(Double.parseDouble(heightMaxField.getText()));
				mandel.setMaxX(Double.parseDouble(widthMaxField.getText()));
				mandel.setIterations(Integer.parseInt(iterField.getText()));
				mandel.setMinY(Double.parseDouble(heightMinField.getText()));
				mandel.setMinX(Double.parseDouble(widthMinField.getText()));
				mandel.repaint();

			};
		});
		RGBSliders.add(render);
	}
}

class JuliaControls extends JPanel {

	/*
	 * The Julia controls handle saving and loading the users favourite Julia
	 * sets JList used.
	 */

	private MandelEditor editor;

	public JuliaControls(MandelEditor editor) {

		this.editor = editor;

		this.setLayout(new BorderLayout());

		// First, the JList gets the currently saved Julias
		JList list = new JList(editor.getSaved().keySet().toArray());

		// Makes sure only one thing can be selected at a time
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);

		// Puts it all in a scroll pane, for if the user has too many saved
		// Julias
		JScrollPane scroller = new JScrollPane(list);
		scroller.setPreferredSize(new Dimension(450, 130));
		this.add(scroller, BorderLayout.CENTER);
		
		JPanel saveLoad = new JPanel();
		saveLoad.setPreferredSize(new Dimension(500, 130));
		JLabel saveLabel = new JLabel("Enter name of Julia set to save:");
		JTextField saveField = new JTextField(20);

		saveLoad.add(saveLabel);
		saveLoad.add(saveField);

		JButton load = new JButton("Load Image");
		JButton save = new JButton("Save Image");
		saveLoad.add(load);
		saveLoad.add(save);
		this.add(saveLoad, BorderLayout.SOUTH);

		load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// gets the saved complexes, and loads them into the julia. The
				// complex is split by +
				String[] complexString = editor.getSaved().get(list.getSelectedValue()).split("\\+");
				editor.setComplex(
						new Complex(Double.parseDouble(complexString[0]), Double.parseDouble(complexString[1])));
				editor.getJulia().setComplex(editor.getComplex());
				editor.getJulia().repaint();

			}

		});

		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// Writes the name from the textfield first, then the colon,
					// the complex with the plus
					// This is appended
					BufferedWriter writer = new BufferedWriter(new FileWriter(editor.getSavedComplexes(), true));
					writer.write(saveField.getText() + ":" + editor.getJulia().getComplex().getReal() + "+"
							+ editor.getJulia().getComplex().getImaginary());

					System.out.println(saveField.getText() + ":" + editor.getJulia().getComplex().getReal() + "+"
							+ editor.getJulia().getComplex().getImaginary());

					editor.getSaved().put(saveField.getText(), editor.getJulia().getComplex().getReal() + "+"
							+ editor.getJulia().getComplex().getImaginary());

					writer.newLine();
					writer.flush();
					writer.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				/*
				 * Since I could not find a way to refresh the JList normally,
				 * it simply removes and readds itself to ensure the list stays
				 * up to date
				 */
				editor.readSaved();
				editor.getTabs().removeTabAt(1);
				editor.getTabs().add("Julia", new JuliaControls(editor));
				editor.getTabs().setSelectedIndex(1);

			}
		});

	}
}

class RGBPanel extends JPanel {

	private String colour;
	private int currentValue;
	private MandelEditor editor;

	public RGBPanel(String colour, MandelEditor editor) {

		this.colour = colour;
		this.editor = editor;

		JLabel rlabel = new JLabel(colour);
		this.add(rlabel);
		currentValue = 50;
		//creates a slider that goes from 0 to 255, and starts at 0
		JSlider red = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
		JTextField redText = new JTextField(4);
		red.addChangeListener(new ChangeListener() {
			//slider will update the textfield
			@Override
			public void stateChanged(ChangeEvent e) {
				redText.setText("" + red.getValue());
				currentValue = Integer.parseInt(redText.getText());
				updateMandelBrot();
			}

		});
		red.setMajorTickSpacing(50);
		red.setPaintTicks(true);
		// red.setPaintLabels(true);
		this.add(red);

		redText.setText("" + red.getValue());
		redText.addKeyListener(new KeyAdapter() {
			
			//the textfield will update the slider. the if statements make sure the program doesn't
			//bug out when the user is changing the textfield or accidentally puts the wrong number in
			
			public void keyReleased(KeyEvent ke) {
				String typed = redText.getText();
				red.setValue(0);
				if (!typed.matches("\\d+") || typed.length() > 3) {
					return;
				}
				int value = Integer.parseInt(typed);
				red.setValue(value);
				currentValue = value;
				updateMandelBrot();
			}
		});
		this.add(redText);
	}

	public int getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}

	public void updateMandelBrot() {
		
		//quite hardcoded, but changed the mandelbrots corresponding RGB value based on the label given to the slider
		
		switch (this.colour) {
		case "Red":
			editor.getMandel().setRed(currentValue);
			break;
		case "Green":
			editor.getMandel().setGreen(currentValue);
			break;
		case "Blue":
			editor.getMandel().setBlue(currentValue);
			break;

		}

		editor.getMandel().repaint();
	}

}
