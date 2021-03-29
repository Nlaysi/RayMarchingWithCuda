public class Vector {
    public double x, y, z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Vector Summation(Vector a, Vector b){
        return new Vector(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static Vector Multiplication(Vector a, double b){
        return new Vector(a.x * b, a.y * b, a.z * b);
    }

    public static Vector Subtraction(Vector a, Vector b){
        return new Vector(a.x * b.x, a.y * b.y, a.z * b.z);
    }

    public static double Distance(Vector a, Vector b){
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y) + (a.z - b.z) * (a.z - b.z));
    }

    public static double Dot(Vector a, Vector b){
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static Vector Normalize(Vector a) {
        double d = Distance(a, new Vector(0,0,0));
        return new Vector(a.x / d, a.y / d, a.z / d);
    }
}
