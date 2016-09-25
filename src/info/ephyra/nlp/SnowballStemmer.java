package info.ephyra.nlp;

/**
 * This class provides an interface to the Snowball stemmer for the English
 * language.
 *
 * @author Nico Schlaefer
 * @version 2006-04-21
 */
public class SnowballStemmer {
    /**
     * Snowball stemmer for the English language.
     */
    private static final ThreadLocal<Stemmer> stemmer = new ThreadLocal<Stemmer>() {
        @Override
        protected Stemmer initialValue() {
            return new Stemmer();
        }
    };

    /**
     * Creates the stemmer.
     */
    public static void create() {
//		stemmer = new englishStemmer();
    }

    /**
     * Stems a single English word.
     *
     * @param word the word to be stemmed
     * @return stemmed word
     */
    public static String stem(String word) {
        stemmer.get().reset();
        char[] w = word.toCharArray();
        stemmer.get().add(w, w.length);
        stemmer.get().stem();
        return stemmer.get().toString();
    }

    /**
     * Stems all tokens in a string of space-delimited English words.
     *
     * @param tokens string of tokens to be stemmed
     * @return string of stemmed tokens
     */
    public static String stemAllTokens(String tokens) {
        String[] tokenArray = tokens.split(" ");
        String stemmed = "";

        if (tokenArray.length > 0) stemmed += stem(tokenArray[0]);
        for (int i = 1; i < tokenArray.length; i++)
            stemmed += " " + stem(tokenArray[i]);

        return stemmed;
    }
}
