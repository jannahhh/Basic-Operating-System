import java.util.ArrayList;
import java.util.HashMap;

public class Test {
    static HashMap<Integer, ArrayList<Object>> memoryPrograms = new HashMap<>();
    static Object [] memory = new Object[40];
    public static void main(String[] args) {
        ArrayList<Object> tet = new ArrayList<>();
        tet.add("Start");
        memoryPrograms.put(1, tet);
        memory[0] = memoryPrograms.get(1);
        memoryPrograms.get(1).set(0, "End");
        System.out.println(((ArrayList<Object>) memory[0]).toString());
        Pair t = new Pair(1,2);
        String c = t.toString();
        System.out.print(c);
        String qq = "wetfetf hhsv hdvxeh \n";
        String r = "dhjkdcj";
        System.out.print(qq);
        System.out.println(r);
    }
}
