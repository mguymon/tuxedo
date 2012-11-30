package com.tobedevoured.tuxedo;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.tobedevoured.command.annotation.Command;

public interface IMigrations {

    @Command
    public abstract void createKeyspace() throws ConnectionException;

    @Command
    public abstract void deleteKeyspace() throws ConnectionException;

}