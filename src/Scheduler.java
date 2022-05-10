import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Scheduler {

    int time = -1;
    int counter;

    ArrayList<Pair> programs = new ArrayList<Pair>();

    static int currentProgram = 0;

    public Scheduler() {

    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void scheduler(Interpreter interpreter, int id) throws Exception {
        programs = interpreter.programs;
        while (true) {
            for (int pid : interpreter.readyQueue) {
                System.out.print("The Ready Programs are: "+ pid);
            }
            time++;
            if (time == 0) {
                interpreter.readyQueue.add((int)programs.get(0).x);
            } else if (time == 1) {
                interpreter.readyQueue.add((int)programs.get(1).x);

            } else if (time == 4) {
                interpreter.readyQueue.add((int)programs.get(2).x);
            }


            boolean finished = false;
            if (currentProgram == 0){
                if(interpreter.readyQueue.isEmpty())
                    break;
                currentProgram = interpreter.readyQueue.poll();
                boolean acquired = interpreter.readLine(interpreter.instructionQueue.get(currentProgram).element(), currentProgram);
                counter++;
                if(acquired){
                    interpreter.instructionQueue.get(currentProgram).remove();
                }
            } else if ( interpreter.instructionQueue.get(currentProgram).isEmpty() ) {
                counter = 0;
                if (!interpreter.readyQueue.isEmpty()) {
                    currentProgram = interpreter.readyQueue.poll();
                    boolean acquired = interpreter.readLine(interpreter.instructionQueue.get(currentProgram).element(),currentProgram);
                    counter++;
                    if(acquired){
                        interpreter.instructionQueue.get(currentProgram).remove();
                    }
                }
            }


            if (counter == 2) {
                interpreter.isRunning = false;
                if (!finished) {
                    interpreter.readyQueue.add(currentProgram);
                }
                currentProgram = 0;


            }
            if (!interpreter.isRunning && !interpreter.readyQueue.isEmpty()) {
                counter = 0;
                currentProgram = interpreter.readyQueue.peek();
                boolean acquired = interpreter.readLine(interpreter.instructionQueue.get(currentProgram).element(), id);
                if(acquired){
                    interpreter.instructionQueue.get(currentProgram).poll();
                }

            }
            if (!interpreter.isRunning && interpreter.readyQueue.isEmpty() && interpreter.blocked.isEmpty()) {
                break;
            }
            if (interpreter.isRunning) {
                boolean acquired = interpreter.readLine(interpreter.instructionQueue.get(currentProgram).element(), id);
                if(acquired){
                    interpreter.instructionQueue.get(currentProgram).poll();
                }
            }


        }
    }
}
