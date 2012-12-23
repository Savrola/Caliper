package com.obtuse.db.raw.devutils;

import com.obtuse.db.raw.ti.DBType;
import com.obtuse.db.raw.ti.TableInfo;
import com.obtuse.util.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Generate a {@link TableInfo} class and a tuple carrier class which manages the column values directly.
 * <p/>
 * Copyright © 2012 Daniel Boulet.
 */

@SuppressWarnings({ "ClassWithoutToString" })
public class DbClassGeneratorV2 implements DbClassGenerator {

    private static final Map<String, SavrolaTypeName> s_dbTypesToSavrolaTypes = new HashMap<String, SavrolaTypeName>();

    private final String _autoDirectoryName;

    static {

        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "int4", new SavrolaTypeName(
                "int4", "Int", "int", "Integer",
                "Int", "Int", false, false, false
        )
        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "int2", new SavrolaTypeName(
                "int2", "Short", "short", "Short",
                "Short", "Short", false, false, false
        )
        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "float4", new SavrolaTypeName(
                "float4", "Float", "float", "Float",
                "Float", "Float", false, false, false
        )
        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "float8", new SavrolaTypeName(
                "float8", "Double", "double", "Double",
                "Double", "Double", false, false, false
        )
        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "bigserial", new SavrolaTypeName(
                "bigserial", "Serial8", "long",
                "Long", "Long", "Long", false, false, true
        )
        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "serial", new SavrolaTypeName(
                "serial", "Serial", "int",
                "Integer", "Int", "Int", false, false, true
        )
        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "text", new SavrolaTypeName(
                "text", "Text", "String",
                "String", "String", "String", false, false, false
        )
        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "varchar", new SavrolaTypeName(
                "text", "Text", "String",
                "String", "String", "String", false, false, false
        )
        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "_text", new SavrolaTypeName(
                "_text", "TextArray", "java.util.Array<String>",
                "java.util.Array<String>", "Object", "Object", true, true, false
        )
        );
//        _dbTypesToSavrolaTypes.put(
//                "_int4", new SavrolaTypeName(
//                "_int4", "IntArray", "java.sql.Array",
//                "java.sql.Array", "Object", "Object", false, true, false
//        )
//        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "_int4", new SavrolaTypeName(
                "_int4", "IntArray", "String",
                "String", "String", "String", false, false, false
        )
        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "bpchar", new SavrolaTypeName(
                "bpchar", "Text", "String",
                "String", "String", "String", false, false, false
        )
        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "bytea",
                new SavrolaTypeName( "bytea", "Bytes", "byte[]", "byte[]", "Bytes", "Bytes", false, false, false )
        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "timestamptz", new SavrolaTypeName(
                "timestamptz", "TimestampTZ", "java.sql.Timestamp",
                "java.sql.Timestamp", "Timestamp", "Timestamp", true, false, false
        )
        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "timestamp", new SavrolaTypeName(
                "timestamp", "Timestamp", "java.sql.Timestamp",
                "java.sql.Timestamp", "Timestamp", "Timestamp", true, false, false
        )
        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "date", new SavrolaTypeName(
                "date", "Date", "java.sql.Date", "java.sql.Date", "Date", "Date", true, false, false
        )
        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "int8", new SavrolaTypeName(
                "int8", "Long", "long",
                "Long", "Long", "Long", false, false, false
        )
        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "bool", new SavrolaTypeName(
                "bool", "Boolean", "boolean", "Boolean",
                "Boolean", "Boolean", false, false, false
        )
        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "inet", new SavrolaTypeName(
                "inet", "Inet", "org.postgresql.util.PGobject",
                "org.postgresql.util.PGobject", "Object", "Object", true, true, false
        )
        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "macaddr", new SavrolaTypeName(
                "macaddr", "Macaddr", "org.postgresql.util.PGobject",
                "org.postgresql.util.PGobject", "Object", "Object", true, true, false
        )
        );
        DbClassGeneratorV2.s_dbTypesToSavrolaTypes.put(
                "money", new SavrolaTypeName(
                "money", "Money", "java.math.BigDecimal",
                "java.math.BigDecimal", "BigDecimal", "BigDecimal", false, false, false
        )
        );
    }

    public DbClassGeneratorV2( @SuppressWarnings("SameParameterValue") String autoDirectoryName ) {

        super();

        _autoDirectoryName = autoDirectoryName;

    }

    /**
     * Convert an identifier which uses underscores to separate its parts into a CamelHumpsStyle name. For example,
     * this_is_a_test is converted to thisIsATest. Sequences of two or more underscores are treated as though there is
     * only only underscore. Underscores at the end of the identifier are ignored.
     * <p/>
     * If the caller wants the first letter of the identifier to be up-cased, they should prepend an underscore to the
     * identifier being passed to this method.
     *
     * @param identifier the identifier to be converted to CamelHumpsStyle.
     * @return the identifier in CamelHumpsStyle.
     */

    public static String convertToCamelHumps( String identifier ) {

        String id = identifier;
        String rval = "";

        int underscoreOffset;
        //noinspection NestedAssignment
        while ( ( underscoreOffset = id.indexOf( (int)'_' ) ) >= 0 ) {
            rval += id.substring( 0, underscoreOffset );
            underscoreOffset += 1;
            id = id.substring( underscoreOffset );
            if ( !id.isEmpty() ) {
                id = id.substring( 0, 1 ).toUpperCase() + id.substring( 1 );
            }
        }

        rval += id;

        return rval;
    }

    public void generateTableInfoClass( TableMetaData tableMetaData )
            throws
            FileNotFoundException {

        String tableName = tableMetaData.getTableName();
        String schemaName = tableMetaData.getSchemaName();
        String tupleCarrierName = tableMetaData.getExpectedTableInfo().getTupleCarrierName();

        PrintWriter writer = new PrintWriter(
                _autoDirectoryName + tupleCarrierName + "TableInfo.java"
        );

        try {

            writer.println( "package com.invidi.nielsen.abdw.auto;" );
            writer.println();

            writer.println( "import com.obtuse.db.raw.dbdata.Tuple2;" );
            writer.println( "import com.obtuse.db.raw.ti.TableInfo;" );
            writer.println();
            writer.println( "import java.sql.ResultSet;" );
            writer.println( "import java.sql.SQLException;" );
            writer.println();
            writer.println( "/**" );
            writer.println( " * Describe the " + tableName + " table." );
            writer.println( " * <p/>" );
            writer.println( " * Copyright © 2008 Invidi Technologies Corporation." );
            writer.println( " */" );
            writer.println();
            writer.println( "@SuppressWarnings( { \"MagicNumber\", \"ClassWithoutToString\" } )" );
            writer.println( "public class " + tupleCarrierName + "TableInfo extends TableInfo {" );
            writer.println();
            writer.println( "/*" );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println( " */" );
            writer.println();

            // Generate the column name constants.

            ColumnMetaData[] columnMetaData = tableMetaData.getColumnMetaDataArray();
            for ( ColumnMetaData metaData : columnMetaData ) {
                String columnName = metaData.getDbColumnName();
                writer.println(
                        "    public static final String " + columnName.toUpperCase() + " = \"" +
                        columnName.toLowerCase() + "\";"
                );
                writer.println();
            }

            // Generate the singleton's declaration and constructor invocation.

            writer.println(
                    "    private static " + tupleCarrierName + "TableInfo _singleton = new " + tupleCarrierName +
                    "TableInfo();"
            );
            writer.println();

            // Generate the private constructor.

            writer.println( "    private " + tupleCarrierName + "TableInfo() {" );
            writer.println( "        super( \"" + schemaName + "\", \"" + tableName.toLowerCase() + "\" );" );
            writer.println();

            for ( ColumnMetaData metaData : columnMetaData ) {
                String columnName = metaData.getDbColumnName();
                writer.println(
                        "        dc" + metaData.getSavrolaTypeInfo().getSavrolaType() + "( \"" +
                        columnName.toUpperCase() + "\" );"
                );
            }

            writer.println();
            writer.println( "        freeze();" );
            writer.println( "    }" );
            writer.println();

            // Generate the singleton getter.

            writer.println( "    public static " + tupleCarrierName + "TableInfo ti() {" );
            writer.println( "        return _singleton;" );
            writer.println( "    }" );
            writer.println();

            // Generate a method that constructs an instance of the carrier class.

            writer.println( "    /**" );
            writer.println(
                    "     * Create a new {@link " + tupleCarrierName +
                    "} instance from the current row of a {@link ResultSet}."
            );
            writer.println( "     * <p/>" );
            writer.println( "     * <b>IMPORTANT:</b> this method assumes that the {@link ResultSet} was the result " +
                            "of" );
            writer.println( "     * a query equivalent to \"SELECT * FROM " + tableName + " ...\"." );
            writer.println( "     *" );
            writer.println(
                    "     * @param rs the result set containing the values for the about to be created instance."
            );
            writer.println( "     * @return the newly created instance." );
            writer.println( "     * @throws SQLException if something goes wrong in JDBC land." );
            writer.println( "     */" );
            writer.println();
            writer.println( "    public Tuple2 makeNewInstance( ResultSet rs )" );
            writer.println( "            throws" );
            writer.println( "            SQLException {" );
            writer.println();
            writer.println( "        return new " + tupleCarrierName + "(" );

            for ( int i = 0; i < columnMetaData.length; i += 1 ) {
                SavrolaTypeName savrolaTypeName = columnMetaData[i].getSavrolaTypeInfo();
                String typeName;
                if ( columnMetaData[i].getNullable() == 1 && !"money".equals( columnMetaData[i].getDbType() ) ) {
                    typeName = "Object";
                } else {
                    typeName = savrolaTypeName.getResultSetType();
                }
                writer.println(
                        "            " +
                        ( columnMetaData[i].getNullable() == 1 ? "(" + savrolaTypeName.getJavaType( 1 ) + ")" : "" ) +
                        (
                                savrolaTypeName.requiresCast() ?
                                "(" + savrolaTypeName.getJavaType(
                                        columnMetaData[i].getNullable()
                                ) + ")" : ""
                        ) +
                        "rs.get" + typeName + "( " + (
                                i + 1
                        ) + " )" + ( i < columnMetaData.length - 1 ? "," : "" )
                );
            }

            writer.println( "        );" );
            writer.println();
            writer.println( "    }" );

            // Close the class.

            writer.println( "}" );

        } finally {

            writer.close();

        }

    }

    public void generateTupleCarrierClass( TableMetaData tableMetaData )
            throws
            FileNotFoundException {

        String tableName = tableMetaData.getTableName();
        String schemaTableName = tableMetaData.getSchemaTableName();
        String tupleCarrierName = tableMetaData.getExpectedTableInfo().getTupleCarrierName();

        PrintWriter writer =
                new PrintWriter(
                        "NielsenDatabaseSupport/src/com/invidi/nielsen/abdw/auto/" + tupleCarrierName + ".java"
                );

        try {

            writer.println( "package com.invidi.nielsen.abdw.auto;" );
            writer.println();
            writer.println( "import com.obtuse.db.raw.ElephantConnection;" );
            writer.println( "import com.obtuse.db.raw.dbdata.BundledKeys;" );
            writer.println( "import com.obtuse.db.raw.dbdata.Tuple2;" );
            writer.println( "import com.obtuse.util.ObtuseUtil5;" );
            writer.println();
            writer.println( "import java.sql.PreparedStatement;" );
            writer.println( "import java.sql.ResultSet;" );
            writer.println( "import java.sql.SQLException;" );
            writer.println();
            writer.println( "/**" );
            writer.println( " * In-memory representation of a tuple from the " + tableName + " database table." );
            writer.println( " * <p/>" );
            writer.println( " * Copyright © 2008 Invidi Technologies Corporation." );
            writer.println( " */" );
            writer.println();
            writer.println( "@SuppressWarnings( { \"ClassWithoutToString\", \"MagicNumber\" } )" );
            writer.println( "public class " + tupleCarrierName + " extends Tuple2 {" );
            writer.println();
            writer.println( "/*" );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println(
                    " * IMPORTANT:  this file is automatically generated via {@link MetaDataParserUtils} - DO NOT " +
                    "EDIT THIS FILE"
            );
            writer.println( " */" );
            writer.println();

            ColumnMetaData[] columnMetaData = tableMetaData.getColumnMetaDataArray();
            for ( ColumnMetaData metaData : columnMetaData ) {
                String columnName = metaData.getDbColumnName();
                writer.println(
                        "    public static final int " + columnName.toUpperCase() + "_IX = ti().getColumnIndex( " +
                        tupleCarrierName + "TableInfo." + columnName.toUpperCase() + " );"
                );
                writer.println();
            }

            for ( ColumnMetaData metaData : columnMetaData ) {
                writer.println( "    /**" );
                writer.println(
                        "     * The " + metaData.getDbColumnName() + "/" + metaData.getSavrolaColumnName() +
                        " column."
                );
                writer.println( "     * <br>DB column name:  " + metaData.getDbColumnName() );
                writer.println( "     * <br>DB type name:  " + metaData.getDbType() );
                writer.println( "     * <br>DB column size:  " + metaData.getColumnSize() );
                writer.println( "     * <br>DB decimal digits:  " + metaData.getDecimalDigits() );
                writer.println( "     * <br>DB nullable:  " + metaData.getNullable() );
                writer.println( "     */" );
                writer.println();
                writer.println(
                        "    private " + metaData.getSavrolaTypeInfo().getJavaType( metaData.getNullable() ) +
                        " _" + metaData.getSavrolaColumnName() + ";"
                );
                writer.println();
            }

            writer.println( "    /**" );
            writer.println( "     * Intended for use by serialization mechanism." );
            writer.println( "     */" );
            writer.println();
            writer.println( "    public " + tupleCarrierName + "() {" );
            writer.println( "        super();" );
            writer.println( "    }" );
            writer.println();

            writer.println( "    public " + tupleCarrierName + "(" );

            for ( int i = 0; i < columnMetaData.length; i += 1 ) {
                String typeName = columnMetaData[i].getSavrolaTypeInfo().getJavaType( columnMetaData[i].getNullable() );
                String columnName = columnMetaData[i].getSavrolaColumnName();
                writer.println(
                        "            " + typeName + " " + columnName + ( i < columnMetaData.length - 1 ? "," : "" )
                );
            }

            writer.println( "    ) {" );
            writer.println( "        super( " + tupleCarrierName + "TableInfo.ti() );" );
            writer.println();

            for ( ColumnMetaData metaData : columnMetaData ) {
                if ( metaData.getSavrolaTypeInfo().doesAssignmentRequireCloning() ) {
                    writer.println(
                            "        _" + metaData.getSavrolaColumnName() + " = " +
                            metaData.getSavrolaColumnName() + " == null ? null : (" +
                            metaData.getSavrolaTypeInfo().getJavaType( metaData.getNullable() ) + ")" +
                            metaData.getSavrolaColumnName() + ".clone();"
                    );
                } else {
                    writer.println(
                            "        _" + metaData.getSavrolaColumnName() + " = " +
                            metaData.getSavrolaColumnName() + ";"
                    );
                }
            }

            writer.println();
            writer.println( "    }" );
            writer.println();

            writer.println( "    public static " + tupleCarrierName + "TableInfo ti() {" );
            writer.println();
            writer.println( "        return " + tupleCarrierName + "TableInfo.ti();" );
            writer.println();
            writer.println( "    }" );
            writer.println();

            generateInsertMethod( writer, schemaTableName, tupleCarrierName, columnMetaData );

            generateUpdateResultSetMethod( writer, tableName, columnMetaData );

            // Generate the getters and setters.

            for ( ColumnMetaData metaData : columnMetaData ) {

                String javaTypeName = metaData.getSavrolaTypeInfo().getJavaType( metaData.getNullable() );
                String savrolaColumnName = metaData.getSavrolaColumnName();
                String dbColumnName = metaData.getDbColumnName();
                String ucSavrolaColumnName = savrolaColumnName.substring( 0, 1 ).toUpperCase() + savrolaColumnName.substring( 1 );

                writer.println( "    public " + javaTypeName + " get" + ucSavrolaColumnName + "() {" );
                writer.println();
                if ( metaData.getSavrolaTypeInfo().doesAssignmentRequireCloning() ) {
                    writer.println(
                            "        return _" + savrolaColumnName + " == null ? null : " +
                            "(" + javaTypeName + ")_" + savrolaColumnName + ".clone();"
                    );
                } else {
                    writer.println( "        return _" + savrolaColumnName + ";" );
                }
                writer.println();
                writer.println( "    }" );
                writer.println();

                if ( metaData.getSavrolaTypeInfo().isSerialType() ) {

                    writer.println(
                            "    // No setter for " + savrolaColumnName + " because it is a serial column in the database"
                    );
                    writer.println();

                } else {

                    writer.println(
                            "    public void set" + ucSavrolaColumnName + "( " + javaTypeName + " " + savrolaColumnName + " ) {"
                    );
                    writer.println();
                    //                writer.println("        _" + savrolaColumnName + " = copy" + loaTypeName + "Value(
                    // " + savrolaColumnName + " );" );
                    if ( metaData.getSavrolaTypeInfo().doesAssignmentRequireCloning() ) {
                        writer.println(
                                "        if ( " + savrolaColumnName + " == null ) {"
                        );
                        writer.println();
                        writer.println(
                                "            _" + savrolaColumnName + " = null;"
                        );
                        writer.println();
                        writer.println(
                                "        } else {"
                        );
                        writer.println();
                        writer.println(
                                "            _" + savrolaColumnName + " = (" + javaTypeName + ")" + savrolaColumnName +
                                ".clone();"
                        );
                        writer.println();
                        writer.println(
                                "        }"
                        );
                    } else {

                        writer.println( "        _" + savrolaColumnName + " = " + savrolaColumnName + ";" );

                    }

                    writer.println();
                    writer.println( "        setDirty( " + dbColumnName.toUpperCase() + "_IX );" );
                    writer.println();
                    writer.println( "    }" );
                    writer.println();

                }

            }

            writer.println( "}" );

        } finally {

            writer.close();

        }

    }

    @SuppressWarnings({ "UnusedDeclaration" })
    public static SavrolaTypeName lookupTypeInfo( String typeName ) {

        return DbClassGeneratorV2.s_dbTypesToSavrolaTypes.get( typeName );

    }

    //public static Map<String, SavrolaTypeName> getdbTypesToSavrolaTypes() {
    //
    //    return Collections.unmodifiableMap( _dbTypesToSavrolaTypes );
    //
    //}

    /**
     * Generate a method which updates a ResultSet with the values in an instance.
     *
     * @param writer         where to write the method.
     * @param tableName      the table that the method is being generated for.
     * @param columnMetaData description of the table's columns.
     */

    @SuppressWarnings({ "UnusedDeclaration" })
    private void generateUpdateResultSetMethod(
            PrintWriter writer,
            String tableName,
            ColumnMetaData[] columnMetaData
    ) {

        writer.println( "    public void updateResultSet( ResultSet rs )" );
        writer.println( "            throws" );
        writer.println( "            SQLException {" );
        writer.println();

        int ix = 1;
        for ( ColumnMetaData metaData : columnMetaData ) {

            SavrolaTypeName savrolaTypeName = metaData.getSavrolaTypeInfo();
            if ( savrolaTypeName.isSerialType() ) {

                writer.println( "        // skipping serial column " + metaData.getDbColumnName() );

            } else {

                writer.println(
                        "            " + (
                                metaData.getNullable() == 1 ? "if ( _" + metaData.getSavrolaColumnName() +
                                                              " == null ) { rs.updateObject( " + ix +
                                                              ", null ); } else { " : ""
                        ) +
                        "rs.update" + savrolaTypeName.getPreparedStatementType() + "( " +
                        ix + ", " +
                        (
                                savrolaTypeName.doesAssignmentRequireCloning()
                                ?
                                "_" + metaData.getSavrolaColumnName() + " == null ? null : (" +
                                savrolaTypeName.getJavaType( metaData.getNullable() ) + ")_" +
                                metaData.getSavrolaColumnName() + ".clone()"
                                :
                                "_" + metaData.getSavrolaColumnName()
                        ) +
                        " );" +
                        ( metaData.getNullable() == 1 ? " }" : "" )
                );
            }

            ix += 1;

        }

        writer.println();

        writer.println( "    }" );
        writer.println();
    }

    /**
     * Generate a method which inserts an instance into the appropriate DB table.
     *
     * @param writer           where to write the method.
     * @param schemaTableName  the table that the method is being generated for.
     * @param tupleCarrierName the class used to carry around instances of the table's rows.
     * @param columnMetaData   description of the table's columns.
     */

    private void generateInsertMethod(
            PrintWriter writer,
            String schemaTableName,
            String tupleCarrierName,
            ColumnMetaData[] columnMetaData
    ) {

        writer.println(
                "    public BundledKeys insert( ElephantConnection elephantConnection, " +
                "boolean retrieveAutogeneratedKeys )"
        );
        writer.println( "            throws" );
        writer.println( "            SQLException {" );

        String stmt = "INSERT INTO " + schemaTableName + " (";

        String comma = "";
        for ( ColumnMetaData metaData : columnMetaData ) {
            if ( metaData.getSavrolaTypeInfo().isSerialType() ) {
            } else {
                stmt += comma + " " + metaData.getDbColumnName();
                comma = ", ";
            }
        }

        stmt += " ) VALUES (";

        comma = "";
        for ( ColumnMetaData metaData : columnMetaData ) {
            if ( metaData.getSavrolaTypeInfo().isSerialType() ) {
            } else {
                stmt += comma + " ?";
                comma = ", ";
            }
        }

        stmt += " )";

        writer.println( "        String insertStatement = \"" + stmt + "\";" );
        writer.println();
        writer.println( "        PreparedStatement ps = elephantConnection.c().prepareStatement( insertStatement );" );
        writer.println();
        writer.println( "        try {" );
        writer.println();

        int ix = 1;
        for ( ColumnMetaData metaData : columnMetaData ) {

            SavrolaTypeName savrolaTypeName = metaData.getSavrolaTypeInfo();
            if ( savrolaTypeName.isSerialType() ) {

                writer.println( "            // skipping serial column " + metaData.getDbColumnName() );

            } else {

                writer.println(
                        "            " + (
                                metaData.getNullable() == 1 ? "if ( _" + metaData.getSavrolaColumnName() +
                                                              " == null ) { ps.setObject( " + ix +
                                                              ", null ); } else { " : ""
                        ) +
                        "ps.set" + savrolaTypeName.getPreparedStatementType() + "( " +
                        ix + ", " +
                        (
                                savrolaTypeName.doesAssignmentRequireCloning()
                                ?
                                "_" + metaData.getSavrolaColumnName() + " == null ? null : (" +
                                savrolaTypeName.getJavaType( metaData.getNullable() ) + ")_" +
                                metaData.getSavrolaColumnName() + ".clone()"
                                :
                                "_" + metaData.getSavrolaColumnName()
                        ) +
                        " );" +
                        ( metaData.getNullable() == 1 ? " }" : "" )
                );
                ix += 1;

            }

        }

        writer.println();

        writer.println(
                "            return executeInsertStatement( insertStatement, ps, elephantConnection, " +
                tupleCarrierName +
                "TableInfo.ti().getSequenceNames(), " + tupleCarrierName +
                "TableInfo.ti().getSerialColumnNumbers(), retrieveAutogeneratedKeys );"
        );

        writer.println();

        writer.println( "        } finally {" );
        writer.println();
        writer.println( "            ObtuseUtil5.closeQuietly( ps );" );
        writer.println();
        writer.println( "        }" );
        writer.println();
        writer.println( "    }" );
        writer.println();
    }

    /**
     * Verify that the current versions of the {@link TableInfo} classes match the current database metadata.
     *
     * @param tableMetaData the current database metadata.
     * @return true if they match; false (and messages sent to Logger.logErr) otherwise.
     */

    @SuppressWarnings({ "BooleanMethodNameMustStartWithQuestion" })
    public boolean validateTableInfoClasses( TableMetaData tableMetaData ) {

        @SuppressWarnings({ "UnusedDeclaration", "UnusedAssignment" })
        String tableName = tableMetaData.getTableName();
        String tupleCarrierName = tableMetaData.getExpectedTableInfo().getTupleCarrierName();

        String tableInfoClassName = "com.obtuse.nahanni.elephant2.auto." + tupleCarrierName + "TableInfo";

        try {

            //noinspection unchecked
            Class<? extends TableInfo> tableInfoClass = (Class<? extends TableInfo>)Class.forName( tableInfoClassName );

            //noinspection NullArgumentToVariableArgMethod,TypeParameterExplicitlyExtendsObject
            Method tiMethod = tableInfoClass.getMethod( "ti", (Class<? extends Object>[])null );

            TableInfo ti = (TableInfo)tiMethod.invoke( null, (Object[])null );
            String[] columnNames = ti.getColumnNames();
            DBType[] columnTypes = ti.getColumnTypes();
            String[] sequenceNames = ti.getSequenceNames();

            ColumnMetaData[] metaDataArray = tableMetaData.getColumnMetaDataArray();
            if ( columnNames.length != metaDataArray.length ) {
                consistencyError(
                        "table info class " + tableInfoClassName +
                        " describes " + columnNames.length +
                        " columns but database defines " + metaDataArray.length + " columns",
                        null
                );
                return false;
            }

            int serialCount = 0;

            for ( int i = 0; i < columnNames.length; i += 1 ) {
                String dbColumnName = metaDataArray[i].getDbColumnName();
                if ( !columnNames[i].toUpperCase().equals( dbColumnName.toUpperCase() ) ) {
                    consistencyError(
                            "table info class " + tableInfoClassName + " states that column " + ( i + 1 ) +
                            " is named " +
                            "\"" + columnNames[i] + "\" but the database states that the column's name is \"" +
                            dbColumnName + "\"", null
                    );
                    return false;
                }

                String dbColumnType = metaDataArray[i].getSavrolaTypeInfo().getSavrolaType().toUpperCase();
                if ( !dbColumnType.equals( columnTypes[i].toString() ) ) {
                    consistencyError(
                            "table info class " + tableInfoClassName + " states that column " + ( i + 1 ) + " is a " +
                            "\"" + columnTypes[i] + "\" but the database states that the column is a \"" +
                            dbColumnType + "\"", null
                    );
                    return false;
                }

                if ( metaDataArray[i].getSavrolaTypeInfo().isSerialType() ) {
                    serialCount += 1;
                }

            }

            if ( sequenceNames.length != serialCount ) {
                consistencyError(
                        "table info class " + tableInfoClassName + " describes " + sequenceNames.length +
                        " serial columns but the database says that the table has " + serialCount + " serial columns",
                        null
                );
                return false;
            }

        } catch ( ClassNotFoundException e ) {

            consistencyError( "table info class " + tableInfoClassName + " does not exist", null );
            return false;

        } catch ( NoSuchMethodException e ) {

            consistencyError( "table info class " + tableInfoClassName + " has no ti() method", null );
            return false;

        } catch ( IllegalAccessException e ) {

            consistencyError( "table info class " + tableInfoClassName + "'s ti() method is not public", null );
            return false;

        } catch ( InvocationTargetException e ) {

            consistencyError(
                    "table info class " + tableInfoClassName + "'s ti() method threw an exception",
                    e.getTargetException()
            );
            return false;

        }

        return true;

    }

    private void consistencyError( String msg, @Nullable Throwable e ) {

        Logger.logErr( "Database does not match running software:  " + msg, e );

    }

}
