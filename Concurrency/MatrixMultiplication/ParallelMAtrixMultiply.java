package Concurrency.MatrixMultiplication;

public class ParallelMAtrixMultiply {

    public static int[][] multiply(int [][]A, int [][]B) throws InterruptedException {
        int m = A.length;
        int n = A[0].length;
        int p = B[0].length;

        if (B.length != n) {
            throw new IllegalArgumentException("Invalid matrix sizes");
        }
        int [][] C = new int[n][p];

        Thread[] threads = new Thread[n];

        for (int i=0;i<m;i++) {
            threads[i] = new RowWorker(A,B,C,i);
            threads[i].start();
        }

        for (int i=0;i<m;i++) {
            threads[i].join();
        }

        for(int i=0;i<m;i++) {
            for(int j=0;j<p;j++) {
                System.out.print(C[i][j] + " ");
            }
            System.out.println();
        }

        return C;
    }
}

/*
Q - Why no synchronization needed?
A - Each thread writes to a different row in result matrix C.
No two threads write to same memory location.
So no synchronization required.



 */