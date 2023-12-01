import java.io.*;
import java.util.*;

class Process {
    int arrivalTime;
    int cpuBurstUnits;
    int priority;
    int remainingBurstUnits;
    boolean isSelected;
    public int order;

    public Process(int arrivalTime, int cpuBurstUnits, int priority) {
        this.arrivalTime = arrivalTime;
        this.cpuBurstUnits = cpuBurstUnits;
        this.priority = priority;
        this.remainingBurstUnits = cpuBurstUnits;
        this.isSelected = false;
    }
}

class Scheduler {
    private Queue<Process> readyQueue;
    private Process runningProcess;
    private List<Process> completedProcesses;
    private int currentTime;
    private int totalTime;
    private double throughput;
    private double utilization;
    private double avgWaitTime;
    private double avgTurnaroundTime;
    private double avgResponseTime;
    private double waitTime;
    private double responseTime;
    private double turnAroundTime;

    public Scheduler() {
        readyQueue = new PriorityQueue<>(Comparator.comparingInt(p -> p.order));;
        runningProcess = null;
        completedProcesses = new ArrayList<>();
        currentTime = 0;
        throughput = 0.0;
        utilization = 0.0;
        avgWaitTime = 0.0;
        avgTurnaroundTime = 0.0;
        avgResponseTime = 0.0;
        waitTime = 0.0;
        responseTime = 0.0;
        turnAroundTime = 0.0;
    }
    public void FIFO(Queue<Process> processQueue) {
        while (!processQueue.isEmpty() || runningProcess != null || !readyQueue.isEmpty()) {
            // Add arriving processes to the ready queue
            while (!processQueue.isEmpty() && processQueue.peek().arrivalTime <= currentTime) {
                readyQueue.add(processQueue.poll());
            }
            // If no process is running, start the next one from the ready queue
            if (runningProcess == null && !readyQueue.isEmpty()) {
                runningProcess = readyQueue.poll();
                //System.out.println(runningProcess.cpuBurstUnits);
                waitTime += currentTime - runningProcess.arrivalTime;
                turnAroundTime += runningProcess.cpuBurstUnits;
                //finish the process add burst to current time
                currentTime += runningProcess.cpuBurstUnits;
                completedProcesses.add(runningProcess);
                runningProcess = null;
            }
            else{
                currentTime++;
            }
        }
        responseTime = waitTime;
        turnAroundTime += waitTime;
        // Calculate statistics
        calculateStatistics();
    }
    public void SJF(Queue<Process> processQueue) {
        while (!processQueue.isEmpty() || runningProcess != null || !readyQueue.isEmpty()) {
            // Add arriving processes to the ready queue
            while (!processQueue.isEmpty() && processQueue.peek().arrivalTime <= currentTime) {
                readyQueue.add(processQueue.poll());
            }
            // If no process is running, start the next one from the ready queue
            if (runningProcess == null && !readyQueue.isEmpty()) {
                runningProcess = getShortestJob(readyQueue);
                //System.out.println(runningProcess.cpuBurstUnits);
                waitTime += currentTime - runningProcess.arrivalTime;
                turnAroundTime += runningProcess.cpuBurstUnits;
                // Finish the process and add burst to current time
                currentTime += runningProcess.cpuBurstUnits;
                completedProcesses.add(runningProcess);
                runningProcess = null;
            } 
            else {
                currentTime++;
            }
        }
        //System.out.println(currentTime);
        responseTime = waitTime;
        turnAroundTime = turnAroundTime + waitTime;
        // Calculate statistics
        calculateStatistics();
    }

    // get the process with the shortest remaining burst time
    private Process getShortestJob(Queue<Process> readyQueue) {
        PriorityQueue<Process> sortedQueue = new PriorityQueue<>(Comparator.comparingInt(p -> p.cpuBurstUnits));
        sortedQueue.addAll(readyQueue);

        Process shortestJob = sortedQueue.poll();

        // Remove the shortest job from the original queue
        readyQueue.remove(shortestJob);

        return shortestJob;
    }

