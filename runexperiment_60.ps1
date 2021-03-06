echo "start"
date

cd \gitlab\scheduling\workspace\DynamicSchedulingM\target\
java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\epi10000_60_lcp.xml
echo "--------------------------------------------------------------------------------------------"
java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\epi10000_60_pcp.xml
echo "--------------------------------------------------------------------------------------------"
java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\epi10000_60_spss.xml
echo "--------------------------------------------------------------------------------------------"

echo "############################################################################################"

java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\inspi10000_60_lcp.xml
echo "--------------------------------------------------------------------------------------------"
java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\inspi10000_60_pcp.xml
echo "--------------------------------------------------------------------------------------------"
java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\inspi10000_60_spss.xml
echo "--------------------------------------------------------------------------------------------"

echo "############################################################################################"

java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\sipht10000_60_lcp.xml
echo "--------------------------------------------------------------------------------------------"
java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\sipht10000_60_pcp.xml
echo "--------------------------------------------------------------------------------------------"
java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\sipht10000_60_spss.xml
echo "--------------------------------------------------------------------------------------------"

echo "############################################################################################"

java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\cyber10000_60_lcp.xml
echo "--------------------------------------------------------------------------------------------"
java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\cyber10000_60_pcp.xml
echo "--------------------------------------------------------------------------------------------"
java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\cyber10000_60_spss.xml
echo "--------------------------------------------------------------------------------------------"

echo "############################################################################################"

java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\montage10000_60_lcp.xml
echo "--------------------------------------------------------------------------------------------"
java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\montage10000_60_pcp.xml
echo "--------------------------------------------------------------------------------------------"
java -Xmx12g -cp .\DynamicSchedulingM-1.0-SNAPSHOT-jar-with-dependencies.jar dynamic.main.ScheduleFrameworkMain \gitlab\scheduling\data\config\paper\montage10000_60_spss.xml
echo "--------------------------------------------------------------------------------------------"

echo "############################################################################################"

echo "end"
date

$exit = Read-Host -Prompt 'Input to exit'