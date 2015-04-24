@echo off
FOR /L %%i IN (1,1,10) DO (
copy input%%i.txt input.txt
echo --------------------input%%i.txt-------------------------
java -jar VkCupPlagiator.jar
echo ==Real solution==:
type res\0%%i.a
)
type res\10.a
pause