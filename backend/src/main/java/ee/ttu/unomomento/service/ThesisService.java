package ee.ttu.unomomento.service;

import ee.ttu.unomomento.db.tables.records.ThesisOwnerRecord;
import ee.ttu.unomomento.db.tables.records.ThesisRecord;
import ee.ttu.unomomento.db.tables.records.ThesisTagRecord;
import ee.ttu.unomomento.dto.WorkplaceDTO;
import ee.ttu.unomomento.model.Account;
import ee.ttu.unomomento.model.Thesis;
import ee.ttu.unomomento.model.ThesisOwner;
import ee.ttu.unomomento.model.ThesisTag;
import ee.ttu.unomomento.model.template.AccountPersonInformation;
import ee.ttu.unomomento.model.template.AddThesis;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

import static ee.ttu.unomomento.db.tables.Account.ACCOUNT;
import static ee.ttu.unomomento.db.tables.Person.PERSON;
import static ee.ttu.unomomento.db.tables.PersonAccountOwner.PERSON_ACCOUNT_OWNER;
import static ee.ttu.unomomento.db.tables.Thesis.THESIS;
import static ee.ttu.unomomento.db.tables.ThesisCandidate.THESIS_CANDIDATE;
import static ee.ttu.unomomento.db.tables.ThesisOwner.THESIS_OWNER;
import static ee.ttu.unomomento.db.tables.ThesisPicked.THESIS_PICKED;
import static ee.ttu.unomomento.db.tables.ThesisTag.THESIS_TAG;
import static org.jooq.impl.DSL.arrayAgg;
import static org.jooq.impl.DSL.concat;

@Service
@Transactional
public class ThesisService {

    private final DSLContext dslContext;
    private final AccountService accountService;

    @Autowired
    public ThesisService(DSLContext dslContext, AccountService accountService) {
        this.dslContext = dslContext;
        this.accountService = accountService;
    }

    public boolean save(AddThesis thesisTemplate, String username) {
        AccountPersonInformation account = accountService.findAccountByUsernameFacultyRoleCodes(username, thesisTemplate.getDegreeCode(),
                thesisTemplate.getFacultyCode(), thesisTemplate.getRoleCode());

        if (account == null) return false;

        short degreeCode = account.getDegreeCode();
        if (degreeCode < 4) degreeCode++;
        else return false;

        Long personId = account.getPersonId();
        Thesis thesis = new Thesis(null, thesisTemplate.getSupervisorName(), thesisTemplate.getFacultyCode(), null, degreeCode,
                thesisTemplate.getEeTitle(), thesisTemplate.getEnTitle(), thesisTemplate.getEeDescription(), thesisTemplate.getEnDescription());
        ThesisRecord thesisRecord = dslContext.newRecord(THESIS, thesis);
        thesisRecord.insert();

        Long thesisId = thesisRecord.getThesisId();
        ThesisOwnerRecord thesisOwnerRecord = dslContext.newRecord(THESIS_OWNER,
                new ThesisOwner(thesisId, personId, thesisTemplate.getRoleCode()));
        thesisOwnerRecord.insert();

        if (thesisTemplate.getTags() != null) {
            for (String tag : thesisTemplate.getTags()) {
                ThesisTagRecord thesisTagRecord = dslContext.newRecord(THESIS_TAG,
                        new ThesisTag(thesisId, tag));
                thesisTagRecord.insert();
            }
        }

        return true;
    }

