package com.example.collaborative_code_editor.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
public class CodeExecutionController {

    @PostMapping("/code/execute")
    public ResponseEntity<Map<String, String>> executeCode(@RequestBody Map<String, String> request) {
        String code = request.get("code");

        try {
            File sourceFile = new File("Test.java");
            try (FileWriter writer = new FileWriter(sourceFile)) {
                writer.write(code);
            }

            ProcessBuilder compileProcessBuilder = new ProcessBuilder("javac", sourceFile.getAbsolutePath());
            Process compileProcess = compileProcessBuilder.start();

            String compileErrors = getProcessOutput(compileProcess.getErrorStream());
            compileProcess.waitFor();

            if (!compileErrors.isEmpty()) {
                return ResponseEntity.ok(Map.of("result", "Compilation Error:\n" + compileErrors));
            }

            ProcessBuilder runProcessBuilder = new ProcessBuilder("java", "Test");
            Process runProcess = runProcessBuilder.start();

            String output = getProcessOutput(runProcess.getInputStream());
            String errors = getProcessOutput(runProcess.getErrorStream());
            runProcess.waitFor();

            String result = output.isEmpty() ? "No output returned" : output;
            if (!errors.isEmpty()) {
                result += "\nRuntime Error:\n" + errors;
            }

            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("result", "Error during execution: " + e.getMessage()));
        }
    }

    private String getProcessOutput(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString();
        }
    }
}
