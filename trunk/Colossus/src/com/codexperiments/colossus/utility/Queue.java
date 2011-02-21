package com.codexperiments.colossus.utility;


public interface Queue
{
    void put (RunnableTask r);

    RunnableTask take ();
}
