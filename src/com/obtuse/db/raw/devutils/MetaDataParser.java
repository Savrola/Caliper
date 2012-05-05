package com.obtuse.db.raw.devutils;

import com.obtuse.db.raw.ElephantConnection;

/**
 * Parse the metadata of a database and emit a Java class that can be used to carry around tuples from the class and a
 * Java class that describes the metadata of each class.
 * <p/>
 * Copyright © 2012 Daniel Boulet.
 * Those parts which describe Invidi-specific database tables are Copyright © 2009 Invidi Technologies Corporation.
 */

@SuppressWarnings({ "ClassWithoutToString" })
public class MetaDataParser {

    //private static Map<String, SavrolaTypeName> _dbTypesToSavrolaTypes = new HashMap<String, SavrolaTypeName>();

    private static final ExpectedTableInfo[] NMR_EXPECTED_ABDW_TABLE_NAMES_ARRAY = {
            new ExpectedTableInfo( "nmr", "loaded_program_file", "NmrLoadedProgramFile" ),
            new ExpectedTableInfo( "nmr", "loaded_qhr_file", "NmrLoadedQhrFile" ),
            new ExpectedTableInfo( "nmr", "market", "NmrMarket" ),
            new ExpectedTableInfo( "nmr", "qhr_hut_put_record", "NmrQhrHutPutRecord" ),
            new ExpectedTableInfo( "nmr", "qhr_distributor_header_record", "NmrQhrDistributorHeaderRecord" ),
            new ExpectedTableInfo( "nmr", "qhr_distributor_record", "NmrQhrDistributorRecord" ),
            new ExpectedTableInfo( "nmr", "qhr_broadcast_channel_number", "NmrQhrBroadcastChannelNumber" ),
            new ExpectedTableInfo( "nmr", "qhr_market_intab_record", "NmrQhrMarketIntabRecord" ),
            new ExpectedTableInfo( "nmr", "qhr_market_universe_record", "NmrQhrMarketUniverseRecord" ),
            new ExpectedTableInfo( "nmr", "qhr_exclusion_record", "NmrQhrExclusionRecord" ),
            new ExpectedTableInfo( "nmr", "program_name_record", "NmrProgramNameRecord" )
    };

    private static final ExpectedTableInfo[] AMRLD_EXPECTED_ABDW_TABLE_NAMES_ARRAY = {
            new ExpectedTableInfo( "amrld", "data_source", "AmrldDataSource" ),
            new ExpectedTableInfo( "amrld", "cable_network_reference_record", "AmrldCableNetworkReferenceRecord" ),
            new ExpectedTableInfo( "amrld", "daypart_reference_record", "AmrldDaypartReferenceRecord" ),
            new ExpectedTableInfo(
                    "amrld",
                    "demographic_building_block_category_reference_record",
                    "AmrldDemographicBuildingBlockCategoryReferenceRecord"
            ),
            new ExpectedTableInfo( "amrld", "enhanced_calendar_record", "AmrldEnhancedCalendarRecord" ),
            new ExpectedTableInfo( "amrld", "enhanced_program_descriptive_record", "AmrldEnhancedProgramDescriptiveRecord" ),
            new ExpectedTableInfo( "amrld", "enhanced_program_viewing_record", "AmrldEnhancedProgramViewingRecord" ),
            new ExpectedTableInfo(
                    "amrld", "enhanced_reprocessed_programs_calendar_record", "AmrldEnhancedReprocessedProgramsCalendarRecord"
            ),
            new ExpectedTableInfo( "amrld", "enhanced_time_period_descriptive_record", "AmrldEnhancedTimePeriodDescriptiveRecord" ),
            new ExpectedTableInfo( "amrld", "enhanced_time_period_viewing_record", "AmrldEnhancedTimePeriodViewingRecord" ),
            new ExpectedTableInfo( "amrld", "feed_pattern_reference_record", "AmrldFeedPatternReferenceRecord" ),
            new ExpectedTableInfo( "amrld", "file_header_record", "AmrldFileHeaderRecord" ),
            new ExpectedTableInfo( "amrld", "file_trailer_record", "AmrldFileTrailerRecord" ),
            new ExpectedTableInfo( "amrld", "household_classification_record", "AmrldHouseholdClassificationRecord" ),
            new ExpectedTableInfo( "amrld", "household_weights_record", "AmrldHouseholdWeightsRecord" ),
            new ExpectedTableInfo( "amrld", "j_d_power_vehicle_segments_record", "AmrldJDPowerVehicleSegmentsRecord" ),
            new ExpectedTableInfo(
                    "amrld", "multi_segment_program_summary_quarter_hour_record", "AmrldMultiSegmentProgramSummaryQuarterHourRecord"
            ),
            new ExpectedTableInfo( "amrld", "n_h_i_n_h_i_h_program", "AmrldNHINHIHProgram" ),
            new ExpectedTableInfo( "amrld", "persons_classification_record", "AmrldPersonsClassificationRecord" ),
            new ExpectedTableInfo( "amrld", "persons_weights_record", "AmrldPersonsWeightsRecord" ),
            new ExpectedTableInfo( "amrld", "program_commercial_minutes_record", "AmrldProgramCommercialMinutesRecord" ),
            new ExpectedTableInfo( "amrld", "program_gap_records", "AmrldProgramGapRecords" ),
            new ExpectedTableInfo( "amrld", "program_time_for_share_record", "AmrldProgramTimeForShareRecord" ),
            new ExpectedTableInfo( "amrld", "set_descriptive_record", "AmrldSetDescriptiveRecord" ),
            new ExpectedTableInfo( "amrld", "summary_program", "AmrldSummaryProgram" ),
            new ExpectedTableInfo( "amrld", "syndicator_originator_reference_record", "AmrldSyndicatorOriginatorReferenceRecord" ),
            new ExpectedTableInfo( "amrld", "unification_interval_reference_record", "AmrldUnificationIntervalReferenceRecord" ),
            new ExpectedTableInfo( "amrld", "universe_estimate_record", "AmrldUniverseEstimateRecord" ),
    };

