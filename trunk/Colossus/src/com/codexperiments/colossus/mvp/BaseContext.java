package com.codexperiments.colossus.mvp;


public class BaseContext<TContext extends Context<TContext, TView>, TView>
implements Context<TContext, TView>
{
    public TView view;


    public BaseContext (TView pView)
    {
        view = pView;
    }

    @Override
    public void bindTo (TView pView)
    {
        view = pView;
    }
}
