package com.codexperiments.colossus.mvp.command;


import com.codexperiments.colossus.mvp.Presenter.Command;


public abstract class CommandSynchronous<TContext> extends Command<TContext>
{
    public CommandSynchronous (TContext pContext)
    {
        super(pContext);
    }


    @Override
    protected final void execute ()
    {
        onProcess();
    }


    protected abstract void onProcess ();
}
