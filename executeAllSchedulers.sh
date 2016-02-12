for i in naive antiAffinity nextFit worstFit noViolations energy greedy
do
	echo $i
	mvn compile exec:java -Dsched=naive -Dday=all
	echo "--------------------------"
done
