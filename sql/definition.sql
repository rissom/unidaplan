

-- Table definitions:

CREATE TABLE users (
  ID                SERIAL PRIMARY KEY, 
  fullname          VARCHAR(200),
  username          VARCHAR(50),
  email             VARCHAR(200),
  blocked           boolean,
  preferredlanguage VARCHAR(5),
  pw_hash           VARCHAR(134),
  token             VARCHAR(100),
  token_valid_to    TIMESTAMP,
  lastChange        TIMESTAMP
);

CREATE TABLE language_preferences (
  ID              SERIAL PRIMARY KEY,
  user_id         INTEGER REFERENCES users(ID),
  language        VARCHAR(5),
  rank            INTEGER,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE string_key_table (
  ID 				      SERIAL PRIMARY KEY,
  description		  VARCHAR(100),  -- Beschreibungsfeld (nur für Dokumentationszwecke)
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE stringtable (
  ID 			        SERIAL PRIMARY KEY,
  string_key 		  INTEGER NOT NULL REFERENCES string_key_table(ID) ON DELETE CASCADE,
  language			  VARCHAR(5),
  value 			    VARCHAR(4000),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE objecttypesgrp (
  ID              SERIAL PRIMARY KEY,
  position        INTEGER,
  name            INTEGER NOT NULL REFERENCES string_key_table ON DELETE CASCADE,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
); 

CREATE TABLE objecttypes (
  ID 				      SERIAL PRIMARY KEY,
  position        INTEGER,
  otgrp           INTEGER REFERENCES objecttypesgrp ON DELETE NO ACTION,
  string_key 			INTEGER NOT NULL REFERENCES string_key_table(ID) ON DELETE NO ACTION, -- name of objecttype
  description     INTEGER REFERENCES string_key_table ON DELETE NO ACTION,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE ot_parametergrps (
  ID 			     	  SERIAL PRIMARY KEY,
  OT_ID 			    INTEGER NOT NULL REFERENCES objecttypes(ID),
  StringKey			  INTEGER NOT NULL REFERENCES string_key_table(ID),
  pos             INTEGER NOT NULL,
  lastChange		  TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE paramdef ( -- definition of parameters
  ID 				      SERIAL PRIMARY KEY,
  StringKeyName   INTEGER NOT NULL REFERENCES string_key_table(ID),
  StringKeyUnit		INTEGER REFERENCES string_key_table(ID),
  datatype		  	INTEGER NOT NULL,  
  -- 1: integer, 2: float, 3: measurement, 4: string, 5: long string 
  -- 6: chooser, 7: date+time, 8: checkbox 9:timestamp 10: URL
  -- 11: an email --12: a sample
  description	    INTEGER REFERENCES string_key_table(ID),
  format          VARCHAR (20),
  regex           VARCHAR (200),
  min             NUMERIC,
  max             NUMERIC,
  sampletype      INTEGER REFERENCES objecttypes,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE ot_parameters (
  ID 				      SERIAL PRIMARY KEY,
  ObjecttypesID 	INTEGER NOT NULL REFERENCES objecttypes(ID),
  Parametergroup	INTEGER REFERENCES ot_parametergrps(ID),
  compulsory		  BOOLEAN,
  ID_Field			  BOOLEAN,
  Formula		   	  VARCHAR(250),
  Hidden		 	    BOOLEAN,
  pos             INTEGER NOT NULL,
  definition      INTEGER NOT NULL REFERENCES paramdef(ID),
  StringKeyName		INTEGER REFERENCES string_key_table(ID),
  description     INTEGER REFERENCES string_key_table(ID),
  lastChange		  TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE samples (  -- Table with instances of samples
  ID 				      SERIAL PRIMARY KEY,
  ObjecttypesID 	INTEGER NOT NULL REFERENCES objecttypes(ID),
  Creator         INTEGER REFERENCES users(ID),
  Creationdate    TIMESTAMP,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE sampledata (
  ID              BIGSERIAL PRIMARY KEY,
  ObjectID        INTEGER NOT NULL REFERENCES samples(ID) ON DELETE CASCADE,
  Ot_Parameter_ID INTEGER NOT NULL REFERENCES ot_parameters(ID),
  Data            JSONB,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE originates_from ( -- inheritance data of the samples
  ID 				      SERIAL PRIMARY KEY, -- for editing
  Parent 			    INTEGER NOT NULL REFERENCES samples(ID) ON DELETE CASCADE, -- Objekte
  Child 		     	INTEGER NOT NULL REFERENCES samples(ID) ON DELETE CASCADE,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE possible_values (
  id              SERIAL PRIMARY KEY, 
  parameterID 		INTEGER NOT NULL REFERENCES paramdef(ID) ON DELETE CASCADE,
  position		  	INTEGER NOT NULL,
  string		  	  VARCHAR(1000),
  lastChange	  	TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE groups (
  ID              SERIAL PRIMARY KEY, 
  Name            VARCHAR(200),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE processtypegroups (
  ID              SERIAL PRIMARY KEY, 
  position        INTEGER,
  name            INTEGER NOT NULL REFERENCES string_key_table ON DELETE CASCADE,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE processtypes (   -- kind of process (i.e. washing)
  ID              SERIAL PRIMARY KEY, 
  position        INTEGER,
  ptgroup         INTEGER REFERENCES processtypegroups,
  name            INTEGER NOT NULL REFERENCES string_key_table ON DELETE CASCADE,
  description     INTEGER REFERENCES string_key_table ON DELETE CASCADE,  
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE p_parametergrps (
  ID              SERIAL PRIMARY KEY,
  processtype     INTEGER NOT NULL REFERENCES processtypes(ID) ON DELETE CASCADE,
  StringKey       INTEGER NOT NULL REFERENCES string_key_table(ID),
  pos             INTEGER NOT NULL,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE p_parameters (
  ID              SERIAL PRIMARY KEY,
  ProcesstypeID   INTEGER NOT NULL REFERENCES processtypes(ID) ON DELETE NO ACTION,
  Parametergroup  INTEGER REFERENCES p_parametergrps(ID) ON DELETE NO ACTION,
  compulsory      BOOLEAN,
  description     INTEGER REFERENCES string_key_table(id),
  ID_Field        BOOLEAN,
  Formula         VARCHAR(250),
  Hidden          BOOLEAN,
  pos             INTEGER NOT NULL,
  definition      INTEGER NOT NULL REFERENCES paramdef(ID),
  StringKeyName   INTEGER REFERENCES string_key_table(ID),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE groupmemberships (
  ID              SERIAL PRIMARY KEY, 
  groupID         INTEGER NOT NULL REFERENCES groups(ID) ON DELETE CASCADE,
  userID          INTEGER NOT NULL REFERENCES users(ID) ON DELETE CASCADE,  
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE rightsprocesstypegroup (
  ID              SERIAL PRIMARY KEY, 
  groupID         INTEGER NOT NULL REFERENCES groups(id),
  processtype     INTEGER NOT NULL REFERENCES processtypes(id),
  permission      VARCHAR(1),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE rightsprocesstypeuser (
  ID              SERIAL PRIMARY KEY, 
  userID          INTEGER NOT NULL REFERENCES users(id),
  processtype     INTEGER NOT NULL REFERENCES processtypes(id),
  permission      VARCHAR(1),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE rightssampletypegroup (
  ID              SERIAL PRIMARY KEY, 
  groupID         INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
  sampletype      INTEGER NOT NULL REFERENCES objecttypes(id) ON DELETE CASCADE,  
  permission      VARCHAR(1),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE rightssampletypeuser  (
  ID              SERIAL PRIMARY KEY, 
  userID          INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  sampletype      INTEGER NOT NULL REFERENCES objecttypes(id) ON DELETE CASCADE,
  permission      VARCHAR(1),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE samplerecipes (   -- Rezepte für Objekte
  id              SERIAL PRIMARY KEY, 
  name            INTEGER NOT NULL REFERENCES string_key_table(id) ON DELETE CASCADE,
  sampletype      INTEGER NOT NULL REFERENCES objecttypes(id) ON DELETE CASCADE,
  position        INTEGER,  
  owner           INTEGER REFERENCES users(ID) ON DELETE SET NULL,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE samplerecipedata (  -- 
  ID              SERIAL PRIMARY KEY, 
  recipe          INTEGER NOT NULL REFERENCES samplerecipes(id) ON DELETE CASCADE,
  parameter       INTEGER NOT NULL REFERENCES ot_parameters(id) ON DELETE CASCADE,
  data            JSONB,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE processes (  -- Table with instances of processes
  ID              BIGSERIAL PRIMARY KEY,
  ProcesstypesID  INTEGER NOT NULL REFERENCES processtypes(id),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE samplesinprocess (
  ID              BIGSERIAL PRIMARY KEY,
  ProcessID       INTEGER NOT NULL REFERENCES processes(ID) ON DELETE CASCADE,
  sampleID        INTEGER NOT NULL REFERENCES samples(ID) ON DELETE NO ACTION,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL,
  CONSTRAINT one_reference_per_sample_and_process UNIQUE (ProcessID,sampleID)
);

CREATE TABLE processrecipes (   -- Rezepte für Objekte
  id              SERIAL PRIMARY KEY, 
  name            INTEGER NOT NULL REFERENCES string_key_table(ID) ON DELETE CASCADE,
  processtype     INTEGER NOT NULL REFERENCES processtypes(ID) ON DELETE CASCADE,  
  position        INTEGER,
  owner           INTEGER REFERENCES users(ID) ON DELETE SET NULL,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE processrecipedata ( -- process parameter data for recipes
  id              SERIAL PRIMARY KEY,
  recipeid        INTEGER NOT NULL REFERENCES processrecipes(id) ON DELETE CASCADE,
  parameterid     INTEGER NOT NULL REFERENCES p_parameters(id) ON DELETE NO ACTION,
  data            JSONB,
  lastchange      TIMESTAMP,
  lastuser        INTEGER REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE po_parameters (
  ID              SERIAL PRIMARY KEY,
  ProcesstypeID   INTEGER NOT NULL REFERENCES processtypes(ID),
  compulsory      BOOLEAN,
  Hidden          BOOLEAN,
  description     INTEGER REFERENCES string_key_table(id),
  position        INTEGER,
  definition      INTEGER NOT NULL REFERENCES paramdef(ID),
  StringKeyName   INTEGER REFERENCES string_key_table(ID),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE processdata ( -- process parameter data
  id              BIGSERIAL PRIMARY KEY,
  processID       INTEGER NOT NULL REFERENCES processes(id) ON DELETE CASCADE,
  parameterID     INTEGER NOT NULL REFERENCES p_parameters(id) ON DELETE NO ACTION,
  data            JSONB,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE spdata ( -- sample related process data
  id              BIGSERIAL PRIMARY KEY,
  sip             INTEGER NOT NULL REFERENCES samplesinprocess(ID) ON DELETE CASCADE,
  parameterid     INTEGER NOT NULL REFERENCES po_parameters(ID) ON DELETE NO ACTION,
  data            JSONB,
  lastchange      TIMESTAMP,
  lastuser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE searches (  -- abstract searchobject
  ID              SERIAL PRIMARY KEY,
  Name            INTEGER NOT NULL REFERENCES string_key_table(ID),
  operation       BOOLEAN,  -- 'And' or 'Or' connection of search criteria
  type            INTEGER, -- 1: Samples, 2: Processes, 3: Sample in Process
  owner           INTEGER NOT NULL REFERENCES users(ID), -- Owner of the search
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE searchObject (  -- Search requests for samples
  id              SERIAL PRIMARY KEY,
  search          INTEGER NOT NULL REFERENCES searches(ID) ON DELETE CASCADE,
  otparameter     INTEGER NOT NULL REFERENCES ot_parameters(ID) ON DELETE CASCADE,
  comparison      INTEGER, -- 1:< , 2:> , 3:=, 4:not
  value           VARCHAR (100),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE searchProcess (  -- Search requests for processes
  id              SERIAL PRIMARY KEY,
  search          INTEGER NOT NULL REFERENCES searches(ID) ON DELETE CASCADE,
  pparameter      INTEGER NOT NULL REFERENCES p_parameters(ID) ON DELETE CASCADE,
  comparison      INTEGER, -- 1:< , 2:> , 3:=, 4:not
  value           VARCHAR (100),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE searchPO (  -- Search requests samplespecific data in processes
  id              SERIAL PRIMARY KEY,
  search          INTEGER NOT NULL REFERENCES searches(ID),
  poparameter     INTEGER NOT NULL REFERENCES po_parameters(ID),
  comparison      INTEGER, -- 1:< , 2:> , 3:=, 4:not
  value           VARCHAR (100),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE oSearchOutput (
  id              SERIAL PRIMARY KEY,
  search          INTEGER NOT NULL REFERENCES searches(ID) ON DELETE CASCADE,
  position        INTEGER NOT NULL,
  otparameter     INTEGER NOT NULL REFERENCES ot_parameters(ID) ON DELETE CASCADE,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE pSearchOutput (
  id              SERIAL PRIMARY KEY,
  search          INTEGER NOT NULL REFERENCES searches(ID) ON DELETE CASCADE,
  position        INTEGER NOT NULL,
  pparameter      INTEGER NOT NULL REFERENCES p_parameters(ID) ON DELETE CASCADE,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE poSearchOutput (
  id              SERIAL PRIMARY KEY,
  search          INTEGER NOT NULL REFERENCES searches(ID) ON DELETE CASCADE,
  position        INTEGER NOT NULL,
  poparameter     INTEGER NOT NULL REFERENCES po_parameters(ID) ON DELETE CASCADE,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE rightsSearchGroups (
  id              SERIAL PRIMARY KEY, 
  groupID         INTEGER NOT NULL REFERENCES groups(ID) ON DELETE CASCADE,
  searchID        INTEGER NOT NULL REFERENCES searches(ID) ON DELETE CASCADE,
  permission      VARCHAR(1),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE rightsSearchUser (
  id              SERIAL PRIMARY KEY, 
  userID          INTEGER NOT NULL REFERENCES users(ID) ON DELETE CASCADE,
  searchID        INTEGER NOT NULL REFERENCES searches(ID) ON DELETE CASCADE,  
  permission      VARCHAR(1),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE rightsProcessRecipeUser (
  id              SERIAL PRIMARY KEY, 
  userID          INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  recipe          INTEGER NOT NULL REFERENCES processrecipes(id) ON DELETE CASCADE,  
  permission      VARCHAR(1),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE rightsProcessRecipeGroups (
  id              SERIAL PRIMARY KEY, 
  groupID         INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
  recipe          INTEGER NOT NULL REFERENCES processrecipes(id) ON DELETE CASCADE,
  permission      VARCHAR(1),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE rightsSampleRecipeUser (
  id              SERIAL PRIMARY KEY, 
  userID          INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  recipe          INTEGER NOT NULL REFERENCES samplerecipes(id) ON DELETE CASCADE,  
  permission      VARCHAR(1),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE rightsSampleRecipeGroups (
  id              SERIAL PRIMARY KEY, 
  groupID         INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
  recipe          INTEGER NOT NULL REFERENCES samplerecipes(id) ON DELETE CASCADE,
  permission      VARCHAR(1),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE experiments (
  id              SERIAL PRIMARY KEY,
  Name            INTEGER NOT NULL REFERENCES string_key_table(ID),
  Number          INTEGER NOT NULL,
  Creator         INTEGER NOT NULL REFERENCES users(ID),
  Status          INTEGER NOT NULL, -- 0: Planungsphase, 1: geplant, 2: in Durchführung, 3: abgeschlossen
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE rightsexperimentgroup (
  id              SERIAL PRIMARY KEY, 
  groupID         INTEGER NOT NULL REFERENCES groups(id),
  experiment      INTEGER NOT NULL REFERENCES experiments(id),
  permission      VARCHAR(1),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE rightsexperimentuser (
  id              SERIAL PRIMARY KEY, 
  userID          INTEGER NOT NULL REFERENCES users(id),
  experiment      INTEGER NOT NULL REFERENCES experiments(id),
  permission      VARCHAR(1),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE expp_param (
  id              SERIAL PRIMARY KEY,
  exp_plan_id     INTEGER NOT NULL REFERENCES experiments(ID) ON DELETE CASCADE,
  hidden          BOOLEAN,
  Formula         VARCHAR(250),
  pos             INTEGER NOT NULL,
  definition      INTEGER NOT NULL REFERENCES paramdef(ID),
  StringKeyName   INTEGER REFERENCES string_key_table(ID),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE experimentdata ( -- process parameter data
  id              BIGSERIAL PRIMARY KEY,
  experimentID    INTEGER NOT NULL REFERENCES experiments(id) ON DELETE CASCADE,
  parameterID     INTEGER NOT NULL REFERENCES expp_param(id) ON DELETE CASCADE,
  data            JSONB,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE exp_plan_processes (
  id              SERIAL PRIMARY KEY,
  position        INTEGER,
  expp_ID         INTEGER NOT NULL REFERENCES experiments(ID) ON DELETE CASCADE,
  ptID            INTEGER NOT NULL REFERENCES processtypes(ID) ON DELETE CASCADE,
  recipe          INTEGER REFERENCES processrecipes(ID),
  note            INTEGER REFERENCES string_key_table(ID),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);

CREATE TABLE expp_samples (
  id              SERIAL PRIMARY KEY,
  position        INTEGER,
  note            INTEGER REFERENCES string_key_table(ID),
  expp_ID         INTEGER NOT NULL REFERENCES experiments(ID) ON DELETE CASCADE,
  sample          INTEGER NOT NULL REFERENCES samples(ID) ON DELETE CASCADE,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL,
  CONSTRAINT one_reference_per_sample_and_experiment UNIQUE (sample,expp_ID)
);

CREATE TABLE exp_plan_steps (
  id              SERIAL PRIMARY KEY,
  exp_plan_pr     INTEGER NOT NULL REFERENCES exp_plan_processes(ID) ON DELETE CASCADE,
  expp_s_ID       INTEGER NOT NULL REFERENCES expp_samples(ID) ON DELETE CASCADE,
  recipe          INTEGER REFERENCES processrecipes(ID),
  note            INTEGER REFERENCES string_key_table(ID),
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL,
  CONSTRAINT one_step_per_sample_and_process UNIQUE (expp_s_ID,exp_plan_pr)
);

CREATE TABLE files (
  id              SERIAL PRIMARY KEY,
  filename        VARCHAR(250),
  sample          INTEGER REFERENCES samples,
  process         INTEGER REFERENCES processes,
  experiment      INTEGER REFERENCES experiments,
  lastChange      TIMESTAMP,
  lastUser        INTEGER REFERENCES users(ID) ON DELETE SET NULL
);



-- Procedures

CREATE OR REPLACE FUNCTION updateLastChange() RETURNS TRIGGER AS $$
    BEGIN
        NEW.lastChange := NOW();
        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;



-- Views

CREATE VIEW samplenames AS
SELECT 
  t2.id, 
  name, 
  typeid, 
  jsonb_agg (exps.expp_id) AS experiments 
FROM(
  SELECT 
      max(id) AS id,
      string_agg(nameSubString,'-' ORDER BY pos) AS name,
      max(typeid) AS typeid
  FROM ( 
    SELECT 
        samples.id, 
        CASE WHEN pd.datatype = 4 THEN sdata.data ->> 'value'
             ELSE CASE WHEN pd.format IS Null
                THEN
                to_char((sdata.data ->> 'value')::float,'999999999')
                ELSE
                    to_char((sdata.data ->> 'value')::float, pd.format)
                  END
        END AS nameSubString,
        ot.id as typeid,
        otp.pos
    FROM samples 
    JOIN objecttypes ot ON (samples.objecttypesID = ot.id)
    JOIN ot_parameters otp ON (ot.id = otp.ObjecttypesID AND otp.ID_FIELD = true)
    JOIN sampledata sdata ON (sdata.ObjectID = samples.id AND sdata.Ot_Parameter_ID = otp.ID)
    JOIN paramdef pd ON (otp.definition = pd.ID) 
    ) t
  GROUP BY id  ) t2
LEFT JOIN expp_samples exps ON exps.sample = t2.id
GROUP BY t2.id, name, typeid;



CREATE VIEW pnumbers AS
SELECT 
  processes.id, 
  processes.processtypesid as processtype, 
  ptd.data -> 'date' AS date, 
  (n.data ->> 'value')::integer AS p_number 
FROM processes 
JOIN processtypes ON (processes.processtypesid = processtypes.id) 
JOIN p_parameters pp ON (pp.definition = 10) 
JOIN p_parameters pp2 ON (pp2.definition = 8)
JOIN processdata ptd ON (ptd.processID = processes.id AND ptd.ParameterID = pp.id) 
JOIN processdata n ON (n.ProcessID = processes.id AND n.ParameterID = pp2.id);


