require('dotenv').config();

module.exports = {
  production: {
    use_env_variable: 'DATABASE_URL',
    dialect: 'postgres',
    dialectOptions: {
      ssl: {
        require: true,
        rejectUnauthorized: false
      }
    }
  },
  development: {
    username: "postgres",
    password: "postgres",
    database: "manospy_dev",
    host: "127.0.0.1",
    dialect: "postgres"
  },
  test: {
    username: "postgres",
    password: "postgres",
    database: "manospy_test",
    host: "127.0.0.1",
    dialect: "postgres"
  }
};
