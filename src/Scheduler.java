import java.util.ArrayList;


public class Scheduler {

    int time = -1;
    int counter;
    int timeSlice = 2;

    ArrayList<Pair> programs = new ArrayList<>();

    static int currentProgram = 0;

    public Scheduler() {

    }

    public void run(Interpreter interpreter) throws Exception {
        System.out.println("------ @Time: " + time + ", process No." + currentProgram + "    " + interpreter.instructionQueue.get(currentProgram).element() + " ------");
        int state = interpreter.readLine(interpreter.instructionQueue.get(currentProgram).element(), currentProgram);
        counter++;
        int unblocked = 0;

        if (state != 1) {

            interpreter.instructionQueue.get(currentProgram).remove();
            if (state == 2) {
                if (!interpreter.inputBlocked.isEmpty()) {
                    unblocked = interpreter.inputBlocked.poll();
                    interpreter.readyQueue.add(unblocked);
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
                counter = 0;
            }
        }
        System.out.println("       Ready Queue: "+ interpreter.readyQueue+"    Blocked Queue: "+interpreter.blocked);
        System.out.println();
    }

    public void scheduler(Interpreter interpreter) throws Exception {
        programs = interpreter.programs;
        while (true) {

            time++;

            if (time == 0) {
                interpreter.readyQueue.add((int) programs.get(0).x);
            } else if (time == 1) {
                interpreter.readyQueue.add((int) programs.get(1).x);

            } else if (time == 4) {
                interpreter.readyQueue.add((int) programs.get(2).x);
            }


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
                    }else {
                        System.out.println("Program No."+currentProgram+" has finished");
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
                        System.out.println("Program No."+currentProgram+" has finished");
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


