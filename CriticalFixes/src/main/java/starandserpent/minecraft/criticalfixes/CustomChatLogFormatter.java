package starandserpent.minecraft.criticalfixes;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

// This is used to remove excess information from the chat log.
// We don't want the logger to add anything extra, we will handle that ourselves.
public class CustomChatLogFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        return record.getMessage() + "\n";
    }
}
