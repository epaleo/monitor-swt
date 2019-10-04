# Monitor SWT

Get Widget SWT information in console. 
Move mouse over a component and press **Ctrl+Shift+A** to get information in console.

## Getting started

1 Add jar and pom from release to your local repository (because the release isn't in maven central)

2 Add dependency to your **pom.xml** project in your debug profile. For example: oficina.
```
<profiles>
    <profile>
        <id>debug</id>
        <dependencies>
            <dependency>
                <groupId>com.inditex.swt</groupId>
                <artifactId>monitor-swt</artifactId>
                <version>1.0.0</version>
            </dependency>
        </dependencies>
    </profile>
</profiles>
```
3 Add new log to logback
```
<logger name="com.inditex.swt" level="DEBUG"/>
```
4 Create a java launcher and add **javaagent** in VM options of the launcher. Check that you have the **${M2}** variable environment.
```
-javaagent:${M2}/repository/org/aspectj/aspectjweaver/1.9.4/aspectjweaver-1.9.4.jar
```
5 Execute mvn install with the profile
```
mvn -U clean install -P oficina
```

6 Execute launcher in your IDE

7 Now you can move your cursor over a component in your application and press **Ctr+Shift+A**. If everything is ok, you will get a message with information in your console:
```
[DEBUG] 2019-09-11 22:42:37.818 [main] MonitorWidgets - ######## CURSOR ON COMPONENT ########
[DEBUG] 2019-09-11 22:42:37.823 [main] MonitorWidgets - class org.eclipse.swt.widgets.Shell
[DEBUG] 2019-09-11 22:42:37.824 [main] MonitorWidgets - 	class org.eclipse.swt.widgets.Button -> string Button {Button 3}
[DEBUG] 2019-09-11 22:42:37.824 [main] MonitorWidgets - #####################################
```
