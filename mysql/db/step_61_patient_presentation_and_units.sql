-- See step_01_create_structure.sql
/* CREATE TABLE BSUNIT
(
    BSU_ID_A               varchar(10)          not null,
    BSU_DESC               varchar(50)          not null,
    BSU_CREATED_BY         varchar(50)          null,
    BSU_CREATED_DATE       datetime             null,
    BSU_LAST_MODIFIED_BY   varchar(50)          null,
    BSU_LAST_MODIFIED_DATE datetime             null,
    BSU_ACTIVE             tinyint(1) default 1 not null,
    PRIMARY KEY (BSU_ID_A)
) ENGINE = MyISAM;
CREATE TABLE TEMPUNIT
(
    TPU_ID_A               varchar(2)           not null,
    TPU_DESC               varchar(50)          not null,
    TPU_CREATED_BY         varchar(50)          null,
    TPU_CREATED_DATE       datetime             null,
    TPU_LAST_MODIFIED_BY   varchar(50)          null,
    TPU_LAST_MODIFIED_DATE datetime             null,
    TPU_ACTIVE             tinyint(1) default 1 not null,
    PRIMARY KEY (TPU_ID_A)
) ENGINE = MyISAM;*/

-- See step_03_dump_default_data_en.sql
/* -- BSUNIT
LOAD DATA LOCAL INFILE './data_en/bsunit.csv'
    INTO TABLE BSUNIT
    FIELDS TERMINATED BY ';'
    LINES TERMINATED BY '\n'
    (BSU_ID_A, BSU_DESC, BSU_ACTIVE);

-- TEMPUNIT
LOAD DATA LOCAL INFILE './data_en/tempunit.csv'
    INTO TABLE TEMPUNIT
    FIELDS TERMINATED BY ';'
    LINES TERMINATED BY '\n'
    (TPU_ID_A, TPU_DESC, TPU_ACTIVE);
*/

-- Add to menu
INSERT INTO GROUPMENU (GM_UG_ID_A, GM_MNI_ID_A, GM_ACTIVE)
VALUES ('admin', 'bsunit', 1),
       ('admin', 'tempunit', 1),
       ('admin', 'patientpresent', 1);
INSERT INTO MENUITEM (MNI_ID_A, MNI_BTN_LABEL, MNI_LABEL, MNI_TOOLTIP, MNI_SHORTCUT, MNI_SUBMENU, MNI_CLASS, MNI_IS_SUBMENU, MNI_POSITION)
VALUES ('bsunit', 'angal.menu.btn.bsunit', 'angal.menu.bsunit', 'x', 'B', 'types', 'org.isf.bsunit.gui.BsUnitBrowser', 'N', 13),
       ('tempunit', 'angal.menu.btn.tempunit', 'angal.menu.tempunit', 'x', 'T', 'types', 'org.isf.tempunit.gui.TempUnitBrowser', 'N', 14),
       ('patientpresent', 'angal.menu.btn.patientpresent', 'angal.menu.patientpresent', 'x', 'S', 'main', 'org.isf.patpres.gui.PatPresBrowser', 'N', 6);
COMMIT;

-- Patient Presentation
CREATE TABLE PATIENTPRESENTATION
(
    PPR_ID                 int                  not null auto_increment,
    PPR_PAT_ID             int                  not null,
    PPR_PRES_DATE          date,
    PPR_CONS_DATE          date,
    PPR_PREV_DATE          date,
    PPR_REFERRED_FROM      varchar(100),
    PPR_PAT_AILM_DESC      text,
    PPR_DOC_AILM_DESC      text,
    PPR_SPEC_SYMPTOMS      text,
    PPR_DIAGNOSIS          text,
    PPR_PROGNOSIS          text,
    PPR_ADVICE             text,
    PPR_PRESCRIBED         text,
    PPR_FOLLOW_UP          text,
    PPR_REFERRED_TO        varchar(100),
    PPR_SUMMARY            varchar(1000)        not null,
    PPR_VITALS_WEIGHT      float,
    PPR_VITALS_HEIGHT      float,
    PPR_VITALS_BLOOD_SUGAR float,
    PPR_VITALS_TEMPERATURE float,
    PPR_VITALS_BS_UNIT     varchar(50),
    PPR_VITALS_TEMP_UNIT   varchar(50),
    PPR_VITALS_BP_SYSTOLE  int,
    PPR_VITALS_BP_DIASTOLE int,
    PPR_CREATED_BY         varchar(50)          null,
    PPR_CREATED_DATE       datetime             null,
    PPR_LAST_MODIFIED_BY   varchar(50)          null,
    PPR_LAST_MODIFIED_DATE datetime             null,
    PPR_ACTIVE             tinyint(1) default 1 not null,
    PRIMARY KEY (PPR_ID)
) ENGINE = MyISAM;

