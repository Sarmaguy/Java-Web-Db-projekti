package jmbag0036531996;

import java.util.Arrays;
import java.util.List;

public class ComplexRootedPolynomial {
    private Complex constant;
    private List<Complex> roots;
// constructor
public ComplexRootedPolynomial(Complex constant, Complex ... roots) {
    this.constant = constant;
    this.roots = Arrays.asList(roots);
}
// computes polynomial value at given point z
public Complex apply(Complex z) {
    Complex result = constant;
    for (Complex root : roots) {
        result = result.multiply(z.sub(root));
    }
    return result;
}
// converts this representation to ComplexPolynomial type
public ComplexPolynomial toComplexPolynom() {
	ComplexPolynomial product = new ComplexPolynomial(this.constant);

    for (Complex root : this.roots) 
        product = product.multiply(new ComplexPolynomial(root.negate(), Complex.ONE));

    return  product;

}
@Override
public String toString() {
    String s="";
    s+=constant;
    for (Complex root : roots) {
        s+="*(z-("+root+"))";
    }
    return s;
}

// finds index of closest root for given complex number z that is within
// treshold; if there is no such root, returns -1
// first root has index 0, second index 1, etc
public int indexOfClosestRootFor(Complex z, double treshold) {
    int index = -1;
    double minDistance = Double.MAX_VALUE;
    for (int i = 0; i < roots.size(); i++) {
        double distance = z.sub(roots.get(i)).module();
        if (distance < minDistance && distance < treshold) {
            minDistance = distance;
            index = i;
        }
    }
    return index;
}
}