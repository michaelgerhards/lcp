echo "start"
date

cd \gitlab\scheduling\workspace\DynamicSchedulingM\target\

java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\sipht10000_75_lcp_00.xml
echo "--------------------------------------------------------------------------------------------"
java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\sipht10000_75_lcp_03.xml
echo "--------------------------------------------------------------------------------------------"
java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\sipht10000_75_lcp_05.xml
echo "--------------------------------------------------------------------------------------------"
java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\sipht10000_75_lcp_07.xml
echo "--------------------------------------------------------------------------------------------"
java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\sipht10000_75_lcp_10.xml
echo "--------------------------------------------------------------------------------------------"

echo "############################################################################################"

echo "end"
date

$exit = Read-Host -Prompt 'Input to exit'