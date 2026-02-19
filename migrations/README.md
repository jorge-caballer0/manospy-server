Run migrations in production carefully. Example using psql:

```powershell
# Linux / WSL / macOS
psql "$DATABASE_URL" -f 20260219_add_chats_and_chatid.sql

# Windows (PowerShell)
$env:DATABASE_URL = "your_database_url"
psql $env:DATABASE_URL -f 20260219_add_chats_and_chatid.sql
```

Verify backups before applying. Adapt to your deployment workflow (Sequelize migrations, Flyway, etc.).
