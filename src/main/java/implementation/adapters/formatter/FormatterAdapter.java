package implementation.adapters.formatter;

import factory.FormatCommandFactory;
import factory.Version;
import interpreter.PrintScriptFormatter;
import kotlin.Result;
import lexer.Lexer;
import token.Token;
import java.util.regex.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class FormatterAdapter implements PrintScriptFormatter {

    @Override
    public void format(InputStream src, String version, InputStream config, Writer writer) {
        try {

            Version v = "1".equals(normalizeVersion(version)) ? Version.V1 : Version.V2;

            String configJson = readAll(config);
            String adaptedConfigJson = kebabToCamel(configJson);
            adaptedConfigJson = fixSpecialKeys(adaptedConfigJson);

            File tempConfigFile = File.createTempFile("formatter-config", ".json");
            try (FileOutputStream fos = new FileOutputStream(tempConfigFile)) {
                fos.write(adaptedConfigJson.getBytes(StandardCharsets.UTF_8));
            }

            FormatCommandFactory commandFactory = new FormatCommandFactory(v, tempConfigFile.getAbsolutePath());
            Lexer lexer = commandFactory.getLexer();
            var formatter = commandFactory.getFormatter();

            InputStreamReader reader = new InputStreamReader(src);
            List<Result<Token>> tokens = extractAllTokens(reader, lexer);

            String formattedCode = formatter.format(tokens);
            writer.write(formattedCode);
            writer.flush();

        } catch (IOException e) {
            throw new RuntimeException("Error during formatting", e);
        }
    }

    private List<Result<Token>> extractAllTokens(InputStreamReader reader, Lexer lexer) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1024];
        int n;
        while ((n = reader.read(buffer)) != -1) {
            sb.append(buffer, 0, n);
        }

        String cleanSource = sb.toString().replace("\r", "");
        return lexer.tokenize(cleanSource);
    }

    private String readAll(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }


    private String kebabToCamel(String json) {
        Pattern p = Pattern.compile("-([a-zA-Z])");
        Matcher m = p.matcher(json);
        StringBuffer sb = new StringBuffer();

        while (m.find()) {
            m.appendReplacement(sb, m.group(1).toUpperCase());
        }
        m.appendTail(sb);

        String result = sb.toString();
        if (result.contains("-")) {
            return kebabToCamel(result);
        }
        return result;
    }


    private String normalizeVersion(String version) {
        if (version == null || version.trim().isEmpty()) return "V1";
        switch (version.trim()) {
            case "1.0": return "1";
            case "1.1": return "2";
            case "V1":
            case "v1": return "1";
            case "V2":
            case "v2": return "2";
            default: return "1";
        }
    }
    private String fixSpecialKeys(String json) {
        return json.replace("\"lineBreaksAfterPrintln\"", "\"lineBreakAfterPrintLn\"");
    }

}