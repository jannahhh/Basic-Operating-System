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
    public void run ( Interpreter interpreter) throws Exception {

        int state = interpreter.readLine(interpreter.instructionQueue.get(currentProgram).element(), currentProgram);
        counter++;
        int unblocked = 0;
        int oldID = currentProgram;
        if (state != 1) {

            interpreter.instructionQueue.get(currentProgram).remove();
            if (state == 2) {
                if (!interpreter.inputBlocked.isEmpty()) {
                    unblocked = interpreter.inputBlocked.poll();
                    System.out.println("this is the unblocked: "+unblocked);
                    interpreter.readyQueue.add(unblocked);
                    if(! interpreter.instructionQueue.get(oldID).isEmpty()){
                        interpreter.readyQueue.add(oldID);
                    }
                    currentProgram = interpreter.readyQueue.poll();
                    counter = 0;
                }
                for (int pid : interpreter.blocked) {
                    if (pid == unblocked) {
                        interpreter.blocked.remove((int) pid);
                    }
                }
            } else if (state == 3) {
                if (!interpreter.outputBlocked.isEmpty()) {
                    unblocked = interpreter.outputBlocked.poll();
                    interpreter.readyQueue.add(unblocked);
                    if(! interpreter.instructionQueue.get(oldID).isEmpty()){
                        interpreter.readyQueue.add(oldID);
                    }
                    currentProgram = interpreter.readyQueue.poll();
                    counter = 0;
                }
                for (int pid : interpreter.blocked) {
                    if (pid == unblocked) {
                        interpreter.blocked.remove((int) pid);
                    }
                }
            } else if (state == 4) {
                if (!interpreter.fileBlocked.isEmpty()) {
                    unblocked = interpreter.fileBlocked.poll();
                    interpreter.readyQueue.add(unblocked);
                    if(! interpreter.instructionQueue.get(oldID).isEmpty()){
                        interpreter.readyQueue.add(oldID);
                    }
                    currentProgram = interpreter.readyQueue.poll();
                    counter = 0;
                }
                for (int pid : interpreter.blocked) {
                    if (pid == unblocked) {
                        interpreter.blocked.remove((int) pid);
                    }
                }
            }
            }  else {
            if (!interpreter.readyQueue.isEmpty()) {
                System.out.println("HEYY IAM N");
                currentProgram = interpreter.readyQueue.poll();
                counter = 0;
            }
        }
    }

    public void scheduler(Interpreter interpreter) throws Exception {
        programs = interpreter.programs;
        while (true) {
//            for (int pid : interpreter.readyQueue) {
//                System.out.print("The Ready Programs are: "+ pid);
//            }

            time++;
            if(time >= 25){
                break;
            }
            System.out.println("Current program: "+ currentProgram + " Time: "+ time);
            if (time == 0) {
                System.out.println("p1 entered");
                interpreter.readyQueue.add((int)programs.get(0).x);
            } else if (time == 1) {
                System.out.println("p2 entered");
                interpreter.readyQueue.add((int)programs.get(1).x);

            }
            else if (time == 4) {
                System.out.println("p3 entered");
                interpreter.readyQueue.add((int)programs.get(2).x);
            }


            boolean finished = false;
            if (currentProgram == 0) {
                System.out.println("1");
                if (interpreter.readyQueue.isEmpty()) {
                    System.out.println("2");
                    if (interpreter.blocked.isEmpty()) {
                        System.out.println("3");
                        break;
                    } else {
                        currentProgram = interpreter.blocked.poll();
                        counter = 0;
                        run(interpreter);
                    }

                } else {
                    currentProgram = interpreter.readyQueue.poll();
                    counter = 0;
                    run(interpreter);

                }
                continue;
            }


            if(currentProgram!=0){
                if (counter ==2 ){
                    if (!interpreter.instructionQueue.get(currentProgram).isEmpty()){
                        interpreter.readyQueue.add(currentProgram);
                    }
                    if (interpreter.readyQueue.isEmpty()){
                        if (interpreter.blocked.isEmpty()){
                            break;
                        }
                        else {
                            currentProgram = interpreter.blocked.poll();
                            counter = 0;
                            run(interpreter);
                        }
                    }else {
                        currentProgram=interpreter.readyQueue.poll();
                        counter = 0;
                        run(interpreter);
                    }
                }
                else {
                    if (interpreter.instructionQueue.get(currentProgram).isEmpty()){
                        if (!interpreter.readyQueue.isEmpty()){
                            currentProgram=interpreter.readyQueue.poll();
                            run(interpreter);
                        }else {
                            if (!interpreter.blocked.isEmpty()){
                               currentProgram=interpreter.blocked.poll();
                                run(interpreter);
                            } else {
                                break;
                            }
                        }

                    }else {
                        run(interpreter);
                    }
                }
            }

            }

        }
    }


