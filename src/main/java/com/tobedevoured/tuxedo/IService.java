package com.tobedevoured.tuxedo;

import java.io.IOException;

import com.tobedevoured.command.annotation.Command;

public interface IService {

    public abstract void start() throws ServiceException ;

    public abstract void stop() throws ServiceException ;

}