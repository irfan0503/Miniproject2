Multi-Threaded Log Analyzer – Mini Project 2

This project implements a concurrent system that analyzes multiple log files at the same time using Java threads, ExecutorService, and Stream operations. It compares sequential and parallel log-processing performance.

📌 Features

Multi-Threaded Log Processing
Each log file is processed by a Callable worker.

Workers return keyword-count results.

Uses a fixed-size ExecutorService thread pool.

Concurrent Aggregation
A ConcurrentHashMap is used to safely merge keyword counts from different threads.

Stream-Based Analysis
Streams are used inside each worker to:

Read file lines

Filter keywords

Aggregate counts

Performance Measurement
Measures and prints execution time for:

Sequential version

Concurrent version

Output
Prints summary to console

Writes results to an output file

📁 Project Flow

Read all log files from the input folder.

Submit each file to a worker (Callable).

Workers return count maps for each file.

Merge results into a ConcurrentHashMap.

Print execution time & summary.

Write final counts to result file.

▶ How to Run java -jar LogAnalyzer.jar <folder_path>

🧪 Outputs Required (Deliverables) ✔ 1. Source code + compiled JAR

Stored in /src and /target.

✔ 2. Execution Time Comparison

Example (your real results will vary):

Mode Time (ms) Sequential 842 ms Concurrent (4 threads) 273 ms ✔ 3. Screenshot

Thread activity screenshot (Task Manager, console logs, or thread-pool debug prints).

✔ 4. README

Explains concurrency strategy (this file).

🧵 Concurrency Strategy (Summary)

A fixed thread pool handles all file-processing tasks.

Each log file is processed by a Callable, enabling result return values.

A ConcurrentHashMap ensures safe updates from multiple threads.

Futures collect results from workers and merge them.

This avoids thread creation overhead and scales well for large input folders.

Streams inside each worker enable concise filtering and keyword aggregation.

✔ LeetCode Problems for Practice

Required problems: 1, 217, 349, 242, 189, 704, 912, 26, 283

These help strengthen thinking in arrays, hashing, two-pointers, sorting, searching, etc.

👨‍💻 Author Ahamed Irfan Mini Project 2 – Multi-Threaded Log Analyzer
