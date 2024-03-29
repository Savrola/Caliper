package com.obtuse.db.raw.devutils;

import com.obtuse.db.raw.ElephantConnection;
import com.obtuse.db.raw.exceptions.ObtuseJDBCDriverLoadFailedException;
import com.obtuse.db.raw.exceptions.ObtuseJDBCgetConnectionFailedException;
import com.obtuse.util.Logger;

import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;

/**
 * Parse the metadata of a database and emit a Java class that can be used to carry around tuples from the class and a
 * Java class that describes the metadata of each class.
 * <p/>
 * Copyright © 2012 Daniel Boulet.
 * Those parts which describe Invidi-specific database tables are Copyright © 2008-2009 Invidi Technologies Corporation.
 */

@SuppressWarnings({ "ClassWithoutToString" })
public class MetaDataParserUtils {

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

//    static {
//        _dbTypesToSavrolaTypes.put(
//                "int4", new SavrolaTypeName(
//                        "int4", "Int", "int", "Integer",
//                        "Int", "Int", false, false, false
//                )
//        );
//        _dbTypesToSavrolaTypes.put(
//                "int2", new SavrolaTypeName(
//                        "int2", "Short", "short", "Short",
//                        "Short", "Short", false, false, false
//                )
//        );
//        _dbTypesToSavrolaTypes.put(
//                "float4", new SavrolaTypeName(
//                        "float4", "Float", "float", "Float",
//                        "Float", "Float", false, false, false
//                )
//        );
//        _dbTypesToSavrolaTypes.put(
//                "float8", new SavrolaTypeName(
//                        "float8", "Double", "double", "Double",
//                        "Double", "Double", false, false, false
//                )
//        );
//        _dbTypesToSavrolaTypes.put(
//                "bigserial", new SavrolaTypeName(
//                        "bigserial", "Serial8", "long",
//                        "Long", "Long", "Long", false, false, true
//                )
//        );
//        _dbTypesToSavrolaTypes.put(
//                "serial", new SavrolaTypeName(
//                        "serial", "Serial", "int",
//                        "Integer", "Int", "Int", false, false, true
//                )
//        );
//        _dbTypesToSavrolaTypes.put(
//                "text", new SavrolaTypeName(
//                        "text", "Text", "String",
//                        "String", "String", "String", false, false, false
//                )
//        );
//        _dbTypesToSavrolaTypes.put(
//                "varchar", new SavrolaTypeName(
//                        "text", "Text", "String",
//                        "String", "String", "String", false, false, false
//                )
//        );
//        _dbTypesToSavrolaTypes.put(
//                "_text", new SavrolaTypeName(
//                        "_text", "TextArray", "java.util.Array<String>",
//                        "java.util.Array<String>", "Object", "Object", true, true, false
//                )
//        );
////        _dbTypesToSavrolaTypes.put(
////                "_int4", new SavrolaTypeName(
////                "_int4", "IntArray", "java.sql.Array",
////                "java.sql.Array", "Object", "Object", false, true, false
////        )
////        );
//        _dbTypesToSavrolaTypes.put(
//                "_int4", new SavrolaTypeName(
//                        "_int4", "IntArray", "String",
//                        "String", "String", "String", false, false, false
//                )
//        );
//        _dbTypesToSavrolaTypes.put(
//                "bpchar", new SavrolaTypeName(
//                        "bpchar", "Text", "String",
//                        "String", "String", "String", false, false, false
//                )
//        );
//        _dbTypesToSavrolaTypes.put(
//                "bytea", new SavrolaTypeName( "bytea", "Bytes", "byte[]", "byte[]", "Bytes", "Bytes", false, false, false )
//        );
//        _dbTypesToSavrolaTypes.put(
//                "timestamptz", new SavrolaTypeName(
//                        "timestamptz", "TimestampTZ", "java.sql.Timestamp",
//                        "java.sql.Timestamp", "Timestamp", "Timestamp", true, false, false
//                )
//        );
//        _dbTypesToSavrolaTypes.put(
//                "timestamp", new SavrolaTypeName(
//                        "timestamp", "Timestamp", "java.sql.Timestamp",
//                        "java.sql.Timestamp", "Timestamp", "Timestamp", true, false, false
//                )
//        );
//        _dbTypesToSavrolaTypes.put(
//                "date", new SavrolaTypeName(
//                        "date", "Date", "java.sql.Date", "java.sql.Date", "Date", "Date", true, false, false
//                )
//        );
//        _dbTypesToSavrolaTypes.put(
//                "int8", new SavrolaTypeName(
//                        "int8", "Long", "long",
//                        "Long", "Long", "Long", false, false, false
//                )
//        );
//        _dbTypesToSavrolaTypes.put(
//                "bool", new SavrolaTypeName(
//                        "bool", "Boolean", "boolean", "Boolean",
//                        "Boolean", "Boolean", false, false, false
//                )
//        );
//        _dbTypesToSavrolaTypes.put(
//                "inet", new SavrolaTypeName(
//                        "inet", "Inet", "org.postgresql.util.PGobject",
//                        "org.postgresql.util.PGobject", "Object", "Object", true, true, false
//                )
//        );
//        _dbTypesToSavrolaTypes.put(
//                "macaddr", new SavrolaTypeName(
//                        "macaddr", "Macaddr", "org.postgresql.util.PGobject",
//                        "org.postgresql.util.PGobject", "Object", "Object", true, true, false
//                )
//        );
//        _dbTypesToSavrolaTypes.put(
//                "money", new SavrolaTypeName(
//                        "money", "Money", "java.math.BigDecimal",
//                        "java.math.BigDecimal", "BigDecimal", "BigDecimal", false, false, false
//                )
//        );
//    }

