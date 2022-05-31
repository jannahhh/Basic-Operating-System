import java.io.*;
import java.util.*;


public class Interpreter {

//  Critical Resources
    userInput userInput = new userInput();
    userOutput userOutput = new userOutput();
    file file = new file();
//-------------------------

//    Ready and Blocked Queues
    Queue<Integer> readyQueue = new LinkedList<>();
    Queue<Integer> blocked = new LinkedList<>();
    Queue<Integer> inputBlocked = new LinkedList<>();
    Queue<Integer> outputBlocked = new LinkedList<>();
    Queue<Integer> fileBlocked = new LinkedList<>();
//  -----------------------------------------------------
    int start = 0;
    HashMap<Integer,Pair> Boundaries = new HashMap<>();
    HashMap<Integer, ArrayList<Object>> memoryPrograms = new HashMap<>();
    Object [] memory = new Object[40];
    HashMap<Integer, Queue<String>> instructionQueue = new HashMap<>();
    ArrayList<Pair> temporaryInputs = new ArrayList<>();
    ArrayList<Pair> programs;
    HashMap<Integer, List <String>> programQueue = new HashMap<>();
    int lastIn = 0;
    int c = 1;

    public Interpreter(ArrayList<Pair> programs){
        this.programs = programs;
        for (Pair p: programs) {
            instructionQueue.put((Integer) p.x, new LinkedList<>());
            execute((Integer) p.x);
            memoryPrograms.put((Integer) p.x, new ArrayList<>());
//            addToMemory((Integer) p.x);
//            writeInDisk((Integer) p.x);
        }
    }

    public void print(Object x) {
        String s = (String) x;
        System.out.println(s);
    }


    public void assign(String x, String value, int pid) {
        for (Object o : memoryPrograms.get(pid)) {
            if(o instanceof Pair p){
                if (p.x.equals(x))
                    p.y = value;
            }
        }
    }


