@echo OFF

IF "%1"=="js" (%2 -I=%3 --js_out=%4 %5)

IF "%1"=="java" (%2 -I=%3 --java_out=%4 %5)
