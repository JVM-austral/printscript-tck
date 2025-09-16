package implementation.adapters.linter;

import evaluator.input.ConsoleInputProvider;
import interpreter.ErrorHandler;
import interpreter.PrintScriptLinter;
import mock.StdOutputHandler;
import runner.RunnerImplementation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class LinterAdapter implements PrintScriptLinter {

  @Override
  public void lint(InputStream src, String version, InputStream config, ErrorHandler handler) {
    try {
      String code = readStream(src).replace("\r", "");
      String normalizedVersion = normalizeVersion(version);
      String configPath = prepareConfigFile(config, normalizedVersion);

      runLintWithCapturedOutput(code, configPath, normalizedVersion, handler);

    } catch (IOException e) {
      report(handler, "Error reading input streams: " + e.getMessage());
    } finally {
      closeQuietly(src, handler);
      closeQuietly(config, handler);
    }
  }

  private void runLintWithCapturedOutput(String code, String configPath, String version, ErrorHandler handler) {
    RunnerImplementation runner = new RunnerImplementation(
        version, new StdOutputHandler(), new ConsoleInputProvider(), new HashMap<>()
    );

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outputStream));

    try {
      runner.lint(code, configPath);
      processOutput(outputStream.toString().replace("\r", ""), handler);
    } finally {
      System.setOut(originalOut);
    }
  }

  private void processOutput(String output, ErrorHandler handler) {
    if (output.trim().isEmpty() || output.contains("No lint issues found")) return;
    for (String line : output.split("\\n")) {
      line = line.trim();
      if (!line.isEmpty() && !line.startsWith(",")) {
        handler.reportError(line);
      }
    }
  }

  private String prepareConfigFile(InputStream config, String version) throws IOException {
    if (config == null) return null;
    String content = readStream(config).trim();
    if (content.isEmpty()) return null;

    String adaptedJson = adaptJsonConfig(content, version);
    Path tempFile = Files.createTempFile("linter_config", ".json");
    Files.write(tempFile, adaptedJson.getBytes(StandardCharsets.UTF_8));
    tempFile.toFile().deleteOnExit();
    return tempFile.toString();
  }

  private String normalizeVersion(String version) {
    if (version == null || version.trim().isEmpty()) return "V1";
    return switch (version.trim()) {
      case "1.0", "V1", "v1" -> "V1";
      case "1.1", "V2", "v2" -> "V2";
      default -> "V1";
    };
  }

  private String adaptJsonConfig(String originalJson, String version) {
    try {
      String identifierFormat = extractJsonValue(originalJson, "identifier_format");
      String printlnAnalyzer = extractJsonValue(originalJson, "mandatory-variable-or-literal-in-println");
      String readInputAnalyzer = extractJsonValue(originalJson, "mandatory-variable-or-literal-in-readInput");

      StringBuilder json = new StringBuilder("{");

      appendNamingConvention(json, identifierFormat);

      if (json.charAt(json.length() - 1) != '{') json.append(",");
      json.append("\"usePrintlnAnalyzer\":").append(printlnAnalyzer != null ? printlnAnalyzer : "false");

      if ("V2".equalsIgnoreCase(version)) {
        json.append(",\"useReadInputAnalyzer\":").append(readInputAnalyzer != null ? readInputAnalyzer : "false");
      }

      json.append("}");
      return json.toString();

    } catch (Exception e) {
      return "V2".equalsIgnoreCase(version)
          ? "{\"usePrintlnAnalyzer\":false,\"useReadInputAnalyzer\":false}"
          : "{\"usePrintlnAnalyzer\":false}";
    }
  }

  private void appendNamingConvention(StringBuilder json, String identifierFormat) {
    if (identifierFormat == null) return;
    String fmt = identifierFormat.replace("\"", "").trim().toLowerCase();
    switch (fmt) {
      case "camel case" -> json.append("\"namingConvention\":\"camelCase\"");
      case "snake case" -> json.append("\"namingConvention\":\"snake_case\"");
    }
  }

  private String extractJsonValue(String json, String key) {
    try {
      String searchKey = "\"" + key + "\"";
      int keyIndex = json.indexOf(searchKey);
      if (keyIndex == -1) return null;

      int colonIndex = json.indexOf(":", keyIndex);
      if (colonIndex == -1) return null;

      int valueStart = colonIndex + 1;
      while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) valueStart++;
      if (valueStart >= json.length()) return null;

      char firstChar = json.charAt(valueStart);
      if (firstChar == '"') {
        int valueEnd = json.indexOf('"', valueStart + 1);
        return (valueEnd != -1) ? json.substring(valueStart, valueEnd + 1) : null;
      } else if (json.startsWith("true", valueStart)) return "true";
      else if (json.startsWith("false", valueStart)) return "false";
      else {
        int valueEnd = valueStart;
        while (valueEnd < json.length() &&
            (Character.isDigit(json.charAt(valueEnd)) || ".-".indexOf(json.charAt(valueEnd)) >= 0)) {
          valueEnd++;
        }
        return json.substring(valueStart, valueEnd);
      }
    } catch (Exception e) {
      return null;
    }
  }

  private String readStream(InputStream in) throws IOException {
    if (in == null) return null;
    StringBuilder result = new StringBuilder();
    try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
      char[] buffer = new char[1024];
      int len;
      while ((len = reader.read(buffer)) != -1) {
        result.append(buffer, 0, len);
      }
    }
    return result.toString();
  }

  private void closeQuietly(InputStream in, ErrorHandler handler) {
    if (in == null) return;
    try {
      in.close();
    } catch (IOException e) {
      report(handler, "Error closing input streams: " + e.getMessage());
    }
  }

  private void report(ErrorHandler handler, String msg) {
    if (handler != null) handler.reportError(msg);
  }
}
