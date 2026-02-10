package Concurrency.ReadWriteSimulator;

public class ReadResult {
    String data;
    long version;

    ReadResult(String d,long v)
    {
        data=d;
        version=v;
    }
}