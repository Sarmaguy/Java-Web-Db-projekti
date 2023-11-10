package jmbag0036531996;

import java.util.ArrayList;
import java.util.List;

public class Complex {
	
public static final Complex ZERO = new Complex(0,0);
public static final Complex ONE = new Complex(1,0);
public static final Complex ONE_NEG = new Complex(-1,0);
public static final Complex IM = new Complex(0,1);
public static final Complex IM_NEG = new Complex(0,-1);
private double real;
private double imag;


public Complex() {
	this(0,0);
}
public Complex(double re, double im) {
	real = re;
	imag = im;
}

// returns module of complex number
public double module() {
	return Math.sqrt(imag*imag + real*real);
}
// returns this*c
public Complex multiply(Complex c) {
	
	double r = real*c.real - imag*c.imag;
	double i = real*c.imag + imag*c.real;
	
	return new Complex(r,i);
	
}
// returns this/c
public Complex divide(Complex c) {
	if (c==null || (c.real==0 && c.imag==0)) throw new IllegalArgumentException("Dividing  with 0");
	
	double m = c.real*c.real + c.imag*c.imag;

	double re = (this.real*c.real + this.imag*c.imag)/m;
	double im = (this.imag*c.real - this.real*c.imag)/m;
	
	return new Complex(re, im);
}
// returns this+c
public Complex add(Complex c) {
	return new Complex(real+c.real, imag+c.imag);
}
// returns this-c
public Complex sub(Complex c) {
	return new Complex(real-c.real, imag-c.imag);
}
// returns -this
public Complex negate() {
    return multiply(ONE_NEG);
}
// returns this^n, n is non-negative integer
public Complex power(int n) {
    Complex result = this;
    for (int i = 0; i < n-1; i++) {
        result = result.multiply(this);
    }
    return result;
}
// returns n-th root of this, n is positive integer
public List<Complex> root(int n) {
	List<Complex> results = new ArrayList<>();

	double theta = Math.atan2(imag, real);
	if (theta<0) theta += 2*Math.PI;

	double r = Math.pow(module(), 1d/n);
	
	for (int i = 0; i < n; i++) {
		results.add(new Complex(r*Math.cos((theta+2*i*Math.PI)/n), r*Math.sin((theta+2*i*Math.PI)/n)));
	}
	return results;
}
@Override
public String toString() {
    return real + " + " + imag + "i";
}
}