    private static final ExpectedTableInfo[] BBM2_EXPECTED_ABDW_TABLE_NAMES_ARRAY = {
            new ExpectedTableInfo( "bbm2", "data_insertion", "Bbm2DataInsertion" ),
            new ExpectedTableInfo( "bbm2", "demographic_interpretation", "Bbm2DemographicInterpretation" ),
            new ExpectedTableInfo( "bbm2", "demographics_household", "Bbm2DemographicsHousehold" ),
            new ExpectedTableInfo( "bbm2", "demographics_member", "Bbm2DemographicsMember" ),
            new ExpectedTableInfo( "bbm2", "household", "Bbm2Household" ),
            new ExpectedTableInfo( "bbm2", "household_income_interpretation", "Bbm2HouseholdIncomeInterpretation" ),
            new ExpectedTableInfo( "bbm2", "household_location_interpretation", "Bbm2HouseholdLocationInterpretation" ),
            new ExpectedTableInfo( "bbm2", "household_time_zone_interpretation", "Bbm2HouseholdTimeZoneInterpretation" ),
            new ExpectedTableInfo( "bbm2", "household_weight", "Bbm2HouseholdWeight" ),
            new ExpectedTableInfo( "bbm2", "member", "Bbm2Member" ),
            new ExpectedTableInfo( "bbm2", "member_education_interpretation", "Bbm2MemberEducationInterpretation" ),
            new ExpectedTableInfo( "bbm2", "network", "Bbm2Network" ),
            new ExpectedTableInfo( "bbm2", "network_status_interpretation", "Bbm2NetworkStatusInterpretation" ),
            new ExpectedTableInfo( "bbm2", "network_time_zone_interpretation", "Bbm2NetworkTimeZoneInterpretation" ),
            new ExpectedTableInfo( "bbm2", "playback", "Bbm2Playback" ),
            new ExpectedTableInfo( "bbm2", "tv_viewing", "Bbm2TvTiewing" ),
            new ExpectedTableInfo( "bbm2", "viewing", "Bbm2Viewing" )
    };

