package info.ephyra.search.searchers;

import info.ephyra.search.Result;

public class GalagoKM extends KnowledgeMiner {


    @Override
    protected int getMaxResultsTotal() {
        return 0;
    }

    @Override
    protected int getMaxResultsPerQuery() {
        return 0;
    }

    @Override
    public KnowledgeMiner getCopy() {
        return null;
    }

    @Override
    protected Result[] doSearch() {
        return new Result[0];
    }
}
