import com.sun.btrace.BTraceUtils;
import com.sun.btrace.aggregation.Aggregation;
import com.sun.btrace.aggregation.AggregationFunction;
import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.Duration;
import com.sun.btrace.annotations.Kind;
import com.sun.btrace.annotations.Location;
import com.sun.btrace.annotations.OnEvent;
import com.sun.btrace.annotations.OnExit;
import com.sun.btrace.annotations.OnMethod;

import static com.sun.btrace.BTraceUtils.println;
import static com.sun.btrace.BTraceUtils.str;
import static com.sun.btrace.BTraceUtils.strcat;

/**
 * Created by qqcao on 9/25/16.
 *
 * Tracing qa process script
 */
@BTrace
public class QaTrace {
//    private static Aggregation histogram = BTraceUtils.Aggregations.newAggregation(AggregationFunction.QUANTIZE);
//    private static Aggregation average = BTraceUtils.Aggregations.newAggregation(AggregationFunction.AVERAGE);
//    private static Aggregation max = BTraceUtils.Aggregations.newAggregation(AggregationFunction.MAXIMUM);
//    private static Aggregation min = BTraceUtils.Aggregations.newAggregation(AggregationFunction.MINIMUM);

    private static Aggregation allQaStemSum = BTraceUtils.Aggregations.newAggregation(AggregationFunction.SUM);
    private static Aggregation allQaCrfSum = BTraceUtils.Aggregations.newAggregation(AggregationFunction.SUM);
    private static Aggregation allQaRegexSum = BTraceUtils.Aggregations.newAggregation(AggregationFunction.SUM);
    private static Aggregation allQaSearchSum = BTraceUtils.Aggregations.newAggregation(AggregationFunction.SUM);
    private static Aggregation allQaSum = BTraceUtils.Aggregations.newAggregation(AggregationFunction.SUM);
    private static Aggregation allQaCount = BTraceUtils.Aggregations.newAggregation(AggregationFunction.COUNT);


//    private static Aggregation oneQaStemSum = BTraceUtils.Aggregations.newAggregation(AggregationFunction.SUM);
//    private static AggregationKey stemKey = BTraceUtils.Aggregations.newAggregationKey(oneQaStemSum);

    @OnMethod(
            clazz = "info.ephyra.nlp.SnowballStemmer",
            method = "stem",
            location = @Location(Kind.RETURN)
    )
    public static void stem(@Duration long duration) {
        BTraceUtils.Aggregations.addToAggregation(allQaStemSum, duration);
    }

    @OnMethod(
            clazz = "info.ephyra.nlp.StanfordNeTagger",
            method = "extractNEs",
            location = @Location(Kind.RETURN)
    )
    public static void crf(@Duration long duration) {
        BTraceUtils.Aggregations.addToAggregation(allQaCrfSum, duration);
    }

    @OnMethod(
            clazz = "info.ephyra.nlp.RegExMatcher",
            method = "/[extractAllMatches|markAllMatches|extractAllContained].*/",
            location = @Location(Kind.RETURN)
    )
    public static void regex(@Duration long duration) {
        BTraceUtils.Aggregations.addToAggregation(allQaRegexSum, duration);
    }

    @OnMethod(
            clazz = "info.ephyra.search.searchers.IndriKM",
            method = "doSearch",
            location = @Location(Kind.RETURN)
    )
    public static void search(@Duration long duration) {
        BTraceUtils.Aggregations.addToAggregation(allQaSearchSum, duration);
    }

    @OnMethod(
            clazz = "info.ephyra.OpenEphyraServer",
            method = "handle",
            location = @Location(Kind.RETURN)
    )
    public static void qa(@Duration long duration) {
        BTraceUtils.Aggregations.addToAggregation(allQaSum, duration);
        BTraceUtils.Aggregations.addToAggregation(allQaCount, duration);
        println(strcat("Heap: ",str(BTraceUtils.Sys.Memory.heapUsage())));
        println(strcat("Non-Heap: ",str(BTraceUtils.Sys.Memory.nonHeapUsage())));
        println("---------------------------------------------");
        BTraceUtils.Aggregations.printAggregation("Stem Sum", allQaStemSum);
        BTraceUtils.Aggregations.printAggregation("CRF Sum", allQaCrfSum);
        BTraceUtils.Aggregations.printAggregation("Regex Sum", allQaRegexSum);
        BTraceUtils.Aggregations.printAggregation("Search Sum", allQaSearchSum);
        BTraceUtils.Aggregations.printAggregation("QA Count", allQaCount);
        BTraceUtils.Aggregations.printAggregation("QA Sum", allQaSum);
        println("---------------------------------------------");

    }

    @OnEvent
    public static void onEvent() {
        // Top 10 queries only
//        BTraceUtils.Aggregations.truncateAggregation(histogram, 10);

    }

    @OnExit
    public static void onExit(int code) {
        println("BTrace program exits!");
    }
}
