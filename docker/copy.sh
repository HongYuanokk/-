#!/bin/sh

# 复制项目的文件到对应docker路径，便于一键生成镜像。
usage() {
	echo "Usage: sh copy.sh"
	exit 1
}


# copy sql
echo "begin copy sql "
cp ../sql/ry_20230706.sql ./mysql/db
cp ../sql/ry_config_20220929.sql ./mysql/db

# copy html
echo "begin copy html "
cp -r ../spzx-ui/dist/** ./nginx/html/dist


# copy jar
echo "begin copy spzx-gateway "
cp ../spzx-gateway/target/spzx-gateway.jar ./spzx/gateway/jar

echo "begin copy spzx-auth "
cp ../spzx-auth/target/spzx-auth.jar ./spzx/auth/jar

echo "begin copy spzx-visual "
cp ../spzx-visual/spzx-monitor/target/spzx-visual-monitor.jar  ./spzx/visual/monitor/jar

echo "begin copy spzx-modules-system "
cp ../spzx-modules/spzx-system/target/spzx-modules-system.jar ./spzx/modules/system/jar

echo "begin copy spzx-modules-file "
cp ../spzx-modules/spzx-file/target/spzx-modules-file.jar ./spzx/modules/file/jar

echo "begin copy spzx-modules-job "
cp ../spzx-modules/spzx-job/target/spzx-modules-job.jar ./spzx/modules/job/jar

echo "begin copy spzx-modules-gen "
cp ../spzx-modules/spzx-gen/target/spzx-modules-gen.jar ./spzx/modules/gen/jar

