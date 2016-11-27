-- Get all searches with rights and owner for one User
CREATE OR REPLACE FUNCTION getSearchesForUser(vuserid INTEGER, OUT searchesJSON JSON) AS
$$
BEGIN
IF ( SELECT NOT blocked FROM users WHERE users.id = vuserid ) THEN
	WITH 
	searcheswithrights AS(  
		SELECT  -- get all searches were rights are granted through groups
		searches.id, 
		searches.name,
		max(users.fullname) AS owner, 
		max(users.id) AS ownerid,
		max(rsg.permission) AS permission
		FROM searches
		JOIN groupmemberships gm ON gm.userid = vuserid
		JOIN rightssearchgroups rsg ON rsg.groupid = gm.groupid AND rsg.searchid = searches.id
		JOIN users ON (users.id=searches.owner)
		GROUP BY searches.id
	UNION
		SELECT  -- combine with searches were the user was granted a right
		searches.id, 
		searches.name,
		max(users.fullname) AS owner, 
		max(users.id) AS ownerid,
		max(rsu.permission) AS permission
		FROM searches
		JOIN rightssearchuser rsu ON rsu.userid = vuserid AND rsu.searchid = searches.id
		JOIN users ON (users.id = searches.owner)
		GROUP BY searches.id
	)
	SELECT INTO searchesJSON -- delete dupicates and package as JSON
	array_to_json(array_agg(search)) AS searches 
	FROM (	SELECT JSON_BUILD_OBJECT(
				'id', id,
				'name', max(name),
				'owner', max(owner),
				'ownerid', max(ownerid),
				'permission', max(permission)
				) AS search
			FROM 
			searcheswithrights 
			GROUP BY id
		) t 
	GROUP BY TRUE;
END IF;
END;
$$
LANGUAGE plpgsql;





-- Get the rights for one particular search and user
CREATE OR REPLACE FUNCTION getSearchRights(vuserid INTEGER, vsearchid INTEGER, OUT rights CHAR) AS
$$
BEGIN
IF ( SELECT NOT blocked FROM users WHERE users.id=vuserid ) THEN
	SELECT INTO rights max(permission)
	FROM(
		SELECT max(permission) AS permission FROM rightssearchuser ru 
		WHERE searchid = vsearchid AND userid = vuserid 
		GROUP BY ru.userid
		UNION
		SELECT max(permission) AS permission FROM rightssearchgroups rg 
		JOIN groupmemberships gm ON (rg.groupid = gm.groupid AND gm.userid = vuserid) 
		WHERE searchid = vsearchid
		GROUP BY gm.userid
		UNION
		SELECT 'w' AS permission FROM groupmemberships WHERE groupid = 1 AND userid = vuserid
	) t GROUP BY TRUE;
END IF;
rights := COALESCE(rights,'n');
END;
$$
LANGUAGE plpgsql;



-- Get the rights for one particular experiment and user
CREATE OR REPLACE FUNCTION getExperimentRights(vuserid INTEGER, vexperimentid INTEGER, OUT rights CHAR) AS
$$
BEGIN
IF ( SELECT NOT blocked FROM users WHERE users.id = vuserid ) THEN
	SELECT INTO rights max(permission)
	FROM(
		SELECT max(permission) AS permission 
		FROM rightsexperimentuser ru WHERE experiment = vexperimentid AND userid = vuserid 
		GROUP BY ru.userid
		UNION
		SELECT max(permission) AS permission FROM rightsexperimentgroup rg 
		JOIN groupmemberships gm ON (rg.groupid=gm.groupid AND gm.userid = vuserid) 
		WHERE experiment = vexperimentid
		GROUP BY gm.userid
		UNION
		SELECT 'w' AS permission FROM groupmemberships WHERE groupid = 1 AND userid = vuserid
	) t GROUP BY TRUE;
END IF;
rights:=COALESCE(rights,'n');
END;
$$
LANGUAGE plpgsql;



