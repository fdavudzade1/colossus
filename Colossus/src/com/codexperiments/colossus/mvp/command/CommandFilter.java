package com.codexperiments.colossus.mvp.command;


import android.widget.Filter;


// FIXME Use a map<Action, Context data> to avoid the need of extension for the Action
public abstract class CommandFilter<TContext, TFilterContext, TFilterItem> extends Filter
{
    protected final TContext mContext;


    public CommandFilter (TContext pContext)
    {
        mContext = pContext;
    }


    @Override
    protected final FilterResults performFiltering (CharSequence pConstraint)
    {
        String lConstraint = (pConstraint != null) ? pConstraint.toString() : null;
        TFilterContext lFilterContext = onCreate(lConstraint);
        CommandFilterResults<TFilterContext> lFilterResults = new CommandFilterResults<TFilterContext>(lFilterContext);
        lFilterResults.count = -1;
        lFilterResults.values = lFilterResults;

        try {
            onProcess(lFilterResults.filterContext);
            return lFilterResults;
        } catch (Throwable pThrowable) {
            lFilterResults.exception = pThrowable;
            return lFilterResults;
        }
    }

    @Override
    protected final void publishResults (CharSequence pConstraint, FilterResults pFilterResults)
    {
        if (pFilterResults != null) {
            @SuppressWarnings("unchecked")
            CommandFilterResults<TFilterContext> lFilterResults = (CommandFilterResults<TFilterContext>) pFilterResults.values;
            if (lFilterResults.exception == null) {
                lFilterResults.count = onFinish(lFilterResults.filterContext);
            } else {
                lFilterResults.count = onFail(lFilterResults.filterContext, (Throwable) lFilterResults.exception);
            }
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public final CharSequence convertResultToString (Object pResultValue)
    {
        return asString((TFilterItem) pResultValue);
    }


    protected abstract TFilterContext onCreate (String pConstraint);

    protected abstract void onProcess (TFilterContext pFilterContext) throws Throwable;

    protected abstract int onFinish (TFilterContext pFilterContext);

    protected abstract int onFail (TFilterContext pFilterContext, Throwable pThrowable);

    protected abstract String asString (TFilterItem pResultValue);



    private static class CommandFilterResults<TFilterContext> extends FilterResults
    {
        private Throwable      exception;
        private TFilterContext filterContext;
        


        public CommandFilterResults (TFilterContext pFilterContext)
        {
            super();
            exception     = null;
            filterContext = pFilterContext;
        }
    }
}
