#!/bin/bash
#SBATCH --job-name=atosim-selfish-pilot
#SBATCH --output=logs/atosim-selfish-pilot-%A_%a.out
#SBATCH --error=logs/atosim-selfish-pilot-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-199
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=32gb
#SBATCH --time=04:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# Pilot test: maxAllowedBlockchainLength=200 (vs 25 in production) to check
# whether the selfish-mining excess (revenue - fair_share) still shrinks with
# attacker_hash_power when the simulation horizon is much longer.
# numberOfMonteCarloRounds=100 (vs 500) to keep wall time manageable.
# Rows 0-199 cover all 200 attacker configs (full alpha range 0.2-0.5) for
# system_config_id=1 (validator_count=85, node_degree=5) -- fixed topology so
# the alpha effect is isolated without topology confounding.

ROW_INDEX=${SLURM_ARRAY_TASK_ID}

mkdir -p results_new/selfish_pilot logs

java -Xmx24G \
     -XX:+UseG1GC \
     -XX:ParallelGCThreads=8 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=heapdump_${SLURM_JOB_ID}_${SLURM_ARRAY_TASK_ID}.hprof \
     -jar atosim.jar \
     sampling/run_configurations_selfish.csv \
     sampling/generated_models \
     simulator/org.palladiosimulator.blockchainsystems.atosim/testmodels/configuration_pilot.json \
     --row-index ${ROW_INDEX} \
     --output-dir results_new/selfish_pilot
