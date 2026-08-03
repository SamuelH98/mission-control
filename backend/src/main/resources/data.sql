INSERT OR IGNORE INTO managers (id, first_name, last_name, email) VALUES
    (1, 'Gene',   'Kranz',   'gene.kranz@nasa.gov'),
    (2, 'Margaret', 'Hamilton', 'margaret.hamilton@nasa.gov'),
    (3, 'Katherine', 'Johnson', 'katherine.johnson@nasa.gov');

INSERT OR IGNORE INTO projects (id, title, description, status, manager_id) VALUES
    (1, 'Artemis III Landing',    'Crewed return to the lunar south pole.', 'planned',   2),
    (2, 'Europa Clipper Cruise',  'Jupiter flyby science operations.',       'active',    3),
    (3, 'ISS Resupply - CRS-31',  'Cargo delivery to the station.',          'completed', 1);
