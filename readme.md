
## Introduce
OF-SIM is a novel constraints handling algorithm based on the over-capacity flow.

It is designed for medium- to long-term scheduling of water transfer projects.

It is comprised of three proposed components, including over-capacity flow traceability (OFT) to pinpoint the critical hubs contributing to over-capacity flow, over-capacity flow reduction (OFR) to ascertain the appropriate reduction ratios, and dynamic boundary adaptation (DBA) to enhance computational efficiency.

## Data available

Data related to the Yangtze-to-Huaihe water diversion project can be obtained from the following sources:
Ministry of Water Resources of China official website: http://www.mwr.gov.cn/
National Earth System Science Data Center: https://www.geodata.cn/data/index.html
Anhui provincial group limited for Yangtze-to-Huaihe water diversion: https://www.ahyjjh.com.cn/

We also provide the desensitized data (https://doi.org/10.6084/m9.figshare.31889719).

## Use

the main function of of-sim in the com.research.instance.LongTermModel#main.
the main function of fr-sim in the com.research.frsim.FRSIM#main.
the main function of apca in the com.research.apca.LongTermModel#main.

## Project structure

the dynamic boundary adaptation (DBA) component is in the com.research.instance.ResearchSimulation#dynamicallyAdjustBoundaries
the over-capacity flow traceability (OFT) component and over-capacity flow reduction (OFR) component involve numerous gates, pumping stations and reservoirs, so these two components are scattered across multiple files.

### Gate

the OFT and OFR components is in the com.research.instance.gate.ResearchGateEntity#flowControl and com.research.instance.gate.ResearchGateEntity#adjustIntakeFlow
the flowControl function mainly determine when to execute OFT. Then, the adjustIntakeFlow function performs the function of OFR.

### Pump

the OFT and OFR components is in the com.research.instance.gate.ResearchGateEntity#flowControl and com.research.instance.gate.ResearchGateEntity#adjustIntakeFlow
the flowControl function mainly determines when to execute OFT. Then, the adjustIntakeFlow function performs the function of OFR.

### Reservoir

the OFT and OFR components is in the com.research.instance.gate.ResearchReservoirEntity#reservoirLevelControl, com.research.instance.gate.ResearchReservoirEntity#flowControl and com.research.instance.gate.ResearchReservoirEntity#adjustIntakeFlow
the flowControl and reservoirLevelControl functions mainly determine when to execute OFT. Then, the adjustIntakeFlow function performs the function of OFR.

### Comparison method

The fr-sim code is located in ResearchInstance/src/main/java/com/research/frsim
The apca code is located in ResearchInstance/src/main/java/com/research/apca

## Prerequisites

Before using this software, please ensure that the following conditions have been met.

JDK>=21
Maven>=3.9.10
