import java.io.*;  // Import the File class
import java.util.*;
import java.nio.file.*;


public class Interpreter {


    static Object a;
    static Object b;

    static userInput userInput= new userInput();
    static userOutput userOutput= new userOutput();
    static file file= new file();

    static Queue<Integer> blocked = new LinkedList<Integer>();
    static Queue<Integer> inputBlocked = new LinkedList<Integer>();
    static Queue<Integer> outputBlocked = new LinkedList<Integer>();
    static Queue<Integer> fileBlocked = new LinkedList<Integer>();

    static HashMap<Integer, String> programs = new HashMap<Integer, String>();

    static boolean semExist = true;





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
    public static void assign(Object x, int pid, boolean flag){
        if(flag){
            Scanner myObj = new Scanner(System.in);
            String input;
            System.out.println("Enter value:");
            input = myObj.nextLine();
            x = input;
        }
        else {
            if(semWait(userInput, pid)){
                Scanner myObj = new Scanner(System.in);
                String input;
                System.out.println("Enter value:");
                input = myObj.nextLine();
                x = input;
            }
        }

    }
    public static void readFile(String x) {
        try {
            File myObj = new File(x);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
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
                argument1 =Line[1];
                print(argument1);
                break;

            case "readFile":
                argument1 = Line[1];
                readFile(argument1);
                break;
            case "assign":
                if(Line.length == 3){

                    if(Line[1].equals("a")){
                        assign(a,pid,semExist);
                    }else {
                        assign(b,pid,semExist);
                    }
                }


                break;
            case "writeFile":
                argument1 = Line[1];
                argument2 = Line[2];
                writeFile(argument1, argument2);
                break;
            case "printFromTo":
                int arg1 = Integer.parseInt(Line[1]);
                int arg2 = Integer.parseInt(Line[2]);
                printFromTo(arg1,arg2);
                break;
            case "semWait":
                argument1 = Line[1];
                switch(argument1) {

                    case "userInput":
                        semWait(userInput, pid);
                        semExist = false;
                        break;
                    case "userOutput":
                        semWait(userOutput, pid);
                        semExist = false;
                        break;
                    case "file":
                        semWait(file, pid);
                        semExist = false;
                        break;
                }
                break;
            case "semSignal":
                argument1 = Line[1];
                semExist = true;
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

            String pathname = programs.get(pid);

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
    public static void main(String[] args) {
        programs.put(1,"F:\\SEMESTER 6\\CSEN602 Operating Systems2\\OS_22_Project\\Program_1.txt");
        programs.put(2,"Program_2.txt");
        programs.put(3,"Program_3.txt");
//        printFromTo(0,2);
        execute(1);
    }
}