    public String readFile(String x) {
        try {
            x = "src/" + x + ".txt";
            File myObj = new File(x);
            Scanner myReader = new Scanner(myObj);
            String content = "";
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                content = content + data + "\n";
            }
            myReader.close();
            return content;

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return "An error occurred";
        }

    }

    public void writeFile(Object x, Object y) {
        String fileName = "src/" + (String) x + ".txt";
        String data = (String) y;

        try {
            File myObj = new File(fileName);
            if (myObj.createNewFile()) {
                FileWriter myWriter = new FileWriter(fileName);
                myWriter.write(data);
                myWriter.close();
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
    public void writeInDisk(int pid) {
        String fileName = "src/hardDisk.txt";
        lastIn=pid;
        try {
            String previous = readFile("hardDisk");
            FileWriter myWriter = new FileWriter(fileName);
            myWriter.write(previous);
            myWriter.write(c);
            c++;
            for (Object o :memoryPrograms.get(pid)) {
                if (o instanceof List<?>){
                    for (String s:(List<String>) o) {
                        myWriter.write(s);
                        myWriter.write(System.lineSeparator());
                    }
                }else{
                    myWriter.write(o.toString());
                    myWriter.write(System.lineSeparator());
                }
            }
            myWriter.write(System.lineSeparator());
            myWriter.write("----------------------------------------------------------------------------------------");
            myWriter.write(System.lineSeparator());
            myWriter.close();
            System.out.println("PID No."+ pid +" entered hardDisk.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    public void printFromTo(int x, int y) {
        if (x == y) {
            System.out.println("The two numbers are equal!");
            return;
        }
        int min;
        int max;
        if (x < y) {
            min = x;
            max = y - 1;
        } else {
            min = y;
            max = x - 1;
        }
        System.out.println("Our range is:");
        while (min != max) {

            System.out.println(++min);
        }

    }

    public boolean semWait(resource x, int pid) {

        if (x.getAvailable()) {
            x.setAvailable();
            return true;
        } else {
            memoryPrograms.get(pid).set(1, "Blocked");
            blocked.add(pid);
            if (x.equals(userInput)) {
                inputBlocked.add(pid);
            } else if (x.equals(userOutput)) {
                outputBlocked.add(pid);
            } else {
                fileBlocked.add(pid);
            }
            return false;
        }
    }

    public void semSignal(resource x) {
        x.setAvailable();
    }
    public void changeState(){
        for (int pid : readyQueue) {
            memoryPrograms.get(pid).set(1, "Ready");
        }
    }
    public void finishState(int pid){
        memoryPrograms.get(pid).set(1, "Finished");
    }
    public void runState(int pid){
        memoryPrograms.get(pid).set(1, "Running");
    }
    public int readLine(String line, int pid) throws Exception {

        memoryPrograms.get(pid).set(1, "Running");
        memoryPrograms.get(pid).set(2, ((int) memoryPrograms.get(pid).get(2))+1);
        String[] Line = line.split(" ");
        String function = Line[0];
        String argument1;
        String argument2;


        switch (function) {
            case "input":
                Scanner myObj = new Scanner(System.in);
                String input ;
                System.out.println("Enter value:");
                input = myObj.nextLine();
                for (Pair p : temporaryInputs) {
                    if (pid == (int)p.x){
                        p.y = input;
                    }
                }
                break;

            case "print":
                argument1 = "";
                for (Object o : memoryPrograms.get(pid)) {
                    if(o instanceof Pair p){
                        if (p.x.equals(Line[1]))
                            argument1 = (String) p.y;
                    }
                }
                print(argument1);
                break;

            case "readFile":
                String tmp = "";
                String output;
                for (Object o: memoryPrograms.get(pid)) {
                    if (o instanceof Pair p){
                        if (p.x.equals(Line[1]))
                            tmp = (String) p.y;
                    }
                }
                output = readFile(tmp);
                for (Pair p : temporaryInputs) {
                    if (pid == (int)p.x){
                        p.y = output;
                    }
                }
                break;

            case "assign":
                memoryPrograms.get(pid).add(new Pair(Line[1], ""));
                for (Pair p : temporaryInputs) {
                    if (pid == (int)p.x){
                        assign(Line[1], (String) p.y, pid);
                    }
                }
                break;

            case "writeFile":
                argument1 = "";
                argument2 = "";
                for (Object o : memoryPrograms.get(pid)) {
                    if (o instanceof Pair p){
                        if (p.x.equals(Line[1])) {
                            argument1 = (String) p.y;
                        }
                        if (p.x.equals(Line[2])) {
                            argument2 = (String) p.y;
                        }
                    }
                }
                writeFile(argument1, argument2);
                break;

            case "printFromTo":
                int arg1 = 0;
                int arg2 = 0;

                for (Object o : memoryPrograms.get(pid)) {
                    if (o instanceof Pair p){
                        if (p.x.equals(Line[1])) {
                            arg1 = Integer.parseInt((String) p.y);
                        }
                        if (p.x.equals(Line[2])) {
                            arg2 = Integer.parseInt((String) p.y);
                        }
                    }
                }
                printFromTo(arg1, arg2);
                break;

            case "semWait":
                argument1 = Line[1];
                switch (argument1) {

                    case "userInput":
                        if (!semWait(userInput, pid)) {
                            memoryPrograms.get(pid).set(2, ((int) memoryPrograms.get(pid).get(2))-1);
                            return 1;
                        }
                        break;
                    case "userOutput":

                        if (!semWait(userOutput, pid)) {
                            memoryPrograms.get(pid).set(2, ((int) memoryPrograms.get(pid).get(2))-1);
                            return 1;
                        }
                        break;
                    case "file":
                        if (!semWait(file, pid)) {
                            memoryPrograms.get(pid).set(2, ((int) memoryPrograms.get(pid).get(2))-1);
                            return 1;
                        }
                        break;
                }
                break;

            case "semSignal":
                argument1 = Line[1];
                switch (argument1) {
                    case "userInput" -> {
                        semSignal(userInput);
                        return 2;
                    }
                    case "userOutput" -> {
                        semSignal(userOutput);
                        return 3;
                    }
                    case "file" -> {
                        semSignal(file);
                        return 4;
                    }
                }
                break;

            default:
                return 0;
        }
        return 0;
    }
    public void addToMemory(int pid){
        int noOfInstructions = instructionQueue.get(pid).size();
        String state = "Created";
        int pc = noOfInstructions - programQueue.get(pid).size();
        int sizeInMemory = 7 + noOfInstructions;
        int end = start + sizeInMemory -1;
        Pair boundaries = new Pair(start, end );
        start += sizeInMemory;
        memoryPrograms.get(pid).add(pid);
        memoryPrograms.get(pid).add(state);
        memoryPrograms.get(pid).add(pc);
        memoryPrograms.get(pid).add(boundaries);
        memoryPrograms.get(pid).add(programQueue.get(pid));
        if (memory[0] == null){
            memory[0] = memoryPrograms.get(pid);
        }else if(memory[20] == null){
            memory[20] = memoryPrograms.get(pid);
        }else {
            if (!((String)((ArrayList<Object>)memory[0]).get(1)).equals("Running")){
                // Write to Hard-Disk
                writeInDisk((int) ((ArrayList<Object>)memory[0]).get(0));
                end = sizeInMemory -1;
                ((Pair) memoryPrograms.get(pid).get(3)).x = 0;
                ((Pair) memoryPrograms.get(pid).get(3)).y = end;

                memory[0] = memoryPrograms.get(pid);
            }
            else {
                // Write to Hard-Disk
                writeInDisk((int) ((ArrayList<Object>)memory[20]).get(0));
                start = ((int) ((Pair) ((ArrayList<Object>)memory[20]).get(3)).x);
                end = start + sizeInMemory -1;
                ((Pair) memoryPrograms.get(pid).get(3)).x = start;
                ((Pair) memoryPrograms.get(pid).get(3)).y = end;
                memory[20] = memoryPrograms.get(pid);
            }
        }
        Boundaries.put(pid, new Pair(start,end));
    }
    public void swap(){
        System.out.println(((String)((ArrayList<Object>)memory[0]).get(1)));
        if (!((String)((ArrayList<Object>)memory[0]).get(1)).equals("Running")){
            // Write to Hard-Disk
            int temp = lastIn;
            writeInDisk((int) ((ArrayList<Object>)memory[0]).get(0));
            ((Pair) memoryPrograms.get(lastIn).get(3)).x = 0;
            ((Pair) memoryPrograms.get(lastIn).get(3)).x = 7 + programQueue.get(lastIn).size() -1;
            memory[0] = memoryPrograms.get(temp);
        }
        else {
            // Write to Hard-Disk
            int temp = lastIn;
            writeInDisk((int) ((ArrayList<Object>)memory[20]).get(0));
            ((Pair) memoryPrograms.get(lastIn).get(3)).x = 20;
            ((Pair) memoryPrograms.get(lastIn).get(3)).y = 7 + programQueue.get(lastIn).size() -1 + 20;
            memory[20] = memoryPrograms.get(temp);
        }
    }
    public void displayMemory(){
        ArrayList<Object> tempoMemo = new ArrayList<>();
        int c = 0;
        int b1 = 0;
        int b2 = 0;
        if(memory[0] != null){
            for (Object o : ((ArrayList<Object>)memory[0])) {
                if (o instanceof ArrayList<?>){
                    for (String s:(List<String>) o) {
                        tempoMemo.add(s);
                        c++;
                    }
                } else {
                    if (o instanceof Pair p){
                        if (p.x instanceof Integer){
                            b1 = (int) p.y;
                        }
                    }
                    tempoMemo.add(o);
                    c++;
                }
            }
            while(c <= b1){
                tempoMemo.add("");
                c++;
            }
            if (memory[20] == null){
                while(c < 40){
                    tempoMemo.add("");
                    c++;
                }
            }
        }
        if(memory[20] != null){
            for (Object o : ((ArrayList<Object>)memory[20])) {
                if (o instanceof ArrayList<?>){
                    for (String s:(List<String>) o) {
                        tempoMemo.add(s);
                        c++;
                    }
                } else {
                    if (o instanceof Pair p){
                        if (p.x instanceof Integer){
                            b2 = (int) p.y;
                        }
                    }
                    tempoMemo.add(o);
                    c++;
                }
            }
            while(c < 40){
                tempoMemo.add("");
                c++;
            }
        }
        System.out.println(tempoMemo.toString());
    }

    public void execute(int pid) {
        try {

            temporaryInputs.add(new Pair(pid, ""));

            String pathname = "";
            for (Pair temp : programs) {
                if ((Integer) temp.x == pid) {
                    pathname = (String) temp.y;
                }
            }


            File myObj = new File(pathname);
            Scanner myReader = new Scanner(myObj);
            programQueue.put(pid,new ArrayList<>());
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.contains("assign")){
                    String[] Line = data.split(" ");
                    if (data.contains("readFile")){
                        instructionQueue.get(pid).add("readFile "+Line[3]);
                        programQueue.get(pid).add("readFile "+Line[3]);

                    }else{
                        instructionQueue.get(pid).add("input");
                        programQueue.get(pid).add("input");
                    }
                    instructionQueue.get(pid).add(Line[0] +" "+ Line[1]);
                    programQueue.get(pid).add(Line[0] +" "+ Line[1]);
                }else {
                    instructionQueue.get(pid).add(data);
                    programQueue.get(pid).add(data);
                }
            }


            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
