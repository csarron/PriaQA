package info.ephyra.search.searchers;

import info.ephyra.io.MsgPrinter;
import info.ephyra.search.Result;
import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.query.Node;
import org.lemurproject.galago.core.retrieval.query.StructuredQuery;
import org.lemurproject.galago.core.tools.apps.BatchSearch;
import org.lemurproject.galago.utility.Parameters;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GalagoKM extends KnowledgeMiner {
    /**
     * Maximum total number of search results.
     */
    public static final int MAX_RESULTS_TOTAL = 20;
    /**
     * Maximum number of search results per query.
     */
    public static final int MAX_RESULTS_PERQUERY = 20;
    /**
     * Maximum number of documents fetched at a time.
     */
    public static final int MAX_DOCS = 20;

    private String indexLocation;

    public GalagoKM(String indexLocation) {
        this.indexLocation = indexLocation;
    }

    @Override
    protected int getMaxResultsTotal() {
        return MAX_RESULTS_TOTAL;
    }

    @Override
    protected int getMaxResultsPerQuery() {
        return MAX_RESULTS_PERQUERY;
    }

    @Override
    public KnowledgeMiner getCopy() {
        return new GalagoKM(this.indexLocation);
    }

    /**
     * Returns a representation of the query string that is suitable for Indri.
     *
     * @param qs query string
     * @return query string for Galago
     */
    public static String transformQueryString(String qs) {

        // replace ... OR ... by #or(... ...)
//        Matcher m = Pattern.compile(
//                "((\\([^\\(\\)]*+\\)|\\\"[^\\\"]*+\\\"|[^\\s\\(\\)]++) OR )++" +
//                        "(\\([^\\(\\)]*+\\)|\\\"[^\\\"]*+\\\"|[^\\s\\(\\)]++)").matcher(qs);
//        while (m.find())
//            qs = qs.replace(m.group(0), "#or(" + m.group(0) + ")");
        qs = qs.replace(" OR", "");

        // replace ... AND ... by #combine(... ...)
        Matcher m = Pattern.compile(
                "((\\([^\\(\\)]*+\\)|\\\"[^\\\"]*+\\\"|[^\\s\\(\\)]++) AND )++" +
                        "(\\([^\\(\\)]*+\\)|\\\"[^\\\"]*+\\\"|[^\\s\\(\\)]++)").matcher(qs);
        while (m.find())
            qs = qs.replace(m.group(0), "#combine(" + m.group(0) + ")");
        qs = qs.replace(" AND", "");

        // replace "..." by #ordered:1(...)
        m = Pattern.compile("\"([^\"]*+)\"").matcher(qs);
        while (m.find())
            qs = qs.replace(m.group(0), "#ordered:1(" + m.group(1) + ")");

        // form passage query
//		qs = "#combine[p](" + qs + ")";
        qs = "#combine(" + qs + ")";

        return qs;
    }

    @Override
    protected Result[] doSearch() {
        MsgPrinter.printStatusMsg("3.2.1 Galago Searching...query string: " + query.getQueryString());
        MsgPrinter.printStatusMsg("3.2.1 Searching...transformed query string: " + transformQueryString(query.getQueryString()));
        String queryText = transformQueryString(query.getQueryString());

        BatchSearch batchSearch = new BatchSearch();
        Parameters queryParams = Parameters.create();
        queryParams.set("index", indexLocation);
        queryParams.set("requested", 5);

        // open index
        try {
            Retrieval retrieval = RetrievalFactory.create(queryParams);

            // parse and transform query into runnable form
            Node root = StructuredQuery.parse(queryText);
            Node transformed = retrieval.transformQuery(root, queryParams);
            MsgPrinter.printStatusMsg("3.2.1 Searching...transformed galago query string: " + transformed.toPrettyString());
            List<ScoredDocument> queryResults = retrieval.executeQuery(transformed, queryParams).scoredDocuments;
            // if we have some results -- print in to output stream
            if (!queryResults.isEmpty()) {
                String[] passages = new String[queryResults.size()];
                String[] neStrs = new String[queryResults.size()];
                String[] docNos = new String[queryResults.size()];

                for (int i = 0; i < queryResults.size(); i++) {
                    ScoredDocument sd = queryResults.get(i);
                    MsgPrinter.printStatusMsg(sd.toString());
                    Document doc = retrieval.getDocument(sd.documentName, Document.DocumentComponents.All);
//                    System.err.println(doc.text);
                    docNos[i] = sd.documentName;

                    passages[i] = parsePassage(doc.text);
                    neStrs[i] = parseNeStr(doc.text);
                }

                return getResults(passages, neStrs, docNos, false);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Result[0];
    }

    private String parseNeStr(String text) {
        int start = text.indexOf("<NE>");
        int end = text.indexOf("</NE>");

        return text.substring(start + 4, end).trim();
    }

    private String parsePassage(String text) {
        int start = text.indexOf("<TEXT>");
        int end = text.indexOf("</TEXT>");

        return text.substring(start + 6, end).trim();
    }
}
