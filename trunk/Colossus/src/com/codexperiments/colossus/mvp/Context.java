package com.codexperiments.colossus.mvp;


public interface Context<TContext extends Context<TContext, TView>, TView>
{
    public void bindTo (TView pView);
}
