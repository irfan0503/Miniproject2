import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class LogAnalyzer {

    private static final List<String> KEYWORDS = List.of("ERROR", "WARN", "INFO");

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Usage: java LogAnalyzer <folder_path>");
            return;
        }

        Path folderPath = Paths.get(args[0]);

        // Sequential run for comparison
        long seqStart = System.currentTimeMillis();
        Map<String, Integer> seqResult = runSequential(folderPath);
        long seqEnd = System.currentTimeMillis();

        // Concurrent run
        long concStart = System.currentTimeMillis();
        Map<String, Integer> concResult = runConcurrent(folderPath, 4);
        long concEnd = System.currentTimeMillis();

        // Print results
        System.out.println("=== Sequential Results ===");
        seqResult.forEach((k, v) -> System.out.println(k + ": " + v));
        System.out.println("Time: " + (seqEnd - seqStart) + " ms\n");

        System.out.println("=== Concurrent Results ===");
        concResult.forEach((k, v) -> System.out.println(k + ": " + v));
        System.out.println("Time: " + (concEnd - concStart) + " ms\n");

        // Write results to file
        writeOutput(concResult, "output/results.txt");

        System.out.println("Results written to output/results.txt");
    }

    // ------------------ Sequential Implementation ------------------

    private static Map<String, Integer> runSequential(Path folder) throws IOException {
        Map<String, Integer> keywordCounts = initKeywordMap();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.txt")) {
            for (Path file : stream) {
                List<String> lines = Files.readAllLines(file);
                processLines(lines, keywordCounts);
            }
        }
        return keywordCounts;
    }

    // ------------------ Concurrent Implementation ------------------

    private static Map<String, Integer> runConcurrent(Path folder, int poolSize) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        ConcurrentHashMap<String, Integer> globalCounts = new ConcurrentHashMap<>();
        initKeywordMap().forEach(globalCounts::put);

        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder, "*.txt")) {
            for (Path file : stream) {

                Callable<Map<String, Integer>> task = () -> {
                    Map<String, Integer> localMap = initKeywordMap();
                    List<String> lines = Files.readAllLines(file);
                    processLines(lines, localMap);
                    return localMap;
                };

                futures.add(executor.submit(task));
            }
        }

        for (Future<Map<String, Integer>> f : futures) {
            Map<String, Integer> result = f.get();
            result.forEach((k, v) -> globalCounts.merge(k, v, Integer::sum));
        }

        executor.shutdown();
        return globalCounts;
    }

    // ------------------ Helpers ------------------

    private static Map<String, Integer> initKeywordMap() {
        return KEYWORDS.stream().collect(Collectors.toMap(k -> k, k -> 0));
    }

    private static void processLines(List<String> lines, Map<String, Integer> map) {
        lines.forEach(line -> {
            KEYWORDS.forEach(keyword -> {
                if (line.contains(keyword)) {
                    map.put(keyword, map.get(keyword) + 1);
                }
            });
        });
    }

    private static void writeOutput(Map<String, Integer> results, String filePath) {
        try (PrintWriter out = new PrintWriter(filePath)) {
            results.forEach((k, v) -> out.println(k + ": " + v));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
