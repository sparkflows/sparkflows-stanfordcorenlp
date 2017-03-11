/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fire.nodes.stanfordnlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import fire.context.JobContext;
import fire.workflowengine.FireSchema;
import fire.workflowengine.Node;
import fire.workflowengine.Workflow;
import fire.workflowengine.WorkflowContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by jayantshekhar
 */
public class NodeStanfordNER extends Node implements Serializable {
    public String inputCol = "txt";
    public String outputCol = "ner";

    public NodeStanfordNER() {}

    public NodeStanfordNER(int i, String nm) {
        super(i, nm);
    }

    public NodeStanfordNER(int i, String nm, String inputCol, String outputCol) {
        super(i, nm);

        this.inputCol = inputCol;
        this.outputCol = outputCol;
    }

    //------------------------------------------------------------------------------------------------------

    @Override
    public void execute(JobContext jobContext) throws Exception {

        NERUDF nerudf = new NERUDF();
        jobContext.sqlctx().udf().register("ner", nerudf, DataTypes.StringType);
        dataFrame.registerTempTable("df");

        DataFrame results = jobContext.sqlctx().sql("SELECT *, ner("+inputCol+") as "+outputCol+" FROM df");

        //results.show();
        //results.printSchema();
        passDataFrameToNextNodesAndExecute(jobContext, results);
    }

    //------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------

    @Override
    public FireSchema getOutputSchema(Workflow workflow, FireSchema inputSchema) {

        inputSchema.addColumn(outputCol, FireSchema.Type.STRING, FireSchema.MLType.TEXT);

        return inputSchema;
    }

}

class NERUDF implements UDF1<String, String> {

    StanfordCoreNLPWrapper pipeline;

    NERUDF() {
        // creates a StanfordCoreNLP object,
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
        pipeline = new StanfordCoreNLPWrapper(props);
    }

    @Override
    public String call(String line) throws Exception {
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(line);

        // run all Annotators on this text
        pipeline.get().annotate(document);

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

        String allNer = "";

        // for NER:  not interested in unclassified / unknown things..
        String nerPreviousToken = "";
        String nerPhrase = "";

        for(CoreMap sentence: sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                //String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                //String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // this is the NER label of the token

                String word = token.get(CoreAnnotations.TextAnnotation.class);
                String posToken   = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);

                String nerToken = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);

                if (! nerToken.equals("O")) {
                    //System.out.println(" ner:" + nerToken);
                    nerPhrase = (nerPreviousToken.equalsIgnoreCase(nerToken)) ?  nerPhrase + " " + word : word;

                    allNer += nerToken + "," + nerPhrase + ":";
                }
            }
        }

        return allNer;
    }
}




