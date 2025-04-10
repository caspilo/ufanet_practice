DROP TABLE tasks;
CREATE TABLE tasks (
    id BIGINT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    canonical_name VARCHAR(255) NOT NULL,
    params JSON NOT NULL,
    #status ENUM('PENDING','READY','PROCESSING','FAILED','COMPLETED','CANCELED','NONE') NOT NULL,
    status VARCHAR(15) NOT NULL,
    execution_time DATETIME NOT NULL,
    retry_count INT NOT NULL
);
