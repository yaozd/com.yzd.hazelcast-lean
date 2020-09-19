package com.yzd.verticle;

import com.yzd.utils.CommandUtil;
import io.vertx.core.Handler;
import io.vertx.ext.shell.term.Term;

public class CommandHandler implements Handler<String> {
    private static final String CALL = "\r\nCall:";
    private static final String END = "\r";
    private static final String NEW_LINE = "\r\n";
    private final Term term;
    private StringBuilder command;

    public CommandHandler(Term term) {
        this.term = term;
        command = newStringBuilder();
    }

    @Override
    public void handle(String line) {
        if (!END.equals(line)) {
            this.command.append(line);
            this.term.write(line);
            return;
        }
        if (command.length() == 0) {
            this.term.write(NEW_LINE);
            return;
        }
        String fullCommand = command.toString();
        this.term.write(CALL + fullCommand + NEW_LINE);
        //TODO 调整为异步线程池的模式即可
        String result = CommandUtil.runCmdNew(fullCommand);
        this.term.write(result);
        this.command = newStringBuilder();
    }

    private StringBuilder newStringBuilder() {
        return new StringBuilder();
    }
}
