import java.util.*;

public class userMode {
    ArrayList<Pair> programs;
    Scheduler scheduler;

    public userMode(int timeSlice, HashMap<Integer,Integer> arrivalTime){
        programs = new ArrayList<>();
        scheduler = new Scheduler(timeSlice, arrivalTime);
    }

    public static void main(String[] args) throws Exception {
        HashMap<Integer,Integer> arrivalTime = new HashMap<Integer, Integer>();
        arrivalTime.put(1,0);
        arrivalTime.put(2,1);
//        arrivalTime.put(3,4);

        int timeSlice = 2;

        userMode userMode = new userMode(timeSlice, arrivalTime);

        userMode.programs.add(new Pair(1, "src/Program_1.txt"));
        userMode.programs.add(new Pair(2, "src/Program_2.txt"));
//        userMode.programs.add(new Pair(3, "src/Program_3.txt"));

        Interpreter i = new Interpreter(userMode.programs);

        userMode.scheduler.scheduler(i);
        i.displayMemory();
    }
}
