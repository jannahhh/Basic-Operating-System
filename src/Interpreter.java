import java.io.*;
import java.util.*;


public class Interpreter {


    userInput userInput = new userInput();
    userOutput userOutput = new userOutput();
    file file = new file();

    Queue<Integer> readyQueue = new LinkedList<Integer>();
    Queue<Integer> blocked = new LinkedList<Integer>();
    Queue<Integer> inputBlocked = new LinkedList<Integer>();
    Queue<Integer> outputBlocked = new LinkedList<Integer>();
    Queue<Integer> fileBlocked = new LinkedList<Integer>();


    ArrayList<Pair> programs = new ArrayList<Pair>();


    HashMap<Integer, ArrayList<Pair>> memory = new HashMap<Integer, ArrayList<Pair>>();

    HashMap<Integer, Queue<String>> instructionQueue = new HashMap<Integer, Queue<String>>();
    Scheduler scheduler = new Scheduler();
    ArrayList<Pair> temporaryInputs = new ArrayList<Pair>();


    public void print(Object x) {
        String s = (String) x;
        System.out.println(s);
    }

    public void assign(String x, int pid) {
        Scanner myObj = new Scanner(System.in);
        String input;
        System.out.println("Enter value:");
        input = myObj.nextLine();

        for (int i = 0; i < memory.get(pid).size(); i++) {
            Pair temp = memory.get(pid).get(i);
            String var = (String) temp.x;
            if (var.equals(x)) {
                memory.get(pid).set(i, new Pair((String) x, (Object) input));
            }
        }

    }

    public void assign(String x, String value, int pid) {

        for (int i = 0; i < memory.get(pid).size(); i++) {
            Pair temp = memory.get(pid).get(i);
            if (temp.x.equals(x)) {
                memory.get(pid).set(i, new Pair((String) x, value));
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

    public void semSignal(resource x, int pid) {

        x.setAvailable();
    }

    public int readLine(String line, int pid) throws Exception {
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

                for (int i = 0; i < memory.get(pid).size(); i++) {
                    Pair temp = memory.get(pid).get(i);
                    if (temp.x.equals(Line[1])) {
                        argument1 = (String) temp.y;
                    }
                }
                print(argument1);
                break;

            case "readFile":
                String tmp = "";
                String output;
                for (int i = 0; i < memory.get(pid).size(); i++) {
                    Pair temp = memory.get(pid).get(i);
                    if (temp.x.equals(Line[1])) {
                        tmp = (String) temp.y;
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
//                if (Line.length == 3) {
//                    memory.get(pid).add(new Pair(Line[1], ""));
//                    assign(Line[1], pid);
//                } else {
//                    memory.get(pid).add(new Pair(Line[1], ""));
//                    String var = Line[3];
//                    String fileName = "";
//                    for (int i = 0; i < memory.get(pid).size(); i++) {
//                        Pair temp = memory.get(pid).get(i);
//                        if (temp.x.equals(var)) {
//                            fileName = (String) temp.y;
//                        }
//                    }
//                    String output = readFile(fileName);
//                    assign(Line[1], output, pid);
//                }
                memory.get(pid).add(new Pair(Line[1], ""));
                for (Pair p : temporaryInputs) {
                    if (pid == (int)p.x){
                        assign(Line[1], (String) p.y, pid);
                    }
                }

                break;
            case "writeFile":
                argument1 = "";
                argument2 = "";
                for (int i = 0; i < memory.get(pid).size(); i++) {
                    Pair temp = memory.get(pid).get(i);
                    if (temp.x.equals(Line[1])) {
                        argument1 = (String) temp.y;
                    }
                    if (temp.x.equals(Line[2])) {
                        argument2 = (String) temp.y;
                    }
                }
                writeFile(argument1, argument2);
                break;
            case "printFromTo":
                int arg1 = 0;
                int arg2 = 0;

                for (int i = 0; i < memory.get(pid).size(); i++) {
                    Pair temp = memory.get(pid).get(i);
                    if (temp.x.equals(Line[1])) {
                        arg1 = Integer.parseInt((String) temp.y);
                    }
                    if (temp.x.equals(Line[2])) {
                        arg2 = Integer.parseInt((String) temp.y);
                    }
                }


                printFromTo(arg1, arg2);
                break;
            case "semWait":
                argument1 = Line[1];
                switch (argument1) {

                    case "userInput":
                        if (!semWait(userInput, pid)) {
                            return 1;
                        }
                        ;
                        break;
                    case "userOutput":

                        if (!semWait(userOutput, pid)) {
                            return 1;
                        }
                        ;
                        break;
                    case "file":
                        if (!semWait(file, pid)) {
                            return 1;
                        }
                        ;
                        break;
                }
                break;
            case "semSignal":
                argument1 = Line[1];
                switch (argument1) {

                    case "userInput":
                        semSignal(userInput, pid);
                        return 2;
                    case "userOutput":
                        semSignal(userOutput, pid);
                        return 3;
                    case "file":
                        semSignal(file, pid);
                        return 4;
                }
                break;
            default:
                return 0;
        }
        return 0;
    }

    public void execute(int pid) {
        try {

            String pathname = "";
            for (Pair temp : programs) {
                if ((Integer) temp.x == pid) {
                    pathname = (String) temp.y;
                }
            }


            File myObj = new File(pathname);
            Scanner myReader = new Scanner(myObj);
            String content = "";
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.contains("assign")){
                    if (data.contains("readFile")){
                        String[] Line = data.split(" ");
                        instructionQueue.get(pid).add("readFile "+Line[3]);
                        instructionQueue.get(pid).add(Line[0] +" "+ Line[1]);

                    }else{
                    String[] Line = data.split(" ");
                    instructionQueue.get(pid).add("input");
                    instructionQueue.get(pid).add(Line[0] +" "+ Line[1]);
                    }
                }else {
                    instructionQueue.get(pid).add(data);
                }
//                readLine(data,pid);
                content = content + data + "\n";
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {

        Interpreter i = new Interpreter();
        i.programs.add(new Pair(1, "src/Program_1.txt"));
        i.programs.add(new Pair(2, "src/Program_2.txt"));
        i.programs.add(new Pair(3, "src/Program_3.txt"));



        for (Pair p: i.programs) {
            i.memory.put((int)p.x, new ArrayList<Pair>());
            i.temporaryInputs.add(new Pair((int)p.x, ""));
            i.instructionQueue.put((Integer) p.x, new LinkedList<String>());
            i.execute((Integer) p.x);
        }
        i.scheduler.scheduler(i);



    }
}
