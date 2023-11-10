package jmbag0036531996;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;

public class Newton {
	public static void main(String[] args) {
		System.out.println("Welcome to Newton-Raphson iteration-based fractal viewer.");
		System.out.println("Please enter at least two roots, one root per line. Enter 'done' when done.");
		
		List<Complex> roots = new ArrayList<>();
		Scanner s = new Scanner(System.in);
		
		while(true) {
			System.out.print("Root " + (roots.size()+1) + "> ");
			String nextLine = s.nextLine().trim();
			
			if(nextLine.equals("done"))
				break;
			try {
				roots.add(parseToComplex(nextLine));
			} catch (IllegalArgumentException e) {
				System.out.println("Neispravan imaginarni broj");
			}
			

			
		}
		
		s.close();
		System.out.println("Image od fractal will appear shortly. Thank you.");
		
		FractalViewer.show(new MojProducer(roots));
		
	}
	

	public static Complex parseToComplex(String s) {
		s = s.replaceAll("\\s+", "");
		
		if(s.matches("^[+-]?[0-9]+[+-][0-9]*i$")) {
			boolean flag = false;
			boolean imgflag=false;
			
			if (s.startsWith("-")) {
				s=s.substring(1);
				flag=true;
			}
			
			if (s.startsWith("+")) s=s.substring(1);
			
			if (s.contains("-")) imgflag=true;
			
			String[] parts = s.split("[+-]");
			
			if (imgflag) parts[1]="-"+parts[1];
			
			String imag = parts[1].replace("i", "");
			
			if (parts[1].equals("i")) imag = "1";
			
			if (flag) parts[0]="-"+parts[0];
			
			

			return new Complex(Double.parseDouble(parts[0]), Double.parseDouble(imag));
		} 
		
		else if(s.matches("^[+-]?[0-9]*i$")) {

			String imag = s.replace("i", "");
			
			if (s.equals("i")) imag = "1";
			if (s.equals("-i")) imag = "-1";
			
			return new Complex(0, Double.parseDouble(imag));
			
		} else if(s.matches("^[+-]?[0-9]*$")) {
			
			return new Complex(Double.parseDouble(s), 0);
			
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public static class MojProducer implements IFractalProducer {

		private ComplexRootedPolynomial rooted;

		public MojProducer(List<Complex> roots) {
			rooted = new ComplexRootedPolynomial(Complex.ONE, roots.toArray(new Complex[0]));
			
		}

		@Override
		public void produce(double reMin, double reMax, double imMin, double imMax,
				int width, int height, long requestNo, IFractalResultObserver observer, AtomicBoolean cancel) {
			System.out.println("Zapocinjem izracun...");
			int m = 16*16*16;
			int offset = 0;
			short[] data = new short[width * height];
			double convTreshold = 1E-3;
			double rootTreshold = 2E-3;
			ComplexPolynomial polynomial = rooted.toComplexPolynom();
			ComplexPolynomial derived = polynomial.derive();
			for(int y = 0; y < height; y++) {
				if(cancel.get()) break;
				for(int x = 0; x < width; x++) {
					double cre = x / (width-1.0) * (reMax - reMin) + reMin;
					double cim = (height-1.0-y) / (height-1) * (imMax - imMin) + imMin;
					Complex zn = new Complex(cre, cim);

					double module = 0;
					int iters = 0;
					do {
						Complex numerator = polynomial.apply(zn);
						Complex denominator = derived.apply(zn);
						Complex fraction = numerator.divide(denominator);
						Complex znold = zn;
						Complex znSub = zn.sub(fraction);
						zn = znSub;
						module = znold.sub(zn).module();

						iters++;
					} while(iters < m && module > convTreshold);
					int index = rooted.indexOfClosestRootFor(zn, rootTreshold);
					data[offset++] = (short) (index + 1);
				}
			}
			System.out.println("Racunanje gotovo. Idem obavijestiti promatraca tj. GUI!");
			observer.acceptResult(data, (short) (polynomial.order() + 1), requestNo);
		}

		@Override
		public void setup() {

		}

		@Override
		public void close() {

		}
	}
}


