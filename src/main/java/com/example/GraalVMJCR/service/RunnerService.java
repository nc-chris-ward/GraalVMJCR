package com.example.GraalVMJCR.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class RunnerService {

    private static final Path tempDir;
    private static final Path sourceFile;

    static {
        try {
            tempDir = Files.createTempDirectory("code");
            sourceFile = tempDir.resolve("Main.java");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String compileAndRunCode(String code) {
        try {
            Files.write(sourceFile, code.getBytes());
            String compileResult = compile();
            if (!compileResult.isEmpty()) {
                return formatResult(Result.COMPILE_FAILURE, compileResult);
            }
            return formatResult(Result.SUCCESS, run());
        } catch (IOException e) {
            System.err.println("IOException - check valid temporary directory creation");
        }
        catch (InterruptedException e) {
            System.err.println("InterruptedException - check input code format");
        }
        return "";
    }

    private String compile() throws IOException, InterruptedException {
        ProcessBuilder pbCompile = new ProcessBuilder("javac", sourceFile.toString());
        pbCompile.directory(tempDir.toFile());
        Process compileProcess = pbCompile.start();

        BufferedReader errorReader = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()));
        StringBuilder errorOutput = new StringBuilder();
        String line;
        while ((line = errorReader.readLine()) != null) {
            errorOutput.append(line).append("\n");
        }

        compileProcess.waitFor();

        if (compileProcess.exitValue() != 0) {
            return errorOutput.toString();
        }

        return "";
    }

    private String run() throws IOException, InterruptedException {
        ProcessBuilder pbRun = new ProcessBuilder("java", "-cp", tempDir.toString(), "Main");
        pbRun.directory(tempDir.toFile());
        Process runProcess = pbRun.start();

        BufferedReader outputReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = outputReader.readLine()) != null) {
            output.append(line).append("\n");
        }

        BufferedReader runtimeErrorReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
        StringBuilder runtimeErrorOutput = new StringBuilder();
        while ((line = runtimeErrorReader.readLine()) != null) {
            runtimeErrorOutput.append(line).append("\n");
        }

        runProcess.waitFor();

        if (runProcess.exitValue() != 0) {
            return formatResult(Result.RUNTIME_ERROR, runtimeErrorOutput.toString());
        }

        return output.toString();
    }

    private String formatResult(Result result, String output) {
        return switch (result) {
            case COMPILE_FAILURE -> "- COMPILE ERROR -\n" + output;
            case RUNTIME_ERROR -> "- RUNTIME ERROR -\n" + output;
            case SUCCESS -> output;
        };
    }

    private enum Result {
        COMPILE_FAILURE,
        RUNTIME_ERROR,
        SUCCESS
    }
}
