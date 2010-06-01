#!/bin/bash

#This launcher courtesy of the Eclipse Foundation


unset CLASSPATH; export CLASSPATH

#unset JAVA_HOME: export JAVA_HOME

if [ -x /usr/bin/zenity ]; then
    DIALOG=/usr/bin/zenity
    DIALOGW="$DIALOG --warning"
elif [ -x /usr/bin/kdialog ]; then
    DIALOG=/usr/bin/kdialog
    DIALOGW="$DIALOG --warningyesno"
elif [ -x /usr/bin/xdialog ]; then
    DIALOG=/usr/bin/xdialog
    DIALOGW="$DIALOG --warning"
else
    DIALOG=echo
    DIALOGW="$DIALOG"
fi




if [ -n "${JAVA_HOME}" ]; then
    echo "using specified vm: ${JAVA_HOME}"
    if [ ! -x "${JAVA_HOME}/bin/java" ]; then
        $DIALOG \
            --error \
            --title="Could not launch Golems" \
            --text="The custom VM you have chosen is not a valid executable."
        exit 1
    fi
fi

# If the user has not set JAVA_HOME, cycle through our list of compatible VM's
# and pick the first one that exists.
if [ -z "${JAVA_HOME}" -a ! -n "${JAVACMD}" ]; then
    echo "searching for compatible vm..."
    javahomelist=`cat "$(dirname "$0")/java_home"  | grep -v '^#' | grep -v '^$' | while read line ; do echo -n $line ; echo -n ":" ; done`
    OFS="$IFS"
    IFS=":"
    for JAVA_HOME in $javahomelist ; do
        echo -n "  testing ${JAVA_HOME}..."
        if [ -x "${JAVA_HOME}/bin/java" ]; then
            export JAVA_HOME
            echo "found"
            break
        else
            echo "not found"
        fi
    done
    IFS="$OFS"
fi



# If we don't have a JAVA_HOME yet, we're doomed.
if [ -z "${JAVA_HOME}" -a ! -n "${JAVACMD}" ]; then
    $DIALOG \
        --error \
        --title="Could not launch Golems" \
        --text="A suitable Java Virtual Machine could not be located."
    exit 1
fi


# Set JAVACMD from JAVA_HOME
if [ -n "${JAVA_HOME}" -a -z "${JAVACMD}" ]; then
    JAVACMD="$JAVA_HOME/bin/java"
fi


#exec "$JAVA_HOME/bin/java" -Xmx256m -XX:MaxDirectMemorySize=128m "-Djava.library.path=$(dirname $(readlink -f $0))/lib/native/" -jar "$(dirname $(readlink -f $0))/launcher.jar"
exec "$JAVA_HOME/bin/java"  -jar "$(dirname $(readlink -f $0))/launcher.jar"