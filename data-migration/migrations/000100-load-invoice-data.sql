INSERT INTO gs.invoice (
	id,
	amount,
	status_id,
	is_archived,
	status_changed_at,
	customer_email,
	last_manual_check
)
SELECT
	"ID",
	"Amount",
	"Status",
	CASE WHEN "IsArchived" = 0 THEN false
	     WHEN "IsArchived" = 1 THEN true
	END,
	"StatusChangedAt",
	"CustomerEmail",
	"LastManualCheck"
FROM
  staging."Invoices";
