package Concurrency.MatrixMultiplication;

public class Runner {

    public static void main(String args[]) throws InterruptedException {

        int [][] A = {
                {1,2,3},
                {4,5,6}
        };

        int [][] B = {
                {7,8},
                {9,10},
                {11,12}
        };

        int [][] C = ParallelMAtrixMultiply.multiply(A,B);

    }
}
