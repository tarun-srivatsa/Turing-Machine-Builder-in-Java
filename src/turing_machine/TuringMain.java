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
    //Constructor that divides string into specific symbols and next state's number
        read=s.charAt(0);
        write=s.charAt(2);
        shift=s.charAt(4);

        int l=s.length();
        String substr=s.substring(6,l);
        nextState=Integer.parseInt(substr);
    }
}

class State{
  	//List of transitions for a state
    ArrayList<Transition> trs;

    State(ArrayList<Transition> ts){
        trs=ts;
    }
}

class Machine{
    Scanner fs;		//scanner object to read input
    int stateCount;	//number of states to be read
  	int currState;	//initialized to start state, and then to keep track of current state in automaton
  	int finalState;	//to halt the machine when reached, must not contain any transitions
    char blankSym;	//blank symbol defined for the machine in the input file
  	
  	//TAPE is a member of machine
    StringBuffer Tape=new StringBuffer();
  	//list of states
    ArrayList<State> states=new ArrayList<>();

    void buildMachine(Scanner f){
        this.fs=f;
      
      	//prints the title in the first line of input file
        System.out.println("\n\t" + readString());
      
      	//reads the string of input symbols (space separated)
        String s=readString();
        System.out.println("Input symbols: "+s);
		
      	//reads string of other tape symbols defined in transitions
        s+=" "+readString();
      
      	//reads the blank symbol from the file
        blankSym=readChar();
        System.out.println("Blank symbol: "+blankSym);

        s+=" "+blankSym;
        System.out.println("Tape symbols: "+s);
		
      	//number of states to be defined, say N
        stateCount=readInt();
        System.out.println("\nNumber of States: "+stateCount);
      
      	//current state variable (currState) is initialized to start-state
        currState=readInt();
        System.out.println("Start State: "+currState);
		
      	//addState method is called N number of times
        for(int i=0;i<stateCount;i++) addState(i);
    }

    void addState(int ind){
      	//number of transitions is read for a state and stored in trCount
        int trCount=readInt();
      
      	//state with 0 transitions is assigned to be final state for the machine to halt
        if(trCount==0) finalState=ind;
        ArrayList<Transition> trs=new ArrayList<>();

        for(int i=0;i<trCount;i++){
        //each transition object is created and appended to the list of transitions
            String s=readString();
            Transition tr=new Transition(s);
            trs.add(tr);
        }
      
      	//new state object is created by passing list of transitions with the constructor
        State st=new State(trs);
        states.add(st);
    }

  	//to read input from file object "fs" and return it
    String readString(){
        String s=fs.next();
      	//to ignore lines starting from '//'
        while(s.startsWith("//") || s.isEmpty()) s=fs.next();
        return s;
    }

  	//to read input from file object as string and return the first character
    char readChar(){
        String s=fs.next();
        while(s.startsWith("//") || s.isEmpty()) s=fs.next();
        return s.charAt(0);
    }
	
  	//to read input from file object and return it's integer form
    int readInt(){
        String s=fs.next();
        while(s.startsWith("//") || s.isEmpty()) s=fs.next();
        return Integer.parseInt(s);
    }
	
  	//to perform transitions on the tape starting from currState
    void runTuring(int index) throws InterruptedException {
        while(currState!=finalState){
          	//calling makeTrans() to perform transition and return the index pointed by the R/W head
            index=makeTrans(index);
            if(index==-1) throw new InterruptedException("ERROR: Transition Not Found! Machine HALTED.");
            
          	//tape instance printed after each transition
          	printTape(index);
        }
    }
	
    int makeTrans(int index) throws InterruptedException {
        if(Tape.charAt(index)=='$') throw new InterruptedException("ERROR: Head left the Tape boundary! Machine HALTED.");

        State st=states.get(currState);
      
      	//to traverse across the list of transitions to match tape symbol with read symbol
        for(Transition tr:st.trs){
            if(tr.read==Tape.charAt(index)){
              	//to write the write-symbol onto the tape
                Tape.replace(index,index+1,String.valueOf(tr.write));
                currState=tr.nextState;

                switch (tr.shift){
                    case 'R': return index+1;	//shift right on tape
                    case 'L': return index-1;	//shift left on tape
                    default: return -1;			//unknown shift symbol
                }
            }
        }
        return -1;	//transition not found
    }

    void printTape(int index) {
      	int interval=500;	//in milliseconds
        System.out.println("Tape: "+Tape);
        for(int i=0;i<index;i++) System.out.print(" ");	//to align
      	
      	//to print the R/W head of machine pointing to particular tape index along with current state index
        System.out.println("      ^q"+currState+"\n");
        try {
          	//to print new instance of tape with a particular interval
            Thread.sleep(interval);
        } catch (InterruptedException e) {System.out.println(e.getMessage());}
    }
}

class FileScanner{
    Scanner scan=new Scanner(System.in);
  	Scanner fileScan;
    String inputstr;

    FileScanner() throws FileNotFoundException {
      	//to read the input from .txt file
        System.out.print("Enter file path: ");
        String path = scan.nextLine();
        fileScan = new Scanner(new File(path));
        fileScan.useDelimiter("\n");
    }

    String buildTape(String str,char blank){
      	//str is the input string to be added to the tape
      	//tape defined to begin and end with '$' symbol to avoid indefinite transitions
        
      	String s="$";	//begin
        for(int i=0;i<5;i++) s+=blank;	//adding few blank symbols
        s=s.concat(str);				//ading the input string
        for(int i=0;i<30;i++) s+=blank;	//adding few more blanks	
        s+='$';			//end
        //this concatenated string forms a Tape and is returned
      	return s;
    }

    void setTape(Machine m) {
      	//read input string from console
        System.out.print("\nEnter input string: ");
        inputstr=scan.nextLine();
      
      	//pass string as parameter to buildTape() method
        m.Tape=new StringBuffer(buildTape(inputstr,m.blankSym));
      
      	//6 == initial index of tape that is pointed by R/W head
        m.printTape(6);
    }
}

//class with main method
public class TuringMain {
    public static void main(String[] args) {
        System.out.println("\n\tTRANSDUCER TURING MACHINE BUILDER\n");
        Machine m=new Machine();	//new object of Machine class
        try {
            FileScanner fileScanner = new FileScanner();
          
          	//constructing the machine using details from Scanner object that reads the file
            m.buildMachine(fileScanner.fileScan);
            fileScanner.setTape(m);		//setting tape for the machine
            m.runTuring(6);				//to start execution of Turing Machine
        } catch (FileNotFoundException | InterruptedException e) {
            System.out.println(e);
            System.exit(0);
        }
    }
}
