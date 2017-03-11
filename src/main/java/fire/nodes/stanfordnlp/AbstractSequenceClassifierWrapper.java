package fire.nodes.stanfordnlp;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by jayant on 12/5/15.
 */
public class AbstractSequenceClassifierWrapper implements Serializable {

    public String classifier;

    public transient AbstractSequenceClassifier<CoreLabel> abstractSequenceClassifier = null;

    public AbstractSequenceClassifierWrapper(String classifier) {
        this.classifier = classifier;
    }

    public AbstractSequenceClassifier<CoreLabel> get() throws Exception {
        if (abstractSequenceClassifier == null) {
            String serializedClassifier = classifier;
            abstractSequenceClassifier = CRFClassifier.getClassifier(serializedClassifier);
        }

        return abstractSequenceClassifier;
    }
}
