CLASSPATH=$(JDBC_HOME)/classes/jconn4.jar:$(JDBC_HOME)/classes:.
JAVAC = $(JAVA_HOME)/bin/javac -classpath $(CLASSPATH)
CLASSDIR=classes

all:
	 cd $(JDBC_HOME); $(JAVAC) -d $(CLASSDIR) `ls sample2/*.java 
