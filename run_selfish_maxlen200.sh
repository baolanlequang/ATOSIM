#!/bin/bash
#SBATCH --job-name=atosim-selfish-maxlen200
#SBATCH --output=logs/atosim-selfish-maxlen200-%A_%a.out
#SBATCH --error=logs/atosim-selfish-maxlen200-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --array=0-999
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=8
#SBATCH --mem=32gb
#SBATCH --time=04:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# Full 7-param fANOVA with maxAllowedBlockchainLength=200.
# Sample every 100th row from the 100,000-row Cartesian-product CSV
# (500 system configs x 200 attacker configs) -> 1,000 runs covering
# all 500 system configs (2 attacker configs each) for good variation
# across all 7 LHS parameters. MaxArraySize on this cluster caps task
# IDs at 999, so 1,000 jobs (0-999) is the maximum per submission.
# numberOfMonteCarloRounds=100 (vs 500 production) to keep wall time
# manageable; maxLen=200 (vs 25 production) for reliable horizon.
ROW_INDEX=$(( SLURM_ARRAY_TASK_ID * 100 ))

mkdir -p results_new/selfish_maxlen200 logs

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
     --output-dir results_new/selfish_maxlen200