    private MetaDataParserUtils() {
        super();
    }

    @SuppressWarnings({ "SameParameterValue" })
    public static void parse(
            ElephantConnection elephantConnection,
            String dbName,
            String schemaPattern,
            String tableNamePattern,
            ExpectedTableInfo[] expectedTableNamesArray,
            DbClassGenerator classGenerator,
            boolean generateClasses
    ) {

        // Build a map containing the table names we expect to find.

        Map<String, ExpectedTableInfo> expectedTableNames = new HashMap<String, ExpectedTableInfo>();
        for ( ExpectedTableInfo expectedTableInfo : expectedTableNamesArray ) {
            expectedTableNames.put( expectedTableInfo.getExpectedTableName().toLowerCase(), expectedTableInfo );
        }

        try {

            DatabaseMetaData metadata = elephantConnection.c().getMetaData();
            ResultSet tableNamesRS = metadata.getTables( null, schemaPattern, tableNamePattern, new String[] { "TABLE" } );
            boolean gotError = false;
            while ( tableNamesRS.next() ) {
                String tableName = tableNamesRS.getString( "TABLE_NAME" );
                if ( "data_insertion".equals( tableName ) ) {

                    Logger.logMsg( "got it" );

                }
                if ( generateClasses ) {
                    Logger.logMsg(
                            tableName + ":" /* +
                        ", table schem = " + tableNamesRS.getString( "TABLE_SCHEM" ) +
                        ", table cat = " + tableNamesRS.getString( "TABLE_CAT" ) +
                        ", table type = " + tableNamesRS.getString( "TABLE_TYPE" ) +
                        ", remarks = " + tableNamesRS.getString( "REMARKS" ) */
                    );
                }

                if ( expectedTableNames.containsKey( tableName.toLowerCase() ) ) {

                    ExpectedTableInfo expectedTableInfo = expectedTableNames.get( tableName.toLowerCase() );
                    if ( expectedTableInfo.isDone() ) {

                        Logger.logErr(
                                "table " + tableName +
                                " already processed - why did we see it twice in the same database???"
                        );

                        System.exit( 1 );

                    }
                    expectedTableNames.remove( tableName.toLowerCase() );

                    expectedTableInfo.markDone();

                    TableMetaData tableMetaData = new TableMetaData( expectedTableInfo );

                    ResultSet columns = metadata.getColumns( null, schemaPattern, tableName, "%" );
                    while ( columns.next() ) {
                        String columnName = columns.getString( "COLUMN_NAME" );
                        String typeName = columns.getString( "TYPE_NAME" );
                        int columnSize = columns.getInt( "COLUMN_SIZE" );
                        int decimalDigits = columns.getInt( "DECIMAL_DIGITS" );
                        int nullable = columns.getInt( "NULLABLE" );

                        ColumnMetaData columnMetaData =
                                new ColumnMetaData(
                                        columnName,
                                        typeName,
                                        DbClassGeneratorV2.lookupTypeInfo( typeName ),
                                        columnSize,
                                        decimalDigits,
                                        nullable
                                );
                        tableMetaData.addColumnMetaData( columnMetaData );

                    }

                    columns.close();

                    if ( generateClasses ) {

                        try {

                            classGenerator.generateTableInfoClass( tableMetaData );
                            classGenerator.generateTupleCarrierClass( tableMetaData );

                        } catch ( FileNotFoundException e ) {

                            //noinspection CallToPrintStackTrace
                            e.printStackTrace();
                            System.exit( 1 );

                        }

                    } else {

                        if ( !classGenerator.validateTableInfoClasses( tableMetaData ) ) {

                            Logger.logErr( "Suggestion:  re-run the metadata parser and then recompile Elephant2" );
                            System.exit( 1 );

                        }
                    }

                } else {

                    Logger.logErr(
                            "MetaDataProcessor:  unexpected table \"" + tableName + "\" found in " + dbName +
                            " database!!!"
                    );
                    gotError = true;

                }

            }

            for ( ExpectedTableInfo tableInfo : expectedTableNames.values() ) {

                Logger.logErr(
                        "MetaDataProcessor:  expected table \"" + tableInfo.getExpectedTableName() +
                        "\" not found in " +
                        dbName + " database!!!"
                );
                gotError = true;


            }
            if ( gotError ) {

                Logger.logErr( "got an error - bye!\n" );
                System.exit( 1 );

            }

            tableNamesRS.close();

        } catch ( SQLException e ) {

            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            System.exit( 1 );

        }

    }


