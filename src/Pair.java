public class Pair {

    Object x;
    Object y;

    public Pair(Object x, Object y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "( "+x +", " + y +" )";
    }
}
