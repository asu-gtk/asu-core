# Developer & Agent Guidelines — ASU GTK Core Platform

## Project Overview
Modular Monolith for Open-Pit Mining Fleet Management and Simplex Optimization.
- Language: Java 21
- Framework: Spring Boot 3.3.3
- Build Tool: Maven
- Modules: `asu-common`, `asu-domain-mining`, `asu-math-engine`, `asu-telemetry-ingest`, `asu-reports-analytics`, `asu-api-server`

## Commands
- Build full project: `mvn clean package -DskipTests`
- Run test suite: `mvn clean test`
- Run API server: `mvn -pl asu-api-server spring-boot:run`

## Mathematical Model Rules
1. Always use `HaulageCostCalculator` for cost/fuel calculations.
2. Never round intermediate values before division or multiplication.
3. Unit tests in `asu-math-engine` must always pass.