-- Get the rights for a sampletype and user
CREATE OR REPLACE FUNCTION getSampleTypeRights(vuserid INTEGER, vsampletype INTEGER, OUT rights CHAR) AS
$$
BEGIN
IF ( SELECT NOT blocked FROM users WHERE users.id = vuserid ) THEN
	SELECT  INTO rights max(permission)
	FROM(
		SELECT max(permission) AS permission 
		FROM rightssampletypeuser ru WHERE sampletype = vsampletype AND userid = vuserid 
		GROUP BY ru.userid
		UNION
		SELECT max(permission) AS permission FROM rightssampletypegroup rg 
		JOIN groupmemberships gm ON (rg.groupid = gm.groupid AND gm.userid = vuserid) 
		WHERE sampletype=vsampletype
		GROUP BY gm.userid
		UNION
		SELECT 'w' AS permission FROM groupmemberships WHERE groupid=1 AND userid=vuserid
	) t GROUP BY TRUE;
END IF;
rights:=COALESCE(rights,'n');
END;
$$
LANGUAGE plpgsql;



-- Get the rights for a process recipe and user
CREATE OR REPLACE FUNCTION getProcessRecipeRights(vuserid INTEGER, 
	                         vprocessrecipe INTEGER, OUT rights CHAR) AS
$$
BEGIN
IF ( SELECT NOT blocked FROM users WHERE users.id = vuserid ) THEN
	SELECT  INTO rights max(permission)
	FROM(
		SELECT max(permission) AS permission 
		FROM rightsProcessRecipeUser ru 
		WHERE recipe = vprocessrecipe AND userid = vuserid GROUP BY ru.userid

		UNION

		SELECT max(permission) AS permission 
		FROM rightsProcessRecipeGroups rg 
		JOIN groupmemberships gm ON (rg.groupid = gm.groupid AND gm.userid = vuserid) 
		WHERE recipe = vprocessrecipe
		GROUP BY gm.userid

		UNION

		SELECT 'w' AS permission FROM groupmemberships 
		WHERE groupid = 1 AND userid = vuserid

		UNION
		
		SELECT 'w' AS permission FROM processrecipes 
		WHERE processrecipes.id = vprocessrecipe AND owner = vuserid

	) t GROUP BY TRUE;
END IF;
rights := COALESCE(rights,'n');
END;
$$
LANGUAGE plpgsql;




-- Get the rights for a sample recipe and user
CREATE OR REPLACE FUNCTION getSampleRecipeRights(vuserid INTEGER, 
	                         vsamplerecipe INTEGER, OUT rights CHAR) AS
$$
BEGIN
IF ( SELECT NOT blocked FROM users WHERE users.id = vuserid ) THEN
	SELECT  INTO rights max(permission)
	FROM(
		SELECT max(permission) AS permission 
		FROM rightsSampleRecipeUser ru 
		WHERE recipe = vsamplerecipe AND userid = vuserid 
		GROUP BY ru.userid

		UNION

		SELECT max(permission) AS permission 
		FROM rightsSampleRecipeGroups rg 
		JOIN groupmemberships gm ON (rg.groupid = gm.groupid AND gm.userid = vuserid) 
		WHERE recipe = vsamplerecipe
		GROUP BY gm.userid

		UNION

		SELECT 'w' AS permission FROM groupmemberships  -- admin?
		WHERE groupid = 1 AND userid = vuserid

		UNION
		
		SELECT 'w' AS permission FROM samplerecipes 
		WHERE samplerecipes.id = vsamplerecipe AND owner = vuserid

	) t GROUP BY TRUE;
END IF;
rights := COALESCE(rights,'n');
END;
$$
LANGUAGE plpgsql;




-- Get the rights for one particular sample and user
CREATE OR REPLACE FUNCTION getSampleRights(vuserid INTEGER, vsample INTEGER, OUT rights CHAR) AS
$$
BEGIN
	SELECT INTO rights getSampletypeRights(
		vuserid := vuserid,
		vsampletype := (SELECT objecttypesid FROM samples WHERE id = vsample)
	);
	rights := COALESCE(rights,'n');
