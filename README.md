# Stanford Core NLP Wrapper for Sparkflows.io

Stanford Core NLP is available at : http://stanfordnlp.github.io/CoreNLP/

Stanford CoreNLP provides a set of natural language analysis tools. It can give the base forms of words, their parts of speech, whether they are names of companies, people, etc., normalize dates, times, and numeric quantities, mark up the structure of sentences in terms of phrases and word dependencies, indicate which noun phrases refer to the same entities, indicate sentiment, extract particular or open-class relations between entity mentions, get quotes people said, etc.


This repository contains implementation of wrappers for Stanford Core NLP for Sparkflows.io. This enables the Stanford CoreNLP building blocks/nodes to be visible in the Sparkflows.io workflow editor.

## Directory Contents

Below is the contents of the directory.

* fire-core-1.4.0.jar
    * fire core jar which contains the fire Nodes and Workflow engine
* data
    * sample data files
* src/main/java/fire/nodes/stanfordnlp/*.java
    * Implementation of wrapper for Stanford Core NLP NER, Sentence Detection, Sentiment Analysis etc.
* src/main/java/fire/workflows/stanfordnlp/*.java
    * example workflows which exercise the functionality
* pom.xml
    * for building the code
* README.md
    * this README file which provides the steps for building and deploying Stanford Core NLP in Sparkflows.io.

## Building

### Install the Fire jar to the local maven repository

Writing new Node depends on the Fire jar file. The Fire jar file provides the parent class for any new Node. Use the below commands to install the fire jar in your local maven repo.

    mvn install:install-file -Dfile=fire-core-1.4.0.jar -DgroupId=fire  -DartifactId=fire-core  -Dversion=1.4.0 -Dpackaging=jar
    
### Build with Maven

    mvn package

# Running the workflow on a Spark Cluster

Use the command below to load example data onto HDFS. It is then used by the example Workflow.

	hadoop fs -put data

Below is the command to execute the Sentiment Workflow on a Spark cluster. 

Executors with 1G and 1 vcore each have been specified in the commands. The parameter **'cluster'** specifies that we are running the workflow on a cluster as against locally.

	spark-submit --class fire.workflows.stanfordnlp.WorkflowStanfordNLPSentiment --master yarn-client --executor-memory 1G --executor-cores 1  target/sparkflows-stanfordcorenlp-1.4.0-jar-with-dependencies.jar cluster


## Jar files

Building this repo generates the following jar files:

	target/sparkflows-stanfordcorenlp-1.4.0.jar
	target/sparkflows-stanfordcorenlp-1.4.0-jar-with-dependencies.jar

The details for coding a New Node is here : https://github.com/sparkflows/writing-new-node/blob/master/CreatingNewNodes.md


## Having the Stanford Core NLP nodes appear in Sparkflows fire

New nodes written can be made visible in the Sparkflows UI. Thus, the users can start using them immediately.

* Copy the sparkflows-stanfordcorenlp-1.4.0-jar-with-dependencies.jar to fire-lib directory of the sparkflows install
* Copy the nodes/StanfordCoreNLP directory into the nodes directory of the sparkflows install
* Restart fire-ui


#### Running Stanford NLP in fire-ui

- Download the models file from :
    - http://stanfordnlp.github.io/CoreNLP/download.html
    - http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22stanford-corenlp%22

- Copy the models jar file into fire-lib directory of sparkflows

- Restart the sparkflows server

- When running the job on the cluster, include the model jars with --jars ...




