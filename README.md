# Open Source Maintainer

A minimal JavaFX application for tracking open source project maintenance. Manage projects, issues, and contributors through a clean, distraction-free interface.

## Features

- User Authentication - Login with Admin or Maintainer roles
- Project Management - Track repositories, languages, and status
- Issue Tracking - Create and manage bugs, features, docs, and security issues
- Statistics Dashboard - View issue and project summaries
- Minimal Theme - Clean white or AMOLED black, no distractions
- Iosevka Font - Monospace typography throughout

## Tech Stack

- JavaFX - UI framework
- MySQL - Database
- JDBC - Database connection
- Iosevka Term - Font

## Database Setup

1. Start MySQL server
2. Run the SQL dump:

```bash
mysql -u root < db/maintainer.sql
```

## Default Credentials

| Username | Password | Role       |
|----------|----------|------------|
| admin    | admin    | Admin      |
| user     | user     | Maintainer |

## Project Structure

```
src/
  controllers/    - Business logic
  models/         - Data models (User, Project, Issue, Contributor)
  views/          - JavaFX scenes
  themes/         - CSS themes (light.css, dark.css)
  utils/          - Database helper, theme manager
db/
  maintainer.sql  - Database schema and sample data
```

## Building

Open in NetBeans and run, or use Ant:

```bash
ant -f . run
```

## Themes

Two themes available via the dashboard dropdown:
- Light - Clean white background
- Dark - AMOLED black for OLED displays

## License

MIT
