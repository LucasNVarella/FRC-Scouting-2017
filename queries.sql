SELECT rpt.teamnum, r.value as results FROM scouting.record r
join scouting.item i on i.ID = r.ITEM_ID and i.Name = 'rate shooting'
join scouting.report rpt on rpt.id = r.report_id and rpt.teamnum > 0
order by rpt.teamnum asc;

-----------

SELECT rpt.teamnum, i.Name, 2-avg(r.value) as rating FROM scouting.record r
join scouting.item i on i.ID = r.ITEM_ID and 
i.Name in(
'Attempt Cheval De Frise', 
'Attempt portcullis',
'Attempt Rock Wall',
'Attempt Sally Port',
'Attempt Low Bar',
'Attempt Rough Terrain',
'Attempt Ramparts',
'Attempt Drawbridge',
'Attempt Moat'
)
join scouting.report rpt on rpt.id = r.report_id and rpt.teamnum > 0 AND r.value < 3
group by rpt.teamnum, i.Name
order by rpt.teamnum asc, i.Name ASC;

------------	

SELECT rpt.teamnum, i.Name, 2-AVG(r.value) AS rating
FROM scouting.record r
JOIN scouting.item i ON i.ID = r.ITEM_ID AND
i.Name IN(
'Attempt Cheval De Frise',
'Attempt portcullis',
'Attempt Rock Wall',
'Attempt Sally Port',
'Attempt Low Bar',
'Attempt Rough Terrain',
'Attempt Ramparts',
'Attempt Drawbridge',
'Attempt Moat'
)
JOIN scouting.report rpt ON rpt.id = r.report_id AND rpt.teamnum > 0 AND r.value < 3
GROUP BY rpt.teamnum, i.Name

UNION

SELECT rpt.teamNum, i.name, AVG(r.Value) AS rating
FROM scouting.record r
JOIN scouting.item i ON i.ID = r.ITEM_ID AND
i.Name IN(
'Auto: reaches over walls?',
'Auto: crosses outer works?',
'Auto: shooting?',
'Can shoot?',
'Can climb?',
'Rate shooting',
'Rate driving',
'Shoots High?',
'Shoots Low?'
)
JOIN scouting.report rpt ON rpt.id = r.report_id
GROUP BY rpt.teamnum, i.Name

UNION

SELECT rpt.teamNum, i.name, AVG(r.Value)/10 AS rating
FROM scouting.record r
JOIN scouting.item i ON i.ID = r.ITEM_ID AND
i.Name IN(
'Times Crossed Cheval De Frise', 
'Times Crossed portcullis',
'Times Crossed Rock Wall',
'Times Crossed Sally Port',
'Times Crossed Low Bar',
'Times Crossed Rough Terrain',
'Times Crossed Ramparts',
'Times Crossed Drawbridge',
'Times Crossed Moat'
)
AND r.Value > 0
JOIN scouting.report rpt ON rpt.id = r.report_id
GROUP BY rpt.teamnum, i.Name

UNION

SELECT rpt.teamNum, i.name, -AVG(r.Value) AS rating
FROM scouting.record r
JOIN scouting.item i ON i.ID = r.ITEM_ID AND
i.Name IN(
'Got Stuck Cheval De Frise',
'Got Stuck portcullis',
'Got Stuck Rock Wall',
'Got Stuck Sally Port',
'Got Stuck Low Bar',
'Got Stuck Rough Terrain',
'Got Stuck Ramparts',
'Got Stuck Drawbridge',
'Got Stuck Moat'
)
AND r.Value > 0
JOIN scouting.report rpt ON rpt.id = r.report_id
GROUP BY rpt.teamnum, i.Name
ORDER BY teamnum ASC, Name ASC;

------------

DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `procGetAverage`(
	IN itemName VARCHAR(50)
)
BEGIN
SELECT rpt.teamnum, avg(r.value) as results FROM scouting.record r
join scouting.item i on i.ID = r.ITEM_ID and i.Name = itemName
join scouting.report rpt on rpt.id = r.report_id and rpt.teamnum > 0
group by rpt.teamnum
order by rpt.teamnum asc;
END$$
DELIMITER ;