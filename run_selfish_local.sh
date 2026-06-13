#!/bin/bash
java -Xmx12g -XX:+UseG1GC -jar atosim.jar \
     simulator/org.palladiosimulator.blockchainsystems.atosim/optimized_deterministic_lhs_configurations.csv \
     sampling/generated_models \
     simulator/org.palladiosimulator.blockchainsystems.atosim/testmodels/configuration.json \
     --output-dir results_local/selfish

     

