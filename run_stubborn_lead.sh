#!/bin/bash
#SBATCH --job-name=atosim-stubborn-lead
#SBATCH --output=logs/atosim-stubborn-lead-%A_%a.out
#SBATCH --error=logs/atosim-stubborn-lead-%A_%a.err
#SBATCH --partition=highmem
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=48
#SBATCH --mem=600gb
#SBATCH --time=72:00:00
#SBATCH --mail-type=BEGIN,END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# Targets the highmem partition (~2.3 TB/node) since the full 471-config run
# OOM'd at ~382 GB on a 390 GB standard node — needs more headroom.
# Capped at --mem=350gb to fit inside a single 384 GB Standard node safely.

java -Xmx550G \
     -XX:+UseG1GC \
     -XX:ParallelGCThreads=48 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=heapdump_${SLURM_JOB_ID}.hprof \
     -jar atosim-stubborn-lead.jar \
     simulator/org.palladiosimulator.blockchainsystems.atosim/optimized_deterministic_lhs_configurations.csv \
     sampling/generate_models_stubborn_lead \
     simulator/org.palladiosimulator.blockchainsystems.atosim/testmodels/configuration.json \
     --attack-type LEAD_STUBBORN_MINING \
     --output-dir results_new/lead_stubborn

