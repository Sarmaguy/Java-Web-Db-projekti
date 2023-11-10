package jmbag0036531996;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import hr.fer.zemris.java.fractals.viewer.FractalViewer;
import hr.fer.zemris.java.fractals.viewer.IFractalProducer;
import hr.fer.zemris.java.fractals.viewer.IFractalResultObserver;



public class NewtonP1 {

    public static void main(String[] args) {
        int workers=0;
        int tracks=0;

        for(int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--workers=")) {
                if(workers!=0)
                    throw new IllegalArgumentException("Duplicate argument: workers");
                workers = Integer.parseInt(args[i].substring(10));
            } else if (args[i].startsWith("--tracks=")) {
                if(tracks!=0)
                    throw new IllegalArgumentException("Duplicate argument: tracks");
                tracks = Integer.parseInt(args[i].substring(9));
            }else if (args[i].startsWith("-t")) {
                if(tracks!=0)
                    throw new IllegalArgumentException("Duplicate argument: tracks");
                tracks = Integer.parseInt(args[++i]);
            } else if (args[i].startsWith("-w")) {
                if(workers!=0)
                    throw new IllegalArgumentException("Duplicate argument: workers");
                workers = Integer.parseInt(args[++i]);
            } else {
                throw new IllegalArgumentException("Unknown argument: " + args[i]);
            }
        }
        if(workers==0) workers=Runtime.getRuntime().availableProcessors();
        if(tracks==0) tracks=4*Runtime.getRuntime().availableProcessors();
        System.out.println("Welcome to Newton-Raphson iteration-based fractal viewer.");
        System.out.println("Number of workers: " + workers);
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

        FractalViewer.show(new MojProducer(roots, workers, tracks));

}

    public static class MojProducer implements IFractalProducer {

        private ComplexRootedPolynomial rooted;
        private int workers;
        private int tracks;
        private ExecutorService pool;

        public MojProducer(List<Complex> roots, int workers, int tracks) {
            rooted = new ComplexRootedPolynomial(Complex.ONE, roots.toArray(new Complex[0]));
            this.workers = workers;
            this.tracks = tracks;
        }

        @Override
        public void produce(double reMin, double reMax, double imMin, double imMax,
                            int width, int height, long requestNo, IFractalResultObserver observer, AtomicBoolean cancel) {
            System.out.println("Zapocinjem izracun...");
            int m = 16 * 16 * 16;
            short[] data = new short[width * height];
            ComplexPolynomial poly = rooted.toComplexPolynom();

            if (tracks > height) tracks = height;
            int brojYPoTraci = height / tracks;

            List<PosaoIzracuna> poslovi = new ArrayList<>();

            for (int i = 0; i < tracks; i++) {
                int yMin = i * brojYPoTraci;
                int yMax = (i + 1) * brojYPoTraci - 1;

                if (i == tracks - 1) yMax = height - 1;

                poslovi.add(new PosaoIzracuna(reMin, reMax, imMin, imMax, width, height, yMin, yMax, m, data, cancel, rooted));
            }

            List<Future<?>> futures = new ArrayList<>();
            for (PosaoIzracuna posao : poslovi) {
                futures.add(pool.submit(posao));
            }

            for (Future<?> future : futures) {
                while (true){
                    try {
                        future.get();
                        break;
                    } catch (Exception ignorable) {
                    }
                }
            }

            System.out.println("Racunanje gotovo. Idem obavijestiti promatraca tj. GUI!");
            observer.acceptResult(data, (short) (poly.order() + 1), requestNo);


        }

        @Override
        public void setup() {
            pool = Executors.newFixedThreadPool(workers);
        }

        @Override
        public void close() {
            pool.shutdown();
        }
    }

    public static class PosaoIzracuna implements Runnable {
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


        public PosaoIzracuna(double reMin, double reMax, double imMin,
                             double imMax, int width, int height, int yMin, int yMax,
                             int m, short[] data, AtomicBoolean cancel, ComplexRootedPolynomial rooted) {
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
        }

        @Override
        public void run() {
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
