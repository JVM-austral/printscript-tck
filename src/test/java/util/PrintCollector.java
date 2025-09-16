package util;

import interpreter.PrintEmitter;

import java.util.ArrayList;
import java.util.List;

public class PrintCollector implements PrintEmitter {

    final private List<String> messages = new ArrayList<>();

    @Override
    public void print(String message) {
        messages.add(message);
       if( message.length() > 1024 * 32){
              throw new OutOfMemoryError("Java heap space");
       }
    }

    public List<String> getMessages() {
        return messages;
    }
}
