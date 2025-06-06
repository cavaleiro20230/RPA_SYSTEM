package com.rpa.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
public class RpaScriptExecutor {
    
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + "/rpa-scripts/";
    
    public String execute(String scriptContent, String scriptType) throws Exception {
        switch (scriptType.toUpperCase()) {
            case "JAVA":
                return executeJavaScript(scriptContent);
            case "PYTHON":
                return executePythonScript(scriptContent);
            case "SELENIUM":
                return executeSeleniumScript(scriptContent);
            default:
                throw new UnsupportedOperationException("Tipo de script não suportado: " + scriptType);
        }
    }
    
    private String executeJavaScript(String scriptContent) throws Exception {
        // Criar diretório temporário
        Path tempDir = Paths.get(TEMP_DIR);
        Files.createDirectories(tempDir);
        
        // Extrair nome da classe do código
        String className = extractClassName(scriptContent);
        String fileName = className + ".java";
        Path javaFile = tempDir.resolve(fileName);
        
        // Escrever código Java em arquivo
        try (FileWriter writer = new FileWriter(javaFile.toFile())) {
            writer.write(scriptContent);
        }
        
        // Compilar
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new RuntimeException("Compilador Java não disponível");
        }
        
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        int compilationResult = compiler.run(null, null, errorStream, javaFile.toString());
        
        if (compilationResult != 0) {
            throw new RuntimeException("Erro de compilação: " + errorStream.toString());
        }
        
        // Executar
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{tempDir.toUri().toURL()});
        Class<?> clazz = Class.forName(className, true, classLoader);
        Object instance = clazz.getDeclaredConstructor().newInstance();
        
        // Procurar método executar
        Method executeMethod = clazz.getMethod("executar");
        
        // Capturar saída
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outputStream));
        
        try {
            executeMethod.invoke(instance);
            return outputStream.toString();
        } finally {
            System.setOut(System.out); // Restaurar saída padrão
            classLoader.close();
            // Limpar arquivos temporários
            Files.deleteIfExists(javaFile);
            Files.deleteIfExists(tempDir.resolve(className + ".class"));
        }
    }
    
    private String executePythonScript(String scriptContent) throws Exception {
        // Implementação para executar scripts Python
        Path tempDir = Paths.get(TEMP_DIR);
        Files.createDirectories(tempDir);
        
        Path pythonFile = tempDir.resolve("script.py");
        try (FileWriter writer = new FileWriter(pythonFile.toFile())) {
            writer.write(scriptContent);
        }
        
        ProcessBuilder pb = new ProcessBuilder("python", pythonFile.toString());
        Process process = pb.start();
        
        // Capturar saída
        byte[] output = process.getInputStream().readAllBytes();
        byte[] error = process.getErrorStream().readAllBytes();
        
        int exitCode = process.waitFor();
        
        Files.deleteIfExists(pythonFile);
        
        if (exitCode != 0) {
            throw new RuntimeException("Erro na execução Python: " + new String(error));
        }
        
        return new String(output);
    }
    
    private String executeSeleniumScript(String scriptContent) throws Exception {
        // Implementação para scripts Selenium
        // Por simplicidade, tratamos como Java com dependências Selenium
        return executeJavaScript(scriptContent);
    }
    
    private String extractClassName(String javaCode) {
        // Extrair nome da classe do código Java
        String[] lines = javaCode.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("public class ")) {
                String[] parts = line.split("\\s+");
                for (int i = 0; i < parts.length - 1; i++) {
                    if ("class".equals(parts[i])) {
                        return parts[i + 1].replace("{", "");
                    }
                }
            }
        }
        return "RpaScript"; // Nome padrão
    }
}
