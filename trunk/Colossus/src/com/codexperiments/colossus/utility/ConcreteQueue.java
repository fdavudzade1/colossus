package com.codexperiments.colossus.utility;


import java.util.Vector;

import android.os.Handler;
import android.util.Log;


public class ConcreteQueue implements Queue
{
    private Handler lHandler;
    private Vector  tasks = new Vector();
    private boolean waiting;
    private boolean shutdown;


    public void setShutdown (boolean isShutdown)
    {
        shutdown = isShutdown;
        if (waiting) {
            synchronized (this) {
                notifyAll();
            }
        }
    }

    public ConcreteQueue ()
    {
        lHandler = new Handler();
        tasks = new Vector();
        waiting = false;
        new Thread(new Worker()).start();
    }

    public void put (RunnableTask r)
    {
        tasks.add(r);
        if (waiting) {
            synchronized (this) {
                notifyAll();
            }
        }
    }

    public RunnableTask take ()
    {
        if (tasks.isEmpty()) {
            synchronized (this) {
                waiting = true;
                try {
                    wait();
                } catch (InterruptedException ie) {
                    waiting = false;
                }
            }
        }
        if (tasks.size() > 0)
            return (RunnableTask) tasks.remove(0);
        else
            return null;
    }



    private class Worker implements Runnable
    {
        public void run ()
        {
            while (!shutdown) {
                try {
                final RunnableTask r = take();
                if (r != null) {
                r.execute();
                lHandler.post(new Runnable() {
                    @Override
                    public void run ()
                    {
                        r.end();
                    }
                });
                }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ConcreteQueue", "Error", e);
                }
            }
            System.out.println("Finished");
        }
    }
}
