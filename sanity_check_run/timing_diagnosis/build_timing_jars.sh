#!/bin/bash
#SBATCH --job-name=atosim-timing-build
#SBATCH --output=logs/atosim-timing-build-%j.out
#SBATCH --error=logs/atosim-timing-build-%j.err
#SBATCH --partition=cpu
#SBATCH --nodes=1
#SBATCH --ntasks=1
#SBATCH --cpus-per-task=2
#SBATCH --mem=8gb
#SBATCH --time=00:20:00
#SBATCH --mail-type=END,FAIL
#SBATCH --mail-user=baolan2005@gmail.com

# BUILD STEP (runs as a SLURM job on the cluster; submit from the repo root):
#   sbatch sanity_check_run/timing_diagnosis/build_timing_jars.sh
#
# Produces timing-instrumented COPIES of the three production jars under
#   ${WORKSPACE_PATH}/sanity_check_run/timing_diagnosis/jars/
# The production jars (atosim.jar, atosim-stubborn-lead.jar,
# atosim-stubborn-trail.jar) and the tracked source tree are never modified:
# the patch is applied to a scratch copy of the one source file, only that
# class is recompiled, and the resulting .class files are overlaid onto
# copies of each jar.
#
# Why overlay rather than a full rebuild: atosim*.jar are fat jars exported
# from Eclipse PDE (no command-line build recipe exists in the repo), and the
# change touches only ATOSIMSimulator.java. The ATOSIMSimulator*.class files
# are byte-identical in all three production jars (verified by checksum), so
# one compiled class is valid for each. The script re-checks this and aborts
# if the jars on the cluster differ from that assumption.

set -euo pipefail

PKG_PATH="org/palladiosimulator/blockchainsystems/atosim"
SRC_REL="simulator/org.palladiosimulator.blockchainsystems.atosim/src/${PKG_PATH}/ATOSIMSimulator.java"
PATCH="sanity_check_run/timing_diagnosis/per_row_timing.patch"
JARS=(atosim.jar atosim-stubborn-lead.jar atosim-stubborn-trail.jar)

command -v javac >/dev/null || { echo "ERROR: javac not on PATH (need a JDK, not only a JRE)" >&2; exit 1; }
command -v jar   >/dev/null || { echo "ERROR: jar tool not on PATH" >&2; exit 1; }
for f in "${SRC_REL}" "${PATCH}" "${JARS[@]}"; do
    [ -f "$f" ] || { echo "ERROR: missing $f (submit from the repo root)" >&2; exit 1; }
done

RESULTS_WORKSPACE="${RESULTS_WORKSPACE:-atosim_results}"
WORKSPACE_PATH="$(ws_find "${RESULTS_WORKSPACE}")"
[ -n "${WORKSPACE_PATH}" ] || { echo "ERROR: workspace '${RESULTS_WORKSPACE}' not found" >&2; exit 1; }
OUT_JARS="${WORKSPACE_PATH}/sanity_check_run/timing_diagnosis/jars"
mkdir -p "${OUT_JARS}" logs

REPO="$(pwd)"
BUILD="${TMPDIR:-/tmp}/atosim_timing_build_${SLURM_JOB_ID:-manual}"
rm -rf "${BUILD}"; mkdir -p "${BUILD}/src" "${BUILD}/classes"

# 0. The driver classes must be identical across the three jars.
ref=""
for j in "${JARS[@]}"; do
    sum="$(unzip -p "$j" "${PKG_PATH}/ATOSIMSimulator.class" "${PKG_PATH}/ATOSIMSimulator\$1.class" | sha256sum | cut -d' ' -f1)"
    echo "driver classes in $j: ${sum}"
    [ -z "$ref" ] && ref="$sum"
    [ "$sum" = "$ref" ] || { echo "ERROR: driver classes differ between jars; overlay is not safe" >&2; exit 1; }
done

# 1. Apply the patch to a scratch copy of the source tree path (not the repo).
mkdir -p "${BUILD}/src/$(dirname "${SRC_REL}")"
cp "${SRC_REL}" "${BUILD}/src/${SRC_REL}"
( cd "${BUILD}/src" \
  && { patch -p1 --binary < "${REPO}/${PATCH}" \
       || patch -p1 --binary --ignore-whitespace < "${REPO}/${PATCH}"; } )
grep -c 'ROW-TIMING' "${BUILD}/src/${SRC_REL}" | sed 's/^/ROW-TIMING lines in patched source: /'

# 2. Compile only that file against the production fat jar (target Java 17 =
#    class-file major 61, same as the classes already in the jars).
javac --release 17 -encoding UTF-8 -cp atosim.jar -d "${BUILD}/classes" "${BUILD}/src/${SRC_REL}"
ls "${BUILD}/classes/${PKG_PATH}/"

# 3. Overlay onto copies of each jar.
for j in "${JARS[@]}"; do
    out="${OUT_JARS}/${j%.jar}-timing.jar"
    cp "$j" "$out"
    jar uf "$out" -C "${BUILD}/classes" "${PKG_PATH}/ATOSIMSimulator.class" \
                  -C "${BUILD}/classes" "${PKG_PATH}/ATOSIMSimulator\$1.class"
    n="$(unzip -p "$out" "${PKG_PATH}/ATOSIMSimulator.class" | grep -c 'ROW-TIMING' || true)"
    [ "$n" -ge 1 ] || { echo "ERROR: $out does not contain the patched class" >&2; exit 1; }
    echo "built ${out}  (patched class verified)"
done

# 4. Production jars untouched.
git status --short -- "${JARS[@]}" "${SRC_REL}" | sed 's/^/git status: /' || true
echo "BUILD OK -> ${OUT_JARS}"
