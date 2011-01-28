package com.codexperiments.colossus.mvp.command;


import android.os.Handler;

import com.codexperiments.colossus.mvp.Presenter.Command;


public abstract class CommandAsynchronousThrowable<TContext, TResult> extends Command<TContext>
{
    private final Handler mHandler;
    

    public CommandAsynchronousThrowable (TContext pContext)
    {
        super(pContext);
        mHandler = new Handler();
    }


    @Override
    protected final void execute ()
    {
        onBegin();
        new RunTask().start();
    }
    
    private class RunTask extends Thread
    {
        public void run ()
        {
            try {
                final TResult lResult = onProcess();
                mHandler.post(new Runnable()
                {
                    public void run ()
                    {
                        onFinish(lResult);
                    }
                });
            } catch (final Throwable eThrowable) {
                mHandler.post(new Runnable()
                {
                    public void run ()
                    {
                        onFail(eThrowable);
                    }
                });
            }
        }
    }



    protected abstract void onBegin ();

    protected abstract TResult onProcess () throws Throwable;

    protected abstract void onFinish (TResult pResult);

    protected abstract void onFail (Throwable pThrowable);
}
