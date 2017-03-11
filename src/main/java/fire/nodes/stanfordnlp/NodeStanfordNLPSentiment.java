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
import java.util.Properties;
import java.util.Set;

/**
 * Created by jayantshekhar
 */
public class NodeStanfordNLPSentiment extends Node implements Serializable {
    //public String posModel = "edu/stanford/nlp/models/pos-tagger/wsj-bidirectional/wsj-0-18-bidirectional-distsim.tagger";
    public String inputCol = "txt";
    public String outputCol = "sentiment";

    public NodeStanfordNLPSentiment() {}

    public NodeStanfordNLPSentiment(int i, String nm) {
        super(i, nm);
    }

    public NodeStanfordNLPSentiment(int i, String nm, String inputCol, String outputCol) {
        super(i, nm);

        this.inputCol = inputCol;
        this.outputCol = outputCol;
    }

    //------------------------------------------------------------------------------------------------------

    @Override
    public void execute(JobContext jobContext) throws Exception {

        //ctx.broadcast(loadStopWords("stopwords.txt")).value

        SentimentUDF sentimentUDF = new SentimentUDF();
        jobContext.sqlctx().udf().register("sentiment", sentimentUDF, DataTypes.IntegerType);
        dataFrame.registerTempTable("df");

        DataFrame results = jobContext.sqlctx().sql("SELECT *, sentiment("+inputCol+") as "+outputCol+" FROM df");

        passDataFrameToNextNodesAndExecute(jobContext, results);
    }

    /***
    public String findSentiment(String line) {

        // properties
        Properties props = new Properties();

            // http://stackoverflow.com/questions/14735212/specifying-a-path-to-models-for-stanfordcorenlp
        props.put("pos.model", posModel);
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");

        // create stanford core nlp
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        int mainSentiment = 0;
        if (line != null && line.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(line);
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();
                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }

            }
        }

        if (mainSentiment == 2 || mainSentiment > 4 || mainSentiment < 0) {
            return null;
        }
        //TweetWithSentiment tweetWithSentiment = new TweetWithSentiment(line, toCss(mainSentiment));
        return ""+mainSentiment;

    }
     ***/

    //------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------

    @Override
    public FireSchema getOutputSchema(Workflow workflow, FireSchema inputSchema) {

        inputSchema.addColumn(outputCol, FireSchema.Type.STRING, FireSchema.MLType.TEXT);

        return inputSchema;
    }

}

class SentimentUDF implements UDF1<String, Integer> {

    StanfordCoreNLPWrapper pipeline;

    SentimentUDF() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        pipeline = new StanfordCoreNLPWrapper(props);
    }

    @Override
    public Integer call(String line) throws Exception {
        int mainSentiment = 0;
        if (line != null && line.length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.get().process(line);
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);

                String partText = sentence.toString();
                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }

            }
        }
        if (mainSentiment == 2 || mainSentiment > 4 || mainSentiment < 0) {
            //return null;
        }
        //TweetWithSentiment tweetWithSentiment = new TweetWithSentiment(line, toCss(mainSentiment));
        return mainSentiment;
    }
}




