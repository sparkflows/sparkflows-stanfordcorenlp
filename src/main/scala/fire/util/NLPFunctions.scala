package fire.util

import java.util.Properties

import edu.stanford.nlp.ie.machinereading.structure.MachineReadingAnnotations.RelationMentionsAnnotation
import edu.stanford.nlp.ling.CoreAnnotations._
import edu.stanford.nlp.pipeline.{Annotation, RelationExtractorAnnotator, StanfordCoreNLP}
import org.apache.spark.sql.functions.udf

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

object NLPFunctions {

  val lemmas = udf{(text: String ) =>
    val props = new Properties()
    props.put("annotators", "tokenize, ssplit, pos, lemma")
    val pipeline = new StanfordCoreNLP(props)

    val doc = new Annotation(text)
    pipeline.annotate(doc)

    val lemmas = new ArrayBuffer[String]()

    val sentences = doc.get(classOf[SentencesAnnotation])
    for (sentence <- sentences){
      for(token <- sentence.get(classOf[TokensAnnotation])){
           val lemma = token.get(classOf[LemmaAnnotation])
             lemmas += lemma
       }
    }
    lemmas.toList
  }


  val pos = udf{(text: String) =>

    val props: Properties = new Properties
    props.put("annotators", "tokenize, ssplit, pos")

    val pipeline = new StanfordCoreNLP(props)

    val doc = new Annotation(text)
    pipeline.annotate(doc)

    val poss = new ArrayBuffer[String]()

    val sentences = doc.get(classOf[SentencesAnnotation])
    for (sentence <- sentences){
      for(token <- sentence.get(classOf[TokensAnnotation])){
        val po = token.get(classOf[PartOfSpeechAnnotation])
        poss += po
      }
    }
    poss.toList
  }

  //  No annotator named depparse depparse & natlog
  val natlog = udf{(text:String ) =>

    val props: Properties = new Properties
    props.put("annotators", "tokenize, ssplit, depparse, natlog")

    val pipeline = new StanfordCoreNLP(props)

    val doc = new Annotation(text)
    pipeline.annotate(doc)


    val natlogs = new ArrayBuffer[String]()

    val sentences = doc.get(classOf[SentencesAnnotation])
    for (sentence <- sentences){
      for(token <- sentence.get(classOf[TokensAnnotation])){
        val nat = token.get(classOf[PolarityAnnotation])
        natlogs += nat
      }
    }
    natlogs.toList
  }

 // check stanford 3.6 for OpenIE
  val  OpenIE = udf{(text: String)=>

    val props = new Properties()
    props.put("annotators", "tokenize,ssplit,pos,lemma,depparse,parse,natlog,openie")
    val pipeline = new StanfordCoreNLP(props)

    val doc = new Annotation(text)
    pipeline.annotate(doc)

   val res = new ArrayBuffer[String]()
   res
  }

  val relationExtractor = udf{(text: String) =>

    val props = new Properties()
    props.put("annotators", "tokenize, ssplit, pos, lemma, parse, ner")
    val pipeline = new StanfordCoreNLP(props)

    val doc = new Annotation(text)
    pipeline.annotate(doc)

    val r = new RelationExtractorAnnotator(props)
    r.annotate(doc)

    val res = new ArrayBuffer[String]()

    val sentences = doc.get(classOf[SentencesAnnotation])
    for (sentence <- sentences){
      val rls = sentence.get(classOf[RelationMentionsAnnotation])
      for( rl <- rls){
        println(rl.toString())
      }
    }
    res
  }

}
