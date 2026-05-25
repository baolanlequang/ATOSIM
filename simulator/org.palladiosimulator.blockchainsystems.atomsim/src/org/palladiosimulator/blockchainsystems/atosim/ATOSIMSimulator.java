package org.palladiosimulator.blockchainsystems.atosim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ATOSIMSimulator {
	
    // ----------------------------------------------------
    // DEFAULT LOCATIONS (can be overridden via CLI args)
    // ----------------------------------------------------
    private static final Path DEFAULT_TESTMODELS_DIR =
            Paths.get("testmodels");

    private static final Path DEFAULT_BASE_CONFIG_JSON =
            DEFAULT_TESTMODELS_DIR.resolve("configuration.json");

    private static final Path DEFAULT_CSV =
            Paths.get("optimized_deterministic_lhs_configurations.csv");

    public static void main(String[] args) {

        Path csvPath = (args.length >= 1) ? Paths.get(args[0]) : DEFAULT_CSV;
        Path testmodelsDir = (args.length >= 2) ? Paths.get(args[1]) : DEFAULT_TESTMODELS_DIR;
        Path baseConfigJson = (args.length >= 3) ? Paths.get(args[2]) : DEFAULT_BASE_CONFIG_JSON;

        BlockchainATOSIMStandalone simulator =
                new BlockchainATOSIMStandalone(
                        "org.palladiosimulator.blockchainsystems.atomsim",
                        Activator.class);

        if (!simulator.initAnalysis()) {
            System.err.println("❌ Unable to initialize simulator");
            return;
        }

        System.out.println("✔ Simulator initialized");
        System.out.println("CSV: " + csvPath.toAbsolutePath());
        System.out.println("testmodels/: " + testmodelsDir.toAbsolutePath());
        System.out.println("Base config: " + baseConfigJson.toAbsolutePath());

        try {
            Map<String, String> baseConfig = loadJsonConfig(baseConfigJson);
            List<Map<String, String>> rows = loadCsv(csvPath);

            int runId = 1;

            for (Map<String, String> row : rows) {

                // Validate CSV row BEFORE running anything
                validateCsvColumns(row);

                // Start from base configuration.json
                Map<String, String> config = new LinkedHashMap<>(baseConfig);

                // Preserve config_id from CSV if present, else use run counter
                String configId = row.getOrDefault("config_id", String.valueOf(runId));
                config.put("config_id", configId);
                config.put("id", configId);

                // Copy CSV parameters as-is
                config.putAll(row);

                Path modelPath = pickModelPath(testmodelsDir, configId);
                config.put("blockchainSystemModelFilePath", modelPath.toString());

                System.out.println("\n▶ Run " + runId + " | config_id=" + configId);
                System.out.println("   Using model: " + modelPath.toAbsolutePath());
                System.out.println("   Monte-Carlo rounds = "
                        + config.getOrDefault("numberOfMonteCarloRounds", "?"));

                simulator.runSimulation(config, runId);
                runId++;
            }

            System.out.println("\n✔ All runs completed");

        } catch (Exception e) {
            System.err.println("❌ Batch execution failed");
            e.printStackTrace();
        }
    }

    // ----------------------------------------------------
    // Load base configuration.json
    // ----------------------------------------------------
    private static Map<String, String> loadJsonConfig(Path jsonPath) throws IOException {
        if (!Files.exists(jsonPath)) {
            throw new IllegalArgumentException(
                    "Base configuration.json not found: " + jsonPath.toAbsolutePath());
        }

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {}.getType();

        try (Reader r = Files.newBufferedReader(jsonPath)) {
            Map<String, String> m = gson.fromJson(r, type);
            if (m == null || m.isEmpty()) {
                throw new IllegalArgumentException(
                        "Base configuration.json is empty/unreadable: "
                                + jsonPath.toAbsolutePath());
            }
            return m;
        }
    }

    // ----------------------------------------------------
    // Load CSV configurations
    // ----------------------------------------------------
    private static List<Map<String, String>> loadCsv(Path csvPath) throws IOException {
        if (!Files.exists(csvPath)) {
            throw new IllegalArgumentException(
                    "CSV not found: " + csvPath.toAbsolutePath());
        }

        List<Map<String, String>> rows = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(csvPath)) {

            String headerLine = br.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException(
                        "CSV is empty: " + csvPath.toAbsolutePath());
            }

            String[] headers = headerLine.split(",");

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] values = line.split(",", -1);
                Map<String, String> row = new LinkedHashMap<>();

                for (int i = 0; i < headers.length && i < values.length; i++) {
                    row.put(headers[i].trim(), values[i].trim());
                }
                rows.add(row);
            }
        }

        if (rows.isEmpty()) {
            throw new IllegalArgumentException(
                    "No rows found in CSV: " + csvPath.toAbsolutePath());
        }

        return rows;
    }

    // ----------------------------------------------------
    // CSV validation
    // ----------------------------------------------------
    private static void validateCsvColumns(Map<String, String> row) {

        List<String> required = List.of(
                "config_id",
                "validator_count",
                "node_degree",
                "propagation_delay",
                "block_creation_interval",
                "max_block_size",
                "attacker_hash_power",
                "tie_breaking_parameter",
                "transaction_delay",
                "transaction_acceleration",
                "number_of_attackers",
                "attacker_hash_power_realized"
        );

        for (String key : required) {
            if (!row.containsKey(key) || row.get(key).isBlank()) {
                throw new IllegalArgumentException(
                        "❌ Missing or empty required CSV column: " + key
                                + " in row: " + row);
            }
        }
    }

    private static Path pickModelPath(Path testmodelsDir, String configId) {
        Path modelPath =
                testmodelsDir
                        .resolve("threesim-" + configId)
                        .resolve("Net.blockchainsystem");

        if (!Files.exists(modelPath)) {
            throw new IllegalArgumentException(
                    "❌ Model not found for config_id=" + configId +
                    " at " + modelPath.toAbsolutePath());
        }

        return modelPath;
    }
}

