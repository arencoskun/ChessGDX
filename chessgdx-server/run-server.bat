@echo OFF

if [%2]==[--port] (start cmd /k node %1 --port=%3) else (echo You must enter the port using the --port argument. ^(--port 1234^))
if [%4]==[--ngrok] (if DEFINED [%5] (start cmd /k "ngrok http --domain=%5 http://localhost:3131") else (echo You must enter the domain. && :EXIT))

:EXIT
timeout /t 1
taskkill /f /im node.exe