    private static final ExpectedTableInfo[] TVG_EXPECTED_ABDW_TABLE_NAMES_ARRAY = {
            new ExpectedTableInfo( "tvg", "corrected_schedule", "TvgCorrectedSchedule" ),
            new ExpectedTableInfo( "tvg", "predicted_schedule", "TvgPredictedSchedule" ),
            new ExpectedTableInfo( "tvg", "network_map", "TvgNetworkMap" ),
            new ExpectedTableInfo( "tvg", "show", "TvgShow" )
    };

    private static final String NMR_DATABASE_NAME = "abdw-nielsen-baltimore";
    private static final String AMRLD_DATABASE_NAME = "abdw-amrld";

    private MetaDataParser() {
        super();
    }

    public static void main( String[] args ) {

        ElephantConnection elephantConnection = new ElephantConnection( "127.0.0.1", NMR_DATABASE_NAME );

        MetaDataParserUtils.connectToDatabase( elephantConnection );

        //MetaDataParser processor = new MetaDataParser();

        MetaDataParserUtils.parse(
                elephantConnection,
                NMR_DATABASE_NAME,
                "nmr",
                "%",
                NMR_EXPECTED_ABDW_TABLE_NAMES_ARRAY,
                new DbClassGeneratorV2( "NielsenDatabaseSupport/src/com/invidi/nielsen/abdw/auto/" ),
                true
        );

        elephantConnection = new ElephantConnection( "127.0.0.1", AMRLD_DATABASE_NAME );

        MetaDataParserUtils.connectToDatabase( elephantConnection );

        MetaDataParserUtils.parse(
                elephantConnection,
                AMRLD_DATABASE_NAME,
                "amrld",
                "%",
                AMRLD_EXPECTED_ABDW_TABLE_NAMES_ARRAY,
                new DbClassGeneratorV2( "NielsenDatabaseSupport/src/com/invidi/nielsen/abdw/auto/" ),
                true
        );

        MetaDataParserUtils.parse(
                elephantConnection,
                AMRLD_DATABASE_NAME,
                "bbm2",
                "%",
                BBM2_EXPECTED_ABDW_TABLE_NAMES_ARRAY,
                new DbClassGeneratorV2( "NielsenDatabaseSupport/src/com/invidi/nielsen/abdw/auto/" ),
                true
        );

        MetaDataParserUtils.parse(
                elephantConnection,
                AMRLD_DATABASE_NAME,
                "tvg",
                "%",
                TVG_EXPECTED_ABDW_TABLE_NAMES_ARRAY,
                new DbClassGeneratorV2( "NielsenDatabaseSupport/src/com/invidi/nielsen/abdw/auto/" ),
                true
        );

    }

    //public static void connectToDatabase( ElephantConnection elephantConnection ) {
    //
    //    try {
    //
    //        Properties props = new Properties();
    //        props.setProperty( "user", "danny" );
    //        // props.setProperty("password","secret");
    //        // props.setProperty("ssl","true");
    //
    //        elephantConnection.connect( props );
    //
    //    } catch ( ObtuseJDBCDriverLoadFailedException e ) {
    //
    //        Logger.logErr( "unable to load JDBC driver", e );
    //        System.exit( 1 );
    //
    //    } catch ( ObtuseJDBCgetConnectionFailedException e ) {
    //
    //        Logger.logErr( "unable to connect to Savrola database", e );
    //        System.exit( 1 );
    //
    //    }
    //
    //}

//    public static void validateClasses(
//            ElephantConnection elephantConnection,
//            String databaseName
//    ) {
//
//        parseClasses( elephantConnection, databaseName, false );
//
//    }
//
//    private static void parseClasses(
//            ElephantConnection elephantConnection,
//            String databaseName,
//            boolean generateClasses
//    ) {
//
//        MetaDataParserUtils parser = new MetaDataParserUtils();
//
//        parser.parse(
//                elephantConnection,
//                databaseName,
//                "nmr",
//                "%",
//                NMR_EXPECTED_ABDW_TABLE_NAMES_ARRAY,
//                new DbClassGeneratorV2(),
//                generateClasses
//        );
//
//        parser.parse(
//                elephantConnection,
//                databaseName,
//                "amrld",
//                "%",
//                AMRLD_EXPECTED_ABDW_TABLE_NAMES_ARRAY,
//                new DbClassGeneratorV2(),
//                generateClasses
//        );
//
//    }

}
