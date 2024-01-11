const sqlite3 = require("sqlite3").verbose();
const db = new sqlite3.Database("log.db");

const setupDatabase = () => {
  // logs => id, timestamp, ip_address, error, prompt, response
  db.run(
    `CREATE TABLE IF NOT EXISTS logs (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
      ip_address TEXT,
      error TEXT NULL,
      prompt TEXT NULL,
      response TEXT NULL
    )`
  );
}

const success = (ip_address, prompt, response) => {
  db.run(
    `INSERT INTO logs (ip_address, prompt, response) VALUES (?, ?, ?)`,
    [ip_address, prompt, response],
    (err) => {
      if (err) {
        console.error(err);
      }
    }
  );
};

const error = (ip_address, prompt, error) => {
  db.run(
    `INSERT INTO logs (ip_address, prompt, error) VALUES (?, ?, ?)`,
    [ip_address, prompt, error],
    (err) => {
      if (err) {
        console.error(err);
      }
    }
  );
};

module.exports = {
  setupDatabase,
  success,
  error,
};