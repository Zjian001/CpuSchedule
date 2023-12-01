### Overview
    This Java program simulates CPU scheduling algorithms, including FIFO (First-In-First-Out), SJF (Shortest Job First), and Priority Scheduling. It reads process data from a file, simulates their execution based on the selected algorithm, and outputs performance statistics.

### Usage
* Compile the Program:
## 'javac CPUSimulator.java' 

### Run the Program:
* Provide the scheduling type as a command-line argument.
## 'java CPUSimulator <scheduling_type>'
* Replace <scheduling_type> with one of the following: fifo, sjf, or priority.

### Parameters
* The scheduling type is a required command-line argument. Choose one of the following:
* 'fifo' for First-In-First-Out scheduling.
* 'sjf' for Shortest Job First scheduling.
* 'priority' for Priority Scheduling.

### Sample Command
## 'java CPUSimulator fifo'
* This command runs the program with the FIFO scheduling algorithm.
## 'java CPUSimulator sjf'
* This command runs the program with the SJF scheduling algorithm.
## 'java CPUSimulator priority'
* This command runs the program with the Priority scheduling algorithm.


### Output:
* The program appends results to the sample_output.txt file, including the number of processes, total elapsed time, throughput, CPU utilization, average waiting time, average  turnaround time, and average response time.

### Results
* The program calculates and appends statistics to the sample_output.txt file based on the chosen scheduling algorithm.