package fire.util

import java.util.Properties

import edu.stanford.nlp.ling.CoreAnnotations._
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions.{udf, _}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer


object NLPTest {


  def main(args : Array[String]): Unit = {

    val sparkConf = new SparkConf()
    sparkConf.setMaster("local[2]")
    sparkConf.setAppName("NLP")

    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._

    val natlog = udf {(text:String ) =>

      val props: Properties = new Properties
      props.put("annotators", "tokenize, ssplit, pos")

      val pipeline = new StanfordCoreNLP(props)

      val doc = new Annotation(text)
      pipeline.annotate(doc)

      val res = new ArrayBuffer[String]()

      val poss = new ArrayBuffer[String]()

      val sentences = doc.get(classOf[SentencesAnnotation])
      for (sentence <- sentences){
        for(token <- sentence.get(classOf[TokensAnnotation])){
          val po = token.get(classOf[PolarityAnnotation])
          poss += po
        }
      }
      poss.toList
    }

    val input = sc.parallelize(Seq(
      ("Barack Obama lives in America. Obama works for the Federal Goverment")
    )).toDF("text")
    input.show()
    input.select(col("text"), natlog(col("text"))).show()

  }

}