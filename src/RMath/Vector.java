package RMath;

public class Vector {

    private double x;
    private double y;
    private double r;
    private double theta;

    public Vector(Point p){
        x = p.x;
        y = p.y;
        r = Util.dist(p.x, p.y);
        theta = Util.angle(p.x, p.y);
    }

    public Vector(PolarPoint p){
        r = p.r;
        theta = p.theta;
        x = r * Math.cos(theta);
        y = r * Math.sin(theta);
    }

    public Vector clone(){
        return new Vector(new Point(x, y));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getMagnitude() {
        return r;
    }

    public double getTheta() {
        return theta;
    }

    public Vector setX(double x) {
        this.x = x;
        this.r = Util.dist(x, y);
        this.theta = Util.angle(x, y);

        return this;
    }

    public Vector setY(double y) {
        this.y = y;
        this.r = Util.dist(x, y);
        this.theta = Util.angle(x, y);

        return this;
    }

    public Vector setMagnitude(double r) {
        this.r = r;
        this.x = r * Math.cos(theta);
        this.y = r * Math.sin(theta);

        return this;
    }

    public Vector setTheta(double theta) {
        this.theta = theta;
        this.x = r * Math.cos(theta);
        this.y = r * Math.sin(theta);

        return this;
    }

    public Vector add(Vector other){
        setX(x + other.x);
        setY(y + other.y);

        return this;
    }

    public Vector subtract(Vector other){
        setX(x - other.x);
        setY(y - other.y);

        return this;
    }

    public Vector scale(double scale){
        setX(x * scale);
        setY(y * scale);

        return this;
    }

    public Vector normalize(){
        return scale(1 / getMagnitude());
    }

    public Point toPoint(){
        return new Point(x, y);
    }

    public PolarPoint toPolarPoint(){
        return new PolarPoint(r, theta);
    }

    public Segment toSegment(){
        return new Segment(new Point(0,0), toPoint());
    }

    public Segment toSegment(Point start){
        return new Segment(start, toPoint());
    }

    public static Vector rect(double x, double y){
        return new Vector(new Point(x, y));
    }

    public static Vector polar(double r, double theta){
        return new Vector(new PolarPoint(r, theta));
    }
}
