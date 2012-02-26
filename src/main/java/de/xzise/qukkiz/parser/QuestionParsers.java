package de.xzise.qukkiz.parser;

import de.xzise.XLogger;
import de.xzise.qukkiz.QukkizSettings;

public class QuestionParsers {

    private final ConfigurationParser ymlParser;
    private final TriviaParser defaultParser;

    public QuestionParsers(final QukkizSettings settings, final XLogger logger) {
        this.ymlParser = new ConfigurationParser(settings, logger);
        this.defaultParser = new TriviaParser(settings, logger);
    }

    public static String getFileExtension(final String filename) {
        int delimiter = filename.lastIndexOf('.');
        if (delimiter > 0 && delimiter < filename.length() - 1) {
            return filename.substring(delimiter + 1);
        } else {
            return "";
        }
    }

    public QuestionParser getParserByFileExtension(final String filename) {
        final String extension = getFileExtension(filename);
        if (extension.equalsIgnoreCase("yml")) {
            return this.ymlParser;
        } else {
            return this.defaultParser;
        }
    }
}
