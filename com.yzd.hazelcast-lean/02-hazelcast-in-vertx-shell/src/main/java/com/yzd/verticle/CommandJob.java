package com.yzd.verticle;

import com.yzd.utils.CommandUtil;
import io.vertx.ext.shell.term.Term;

public class CommandJob implements Runnable {
    private final Term term;
    private final String fullCommand;

    public CommandJob(Term term, String fullCommand) {
        this.term = term;
        this.fullCommand = fullCommand;
    }

    @Override
    public void run() {
        String result = CommandUtil.runCmdNew(this.fullCommand);
        this.term.write(result);
    }
}
