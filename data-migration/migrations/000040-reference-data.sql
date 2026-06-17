
DROP TABLE IF EXISTS gs.carbon_stream;
CREATE TABLE gs.carbon_stream (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.carbon_stream
	(id, code, description)
VALUES
    (0,'REPORTING_ONLY','Reporting Only'),
    (1,'VER','VER'),
    (2,'GS_CER','GS CER');

DROP TABLE IF EXISTS gs.current_standard_version;
CREATE TABLE gs.current_standard_version (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.current_standard_version
	(id, code, description)
VALUES
    (0,'CARBON_FIX','CarbonFix'),
    (1,'CDM','CDM'),
    (2,'GS4GG','Gold Standard for the Global Goals'),
    (3,'JOINT_IMPLEMENTATION','Joint Implementation (JI)'),
    (4,'V0_9_CASH','V0.9 CASH'),
    (5,'V1_0_CASH','v1 CASH'),
    (6,'V2_0','V2'),
    (7,'V2_1_SOP','V2.1 SOP'),
    (8,'V2_2_CASH','v2.2 CASH'),
    (9,'V2_2_SOP','v2.2 SOP'),
    (10,'V2_2','V2.2'),
    (11,'A_R_REQUIREMENTS_V0_9','A/R Requirements v0.9');

DROP TABLE IF EXISTS gs.fee_schedule;
CREATE TABLE gs.fee_schedule (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.fee_schedule
	(id, code, description)
VALUES
    (0,'CASH_PER_CREDIT','Cash per credit'),
    (1,'SHARE_OF_PROCEEDS','Share of proceeds');

DROP TABLE IF EXISTS gs.methodology;
CREATE TABLE gs.methodology (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.methodology
	(id, code, description)
VALUES
    (0,'ACM0001_FLARING_OR_USE_OF_LANDFILL_GAS','ACM0001 Flaring or use of landfill gas'),
    (1,'ACM0002_GRID_CONNECTED_ELECTRICITY_GENERATION_FROM_RENEWABLE_SOURCES','ACM0002 Grid-connected electricity generation from renewable sources'),
    (2,'ACM0003_PARTIAL_SUBSTITUTION_OF_FOSSIL_FUELS_WITH_ALTERNATIVE_FUELS_OR_LESS_CARBON_INTENSIVE_FUELS_IN_CEMENT_MANUFACTURE','ACM0003 Partial substitution of fossil fuels with alternative fuels or less carbon intensive fuels in cement manufacture'),
    (3,'ACM0006_ELECTRICITY_AND_HEAT_GENERATION_FROM_BIOMASS_RESIDUES','ACM0006 Electricity and heat generation from biomass residues'),
    (4,'ACM0007_CONVERSION_FROM_SINGLE_CYCLE_TO_COMBINED_CYCLE_POWER_GENERATION','ACM0007 Conversion from single cycle to combined cycle power generation'),
    (5,'ACM0009_FUEL_SWITCHING_FROM_COAL_OR_PETROLEUM_FUELS_TO_NATURAL_GAS','ACM0009 Fuel switching from coal or petroleum fuels to natural gas'),
    (6,'ACM0010_GHG_EMISSION_REDUCTIONS_FROM_MANURE_MANAGEMENT_SYSTEMS','ACM0010 GHG emission reductions from manure management systems'),
    (7,'ACM0012_WASTE_ENERGY_RECOVERY','ACM0012 Waste energy recovery'),
    (8,'ACM0014_TREATMENT_OF_WASTEWATER','ACM0014 Treatment of wastewater'),
    (9,'ACM0015_EMISSION_REDUCTIONS_FROM_RAW_MATERIALS_IN_CLINKER_PRODUCTION','ACM0015 Emission reductions from raw materials in clinker production'),
    (10,'ACM0016_MASS_RAPID_TRANSIT_PROJECTS','ACM0016 Mass Rapid Transit Projects'),
    (11,'ACM0017_PRODUCTION_OF_BIODIESEL','ACM0017 Production of biodiesel'),
    (12,'ACM0018_ELECTRICITY_GENERATION_FROM_BIOMASS_RESIDUES_IN_POWER_ONLY_PLANTS','ACM0018 Electricity generation from biomass residues in power-only plants'),
    (13,'ACM0020_CO_FIRING_OF_BIOMASS_RESIDUES_FOR_HEAT_GENERATION_AND_OR_ELECTRICITY_GENERATION_IN_GRID_CONNECTED_POWER_PLANTS','ACM0020 Co-firing of biomass residues for heat generation and/or electricity generation in grid connected power plants'),
    (14,'ACM0022_ALTERNATIVE_WASTE_TREATMENT_PROCESSES','ACM0022 Alternative waste treatment processes'),
    (15,'ACM0023_INTRODUCTION_OF_AN_EFFICIENCY_IMPROVEMENT_TECHNOLOGY_IN_A_BOILER','ACM0023 Introduction of an efficiency improvement technology in a boiler'),
    (16,'ACM0024_NATURAL_GAS_SUBSTITUTION_BY_BIOGENIC_METHANE_PRODUCED_FROM_THE_ANAEROBIC_DIGESTION_OF_ORGANIC_WASTE','ACM0024 Natural gas substitution by biogenic methane produced from the anaerobic digestion of organic waste'),
    (17,'ACM0026_FOSSIL_FUEL_BASED_COGENERATION_FOR_IDENTIFIED_RECIPIENT_FACILITY_IES','ACM0026 Fossil fuel based cogeneration for identified recipient facility(ies)'),
    (18,'AM0007_ANALYSIS_OF_THE_LEAST_COST_FUEL_OPTION_FOR_SEASONALLY_OPERATING_BIOMASS_COGENERATION_PLANTS','AM0007 Analysis of the least-cost fuel option for seasonally-operating biomass cogeneration plants'),
    (19,'AM0017_STEAM_SYSTEM_EFFICIENCY_IMPROVEMENTS_BY_REPLACING_STEAM_TRAPS_AND_RETURNING_CONDENSATE','AM0017 Steam system efficiency improvements by replacing steam traps and returning condensate'),
    (20,'AM0018_BASELINE_METHODOLOGY_FOR_STEAM_OPTIMIZATION_SYSTEMS','AM0018 Baseline methodology for steam optimization systems'),
    (21,'AM0019_RENEWABLE_ENERGY_PROJECT_ACTIVITIES_REPLACING_PART_OF_THE_ELECTRICITY_PRODUCTION_OF_ONE_SINGLE_FOSSIL_FUEL_FIRED_POWER_PLANT_THAT_STANDS_ALONE_OR_SUPPLIES_ELECTRICITY_TO_A_GRID_EXCLUDING_BIOMASS_PROJECTS','AM0019 Renewable energy project activities replacing part of the electricity production of one single fossil-fuel-fired power plant that stands alone or supplies electricity to a grid = 21, excluding biomass projects'),
    (22,'AM0020_BASELINE_METHODOLOGY_FOR_WATER_PUMPING_EFFICIENCY_IMPROVEMENTS','AM0020 Baseline methodology for water pumping efficiency improvements'),
    (23,'AM0026_METHODOLOGY_FOR_ZERO_EMISSIONS_GRID_CONNECTED_ELECTRICITY_GENERATION_FROM_RENEWABLE_SOURCES_IN_CHILE_OR_IN_COUNTRIES_WITH_MERIT_ORDER_BASED_DISPATCH_GRID','AM0026 Methodology for zero-emissions grid-connected electricity generation from renewable sources in Chile or in countries with merit order based dispatch grid'),
    (24,'AM0027_SUBSTITUTION_OF_CO2_FROM_FOSSIL_OR_MINERAL_ORIGIN_BY_CO2_FROM_RENEWABLE_SOURCES_IN_THE_PRODUCTION_OF_INORGANIC_COMPOUNDS','AM0027 Substitution of CO2 from fossil or mineral origin by CO2 from renewable sources in the production of inorganic compounds'),
    (25,'AM0031_BUS_RAPID_TRANSIT_PROJECTS','AM0031 Bus rapid transit projects'),
    (26,'AM0036_FUEL_SWITCH_FROM_FOSSIL_FUELS_TO_BIOMASS_RESIDUES_IN_HEAT_GENERATION_EQUIPMENT','AM0036 Fuel switch from fossil fuels to biomass residues in heat generation equipment'),
    (27,'AM0038_METHODOLOGY_FOR_IMPROVED_ELECTRICAL_ENERGY_EFFICIENCY_OF_AN_EXISTING_SUBMERGED_ELECTRIC_ARC_FURNACE_USED_FOR_THE_PRODUCTION_OF_SIMN','AM0038 Methodology for improved electrical energy efficiency of an existing submerged electric arc furnace used for the production of SiMn'),
    (30,'AM0044_ENERGY_EFFICIENCY_IMPROVEMENT_PROJECTS_BOILER_REHABILITATION_OR_REPLACEMENT_IN_INDUSTRIAL_AND_DISTRICT_HEATING_SECTORS','AM0044 Energy efficiency improvement projects: boiler rehabilitation or replacement in industrial and district heating sectors'),
    (31,'AM0046_DISTRIBUTION_OF_EFFICIENT_LIGHT_BULBS_TO_HOUSEHOLDS','AM0046 Distribution of efficient light bulbs to households'),
    (32,'AM0048_NEW_COGENERATION_FACILITIES_SUPPLYING_ELECTRICITY_AND_OR_STEAM_TO_MULTIPLE_CUSTOMERS_AND_DISPLACING_GRID_OFF_GRID_STEAM_AND_ELECTRICITY_GENERATION_WITH_MORE_CARBON_INTENSIVE_FUELS','AM0048 New cogeneration facilities supplying electricity and/or steam to multiple customers and displacing grid/off-grid steam and electricity generation with more carbon-intensive fuels'),
    (33,'AM0049_METHODOLOGY_FOR_GAS_BASED_ENERGY_GENERATION_IN_AN_INDUSTRIAL_FACILITY','AM0049 Methodology for gas based energy generation in an industrial facility'),
    (34,'AM0052_INCREASED_ELECTRICITY_GENERATION_FROM_EXISTING_HYDROPOWER_STATIONS_THROUGH_DECISION_SUPPORT_SYSTEM_OPTIMIZATION','AM0052 Increased electricity generation from existing hydropower stations through Decision Support System optimization'),
    (35,'AM0053_BIOGENIC_METHANE_INJECTION_TO_A_NATURAL_GAS_DISTRIBUTION_GRID','AM0053 Biogenic methane injection to a natural gas distribution grid'),
    (36,'AM0055_RECOVERY_AND_UTILIZATION_OF_WASTE_GAS_IN_REFINERY_OR_GAS_PLANT_','AM0055 Recovery and utilization of waste gas in refinery or gas plant'),
    (37,'AM0056_EFFICIENCY_IMPROVEMENT_BY_BOILER_REPLACEMENT_OR_REHABILITATION_AND_OPTIONAL_FUEL_SWITCH_IN_FOSSIL_FUEL_FIRED_STEAM_BOILER_SYSTEMS','AM0056 Efficiency improvement by boiler replacement or rehabilitation and optional fuel switch in fossil fuel-fired steam boiler systems'),
    (38,'AM0057_AVOIDED_EMISSIONS_FROM_BIOMASS_WASTES_THROUGH_USE_AS_FEED_STOCK_IN_PULP_AND_PAPER_PRODUCTION_OR_IN_BIO_OIL_PRODUCTION','AM0057 Avoided emissions from biomass wastes through use as feed stock in pulp and paper production or in bio-oil production'),
    (39,'AM0058_INTRODUCTION_OF_A_DISTRICT_HEATING_SYSTEM','AM0058 Introduction of a district heating system'),
    (40,'AM0059_REDUCTION_IN_GHGS_EMISSION_FROM_PRIMARY_ALUMINIUM_SMELTERS','AM0059 Reduction in GHGs emission from primary aluminium smelters'),
    (41,'AM0060_POWER_SAVING_THROUGH_REPLACEMENT_BY_ENERGY_EFFICIENT_CHILLERS','AM0060 Power saving through replacement by energy efficient chillers'),
    (42,'AM0063_RECOVERY_OF_CO2_FROM_TAIL_GAS_IN_INDUSTRIAL_FACILITIES_TO_SUBSTITUTE_THE_USE_OF_FOSSIL_FUELS_FOR_PRODUCTION_OF_CO2','AM0063 Recovery of CO2 from tail gas in industrial facilities to substitute the use of fossil fuels for production of CO2'),
    (43,'AM0066_GHG_EMISSION_REDUCTIONS_THROUGH_WASTE_HEAT_UTILISATION_FOR_PRE_HEATING_OF_RAW_MATERIALS_IN_SPONGE_IRON_MANUFACTURING_PROCESS','AM0066 GHG emission reductions through waste heat utilization for pre-heating of raw materials in sponge iron manufacturing process'),
    (44,'AM0068_METHODOLOGY_FOR_IMPROVED_ENERGY_EFFICIENCY_BY_MODIFYING_FERROALLOY_PRODUCTION_FACILITY','AM0068 Methodology for improved energy efficiency by modifying ferroalloy production facility'),
    (45,'AM0069_BIOGENIC_METHANE_USE_AS_FEEDSTOCK_AND_FUEL_FOR_TOWN_GAS_PRODUCTION','AM0069 Biogenic methane use as feedstock and fuel for town gas production'),
    (46,'AM0070_MANUFACTURING_OF_ENERGY_EFFICIENT_DOMESTIC_REFRIGERATORS','AM0070 Manufacturing of energy efficient domestic refrigerators'),
    (47,'AM0072_FOSSIL_FUEL_DISPLACEMENT_BY_GEOTHERMAL_RESOURCES_FOR_SPACE_HEATING','AM0072 Fossil Fuel Displacement by Geothermal Resources for Space Heating'),
    (48,'AM0073_GHG_EMISSION_REDUCTIONS_THROUGH_MULTI_SITE_MANURE_COLLECTION_AND_TREATMENT_IN_A_CENTRAL_PLANT','AM0073 GHG emission reductions through multi-site manure collection and treatment in a central plant'),
    (49,'AM0075_METHODOLOGY_FOR_COLLECTION_PROCESSING_AND_SUPPLY_OF_BIOGAS_TO_END_USERS_FOR_PRODUCTION_OF_HEAT','AM0075 Methodology for collection = 49, processing and supply of biogas to end-users for production of heat'),
    (50,'AM0076_METHODOLOGY_FOR_IMPLEMENTATION_OF_FOSSIL_FUEL_TRIGENERATION_SYSTEMS_IN_EXISTING_INDUSTRIAL_FACILITIES','AM0076 Methodology for implementation of fossil fuel trigeneration systems in existing industrial facilities'),
    (51,'AM0080_MITIGATION_OF_GREENHOUSE_GASES_EMISSIONS_WITH_TREATMENT_OF_WASTEWATER_IN_AEROBIC_WASTEWATER_TREATMENT_PLANTS','AM0080 Mitigation of greenhouse gases emissions with treatment of wastewater in aerobic wastewater treatment plants'),
    (52,'AM0081_FLARE_OR_VENT_REDUCTION_AT_COKE_PLANTS_THROUGH_THE_CONVERSION_OF_THEIR_WASTE_GAS_INTO_DIMETHYL_ETHER_FOR_USE_AS_A_FUEL','AM0081 Flare or vent reduction at coke plants through the conversion of their waste gas into dimethyl ether for use as a fuel'),
    (53,'AM0082_USE_OF_CHARCOAL_FROM_PLANTED_RENEWABLE_BIOMASS_IN_THE_IRON_ORE_REDUCTION_PROCESS_THROUGH_THE_ESTABLISHMENT_OF_A_NEW_IRON_ORE_REDUCTION_SYSTEM','AM0082 Use of charcoal from planted renewable biomass in the iron ore reduction process through the establishment of a new iron ore reduction system'),
    (54,'AM0083_AVOIDANCE_OF_LANDFILL_GAS_EMISSIONS_BY_IN_SITU_AERATION_OF_LANDFILLS','AM0083 Avoidance of landfill gas emissions by in-situ aeration of landfills'),
    (55,'AM0084_INSTALLATION_OF_COGENERATION_SYSTEM_SUPPLYING_ELECTRICITY_AND_CHILLED_WATER_TO_NEW_AND_EXISTING_CONSUMERS','AM0084 Installation of cogeneration system supplying electricity and chilled water to new and existing consumers'),
    (57,'AM0086_INSTALLATION_OF_ZERO_ENERGY_WATER_PURIFIER_FOR_SAFE_DRINKING_WATER_APPLICATION','AM0086 Installation of zero energy water purifier for safe drinking water application'),
    (59,'AM0088_AIR_SEPARATION_USING_CRYOGENIC_ENERGY_RECOVERED_FROM_THE_VAPORIZATION_OF_LNG','AM0088 Air separation using cryogenic energy recovered from the vaporization of LNG'),
    (60,'AM0089_PRODUCTION_OF_DIESEL_USING_A_MIXED_FEEDSTOCK_OF_GASOIL_AND_VEGETABLE_OIL','AM0089 Production of diesel using a mixed feedstock of gasoil and vegetable oil'),
    (61,'AM0090_MODAL_SHIFT_IN_TRANSPORTATION_OF_CARGO_FROM_ROAD_TRANSPORTATION_TO_WATER_OR_RAIL_TRANSPORTATION','AM0090 Modal shift in transportation of cargo from road transportation to water or rail transportation'),
    (62,'AM0091_ENERGY_EFFICIENCY_TECHNOLOGIES_AND_FUEL_SWITCHING_IN_NEW_BUILDINGS','AM0091 Energy efficiency technologies and fuel switching in new buildings'),
    (63,'AM0094_DISTRIBUTION_OF_BIOMASS_BASED_STOVE_AND_OR_HEATER_FOR_HOUSEHOLD_OR_INSTITUTIONAL_USE','AM0094 Distribution of biomass based stove and/or heater for household or institutional use'),
    (64,'AM0095_WASTE_GAS_BASED_COMBINED_CYCLE_POWER_PLANT_IN_A_GREENFIELD_IRON_AND_STEEL_PLANT','AM0095 Waste gas based combined cycle power plant in a Greenfield iron and steel plant'),
    (65,'AM0098_UTILIZATION_OF_AMMONIA_PLANT_OFF_GAS_FOR_STEAM_GENERATION','AM0098 Utilization of ammonia-plant off gas for steam generation'),
    (66,'AM0100_INTEGRATED_SOLAR_COMBINED_CYCLE_ISCC_PROJECTS','AM0100 Integrated Solar Combined Cycle (ISCC) projects'),
    (67,'AM0101_HIGH_SPEED_PASSENGER_RAIL_SYSTEMS','AM0101 High speed passenger rail systems'),
    (68,'AM0103_RENEWABLE_ENERGY_POWER_GENERATION_IN_ISOLATED_GRIDS','AM0103 Renewable energy power generation in isolated grids'),
    (69,'AM0105_ENERGY_EFFICIENCY_IN_DATA_CENTRES_THROUGH_DYNAMIC_POWER_MANAGEMENT','AM0105 Energy efficiency in data centers through dynamic power management'),
    (70,'AM0106_ENERGY_EFFICIENCY_IMPROVEMENTS_OF_A_LIME_PRODUCTION_FACILITY_THROUGH_INSTALLATION_OF_NEW_KILNS','AM0106 Energy efficiency improvements of a lime production facility through installation of new kilns'),
    (71,'AM0109_INTRODUCTION_OF_HOT_SUPPLY_OF_DIRECT_REDUCED_IRON_IN_ELECTRIC_ARC_FURNACES','AM0109 Introduction of hot supply of Direct Reduced Iron in Electric Arc Furnaces'),
    (72,'AM0110_MODAL_SHIFT_IN_TRANSPORTATION_OF_LIQUID_FUELS','AM0110 Modal shift in transportation of liquid fuels'),
    (73,'AM0113_DISTRIBUTION_OF_COMPACT_FLUORESCENT_LAMPS_CFL_AND_LIGHT_EMITTING_DIODE_LED_LAMPS_TO_HOUSEHOLDS','AM0113 Distribution of compact fluorescent lamps (CFL) and light-emitting diode (LED) lamps to households'),
    (74,'AM0114_SHIFT_FROM_ELECTROLYTIC_TO_CATALYTIC_PROCESS_FOR_RECYCLING_OF_CHLORINE_FROM_HYDROGEN_CHLORIDE_GAS_IN_ISOCYANATE_PLANTS','AM0114 Shift from electrolytic to catalytic process for recycling of chlorine from hydrogen chloride gas in isocyanate plants'),
    (75,'AM0116_ELECTRIC_TAXIING_SYSTEMS_FOR_AIRPLANES','AM0116 Electric taxiing systems for airplanes'),
    (76,'AM0117_INTRODUCTION_OF_A_NEW_DISTRICT_COOLING_SYSTEM','AM0117 Introduction of a new district cooling system'),
    (77,'AMS_I_A_ELECTRICITY_GENERATION_BY_THE_USER','AMS-I.A. Electricity generation by the user'),
    (78,'AMS_I_B_MECHANICAL_ENERGY_FOR_THE_USER_WITH_OR_WITHOUT_ELECTRICAL_ENERGY','AMS-I.B. Mechanical energy for the user with or without electrical energy'),
    (79,'AMS_I_C_THERMAL_ENERGY_PRODUCTION_WITH_OR_WITHOUT_ELECTRICITY','AMS-I.C. Thermal energy production with or without electricity'),
    (80,'AMS_I_D_GRID_CONNECTED_RENEWABLE_ELECTRICITY_GENERATION','AMS-I.D. Grid connected renewable electricity generation'),
    (81,'AMS_I_E_SWITCH_FROM_NON_RENEWABLE_BIOMASS_FOR_THERMAL_APPLICATIONS_BY_THE_USER','AMS-I.E. Switch from Non-Renewable Biomass for Thermal Applications by the User'),
    (82,'AMS_I_F_RENEWABLE_ELECTRICITY_GENERATION_FOR_CAPTIVE_USE_AND_MINI_GRID','AMS-I.F. Renewable electricity generation for captive use and mini-grid'),
    (83,'AMS_I_G_PLANT_OIL_PRODUCTION_AND_USE_FOR_ENERGY_GENERATION_IN_STATIONARY_APPLICATIONS','AMS-I.G. Plant oil production and use for energy generation in stationary applications'),
    (84,'AMS_I_H_BIODIESEL_PRODUCTION_AND_USE_FOR_ENERGY_GENERATION_IN_STATIONARY_APPLICATIONS','AMS-I.H. Biodiesel production and use for energy generation in stationary applications'),
    (85,'AMS_I_I_BIOGAS_BIOMASS_THERMAL_APPLICATIONS_FOR_HOUSEHOLDS_SMALL_USERS','AMS-I.I. Biogas/biomass thermal applications for households/small users'),
    (86,'AMS_I_J_SOLAR_WATER_HEATING_SYSTEMS_SWH','AMS-I.J. Solar water heating systems (SWH)'),
    (87,'AMS_I_K_SOLAR_COOKERS_FOR_HOUSEHOLDS','AMS-I.K. Solar cookers for households'),
    (88,'AMS_I_L_ELECTRIFICATION_OF_RURAL_COMMUNITIES_USING_RENEWABLE_ENERGY','AMS-I.L. Electrification of rural communities using renewable energy'),
    (89,'AMS_I_M_SOLAR_POWER_FOR_DOMESTIC_AIRCRAFT_AT_GATE_OPERATIONS','AMS-I.M. Solar power for domestic aircraft at-gate operations'),
    (90,'AMS_II_B_SUPPLY_SIDE_ENERGY_EFFICIENCY_IMPROVEMENTS_GENERATION','AMS-II.B. Supply side energy efficiency improvements – generation'),
    (91,'AMS_II_C_DEMAND_SIDE_ENERGY_EFFICIENCY_ACTIVITIES_FOR_SPECIFIC_TECHNOLOGIES','AMS-II.C. Demand-side energy efficiency activities for specific technologies'),
    (92,'AMS_II_D_ENERGY_EFFICIENCY_AND_FUEL_SWITCHING_MEASURES_FOR_INDUSTRIAL_FACILITIES','AMS-II.D. Energy efficiency and fuel switching measures for industrial facilities'),
    (93,'AMS_II_E_ENERGY_EFFICIENCY_AND_FUEL_SWITCHING_MEASURES_FOR_BUILDINGS','AMS-II.E. Energy efficiency and fuel switching measures for buildings'),
    (94,'AMS_II_F_ENERGY_EFFICIENCY_AND_FUEL_SWITCHING_MEASURES_FOR_AGRICULTURAL_FACILITIES_AND_ACTIVITIES','AMS-II.F. Energy efficiency and fuel switching measures for agricultural facilities and activities'),
    (95,'AMS_II_G_ENERGY_EFFICIENCY_MEASURES_IN_THERMAL_APPLICATIONS_OF_NON_RENEWABLE_BIOMASS','AMS-II.G. Energy Efficiency Measures in Thermal Applications of Non-Renewable Biomass'),
    (96,'AMS_II_H_ENERGY_EFFICIENCY_MEASURES_THROUGH_CENTRALIZATION_OF_UTILITY_PROVISIONS_OF_AN_INDUSTRIAL_FACILITY','AMS-II.H. Energy efficiency measures through centralization of utility provisions of an industrial facility'),
    (97,'AMS_II_I_EFFICIENT_UTILIZATION_OF_WASTE_ENERGY_IN_INDUSTRIAL_FACILITIES','AMS-II.I. Efficient utilization of waste energy in industrial facilities'),
    (98,'AMS_II_J_DEMAND_SIDE_ACTIVITIES_FOR_EFFICIENT_LIGHTING_TECHNOLOGIES','AMS-II.J. Demand-side activities for efficient lighting technologies'),
    (99,'AMS_II_K_INSTALLATION_OF_CO_GENERATION_OR_TRI_GENERATION_SYSTEMS_SUPPLYING_ENERGY_TO_COMMERCIAL_BUILDING','AMS-II.K. Installation of co-generation or tri-generation systems supplying energy to commercial building'),
    (100,'AMS_II_L_DEMAND_SIDE_ACTIVITIES_FOR_EFFICIENT_OUTDOOR_AND_STREET_LIGHTING_TECHNOLOGIES','AMS-II.L Demand-side activities for efficient outdoor and street lighting technologies'),
    (101,'AMS_II_M_DEMAND_SIDE_ENERGY_EFFICIENCY_ACTIVITIES_FOR_INSTALLATION_OF_LOW_FLOW_HOT_WATER_SAVINGS_DEVICES','AMS-II.M Demand-side energy efficiency activities for installation of low-flow hot water savings devices'),
    (102,'AMS_II_N_DEMAND_SIDE_ENERGY_EFFICIENCY_ACTIVITIES_FOR_INSTALLATION_OF_ENERGY_EFFICIENT_LIGHTING_AND_OR_CONTROLS_IN_BUILDINGS','AMS-II.N. Demand-side energy efficiency activities for installation of energy efficient lighting and/or controls in buildings'),
    (103,'AMS_II_O_DISSEMINATION_OF_ENERGY_EFFICIENT_HOUSEHOLD_APPLIANCES','AMS-II.O. Dissemination of energy efficient household appliances'),
    (104,'AMS_II_P_ENERGY_EFFICIENT_PUMP_SET_FOR_AGRICULTURE_USE','AMS-II.P. Energy efficient pump-set for agriculture use'),
    (105,'AMS_II_Q_ENERGY_EFFICIENCY_AND_OR_ENERGY_SUPPLY_PROJECTS_IN_COMMERCIAL_BUILDINGS','AMS-II.Q. Energy efficiency and/or energy supply projects in commercial buildings'),
    (106,'AMS_II_R_ENERGY_EFFICIENCY_SPACE_HEATING_MEASURES_FOR_RESIDENTIAL_BUILDINGS','AMS-II.R. Energy efficiency space heating measures for residential buildings'),
    (107,'AMS_II_S_ENERGY_EFFICIENCY_IN_MOTOR_SYSTEMS','AMS-II.S. Energy efficiency in motor systems'),
    (108,'AMS_III_A_UREA_OFFSET_BY_INOCULANT_APPLICATION_IN_SOYBEAN_CORN_ROTATIONS_ON_ACIDIC_SOILS_ON_EXISTING_CROPLAND','AMS-III.A. Urea offset by inoculant application in soybean-corn rotations on acidic soils on existing cropland'),
    (109,'AMS_III_AA_TRANSPORTATION_ENERGY_EFFICIENCY_ACTIVITIES_USING_RETROFIT_TECHNOLOGIES','AMS-III.AA. Transportation Energy Efficiency Activities using Retrofit Technologies'),
    (110,'AMS_III_AC_ELECTRICITY_AND_OR_HEAT_GENERATION_USING_FUEL_CELL','AMS-III.AC. Electricity and/or heat generation using fuel cell'),
    (111,'AMS_III_AD_EMISSION_REDUCTIONS_IN_HYDRAULIC_LIME_PRODUCTION','AMS-III.AD. Emission reductions in hydraulic lime production'),
    (112,'AMS_III_AE_ENERGY_EFFICIENCY_AND_RENEWABLE_ENERGY_MEASURES_IN_NEW_RESIDENTIAL_BUILDINGS','AMS-III.AE. Energy efficiency and renewable energy measures in new residential buildings'),
    (113,'AMS_III_AF_AVOIDANCE_OF_METHANE_EMISSIONS_THROUGH_EXCAVATING_AND_COMPOSTING_OF_PARTIALLY_DECAYED_MUNICIPAL_SOLID_WASTE_MSW','AMS-III.AF. Avoidance of methane emissions through excavating and composting of partially decayed municipal solid waste (MSW)'),
    (114,'AMS_III_AG_SWITCHING_FROM_HIGH_CARBON_INTENSIVE_GRID_ELECTRICITY_TO_LOW_CARBON_INTENSIVE_FOSSIL_FUEL','AMS-III.AG. Switching from high carbon intensive grid electricity to low carbon intensive fossil fuel'),
    (115,'AMS_III_AH_SHIFT_FROM_HIGH_CARBON_INTENSIVE_FUEL_MIX_RATIO_TO_LOW_CARBON_INTENSIVE_FUEL_MIX_RATIO_','AMS-III.AH. Shift from high carbon intensive fuel mix ratio to low carbon intensive fuel mix ratio'),
    (116,'AMS_III_AI_EMISSION_REDUCTIONS_THROUGH_RECOVERY_OF_SPENT_SULPHURIC_ACID','AMS-III.AI. Emission reductions through recovery of spent sulphuric acid'),
    (117,'AMS_III_AJ_RECOVERY_AND_RECYCLING_OF_MATERIALS_FROM_SOLID_WASTES','AMS-III.AJ. Recovery and recycling of materials from solid wastes'),
    (118,'AMS_III_AK_BIODIESEL_PRODUCTION_AND_USE_FOR_TRANSPORT_APPLICATIONS','AMS-III.AK. Biodiesel production and use for transport applications'),
    (119,'AMS_III_AL_CONVERSION_FROM_SINGLE_CYCLE_TO_COMBINED_CYCLE_POWER_GENERATION','AMS-III.AL. Conversion from single cycle to combined cycle power generation'),
    (120,'AMS_III_AO_METHANE_RECOVERY_THROUGH_CONTROLLED_ANAEROBIC_DIGESTION','AMS-III.AO. Methane recovery through controlled anaerobic digestion'),
    (121,'AMS_III_AP_TRANSPORT_ENERGY_EFFICIENCY_ACTIVITIES_USING_POST_FIT_IDLING_STOP_DEVICE','AMS-III.AP. Transport energy efficiency activities using post - fit Idling Stop device'),
    (122,'AMS_III_AQ_INTRODUCTION_OF_BIO_CNG_IN_TRANSPORTATION_APPLICATIONS','AMS-III.AQ. Introduction of Bio-CNG in transportation applications'),
    (123,'AMS_III_AR_SUBSTITUTING_FOSSIL_FUEL_BASED_LIGHTING_WITH_LED_LIGHTING_SYSTEMS','AMS-III.AR. Substituting fossil fuel based lighting with LED lighting systems'),
    (124,'AMS_III_AS_SWITCH_FROM_FOSSIL_FUEL_TO_BIOMASS_IN_EXISTING_MANUFACTURING_FACILITIES_FOR_NON_ENERGY_APPLICATIONS','AMS-III.AS. Switch from fossil fuel to biomass in existing manufacturing facilities for non-energy applications'),
    (125,'AMS_III_AT_TRANSPORTATION_ENERGY_EFFICIENCY_ACTIVITIES_INSTALLING_DIGITAL_TACHOGRAPH_SYSTEMS_TO_COMMERCIAL_FREIGHT_TRANSPORT_FLEETS','AMS-III.AT. Transportation energy efficiency activities installing digital tachograph systems to commercial freight transport fleets'),
    (126,'AMS_III_AV_LOW_GREENHOUSE_GAS_EMITTING_WATER_PURIFICATION_SYSTEMS','AMS-III.AV. Low greenhouse gas emitting water purification systems'),
    (127,'AMS_III_AW_ELECTRIFICATION_OF_RURAL_COMMUNITIES_BY_GRID_EXTENSION','AMS-III.AW. Electrification of rural communities by grid extension'),
    (128,'AMS_III_AU_METHANE_EMISSION_REDUCTION_BY_ADJUSTED_WATER_MANAGEMENT_PRACTICE_IN_RICE_CULTIVATION','AMS-III.AU. Methane emission reduction by adjusted water management practice in rice cultivation'),
    (129,'AMS_III_AY_INTRODUCTION_OF_LNG_BUSES_TO_EXISTING_AND_NEW_BUS_ROUTES','AMS-III.AY. Introduction of LNG buses to existing and new bus routes'),
    (130,'AMS_III_BA_RECOVERY_AND_RECYCLING_OF_MATERIALS_FROM_E_WASTE_','AMS-III.BA Recovery and recycling of materials from E-waste'),
    (131,'AMS_III_BC_EMISSION_REDUCTIONS_THROUGH_IMPROVED_EFFICIENCY_OF_VEHICLE_FLEETS','AMS-III.BC Emission reductions through improved efficiency of vehicle fleets'),
    (132,'AMS_III_BD_GHG_EMISSION_REDUCTION_DUE_TO_SUPPLY_OF_MOLTEN_METAL_INSTEAD_OF_INGOTS_FOR_ALUMINIUM_CASTINGS','AMS-III.BD GHG emission reduction due to supply of molten metal instead of ingots for aluminium castings'),
    (133,'AMS_III_BE_AVOIDANCE_OF_METHANE_AND_NITROUS_OXIDE_EMISSIONS_FROM_SUGARCANE_PRE_HARVEST_OPEN_BURNING_THROUGH_MULCHING','AMS-III.BE Avoidance of methane and nitrous oxide emissions from sugarcane pre-harvest open burning through mulching'),
    (134,'AMS_III_BF_REDUCTION_OF_N2O_EMISSIONS_FROM_USE_OF_NITROGEN_USE_EFFICIENT_NUE_SEEDS_THAT_REQUIRE_LESS_FERTILIZER_APPLICATION','AMS-III.BF Reduction of N2O emissions from use of Nitrogen Use Efficient (NUE) seeds that require less fertilizer application'),
    (135,'AMS_III_BG_EMISSION_REDUCTION_THROUGH_SUSTAINABLE_CHARCOAL_PRODUCTION_AND_CONSUMPTION','AMS-III.BG Emission reduction through sustainable charcoal production and consumption'),
    (136,'AMS_III_BH_DISPLACEMENT_OF_PRODUCTION_OF_BRICK_AND_CEMENT_BY_MANUFACTURE_AND_INSTALLATION_OF_GYPSUM_CONCRETE_WALL_PANELS','AMS-III.BH Displacement of production of brick and cement by manufacture and installation of gypsum concrete wall panels'),
    (137,'AMS_III_BK_STRATEGIC_FEED_SUPPLEMENTATION_IN_SMALLHOLDER_DAIRY_SECTOR_TO_INCREASE_PRODUCTIVITY','AMS-III.BK Strategic feed supplementation in smallholder dairy sector to increase productivity'),
    (138,'AMS_III_C_EMISSION_REDUCTIONS_BY_ELECTRIC_AND_HYBRID_VEHICLES','AMS-III.C. Emission reductions by electric and hybrid vehicles'),
    (139,'AMS_III_D_METHANE_RECOVERY_IN_ANIMAL_MANURE_MANAGEMENT_SYSTEMS','AMS-III.D. Methane recovery in animal manure management systems'),
    (140,'AMS_III_E_AVOIDANCE_OF_METHANE_PRODUCTION_FROM_DECAY_OF_BIOMASS_THROUGH_CONTROLLED_COMBUSTION_GASIFICATION_OR_MECHANICAL_THERMAL_TREATMENT','AMS-III.E. Avoidance of methane production from decay of biomass through controlled combustion = 140, gasification or mechanical/thermal treatment'),
    (141,'AMS_III_F_AVOIDANCE_OF_METHANE_EMISSIONS_THROUGH_CONTROLLED_BIOLOGICAL_TREATMENT_OF_BIOMASS','AMS-III.F. Avoidance of methane emissions through controlled biological treatment of biomass'),
    (142,'AMS_III_G_LANDFILL_METHANE_RECOVERY','AMS-III.G. Landfill methane recovery'),
    (143,'AMS_III_H_METHANE_RECOVERY_IN_WASTEWATER_TREATMENT','AMS-III.H. Methane recovery in wastewater treatment'),
    (144,'AMS_III_I_AVOIDANCE_OF_METHANE_PRODUCTION_IN_WASTEWATER_TREATMENT_THROUGH_REPLACEMENT_OF_ANAEROBIC_SYSTEMS_BY_AEROBIC_SYSTEMS','AMS-III.I. Avoidance of methane production in wastewater treatment through replacement of anaerobic systems by aerobic systems'),
    (145,'AMS_III_J_AVOIDANCE_OF_FOSSIL_FUEL_COMBUSTION_FOR_CARBON_DIOXIDE_PRODUCTION_TO_BE_USED_AS_RAW_MATERIAL_FOR_INDUSTRIAL_PROCESSES','AMS-III.J. Avoidance of fossil fuel combustion for carbon dioxide production to be used as raw material for industrial processes'),
    (146,'AMS_III_K_AVOIDANCE_OF_METHANE_RELEASE_FROM_CHARCOAL_PRODUCTION_BY_SHIFTING_FROM_TRADITIONAL_OPEN_ENDED_METHODS_TO_MECHANIZED_CHARCOALING_PROCESS','AMS-III.K. Avoidance of methane release from charcoal production by shifting from traditional open-ended methods to mechanized charcoaling process'),
    (147,'AMS_III_L_AVOIDANCE_OF_METHANE_PRODUCTION_FROM_BIOMASS_DECAY_THROUGH_CONTROLLED_PYROLYSIS','AMS-III.L. Avoidance of methane production from biomass decay through controlled pyrolysis'),
    (148,'AMS_III_M_REDUCTION_IN_CONSUMPTION_OF_ELECTRICITY_BY_RECOVERING_SODA_FROM_PAPER_MANUFACTURING_PROCESS','AMS-III.M. Reduction in consumption of electricity by recovering soda from paper manufacturing process'),
    (149,'AMS_III_O_HYDROGEN_PRODUCTION_USING_METHANE_EXTRACTED_FROM_BIOGAS','AMS-III.O. Hydrogen production using methane extracted from biogas'),
    (150,'AMS_III_P_RECOVERY_AND_UTILIZATION_OF_WASTE_GAS_IN_REFINERY_FACILITIES','AMS-III.P. Recovery and utilization of waste gas in refinery facilities'),
    (151,'AMS_III_Q_WASTE_ENERGY_RECOVERY_GAS_HEAT_PRESSURE_PROJECTS','AMS-III.Q. Waste Energy Recovery (gas/heat/pressure) Projects'),
    (152,'AMS_III_R_METHANE_RECOVERY_IN_AGRICULTURAL_ACTIVITIES_AT_HOUSEHOLD_SMALL_FARM_LEVEL','AMS-III.R. Methane recovery in agricultural activities at household/small farm level'),
    (153,'AMS_III_S_INTRODUCTION_OF_LOW_EMISSION_VEHICLES_TECHNOLOGIES_TO_COMMERCIAL_VEHICLE_FLEETS','AMS-III.S. Introduction of low-emission vehicles/technologies to commercial vehicle fleets'),
    (154,'AMS_III_T_PLANT_OIL_PRODUCTION_AND_USE_FOR_TRANSPORT_APPLICATIONS','AMS-III.T. Plant oil production and use for transport applications'),
    (155,'AMS_III_U_CABLE_CARS_FOR_MASS_RAPID_TRANSIT_SYSTEM_MRTS','AMS-III.U. Cable Cars for Mass Rapid Transit System (MRTS)'),
    (156,'AMS_III_V_DECREASE_OF_COKE_CONSUMPTION_IN_BLAST_FURNACE_BY_INSTALLING_DUST_SLUDGE_RECYCLING_SYSTEM_IN_STEEL_WORKS','AMS-III.V. Decrease of coke consumption in blast furnace by installing dust/sludge recycling system in steel works'),
    (157,'AMS_III_X_ENERGY_EFFICIENCY_AND_HFC_134A_RECOVERY_IN_RESIDENTIAL_REFRIGERATORS','AMS-III.X. Energy Efficiency and HFC-134a Recovery in Residential Refrigerators'),
    (158,'AMS_III_Y_METHANE_AVOIDANCE_THROUGH_SEPARATION_OF_SOLIDS_FROM_WASTEWATER_OR_MANURE_TREATMENT_SYSTEMS','AMS-III.Y. Methane avoidance through separation of solids from wastewater or manure treatment systems'),
    (159,'AMS_III_Z_FUEL_SWITCH_PROCESS_IMPROVEMENT_AND_ENERGY_EFFICIENCY_IN_BRICK_MANUFACTURE','AMS-III.Z. Fuel Switch = 159, process improvement and energy efficiency in brick manufacture'),
    (160,'GS_ADALYS_V1','GS ADALYS v1.'),
    (161,'GS_ADVANCED_HULL_COATINGS_V2','GS Advanced Hull Coatings v2.'),
    (162,'GS_ALTERNATIVE_IGNITION_COAL_FIRES_V1','GS Alternative Ignition Coal Fires v1.'),
    (163,'GS_BASELINE_AND_MONITORING_METHODOLOGY_BIODIGESTER_V1','GS Baseline and Monitoring Methodology Biodigester v1.'),
    (164,'GS_BIODIESEL_FROM_WASTE_OIL_OR_FAT_V1','GS Biodiesel from Waste Oil or Fat v1.'),
    (165,'GS_BLACK_CARBON_AND_CO_EMITTED_SPECIES_V1_1','GS Black Carbon and Co-emitted Species V1.1'),
    (166,'GS_CFL_AND_WATER_SAVING_PRODUCTS_V2','GS CFL and Water Saving Products v2.'),
    (167,'GS_FOSSIL_FUELS_TO_BIOMASS_RESIDUES_IN_BOILERS_V1','GS Fossil Fuels to Biomass Residues in Boilers v1.'),
    (168,'GS_GHG_A_R_GHG_EMISSIONS_REDUCTION_SEQUESTRATION_V1','GS GHG A.R GHG Emissions Reduction Sequestration v1.'),
    (169,'GS_IMPROVED_TILLAGE_PRACTICES_V_0_9','GS Improved Tillage Practices v 0.9'),
    (170,'GS_METHODOLOGY_FOR_IMPROVED_COOK_STOVES_AND_KITCHEN_REGIMES_V1','GS Methodology for Improved Cook stoves and Kitchen Regimes v1.'),
    (171,'GS_METHODOLOGY_FOR_IMPROVED_COOK_STOVES_AND_KITCHEN_REGIMES_V2','GS Methodology for Improved Cook stoves and Kitchen Regimes v2.'),
    (172,'GS_MS_INSTALLATION_OF_FLOW_IMPROVEMENT_EQUIPMENT_ON_SHIPS_V1','GS MS Installation of Flow Improvement Equipment on Ships v1.'),
    (173,'GS_MS_MICROSCALE_ELECTRIFICATION_AND_ENERGIZATION_V1','GS MS Microscale Electrification and Energization v1.'),
    (174,'GS_MS_SIMPLIFIED_METHODOLOGY_FOR_EFFICIENT_COOKSTOVES_V1','GS MS Simplified Methodology for Efficient Cookstoves v1.'),
    (175,'GS_RETROFIT_ENERGY_EFFICIENCY_MEASURES_IN_SHIPPING_V1','GS Retrofit Energy Efficiency Measures in Shipping v1.'),
    (176,'GS_REVISED_METHODOLOGY_FOR_MANURE_MANAGEMENT_SYSTEMS_AND_MSW_V1','GS Revised Methodology for Manure Management Systems and MSW v1.'),
    (177,'GS_SMALLHOLDER_DAIRY_METHODOLOGY_V_1','GS Smallholder Dairy Methodology v 1.'),
    (178,'GS_SS_ECOLOGICALLY_SOUND_FUEL_SWITCH_TO_BIOMASS_V1','GS SS Ecologically Sound Fuel Switch to Biomass v1.'),
    (179,'GS_SS_METHODOLOGY_FOR_LOW_GHG_FOOD_PRESERVATION_V1','GS SS Methodology for Low GHG Food Preservation v1.'),
    (180,'GS_SS_PROCESSING_AGRICULTURAL_PRODUCTS_V_1','GS SS Processing Agricultural Products v 1.'),
    (181,'GS_SS_THERMAL_ENERGY_FROM_PLANT_OIL_V1','GS SS Thermal energy from plant oil v1.'),
    (182,'GS_THERMAL_PERFORMANCE_IMPROVEMENTS_IN_DWELLINGS_V1','GS Thermal performance improvements in Dwellings v1.'),
    (183,'GS_TPDDTEC_V_1','GS TPDDTEC v 1.'),
    (184,'GS_TPDDTEC_V_2','GS TPDDTEC v 2.'),
    (185,'GS_TPDDTEC_V_3','GS TPDDTEC v 3.'),
    (186,'GS_TPDDTEC_V3_1','GS TPDDTEC v3.1'),
    (187,'GS_WATER_ACCESS_AND_WASH_METHODOLOGY_V1','GS Water Access and WASH Methodology v1.'),
    (188,'OTHER','Other'),
    (189,'GS_METHODOLOGY_FOR_EMISSION_REDUCTIONS','GS Methodology for emission reductions from safe drinking water supply'),
    (190,'AR_AFFORESTATION','Afforestation/Reforestation GHG Emissions Reduction & Sequestration Methodology'),
    (191,'AGR_SMALLHOLDER_DAIRY','Smallholder Dairy Methodology: Methodology for GHG Emission Reductions from Smallholder Dairy Production Systems'),
    (192,'CDM_METHODOLOGY','CDM METHODOLOGY - AMS-III.AU “METHANE EMISSION REDUCTION BY ADJUSTED WATER MANAGEMENT PRACTICE IN RICE CULTIVATION'),
    (193,'SOIL_ORG_CARBON_FRAMEW_METH','Soil Organic Carbon Framework Methodology'),
    (194,'SOIL_ORGANIC_CARBON_ACT_MOD','Soil Organic Carbon Activity Module: Increasing Soil Carbon Through Improved Tillage Practices V1.0'),
    (195,'REDUCING_METHANE_EMISSIONS_FROM_ENTERIC','Reducing Methane Emissions from Enteric Fermentation in Dairy Cows Through Application Of Feed Supplements'),
    (196,'WATER_EROSION_IMPACT_ASSESSMENT_SUST_AG_LAND_MANAG_PROJ_V1','Water and Erosion Impact Assessment of Sustainable Agricultural Land Management Projects V1.0'),
    (197,'METHOD_METERED_MEASURED_ENERGY_COOKING_DEV','Methodology for Metered & Measured Energy Cooking Devices'),
    (198,'SUST_SUGARCANE_INIT_METHOD_QUANTIFY_WATER_EFFICIENCY_SEED_NURSERIES_V1_0','Sustainable Sugarcane Initiative Methodology to Quantify Water Efficiency Outcomes from Seedling Nurseries V1.0'),
    (199,'GS_TPDDTEC_V4_0','GS TPDDTEC V4.0: REDUCED EMISSIONS FROM COOKING AND HEATING – TECHNOLOGIES AND PRACTICES TO DISPLACE DECENTRALIZED THERMAL ENERGY CONSUMPTION'),
    (200,'GS_MS_SIMPLIFIED_METHOD_EFFICIENT_COOKSTOVES_V1_1','GS MS Simplified Methodology for Efficient Cookstoves v1.1'),
    (201,'AMS_III_BM_V1_0','AMS-III.BM: Lightweight two and three wheeled personal transportation version 1.0'),
    (202,'AMS_III_BN_V1_0','AMS-III.BN: Efficient operation of public transportation version 1.0'),
    (203,'AMS_III_BO_V1_0','AMS-III.BO: Trip avoidance through equipment improvement of freight transport version 1.0'),
    (204,'AMS_III_BP_V1_0','AMS-III.BP: Emission reduction by shore-side electricity supply system version 1.0'),
    (205,'AM0120_V1_0','AM0120: Energy-efficient refrigerators and air-conditioners version 1.0'),
    (206,'AM0122_V1_0','AM0122: Recovery of methane-rich vapors from hydrocarbon storage tanks version 1.0'),
    (207,'SOC_Module_Pulp','Soil Organic Carbon Activity Module For Application Of Organic Soil Improvers From Pulp And Paper Mill Sludges'),
    (208,'GS_RETROFIT_ENERGY_EFFICIENCY_MEASURES_IN_SHIPPING_V2','GS Retrofit Energy Efficiency Measures in Shipping v2'),
    (209,'METHODOLOGY_ANIMAL_WASTE_MANAGEMENT_AND_BIOGAS_APPLICATION','Methodology for animal waste management and biogas application'),
    (210,'AMS_III_BM','AMS-III.BM: Lightweight two and three wheeled personal transportation'),
    (211,'AMS_III_BL','AMS-III.BL. Integrated methodology for electrification of communities v2'),
    (212,'THE_GOLD_STANDARD_SIMPLIFIED_METHODOLOGY_FOR_CLEAN_AND_EFFICIENT_COOKSTOVES_VERSION_3_0','The Gold Standard Simplified Methodology for Clean and Efficient Cookstoves, Version 3.0'),
    (213,'METHANE_EMISSION_REDUCTION_BY_ADJUSTED_WATER_MANAGEMENT_PRACTICE_IN_RICE_CULTIVATION','Methane Emission Reduction By Adjusted Water Management Practice In Rice Cultivation'),
    (214,'METHODOLOGY_FOR_REDUCING_METHANE_EMISSIONS_FROM_ENTERIC_FERMENTATION_IN_BEEF_CATTLE_THROUGH_APPLICATION_OF_FEED_SUPPLEMENTS','Methodology For Reducing Methane Emissions From Enteric Fermentation In Beef Cattle Through Application Of Feed Supplements'),
    (215,'CARBON_SEQUESTRATION_THROUGH_ACCELERATED_CARBONATION_OF_CONCRETE_AGGREGATE_V1_0','Carbon Sequestration Through Accelerated Carbonation Of Concrete Aggregate V1.0'),
    (999,'NOT_PROVIDED','Not provided');

DROP TABLE IF EXISTS gs.programme_of_activities;
CREATE TABLE gs.programme_of_activities (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.programme_of_activities
	(id, code, description)
VALUES
    (0,'NONE','None'),
    (1,'POA','Master Legacy'),
    (2,'VPA_CPA','VPA/CPA'),
    (3,'REAL_CASE_VPA','Real case VPA'),
    (4,'REGULAR_CASE_VPA','Regular case VPA'),
    (5,'REAL_OR_REGULAR_CASE_POA','REAL OR REGULAR CASE POA');

DROP TABLE IF EXISTS gs.project_asset;
CREATE TABLE gs.project_asset (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.project_asset
	(id, code, description)
VALUES
    (0,'CARBON','Carbon'),
    (1,'RECS','RECs'),
    (2,'GENDER','Gender'),
    (3,'ADALYS','ADALYs'),
    (4,'WATER','Water');

DROP TABLE IF EXISTS gs.project_category;
CREATE TABLE gs.project_category (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.project_category
	(id, code, description)
VALUES
    (1,'LUF','LUF'),
    (2,'ENERGY','ENERGY');

DROP TABLE IF EXISTS gs.project_location;
CREATE TABLE gs.project_location (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.project_location
	(id, code, description)
VALUES
    (1,'AF','Afghanistan'),
    (2,'AX','Åland Islands'),
    (3,'AL','Albania'),
    (4,'DZ','Algeria'),
    (5,'AS','American Samoa'),
    (6,'AD','Andorra'),
    (7,'AO','Angola'),
    (8,'AI','Anguilla'),
    (9,'AQ','Antarctica'),
    (10,'AG','Antigua and Barbuda'),
    (11,'AR','Argentina'),
    (12,'AM','Armenia'),
    (13,'AW','Aruba'),
    (14,'AU','Australia'),
    (15,'AT','Austria'),
    (16,'AZ','Azerbaijan'),
    (17,'BS','Bahamas'),
    (18,'BH','Bahrain'),
    (19,'BD','Bangladesh'),
    (20,'BB','Barbados'),
    (21,'BY','Belarus'),
    (22,'BE','Belgium'),
    (23,'BZ','Belize'),
    (24,'BJ','Benin'),
    (25,'BM','Bermuda'),
    (26,'BT','Bhutan'),
    (27,'BO','Bolivia (Plurinational State of)'),
    (28,'BQ','Bonaire, Sint Eustatius and Saba'),
    (29,'BA','Bosnia and Herzegovina'),
    (30,'BW','Botswana'),
    (31,'BV','Bouvet Island'),
    (32,'BR','Brazil'),
    (33,'IO','British Indian Ocean Territory'),
    (34,'BN','Brunei Darussalam'),
    (35,'BG','Bulgaria'),
    (36,'BF','Burkina Faso'),
    (37,'BI','Burundi'),
    (38,'CV','Cabo Verde'),
    (39,'KH','Cambodia'),
    (40,'CM','Cameroon'),
    (41,'CA','Canada'),
    (42,'KY','Cayman Islands'),
    (43,'CF','Central African Republic'),
    (44,'TD','Chad'),
    (45,'CL','Chile'),
    (46,'CN','China'),
    (47,'CX','Christmas Island'),
    (48,'CC','Cocos (Keeling) Islands'),
    (49,'CO','Colombia'),
    (50,'KM','Comoros'),
    (51,'CG','Congo'),
    (52,'CD','Congo (Democratic Republic of the)'),
    (53,'CK','Cook Islands'),
    (54,'CR','Costa Rica'),
    (55,'CI','Côte d''Ivoire'),
    (56,'HR','Croatia'),
    (57,'CU','Cuba'),
    (58,'CW','Curaçao'),
    (59,'CY','Cyprus'),
    (60,'CZ','Czech Republic (Czechia)'),
    (61,'DK','Denmark'),
    (62,'DJ','Djibouti'),
    (63,'DM','Dominica'),
    (64,'DO','Dominican Republic'),
    (65,'EC','Ecuador'),
    (66,'EG','Egypt'),
    (67,'SV','El Salvador'),
    (68,'GQ','Equatorial Guinea'),
    (69,'ER','Eritrea'),
    (70,'EE','Estonia'),
    (71,'ET','Ethiopia'),
    (72,'FK','Falkland Islands (Malvinas)'),
    (73,'FO','Faroe Islands'),
    (74,'FJ','Fiji'),
    (75,'FI','Finland'),
    (76,'FR','France'),
    (77,'GF','French Guiana'),
    (78,'PF','French Polynesia'),
    (79,'TF','French Southern Territories'),
    (80,'GA','Gabon'),
    (81,'GM','Gambia'),
    (82,'GE','Georgia'),
    (83,'DE','Germany'),
    (84,'GH','Ghana'),
    (85,'GI','Gibraltar'),
    (86,'GR','Greece'),
    (87,'GL','Greenland'),
    (88,'GD','Grenada'),
    (89,'GP','Guadeloupe'),
    (90,'GU','Guam'),
    (91,'GT','Guatemala'),
    (92,'GG','Guernsey'),
    (93,'GN','Guinea'),
    (94,'GW','Guinea-Bissau'),
    (95,'GY','Guyana'),
    (96,'HT','Haiti'),
    (97,'HM','Heard Island and McDonald Islands'),
    (98,'VA','Holy See'),
    (99,'HN','Honduras'),
    (100,'HK','Hong Kong'),
    (101,'HU','Hungary'),
    (102,'IS','Iceland'),
    (103,'IN','India'),
    (104,'ID','Indonesia'),
    (105,'IR','Iran (Islamic Republic of)'),
    (106,'IQ','Iraq'),
    (107,'IE','Ireland'),
    (108,'IM','Isle of Man'),
    (109,'IL','Israel'),
    (110,'IT','Italy'),
    (111,'JM','Jamaica'),
    (112,'JP','Japan'),
    (113,'JE','Jersey'),
    (114,'JO','Jordan'),
    (115,'KZ','Kazakhstan'),
    (116,'KE','Kenya'),
    (117,'KI','Kiribati'),
    (118,'KP','Korea (Democratic People''s Republic of)'),
    (119,'KR','Korea (Republic of)'),
    (120,'KW','Kuwait'),
    (121,'KG','Kyrgyz Republic (Kyrgyzstan)'),
    (122,'LA','Lao People''s Democratic Republic'),
    (123,'LV','Latvia'),
    (124,'LB','Lebanon'),
    (125,'LS','Lesotho'),
    (126,'LR','Liberia'),
    (127,'LY','Libya'),
    (128,'LI','Liechtenstein'),
    (129,'LT','Lithuania'),
    (130,'LU','Luxembourg'),
    (131,'MO','Macao'),
    (132,'MK','North Macedonia (Republic of)'),
    (133,'MG','Madagascar'),
    (134,'MW','Malawi'),
    (135,'MY','Malaysia'),
    (136,'MV','Maldives'),
    (137,'ML','Mali'),
    (138,'MT','Malta'),
    (139,'MH','Marshall Islands'),
    (140,'MQ','Martinique'),
    (141,'MR','Mauritania'),
    (142,'MU','Mauritius'),
    (143,'YT','Mayotte'),
    (144,'MX','Mexico'),
    (145,'FM','Micronesia (Federated States of)'),
    (146,'MD','Moldova (Republic of)'),
    (147,'MC','Monaco'),
    (148,'MN','Mongolia'),
    (149,'ME','Montenegro'),
    (150,'MS','Montserrat'),
    (151,'MA','Morocco'),
    (152,'MZ','Mozambique'),
    (153,'MM','Myanmar'),
    (154,'NA','Namibia'),
    (155,'NR','Nauru'),
    (156,'NP','Nepal'),
    (157,'NL','Netherlands'),
    (158,'NC','New Caledonia'),
    (159,'NZ','New Zealand'),
    (160,'NI','Nicaragua'),
    (161,'NE','Niger'),
    (162,'NG','Nigeria'),
    (163,'NU','Niue'),
    (164,'NF','Norfolk Island'),
    (165,'MP','Northern Mariana Islands'),
    (166,'NO','Norway'),
    (167,'OM','Oman'),
    (168,'PK','Pakistan'),
    (169,'PW','Palau'),
    (170,'PS','Palestine, State of'),
    (171,'PA','Panama'),
    (172,'PG','Papua New Guinea'),
    (173,'PY','Paraguay'),
    (174,'PE','Peru'),
    (175,'PH','Philippines'),
    (176,'PN','Pitcairn'),
    (177,'PL','Poland'),
    (178,'PT','Portugal'),
    (179,'PR','Puerto Rico'),
    (180,'QA','Qatar'),
    (181,'RE','Réunion'),
    (182,'RO','Romania'),
    (183,'RU','Russian Federation'),
    (184,'RW','Rwanda'),
    (185,'BL','Saint Barthélemy'),
    (186,'SH','Saint Helena, Ascension and Tristan da Cunha'),
    (187,'KN','Saint Kitts and Nevis'),
    (188,'LC','Saint Lucia'),
    (189,'MF','Saint Martin (French part)'),
    (190,'PM','Saint Pierre and Miquelon'),
    (191,'VC','Saint Vincent and the Grenadines'),
    (192,'WS','Samoa'),
    (193,'SM','San Marino'),
    (194,'ST','Sao Tome and Principe'),
    (195,'SA','Saudi Arabia'),
    (196,'SN','Senegal'),
    (197,'RS','Serbia'),
    (198,'SC','Seychelles'),
    (199,'SL','Sierra Leone'),
    (200,'SG','Singapore'),
    (201,'SX','Sint Maarten (Dutch part)'),
    (202,'SK','Slovak Republic (Slovakia)'),
    (203,'SI','Slovenia'),
    (204,'SB','Solomon Islands'),
    (205,'SO','Somalia'),
    (206,'ZA','South Africa'),
    (207,'GS','South Georgia and the South Sandwich Islands'),
    (208,'SS','South Sudan'),
    (209,'ES','Spain'),
    (210,'LK','Sri Lanka'),
    (211,'SD','Sudan'),
    (212,'SR','Suriname'),
    (213,'SJ','Svalbard and Jan Mayen'),
    (214,'SZ','Swaziland'),
    (215,'SE','Sweden'),
    (216,'CH','Switzerland'),
    (217,'SY','Syrian Arab Republic'),
    (218,'TW','Taiwan, Province of China[a]'),
    (219,'TJ','Tajikistan'),
    (220,'TZ','Tanzania (United Republic of)'),
    (221,'TH','Thailand'),
    (222,'TL','Timor-Leste'),
    (223,'TG','Togo'),
    (224,'TK','Tokelau'),
    (225,'TO','Tonga'),
    (226,'TT','Trinidad and Tobago'),
    (227,'TN','Tunisia'),
    (228,'TR','Turkey'),
    (229,'TM','Turkmenistan'),
    (230,'TC','Turks and Caicos Islands'),
    (231,'TV','Tuvalu'),
    (232,'UG','Uganda'),
    (233,'UA','Ukraine'),
    (234,'AE','United Arab Emirates'),
    (235,'GB','United Kingdom of Great Britain and Northern Ireland'),
    (236,'US','United States of America'),
    (237,'UM','United States Minor Outlying Islands'),
    (238,'UY','Uruguay'),
    (239,'UZ','Uzbekistan'),
    (240,'VU','Vanuatu'),
    (241,'VE','Venezuela (Bolivarian Republic of)'),
    (242,'VN','Vietnam (Socialist Republic of)'),
    (243,'VG','Virgin Islands (British)'),
    (244,'VI','Virgin Islands (U.S.)'),
    (245,'WF','Wallis and Futuna'),
    (246,'EH','Western Sahara'),
    (247,'YE','Yemen'),
    (248,'ZM','Zambia'),
    (249,'ZW','Zimbabwe'),
    (301,'XK','Kosovo'),
    (302,'XC','Channel Islands'),
    (303,'XG','West Bank and Gaza'),
    (304,'XE','Eswatini'),
    (305,'INTERNATIONAL','International');

DROP TABLE IF EXISTS gs.project_size;
CREATE TABLE gs.project_size (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.project_size
	(id, code, description)
VALUES
    (0,'MICROSCALE','Micro scale'),
	(1,'SMALL_SCALE','Small Scale'),
	(2,'LARGE_SCALE','Large Scale');

DROP TABLE IF EXISTS gs.project_type;
CREATE TABLE gs.project_type (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.project_type
	(id, code, description)
VALUES
    (0,'BIOGAS_HEAT','Biogas - Heat'),
    (1,'COMMUNITY_BIOGAS_ELECTRICITY','Community - biogas - electricity'),
    (2,'COMMUNITY_BIOGAS_HEAT','Community - biogas - heat'),
    (3,'COMMUNITY_ENERGY_EFFICIENCY_DOMESTIC','Community - Energy Efficiency - Domestic'),
    (4,'COMPOSTING','Composting'),
    (5,'ENERGY_EFFICIENCY_TRANSPORT_SECTOR','Energy Efficiency - Transport Sector'),
    (6,'ENERGY_EFFICIENCY_AGRICULTURE','Energy Efficiency - Agriculture'),
    (7,'ENERGY_EFFICIENCY_COMMERCIAL_SECTOR','Energy Efficiency - Commercial Sector'),
    (8,'ENERGY_EFFICIENCY_INDUSTRIAL_SECTOR','Energy Efficiency - Industrial Sector'),
    (9,'ENERGY_EFFICIENCY_PUBLIC_SECTOR','Energy Efficiency - Public Sector'),
    (10,'GEOTHERMAL','Geothermal'),
    (11,'RENEWABLE_ENERGY_BIOGAS_CONGENERATION','Renewable Energy - biogas - cogeneration'),
    (12,'RENEWABLE_ENERGY_BIOMASS_OR_LIQUID_BIOFUEL_CONGENERATION','Renewable Energy - Biomass or Liquid Biofuel - Cogeneration'),
    (13,'RENEWABLE_ENERGY_BIOMASS_OR_LIQUID_BIOFUEL_ELECTRICITY','Renewable Energy - Biomass or Liquid Biofuel - Electricity'),
    (14,'RENEWABLE_ENERGY_BIOMASS_OR_LIQUID_BIOFUEL_HEAT','Renewable Energy - Biomass or Liquid Biofuel - Heat'),
    (15,'SMALL_LOW_IMPACT_HYDRO','Small Low Impact Hydro'),
    (16,'SOLAR_THERMAL_ELECTRICITY','Solar Thermal Electricity'),
    (17,'SOLAR_THERMAL_HEAT','Solar Thermal Heat'),
    (18,'WASTEWATER_TREATMENT','Wastewater Treatment'),
    (19,'WIND','Wind'),
    (20,'RENEWABLE_ENERGY_COMBINE','Renewable Energy - Combine'),
    (21,'BIOGAS_COGENERATION','Biogas - Cogeneration'),
    (22,'BIOGAS_AND_ELECTRICITY','Biogas and Electricity'),
    (23,'COMMUNITY_ENERGY_EFFICIENCY_HEAT','Community - Energy Efficiency-Heat'),
    (24,'LANDFILL','Landfill'),
    (25,'ELECTRICIFICATION','Electricification'),
    (26,'PV','PV'),
    (27,'SOLAR','Solar'),
    (28,'SOLID_WASTE_TREATMENT','Solid Waste Treatment'),
    (29,'VCI','VCI'),
    (30,'AFFORESTATION_REFORESTATION','Afforestation/Reforestation'),
    (31,'AGRICULTURE','Agriculture'),
    (32,'SOIL_ORGANIC_CARBON','Soil Organic Carbon');

DROP TABLE IF EXISTS gs.registration_type;
CREATE TABLE gs.registration_type (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.registration_type
	(id, code, description)
VALUES
    (0,'REGULAR','Regular'),
    (1,'RETROACTIVE','Retroactive');

DROP TABLE IF EXISTS gs.sustainable_development_goal;
CREATE TABLE gs.sustainable_development_goal (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.sustainable_development_goal
	(id, code, description)
VALUES
    (1,'NO_POVERTY','1 - No Poverty'),
    (2,'ZERO_HUNGER','2 - Zero Hunger'),
    (3,'GOOD_HEALTH_AND_WELL_BEING','3 - Good Health and Well-Being'),
    (4,'QUALITY_EDUCATION','4 - Quality Education'),
    (5,'GENDER_EQUALITY','5 - Gender Equality'),
    (6,'CLEAN_WATER_AND_SANITATION','6 - Clean Water and Sanitation'),
    (7,'AFFORDABLE_AND_CLEAN_ENERGY','7 - Affordable and Clean Energy'),
    (8,'DECENT_WORK_AND_ECONOMIC_GROWTH','8 - Decent Work and Economic Growth'),
    (9,'INDUSTRY_INNOVATION_AND_INFRASTRUCTURE','9 - Industry  Innovation and Infrastructure'),
    (10,'REDUCED_INEQUALITIES','10 - Reduced Inequalities'),
    (11,'SUSTAINABLE_CITIES_AND_COMMUNITIES','11 - Sustainable Cities and Communities'),
    (12,'RESPONSIBLE_PRODUCTION_AND_CONSUMPTION','12 - Responsible Production and Consumption'),
    (13,'CLIMATE_ACTION','13 - Climate Action'),
    (14,'LIKE_BELOW_WATER','14 - Life Below Water'),
    (15,'LIFE_ON_LAND','15 - Life on Land'),
    (16,'PEACE_JUSTICE_AND_STRONG_INSTITUTIONS','16 - Peace Justice and Strong Institutions'),
    (17,'PARTERSHIPS_FOR_THE_GOALS','17 - Partnerships for the Goals');

DROP TABLE IF EXISTS gs.request_document_status;
CREATE TABLE gs.request_document_status (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.request_document_status
	(id, code, description)
VALUES
    (0,'DOCUMENTS_MISSING','Documents missing'),
	(1,'DOCUMENTS_COMPLETE','Documents complete');

DROP TABLE IF EXISTS gs.request_type;
CREATE TABLE gs.request_type (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.request_type
	(id, code, description)
VALUES
    (0,'POA_DESIGN_CONSULTATION','POA Design Consultation'),
    (1,'PRELIMINARY_REVIEW','Preliminary Review'),
    (2,'INTERNAL_VALIDATION','Internal Validation'),
    (3,'DESIGN_REVIEW','Design Review'),
    (4,'INTERNAL_VERIFICATION','Internal Verification'),
    (5,'PERFORMANCE_REVIEW','Performance Review'),
    (6,'CREDITING_PERIOD_RENEWAL','Crediting Period Renewal'),
    (7,'DESIGN_CHANGE_REVIEW','Design Change Review'),
    (8,'TRANSITION_REVIEW','Transition Review (Previous GS VERSION to GS4GG)'),
    (9,'DESIGN_CERTIFICATION_RENEWAL','Design Certification Renewal'),
    (10,'NEW_AREA_ADDITION','New Area Addition'),
    (11,'OBJECTIVE_OBSERVER_VISIT','Objective Observer Visit'),
    (12,'FOREST_SOIL_CARBON','Forest Soil Carbon'),
    (13,'PRELIMINARY_REVIEW_TRANSITION','Preliminary Review (transition)'),
    (14,'TRANSITION_REVIEW_CS_CDM_GS_VER','Transition Review (GS CDM to GS VER)'),
    (15,'PRELIMINARY_REVIEW_REGULAR_CASE_VPA','Preliminary Review'),
    (16,'PRELIMINARY_REVIEW_REAL_OR_REGULAR_CASE_POA','POA Preliminary Review');

DROP TABLE IF EXISTS gs.invoice_status;
CREATE TABLE gs.invoice_status (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.invoice_status
	(id, code, description)
VALUES
    (0,'NOT_TRIGGERED','Not triggered'),
    (1,'TRIGGERED','Triggered'),
    (2,'GENERATED','Generated'),
    (3,'PAID','Paid'),
    (4,'VOID','Void'),
    (5,'NOT_REQUIRED','Not Required');

DROP TABLE IF EXISTS gs.project_status;
CREATE TABLE gs.project_status (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.project_status
	(id, code, description)
VALUES
    (1,'NEW','New'),
    (2,'LISTED','Listed'),
    (3,'DESIGN_CERTIFIED','Design Certified'),
    (4,'ISSUED','Issued');

DROP TABLE IF EXISTS gs.request_status;
CREATE TABLE gs.request_status (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.request_status
	(id, code, description)
VALUES
    (0,'NOT_REQUESTED','Not requested'),
    (1,'BOOKED','Booked'),
    (2,'REVIEW_PLANNED_TO_START','Planned To Start'),
    (3,'TODO: UNKNOWN STATUS','TODO: UNKNOWN STATUS'),
    (4,'COMPLETED_APPROVED','Completed - Approved'),
    (5,'COMPLETED_REJECTED','Completed - Rejected'),
    (6,'IN_PREPARATION','In Preparation'),
    (7,'OMITTED','Omitted'),
    (8,'CANCELLED','Cancelled'),
    (9,'COMPLETENESS_CHECK','Completeness Check'),
    (10,'IN_PROGRESS','In Progress');

DROP TABLE IF EXISTS gs.document_type;
CREATE TABLE gs.document_type (
	id integer NOT NULL,
	code varchar NOT NULL,
	description varchar NOT NULL,
	PRIMARY KEY(id)
);

INSERT INTO gs.document_type
	(id, code, description)
VALUES
    (0,'CDM_PROJECT_DESIGN_DOCUMENT','CDM Project Design Document'),
    (1,'GOLD_STANDARD_PROJECT_DESIGN_DOCUMENT','Gold Standard Project Design Document'),
    (2,'COVER_LETTER','Cover Letter'),
    (3,'TERMS_AND_CONDITIONS','Terms and Conditions'),
    (4,'ODA_DECLARATION','ODA Declaration'),
    (5,'EMISSION_REDUCTION_CALCULATION_EXCEL_FILE','Emission Reduction Calculation Excel File'),
    (6,'CDM_VALIDATION_REPORT','CDM Validation Report'),
    (7,'GOLD_STANDARD_VALIDATION_REPORT','Gold Standard Validation Report'),
    (8,'CDM_MONITORING_REPORT','CDM Monitoring Report'),
    (9,'CDM_VERIFICATION_REPORT','CDM Verification Report'),
    (10,'GS4GG_MONITORING_REPORT','GS4GG Monitoring Report'),
    (11,'GS4GG_VERIFICATION_REPORT','GS4GG Verification Report'),
    (12,'GOLD_STANDARD_MONITORING_REPORT','Gold Standard Monitoring Report'),
    (13,'POA_DESIGN_CONSULTATION_REPORT','POA Design Consultation Report'),
    (14,'GOLD_STANDARD_PROJECT_DESIGN_CHANGE_MEMO','Gold Standard Project Design Change Memo'),
    (15,'GS_VALIDATION_REPORT_FOR_DESIGN_CHANGE','GS Validation Report for design change'),
    (16,'EMISSION_REDUCTION_SHEET','Emission Reduction Sheet'),
    (17,'GS4GG_COVER_LETTER','GS4GG Cover Letter'),
    (18,'TRANSITION_ANNEX','Transition Annex'),
    (19,'TRANSITION_REQUEST_FORM','Transition Request Form'),
    (20,'LSC_INVITATION_LETTER','LSC Invitation Letter'),
    (21,'LSC_MEETING_ATTENDANCE_LIST','LSC Meeting Attendance List'),
    (22,'EIA__ESIA_REPORT','EIA / ESIA Report'),
    (23,'STAKEHOLDER_EVALUATION_FORMS','Stakeholder Evaluation Forms'),
    (24,'LOCAL_STAKEHOLDER_CONSULTATION_REPORT','Local Stakeholder Consultation Report'),
    (25,'BASELINE_SURVEY_REPORT__EXCEL_FILE','Baseline survey report / excel file'),
    (26,'KITCHEN_SURVEYS__KITCHEN_PERFORMANCE_TEST_RESULTS','Kitchen Surveys & Kitchen Performance Test results'),
    (27,'EMISSION_FACTOR_CALCULATION_EXCEL_FILE','Emission Factor Calculation Excel File'),
    (28,'OBJECTIVE_OBSERVER_ASSESSMENT_REPORT','Objective Observer Assessment Report'),
    (29,'ENVIRONMENTAL_IMPACT_ASSESSMENT_REPORT','Environmental Impact Assessment Report'),
    (30,'PROJECT_FEASIBILITY_REPORT','Project feasibility report'),
    (31,'KEY_PROJECT_INFORMATION','Key Project Information'),
    (32,'ADDITIONALITY_REQUIREMENTS','Additionality Requirements'),
    (33,'SAFEGUARDING_PRINCIPLES_REQUIREMENTS','Safeguarding Principles Requirements'),
    (34,'LOCAL_STAKEHOLDER_CONSULTATION','Local Stakeholder Consultation'),
    (35,'VALIDATION_REPORT_VVB_STATEMENT','Validation Report/VVB Statement'),
    (36,'NEW_AREA_SHAPEFILES','New area shapefiles'),
    (37,'ADDITIONALITY_TOOL','Additionality tool'),
    (38,'ADDITIONALITY_TEST','Additionality test'),
    (39,'VVB_CHECKLIST_SUGGESTED_OOS','VVB Checklist - Suggested OOs'),
    (40,'VALIDATION_APPRAISAL_REPORT_OR_VERIFICATION_APPRAISAL_REPORT','Validation Appraisal Report or Verification Appraisal Report'),
    (41,'VALIDATION_REPORT_OR_VERIFICATION_REPORT','Validation Report or Verification Report'),
    (42,'SOIL_CARBON_REVIEW_REPORT','Soil Carbon Review Report'),
    (43,'SOIL_CARBON_TOOL','Soil Carbon Tool'),
    (44,'MONITORING_AND_REPORTING_PLAN','Monitoring and Reporting Plan'),
    (46,'PRELIMINARY_REVIEW_FORM','Preliminary Review Form'),
    (47,'SDG_TOOL','SDG Tool'),
    (48,'ANNUAL_REPORT','Annual Report'),
    (49,'PRELIMINARY_REVIEW_REQUEST_FORM','Preliminary Review Request Form'),
    (999999,'OTHER','Other');
