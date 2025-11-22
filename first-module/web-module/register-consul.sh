#!/bin/sh

CONSUL_URL="http://consul:8500"
SERVICE_NAME="organization-service"
SERVICE_ID="organization-service-1"
SERVICE_ADDRESS="wildfly"
SERVICE_PORT=8080

echo "Waiting for Consul to be available..."
while ! curl -s $CONSUL_URL/v1/status/leader >/dev/null; do
    echo "Waiting for Consul..."
    sleep 5
done

echo "Waiting for WildFly to be available..."
while ! curl -s http://$SERVICE_ADDRESS:$SERVICE_PORT/web-module/api/organizations/health >/dev/null; do
    echo "Waiting for WildFly..."
    sleep 5
done

cat <<EOF > /tmp/service.json
{
  "ID": "${SERVICE_ID}",
  "Name": "${SERVICE_NAME}",
  "Address": "${SERVICE_ADDRESS}",
  "Port": ${SERVICE_PORT},
  "Tags": ["wildfly", "ejb", "rest"],
  "Check": {
    "HTTP": "http://${SERVICE_ADDRESS}:${SERVICE_PORT}/web-module/api/organizations/health",
    "Interval": "10s",
    "Timeout": "5s",
    "DeregisterCriticalServiceAfter": "30s"
  }
}
EOF

echo "Registering service in Consul..."
curl -X PUT --data @/tmp/service.json $CONSUL_URL/v1/agent/service/register

if [ $? -eq 0 ]; then
    echo "Service successfully registered in Consul"
else
    echo "Failed to register service in Consul"
    exit 1
fi

echo "Service registration maintained. Press Ctrl+C to exit."
tail -f /dev/null