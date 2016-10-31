package com.yangyang.thrift.serialize;

import com.yangyang.thrift.api.Pair;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TIOStreamTransport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chenshunyang on 2016/10/31.
 */
public class SerializeTest {

    private static  String datafile = "1.dat";

    // *) 把对象写入文件
    public static  void writeData() throws IOException, TException {
        Pair pair = new Pair();
        pair.setKey("key1").setValue("value1");

        FileOutputStream fos = new FileOutputStream(new File(datafile));
        pair.write(new TBinaryProtocol(new TIOStreamTransport(fos)));
        fos.close();
    }

    // *) 从文件恢复对象
    public static  void readData() throws TException, IOException {
        FileInputStream fis = new FileInputStream(new File(datafile));

        Pair pair = new Pair();
        pair.read(new TBinaryProtocol(new TIOStreamTransport(fis)));

        System.out.println("key => " + pair.getKey());
        System.out.println("value => " + pair.getValue());

        fis.close();
    }

    public static void main(String[] args) throws Exception{
        //writeData();
        readData();

    }
}
