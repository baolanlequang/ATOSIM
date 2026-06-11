#!/bin/bash
#SBATCH --job-name=atosim-selfish
#SBATCH --output=logs/atosim-selfish-%A_%a.out
#SBATCH --error=logs/atosim-selfish-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=48
#SBATCH --mem=350gb
#SBATCH --time=72:00:00
#SBATCH --mail-type=END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# Targets the 80-node standard partition instead of the congested highmem queue.
# Capped at --mem=350gb to fit inside a single 384 GB Standard node safely.

java -Xmx310G \
     -XX:+UseG1GC \
     -XX:ParallelGCThreads=48 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=heapdump_${SLURM_JOB_ID}.hprof \
     -jar atosim.jar \
     simulator/org.palladiosimulator.blockchainsystems.atosim/optimized_deterministic_lhs_configurations.csv \
     simulator/org.palladiosimulator.blockchainsystems.atosim/testmodels \
     simulator/org.palladiosimulator.blockchainsystems.atosim/testmodels/configuration.json \
     --output-dir results_new/selfish

