package com.codexperiments.colossus.mvp.command;


import com.codexperiments.colossus.mvp.Presenter.Command;


public abstract class CommandSynchronousThrowable<TContext> extends Command<TContext>
{
    public CommandSynchronousThrowable (TContext pContext)
    {
        super(pContext);
    }


    @Override
    protected final void execute ()
    {
        try {
            onProcess();
        } catch (Throwable eThrowable) {
            onFail(eThrowable);
        }
    }


    protected abstract void onProcess () throws Throwable;

    protected abstract void onFail (Throwable pThrowable);
}
