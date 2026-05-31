#!/bin/bash
#SBATCH --job-name=atosim-selfish
#SBATCH --partition=cpu
#SBATCH --time=24:00:00
#SBATCH --nodes=1
#SBATCH --ntasks-per-node=1
#SBATCH --cpus-per-task=48
#SBATCH --mem=64000
#SBATCH --output=logs/atosim-selfish-%j.out
#SBATCH --error=logs/atosim-selfish-%j.err

# bwUniCluster 3.0 batch job — see https://wiki.bwhpc.de/e/BwUniCluster3.0/Running_Jobs
# cpu partition: 2x48-core AMD nodes, max 72h, max 380000 MB/node, 96 tasks/node.

set -euo pipefail

mkdir -p logs

java -Xms8g -Xmx56g -jar atosim.jar \
     simulator/org.palladiosimulator.blockchainsystems.atomsim/optimized_deterministic_lhs_configurations.csv \
     simulator/org.palladiosimulator.blockchainsystems.atomsim/testmodels \
     simulator/org.palladiosimulator.blockchainsystems.atomsim/testmodels/configuration.json
