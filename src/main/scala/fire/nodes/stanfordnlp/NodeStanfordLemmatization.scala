package fire.nodes.stanfordnlp

import fire.context.JobContext
import fire.util.NLPFunctions
import fire.workflowengine.{FireSchema, Workflow, Node}
import org.apache.spark.sql.functions.col
import scala.beans.BeanProperty

class NodeStanfordLemmatization extends Node with Serializable{

  @BeanProperty var inputCol = ""
  @BeanProperty var outputCol = "lemm"
  def this(i: Int, nm: String) {
    this()
    id = i;
    name = nm;
  }

  def this(i: Int, nm: String, in :String, out: String) {
    this()
    id = i;
    name = nm;
    inputCol = in;
    outputCol = out;
  }


  @Override
  override def execute (jobContext : JobContext) {

    val sqlContext = jobContext.sqlctx()
    import sqlContext.implicits._

    val  outDf= dataFrame.withColumn(outputCol, NLPFunctions.lemmas(col(inputCol)))

    outDf.printSchema()

    passDataFrameToNextNodesAndExecute(jobContext, outDf)

  }

  override def getOutputSchema(workflow: Workflow, inputSchema: FireSchema): FireSchema = {

    inputSchema.addColumn(outputCol , FireSchema.Type.ARRAY, FireSchema.MLType.CATEGORICAL)

    return inputSchema
  }

}
