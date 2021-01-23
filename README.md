This repository is outdated and will be kept online for log purposes only.
Starting from the date of this commit, the project will continue at https://github.com/douglas444/active-pattern-learning

# data-stream-env
Strategies based on active learning and semi supervised learning for classification, novelty detection and concept drift detection over data streams.

# compilation

mvn clean compile

# execution

Executing MINAS
`mvn exec:java -Dexec.mainClass=br.com.douglas444.datastreamenv.strategy1.MINAS -Dexec.args=""`

Executing AnyNovel
`mvn exec:java -Dexec.mainClass=br.com.douglas444.datastreamenv.strategy1.AnyNovel -Dexec.args=""`

Executing ECHO
`mvn exec:java -Dexec.mainClass=br.com.douglas444.datastreamenv.strategy1.ECHO -Dexec.args=""`

The arguments used in the experiments can be found at minas.txt, anynovel.txt and echo.txt.
Each line refers to the arguments of one experiment. 
Before executing the program, select the line and insert in `-Dexec.args="""`.

Example:

`mvn exec:java -Dexec.mainClass=br.com.douglas444.datastreamenv.strategy1.MINAS -Dexec.args="./config/minas_fcTe.xml ./dataset/minas/fcTe.csv"`
