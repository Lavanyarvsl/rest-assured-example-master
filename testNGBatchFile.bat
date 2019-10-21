projectLocation=D:\workSpaAdmin\rest-assured-example-master
cd %projectLocation%
set classpath=%projectLocation%\bin;%projectLocation%\lib\*
cd /d "%projectLocation%"
java org.testng.TestNG %projectLocation%\testng.xml
pause