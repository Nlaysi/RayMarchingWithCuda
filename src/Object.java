import jcuda.Pointer;
import jcuda.driver.CUdeviceptr;
import jcuda.runtime.JCuda;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static jcuda.driver.JCudaDriver.cuMemcpyDtoH;
import static jcuda.driver.JCudaDriver.cuMemcpyHtoD;

public class Object {
    private static ArrayList<Object> container = new ArrayList<Object>();
    private static byte[] bytes;
    private static CUdeviceptr pointer = new CUdeviceptr();;

    private int id;
    private byte type;
    private byte R = -1, G = -1, B = -1;
    private byte atr1 = 0,  atr2 = 0, atr3 = 0, atr4 = 0;
    Vector vector1 = new Vector(0,0,0);
    Vector vector2 = new Vector(0,0,0);
    private double r = 0;

    public Object(byte type, Vector vector1, double r) {
        this.type = type;
        this.vector1 = vector1;
        this.r = r;
        container.add(this);
    }

    public Object(byte type, Vector vector1,  Vector vector2) {
        this.type = type;
        this.vector1 = vector1;
        this.vector1 = vector2;
        this.r = r;
        container.add(this);
    }

    public Object(byte type, Vector vector1,  Vector vector2, double r) {
        this.type = type;
        this.vector1 = vector1;
        this.vector1 = vector2;
        this.r = r;
        container.add(this);
    }

    public Object(byte type, byte r, byte g, byte b, Vector vector1, Vector vector2, double r1) {
        this.type = type;
        R = r;
        G = g;
        B = b;
        this.vector1 = vector1;
        this.vector2 = vector2;
        this.r = r1;
        container.add(this);
    }

    public static String[] getList(){
        String[] strings = new String[container.size()];
        int i = 0;
        for (Object obj: container){
            String figure = "";
            switch (obj.type){
                case 1:
                    figure = "∨Sphere ";
                    break;
                case 2:
                    figure = "∧Sphere ";
                    break;
                case 3:
                    figure = "-Sphere ";
                    break;
                case 4:
                    figure = "∨Box ";
                    break;
                case 5:
                    figure = "∧Box ";
                    break;
                case 6:
                    figure = "-Box ";
                    break;
                case 7:
                    figure = "∨Cylinder ";
                    break;
                case 8:
                    figure = "∧Cylinder ";
                    break;
                case 9:
                    figure = "-Cylinder ";
                    break;
                case 10:
                    figure = "∨Prism ";
                    break;
                case 11:
                    figure = "∧Prism ";
                    break;
                case 12:
                    figure = "-Prism ";
                    break;
            }
            strings[i] = figure +" V1{"+obj.vector1.x+","+obj.vector1.y+","+obj.vector1.z+"}"+
                    " V2{"+obj.vector2.x+","+obj.vector2.y+","+obj.vector2.z+"}"+
                    " R:"+obj.R;
            i++;
        }

        return strings;
    }

    public static CUdeviceptr getPointer(){
        return pointer;
    }

    public static int getSize(){
        return container.size();
    }

    public static void deleteElement(int id){
        if(container.size() != 1) {
            container.remove(container.get(id));
        }
    }

    public static void LoadObjects(){
        bytes = new byte[64 * container.size()];

        byte[] vector1x = new byte[8];
        byte[] vector1y = new byte[8];
        byte[] vector1z = new byte[8];
        byte[] vector2x = new byte[8];
        byte[] vector2y = new byte[8];
        byte[] vector2z = new byte[8];
        byte[] r = new byte[8];

        int n = 0;
        for (Object obj: container) {
            ByteBuffer.wrap(vector1x).putDouble(obj.vector1.x);
            ByteBuffer.wrap(vector1y).putDouble(obj.vector1.y);
            ByteBuffer.wrap(vector1z).putDouble(obj.vector1.z);
            ByteBuffer.wrap(vector2x).putDouble(obj.vector2.x);
            ByteBuffer.wrap(vector2y).putDouble(obj.vector2.y);
            ByteBuffer.wrap(vector2z).putDouble(obj.vector2.z);
            ByteBuffer.wrap(r).putDouble(obj.r);

            bytes[n++] = obj.type;
            bytes[n++] = obj.R;
            bytes[n++] = obj.G;
            bytes[n++] = obj.B;
            bytes[n++] = obj.atr1;
            bytes[n++] = obj.atr2;
            bytes[n++] = obj.atr3;
            bytes[n++] = obj.atr4;

            for (int i = 7; i >= 0; i--)
                bytes[n++] = vector1x[i];
            for (int i = 7; i >= 0; i--)
                bytes[n++] = vector1y[i];
            for (int i = 7; i >= 0; i--)
                bytes[n++] = vector1z[i];
            for (int i = 7; i >= 0; i--)
                bytes[n++] = vector2x[i];
            for (int i = 7; i >= 0; i--)
                bytes[n++] = vector2y[i];
            for (int i = 7; i >= 0; i--)
                bytes[n++] = vector2z[i];
            for (int i = 7; i >= 0; i--)
                bytes[n++] = r[i];
        }

        JCuda.cudaMalloc(pointer, bytes.length);
        cuMemcpyHtoD(pointer, Pointer.to(bytes), bytes.length);

        byte[] test = new byte[bytes.length];
        cuMemcpyDtoH(Pointer.to(test), pointer, bytes.length);
    }

    public static void print(){
        for (int i = 0; i < bytes.length; i++) {
            System.out.print((bytes[i] & 0xFF) + " ");
        }
    }
}
