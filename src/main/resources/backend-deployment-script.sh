#!/bin/bash

jar_name="ggt.jar"
final_jar_name="ggt-final.jar"
service_name="ggt"  # Replace with the actual service name
timestamp=$(date +%Y-%m-%d__%H-%M-%S)
total_checks=30


# 1.
echo "Stopping the service: $service_name"
service_status=$(systemctl is-active $service_name)
if [ "$service_status" = "active" ]; then
       sudo systemctl stop $service_name
       for ((i=0; i<total_checks; i++)); do
               service_status=$(systemctl is-active $service_name)
               echo "Service status: $service_status"
               if [ "$service_status" = "inactive" ]; then
                       service_stopped_flag=true
                       break
               fi
               sleep 1  # Wait for 1 second before the next check
       done
       echo "Service: $service_name STOP flag = $service_stopped_flag"
else
       echo "Service $service_name is already stopped"
fi



# 2.
echo "Backup.."
if [ -e /opt/ajaxer-org/springboot/"$jar_name" ]; then
  mv /opt/ajaxer-org/springboot/"$jar_name" /opt/ajaxer-org/springboot/"$jar_name"_"$timestamp"
else
  echo "File does not exist."
fi
sleep 1


# 3.
echo "Copy.."
cp /tmp/github-actions/greedy-ghost/"$final_jar_name" /opt/ajaxer-org/springboot/"$jar_name"
sleep 1



# 4.
echo "Deleting older backup files"
cd /opt/ajaxer-org/springboot
ls -1 | grep "${jar_name}_" | sort -n | head -n -3 | xargs rm -vf
sleep 1



# 5.
echo "Starting the service: $service_name"
sudo systemctl start $service_name

for ((i=0; i<total_checks; i++)); do
  service_status=$(systemctl is-active $service_name)
       sleep 1  # Wait for 1 second before the next check
       echo "Service status: $service_status"
       if [ "$service_status" = "active" ]; then
               service_start_flag=true
               break
       fi
done

echo "Service: $service_name START flag = $service_start_flag"