    public void Priority(Queue<Process> processQueue) {
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(Comparator.comparingInt(p -> p.priority));
        while (!processQueue.isEmpty() || runningProcess != null || !readyQueue.isEmpty()) {
            // Add arriving processes to the ready queue
            while (!processQueue.isEmpty() && processQueue.peek().arrivalTime <= currentTime) {
                readyQueue.add(processQueue.poll());
            } 
            if(runningProcess == null&&!readyQueue.isEmpty()){
                runningProcess = getHighPriority(readyQueue);
            //System.out.println(runningProcess.priority);
                if(!runningProcess.isSelected){
                    responseTime += currentTime - runningProcess.arrivalTime;
                    runningProcess.remainingBurstUnits = runningProcess.cpuBurstUnits;
                    runningProcess.isSelected = true;
                }
                runningProcess.remainingBurstUnits --;
                // Finish the process and add burst to current time
                currentTime ++;
                if(runningProcess.remainingBurstUnits == 0){
                    turnAroundTime += currentTime - runningProcess.arrivalTime;
                    completedProcesses.add(runningProcess);
                    waitTime += currentTime - runningProcess.arrivalTime - runningProcess.cpuBurstUnits;
                    //System.out.println(runningProcess.priority);
                    runningProcess = null;
                }
            }
            else if(runningProcess != null&&readyQueue.isEmpty()){
                runningProcess.remainingBurstUnits --;
                currentTime ++;
                if(runningProcess.remainingBurstUnits == 0){
                    turnAroundTime += currentTime - runningProcess.arrivalTime;
                    completedProcesses.add(runningProcess);
                    waitTime += currentTime - runningProcess.arrivalTime - runningProcess.cpuBurstUnits;
                    //System.out.println(runningProcess.priority);
                    runningProcess = null;
                }
            }
            else if(runningProcess !=null&&!readyQueue.isEmpty()){
                readyQueue.add(runningProcess);
                runningProcess = null;
                runningProcess = getHighPriority(readyQueue);
                if(!runningProcess.isSelected){
                    responseTime += currentTime - runningProcess.arrivalTime;
                    runningProcess.remainingBurstUnits = runningProcess.cpuBurstUnits;
                    runningProcess.isSelected = true;
                }
                runningProcess.remainingBurstUnits --;
                // Finish the process and add burst to current time
                currentTime ++;
                if(runningProcess.remainingBurstUnits == 0){
                    turnAroundTime += currentTime - runningProcess.arrivalTime;
                    completedProcesses.add(runningProcess);
                    waitTime += currentTime - runningProcess.arrivalTime - runningProcess.cpuBurstUnits;
                    //System.out.println(runningProcess.priority);
                    runningProcess = null;
                }
            }
            else {
                currentTime++;
            }
            //System.out.println(currentTime);
        }
        // Calculate statistics
        calculateStatistics();
    }

    private Process getHighPriority(Queue<Process> readyQueue) {
        Comparator<Process> descendingPriorityComparator = Comparator.comparingInt(p -> p.priority);
        Process highPriority = readyQueue.stream().max(descendingPriorityComparator).orElse(null);
        if (highPriority != null) {
            readyQueue.remove(highPriority);
        }
        return highPriority;
    }

    private void calculateStatistics() {
        int numberOfProcesses = completedProcesses.size();
        totalTime = currentTime;
        throughput = (double)numberOfProcesses/currentTime;
        utilization = (double)numberOfProcesses/currentTime*100;
        avgWaitTime = waitTime/numberOfProcesses;
        avgTurnaroundTime = turnAroundTime/numberOfProcesses;
        avgResponseTime = responseTime/numberOfProcesses;
    }

    public void printStatistics(String schedulingType, BufferedWriter outputWriter) throws IOException {
        // Write the results to the output file
        outputWriter.write("Results for " + schedulingType + " scheduling:\n");
        outputWriter.write("Number of processes: " + completedProcesses.size() + "\n");
        outputWriter.write("Total elapsed time (for the scheduler): " + totalTime + "\n");
        outputWriter.write("Throughput (Number of processes executed in one unit of CPU burst time):" + throughput + "\n");
        outputWriter.write("CPU utilization: " + String.format("%.2f", utilization) + "%\n");
        outputWriter.write("Average waiting time (in CPU burst times): " + avgWaitTime + "\n");
        outputWriter.write("Average turnaround time (in CPU burst times): " + avgTurnaroundTime + "\n");
        outputWriter.write("Average response time (in CPU burst times): " + avgResponseTime + "\n\n");
        System.out.println(schedulingType + " Schedule simulating is successful!!");
        completedProcesses.clear();
    }

}

public class CPUSimulator {
    public Queue<Process> readDataFromFile(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filename));
        Queue<Process> processQueue = new LinkedList<>();

        // Skip the first line (header) in the file
        scanner.nextLine();

        int order = 0; // Keep track of the order of processes with the same arrival time

        while (scanner.hasNext()) {
            Process process = new Process(0,0,0);
            process.arrivalTime = scanner.nextInt();
            process.cpuBurstUnits = scanner.nextInt();
            process.priority = scanner.nextInt();
            process.order = order++; // Assign the order
            processQueue.add(process);
        }

        scanner.close();
        return processQueue;
    }

    public void runSimulation(String schedulingType) throws IOException {
        // Read processes from the data file
        Queue<Process> processQueue = readDataFromFile("Datafile1-txt.txt");

        // Initialize the scheduler
        Scheduler scheduler = new Scheduler();

        // Run simulations based on the scheduling type
        if (schedulingType.equals("fifo")) {
            // Replace with your FIFO implementation
            scheduler.FIFO(processQueue);
        } else if (schedulingType.equals("sjf")) {
            // Replace with your SJF implementation
            scheduler.SJF(processQueue);
        } else if (schedulingType.equals("priority")) {
            scheduler.Priority(processQueue);
        } else {
            System.err.println("Invalid scheduling type. Please use 'fifo', 'sjf', or 'priority'.");
            return;
        }

        // Open the output file
        BufferedWriter outputWriter = new BufferedWriter(new FileWriter("sample_output.txt", true)); // append mode

        // Print statistics for the scheduling type
        scheduler.printStatistics(schedulingType, outputWriter);

        // Close the output file
        outputWriter.close();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java CPUSimulator <scheduling_type>");
            System.exit(1);
        }

        // Create an instance of CPUSimulator and run the simulation
        CPUSimulator cpuSimulator = new CPUSimulator();
        try {
            cpuSimulator.runSimulation(args[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