    public boolean update(AddThesis thesisTemplate, String username) {
        AccountPersonInformation account = accountService.findAccountByUsernameFacultyRoleCodes(username, thesisTemplate.getDegreeCode(),
                thesisTemplate.getFacultyCode(), thesisTemplate.getRoleCode());

        if (account == null) return false;

        Long thesisId = thesisTemplate.getThesisId();
        if (thesisId == null) return false;

        Object owner = dslContext
                .select(THESIS_OWNER.PERSON_ID)
                .from(THESIS_OWNER)
                .where(THESIS_OWNER.THESIS_ID.eq(thesisId)
                    .and(THESIS_OWNER.PERSON_ID.eq(account.getPersonId())))
                .fetchOne("person_id");

        if (owner == null) return false;

        Thesis thesis = new Thesis(thesisId, thesisTemplate.getSupervisorName(), thesisTemplate.getFacultyCode(), null, thesisTemplate.getDegreeCode(),
                thesisTemplate.getEeTitle(), thesisTemplate.getEnTitle(), thesisTemplate.getEeDescription(), thesisTemplate.getEnDescription());
        ThesisRecord thesisRecord = dslContext.newRecord(THESIS, thesis);
        thesisRecord.update();

        dslContext
                .delete(THESIS_TAG)
                .where(THESIS_TAG.THESIS_ID.eq(thesisId))
                .execute();

        if (thesisTemplate.getTags() != null) {
            for (String tag : thesisTemplate.getTags()) {
                ThesisTagRecord thesisTagRecord = dslContext.newRecord(THESIS_TAG,
                        new ThesisTag(thesisId, tag));
                thesisTagRecord.insert();
            }
        }

        return true;
    }

    public boolean makeInactive(Long thesisId, String username) {
        Object resultName = dslContext
                .select(ACCOUNT.USERNAME)
                .from(THESIS_OWNER)
                .innerJoin(PERSON).using(PERSON.PERSON_ID)
                .innerJoin(PERSON_ACCOUNT_OWNER).using(PERSON.PERSON_ID)
                .innerJoin(ACCOUNT).using(ACCOUNT.ACCOUNT_ID)
                .where(THESIS_OWNER.THESIS_ID.eq(thesisId))
                .fetchOne("username");

        if (resultName == null || !resultName.toString().equals(username)) {
            return false;
        }

        dslContext
                .update(THESIS)
                .set(THESIS.THESIS_STATE_CODE, (short) 2)
                .where(THESIS.THESIS_ID.eq(thesisId))
                .execute();

        return true;
    }

    public boolean makeActive(Long thesisId, String username) {
        Object resultName = dslContext
                .select(ACCOUNT.USERNAME)
                .from(THESIS_OWNER)
                .innerJoin(PERSON).using(PERSON.PERSON_ID)
                .innerJoin(PERSON_ACCOUNT_OWNER).using(PERSON.PERSON_ID)
                .innerJoin(ACCOUNT).using(ACCOUNT.ACCOUNT_ID)
                .where(THESIS_OWNER.THESIS_ID.eq(thesisId))
                .fetchOne("username");

        if (resultName == null || !resultName.toString().equals(username)) {
            return false;
        }

        dslContext
                .update(THESIS)
                .set(THESIS.THESIS_STATE_CODE, (short) 1)
                .where(THESIS.THESIS_ID.eq(thesisId))
                .execute();

        return true;
    }

    public List<WorkplaceDTO> getAllMyOwnTheses(String username) {
        return dslContext
                .select(THESIS.THESIS_ID, THESIS.EE_TITLE, THESIS.EN_TITLE, THESIS.EE_DESCRIPTION, THESIS.EN_DESCRIPTION, THESIS.REG_TIME,
                        THESIS.THESIS_STATE_CODE, concat(PERSON.FIRSTNAME, DSL.val(" "), PERSON.LASTNAME).as("full_name"),
                        THESIS.SUPERVISOR_NAME, arrayAgg(THESIS_TAG.TAG_NAME).as("tags"))
                .from(THESIS)
                .innerJoin(THESIS_OWNER).using(THESIS.THESIS_ID)
                .innerJoin(PERSON).using(PERSON.PERSON_ID)
                .innerJoin(PERSON_ACCOUNT_OWNER).using(PERSON.PERSON_ID)
                .innerJoin(ACCOUNT).using(ACCOUNT.ACCOUNT_ID)
                .leftJoin(THESIS_TAG).using(THESIS.THESIS_ID)
                .leftJoin(THESIS_CANDIDATE).using(THESIS_CANDIDATE.THESIS_ID)
                .where(ACCOUNT.USERNAME.eq(username))
                .groupBy(THESIS.THESIS_ID, THESIS.EE_TITLE, THESIS.EN_TITLE, THESIS.EE_DESCRIPTION, THESIS.EN_DESCRIPTION, THESIS.REG_TIME, PERSON.FIRSTNAME,
                         PERSON.LASTNAME, THESIS.SUPERVISOR_NAME)
                .fetchInto(WorkplaceDTO.class);
    }

