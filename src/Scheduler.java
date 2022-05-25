import java.util.ArrayList;
import java.util.HashMap;


public class Scheduler {

    int time = -1;
    int counter;
    int timeSlice;

    ArrayList<Pair> programs = new ArrayList<>();

    static int currentProgram = 0;
    HashMap<Integer,Integer> arrivalTime;

    public Scheduler(int timeSlice, HashMap<Integer,Integer> arrivalTime) {
        this.timeSlice = timeSlice;
        this.arrivalTime = arrivalTime;
    }

    public void run(Interpreter interpreter) throws Exception {
        interpreter.runState(currentProgram);
        System.out.println("------ @Time: " + time + ", process No." + currentProgram + "    " + interpreter.instructionQueue.get(currentProgram).element() + " ------");
        System.out.println("       Ready Queue: "+ interpreter.readyQueue+"    Blocked Queue: "+interpreter.blocked);
        interpreter.displayMemory();
        System.out.println();
        int state = interpreter.readLine(interpreter.instructionQueue.get(currentProgram).element(), currentProgram);
        interpreter.changeState();
        counter++;
        int unblocked = 0;

        if (state != 1) {

            interpreter.instructionQueue.get(currentProgram).remove();
            if (state == 2) {
                if (!interpreter.inputBlocked.isEmpty()) {
                    unblocked = interpreter.inputBlocked.poll();
                    interpreter.readyQueue.add(unblocked);
                    interpreter.changeState();
                }
                for (int pid : interpreter.blocked) {
                    if (pid == unblocked) {
                        interpreter.blocked.remove(pid);
                    }
                }
            } else if (state == 3) {
                if (!interpreter.outputBlocked.isEmpty()) {
                    unblocked = interpreter.outputBlocked.poll();
                    interpreter.readyQueue.add(unblocked);
                    interpreter.changeState();
                }
                for (int pid : interpreter.blocked) {
                    if (pid == unblocked) {
                        interpreter.blocked.remove(pid);
                    }
                }
            } else if (state == 4) {
                if (!interpreter.fileBlocked.isEmpty()) {
                    unblocked = interpreter.fileBlocked.poll();
                    interpreter.readyQueue.add(unblocked);
                    interpreter.changeState();
                }
                for (int pid : interpreter.blocked) {
                    if (pid == unblocked) {
                        interpreter.blocked.remove(pid);
                    }
                }
            }
        } else {
            //the current program got blocked, so we get another one from the readyQueue
            if (!interpreter.readyQueue.isEmpty()) {
                currentProgram = interpreter.readyQueue.poll();
                interpreter.changeState();
                counter = 0;
            }
        }

    }

    public void scheduler(Interpreter interpreter) throws Exception {
        programs = interpreter.programs;
        while (true) {

            time++;

            if (time == arrivalTime.get(1)) {
                interpreter.readyQueue.add((int) programs.get(0).x);
                interpreter.addToMemory((int) programs.get(0).x);
                interpreter.changeState();
            } else if (time == arrivalTime.get(2)) {
                interpreter.readyQueue.add((int) programs.get(1).x);
                interpreter.addToMemory((int) programs.get(1).x);
                interpreter.changeState();
            }
//            else if (time == arrivalTime.get(3)) {
//                interpreter.readyQueue.add((int) programs.get(2).x);
//                interpreter.addToMemory((int) programs.get(2).x);
//                interpreter.changeState();
//            }


            if (currentProgram == 0) {
                if (interpreter.readyQueue.isEmpty()) {
                    if (interpreter.blocked.isEmpty()) {
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


            if (currentProgram != 0) {
                if (counter == timeSlice) {
                    if (!interpreter.instructionQueue.get(currentProgram).isEmpty()) {
                        interpreter.readyQueue.add(currentProgram);
                        interpreter.changeState();
                    }else {
                        System.out.println("Program No."+currentProgram+" has finished");
                        interpreter.finishState(currentProgram);
                        System.out.println();
                    }
                    if (interpreter.readyQueue.isEmpty()) {
                        if (interpreter.blocked.isEmpty()) {
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
                } else {
                    if (interpreter.instructionQueue.get(currentProgram).isEmpty()) {
                        interpreter.finishState(currentProgram);
                        System.out.println("Program No."+currentProgram+" has finished");
                        System.out.println();
                        if (!interpreter.readyQueue.isEmpty()) {
                            currentProgram = interpreter.readyQueue.poll();
                            counter = 0;
                            run(interpreter);
                        } else {
                            if (!interpreter.blocked.isEmpty()) {
                                currentProgram = interpreter.blocked.poll();
                                counter = 0;
                                run(interpreter);
                            } else {
                                break;
                            }
                        }

                    } else {
                        run(interpreter);
                    }
                }
            }

        }

    }
}


