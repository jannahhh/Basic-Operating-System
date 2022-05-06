import java.io.*;  // Import the File class
import java.util.*;

public class Interpreter {


    static int var1;
    static int var2;
    static int var3;
    static String var_Str_1;
    static String var_Str_2;
    static String var_Str_3;

    public static void print(int x){
        System.out.println(x);
    }
    public static void print(String x){
        System.out.println(x);
    }
    public static void assign(int x, int y){
        x = y;
    }
    public static void assign(String x, String y){
        x = y;
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
    public static void writeFile(String x, String y) {
        try {
            FileWriter myWriter = new FileWriter(x);
            myWriter.write(y);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public static void printFromTo(int x, int y){
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
    public static void main(String[] args) {
        printFromTo(0,2);
    }
}
