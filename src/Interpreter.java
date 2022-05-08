import java.io.*;  // Import the File class
import java.util.*;
import java.nio.file.*;


public class Interpreter {


    static Object a;
    static Object b;

    static userInput userInput= new userInput();
    static userOutput userOutput= new userOutput();
    static file file= new file();

    static Queue<Integer> readyQueue = new LinkedList<Integer>();
    static Queue<Integer> blocked = new LinkedList<Integer>();
    static Queue<Integer> inputBlocked = new LinkedList<Integer>();
    static Queue<Integer> outputBlocked = new LinkedList<Integer>();
    static Queue<Integer> fileBlocked = new LinkedList<Integer>();


    static ArrayList<Pair> programs = new ArrayList<Pair>();

    static boolean semWaitExist = false;


    static HashMap<Integer,ArrayList<Pair>> memory = new HashMap<Integer,ArrayList<Pair>>();




    public static void print(int x){
        System.out.println(x);
    }
    public static void print(Object x){
        String s = (String) x;
        System.out.println(s);
    }
//    public static void assign(Object x, int y){
//        x = y;
//    }
    public static void assign(Object x, int pid, boolean waitFlag){
        if(waitFlag){
            Scanner myObj = new Scanner(System.in);
            String input;
            System.out.println("Enter value:");
            input = myObj.nextLine();
            for (int i = 0; i < memory.get(pid).size(); i++) {
                Pair temp = memory.get(pid).get(i);
                if (temp.x.equals((String) x)){
                    memory.get(pid).set(i, new Pair((String) x, input));
                }
            }

            semWaitExist = false;
        }
        else {
            if(semWait(userInput, pid)){
                Scanner myObj = new Scanner(System.in);
                String input;
                System.out.println("Enter value:");
                input = myObj.nextLine();
                for (int i = 0; i < memory.get(pid).size(); i++) {
                    Pair temp = memory.get(pid).get(i);
                    if (temp.x.equals((String) x)){
                        memory.get(pid).set(i, new Pair((String) x, input));
                    }
                }
                semSignal(userInput, pid);

            }
        }

    }
    public static void assign(Object x, String value, int pid){

        for (int i = 0; i < memory.get(pid).size(); i++) {
            Pair temp = memory.get(pid).get(i);
            if (temp.x.equals((String) x)){
                memory.get(pid).set(i, new Pair((String) x, value));
            }
        }
        }
    public static String readFile(String x) {
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
    public static void writeFile(Object x, Object y) {
        String s1 = (String) x;
        String s2 = (String) y;
        try {
            FileWriter myWriter = new FileWriter(s1);
            myWriter.write(s2);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public static void printFromTo(Object x, Object y){
        int n1 = (int) x;
        int n2 = (int) y;
        if(n1 == n2){
            System.out.println("The two numbers are equal!");
            return;
        }
        int min;
        int max;
        if(n1<n2){
            min = n1;
            max = n2-1;
        }else{
            min = n2;
            max = n1-1;
        }
        System.out.println("Our range is:");
        while(min != max){

            System.out.println(++min);
        }

    }
    public static boolean semWait(resource x, int pid){

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
    public static void semSignal(resource x, int pid){
        x.setAvailable();
    }
    public static void readLine(String line, int pid) throws Exception {
        String[] Line = line.split(" ");
        String function = Line[0];
        String argument1;
        String argument2;



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

                    if(Line[1].equals("a")){
                        memory.get(pid).add(new Pair("a", ""));
                        assign(a,pid,semWaitExist);
                    }else {
                        assign(b,pid,semWaitExist);
                    }
                }
                else {
                    if(Line[1].equals("a")){
                        String var = Line[3];
                        String output = readFile(var);
                        assign(a,output, pid);
                    }
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
                        arg1 = (Integer) temp.y;
                    }
                    if(temp.x.equals(Line[2])){
                        arg2 = (Integer) temp.y;
                    }
                }

                printFromTo(arg1,arg2);
                break;
            case "semWait":
                argument1 = Line[1];
                switch(argument1) {

                    case "userInput":
                        semWait(userInput, pid);
                        semWaitExist = true;
                        break;
                    case "userOutput":
                        semWait(userOutput, pid);
                        break;
                    case "file":
                        semWait(file, pid);
                        break;
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

        }
    }
    public static void execute(int pid){
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
                content =  content + data + "\n" ;
            }

            readLine(content, pid);
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static void kernel(){

        programs.add(new Pair(1,"F:\\SEMESTER 6\\CSEN602 Operating Systems2\\OS_22_Project\\Program_1.txt"));
        programs.add(new Pair(2,"F:\\SEMESTER 6\\CSEN602 Operating Systems2\\OS_22_Project\\Program_2.txt"));
        programs.add(new Pair(3,"F:\\SEMESTER 6\\CSEN602 Operating Systems2\\OS_22_Project\\Program_3.txt"));
        memory.put(1,new ArrayList<Pair>());
        memory.put(2,new ArrayList<Pair>());
        memory.put(3,new ArrayList<Pair>());
        for (Pair program : programs) {
            readyQueue.add((Integer)program.x);
        }

    }
    public static void main(String[] args) {
//        programs.put(1,"F:\\SEMESTER 6\\CSEN602 Operating Systems2\\OS_22_Project\\Program_1.txt");
//        programs.put(2,"Program_2.txt");
//        programs.put(3,"Program_3.txt");
//        printFromTo(0,2);
        memory.put(1,new ArrayList<Pair>());
        memory.put(2,new ArrayList<Pair>());
        memory.put(3,new ArrayList<Pair>());
        execute(1);
    }
}