    //    /**
    //     * Not much of a test but it did the job.
    //     */
    //
    //    private static void testConvertToCamelHumps() {
    //        System.out.println( convertToCamelHumps( "policy_agreed_time" ) );
    //        System.out.println( convertToCamelHumps( "_savrola_apps" ) );
    //        System.out.println( convertToCamelHumps( "savrola__apps" ) );
    //        System.out.println( convertToCamelHumps( "_savrola_apps_" ) );
    //    }

    public static void main( String[] args ) {

        ElephantConnection elephantConnection = new ElephantConnection(
                "127.0.0.1",
                MetaDataParserUtils.NMR_DATABASE_NAME
        );

        MetaDataParserUtils.connectToDatabase( elephantConnection );

        MetaDataParserUtils.parse(
                elephantConnection,
                MetaDataParserUtils.NMR_DATABASE_NAME,
                "nmr",
                "%",
                MetaDataParserUtils.NMR_EXPECTED_ABDW_TABLE_NAMES_ARRAY,
                new DbClassGeneratorV2( "NielsenDatabaseSupport/src/com/invidi/nielsen/abdw/auto/" ),
                true
        );

        elephantConnection = new ElephantConnection( "127.0.0.1", MetaDataParserUtils.AMRLD_DATABASE_NAME );

        MetaDataParserUtils.connectToDatabase( elephantConnection );

        MetaDataParserUtils.parse(
                elephantConnection,
                MetaDataParserUtils.AMRLD_DATABASE_NAME,
                "amrld",
                "%",
                MetaDataParserUtils.AMRLD_EXPECTED_ABDW_TABLE_NAMES_ARRAY,
                new DbClassGeneratorV2( "NielsenDatabaseSupport/src/com/invidi/nielsen/abdw/auto/" ),
                true
        );

        MetaDataParserUtils.parse(
                elephantConnection,
                MetaDataParserUtils.AMRLD_DATABASE_NAME,
                "bbm2",
                "%",
                MetaDataParserUtils.BBM2_EXPECTED_ABDW_TABLE_NAMES_ARRAY,
                new DbClassGeneratorV2( "NielsenDatabaseSupport/src/com/invidi/nielsen/abdw/auto/" ),
                true
        );

        MetaDataParserUtils.parse(
                elephantConnection,
                MetaDataParserUtils.AMRLD_DATABASE_NAME,
                "tvg",
                "%",
                MetaDataParserUtils.TVG_EXPECTED_ABDW_TABLE_NAMES_ARRAY,
                new DbClassGeneratorV2( "NielsenDatabaseSupport/src/com/invidi/nielsen/abdw/auto/" ),
                true
        );

    }

    public static void connectToDatabase( ElephantConnection elephantConnection ) {
        try {

            Properties props = new Properties();
            props.setProperty( "user", "danny" );
            // props.setProperty("password","secret");
            // props.setProperty("ssl","true");

            elephantConnection.connect( props );

        } catch ( ObtuseJDBCDriverLoadFailedException e ) {

            Logger.logErr( "unable to load JDBC driver", e );
            System.exit( 1 );

        } catch ( ObtuseJDBCgetConnectionFailedException e ) {

            Logger.logErr( "unable to connect to Savrola database", e );
            System.exit( 1 );

        }
    }

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
