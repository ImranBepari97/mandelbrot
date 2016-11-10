

public class Complex {
	private double real;
	private double imaginary;
	
	public static void main(String[] args) {
		System.out.println("Hello");
	}
	
	public Complex(double real, double imaginary) {
		this.real = real;
		this.imaginary = imaginary;
	}
	
	
	public Complex square() {
		double tempreal = (this.getReal() * this.getReal()) - (this.getImaginary() * this.getImaginary());
		double tempimag = 2.0 * this.getReal() * this.getImaginary();
		
		real = tempreal;
		imaginary = tempimag;
		
		return this;
	}
	
	public Complex powerOfMinusTwo() {
		double tempreal = (this.getReal() * this.getReal()) - (this.getImaginary() * this.getImaginary());
		double tempimag = -2.0 * this.getReal() * this.getImaginary();
		
		real = tempreal;
		imaginary = tempimag;
		
		return this;
	}

	public double getReal() {
		return real;
	}

	public double getImaginary() {
		return imaginary;
	}

	public Complex multiply(Complex w) {
		
		double tempReal = real*w.getReal() - imaginary*w.getImaginary();
		imaginary = real*w.getImaginary() + imaginary*w.getReal();
		real = tempReal;
		
		return this;
	}
	
	public Complex add(Complex w) {
		//add them just like anything else
		real = real + w.getReal(); 
		imaginary = imaginary + w.getImaginary();
		return this;
	}
	
	public Complex minus(Complex w) {
		real = real - w.getReal();
		imaginary = imaginary - w.getImaginary();
		return this;
	}
	
	public double modulusSquared() {
		return (real*real) + (imaginary*imaginary);
	}
	
	
}
