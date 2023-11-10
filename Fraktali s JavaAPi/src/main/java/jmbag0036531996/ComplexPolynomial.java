package jmbag0036531996;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComplexPolynomial {
    private List<Complex> factors;
// constructor
/**
 * Creates a new ComplexPolynomial with the given factors.
 * @param factors
 */
public ComplexPolynomial(Complex ...factors) {
    this.factors = Arrays.asList(factors);
}
// returns order of this polynom; eg. For (7+2i)z^3+2z^2+5z+1 returns 3
public short order() {
    return (short) (factors.size()-1);
}
// computes a new polynomial this*p
public ComplexPolynomial multiply(ComplexPolynomial p) {
    List<Complex> results = new ArrayList<>();

    for (int i = 0; i < order() + p.order()+1; i++)  results.add(Complex.ZERO);

    for(int i = 0; i < this.order() + 1; i++) {
        for(int j = 0; j < p.order() + 1; j++) {
            results.set(i+j, results.get(i+j).add(this.factors.get(i).multiply(p.factors.get(j))));
        }
    }
    return new ComplexPolynomial(results.toArray(new Complex[results.size()]));
}
// computes first derivative of this polynomial; for example, for
// (7+2i)z^3+2z^2+5z+1 returns (21+6i)z^2+4z+5
public ComplexPolynomial derive() {
    List<Complex> results = new ArrayList<>();
    for(int i = 1; i < order() + 1; i++) results.add(factors.get(i).multiply(new Complex(i,0)));
    
    return new ComplexPolynomial(results.toArray(new Complex[results.size()]));
}
// computes polynomial value at given point z
public Complex apply(Complex z) {
    Complex results = factors.get(0);

    for(int i = 1; i < order() + 1; i++) results = results.add(factors.get(i).multiply(z.power(i)));

    return results;
}
@Override
public String toString() {
    String s ="";
    int n = order() + 1;
    for(int i = 0; i < n; i++) {
        if(i == 0) s+="("+factors.get(i)+")";
        else if(i == 1) s+= "+(" + factors.get(i) + ")*z";
        else s+= "+(" + factors.get(i) + ")*z^" + i;
    }
    return s;
}
}