    public List<WorkplaceDTO> getAllMyCandidateTheses(String username) {
        return dslContext
                .select(THESIS.THESIS_ID, THESIS.EE_TITLE, THESIS.EN_TITLE, THESIS.EE_DESCRIPTION, THESIS.EN_DESCRIPTION, THESIS.REG_TIME,
                        THESIS.THESIS_STATE_CODE, concat(PERSON.FIRSTNAME, DSL.val(" "), PERSON.LASTNAME).as("full_name"),
                        THESIS.SUPERVISOR_NAME, arrayAgg(THESIS_TAG.TAG_NAME).as("tags"))
                .from(THESIS)
                .innerJoin(THESIS_CANDIDATE).using(THESIS.THESIS_ID)
                .innerJoin(PERSON).on(THESIS_CANDIDATE.CANDIDATE_ID.eq(PERSON.PERSON_ID))
                .innerJoin(PERSON_ACCOUNT_OWNER).using(PERSON.PERSON_ID)
                .innerJoin(ACCOUNT).using(ACCOUNT.ACCOUNT_ID)
                .leftJoin(THESIS_TAG).using(THESIS.THESIS_ID)
                .where(ACCOUNT.USERNAME.eq(username))
                .groupBy(THESIS.THESIS_ID, THESIS.EE_TITLE, THESIS.EN_TITLE, THESIS.EE_DESCRIPTION, THESIS.EN_DESCRIPTION, THESIS.REG_TIME, PERSON.FIRSTNAME,
                        PERSON.LASTNAME, THESIS.SUPERVISOR_NAME)
                .fetchInto(WorkplaceDTO.class);
    }

    public WorkplaceDTO getMyPickedThesis(String username) {
        return dslContext
                .select(THESIS.THESIS_ID, THESIS.EE_TITLE, THESIS.EN_TITLE, THESIS.EE_DESCRIPTION, THESIS.EN_DESCRIPTION, THESIS.REG_TIME,
                        THESIS.THESIS_STATE_CODE,
                        THESIS.SUPERVISOR_NAME, arrayAgg(THESIS_TAG.TAG_NAME).as("tags"))
                .from(THESIS)
                .innerJoin(THESIS_PICKED).using(THESIS.THESIS_ID)
                .innerJoin(PERSON).using(PERSON.PERSON_ID)
                .innerJoin(PERSON_ACCOUNT_OWNER).using(PERSON.PERSON_ID)
                .innerJoin(ACCOUNT).using(ACCOUNT.ACCOUNT_ID)
                .leftJoin(THESIS_TAG).using(THESIS.THESIS_ID)
                .where(ACCOUNT.USERNAME.eq(username))
                .groupBy(THESIS.THESIS_ID, THESIS.EE_TITLE, THESIS.EN_TITLE, THESIS.EE_DESCRIPTION, THESIS.EN_DESCRIPTION, THESIS.REG_TIME, PERSON.FIRSTNAME,
                        PERSON.LASTNAME, THESIS.SUPERVISOR_NAME)
                .fetchOneInto(WorkplaceDTO.class);
    }

    public String getOwnerByThesisId(Long thesisId) {
        return dslContext
                .select(concat(PERSON.FIRSTNAME, DSL.val(" "), PERSON.LASTNAME).as("full_name"))
                .from(THESIS)
                    .innerJoin(THESIS_OWNER).using(THESIS_OWNER.THESIS_ID)
                    .innerJoin(PERSON).using(PERSON.PERSON_ID)
                .where(THESIS.THESIS_ID.eq(thesisId))
                .fetchOne("full_name").toString();
    }

    public Result<?> getAllTheses() {
        return dslContext
                .select()
                .from(THESIS)
                .fetch();
    }

    public Result<?> getThesesByPersonId(Long personId) {
        return dslContext
                .select()
                .from(THESIS)
                .join(THESIS_OWNER).using(THESIS.THESIS_ID)
                .where(THESIS_OWNER.PERSON_ID.eq(personId))
                .fetch();
    }
}
