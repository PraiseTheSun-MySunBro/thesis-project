/*
 * This file is generated by jOOQ.
*/
package ee.ttu.unomomento.db.tables;


import ee.ttu.unomomento.db.Keys;
import ee.ttu.unomomento.db.Public;
import ee.ttu.unomomento.db.tables.records.PersonStateRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PersonState extends TableImpl<PersonStateRecord> {

    private static final long serialVersionUID = -797358357;

    /**
     * The reference instance of <code>public.person_state</code>
     */
    public static final PersonState PERSON_STATE = new PersonState();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PersonStateRecord> getRecordType() {
        return PersonStateRecord.class;
    }

    /**
     * The column <code>public.person_state.person_state_code</code>.
     */
    public final TableField<PersonStateRecord, Short> PERSON_STATE_CODE = createField("person_state_code", org.jooq.impl.SQLDataType.SMALLINT.nullable(false), this, "");

    /**
     * The column <code>public.person_state.ee_name</code>.
     */
    public final TableField<PersonStateRecord, String> EE_NAME = createField("ee_name", org.jooq.impl.SQLDataType.VARCHAR.length(128).nullable(false), this, "");

    /**
     * The column <code>public.person_state.en_name</code>.
     */
    public final TableField<PersonStateRecord, String> EN_NAME = createField("en_name", org.jooq.impl.SQLDataType.VARCHAR.length(128).nullable(false), this, "");

    /**
     * Create a <code>public.person_state</code> table reference
     */
    public PersonState() {
        this("person_state", null);
    }

    /**
     * Create an aliased <code>public.person_state</code> table reference
     */
    public PersonState(String alias) {
        this(alias, PERSON_STATE);
    }

    private PersonState(String alias, Table<PersonStateRecord> aliased) {
        this(alias, aliased, null);
    }

    private PersonState(String alias, Table<PersonStateRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<PersonStateRecord> getPrimaryKey() {
        return Keys.PK_PERSON_STATE_PERSON_STATE_CODE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<PersonStateRecord>> getKeys() {
        return Arrays.<UniqueKey<PersonStateRecord>>asList(Keys.PK_PERSON_STATE_PERSON_STATE_CODE, Keys.AK_PERSON_STATE_EE_NAME, Keys.AK_PERSON_STATE_EN_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersonState as(String alias) {
        return new PersonState(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public PersonState rename(String name) {
        return new PersonState(name, null);
    }
}
