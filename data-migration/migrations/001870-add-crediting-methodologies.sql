--- Make period_id on project_crediting_period_id unique as a project can't have multiple of the same period id
ALTER TABLE gs.project_crediting_period
ADD CONSTRAINT unique_project_period UNIQUE (project_id, period_id);

--- Create Request Methodology Table
CREATE TABLE gs.request_methodology (
    request_id INT,                         
    crediting_period_id INT,                
    methodology_id INT,                     
    methodology_version NUMERIC(5, 2),  

    PRIMARY KEY (request_id, crediting_period_id, methodology_id),

    CONSTRAINT fk_request FOREIGN KEY (request_id) 
        REFERENCES gs.request(id) 
        ON DELETE CASCADE,

    CONSTRAINT fk_methodology FOREIGN KEY (methodology_id) 
        REFERENCES gs.methodology(id) 
        ON DELETE CASCADE
);

-- Permissions
GRANT SELECT, INSERT, UPDATE, DELETE ON gs.request_methodology TO gs_assurance_db_writer;

