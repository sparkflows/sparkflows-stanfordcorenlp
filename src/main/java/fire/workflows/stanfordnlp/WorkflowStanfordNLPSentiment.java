package fire.workflows.stanfordnlp;

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

import fire.context.JobContext;
import fire.context.JobContextImpl;
import fire.nodes.dataset.NodeDatasetTextFiles;
import fire.nodes.stanfordnlp.NodeStanfordNLPSentiment;
import fire.nodes.util.NodePrintFirstNRows;
import fire.spark.CreateSparkContext;
import fire.workflowengine.ConsoleWorkflowContext;
import fire.workflowengine.Workflow;
import fire.workflowengine.WorkflowContext;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Created by jayantshekhar
 */
public class WorkflowStanfordNLPSentiment {

    //--------------------------------------------------------------------------------------

    public static void main(String[] args) throws Exception {

        // create spark context
        JavaSparkContext ctx = CreateSparkContext.create(args);
        // create workflow context
        WorkflowContext workflowContext = new ConsoleWorkflowContext();
        // create job context
        JobContext jobContext = new JobContextImpl(ctx, workflowContext);

        sentiment(jobContext);

        // stop the context
        ctx.stop();
    }


    //--------------------------------------------------------------------------------------

    // sentiment workflow
    private static void sentiment(JobContext jobContext) throws Exception {

        Workflow wf = new Workflow();

        // csv1 node
        NodeDatasetTextFiles txt = new NodeDatasetTextFiles(1, "text node", "data/nlp/sentiment.txt");
        wf.addNode(txt);

        // sentiment node
        NodeStanfordNLPSentiment sentiment = new NodeStanfordNLPSentiment(2, "sentiment node", "txt", "sentiment");
        wf.addLink(txt, sentiment);

        NodePrintFirstNRows npr = new NodePrintFirstNRows(3, "npr", 100);
        wf.addLink(sentiment, npr);

        // execute the workflow
        wf.execute(jobContext);

    }

}
