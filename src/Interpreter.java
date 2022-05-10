import java.io.*;
import java.util.*;



public class Interpreter {




    userInput userInput= new userInput();
    userOutput userOutput= new userOutput();
     file file= new file();

     Queue<Integer> readyQueue = new LinkedList<Integer>();
     Queue<Integer> blocked = new LinkedList<Integer>();
     Queue<Integer> inputBlocked = new LinkedList<Integer>();
     Queue<Integer> outputBlocked = new LinkedList<Integer>();
     Queue<Integer> fileBlocked = new LinkedList<Integer>();


     ArrayList<Pair> programs = new ArrayList<Pair>();



     HashMap<Integer,ArrayList<Pair>> memory = new HashMap<Integer,ArrayList<Pair>>();

     HashMap<Integer,Queue<String>> instructionQueue = new HashMap<Integer, Queue<String>>();

    boolean isRunning;


    public void print(Object x){
        String s = (String) x;
        System.out.println(s);
    }
    public void assign(String x, int pid){
            Scanner myObj = new Scanner(System.in);
            String input;
            System.out.println("Enter value:");
            input = myObj.nextLine();

            for (int i = 0; i < memory.get(pid).size(); i++) {
                Pair temp = memory.get(pid).get(i);
                String var = (String) temp.x;
                if (var.equals(x)){
                    memory.get(pid).set(i, new Pair((String) x, (Object) input));
                }
            }

    }
    public void assign(String x, String value, int pid){

        for (int i = 0; i < memory.get(pid).size(); i++) {
            Pair temp = memory.get(pid).get(i);
            if (temp.x.equals(x)){
                memory.get(pid).set(i, new Pair((String) x, value));
            }
        }
        }
    public String readFile(String x) {
        try {
            File myObj = new File(x);
            Scanner myReader = new Scanner(myObj);
            String content = "";
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
                content =  content + data + "\n" ;
            }
            myReader.close();
            return content;

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return "An error occurred";
        }

    }

    public void writeFile (Object x, Object y){
        String fileName = "src/"+ (String) x + ".txt";
        String data = (String) y;

            try {
                File myObj = new File(fileName);
                if (myObj.createNewFile()) {
                    System.out.println("File created: " + myObj.getName());
                    FileWriter myWriter = new FileWriter(fileName);
                    myWriter.write(data);
                    myWriter.close();
                    System.out.println("Successfully wrote to the file.");
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

    }

    public void printFromTo(int x, int y){
        if(x == y){
            System.out.println("The two numbers are equal!");
            return;
        }
        int min;
        int max;
        if(x<y){
            min = x;
            max = y-1;
        }else{
            min = y;
            max = x-1;
        }
        System.out.println("Our range is:");
        while(min != max){

            System.out.println(++min);
        }

    }
    public boolean semWait(resource x, int pid){

        if(x.getAvailable()){
            return true;
        }
        else{
            blocked.add(pid);
            if(x.equals(userInput)){
                inputBlocked.add(pid);
            } else if (x.equals(userOutput)) {
                outputBlocked.add(pid);
            }
            else {
                fileBlocked.add(pid);
            }
            return  false;
        }
    }
    public void semSignal(resource x, int pid){

        x.setAvailable();
    }
    public boolean readLine(String line, int pid) throws Exception {
        isRunning = true;
        String[] Line = line.split(" ");
        String function = Line[0];
        String argument1;
        String argument2;

        System.out.println(function);
        switch (function){
            case "print":
                argument1 ="";

                for (int i = 0; i < memory.get(pid).size() ; i++) {

                    Pair temp = memory.get(pid).get(i);
                    if(temp.x.equals(Line[1])){
                        argument1 = (String) temp.y;
                    }
                }
                print(argument1);
                break;

            case "readFile":
                argument1 = "";
                for (int i = 0; i < memory.get(pid).size() ; i++) {
                    Pair temp = memory.get(pid).get(i);
                    if(temp.x.equals(Line[1])){
                        argument1 = (String) temp.y;
                    }
                }
                readFile(argument1);
                break;
            case "assign":
                if(Line.length == 3){
                    memory.get(pid).add(new Pair(Line[1], ""));
                    assign(Line[1],pid);
                }
                else {
                    memory.get(pid).add(new Pair(Line[1], ""));
                    String var = Line[3];
                    String output = readFile(var);
                    assign(Line[1],output, pid);
                }


                break;
            case "writeFile":
                argument1 = "";
                argument2 = "";
                for (int i = 0; i < memory.get(pid).size() ; i++) {
                    Pair temp = memory.get(pid).get(i);
                    if(temp.x.equals(Line[1])){
                        argument1 = (String) temp.y;
                    }
                    if(temp.x.equals(Line[2])){
                        argument2 = (String) temp.y;
                    }
                }
                writeFile(argument1, argument2);
                break;
            case "printFromTo":
                int arg1 = 0;
                int arg2 = 0;

                for (int i = 0; i < memory.get(pid).size() ; i++) {
                    Pair temp = memory.get(pid).get(i);
                    if(temp.x.equals(Line[1])){
                        arg1 = Integer.parseInt((String) temp.y);
                    }
                    if(temp.x.equals(Line[2])){
                        arg2 = Integer.parseInt((String) temp.y);
                    }
                }


                printFromTo(arg1,arg2);
                break;
            case "semWait":
                argument1 = Line[1];
                switch(argument1) {

                    case "userInput":
                        return semWait(userInput, pid);
                    case "userOutput":
                        return semWait(userOutput, pid);
                    case "file":
                        return semWait(file, pid);
                }
                break;
            case "semSignal":
                argument1 = Line[1];
                switch(argument1) {

                    case "userInput":
                        semSignal(userInput, pid);
                        break;
                    case "userOutput":
                        semSignal(userOutput, pid);
                        break;
                    case "file":
                        semSignal(file, pid);
                        break;
                }
                break;
            default: return true;
        }
        return true;
    }
    public void execute(int pid){
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
                instructionQueue.get(pid).add(data);
//                readLine(data,pid);
                content =  content + data + "\n" ;
            }

            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

        Interpreter i = new Interpreter();
        i.programs.add(new Pair(1,"src/Program_1.txt"));
        i.programs.add(new Pair(2,"src/Program_2.txt"));
        i.programs.add(new Pair(3,"src/Program_3.txt"));
        i.programs.add(new Pair(4,"src/tempProgram.txt"));
        i.memory.put(1,new ArrayList<Pair>());
        i.memory.put(2,new ArrayList<Pair>());
        i.memory.put(3,new ArrayList<Pair>());
        i.memory.put(4,new ArrayList<Pair>());
        for (Pair p:i.programs
        ) {
            i.instructionQueue.put((Integer) p.x, new LinkedList<String>());
            i.execute((Integer) p.x);
        }
//        System.out.println(i.programs.size() + " , "+ i.memory.size());
//        System.out.println(i.instructionQueue.size());



    }
}
