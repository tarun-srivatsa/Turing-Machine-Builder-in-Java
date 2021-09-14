package turing_machine;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

class Transition{
    char read;
    char write;
    char shift;
    int nextState;

    Transition(String s){
        read=s.charAt(0);
        write=s.charAt(2);
        shift=s.charAt(4);

        int l=s.length();
        String substr=s.substring(6,l);
        nextState=Integer.parseInt(substr);
    }
}

class State{
    ArrayList<Transition> trs;

    State(ArrayList<Transition> ts){
        trs=ts;
    }
}

class Machine{
    Scanner fs;
    int stateCount,currState,finalState;
    char blankSym;
    StringBuffer Tape=new StringBuffer();
    ArrayList<State> states=new ArrayList<>();

    void buildMachine(Scanner f){
        this.fs=f;
        System.out.println("\n\t" + readString());
        String s=readString();
        System.out.println("Input symbols: "+s);

        s+=" "+readString();
        blankSym=readChar();
        System.out.println("Blank symbol: "+blankSym);

        s+=" "+blankSym;
        System.out.println("Tape symbols: "+s);

        stateCount=readInt();
        System.out.println("\nNumber of States: "+stateCount);
        currState=readInt();
        System.out.println("Start State: "+currState);

        for(int i=0;i<stateCount;i++) addState(i);
    }

    void addState(int ind){
        int trCount=readInt();
        if(trCount==0) finalState=ind;
        ArrayList<Transition> trs=new ArrayList<>();

        for(int i=0;i<trCount;i++){
            String s=readString();
            Transition tr=new Transition(s);
            trs.add(tr);
        }
        State st=new State(trs);
        states.add(st);
    }

    String readString(){
        String s=fs.next();
        while(s.startsWith("//") || s.isEmpty()) s=fs.next();
        return s;
    }

    char readChar(){
        String s=fs.next();
        while(s.startsWith("//") || s.isEmpty()) s=fs.next();
        return s.charAt(0);
    }

    int readInt(){
        String s=fs.next();
        while(s.startsWith("//") || s.isEmpty()) s=fs.next();
        return Integer.parseInt(s);
    }

    void runTuring(int index) throws InterruptedException {
        while(currState!=finalState){
            index=makeTrans(index);
            if(index==-1) throw new InterruptedException("ERROR: Transition Not Found! Machine HALTED.");
            printTape(index);
        }
    }

    int makeTrans(int index) throws InterruptedException {
        if(Tape.charAt(index)=='$') throw new InterruptedException("ERROR: Head left the Tape boundary! Machine HALTED.");

        State st=states.get(currState);
        for(Transition tr:st.trs){
            if(tr.read==Tape.charAt(index)){
                Tape.replace(index,index+1,String.valueOf(tr.write));
                currState=tr.nextState;

                switch (tr.shift){
                    case 'R': return index+1;
                    case 'L': return index-1;
                    default: return -1;
                }
            }
        }
        return -1;
    }

    void printTape(int index) {
        System.out.println("Tape: "+Tape);
        for(int i=0;i<index;i++) System.out.print(" ");
        System.out.println("      ^q"+currState+"\n");
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {System.out.println(e.getMessage());}
    }
}

class FileScanner{
    Scanner scan=new Scanner(System.in),fileScan;
    String inputstr;

    FileScanner() throws FileNotFoundException {
        System.out.print("Enter file path: ");
        String path = scan.nextLine();
        fileScan = new Scanner(new File(path));
        fileScan.useDelimiter("\n");
    }

    String buildTape(String str,char blank){
        String s="$";
        for(int i=0;i<5;i++) s+=blank;
        s=s.concat(str);
        for(int i=0;i<30;i++) s+=blank;
        s+='$';
        return s;
    }

    void setTape(Machine m) {
        System.out.print("\nEnter input string: ");
        inputstr=scan.nextLine();
        m.Tape=new StringBuffer(buildTape(inputstr,m.blankSym));
        m.printTape(6);
    }
}

public class TuringMain {
    public static void main(String[] args) {
        System.out.println("\n\tTRANSDUCER TURING MACHINE BUILDER\n");
        Machine m=new Machine();
        try {
            FileScanner fileScanner = new FileScanner();
            m.buildMachine(fileScanner.fileScan);
            fileScanner.setTape(m);
            m.runTuring(6);
        } catch (FileNotFoundException | InterruptedException e) {
            System.out.println(e);
            System.exit(0);
        }
    }
}
