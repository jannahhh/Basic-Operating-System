import java.util.*;

public class userMode {
    ArrayList<Pair> programs;
    Scheduler scheduler;

    public userMode(){
        programs = new ArrayList<>();
        scheduler = new Scheduler();
    }

    public static void main(String[] args) throws Exception {

        userMode userMode = new userMode();
        userMode.programs.add(new Pair(1, "src/Program_1.txt"));
        userMode.programs.add(new Pair(2, "src/Program_2.txt"));
        userMode.programs.add(new Pair(3, "src/Program_3.txt"));
        Interpreter i = new Interpreter(userMode.programs);

        userMode.scheduler.scheduler(i);




    }
}
