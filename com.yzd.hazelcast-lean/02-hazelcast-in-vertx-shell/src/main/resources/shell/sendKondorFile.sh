#!/bin/sh

#日志文件的位置
logFile="/opt/fms2_kondor/sendKondorFile.log"

#Kondor系统的IP地址，会将生成的文件发送到这个地址
kondor_ip=192.168.1.200

#FTP用户名
ftp_username=kondor

#FTP密码
ftp_password=kondor

#要发送的文件的绝对路径
filePath=""

#要发送的文件的文件名
fileName=""

#如果Shell命令带有参数，则将第一个参数赋给filePath，将第二个参数赋给fileName
if [ $# -ge "1" ]
then
filePath=$1
else
echo "没有文件路径"
echo "没有文件路径\n" >
>
$logFile
return
fi

if [ $# -ge "2" ]
then
fileName=$2
else
echo "没有文件名"
echo "没有文件名\n" >
>
$logFile
return
fi

echo "要发送的文件是 ${filePath}/${fileName}"

cd ${filePath}
ls $fileName
if (test $? -eq 0)
then
echo "准备发送文件：${filePath}/${fileName}"
else
echo "文件 ${filePath}/${fileName} 不存在"
echo "文件 ${filePath}/${fileName} 不存在\n" >
>
$logFile
return
fi

ftp -n ${kondor_ip} <
<
_end
user ${ftp_username} ${ftp_password}
asc
prompt
put $fileName
bye
_end

echo "`date +%Y-%m-%d' '%H:%M:%S` 发送了文件 ${filePath}/${fileName}"
echo "`date +%Y-%m-%d' '%H:%M:%S` 发送了文件 ${filePath}/${fileName}\n" >
>
$logFile