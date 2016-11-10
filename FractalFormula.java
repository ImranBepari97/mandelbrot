

public class FractalFormula {
	
	/* This class literally exists to do different fractals. So the same block of code
	 * isnt placed into loads of different methods. It returns the amount of iterations taken
	 * to break the formula. This class works for both the julia set and the other fractals. The Z
	 * complex is simply set to 0 for any of the non Julia fractals 
	 */

	public int calcFractal(Complex c, Complex z, int iterations, String formula) {
		int curIterations = 0;

		switch (formula) {

		case "Mandelbrot":
			
			while (z.square().add(c).modulusSquared() < 4 && curIterations < iterations) {
				curIterations++;
			}
			break;

		case "Burning Ship":
			while (z.modulusSquared() < 4 && curIterations < iterations) {

				Complex temp = new Complex(Math.abs(z.getReal()), Math.abs(z.getImaginary()));

				z = temp.square().add(c);

				curIterations++;
			}

			break;

		case "Multibrot":
			while (z.modulusSquared() < 4 && curIterations < iterations) {
				z = z.square().square().square().add(c);
				curIterations++;
			}
			break;

		case "Tricorn":
			while (z.modulusSquared() < 4 && curIterations < iterations) {
				z = z.powerOfMinusTwo().add(c);
				curIterations++;
			}
			break;
			
		case "Phoenix":
			while (z.modulusSquared() < 4 && curIterations < iterations) {
				Complex prev1 = new Complex(0,0); Complex prev2 = new Complex(0,0);
				z = z.square().add(c);
				curIterations++;
			}
			break;

		}

		return curIterations;
	}

}
