#!/bin/zsh
# Deploy Vacation to hz1.rebbi.is (svakaparty.com).
#
# The JavaMonitor password is NOT stored in the repo (it's public) — put it in
# .deploy.env (gitignored) as: JM_PW=...
set -euo pipefail

cd "$(dirname "$0")"

SERVER="root@hz1.rebbi.is"
DEPLOY_DIR="/rebbi/com.svakaparty/wo"
JM="https://jm.rebbi.is/Apps/WebObjects/JavaMonitor.woa/admin"
APP="Vacation"

if [ -f .deploy.env ]; then
	source .deploy.env
fi

if [ -z "${JM_PW:-}" ]; then
	echo "JM_PW not set — put 'JM_PW=...' in .deploy.env or the environment" >&2
	exit 1
fi

echo "== Building"
mvn clean package -Dlaunch.jvm=/opt/jdk-26/bin/java

echo "== Moving the old .woa aside (kept as $APP.woa.previous for rollback)"
ssh "$SERVER" "rm -rf $DEPLOY_DIR/$APP.woa.previous; [ -d $DEPLOY_DIR/$APP.woa ] && mv $DEPLOY_DIR/$APP.woa $DEPLOY_DIR/$APP.woa.previous || true"

echo "== Copying new .woa to $SERVER:$DEPLOY_DIR"
scp -rq "target/$APP.woa" "$SERVER:$DEPLOY_DIR/"

echo "== Setting ownership"
ssh "$SERVER" "chown -R webobjects:webobjects $DEPLOY_DIR/$APP.woa"

echo "== Restarting via JavaMonitor"
curl -s "$JM/stop?type=app&name=$APP&pw=$JM_PW"
sleep 5
curl -s "$JM/start?type=app&name=$APP&pw=$JM_PW"

echo
echo "== Waiting for the app to answer"
for i in {1..12}; do
	sleep 5
	http_status=$(curl -s -o /dev/null -w '%{http_code}' --max-time 10 https://www.svakaparty.com/ || true)
	if [ "$http_status" = "200" ]; then
		echo "svakaparty.com is up (HTTP 200)"
		exit 0
	fi
	echo "  ...not yet (HTTP $http_status)"
done

echo "App did not come up within a minute — check JavaMonitor" >&2
exit 1
