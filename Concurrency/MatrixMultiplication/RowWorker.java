package Concurrency.MatrixMultiplication;

public class RowWorker extends Thread {

    private int [][] A;
    private int [][] B;
    private int [][] C;

    private int row;

    public RowWorker(int [][]A, int [][]B, int [][]C, int row) {
        this.A = A;
        this.B = B;
        this.C = C;
        this.row = row;
    }

    @Override
    public void run() {
        int cols = B[0].length;
        int common = A[0].length;

        for (int j=0;j<cols;j++) {
            int sum=0;
            for (int k=0;k<common;k++) {
                sum += A[row][k]*B[k][j];
            }
            C[row][j] = sum;
        }
    }

}
