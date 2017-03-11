package fire.nodes.stanfordnlp;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by jayant on 12/5/15.
 */
public class StanfordCoreNLPWrapper implements Serializable {

    Properties props;
    public transient StanfordCoreNLP pipeline = null;

    public StanfordCoreNLPWrapper(Properties p) {
        props = p;
    }

    public StanfordCoreNLP get() {
        if (pipeline == null) {
            pipeline = new StanfordCoreNLP(props);
        }

        return pipeline;
    }
}
