.PHONY: default clean classes run

CLASSPATHS="./:components/:components/mail.jar"

default: classes

classes: components/*.java
	javac -cp ${CLASSPATHS} components/*.java

run:
	java -cp ${CLASSPATHS} components/EmailGUI

clean:
	rm -f components/*.class
