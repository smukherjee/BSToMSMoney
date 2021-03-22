echo WELCOME 

set $PATH=$PATH;$JAVA_HOME/bin;

SET CLASSPATH=.;%CLASSPATH%;../lib/htmlparser.jar

cd bin

java com/sujoy/RunParser CITIBANK  D:/CitibankSample.html

pause
 