package implementation.adapters;

import factory.LexerFactoryV1;
import factory.LintCommandFactory;
import formatter.Formatter;
import formatter.FormatterImpl;
import formatterfactory.FormatterFactoryV1;
import interpreter.PrintScriptFormatter;
import kotlin.Result;
import wrapper.LexerWrapperImplementation;
import wrapper.TokenBuffer;
import lexer.Lexer;
import token.Token;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class FormatterAdapter implements PrintScriptFormatter {

    private Lexer lexer;

    public FormatterAdapter() {
        this.lexer = new LexerFactoryV1().create();
    }

    @Override
    public void format(InputStream src, String version, InputStream config, Writer writer) {
//        try {
////            Formatter myFormatter = new FormatterFactoryV1().create();
////            InputStreamReader reader = new InputStreamReader(src);
////            TokenBuffer tokenBuffer = new TokenBuffer();
////            LexerWrapperImplementation lexerWrapper = new LexerWrapperImplementation(lexer, reader, tokenBuffer);
////
////            List<Result<? extends Token>> rawTokens = extractAllTokens(lexerWrapper);
////            List<Result<Token>> tokens = castTokenList(rawTokens);
//
////            String formattedCode = myFormatter.format(tokens);
////
////            writer.write(formattedCode);
////            writer.flush();
//
//        } catch (IOException e) {
//            throw new RuntimeException("Error during formatting", e);
//        }
    }

//    private List<Result<? extends Token>> extractAllTokens(LexerWrapperImplementation lexerWrapper) {
//        List<Result<? extends Token>> tokens = new ArrayList<>();
//        while (lexerWrapper.hasNext()) {
//            Result<? extends Token> token = lexerWrapper.next();
//            tokens.add(token);
//        }
//        return tokens;
//    }

//    @SuppressWarnings("unchecked")
//    private List<Result<Token>> castTokenList(List<Result<? extends Token>> rawTokens) {
//        List<Result<Token>> tokens = new ArrayList<>();
//        for (Result<? extends Token> result : rawTokens) {
//            tokens.add((Result<Token>) result);
//        }
//        return tokens;
//    }
}