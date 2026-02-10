package Concurrency.MatrixMultiplication;

public class BlockParallelMatrixMultiply {

    static class BlockWorker extends Thread {
        int[][] A, B, C;
        int rowStart, rowEnd;
        int colStart, colEnd;

        public BlockWorker(int[][] A, int[][] B, int[][] C,
                           int rowStart, int rowEnd,
                           int colStart, int colEnd) {
            this.A = A;
            this.B = B;
            this.C = C;
            this.rowStart = rowStart;
            this.rowEnd = rowEnd;
            this.colStart = colStart;
            this.colEnd = colEnd;
        }

        @Override
        public void run() {
            int common = A[0].length;

            for (int i = rowStart; i < rowEnd; i++) {
                for (int j = colStart; j < colEnd; j++) {
                    int sum = 0;
                    for (int k = 0; k < common; k++) {
                        sum += A[i][k] * B[k][j];
                    }
                    C[i][j] = sum;
                }
            }
        }
    }

    public static int[][] multiply(int[][] A, int[][] B, int blockSize)
            throws InterruptedException {

        int m = A.length;
        int n = A[0].length;
        int p = B[0].length;

        if (B.length != n) {
            throw new IllegalArgumentException("Invalid matrix sizes");
        }

        int[][] C = new int[m][p];

        int numRowBlocks = (m + blockSize - 1) / blockSize;
        int numColBlocks = (p + blockSize - 1) / blockSize;

        Thread[] threads = new Thread[numRowBlocks * numColBlocks];
        int t = 0;

        for (int rb = 0; rb < numRowBlocks; rb++) {
            for (int cb = 0; cb < numColBlocks; cb++) {

                int rowStart = rb * blockSize;
                int rowEnd = Math.min(rowStart + blockSize, m);

                int colStart = cb * blockSize;
                int colEnd = Math.min(colStart + blockSize, p);

                threads[t] = new BlockWorker(
                        A, B, C,
                        rowStart, rowEnd,
                        colStart, colEnd
                );

                threads[t].start();
                t++;
            }
        }

        // wait for all blocks to finish
        for (Thread th : threads) {
            th.join();
        }

        return C;
    }

    public static void print(int[][] M) {
        for (int[] r : M) {
            for (int v : r) System.out.print(v + " ");
            System.out.println();
        }
    }

    public static void main(String[] args) throws Exception {

        int[][] A = {
                {1,2,3,4},
                {5,6,7,8},
                {9,1,2,3},
                {4,5,6,7}
        };

        int[][] B = {
                {1,0,2,1},
                {0,1,2,0},
                {1,1,0,2},
                {2,0,1,1}
        };

        int[][] C = multiply(A, B, 2); // block size = 2
        print(C);
    }
}