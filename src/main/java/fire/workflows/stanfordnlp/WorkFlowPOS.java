package fire.workflows.stanfordnlp;

import fire.context.JobContext;
import fire.context.JobContextImpl;
import fire.nodes.dataset.NodeDatasetTextFiles;
import fire.nodes.stanfordnlp.NodeStanfordLemmatization;
import fire.nodes.stanfordnlp.NodeStanfordPOS;
import fire.nodes.util.NodePrintFirstNRows;
import fire.spark.CreateSparkContext;
import fire.workflowengine.ConsoleWorkflowContext;
import fire.workflowengine.Workflow;
import fire.workflowengine.WorkflowContext;
import org.apache.spark.api.java.JavaSparkContext;

public class WorkFlowPOS {

    //--------------------------------------------------------------------------------------

    public static void main(String[] args) throws Exception {

        // create spark context
        JavaSparkContext ctx = CreateSparkContext.create(args);
        // create workflow context
        WorkflowContext workflowContext = new ConsoleWorkflowContext();
        // create job context
        JobContext jobContext = new JobContextImpl(ctx, workflowContext);

        ner(jobContext);

        // stop the context
        ctx.stop();
    }


    //--------------------------------------------------------------------------------------

    // NER workflow
    private static void ner(JobContext jobContext) throws Exception {

        Workflow wf = new Workflow();

        // csv1 node
        NodeDatasetTextFiles txt = new NodeDatasetTextFiles(1, "text node", "data/nlp/lemmatization");
        wf.addNode(txt);

        // ner node
        NodeStanfordPOS lem = new NodeStanfordPOS(2, "pos node");
        lem.setInputCol("txt");
        wf.addLink(txt, lem);

        // print rows
        NodePrintFirstNRows npr = new NodePrintFirstNRows(3, "npr", 10);
        wf.addLink(lem, npr);

        // execute the workflow
        wf.execute(jobContext);

    }

}