END;
$$
LANGUAGE plpgsql;





-- Get the rights for a processtype and user
CREATE OR REPLACE FUNCTION getProcessTypeRights(vuserid INTEGER, vprocesstype INTEGER, OUT rights CHAR) AS
$$
BEGIN
IF ( SELECT NOT blocked FROM users WHERE users.id = vuserid ) THEN
	SELECT  INTO rights max(permission)
	FROM(
		SELECT max(permission) AS permission FROM rightsprocesstypeuser ru 
		WHERE processtype = vprocesstype AND userid = vuserid 
		GROUP BY ru.userid
		UNION
		SELECT max(permission) AS permission FROM rightsprocesstypegroup rg 
		JOIN groupmemberships gm ON (rg.groupid = gm.groupid AND gm.userid = vuserid) 
		WHERE processtype = vprocesstype
		GROUP BY gm.userid
		UNION
		SELECT 'w' AS permission FROM groupmemberships WHERE groupid = 1 AND userid = vuserid
	) t GROUP BY TRUE;
END IF;
rights:=COALESCE(rights,'n');
END;
$$
LANGUAGE plpgsql;





-- Get the rights for one particular sample and user
CREATE OR REPLACE FUNCTION getProcessRights(vuserid INTEGER, vprocess INTEGER, OUT rights CHAR) AS
$$
BEGIN
	SELECT INTO rights getProcesstypeRights(
		vuserid := vuserid,
		vprocesstype := (SELECT processtypesid FROM processes WHERE id = vprocess)
	);
	rights:=COALESCE(rights,'n');
END;
$$
LANGUAGE plpgsql;





CREATE OR REPLACE FUNCTION compare(
	val JSONB, 
	datatype VARCHAR, 
	comparator INTEGER, 
	comval VARCHAR, 
	OUT result BOOLEAN) AS
$$
BEGIN
CASE datatype
	WHEN 'integer', 'float', 'measurement' THEN 
		IF    comparator = 0 THEN -- "<"
		  	SELECT INTO result (val->>'value')::NUMERIC <  (comval::NUMERIC);
		ELSIF comparator = 1 THEN -- "<="
		  	SELECT INTO result (val->>'value')::NUMERIC <= (comval::NUMERIC);
		ELSIF comparator = 2 THEN -- "="
			SELECT INTO result (val->>'value')::NUMERIC =  (comval::NUMERIC);
		ELSIF comparator = 3 THEN -- ">="
		  	SELECT INTO result (val->>'value')::NUMERIC >= (comval::NUMERIC);
		ELSIF comparator = 4 THEN -- ">"
		  	SELECT INTO result (val->>'value')::NUMERIC > (comval::NUMERIC);
		ELSIF comparator = 5 THEN -- "!="
		  	SELECT INTO result (val->>'value')::NUMERIC != (comval::NUMERIC);
		END IF;
	WHEN 'undefined','string','longstring','chooser','url','email' THEN 
		IF comparator = 2 THEN -- "="
	  		SELECT INTO result val->>'value' LIKE comval;
	  	ELSIF comparator = 5 THEN -- "not"
	  		SELECT INTO result  val->>'value' NOT LIKE comval;
		ELSIF comparator = 6 THEN -- "contains"
	  		SELECT INTO result val->>'value' LIKE '%'||comval||'%';
		END IF;
	WHEN 'date','timestamp' THEN		
		IF comparator = 2 THEN -- "="
	  		SELECT INTO result (val->'date')::TIMESTAMP = (comval::TIMESTAMP);
		END IF;
 	WHEN 'checkbox' THEN 
 		IF comparator = 3 THEN -- "="
		  	SELECT INTO result (val->>'value')::NUMERIC = comval;
		ELSIF comparator = 4 THEN -- "not"
		  	SELECT INTO result NOT ((val->>'value')::NUMERIC = comval);
		END IF;
END CASE;
RETURN;
END;
$$
LANGUAGE plpgsql;




REFRESH MATERIALIZED VIEW pnumbers;

