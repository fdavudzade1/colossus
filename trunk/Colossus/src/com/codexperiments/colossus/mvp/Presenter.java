package com.codexperiments.colossus.mvp;


import android.os.Looper;


public final class Presenter<TContext, TView>
{
    public void execute (Command<TContext> pCommand)
    {
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            throw new IllegalStateException("Commands must be executed from the UI Thread");
        }
        pCommand.execute();
    }



    public static abstract class Command<TContext>
    {
        protected final TContext mContext;


        public Command (TContext pContext)
        {
            mContext = pContext;
        }

        protected abstract void execute ();
    };
}
