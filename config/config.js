require('dotenv').config();

module.exports = {
  development: {
    username: "postgres",
    password: "tu_password_local",
    database: "manospy_dev",
    host: "127.0.0.1",
    dialect: "postgres"
  },
  test: {
    username: "postgres",
    password: "tu_password_local",
    database: "manospy_test",
    host: "127.0.0.1",
    dialect: "postgres"
  },
  production: {
    url: process.env.DATABASE_URL,
    dialect: "postgres",
    dialectOptions: {
      ssl: {
        require: true,
        rejectUnauthorized: false
      }
    }
  }
};

