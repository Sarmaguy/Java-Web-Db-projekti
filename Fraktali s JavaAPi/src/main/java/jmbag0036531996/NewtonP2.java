package jmbag0036531996;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;
public class NewtonP2 {

    public static void main(String[] args) {
        int tracks=0;

        if (args.length > 2) {
            throw new IllegalArgumentException("Too many arguments");
        } else if (args.length==0) {
            tracks = 16;
        }
        else {
            if (args[0].startsWith("--mintracks=")){
                tracks = Integer.parseInt(args[1]);

            } else if (args[0].startsWith("-m")) {
                tracks = Integer.parseInt(args[1]);
            }
            else throw new IllegalArgumentException("Unknown argument: " + args[0]);
            }



        System.out.println("Welcome to Newton-Raphson iteration-based fractal viewer.");
        System.out.println("Number of tracks: " + tracks);
        System.out.println("Please enter at least two roots, one root per line. Enter 'done' when done.");

        List<Complex> roots = new ArrayList<>();
        Scanner s = new Scanner(System.in);

        while(true) {
            System.out.print("Root " + (roots.size()+1) + "> ");
            String nextLine = s.nextLine().trim();

            if(nextLine.equals("done"))
                break;
            try {
                roots.add(Newton.parseToComplex(nextLine));
            } catch (IllegalArgumentException e) {
                System.out.println("Neispravan imaginarni broj");
            }



        }
        s.close();
        System.out.println("Image od fractal will appear shortly. Thank you.");

        FractalViewer.show(new MojProducer(roots, tracks));

    }

    public static class MojProducer implements IFractalProducer {

        private ComplexRootedPolynomial rooted;
        private int tracks;
        private ForkJoinPool pool;

        public MojProducer(List<Complex> roots, int tracks) {
            rooted = new ComplexRootedPolynomial(Complex.ONE, roots.toArray(new Complex[0]));
            this.tracks = tracks;
        }

        @Override
        public void produce(double reMin, double reMax, double imMin, double imMax,
                            int width, int height, long requestNo, IFractalResultObserver observer, AtomicBoolean cancel) {
            System.out.println("Zapocinjem izracun...");
            int m = 16 * 16 * 16;
            short[] data = new short[width * height];
            ComplexPolynomial poly = rooted.toComplexPolynom();

            PosaoIzracuna posao = new PosaoIzracuna(reMin, reMax, imMin, imMax, width, height, 0, height-1, m, data, cancel, rooted, tracks);
            pool.invoke(posao);



            System.out.println("Racunanje gotovo. Idem obavijestiti promatraca tj. GUI!");
            observer.acceptResult(data, (short) (poly.order() + 1), requestNo);


        }

        @Override
        public void setup() {
            pool = new ForkJoinPool();
        }

        @Override
        public void close() {
            pool.shutdown();
        }
    }

    public static class PosaoIzracuna extends RecursiveAction {
        double reMin;
        double reMax;
        double imMin;
        double imMax;
        int width;
        int height;
        int yMin;
        int yMax;
        int m;
        short[] data;
        AtomicBoolean cancel;
        ComplexRootedPolynomial rooted;
        int minTracks;


        public PosaoIzracuna(double reMin, double reMax, double imMin,
                             double imMax, int width, int height, int yMin, int yMax,
                             int m, short[] data, AtomicBoolean cancel, ComplexRootedPolynomial rooted, int minTracks) {
            super();
            this.reMin = reMin;
            this.reMax = reMax;
            this.imMin = imMin;
            this.imMax = imMax;
            this.width = width;
            this.height = height;
            this.yMin = yMin;
            this.yMax = yMax;
            this.m = m;
            this.data = data;
            this.cancel = cancel;
            this.rooted = rooted;
            this.minTracks = minTracks;

        }



        @Override
        protected void compute() {
            int h = yMax - yMin + 1;
            if (h <= minTracks) {
                computeHelper();
                return;
            }

            PosaoIzracuna posaoUp = new PosaoIzracuna(reMin, reMax, imMin, imMax, width, height, yMin, yMin + h/2 -1, m, data, cancel, rooted, minTracks);
            PosaoIzracuna posaoDown = new PosaoIzracuna(reMin, reMax, imMin, imMax, width, height, yMin + h / 2, yMax, m, data, cancel, rooted, minTracks);
            invokeAll(posaoUp, posaoDown);
        }

        private void computeHelper(){
            ComplexPolynomial polynomial = rooted.toComplexPolynom();
            ComplexPolynomial derived = polynomial.derive();
            double convTreshold = 1E-3;
            double rootTreshold = 2E-3;
            int offset = width*yMin;

            for(int y = yMin; y <= yMax; y++) {
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
        }
    }

}
