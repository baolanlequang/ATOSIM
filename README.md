# Using Simulator

### Prerequisites

Make sure the following prerequisites are installed:

- Eclipse 2025-06 RAP
- Palladio Nightly Plugin
- Java 17 or higher

### Installation
2. Open Eclipse, select Help > Install New Software...> Add..:
  - Input `https://updatesite.mdsd.tools/library-standaloneinitialization/nightly/` into Location and install all supported libraries

1. Restart Eclipse and import following projects into Eclipse:
  - `org.palladiosimulator.blockchainsystems.bscm`
  - `oorg.palladiosimulator.blockchainsystems.bscm.edit`
  - `org.palladiosimulator.blockchainsystems.bscm.editor`
  - `org.palladiosimulator.blockchainsystems.core`
  - `org.palladiosimulator.blockchainsystems.kotlin-deps`
  - `org.palladiosimulator.blockchainsystems.loggers`
  - `org.palladiosimulator.blockchainsystems.plugin`
  - `org.palladiosimulator.blockchainsystems.threesim`
  - `org.palladiosimulator.blockchainsystems.threesim_plugin`
  - `org.palladiosimulator.blockchainsystems.atosim`

### Usage
1. Edit `configuration.json` to change the configuation of the simulator in `testmodels` folder
2. Copy all models in `sampling/generated_models/` to `org.palladiosimulator.blockchainsystems.atosim/testmodels`
3. Run:
- `ATOSIMSimulator.java` as Java Application to run the simulation.
4. The results will be placed at `indiv_json`

