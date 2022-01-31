import java.util.*;
import java.io.*;
public class CPU_Scheduler {
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		Random rand = new Random();
		boolean fileOpened = true;
		Scanner inputFile = null;
		String fileName = "";
		int choice;
		int numOfProcess = 0;;
		double avgTurnAround;
		double avgWaitTime;
		int qt;
		// 1D arrays used to store different attributes about each process; 
		//index 0 of all the arrays is all data for process 0, index 1 is all data for process 1, etc
		int [] fileNums = new int[40];
		int [] pid = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19};
		int [] arrival = new int[20];
		int [] burst = new int[20];
		int [] completion = new int[20];
		int [] wait = new int[20];
		int [] turnAround = new int[20];
		int [] priority = new int[20];
		
		System.out.print("1) Processes with be randomly generated\n2) Read Processes from a file\nEnter option (1 or 2): ");
		choice = getInt(input);
		while(choice != 1 && choice != 2) {
			System.out.print("Not a valid choice REENTER: ");
			choice = getInt(input);
		}
		System.out.print("Enter quantum time for Round Robin (1-3): ");
		qt = getInt(input);
		while(qt < 1 || qt > 3) {
			System.out.print("Not a valid choice REENTER: ");
			qt = getInt(input);
		}
		while(choice != 1 && choice != 2) {
			System.out.print("Not a valid choice REENTER: ");
			choice = getInt(input);
		}
		if(choice == 1) {
			System.out.print("Enter Number of Processes (MAX IS 20): ");
			numOfProcess = getInt(input);
			while(numOfProcess < 1 || numOfProcess > 20) {
				System.out.print("Out of range, REENTER: ");
				numOfProcess = getInt(input);
			}
			
			// Randomly assigns each process an arrival time and burst time
			for(int i = 0; i < numOfProcess; i++) {
				arrival[i] = rand.nextInt(10 - 1 + 1) + 1;
				burst[i] = rand.nextInt(10 - 1 + 1) + 1;
			}
			
			
		}
		else {
			//Reads numbers from file
			int counter = 0;
			
			System.out.print("Enter filename (Include .txt extension): ");
			
			try {
				fileName = input.next();
	            inputFile = new Scanner(new File(fileName)); 
	        } 
			
	        catch(FileNotFoundException e) { 
	            System.out.println("--- File Not Found! ---"); 
	            fileOpened = false; 
	        } if(fileOpened) {
	        	//puts numbers from file into a temporary array
				while(inputFile.hasNextInt() && counter  < 40){
					
					fileNums[counter] = inputFile.nextInt();
					counter++;
				}
			
	        }
	        // Divides the burst times and arrivals times from the temporary fileNumes array
	        // And puts them in the corresponding burstTime and arrivalTime array
	        int arrivalCounter = 0;
	        int burstCounter = 0;
	        numOfProcess = counter ;
	        for(int i = 0; i < numOfProcess; i++)
				System.out.print(fileNums[i] + " ");
			for(int i = 0; i < numOfProcess; i++) {
				if(i % 2 == 0) {
					arrival[arrivalCounter] = fileNums[i];
					arrivalCounter++;
				}
				else {
					burst[burstCounter] = fileNums[i];
					burstCounter++;
				}
			}
			if(counter  % 2 == 0) 
				numOfProcess = counter/2;
			else
				numOfProcess = (counter/2) + 1;
			
	       
			System.out.println();
			
		}
		System.out.println("The processes are: ");
		printProcesses(numOfProcess, pid, arrival, burst, false, wait, completion, turnAround);
		System.out.println("");
		System.out.println("After completion: ");
		System.out.println("\nFCFS: ");
		sortByArrival(numOfProcess, pid, arrival, burst);
		FCFS(numOfProcess, pid, arrival, burst, completion, wait, turnAround);
		printProcesses(numOfProcess, pid, arrival, burst, true, wait, completion, turnAround);
		System.out.println("Average waiting time for FCFS is: " + (double) averageWT(numOfProcess, wait));
		System.out.println("Average turnaround time for FCFS is: " + (double)averageTT(numOfProcess, turnAround));
		System.out.println("\nRound Robin with q = " + qt + ":");
		roundRobin(numOfProcess, pid, arrival, burst, completion, wait, turnAround, qt);
		printProcesses(numOfProcess, pid, arrival, burst, true, wait, completion, turnAround);
		System.out.println("Average waiting time for Round Robin is: " + (double) averageWT(numOfProcess, wait));
		System.out.println("Average turnaround time for Round Robin is: " + (double)averageTT(numOfProcess, turnAround));
		
		
		
		
		
	}
	// Ensures that input from user is of type int
	public static int getInt(Scanner input){
		
		while(!input.hasNextInt()) {
			input.next(); 
			System.out.print("Not an integer. Try Again! ");
		}
		return input.nextInt();
	}
	
	public static void printProcesses(int size, int [] pid, int []at, int []burst, boolean completed, int []w, int []c, int []tt) {
		if(completed == false) {
			System.out.printf("\n%s | %2s |%6s\n", "PID", "AT", "Burst");
			System.out.print("----------------\n");
			for(int i = 0; i < size; i ++) {
				
				System.out.printf("%3d %4d %6d\n", pid[i], at[i], burst[i]);
			}
		}else {
			System.out.printf("\n%s | %2s |%6s |%5s |%10s |%11s %n", "PID", "AT", "Burst", "Wait", "Completed", "Turnaround");
			System.out.print("------------------------------------------------\n");
			for(int i = 0; i < size; i ++) {
				
				System.out.printf("%3d %4d %6d %6d %7d %11d \n", pid[i], at[i], burst[i], w[i],c[i], tt[i] );
			}
		}
	}
	//Sort arrival times from shortest to greatest to determine order of which proccesses go in
	public static void sortByArrival(int size, int [] pid, int [] at, int [] burst ) {
		int temp;
		 for (int i = 0; i < size; i++) {           
	            for (int j = 0; j < size; j++) {
	                if (i != j && at[i] < at[j]) {
	                	//Puts current process AT into temp and flips at with greater at
	                    temp = at[i];
	                    at[i] = at[j];
	                    at[j] = temp;
	                    //Same process but for burst time
	                    temp = burst[i];
	                    burst[i] = burst[j];
	                    burst[j] = temp;
	                    //Same process but for PID
	                    temp = pid[i];
	                    pid[i] = pid[j];
	                    pid[j] = temp;
	                    
	                    
	                    
	                }
	            }
	        }
		 // This loop ensures that if two processes have the same arrival time then the lowest process id gets processes first
		 for(int i = 0; i < size-1; i++) {
			 if(at[i] == at[i+1] && (pid[i] > pid[i + 1])){
				 temp = at[i];
                 at[i] = at[i+1];
                 at[i+1] = temp;
                 //Same process but for burst time
                 temp = burst[i];
                 burst[i] = burst[i+1];
                 burst[i+1] = temp;
                 //Same process but for PID
                 temp = pid[i];
                 pid[i] = pid[i+1];
                 pid[i+1] = temp;
			 }
		 }
	}

	public static void FCFS(int size, int [] pid, int [] at, int [] burst, int[] comp, int[] wt, int [] tt) { 
		 for(int i = 0; i < size; i ++) {
				if(i == 0) {
					comp[i] = at[i] + burst[i];
				}
				else {
					if(comp[i-1] < at[i] ) {
						
						comp[i] = burst[i] + at[i];
					}
					else {
						comp[i] = comp[i-1] + burst[i];
					}
				}
				tt[i] = comp[i] - at[i];  
				wt[i] = tt[i] - burst[i]; 
		 }
		 
			
	}
	// Function that applies Round Robin to processes with user defined quantum time (1-5)
	public static void roundRobin(int size, int [] pid, int [] at, int [] burst, int[] comp, int[] wt, int [] tt, int qt) {
		boolean finished = false;
		int sum = 0;
		int time = 0;
		// A temporary array that that stores updated remaining burst times of each process after each quantum cycle.
		int [] burstTemp = new int [size];
		//Fills burstTemp array with current burst times of each process
		for(int i = 0; i < size; i ++) {
			burstTemp[i] = burst[i];
		}
		// While loop ensures that every process is finished before it exits the loop
		while(!finished) {
			finished = true;
			for(int i = 0; i < size; i++) {
				if(burstTemp[i] > 0) {
					finished = false;
					if(burstTemp[i] > qt ) {
						time += qt;
						burstTemp[i] -= qt;
						sum++;
					}
					else{
						if(at[i] <= sum) {
							time += burstTemp[i];
							comp[i] = time;
							burstTemp[i] = 0;
							sum++;
						}
					}
				}
				
			}
			if (finished == true)
		         break;
		}
		//Computes wait time and turn around for RR
		for(int i = 0; i < size; i++){
			tt[i] = comp[i] - at[i];  
			wt[i] = tt[i] - burst[i]; 
        }
	}
	// Calculates and returns the average wait time
	public static double averageWT(int size, int[] at) {
		int avg = 0;
		for(int i = 0; i < size ; i ++) {
			avg += at[i];
		}
		return (double)avg/size;
	}
	// Calculated and returns the average turn around time.
	public static double averageTT(int size, int[] tt) {
		int avg = 0;
		for(int i = 0; i < size ; i ++) {
			avg += tt[i];
		}
		return (double)avg/size;
	}
}

