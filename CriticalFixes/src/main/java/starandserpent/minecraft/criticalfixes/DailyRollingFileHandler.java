package starandserpent.minecraft.criticalfixes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class DailyRollingFileHandler extends FileHandler {

    private final SimpleDateFormat dateFormat;
    private String pattern;

    public DailyRollingFileHandler(String pattern) throws IOException, SecurityException {
        super();
        this.pattern = pattern;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.pattern = this.pattern.replace("%d", dateFormat.format(new Date()));
        setOutputStream(new FileOutputStream(new File(this.pattern), true));
    }

    @Override
    public synchronized void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
        String currentDate = dateFormat.format(new Date());
        if (!this.pattern.contains(currentDate)) {
            String oldFilename = this.pattern;
            this.pattern = this.pattern.replace("%d", currentDate);
            if (!this.pattern.equals(oldFilename)) {
                try {
                    super.close();
                    super.setOutputStream(new FileOutputStream(new File(this.pattern), true));
                } catch (IOException e) {
                    System.err.println("Failed to open log file: " + this.pattern);
                }
            }
        }
        super.publish(record);
        flush();
    }
}