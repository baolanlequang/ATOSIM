#!/bin/bash
#SBATCH --job-name=atosim-stubborn-trail
#SBATCH --output=logs/atosim-stubborn-trail-%A_%a.out
#SBATCH --error=logs/atosim-stubborn-trail-%A_%a.err
#SBATCH --partition=cpu
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=48
#SBATCH --mem=350gb
#SBATCH --time=72:00:00

# Targets the 80-node standard partition instead of the congested highmem queue.
# Capped at --mem=350gb to fit inside a single 384 GB Standard node safely.

java -Xmx310G \
     -XX:+UseG1GC \
     -XX:ParallelGCThreads=48 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=heapdump_${SLURM_JOB_ID}.hprof \
     -jar atosim-stubborn-trail.jar \
     simulator/org.palladiosimulator.blockchainsystems.atosim/optimized_deterministic_lhs_configurations.csv \
     sampling/generate_models_stubborn_trail \
     simulator/org.palladiosimulator.blockchainsystems.atosim/testmodels/configuration.json \
     --attack-type TRAIL_STUBBORN_MINING \
     --output-dir results/trail_stubborn

